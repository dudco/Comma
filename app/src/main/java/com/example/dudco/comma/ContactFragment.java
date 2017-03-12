package com.example.dudco.comma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.dudco.comma.databinding.ItemContactListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {
    private static List<ContactData> items = new ArrayList<>();

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(Context context) {

        ContactFragment fragment = new ContactFragment();

        SharedPreferences sharedPreferences = context.getSharedPreferences("hello world", Context.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("isFill", false)){

            String[] arrProjection = {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor clsCursor = context.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI,
                    arrProjection,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "= 1",
                    null, null
            );

            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

            while(clsCursor.moveToNext()){
                ContactData data = new ContactData();

                String name;
                String num = null;

                String contactID = clsCursor.getString(0);
                Log.d("dudco", "연락처 ID : " + clsCursor.getString(0));
                Log.d("dudco", "연락처 이름 : " + clsCursor.getString(1));

                Cursor nCursor = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID,
                        null, null
                );
                while(nCursor.moveToNext()) {
                    Log.d("dudco", "연락처 번호 : " + nCursor.getString(0));
                    num = nCursor.getString(0);
                }

                name = clsCursor.getString(1);

                String nnum = num.replace("-", "");

                sharedPreferencesEditor.putString(num, name);
                sharedPreferencesEditor.putString(nnum, name);
                sharedPreferencesEditor.apply();

                data.setDisplayName(name);
                data.setPhoneNum(num);

                items.add(data);

                nCursor.close();
            }
            clsCursor.close();

            Gson gson = new Gson();
            String json = gson.toJson(items);
            Log.d("dudco", json);
            sharedPreferencesEditor.putString("contacts", json);
            sharedPreferencesEditor.apply();

            sharedPreferencesEditor.putBoolean("isFill", true);
            sharedPreferencesEditor.apply();
        }else{
            String json = sharedPreferences.getString("contacts", null);
//            Log.d("dudco", json);
            if(json != null){
                Gson gson = new Gson();
                items = gson.fromJson(json, new TypeToken<List<ContactData>>(){}.getType());
                Log.d("dudco", items.get(0).toString());
                items.addAll(items);
            }
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ListView list = (ListView) view.findViewById(R.id.conatct_list);
        list.setAdapter(new MyListAdapter(items));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                items.get(position).getPhoneNum();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+items.get(position).getPhoneNum()));
                startActivity(intent);
            }
        });
        return view;
    }

    private class MyListAdapter extends BaseAdapter{

        List<ContactData> items;

        public MyListAdapter(List<ContactData> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact_list, null);
            ItemContactListBinding binding = DataBindingUtil.bind(view);
            binding.setData(items.get(position));
            return view;
        }
    }
}

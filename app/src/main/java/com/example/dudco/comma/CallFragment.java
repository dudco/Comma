package com.example.dudco.comma;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.dudco.comma.databinding.FragmentCallBinding;

import java.util.ArrayList;
import java.util.List;

public class CallFragment extends Fragment {
    FragmentCallBinding binding;
    public CallFragment() {
        // Required empty public constructor
    }

    public static CallFragment newInstance(String param1, String param2) {
        CallFragment fragment = new CallFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
        binding = DataBindingUtil.bind(view);

        binding.grid.setAdapter(new GridAdatper());
        binding.grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String texts = binding.text.getText().toString();
                if(texts.length() == 3 || texts.length() == 8){
                    texts += "-";
                }
                texts += String.valueOf(binding.grid.getItemAtPosition(position));
                binding.text.setText(texts);
            }
        });
        binding.removeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texts = binding.text.getText().toString();
                if(texts.length() > 0) {
                    String result = texts.substring(0, texts.length() - 1);
                    if(result.length() > 0 && result.charAt(result.length() - 1) == '-'){
                        result = result.substring(0, result.length() - 1);
                    }
                    binding.text.setText(result);
                }
            }
        });

        binding.removeBack.setLongClickable(true);
        binding.removeBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                binding.text.setText("");
                return true;
            }
        });

        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+binding.text.getText().toString()));
                startActivity(intent);
//                startActivity(new Intent(getContext(), CallingActivity.class));
            }
        });

        return view;
    }

    public class GridAdatper extends BaseAdapter{
        List<String> items = new ArrayList<>();

        public GridAdatper() {
            items.add(String.valueOf(1));
            items.add(String.valueOf(2));
            items.add(String.valueOf(3));
            items.add(String.valueOf(4));
            items.add(String.valueOf(5));
            items.add(String.valueOf(6));
            items.add(String.valueOf(7));
            items.add(String.valueOf(8));
            items.add(String.valueOf(9));
            items.add("*");
            items.add(String.valueOf(0));
            items.add("#");
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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text = new TextView(getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            text.setLayoutParams(params);
            text.setGravity(Gravity.CENTER);
            text.setText(items.get(position));
            Resources resources = getContext().getResources();
            text.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17, resources.getDisplayMetrics()));
            text.setTextColor(Color.BLACK);
            return text;
        }
    }
}

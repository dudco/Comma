package com.example.dudco.comma;

import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallingActivity extends AppCompatActivity {
    SpeechRecognizer mRecognizer;

    List<JSONObject> items = new ArrayList<>();

    MyAdpater adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        final Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        String name = getIntent().getStringExtra("name");
        String number = getIntent().getStringExtra("num");

        TextView textName = (TextView) findViewById(R.id.text_title);
        TextView textNum = (TextView) findViewById(R.id.text_number);

        textName.setText(name);
        textNum.setText(number);

        ListView list = (ListView) findViewById(R.id.call_list);
        adapter = new MyAdpater(items);
        list.setAdapter(adapter);
        list.setFooterDividersEnabled(false);

        ImageView end = (ImageView) findViewById(R.id.calling_end);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectCall();
                finish();
            }
        });
        end.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startListening();
                return true;
            }
        });
        end.setLongClickable(true);

        mSocket.connect();

        mSocket.on("text", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                addChat("other", args[0].toString());
            }
        });

        mSocket.on("mytext", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                addChat("me", args[0].toString());
            }
        });

        startListening();
    }

    public void startListening(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        Log.d("dudco", "start listening");
        mRecognizer.startListening(i);
    }

    public void disconnectCall(){
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("dudco", "FATAL ERROR: could not connect to telephony subsystem");
            Log.e("dudco", "Exception object: " + e);
        }
    }

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(CallingActivity.this, "준비 끝", Toast.LENGTH_SHORT).show();
            Log.d("dudco", "okay");
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> strings = (ArrayList<String>) results.get(SpeechRecognizer.RESULTS_RECOGNITION);

            Toast.makeText(CallingActivity.this, strings.toString(), Toast.LENGTH_SHORT).show();
            Log.d("dudco", strings.get(0));
            mSocket.emit("text", strings.get(0));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
            startListening();
        }

        @Override
        public void onEndOfSpeech() {
            Toast.makeText(CallingActivity.this, "End", Toast.LENGTH_SHORT).show();
            Log.d("dudco", "end");
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onError(int error) {
//            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
//                if(isRunning)
//                    startListening();
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://iwin247.kr:7474");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecognizer.cancel();
        mRecognizer.destroy();
//        unbindService(mRecognizer.destroy());
    }

    private void addChat(String who, String text){
        Log.d("dudco", "who : " + who + " text : " + text);
        JSONObject json = new JSONObject();
        try {
            json.put("who", who);
            json.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        items.add(json);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
    private class MyAdpater extends BaseAdapter{

        List<JSONObject> items = new ArrayList<>();

        public MyAdpater(List<JSONObject> items) {
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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject json = items.get(position);
            String text, who;
            View view;
            TextView msg;
            try {
                text = json.get("text").toString();
                who = json.get("who").toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            if(who.equals("me")){
                view = LayoutInflater.from(CallingActivity.this).inflate(R.layout.item_chat, null);
                msg = (TextView) view.findViewById(R.id.me_text);
            }else{
                view = LayoutInflater.from(CallingActivity.this).inflate(R.layout.item_chat_other, null);
                msg = (TextView) view.findViewById(R.id.other_text);
            }
            msg.setText(text);
            return view;
        }
    }
}

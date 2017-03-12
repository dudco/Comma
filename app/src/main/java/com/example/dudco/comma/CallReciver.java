package com.example.dudco.comma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallReciver extends BroadcastReceiver {
    public static final String TAG = "dudco";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive()");

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        Log.d(TAG, state);
        if(TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)){
            Toast.makeText(context, "calling!!", Toast.LENGTH_LONG).show();
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            SharedPreferences pref = context.getSharedPreferences("hello world", Context.MODE_PRIVATE);

            String name = pref.getString(incomingNumber, "null");
            Log.d("dudco", incomingNumber + " " + name);
            Intent _intent = new Intent(context, CallingActivity.class);
            _intent.putExtra("name", name);
            _intent.putExtra("num", incomingNumber);
            context.startActivity(_intent);
        }

    }
}

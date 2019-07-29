package com.multiplies.multiring.phoneState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.multiplies.multiring.activity.RingtonesActivity;
import com.multiplies.multiring.R;

public class PhoneStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){

            SharedPreferences preferences = context.getSharedPreferences(RingtonesActivity.MY_PREF, Context.MODE_PRIVATE);
            boolean ringtones_state = preferences.getBoolean(RingtonesActivity.RINGTONES_STATE_KEY, true);
            if(ringtones_state) {

                SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
                boolean silentMode = defaultPref.getBoolean(context.getResources().getString(R.string.silent_checkbox_key), false);
                if(silentMode) {
                    new PhoneStateHandler().onIncomingCallStarted(context);
                }else {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
                        new PhoneStateHandler().onIncomingCallStarted(context);
                    }
                }
            }
        }
    }
}

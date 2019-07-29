package com.multiplies.multiring.phoneState;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;

import com.multiplies.multiring.activity.RingtonesActivity;
import com.multiplies.multiring.ringtoneItem.RingtoneItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PhoneStateHandler {

    private Context context;
    private List<RingtoneItem> list;
    private File file;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public void onIncomingCallStarted(Context context) {
        this.context = context;
        list = new ArrayList<>();
        file = new File(context.getFilesDir(), RingtonesActivity.FILE_NAME);
        preferences = context.getSharedPreferences(RingtonesActivity.MY_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();

        String content = RingtonesActivity.readFromFile(context, file);
        if (!content.equals("empty")) {
            for (String string : Arrays.asList(content.split("!@!"))) {
                RingtoneItem item = RingtoneItem.parse(context, string);
                list.add(item);
            }
        }

        if (list.size() >= 1) {
                next();
        }
    }

    private void next() {

        int index = preferences.getInt(RingtonesActivity.CURRENT_INDEX_KEY, 0);

        if (list.size() - 1 <= index) {
            index = 0;
        } else {
            index++;
        }


        editor.putInt(RingtonesActivity.CURRENT_INDEX_KEY, index);
        editor.commit();

        if (list.get(index).isActive()) {

            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, list.get(index).getUri());

        } else {

            next();
        }
    }

}

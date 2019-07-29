package com.multiplies.multiring.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Switch;

import com.multiplies.multiring.dialog.CantAskPermissionDialog;
import com.multiplies.multiring.dialog.DeniedPhoneDialog;
import com.multiplies.multiring.dialog.RequestWriteSettingsPermissionDialog;
import com.multiplies.multiring.R;
import com.multiplies.multiring.ringtoneItem.RingtoneItem;
import com.multiplies.multiring.ringtoneItem.RingtoneItemAdabter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RingtonesActivity extends AppCompatActivity implements OnClickListener{

    private FloatingActionButton fab;
    public static File file;
    public static List<RingtoneItem> list;
    public static RingtoneItemAdabter adabter;
    public static MenuItem switchItem;
    public static final int RINGTONE_PICKER_REQUEST_CODE = 1;
    public static final int PERMISSION_REQUEST_CODE = 2;
    public static final String MY_PREF = "my_preferences";
    public static final String CURRENT_INDEX_KEY = "current_index_key";
    public static final String RINGTONES_STATE_KEY = "ringtones_state_key";
    public static final String FILE_NAME = "ringtone_data.txt";
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtones);

        preferences = getApplicationContext().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(Build.VERSION.SDK_INT >= 21){ toolbar.setElevation(4); }
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.ringtones_activity_label);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        file = new File(getFilesDir(), FILE_NAME);

        list = new ArrayList<>();

        adabter = new RingtoneItemAdabter(this, this, list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adabter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_menu, menu);
        switchItem = menu.findItem(R.id.action_switch);
        Switch aSwitch = (Switch) switchItem.getActionView();
        aSwitch.setChecked(preferences.getB
                oolean(RINGTONES_STATE_KEY, true));
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean(RINGTONES_STATE_KEY, isChecked);
            editor.commit();
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_clear:
                int length = list.size();
                list.clear();
                adabter.notifyItemRangeRemoved(0, length);
                writeToFile(this);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PermissionChecker.PERMISSION_DENIED && Build.VERSION.SDK_INT >= 23) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                DeniedPhoneDialog dialog = new DeniedPhoneDialog();
                dialog.show(getFragmentManager(), "DeniedPhoneDialog from RingtonesActivity class");
            } else {
                CantAskPermissionDialog dialog = new CantAskPermissionDialog();
                dialog.show(getFragmentManager(), "CantAskPermissionDialog from RingtonesActivity class");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(fab)){
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            startActivityForResult(intent, RINGTONE_PICKER_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RINGTONE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                RingtoneItem ringtoneItem = new RingtoneItem(this, uri, true);
                list.add(ringtoneItem);
                adabter.notifyItemInserted(list.size() - 1);
                writeToFile(this);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= 23) {
            if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
            }

            if (!Settings.System.canWrite(getApplicationContext())) {
                RequestWriteSettingsPermissionDialog dialog = new RequestWriteSettingsPermissionDialog();
                dialog.show(getFragmentManager(), "RequestWriteSettingsPermissionDialog from RingtonesActivity class");
            }
        }
        String content = readFromFile(this, file);
        if(!content.equals("empty")){
            list.clear();
            for(String string: Arrays.asList(content.split("!@!"))){
                list.add(RingtoneItem.parse(this, string));
            }
        }


        boolean contains = false;
        if (list.isEmpty()) {
            RingtoneItem ringtoneItem = new RingtoneItem(this, RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE), true);
            list.add(ringtoneItem);
            editor.putInt(CURRENT_INDEX_KEY, 0);
            editor.apply();
        } else {
            for (RingtoneItem ringtoneItem : list) {
                if (ringtoneItem.getUri().equals(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE))) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                RingtoneItem ringtoneItem = new RingtoneItem(this, RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE), true);
                list.add(preferences.getInt(CURRENT_INDEX_KEY, 0), ringtoneItem);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        writeToFile(this);
    }

    public static void writeToFile(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        for (RingtoneItem ringtoneItem : list) {
            stringBuilder.append(ringtoneItem.toString()).append("!@!");
        }

        String string = stringBuilder.toString();

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, MODE_PRIVATE);
            fileOutputStream.write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFromFile(Context context, File file){
        FileInputStream fileInputStream = null;
        String result = "empty";
        if(file.exists()){
            try {
                fileInputStream = context.openFileInput(FILE_NAME);
                StringBuilder stringBuilder = new StringBuilder();
                int b;
                while ((b = fileInputStream.read()) != -1){
                    stringBuilder.append((char) b);
                }
                result = stringBuilder.toString().length() > 70 ? stringBuilder.toString() : "empty";
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}

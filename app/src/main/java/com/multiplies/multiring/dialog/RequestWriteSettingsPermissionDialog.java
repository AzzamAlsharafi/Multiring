package com.multiplies.multiring.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import com.multiplies.multiring.R;

public class RequestWriteSettingsPermissionDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.settings_dialog_message);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.settings_permission_layout, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.settings_positive_text, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        });

        builder.setNegativeButton(R.string.settings_negative_text, (dialog, which) -> dialog.cancel());

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(Build.VERSION.SDK_INT >= 23 && !Settings.System.canWrite(getActivity().getApplicationContext())){
            DeniedSettingsDialog deniedSettingsDialog = new DeniedSettingsDialog();
            deniedSettingsDialog.show(getFragmentManager(), "DeniedSettingsDialog from RequestWriteSettingsPermissionDialog class");
        }
    }
}

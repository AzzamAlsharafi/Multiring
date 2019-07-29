package com.multiplies.multiring.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.multiplies.multiring.R;

public class DeniedSettingsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        builder.setTitle(R.string.denied_title);
        builder.setMessage(R.string.denied_settings_message);
        builder.setPositiveButton(R.string.denied_positive_text, (dialog, which) -> {
            RequestWriteSettingsPermissionDialog requestWriteSettingsPermissionDialog = new RequestWriteSettingsPermissionDialog();
            requestWriteSettingsPermissionDialog.show(getFragmentManager(), "RequestWriteSettingsPermissionDialog from DeniedSettingsDialog class");
        });
        builder.setNegativeButton(R.string.denied_negative_text, (dialog, which) -> System.exit(0));

        return builder.create();
    }
}

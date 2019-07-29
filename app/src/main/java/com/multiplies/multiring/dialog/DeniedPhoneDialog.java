package com.multiplies.multiring.dialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.multiplies.multiring.activity.RingtonesActivity;
import com.multiplies.multiring.R;

public class DeniedPhoneDialog extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        builder.setTitle(R.string.denied_title);
        builder.setMessage(R.string.denied_phone_message);
        builder.setPositiveButton(R.string.denied_positive_text, (dialog, which) -> ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, RingtonesActivity.PERMISSION_REQUEST_CODE));
        builder.setNegativeButton(R.string.denied_negative_text, (dialog, which) -> System.exit(0));

        return builder.create();
    }
}

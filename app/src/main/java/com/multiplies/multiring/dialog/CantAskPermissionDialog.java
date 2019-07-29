package com.multiplies.multiring.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.multiplies.multiring.R;

public class CantAskPermissionDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        builder.setTitle(R.string.denied_title);
        builder.setMessage(R.string.dont_ask_phone_message);
        builder.setPositiveButton(R.string.denied_negative_text, (dialog, which) -> System.exit(0));
        return builder.create();
    }
}

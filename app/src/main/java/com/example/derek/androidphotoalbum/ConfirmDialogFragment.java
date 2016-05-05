package com.example.derek.androidphotoalbum;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Derek on 5/4/16.
 */
public class ConfirmDialogFragment extends DialogFragment {

    public interface ConfirmDialogListener {
        public void onDialogPositiveClick(ConfirmDialogFragment dialog);
        public void onDialogNegativeClick(ConfirmDialogFragment dialog);
    }

    private ConfirmDialogListener listener;

    static ConfirmDialogFragment newInstance(String message) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Keys.DIALOG_MESSAGE_KEY, message);
        confirmDialogFragment.setArguments(bundle);

        return confirmDialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ConfirmDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ConfirmDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = getArguments().getString(Keys.DIALOG_MESSAGE_KEY);
        builder.setMessage(message)
                .setPositiveButton(android.R.string.yes, (dialog, which) ->
                        listener.onDialogPositiveClick(ConfirmDialogFragment.this))
                .setNegativeButton(android.R.string.cancel, (dialog1, which1) ->
                        listener.onDialogNegativeClick(ConfirmDialogFragment.this));
        return builder.create();
    }
}

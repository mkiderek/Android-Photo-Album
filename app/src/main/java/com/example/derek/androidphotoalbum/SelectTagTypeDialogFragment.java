package com.example.derek.androidphotoalbum;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import java.util.List;

/**
 * Created by Derek on 5/4/16.
 */
public class SelectTagTypeDialogFragment extends DialogFragment {

    public interface SelectTagTypeDialogListener {
        public void onDialogPositiveClick(SelectTagTypeDialogFragment dialog);
        public void onDialogNegativeClick(SelectTagTypeDialogFragment dialog);
    }

    private SelectTagTypeDialogListener listener;

    public int index;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (SelectTagTypeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SelectTagTypeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] types = new String[2];
        types[0] = "PERSON";
        types[1] = "LOCATION";
        builder.setTitle("Choose target album")
                .setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                        listener.onDialogPositiveClick(SelectTagTypeDialogFragment.this);
                    }
                });

        return builder.create();
    }
}

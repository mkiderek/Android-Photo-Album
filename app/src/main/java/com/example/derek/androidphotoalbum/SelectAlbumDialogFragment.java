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
public class SelectAlbumDialogFragment extends DialogFragment {

    public interface SelectAlbumDialogListener {
        public void onDialogPositiveClick(SelectAlbumDialogFragment dialog);
        public void onDialogNegativeClick(SelectAlbumDialogFragment dialog);
    }

    private SelectAlbumDialogListener listener;

    public int index;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (SelectAlbumDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SelectAlbumDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        List<Album> albumList = AlbumList.getInstance().getAlbums(null);
        String[] names = new String[albumList.size()];
        for (int i = 0; i < albumList.size(); i++) {
            names[i] = albumList.get(i).toString();
        }
        builder.setTitle("Choose target album")
                .setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                        listener.onDialogPositiveClick(SelectAlbumDialogFragment.this);
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog1, which1) ->
                        listener.onDialogNegativeClick(SelectAlbumDialogFragment.this));

        return builder.create();
    }
}

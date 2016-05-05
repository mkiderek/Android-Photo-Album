package com.example.derek.androidphotoalbum;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddTag extends AppCompatActivity
        implements SelectTagTypeDialogFragment.SelectTagTypeDialogListener{

    private TextView tagType;
    private EditText tagValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        tagType = (TextView) findViewById(R.id.tagType);
        tagValue = (EditText) findViewById(R.id.tagValue);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(SelectTagTypeDialogFragment dialog) {
        if (dialog.index == 0) {
            tagType.setText(R.string.tag_type_person);
        } else if (dialog.index == 1) {
            tagType.setText(R.string.tag_type_location);
        } else {
            Toast.makeText(this, "UNKNOWN INDEX " + dialog.index, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(SelectTagTypeDialogFragment dialog) {
        // should not happen
    }

    public void chooseTagType(View view) {
        DialogFragment dialogFragment = new SelectTagTypeDialogFragment();
        dialogFragment.show(getFragmentManager(), "select_album");
    }

    public void save(View view) {

        String type = tagType.getText().toString().trim();
        String value = tagValue.getText().toString().trim();
        if (!type.equalsIgnoreCase("PERSON")
                && !type.equalsIgnoreCase("LOCATION")) {
            Toast.makeText(this, "Illegal Tag Type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (value.isEmpty()) {
            Toast.makeText(this, "Tag Value Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(Keys.TAG_TYPE_KEY, type);
        bundle.putString(Keys.TAG_VALUE_KEY,value);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);

//        System.out.println("tag: " + bundle.toString());

        finish();
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}

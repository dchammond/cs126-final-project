package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dillon on 12/7/17.
 */

public class EditableItem extends AppCompatActivity {
    private static String EDITABLE_ITEM_KEY = "editable_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_item_with_image);
    }

    public static String getEditableItemKey() {
        return EDITABLE_ITEM_KEY;
    }
}

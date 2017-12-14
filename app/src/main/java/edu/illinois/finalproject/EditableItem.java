package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;

/**
 * Created by dillon on 12/7/17.
 */

public class EditableItem extends AppCompatActivity {
    private static String EDITABLE_ITEM_KEY = "editable_item";

    private Item currentItem;

    private ImageButton editImageButton;

    private EditText editItemName;
    private EditText editItemDescription;
    private EditText editItemPrice;
    private EditText editItemContactInfo;

    private Button deleteItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_item_with_image);

        this.currentItem = extractItem();

        this.editImageButton = findViewById(R.id.editImageButton);
        Picasso.with(this).load(currentItem.getImageUri()).into(this.editImageButton);
        this.deleteItemButton = findViewById(R.id.deleteItemButton);
        setUpButtons();

        this.editItemName = findViewById(R.id.editItemName);
        this.editItemDescription = findViewById(R.id.editItemDescription);
        this.editItemPrice = findViewById(R.id.editItemPrice);
        this.editItemContactInfo = findViewById(R.id.editItemContactInfo);
        setUpElements();
    }

    private void setUpButtons() {
        this.editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                Intent detailedIntent = new Intent(context, EditPicture.class);

                detailedIntent.putExtra(EditPicture.getEditableImageKey(), EditableItem.this.currentItem);

                context.startActivity(detailedIntent);
            }
        });
        this.deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserPointer userPointer = EditableItem.this.currentItem.getSellerPointer();
                String itemId = EditableItem.this.currentItem.getItemId();
                Item.removeItem(itemId, userPointer, new deleteItem());
            }
        });
    }

    private static class deleteItem extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... booleans) {
            boolean deleted = booleans[0];
            if (!deleted) {
                Log.e(MainActivity.TAG, "Failed to delete Item");
            }

            return null;
        }
    }

    private void setUpElements() {
        this.editItemName.setText(this.currentItem.getItemName());
        this.editItemDescription.setText(this.currentItem.getItemDescription());
        this.editItemPrice.setText("$" + this.currentItem.getItemPrice().toString());
        this.editItemContactInfo.setText(this.currentItem.getContactInfo().getFormattedContactInfo());
    }

    public static String getEditableItemKey() {
        return EDITABLE_ITEM_KEY;
    }

    private Item extractItem() {
        final Intent intent = getIntent();
        return intent.getParcelableExtra(EDITABLE_ITEM_KEY);
    }
}

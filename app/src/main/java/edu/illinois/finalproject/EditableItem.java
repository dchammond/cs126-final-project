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

import java.util.Date;

/**
 * Created by dillon on 12/7/17.
 */

public class EditableItem extends AppCompatActivity {
    public static String EDITABLE_ITEM_KEY = "editable_item";
    public static String USER_KEY = "user_key";
    public static String IMAGE_URI = "image_uri";

    private static final int RESULT_URI = 1;

    private Item currentItem;
    private User currentUser;

    private String imageUri;

    private ImageButton editImageButton;

    private EditText editItemName;
    private EditText editItemDescription;
    private EditText editItemPrice;
    private EditText editItemContactInfo;

    private Button submitItemButton;
    private Button deleteItemButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case RESULT_URI:
                    if (data.getData() != null) {
                        this.imageUri = data.getData().toString();
                    }
                    break;
                default:
                    Log.e(MainActivity.TAG,"Got requestCode:" + requestCode);
                    break;
            }
        } else {
            Log.e(MainActivity.TAG, "resultCode was NOT RESULT_OKAY, it was: " + resultCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.currentUser = extractUser();
        this.currentItem = extractItem();

        if (this.currentItem == null) { // Hack to reuse this code for editing and creating
            setContentView(R.layout.create_item);
            this.deleteItemButton = null;
        } else {
            setContentView(R.layout.editable_item_with_image);
            Picasso.with(this).load(currentItem.getImageUri()).into(this.editImageButton);
            this.deleteItemButton = findViewById(R.id.deleteItemButton);
        }

        this.submitItemButton = findViewById(R.id.submitItem);
        this.editImageButton = findViewById(R.id.editImageButton);
        setUpButtons();

        this.editItemName = findViewById(R.id.editItemName);
        this.editItemDescription = findViewById(R.id.editItemDescription);
        this.editItemPrice = findViewById(R.id.editItemPrice);
        this.editItemContactInfo = findViewById(R.id.editItemContactInfo);

        if (this.currentItem != null) {
            setUpElements();
        }
    }

    private void setUpButtons() {
        this.submitItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item newItem = new Item(
                        EditableItem.this.editItemName.getText().toString(),
                        EditableItem.this.editItemDescription.getText().toString(),
                        Double.valueOf(
                                EditableItem.this.editItemPrice.getText()
                                        .toString().replaceAll("$", "")),
                        new UserPointer(EditableItem.this.currentUser.getUserId()),
                        new Date().toString(),
                        new ContactInfo(EditableItem.this.editItemContactInfo.getText().toString()),
                        EditableItem.this.imageUri
                );
                if (EditableItem.this.currentItem == null) {
                    Item.createItem(newItem, EditableItem.this.currentUser, new writeItem());
                } else {
                    Item.updateItem(newItem, new writeItem());
                }
            }
        });
        this.editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                Intent detailedIntent = new Intent(context, EditPicture.class);

                detailedIntent.putExtra(EditPicture.getEditableImageKey(), EditableItem.this.currentItem);

                context.startActivity(detailedIntent);
            }
        });
        if (this.deleteItemButton != null) {
            this.deleteItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserPointer userPointer = EditableItem.this.currentItem.getSellerPointer();
                    String itemId = EditableItem.this.currentItem.getItemId();
                    Item.removeItem(itemId, userPointer, new deleteItem());
                    startActivity(new Intent(EditableItem.this, MainActivity.class));
                }
            });
        }
    }

    private static class writeItem extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... booleans) {
            boolean deleted = booleans[0];
            if (!deleted) {
                Log.e(MainActivity.TAG, "Failed to write Item");
            }
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            Log.e(MainActivity.TAG, "Write Item was Cancelled");
        }
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

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            Log.e(MainActivity.TAG, "Delete Item was Cancelled");
        }
    }

    private void setUpElements() {
        this.editItemName.setText(this.currentItem.getItemName());
        this.editItemDescription.setText(this.currentItem.getItemDescription());
        this.editItemPrice.setText("$" + this.currentItem.getItemPrice().toString());
        this.editItemContactInfo.setText(this.currentItem.getContactInfo().getContactInfo());
    }

    private Item extractItem() {
        final Intent intent = getIntent();
        return intent.getParcelableExtra(EDITABLE_ITEM_KEY);
    }

    private User extractUser() {
        final Intent intent = getIntent();
        return intent.getParcelableExtra(USER_KEY);
    }
}

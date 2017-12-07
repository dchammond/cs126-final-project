package edu.illinois.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_item_with_image);

        this.currentItem = extractItem();

        this.editImageButton = findViewById(R.id.editImageButton);
        Picasso.with(this).load(currentItem.getImageUrl()).into(this.editImageButton);
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
                // Allow changing image
            }
        });
    }

    private void setUpElements() {
        this.editItemName.setText(this.currentItem.getName());
        this.editItemDescription.setText(this.currentItem.getDescription());
        this.editItemPrice.setText("$" + this.currentItem.getPrice().toString());
        this.editItemContactInfo.setText(this.currentItem.getContactInfo());
    }

    public static String getEditableItemKey() {
        return EDITABLE_ITEM_KEY;
    }

    private Item extractItem() {
        final Intent intent = getIntent();
        return intent.getParcelableExtra(EDITABLE_ITEM_KEY);
    }
}

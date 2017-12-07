package edu.illinois.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by dillon on 12/6/17.
 */

public class DetailedItem extends AppCompatActivity {
    private static String ITEM_KEY = "item";

    private ImageView itemImage;

    private TextView itemDescription;
    private TextView itemPrice;
    private TextView itemSeller;
    private TextView itemContactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_item_with_image);

        setUpElements();

        final Item currentItem = extractItem();

        Picasso.with(this).load(currentItem.getImageUrl()).into(this.itemImage);

        this.itemDescription.setText(currentItem.getDescription());
        this.itemPrice.setText("$" + currentItem.getPrice().toString());
        this.itemSeller.setText(currentItem.getSeller());
        this.itemContactInfo.setText(currentItem.getContactInfo());
    }

    private void setUpElements() {
        this.itemImage = findViewById(R.id.itemImage);
        this.itemDescription = findViewById(R.id.itemDescription);
        this.itemPrice = findViewById(R.id.itemPrice);
        this.itemSeller = findViewById(R.id.itemSeller);
        this.itemContactInfo = findViewById(R.id.itemContactInfo);
    }

    public static String getItemKey() {
        return ITEM_KEY;
    }

    private Item extractItem() {
        final Intent intent = getIntent();
        return intent.getParcelableExtra(ITEM_KEY);
    }
}
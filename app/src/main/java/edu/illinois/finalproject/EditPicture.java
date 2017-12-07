package edu.illinois.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by dillon on 12/7/17.
 */

public class EditPicture extends AppCompatActivity {
    private static String EDITABLE_IMAGE_KEY = "editable_image";

    private Item currentItem;

    private ImageView itemImage;

    private Button launchGalleryButton;
    private Button launchCameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_image);

        this.currentItem = extractItem();

        this.itemImage = findViewById(R.id.itemImage);
        Picasso.with(this).load(this.currentItem.getImageUrl()).into(this.itemImage);

        this.launchGalleryButton = findViewById(R.id.launchGalleryButton);
        this.launchCameraButton = findViewById(R.id.launchCameraButton);
        setUpButtons();
    }

    private void setUpButtons() {
        this.launchGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This will launch the Gallery Intent and replace the image
            }
        });
        this.launchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This will launch the Camera Intent and replace the image
            }
        });
    }

    public static String getEditableImageKey() {
        return EDITABLE_IMAGE_KEY;
    }

    private Item extractItem() {
        final Intent intent = getIntent();
        return intent.getParcelableExtra(EDITABLE_IMAGE_KEY);
    }
}

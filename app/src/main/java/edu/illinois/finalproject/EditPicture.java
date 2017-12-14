package edu.illinois.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dillon on 12/7/17.
 */

public class EditPicture extends AppCompatActivity {
    private static String EDITABLE_IMAGE_KEY = "editable_image";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMG = 2;

    private Item currentItem;

    private ImageView itemImage;

    private Uri currentImageUri;
    private String lastUploadedFile;

    private StorageReference imageStorageRef;

    private Button launchGalleryButton;
    private Button launchCameraButton;
    private Button uploadImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_image);

        this.currentItem = extractItem();

        this.itemImage = findViewById(R.id.itemImage);

        imageStorageRef = FirebaseStorage.getInstance().getReference();

        if (this.currentItem != null && this.currentItem.getImageUri() != null) {
            this.currentImageUri = Uri.parse(this.currentItem.getImageUri());
            Picasso.with(this).load(this.currentItem.getImageUri()).into(this.itemImage);
        }

        this.launchGalleryButton = findViewById(R.id.launchGalleryButton);
        this.launchCameraButton = findViewById(R.id.launchCameraButton);
        this.uploadImageButton = findViewById(R.id.uploadImageButton);
        setUpButtons();
    }

    private void setUpButtons() {
        this.launchGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, EditPicture.RESULT_LOAD_IMG);
            }
        });
        this.launchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        Log.e(MainActivity.TAG, "Failed to get file path", e);
                    }
                    if (photoFile != null) {
                        EditPicture.this.currentImageUri =
                                FileProvider.getUriForFile(EditPicture.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                EditPicture.this.currentImageUri);
                        startActivityForResult(takePictureIntent, EditPicture.REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
        this.uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EditPicture.this.currentImageUri == null) {
                    return;
                }
                String fileName = getFileNameFromUri(EditPicture.this.currentImageUri);
                EditPicture.this.lastUploadedFile = fileName;
                UploadTask uploadTask = imageStorageRef.child("images/" + fileName)
                        .putFile(EditPicture.this.currentImageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(MainActivity.TAG, "Failed to upload picture to firebase", e);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(MainActivity.TAG, "Uploaded to firebase!");
                        EditPicture.this.currentImageUri = taskSnapshot.getDownloadUrl();
                        EditPicture.this.done();
                    }
                });
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private String getFileNameFromUri(Uri uri) {
        // Adapted from https://stackoverflow.com/a/38304115
        Cursor cursor =
                this.getContentResolver()
                        .query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            String fileName = cursor.getString(nameIndex);
            cursor.close();
            return fileName;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public static String getEditableImageKey() {
        return EDITABLE_IMAGE_KEY;
    }

    private void done() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EditableItem.IMAGE_URI, this.currentImageUri.toString());
        setResult(EditPicture.RESULT_OK, resultIntent);
        finish();
    }

    private Item extractItem() {
        final Intent intent = getIntent();
        return intent.getParcelableExtra(EDITABLE_IMAGE_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    this.itemImage.setImageURI(this.currentImageUri);
                    break;
                case RESULT_LOAD_IMG:
                    this.currentImageUri = data.getData();
                    if (this.currentImageUri == null) {
                        Log.e(MainActivity.TAG, "Didn't find image from gallery");
                    }
                    this.itemImage.setImageURI(this.currentImageUri);
                    break;
                default:
                    Log.e(MainActivity.TAG,"Got requestCode:" + requestCode);
                    break;
            }
        } else {
            Log.e(MainActivity.TAG, "resultCode was NOT RESULT_OKAY, it was: " + resultCode);
        }
    }
}

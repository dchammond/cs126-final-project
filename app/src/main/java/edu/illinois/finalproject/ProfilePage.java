package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dillon on 12/7/17.
 */

public class ProfilePage extends AppCompatActivity {

    private Button signoutButton;

    private EditText editMyName;
    private TextView myEmail;
    private EditText editMyDefaultContactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        this.signoutButton = findViewById(R.id.signoutButton);
        setUpButtons();

        this.editMyName = findViewById(R.id.editMyName);
        this.myEmail = findViewById(R.id.myEmail);
        this.editMyDefaultContactInfo = findViewById(R.id.editMyDefaultContactInfo);
        setUpElements();

        displayItems(getMyItems());
    }

    private void setUpButtons() {
        this.signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out with Firebase
            }
        });
    }

    private void setUpElements() {
        getFirebaseUser();
        retrieveUserObject();
        this.editMyName.setText("Dillon");
        this.myEmail.setText("dillonh2@illinois.edu");
        this.editMyDefaultContactInfo.setText("Email: dillonh2@illinois.edu\nPhone: 1234567890");
    }

    private void getFirebaseUser() {
        // Get's the current FireBase signed in user
    }

    private void retrieveUserObject() {
        // This retrieves hte user's object from FireBase DB
    }

    private List<Item> getMyItems() {
        return Arrays.asList(
                new Item("My Item", "Amazing Description 2", 20, "Dillon", "https://img.purch.com/h/1000/aHR0cDovL3d3dy5zcGFjZS5jb20vaW1hZ2VzL2kvMDAwLzAxOS8wOTEvb3JpZ2luYWwvanVseS1za3l3YXRjaGluZy1wb3J0bGFuZC5qcGc=", "Phone: 1234567890\nEmail:test@gmail.com")
        );
    }

    private void displayItems(List<Item> itemsToDisplay) {
        final ItemsAdapter itemsAdapter = new ItemsAdapter(itemsToDisplay, R.layout.profile_page);
        final RecyclerView itemList = findViewById(R.id.myItemsRecyclerView);
        itemList.setAdapter(itemsAdapter);
        itemList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}

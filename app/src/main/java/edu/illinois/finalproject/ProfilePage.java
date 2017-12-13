package edu.illinois.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
    }

    private void setUpButtons() {
        this.signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                    .signOut(ProfilePage.this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(
                                        ProfilePage.this,
                                        "Signed Out",
                                        Toast.LENGTH_LONG).show();
                                startActivity(
                                        new Intent(
                                                ProfilePage.this,
                                                MainActivity.class
                                        )
                                );
                            } else {
                                Toast.makeText(
                                        ProfilePage.this,
                                        "Did NOT Sign Out",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
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
        // TODO: Query FireBase
        return null;
    }
}

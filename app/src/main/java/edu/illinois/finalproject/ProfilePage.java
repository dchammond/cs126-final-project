package edu.illinois.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    public static final String APP_USER_KEY = "app_user";
    public static final String APP_USER_EMAIL = "user_email";

    private User currentUser;
    private String email;

    private Button addNewItemButton;
    private Button signoutButton;
    private Button updateInfoButton;

    private EditText editMyName;
    private TextView myEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        this.currentUser = extractUser();
        this.email = extractEmail();

        this.updateInfoButton = findViewById(R.id.updateUser);
        this.addNewItemButton = findViewById(R.id.addNewItemButton);
        this.signoutButton = findViewById(R.id.signoutButton);
        setUpButtons();

        this.editMyName = findViewById(R.id.editMyName);
        this.myEmail = findViewById(R.id.myEmail);
        setUpElements();
    }

    private void setUpButtons() {
        this.updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User editedUser = ProfilePage.this.currentUser;
                editedUser.setDisplayName(ProfilePage.this.editMyName.getText().toString());
                User.updateUser(editedUser, new updatedUser());
            }
        });
        this.addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Whole activity for creating new items.
            }
        });
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
        this.editMyName.setText(this.currentUser.getDisplayName());
        this.myEmail.setText(this.email);
    }

    private User extractUser() {
        final Intent intent = getIntent();
        return intent.getParcelableExtra(APP_USER_KEY);
    }

    private String extractEmail() {
        final Intent intent = getIntent();
        return intent.getParcelableExtra(APP_USER_EMAIL);
    }

    private static class updatedUser extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... booleans) {
            if (booleans.length > 0) {
                boolean edited = booleans[0];
                if (!edited) {
                    Log.e(MainActivity.TAG, "Failed to edit user");
                }
            }
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            Log.e(MainActivity.TAG, "Editing user was canceled");
        }
    }
}

package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "final-project";
    private static final int RESULT_AUTH = 3;

    private static final List<AuthUI.IdpConfig> signupProviders = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
    );
    private FirebaseUser currentUser;

    private TabLayout tabLayout;
    private int currentTab;

    private Button myProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (this.currentUser == null) {
            signIn();
        } else {
            Log.i(TAG, "We are logged in as: " + this.currentUser);
        }

        this.tabLayout = findViewById(R.id.tabLayout);
        setUpTabs();

        this.myProfileButton = findViewById(R.id.myProfileButton);
        setUpButtons();

        displayItems(getAllItems());
    }

    private void signIn() {
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(signupProviders)
                        .build(),
                RESULT_AUTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case RESULT_AUTH:
                    this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    break;
                default:
                    Log.e(MainActivity.TAG,"Got requestCode:" + requestCode);
                    break;
            }
        } else {
            Log.e(TAG, "resultCode was NOT RESULT_OKAY, it was: " + resultCode);
        }
    }

    private void setUpTabs() {
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        // Make sure all Items load
                        Log.i("FINAL", "Tab 0 selected");
                        MainActivity.this.currentTab = 0;
                        displayItems(getAllItems());
                        break;
                    case 1:
                        // Make sure My Items load
                        Log.i("FINAL", "Tab 1 selected");
                        MainActivity.this.currentTab = 1;
                        displayItems(getMyItems());
                        break;
                    default:
                        Log.e("FINAL", "Tab was out of bounds!");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpButtons() {
        this.myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                Intent profileIntent = new Intent(context, ProfilePage.class);
                context.startActivity(profileIntent);
            }
        });
    }

    private List<Item> getAllItems() {
        // TODO: FireBase Query
        return Arrays.asList();
    }

    private List<Item> getMyItems() {
        // TODO: FireBase Query
        return Arrays.asList();
    }

    private void displayItems(List<Item> itemsToDisplay) {
        final ItemsAdapter itemsAdapter = new ItemsAdapter(itemsToDisplay, this.currentTab);
        final RecyclerView itemList = findViewById(R.id.itemsRecyclerView);
        itemList.setAdapter(itemsAdapter);
        itemList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public int getCurrentTab() {
        return currentTab;
    }
}

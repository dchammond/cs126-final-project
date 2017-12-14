package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "final-project";
    private static final int RESULT_AUTH = 3;

    private static final List<AuthUI.IdpConfig> signupProviders = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
    );
    private FirebaseUser firebaseUser;
    private User currentUser;

    private TabLayout tabLayout;
    private int currentTab;

    private Button myProfileButton;

    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (this.firebaseUser == null) {
            signIn();
        } else {
            Log.i(TAG, "We are logged in as: " + this.firebaseUser);
            setUpUser();
        }

        this.tabLayout = findViewById(R.id.tabLayout);
        setUpTabs();

        this.myProfileButton = findViewById(R.id.myProfileButton);
        setUpButtons();

        displayAllItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshButton:
                switch (this.currentTab) {
                    case 0:
                        displayAllItems();
                        break;
                    case 1:
                        displayMyItems();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return true;
    }

    private class findUserTask extends AsyncTask<User, Void, Void> {

        private FirebaseUser firebaseUser;

        public findUserTask(FirebaseUser firebaseUser) {
            super();
            this.firebaseUser = firebaseUser;
        }

        @Override
        protected Void doInBackground(User... users) {
            if (users.length == 0) {
                FirebaseUser user = this.firebaseUser;
                User newUser = new User(
                        user.getDisplayName(),
                        new ArrayList<ItemPointer>());
                User.createUser(newUser, new createUserTask());
                MainActivity.this.currentUser = newUser;
            }
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }

    private static class createUserTask extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            boolean userCreated = booleans[0];
            if (!userCreated) {
                Log.e(MainActivity.TAG, "Failed to create a user in FireBase");
            }
            return null;
        }
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
                    this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    setUpUser();
                    break;
                default:
                    Log.e(MainActivity.TAG,"Got requestCode:" + requestCode);
                    break;
            }
        } else {
            Log.e(TAG, "resultCode was NOT RESULT_OKAY, it was: " + resultCode);
        }
    }

    private void setUpUser() {
        String userId = this.firebaseUser.getUid();
        User.findUser(userId, new findUserTask(this.firebaseUser));
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
                        displayAllItems();
                        break;
                    case 1:
                        // Make sure My Items load
                        Log.i("FINAL", "Tab 1 selected");
                        MainActivity.this.currentTab = 1;
                        displayMyItems();
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
                profileIntent.putExtra(ProfilePage.APP_USER_KEY, MainActivity.this.currentUser);
                profileIntent.putExtra(ProfilePage.APP_USER_EMAIL, MainActivity.this.firebaseUser.getEmail());
                context.startActivity(profileIntent);
            }
        });
    }

    private void displayAllItems() {
        Item.getAllItems(new getAllItems());
    }

    private void displayMyItems() {
        Item.getAllItems(new getMyItems());
    }

    private class getAllItems extends AsyncTask<Item, Void, Void> {
        @Override
        protected Void doInBackground(Item... items) {
            List<Item> allItems = Arrays.asList(items);
            MainActivity.this.displayItems(allItems);
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            Log.e(MainActivity.TAG, "Failed to get all Items");
        }
    }

    private class getMyItems extends AsyncTask<Item, Void, Void> {
        @Override
        protected Void doInBackground(Item... items) {
            List<Item> allItems = Arrays.asList(items);
            List<Item> myItems = new ArrayList<>(allItems.size());
            for (Item item : allItems) {
                if (item.getSellerPointer()
                        .getUserId()
                        .equals(MainActivity.this.currentUser.getUserId())) {
                    myItems.add(item);
                }
            }
            MainActivity.this.displayItems(myItems);
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            Log.e(MainActivity.TAG, "Failed to get my Items");
        }
    }

    private void displayItems(List<Item> itemsToDisplay) {
        if (this.itemsAdapter == null) {
            this.itemsAdapter = new ItemsAdapter(itemsToDisplay, this.currentTab);
            final RecyclerView itemList = findViewById(R.id.itemsRecyclerView);
            itemList.setAdapter(this.itemsAdapter);
            itemList.setLayoutManager(
                    new LinearLayoutManager(this,
                            LinearLayoutManager.VERTICAL,
                            false)
            );
        } else {
            this.itemsAdapter.refreshItems(itemsToDisplay);
        }
    }

    public int getCurrentTab() {
        return currentTab;
    }
}

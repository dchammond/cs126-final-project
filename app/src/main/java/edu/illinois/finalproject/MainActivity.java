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
    /* ------------------MEMBER VARIABLES------------------ */
    public static final String TAG = "final-project";
    private static final int RESULT_AUTH = 1;
    private static final int RESULT_PROFILE = 2;
    public static final String USER_KEY = "user_key";

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
    /* ------------------MEMBER VARIABLES------------------ */

    /* ------------------OVERRIDES------------------------- */
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
    protected void onResume() {
        super.onResume();
        if (this.itemsAdapter != null) {
            switch(this.currentTab) {
                case 0:
                    this.itemsAdapter.setTabPosition(0);
                    displayAllItems();
                    break;
                case 1:
                    this.itemsAdapter.setTabPosition(1);
                    displayMyItems();
                    break;
                default:
                    break;
            }
        }
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
                if (this.itemsAdapter != null) {
                    switch (this.currentTab) {
                        case 0:
                            this.itemsAdapter.setTabPosition(0);
                            displayAllItems();
                            break;
                        case 1:
                            this.itemsAdapter.setTabPosition(1);
                            displayMyItems();
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case RESULT_AUTH:
                    this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    setUpUser();
                    break;
                case RESULT_PROFILE:
                    this.currentUser = (User) data.getExtras().get(USER_KEY);
                    break;
                default:
                    Log.e(MainActivity.TAG,"Got requestCode:" + requestCode);
                    break;
            }
        } else {
            Log.e(TAG, "resultCode was NOT RESULT_OKAY, it was: " + resultCode);
        }
    }
    /* ------------------OVERRIDES------------------------- */

    /* ------------------METHODS--------------------------- */
    // Launch Firebase UI sign-in
    private void signIn() {
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(signupProviders)
                        .build(),
                RESULT_AUTH);
    }

    // Dispatches AsyncTask calls to find or create the User obejct
    private void setUpUser() {
        String firebaseId = this.firebaseUser.getUid();
        User.findUser(null, firebaseId, new FindUserTask(this.firebaseUser));
    }

    // Tell the All Items and My Items tabs how to behave
    private void setUpTabs() {
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        // Make sure all Items load
                        Log.i(MainActivity.TAG, "Tab 0 selected");
                        MainActivity.this.currentTab = 0;
                        MainActivity.this.itemsAdapter.setTabPosition(0);
                        displayAllItems();
                        break;
                    case 1:
                        // Make sure My Items load
                        Log.i(MainActivity.TAG, "Tab 1 selected");
                        MainActivity.this.currentTab = 1;
                        MainActivity.this.itemsAdapter.setTabPosition(1);
                        displayMyItems();
                        break;
                    default:
                        Log.e(MainActivity.TAG, "Tab was out of bounds!");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
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
                startActivityForResult(profileIntent, RESULT_PROFILE);
            }
        });
    }

    private void displayAllItems() {
        Item.getAllItems(new GetAllItems());
    }

    private void displayMyItems() {
        Item.getAllItems(new GetMyItems());
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
    /* ------------------METHODS--------------------------- */

    /* ------------------ASYNC TASKS----------------------- */

    /**
     * A FindUserTask is an AsyncTask used to handle retrieving the logged-in user's app-specific
     * User object, or create one if none exists utilizing CreateUserTask
     */
    private class FindUserTask extends AsyncTask<User, Void, Void> {
        private FirebaseUser firebaseUser;

        public FindUserTask(FirebaseUser firebaseUser) {
            super();
            this.firebaseUser = firebaseUser;
        }

        @Override
        protected Void doInBackground(User... users) {
            if (users.length == 0) {
                // Necessary because some code cannot be run off of the UI thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseUser user = FindUserTask.this.firebaseUser;
                        User newUser = new User(
                                user.getUid(),
                                user.getDisplayName(),
                                new ArrayList<ItemPointer>());
                        User.createUser(newUser, new CreateUserTask());
                        MainActivity.this.currentUser = newUser;
                    }
                });
            } else {
                MainActivity.this.currentUser = users[0];
            }
            Log.i(MainActivity.TAG, "Found or Created User");
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }

    /**
     * A CreateUserTask is an AsyncTask used to create a new User object in FireBase.
     * The Boolean return value is useful to check if the operation succeeded
     */
    private static class CreateUserTask extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            boolean userCreated = booleans[0];
            if (!userCreated) {
                Log.e(MainActivity.TAG, "Failed to create a user in FireBase");
            }
            return null;
        }
    }

    /**
     * A GetAllItems is an AsyncTask to hand;e retrieving a list fo all Items from the database
     */
    private class GetAllItems extends AsyncTask<Item, Void, Void> {
        @Override
        protected Void doInBackground(Item... items) {
            final List<Item> allItems = Arrays.asList(items);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayItems(allItems);
                }
            });
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            Log.e(MainActivity.TAG, "Failed to get all Items");
        }
    }

    /**
     * A GetMyItems is an AsyncTask to hand;e retrieving a list fo all Items from the database
     * and then discards those not created by the current user
     */
    private class GetMyItems extends AsyncTask<Item, Void, Void> {
        @Override
        protected Void doInBackground(Item... items) {
            List<Item> allItems = Arrays.asList(items);
            final List<Item> myItems = new ArrayList<>(allItems.size());
            for (Item item : allItems) {
                if (item.getSellerPointer()
                        .getUserId()
                        .equals(MainActivity.this.currentUser.getUserId())) {
                    myItems.add(item);
                }
            }
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayItems(myItems);
                }
            });
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            Log.e(MainActivity.TAG, "Failed to get my Items");
        }
    }
    /* ------------------ASYNC TASKS----------------------- */
}

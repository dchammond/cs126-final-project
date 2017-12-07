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

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    private Button myProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tabLayout = findViewById(R.id.tabLayout);
        setUpTabs();

        this.myProfileButton = findViewById(R.id.myProfileButton);
        setUpButtons();

        displayItems(getAllItems());
    }

    private void setUpTabs() {
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        // Make sure all Items load
                        Log.i("FINAL", "Tab 0 selected");
                        displayItems(getAllItems());
                        break;
                    case 1:
                        // Make sure My Items load
                        Log.i("FINAL", "Tab 1 selected");
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
        return Arrays.asList(
                new Item("First Item", "Amazing Description 1", 10, "Seller 1", "https://az616578.vo.msecnd.net/files/2016/07/23/636049076842624124-2115961982_sun.jpg", "Phone: 1234567890\nEmail:test@gmail.com"),
                new Item("My Item", "Amazing Description 2", 20, "Dillon", "https://img.purch.com/h/1000/aHR0cDovL3d3dy5zcGFjZS5jb20vaW1hZ2VzL2kvMDAwLzAxOS8wOTEvb3JpZ2luYWwvanVseS1za3l3YXRjaGluZy1wb3J0bGFuZC5qcGc=", "Email: dillonh2@illinois.edu\nPhone: 1234567890")
        );
    }

    private List<Item> getMyItems() {
        return Arrays.asList(
                new Item("My Item", "Amazing Description 2", 20, "Dillon", "https://img.purch.com/h/1000/aHR0cDovL3d3dy5zcGFjZS5jb20vaW1hZ2VzL2kvMDAwLzAxOS8wOTEvb3JpZ2luYWwvanVseS1za3l3YXRjaGluZy1wb3J0bGFuZC5qcGc=", "Email: dillonh2@illinois.edu\nPhone: 1234567890")
        );
    }

    private void displayItems(List<Item> itemsToDisplay) {
        final ItemsAdapter itemsAdapter = new ItemsAdapter(itemsToDisplay, R.layout.activity_main);
        final RecyclerView itemList = findViewById(R.id.itemsRecyclerView);
        itemList.setAdapter(itemsAdapter);
        itemList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}

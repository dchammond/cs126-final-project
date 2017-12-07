package edu.illinois.finalproject;

import android.support.design.widget.TabItem;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private TabItem allItemsTab;
    private TabItem myItemsTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allItemsTab = findViewById(R.id.allItemsTab);
        myItemsTab = findViewById(R.id.myItemsTab);
        setUpTabs();


    }

    private void setUpTabs() {
        allItemsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make sure the All Items loads
            }
        });
        myItemsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make sure My Items loads
            }
        });
    }
}

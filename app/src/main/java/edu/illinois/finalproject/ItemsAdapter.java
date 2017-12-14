package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dillon on 12/6/17.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private List<Item> allItemsToDisplay = new ArrayList<>();
    private int tabPosition;

    /**
     * Create a new ItemsAdapter to get displayed on screen
     * @param allItemsToDisplay The Items to display
     * @param tabPosition The current Tab selected (All items = 0, My Items = 1;
     */
    public ItemsAdapter(List<Item> allItemsToDisplay, int tabPosition) {
        this.allItemsToDisplay = allItemsToDisplay;
        this.tabPosition = tabPosition;
    }

    public void refreshItems(List<Item> newItems) {
        this.allItemsToDisplay = newItems;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_with_image;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemToDisplay = LayoutInflater.from(parent.getContext()).
                inflate(viewType, parent, false);

        return new ViewHolder(itemToDisplay);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item itemToDisplay = this.allItemsToDisplay.get(position);
        itemToDisplay.getSellerPointer().getRealUser(new getRealUser(holder.itemSeller));
        holder.itemName.setText(itemToDisplay.getItemName());
        holder.itemPrice.setText("$" + itemToDisplay.getItemPrice().toString());
        final Context context = holder.itemView.getContext();
        Picasso.with(context).load(itemToDisplay.getImageUri()).into(holder.itemImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();

                if (ItemsAdapter.this.tabPosition == 1) {
                    Intent detailedIntent = new Intent(context, EditableItem.class);

                    detailedIntent.putExtra(EditableItem.getEditableItemKey(), itemToDisplay);

                    context.startActivity(detailedIntent);
                } else {
                    Intent detailedIntent = new Intent(context, DetailedItem.class);

                    detailedIntent.putExtra(DetailedItem.getItemKey(), itemToDisplay);

                    context.startActivity(detailedIntent);
                }
            }
        });
    }

    private static class getRealUser extends AsyncTask<User, Void, Void> {
        private TextView itemSeller;

        public getRealUser(TextView itemSeller) {
            super();
            this.itemSeller = itemSeller;
        }

        @Override
        protected Void doInBackground(User... users) {
            if (users.length > 0) {
                this.itemSeller.setText(users[0].getDisplayName());
            }
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            Log.e(MainActivity.TAG, "Could not load a User for a ItemsAdapter");
        }
    }

    @Override
    public int getItemCount() {
        return allItemsToDisplay.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemSeller;
        public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemName = itemView.findViewById(R.id.itemName);
            this.itemPrice= itemView.findViewById(R.id.itemPrice);
            this.itemSeller = itemView.findViewById(R.id.itemSeller);
            this.itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}

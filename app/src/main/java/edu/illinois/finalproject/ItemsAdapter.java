package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
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
    private int layoutId;

    /**
     * Create a new ItemsAdapter to get displayed on screen
     * @param allItemsToDisplay The Items to display
     * @param layoutId The Layout ID of the calling activity, used to determine editing of items
     */
    public ItemsAdapter(List<Item> allItemsToDisplay, int layoutId) {
        this.allItemsToDisplay = allItemsToDisplay;
        this.layoutId = layoutId;
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
        holder.itemName.setText(itemToDisplay.getName());
        holder.itemPrice.setText("$" + itemToDisplay.getPrice().toString());
        holder.itemSeller.setText(itemToDisplay.getSeller());
        final Context context = holder.itemView.getContext();
        Picasso.with(context).load(itemToDisplay.getImageUrl()).into(holder.itemImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();

                if (ItemsAdapter.this.layoutId == R.layout.profile_page) {
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

package com.example.sqlite0.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sqlite0.activities.EditItemActivity;
import com.example.sqlite0.R;
import com.example.sqlite0.models.Item;
import com.example.sqlite0.viewmodels.ItemViewModel;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private Context context;
    private ItemViewModel itemViewModel;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        // Khởi tạo ItemViewModel
        if (context instanceof ViewModelStoreOwner) {
            itemViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ItemViewModel.class);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvCategory.setText(item.getCategory());
        holder.tvPrice.setText(item.getPrice());
        holder.tvDate.setText(item.getDate());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditItemActivity.class);
            intent.putExtra("item_id", item.getId());
            intent.putExtra("item_title", item.getTitle());
            intent.putExtra("item_category", item.getCategory());
            intent.putExtra("item_price", item.getPrice());
            intent.putExtra("item_date", item.getDate());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvPrice, tvDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    public void updateData(List<Item> newItemList) {
        this.itemList = newItemList;
        notifyDataSetChanged();
    }
}
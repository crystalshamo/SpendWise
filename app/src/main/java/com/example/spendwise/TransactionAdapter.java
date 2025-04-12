package com.example.spendwise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public final Map<String, List<TransactionItem>> groupedData;
    public final Set<String> expanded = new HashSet<>();

    public TransactionAdapter(Map<String, List<TransactionItem>> groupedData) {
        this.groupedData = groupedData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryText;
        public LinearLayout detailLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.category_title);
            detailLayout = itemView.findViewById(R.id.transaction_detail_list);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String category = new ArrayList<>(groupedData.keySet()).get(position);
        holder.categoryText.setText(category);
        holder.detailLayout.removeAllViews();

        // Show transactions if expanded
        if (expanded.contains(category)) {
            for (TransactionItem item : groupedData.get(category)) {
                TextView tx = new TextView(holder.itemView.getContext());
                tx.setText(String.format("• $%.2f on %s\n%s",
                        item.getAmount(),
                        item.getDate(),
                        item.getDescription()));
                tx.setPadding(32, 8, 8, 16);
                holder.detailLayout.addView(tx);
            }
        }

        // Toggle expand/collapse
        holder.itemView.setOnClickListener(v -> {
            if (expanded.contains(category)) expanded.remove(category);
            else expanded.add(category);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return groupedData.size();
    }
}

package com.sp.finvue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions;
    private Context context;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        // Set data to views
        holder.articleTitle.setText(transaction.getName());
        holder.transactionCategory.setText(transaction.getCategory());
        holder.transactionLocation.setText(transaction.getLocation());
        holder.transactionAmount.setText(String.format(context.getString(R.string.amount_format), transaction.getAmount()));
        // Set the category icon dynamically based on the transaction category
        holder.categoryIcon.setText(transaction.getCategoryIcon()); // Replace with the actual logic to set the icon
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView articleTitle;
        TextView transactionCategory;
        TextView transactionLocation;
        TextView transactionAmount;
        TextView categoryIcon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views from transaction_item.xml
            articleTitle = itemView.findViewById(R.id.article_title);
            transactionCategory = itemView.findViewById(R.id.transactionCategory);
            transactionLocation = itemView.findViewById(R.id.transactionLocation);
            transactionAmount = itemView.findViewById(R.id.transactionAmount);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
        }
    }
}

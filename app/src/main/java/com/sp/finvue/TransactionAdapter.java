package com.sp.finvue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

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
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionUserId;
        TextView transactionId;
        TextView transactionName;
        TextView transactionCategory;
        TextView transactionLocation;
        TextView transactionAmount;
        TextView categoryIcon;
        TextView transactionMop;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views from transaction_item.xml
            //transactionName = itemView.findViewById(R.id.transactionName);
            transactionName = itemView.findViewById(R.id.transName);
            transactionCategory = itemView.findViewById(R.id.transactionCategory);
            transactionLocation = itemView.findViewById(R.id.transactionLocation);
            transactionAmount = itemView.findViewById(R.id.transactionAmount);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
        }

        public void bind(Transaction transaction) {
//            transactionUserId.setText(transaction.getUserId());
//            transactionId.setText(transaction.getId());
            transactionName.setText(transaction.getName());
            transactionCategory.setText(transaction.getCategory());
            transactionLocation.setText(transaction.getLocation());
//            transactionMop.setText(transaction.getMop());
            transactionAmount.setText(String.format(Locale.getDefault(), "$%.2f", transaction.getCost()));

            // Set category icon based on transaction category
            int iconResource = getCategoryIcon(transaction.getCategory());
            if (iconResource != 0) {
                categoryIcon.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, 0, 0);
            }

        }

        private int getCategoryIcon(String category) {
            switch (category) {
                case "Groceries":
                    return R.drawable.ic_groceries;
                case "Transport":
                    return R.drawable.ic_transportation;
                case "Shopping":
                    return R.drawable.ic_shopping;
                case "Food":
                    return R.drawable.ic_food;
                case "Entertainment":
                    return R.drawable.ic_entertainment;
                case "Transfer to Friend":
                    return R.drawable.ic_transfer;
                case "Other":
                    return R.drawable.ic_cash;
                default:
                    return 0; // No icon found
            }
        }

    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void clear() {
        transactions.clear();
        notifyDataSetChanged();
    }
}

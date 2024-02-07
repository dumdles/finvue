package com.sp.finvue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions;
    private Context context;

    // Constructor
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

        // Put code here to activate the dialog
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create and show the dialog here
                showDialogWithDetails(context, transaction);
            }
        });


        holder.bind(transaction);
    }

    public void showDialogWithDetails(Context context, Transaction transaction) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_transaction_details, null);
        builder.setView(dialogView);

        // Find views in the dialog layout
        TextView textViewTransName = dialogView.findViewById(R.id.textViewTransName);
        TextView textViewAmount = dialogView.findViewById(R.id.textViewAmount);
        TextView textViewDate = dialogView.findViewById(R.id.textViewDate);
        TextView textViewMop = dialogView.findViewById(R.id.textViewMop);
        TextView textViewLocation = dialogView.findViewById(R.id.textViewLocation);
        TextView textViewCategory = dialogView.findViewById(R.id.textViewCategory);
        TextView textViewRemarks = dialogView.findViewById(R.id.textViewRemarks);
        TextView textViewTransId = dialogView.findViewById(R.id.textViewTransId);
        TextView categoryIcon = dialogView.findViewById(R.id.categoryIcon); // Find the category icon TextView


        // Set transaction details to the dialog views
        textViewTransName.setText(transaction.getName());
        textViewAmount.setText(String.format(Locale.getDefault(), "$%.2f", transaction.getCost()));
        textViewDate.setText(transaction.getDate());
        textViewMop.setText(transaction.getMop());
        textViewLocation.setText(transaction.getLocation());
        textViewCategory.setText(transaction.getCategory());
        textViewRemarks.setText(transaction.getRemarks());
        textViewTransId.setText(transaction.getId());

        // Set category icon based on transaction category
        int iconResource = getCategoryIcon(transaction.getCategory());
        if (iconResource != 0) {
            Drawable icon = ContextCompat.getDrawable(context, iconResource);
            if (icon != null) {
                // Enlarge the icon
                icon.setBounds(0, 0, icon.getIntrinsicWidth() * 2, icon.getIntrinsicHeight() * 2);
                categoryIcon.setCompoundDrawables(icon, null, null, null);
            }        }

        Button buttonDiscard = dialogView.findViewById(R.id.buttonDiscard);
        buttonDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle discard action
            }
        });

        Button buttonEdit = dialogView.findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit action
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private static int getCategoryIcon(String category) {
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

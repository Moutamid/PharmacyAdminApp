package com.moutamid.pharmacyadminapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moutamid.pharmacyadminapp.activities.AddBankActivity;
import com.moutamid.pharmacyadminapp.model.BankModel;

import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {
    private Context context;
    private List<BankModel> bankList;
    private DatabaseReference databaseReference;

    public BankAdapter(Context context, List<BankModel> bankList) {
        this.context = context;
        this.bankList = bankList;
        databaseReference = Constants.databaseReference().child("Banks");
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bank, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        BankModel bank = bankList.get(position);
        holder.bankName.setText(bank.getBankName());
        holder.accountHolder.setText(bank.getAccountHolder());
        holder.accountNumber.setText(bank.getAccountNumber());
        holder.extraInfo.setText(bank.getExtraInfo());

        if (!bank.getLogoUrl().isEmpty()) {
            Glide.with(context).load(bank.getLogoUrl()).into(holder.bankLogo);
        }

        holder.editBank.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddBankActivity.class);
            intent.putExtra("bankId", bank.getId());
            intent.putExtra("bankName", bank.getBankName());
            intent.putExtra("accountHolder", bank.getAccountHolder());
            intent.putExtra("accountNumber", bank.getAccountNumber());
            intent.putExtra("extraInfo", bank.getExtraInfo());
            intent.putExtra("bankLogoUrl", bank.getLogoUrl());
            context.startActivity(intent);
        });

        holder.deleteBank.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Bank")
                    .setMessage("Are you sure you want to delete this bank?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        databaseReference.child(bank.getId()).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    bankList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Bank deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    public static class BankViewHolder extends RecyclerView.ViewHolder {
        TextView bankName, accountHolder, accountNumber, extraInfo;
        ImageView bankLogo, editBank, deleteBank;

        public BankViewHolder(@NonNull View itemView) {
            super(itemView);
            bankName = itemView.findViewById(R.id.bankName);
            accountHolder = itemView.findViewById(R.id.accountHolder);
            accountNumber = itemView.findViewById(R.id.accountNumber);
            extraInfo = itemView.findViewById(R.id.extraInfo);
            bankLogo = itemView.findViewById(R.id.bankLogo);
            editBank = itemView.findViewById(R.id.editBank);
            deleteBank = itemView.findViewById(R.id.deleteBank);
        }
    }
}

package com.moutamid.pharmacyadminapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.pharmacyadminapp.databinding.ActivityPaymentBinding;
import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    PaymentAdapter adapter;
    ArrayList<PaymentModel> list;

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        getData();
    }

    private void getData() {

        Constants.databaseReference().child(Constants.COURSE_PAYMENTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constants.dismissDialog();
                list.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

//                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            PaymentModel model = dataSnapshot.getValue(PaymentModel.class);
                            if (!model.isApprove()&&model.getSelectedType().equals(Stash.getString("type"))) {
                                model.setKey(dataSnapshot.getKey());
                                list.add(model);
//                            }

                        }
                    }
                } else {
                    Toast.makeText(PaymentActivity.this, "No payments found", Toast.LENGTH_SHORT).show();
                }
                adapter = new PaymentAdapter(PaymentActivity.this, list);
                binding.paymentRC.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Constants.dismissDialog();
                Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> finish());

        list = new ArrayList<>();

        binding.paymentRC.setHasFixedSize(false);
        binding.paymentRC.setLayoutManager(new LinearLayoutManager(this));
    }
}
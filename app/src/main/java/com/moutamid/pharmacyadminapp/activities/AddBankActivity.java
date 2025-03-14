package com.moutamid.pharmacyadminapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moutamid.pharmacyadminapp.Constants;
import com.moutamid.pharmacyadminapp.R;
import com.moutamid.pharmacyadminapp.model.BankModel;
public class AddBankActivity extends AppCompatActivity {
    private EditText bankNameInput, accountHolderInput, accountNumberInput, extraInfoInput;
    private ImageView bankLogoPreview;
    private Button saveBankButton;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String bankId, bankLogoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);

        bankNameInput = findViewById(R.id.bankNameInput);
        accountHolderInput = findViewById(R.id.accountHolderInput);
        accountNumberInput = findViewById(R.id.accountNumberInput);
        extraInfoInput = findViewById(R.id.extraInfoInput);
        bankLogoPreview = findViewById(R.id.bankLogoPreview);
        saveBankButton = findViewById(R.id.saveBankButton);

        storageReference = FirebaseStorage.getInstance().getReference("bank_logos");
        databaseReference = Constants.databaseReference().child("Banks");

        findViewById(R.id.uploadLogoButton).setOnClickListener(v -> selectImage());

        // Check if editing an existing bank
        Intent intent = getIntent();
        if (intent.hasExtra("bankId")) {
            bankId = intent.getStringExtra("bankId");
            bankNameInput.setText(intent.getStringExtra("bankName"));
            accountHolderInput.setText(intent.getStringExtra("accountHolder"));
            accountNumberInput.setText(intent.getStringExtra("accountNumber"));
            extraInfoInput.setText(intent.getStringExtra("extraInfo"));
            bankLogoUrl = intent.getStringExtra("bankLogoUrl");

            if (bankLogoUrl != null && !bankLogoUrl.isEmpty()) {
                Glide.with(this).load(bankLogoUrl).into(bankLogoPreview);
            }

            saveBankButton.setText("Update Bank");
        } else {
            bankId = databaseReference.push().getKey();
        }

        saveBankButton.setOnClickListener(v -> saveBankDetails());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            bankLogoPreview.setImageURI(imageUri);
        }
    }

    private void saveBankDetails() {
        String bankName = bankNameInput.getText().toString().trim();
        String accountHolder = accountHolderInput.getText().toString().trim();
        String accountNumber = accountNumberInput.getText().toString().trim();
        String extraInfo = extraInfoInput.getText().toString().trim();

        if (bankName.isEmpty() || accountHolder.isEmpty() || accountNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(bankId + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateBank(bankId, bankName, accountHolder, accountNumber, extraInfo, uri.toString());
                    })
            ).addOnFailureListener(e -> Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
        } else {
            updateBank(bankId, bankName, accountHolder, accountNumber, extraInfo, bankLogoUrl);
        }
    }

    private void updateBank(String id, String bankName, String accountHolder, String accountNumber, String extraInfo, String logoUrl) {
        BankModel bank = new BankModel(id, bankName, accountHolder, accountNumber, extraInfo, logoUrl);
        databaseReference.child(id).setValue(bank)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bank saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save bank", Toast.LENGTH_SHORT).show());
    }
}

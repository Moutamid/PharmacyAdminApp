package com.moutamid.pharmacyadminapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.pharmacyadminapp.activities.AllBankActivity;
import com.moutamid.pharmacyadminapp.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stash.put("type", "user");
                startActivity(new Intent(MainActivity.this, PaymentActivity.class));
            }
        });
        binding.paymentowner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stash.put("type", "owner");
                startActivity(new Intent(MainActivity.this,PaymentActivity.class));
            }
        });    binding.bankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stash.put("type", "owner");
                startActivity(new Intent(MainActivity.this, AllBankActivity.class));
            }
        });
        binding.pricing.setOnClickListener(v -> {
            showDialog();
        });
        checkApp(MainActivity.this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }

    private Dialog pricingDialog;
    private void getPricing() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.PRICES).get()
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Constants.dismissDialog();
                })
                .addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        String year = dataSnapshot.child("YEAR").getValue(String.class);
                        String six_month = dataSnapshot.child("SIX_MONTH").getValue(String.class);
                        String month = dataSnapshot.child("MONTH").getValue(String.class);
                        updateDialogValues(year, six_month, month);
                    } else {
                        // Default values if no data exists
                        updateDialogValues("130", "100", "90");
                    }
                });
    }

    private void showDialog() {
        pricingDialog = new Dialog(this);
        pricingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pricingDialog.setContentView(R.layout.pricing);
        pricingDialog.show();
        pricingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pricingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pricingDialog.getWindow().setGravity(Gravity.CENTER);

        TextInputLayout monthly = pricingDialog.findViewById(R.id.monthly);
        TextInputLayout month_six = pricingDialog.findViewById(R.id.six_month);
        TextInputLayout yearly = pricingDialog.findViewById(R.id.year);
        Button update = pricingDialog.findViewById(R.id.update);

        // Initialize with data for the default type
        getPricing();

        // Handle Update Button
        update.setOnClickListener(v -> {
            if (!monthly.getEditText().getText().toString().trim().isEmpty() &&
                    !yearly.getEditText().getText().toString().trim().isEmpty() &&
                    !month_six.getEditText().getText().toString().trim().isEmpty()
            ) {
                Map<String, Object> price = new HashMap<>();
                price.put("MONTH", monthly.getEditText().getText().toString().trim());
                price.put("YEAR", yearly.getEditText().getText().toString().trim());
                price.put("SIX_MONTH", month_six.getEditText().getText().toString().trim());

                Constants.databaseReference().child(Constants.PRICES).updateChildren(price)
                        .addOnSuccessListener(unused -> {
                            pricingDialog.dismiss();
                            Toast.makeText(this, "Prices Updated", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            pricingDialog.dismiss();
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDialogValues(String year, String six_month, String month) {
        if (pricingDialog != null && pricingDialog.isShowing()) {
            TextInputLayout monthly = pricingDialog.findViewById(R.id.monthly);
            TextInputLayout month_six = pricingDialog.findViewById(R.id.six_month);
            TextInputLayout yearly = pricingDialog.findViewById(R.id.year);

            monthly.getEditText().setText(month);
            month_six.getEditText().setText(six_month);
            yearly.getEditText().setText(year);


        }
    }



    public static void checkApp(Activity activity) {
        String appName = "PharmacyAppAdmin";

        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if ((input = in != null ? in.readLine() : null) == null) break;
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData).getJSONObject(appName);

                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");

                if (value) {
                    activity.runOnUiThread(() -> {
                        new AlertDialog.Builder(activity)
                                .setMessage(msg)
                                .setCancelable(false)
                                .show();
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

}
package com.moutamid.pharmacyadminapp.model;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.moutamid.pharmacyadminapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private EditText etMessage, etTitle, etToken;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        etToken = findViewById(R.id.etToken);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String message = etMessage.getText().toString().trim();
                String token = "d6cs3uElTu2t2aKRkfHIt1:APA91bGE02KyFCuLoQf-puBH1d0EyF18pDwKz9zCaHqT4oJB26QT5SQii0sPIChDbLowcESewdJuy4ZRU3Wf0QL-lu_77-Jd8s-FFkmvZuuFIw6k7cPLN3E";



                sendFCMPush(token, "dfjhsdihf");
            }
        });
    }

    private void sendFCMPush(String token, String message) {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();

        try {
            notifcationBody.put("title", "Order Alert");
            notifcationBody.put("message", message);

            notification.put("to", token); // Use the FCM token of the recipient device
            notification.put("data", notifcationBody);

            Log.e("DATAAAAAA", notification.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST,
                Config.FCM_URL,
                notification,
                response -> {
                    Log.e("True", response + "");
                    Log.d("Response", response.toString());
                },
                error -> {
                    Log.e("False", error + "");
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + Config.SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60; // 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }}
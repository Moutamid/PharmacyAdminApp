package com.moutamid.pharmacyadminapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;

import com.fxn.stash.Stash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class Constants {

    static Dialog dialog;
    public static final String DATE_FORMAT = "dd_MM_yyyy_hh_mm_ss";
    public static final String MODEL_PAPERS = "MODEL_PAPERS";
    public static final String SELECT = "SELECT";
    public static final String TOPICS = "TOPICS";
    public static final String PRICES = "PRICES";
    public static final String ID = "ID";
    public static final String USER = "users";
    public static final String WRITING = "WRITING";
    public static final String Exercise = "Exercise";
    public static final String VOCABULARY = "VOCABULARY";
    public static final String COURSE_PAYMENTS = "PAYMENT";
    public static final String EURO_SYMBOL = "€";

    public static final String PAYMENTS = "PAYMENTS";
    public static final String exerciseCount = "exerciseCount";
    public static final String URDU = "URDU";
    public static final String HINDI = "HINDI";
    public static final String ARABIC = "ARABIC";
    public static final String Speaking = "Speaking";
    public static final String CONTENT = "CONTENT";
    public static final String EXERCISE = "EXERCISE";
    public static final String PASS = "PASS";
    public static final String LEVEL = "LEVEL";
    public static final String exercise = "exercise";
    public static final String TRIAL_QUESTIONS = "TRIAL_QUESTIONS";
    public static final String EXERCISE_LIST = "EXERCISE_LIST";
    public static final String PASS_CONTENT = "PASS_CONTENT";
    public static final String PASS_EXERCISE = "PASS_EXERCISE";

    public static String getFormattedDate(long date) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date);
    }

    public static void initDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
    }

    public static void showDialog() {
        dialog.show();
    }

    public static void dismissDialog() {
        dialog.dismiss();
    }

    public static String extractFileName(String url) {
        try {
            Uri uri = Uri.parse(url);
            String path = uri.getLastPathSegment();
            int lastSlashIndex = path.lastIndexOf('/');
            return (lastSlashIndex != -1) ? path.substring(lastSlashIndex + 1) : path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public static void checkApp(Activity activity) {
        String appName = "sprachelernenAdmin";

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
                    if ((input = in != null ? in.readLine() : null) == null) break;
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

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("PharmacyApp");
        db.keepSynced(true);
        return db;
    }

    public static StorageReference storageReference() {
        return FirebaseStorage.getInstance().getReference().child("PharmacyApp");
    }

    public static String getLang() {
        return Stash.getString(Constants.SELECT, Constants.HINDI);
    }
}

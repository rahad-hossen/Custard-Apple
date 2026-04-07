package com.rahadtec.custardapple;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.OutputStream;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;

public class HomeScreen extends AppCompatActivity {


    MaterialCardView card_add_data, card_download;
    TextView usernameDisplay,tv_balance;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);

        card_add_data = findViewById(R.id.card_add_data);
        usernameDisplay = findViewById(R.id.usernameDisplay);
        tv_balance = findViewById(R.id.tv_balance);
        card_download = findViewById(R.id.card_download);
        dbHelper = new DatabaseHelper(this);

        card_add_data.setOnClickListener(v -> {
            startActivity(new Intent(HomeScreen.this, InsertData.class));
        });


        card_download.setOnClickListener(v -> {

//            exportToDownloads("rahad");
            showFileNameDialog();

        });


        PrefaranceManager manager = new PrefaranceManager(HomeScreen.this);
        String username = manager.getUsername();
        if (!username.isEmpty()){
            usernameDisplay.setText(username);
        }else {
            Toast.makeText(HomeScreen.this, "Please re-enter.",Toast.LENGTH_LONG).show();
            finishAffinity();
        }

        updateDashboard();

    }




    private void showFileNameDialog() {
        // ১. একটি এডিট টেক্সট তৈরি করা যেখানে ইউজার নাম লিখবে
        final EditText input = new EditText(this);
        input.setHint("Enter file name only");
        input.setPadding(50, 40, 50, 40); // একটু প্যাডিং দিলে দেখতে সুন্দর লাগে

        // ২. অ্যালার্ট ডায়ালগ তৈরি
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download Excel")
                .setMessage("Please enter a name for your excel file:")
                .setView(input) // ডায়ালগের ভেতর এডিট টেক্সট সেট করা
                .setPositiveButton("Download", (dialog, which) -> {
                    String fileName = input.getText().toString().trim();
                    if (!fileName.isEmpty()) {
                        // নাম খালি না থাকলে ডাউনলোড শুরু হবে
                        exportToDownloads(fileName);
                    } else {
                        Toast.makeText(this, "Filename cannot be empty!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void updateDashboard() {
        // ডাটাবেজ থেকে মোট সংখ্যা নিয়ে আসা
        int total = dbHelper.getTotalCount();

        // টেক্সট ভিউতে সেট করা
        tv_balance.setText(total + " PIS");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
    }


    public void exportToDownloads(String file_Name) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getAllData();

        // ১. এক্সেল ফাইল তৈরি করা (মেমোরিতে)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Custard Apple Data");

        // হেডার তৈরি
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Username");
        headerRow.createCell(1).setCellValue("Password");
        headerRow.createCell(2).setCellValue("Secret Key");

        // ডাটাবেজ থেকে ডেটা বসানো
        int rowNum = 1;
        while (cursor.moveToNext()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(cursor.getString(1)); // Username
            row.createCell(1).setCellValue(cursor.getString(2)); // Password
            row.createCell(2).setCellValue(cursor.getString(3)); // Secret Key
        }
        cursor.close();

        // ২. MediaStore ব্যবহার করে Downloads ফোল্ডারে ফাইল রাইট করা
        String fileName = file_Name + ".xlsx";
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // অ্যান্ড্রয়েড ১০ বা তার উপরের জন্য
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
        }

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        }else {
            // অ্যান্ড্রয়েড ৯ বা তার নিচের জন্য
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadDir, fileName);
            uri = Uri.fromFile(file);

            // মনে রাখবেন: পুরনো ভার্সনের জন্য মেনিফেস্টে WRITE_EXTERNAL_STORAGE পারমিশন লাগবে
        }

        try {
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                workbook.write(outputStream);
                outputStream.close();
                workbook.close();
                Toast.makeText(this, "File saved in Downloads ✅", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something Wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.OutputStream;

public class Cookies_main extends AppCompatActivity {

    TextView tvTotalBalance;
    CardView cardAdd, cardDownload, cardReset;
    Cookies_DB_helper dbHelper;
    LinearLayout addBtn,downloadBtn, resetBtn;
    TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookies_main);

        // ১. ইনিশিয়ালাইজ
        dbHelper = new Cookies_DB_helper(this);
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        cardAdd = findViewById(R.id.cardAdd);
        cardDownload = findViewById(R.id.cardDownload);
        cardReset = findViewById(R.id.cardReset);
        addBtn = findViewById(R.id.addBtn);
        downloadBtn = findViewById(R.id.downloadBtn);
        resetBtn = findViewById(R.id.resetbtn);
        tvUsername = findViewById(R.id.tvUsername);

        // ২. ডাটাবেস থেকে ব্যালেন্স (টোটাল কাউন্ট) আপডেট করা
        updateBalance();

        PrefaranceManager manager = new PrefaranceManager(Cookies_main.this);
        String username = manager.getUsername();
        if (!username.isEmpty()){
            tvUsername.setText(username);
        }else {
            Toast.makeText(Cookies_main.this, "Please re-enter.",Toast.LENGTH_LONG).show();
            finishAffinity();
        }

        // ৩. Add বাটনে ক্লিক করলে ডাটা এন্ট্রি স্ক্রিনে যাবে
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Cookies_main.this, InstraCookies.class);
            startActivity(intent);
        });

        // ৪. Download বাটন (ট্যাম্পোরারি টোস্ট)
        downloadBtn.setOnClickListener(v -> {

            showFileNameDialog();

        });

        // ৫. Reset বাটনে ক্লিক করলে সব ডাটা ডিলিট হবে
        resetBtn.setOnClickListener(v -> {

            showResetConfirmationDialog();

        });
    }

    // স্ক্রিনে ফিরে আসলে যেন অটোমেটিক ব্যালেন্স আপডেট হয়
    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
    }

    private void showResetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning")
                .setMessage("Are you sure you want to delete all data?")
                .setIcon(android.R.drawable.ic_dialog_alert) // একটি ওয়ার্নিং আইকন
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteAllData();
                    updateBalance(); // ব্যালেন্স জিরো করার জন্য
                    Toast.makeText(this, "All Data Deleted!", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // কিছুই হবে না, শুধু ডায়ালগ বন্ধ হবে
                    dialog.dismiss();
                })
                .show();
    }
    private void updateBalance() {
        int count = dbHelper.getTotalCount();
        tvTotalBalance.setText(count + " PIS");
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

    public void exportToDownloads(String file_Name) {
        Cookies_DB_helper dbHelper = new Cookies_DB_helper(this);
        Cursor cursor = dbHelper.getAllData();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Custard Apple Data");

        // --- ফন্ট এবং স্টাইল সেটিংস শুরু ---
        // ১. নরমাল ফন্ট তৈরি (সাইজ ১০)
        org.apache.poi.ss.usermodel.Font normalFont = workbook.createFont();
        normalFont.setFontHeightInPoints((short) 10); // ফন্ট সাইজ ১০ সেট করা হলো
        normalFont.setBold(false); // বোল্ড হবে না, একদম নরমাল থাকবে
        normalFont.setFontName("Arial");

        // ২. স্টাইল তৈরি
        org.apache.poi.ss.usermodel.CellStyle normalStyle = workbook.createCellStyle();
        normalStyle.setFont(normalFont);
        // --- ফন্ট এবং স্টাইল সেটিংস শেষ ---

        // হেডার তৈরি
        Row headerRow = sheet.createRow(0);
        String[] headers = {"username", "password", "cookies"};

        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(normalStyle); // হেডারেও সাইজ ১০ সেট করা হলো
        }

        // ডাটাবেজ থেকে ডেটা বসানো
        int rowNum = 1;
        while (cursor.moveToNext()) {
            Row row = sheet.createRow(rowNum++);

            for (int i = 0; i < 3; i++) {
                org.apache.poi.ss.usermodel.Cell cell = row.createCell(i);
                cell.setCellValue(cursor.getString(i + 1));
                cell.setCellStyle(normalStyle); // প্রত্যেকটি ডাটা সেলে সাইজ ১০ সেট করা হলো
            }
        }
        cursor.close();

        sheet.setDefaultColumnWidth(12);
        sheet.setDefaultRowHeightInPoints(15);

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
package com.example.project_test6;

import android.os.Build;
import android.text.format.Time;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
//Constructor for transaction
@IgnoreExtraProperties
public class Transaction {
    private Date timestamp;
    private String type;
    private String category;
    private  double amount;

    public Transaction(){
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }

    public Transaction (String type, String category, int amount){
        this.timestamp = new Date();
        this.type = type;
        this.category = category;
        this.amount = amount;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Transaction (Date timestamp, String type, String category, double amount){
        this.timestamp = timestamp;
        this.type = type;
        this.category = category;
        this.amount = amount;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTimestamp() {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd");
        String formatted_date = ft.format(timestamp);
        return formatted_date;
    }

    public String getType() {
        return type;
    }

    public String  getCategory() {
        return category;
    }

    public String  getAmount() { return String.valueOf((amount)); }
}

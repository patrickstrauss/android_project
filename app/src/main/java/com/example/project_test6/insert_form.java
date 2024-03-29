package com.example.project_test6;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_test6.ui.history.HistoryFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class insert_form extends Activity {

    // Variables for display and inputs
    private Spinner typeList, category;
    private Button btn_Add;
    private EditText amount;
    private String Food = "Food";
    private String Car = "Car";
    private String Health = "Health";
    private String Income = "Income";
    private String Expense = "Expense";

    //Database variables
    Context context = this;
    String TAG = "InsertForm";
    private FirebaseAuth mAuth;
    private DatabaseReference dbRoot;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private String uid;
    DatabaseReference savingRef;

    //Variables used for calculations
    double fixed_dedic_spend;
    double fixed_dedic_save;
    double dedic_to_spend;
    double dedic_to_saving;
    double buffer;
    double current_saving;
    double avgSaving;
    double accBalance;
    boolean isAllGood = true;
    ArrayList<Double> savings = new ArrayList<Double>();
    ArrayList<Saving> savingList = new ArrayList<Saving>();

    public insert_form() {
        // Default constructor required for calls to DataSnapshot.getValue(insert_form.class)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_form);

        //Initialize variables for database
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        dbRoot = FirebaseDatabase.getInstance().getReference();
        userRef = firebaseDatabase.getReference().child("users").child(uid);
        savingRef = firebaseDatabase.getReference().child("users").child(uid).child("savings");

        // Read from the database
        savingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBitch : dataSnapshot.getChildren()) {
                    Map map = (Map) dsBitch.getValue();
                    String str_amount = String.valueOf(map.get("amount"));
                    String str_isSaved = String.valueOf(map.get("isSaved"));
                    String str_timestamp = String.valueOf(map.get("timestamp"));
                    Double amount = Double.parseDouble(str_amount);

                    //Check values
                    Log.e(TAG, str_amount);
                    Log.e(TAG, str_isSaved);
                    Log.e(TAG, str_timestamp);

                    //Putting savings object created from the database in a list
                    savingList.add(new Saving(str_timestamp, amount, false));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Read from the database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map map = (Map) dataSnapshot.getValue();
//                String value = dataSnapshot.getValue(String.class);
                accBalance = Double.parseDouble(String.valueOf(map.get("balance")));
                fixed_dedic_save = Double.parseDouble(String.valueOf(map.get("saving_goal")));
                fixed_dedic_spend = Double.parseDouble(String.valueOf(map.get("daily_budget")));
                fixed_dedic_spend -= fixed_dedic_save;
                dedic_to_saving = Double.parseDouble(String.valueOf(map.get("saving_remain")));
                current_saving = Double.parseDouble(String.valueOf(map.get("total_saving")));
                buffer = Double.parseDouble(String.valueOf(map.get("buffer")));
                dedic_to_spend = Double.parseDouble(String.valueOf(map.get("daily_budget_remain")));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Read from the database
        savingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBitch : dataSnapshot.getChildren()) {
                    Map map = (Map) dsBitch.getValue();
                    String amount = String.valueOf(map.get("amount"));
                    Double am = Double.parseDouble(amount);

                    //Check values
                    Log.e(TAG, amount);

                    //Add the saving amount to list
                    savings.add(am);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        addItemsOnSpinner();
        addItemsOnSpinner2();
        addListenerOnButton();

    }

    public void addItemsOnSpinner() {
        typeList = (Spinner) findViewById(R.id.typeList);
        List<String> list = new ArrayList<String>();
        list.add(Expense);
        list.add(Income);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeList.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinner2() {
        category = (Spinner) findViewById(R.id.category);
        List<String> list = new ArrayList<String>();
        list.add(Food);
        list.add(Car);
        list.add(Health);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(dataAdapter);
    }


    // get the selected dropdown list value
    public void addListenerOnButton() {

        //Initiate variables used for creating transaction object
        typeList = (Spinner) findViewById(R.id.typeList);
        category = (Spinner) findViewById(R.id.category);
        amount = (EditText) findViewById(R.id.amount);
        btn_Add = (Button) findViewById(R.id.btnAdd);

        if (user != null) {

            //Add onclick listener to button
            btn_Add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String ip_type = typeList.getSelectedItem().toString();
                    String ip_category = category.getSelectedItem().toString();
                    String str_amount = amount.getText().toString();

                    //Make sure that the user fill in the amount of their transaction
                    if (!str_amount.isEmpty()) {
                        int ip_amount = Integer.parseInt(amount.getText().toString());

                        //Make a new transaction object
                        Transaction newTransaction = new Transaction(ip_type, ip_category, ip_amount);

                        //Call the transaction calculation method
                        calTransac(typeList.getSelectedItem().toString(), ip_amount);

                        //Making sure that the user have enough capital to make the transaction
                        if (isAllGood) {
                            dbRoot.child("users").child(uid).child("Transactions").push().setValue(newTransaction);
                            Toast.makeText(context, "Added!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        savings.add(dedic_to_saving);

                        startActivity(new Intent(insert_form.this, MainPage.class));
                    } else {

                        Toast.makeText(context, "Please add the amount!",
                                Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }

    }

    //Method for making updates to the data based on the transaction made
    public void calTransac(String tranType, double trans) {
        if (tranType.equals("Expense")) {
            if (dedic_to_spend >= trans) {
                accBalance -= trans;
                dedic_to_spend -= trans;
            } else if ((dedic_to_spend + dedic_to_saving) >= trans) {
                accBalance -= trans;
                dedic_to_saving = (dedic_to_saving + dedic_to_spend) - trans;
                dedic_to_spend = 0;
            } else if ((dedic_to_spend + dedic_to_saving + buffer) >= trans) {
                accBalance -= trans;
                buffer = (dedic_to_saving + dedic_to_spend + buffer) - trans;
                dedic_to_spend = 0;
                dedic_to_saving = 0;
            } else if ((dedic_to_spend + dedic_to_saving + buffer + accBalance) >= trans) {
                accBalance = (accBalance + dedic_to_spend + dedic_to_saving + buffer) - trans;
                dedic_to_spend = 0;
                dedic_to_saving = 0;
                buffer = 0;
            } else {
                isAllGood = false;
                Toast.makeText(context, "You do not have enough money!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            accBalance += trans;
        }

        //Update the database after the calculation processes
        updateAccBalance(accBalance);
        updateBudgetLeft(dedic_to_spend);
        updateSaving(dedic_to_saving);
        updateBuffer(buffer);

    }

    //Calculation for end of month, not tested
    public void refreshBuffer() {
        //update buffer
        current_saving += buffer;

        //cal avg
        double avgBuf = buffer /= 30;
        avgSaving = avgBuf;
        buffer = 0;
        updateDailySaving(avgSaving);
        updateBuffer(buffer);
    }

    public void updateAccBalance(double delta) {
        // Write a message to the database
        userRef.child("balance").setValue(delta);
    }

    public void updateSavingList(ArrayList<Saving> delta) {
        userRef.child("savingList").setValue(delta);
    }

    public void updateBudgetLeft(double delta) {
        //Update them budget
        userRef.child("daily_budget_remain").setValue(delta);

    }

    public void updateSaving(double delta) {
        //Update them saving
        userRef.child("saving_remain").setValue(delta);
    }

    public void updateDailySaving(double delta) {
        //update daily saving average
        userRef.child("avgSaving").setValue(delta);
    }

    public void updateBuffer(double delta) {
        //update buffer
        userRef.child("buffer").setValue(delta);
    }

    public void updateTotalSaving(double delta) {
        //update buffer
        userRef.child("total_saving").setValue(delta);
    }

}

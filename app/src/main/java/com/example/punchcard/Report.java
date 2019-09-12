package com.example.punchcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Report extends AppCompatActivity {

    private TextView date_textview;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Dialog paymentDialog;
    Button back_button;
    Button pay_button;
    Button detail_button;
    Button okay_button;
    Button date_button;
    Long total_hours;
    Long total_minutes;
    Long total_overtime;
    Long remainder_minutes = 0L;
    TextView report_textview;
    TextView date_payment_textview;
    TextView total_payment_textview;
    TextView total_time_textview;
    TextView since_textview;
    TextView real_value_textview;
    String timestamp_string;
    String payment_date;
    String payment_date_display;
    String overtime_value_package;
    String since_date_package;
    String firebase_date;
    String pay_period_date;
    boolean alreadPicked;
    Boolean admin;
    int sel_year;
    int sel_month;
    int sel_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        paymentDialog = new Dialog(this);
        pay_button = findViewById(R.id.pay_button);
        back_button = findViewById(R.id.back_admin_button);
        date_button = findViewById(R.id.date_button);
        detail_button = findViewById(R.id.detail_button);
        report_textview = findViewById(R.id.report_textview);
        //amount_textview = findViewById(R.id.amount_textview);
        since_textview = findViewById(R.id.since_textview);
        date_textview = findViewById(R.id.date_textview);
        real_value_textview = findViewById(R.id.real_value_textview);

        pay_button.setVisibility(View.INVISIBLE);
        admin = getIntent().getExtras().getBoolean("admin");

        SimpleDateFormat fb_sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

        Calendar proto_cal = Calendar.getInstance();
        Log.d("arjun", "Calendar Proto Startup: " + proto_cal.getTime().toString());
        String fb_current_date = fb_sdf.format(proto_cal.getTime());
        calculateTotalOvertime(fb_current_date);

        proto_cal.add(java.util.Calendar.DATE, -1);

        fb_current_date = fb_sdf.format(proto_cal.getTime());
        firebase_date = fb_current_date;
        pay_period_date = sdf.format(proto_cal.getTime());


        Log.d("arjun", "Calendar Proto Startup Minus 1 Day: " + proto_cal.getTime().toString());




        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                alreadPicked = true;
                sel_year = year;
                sel_month = month;
                sel_day = day;

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                SimpleDateFormat fb_sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

                Calendar date_cal = Calendar.getInstance();
                date_cal.set(year, month, day);
                Log.d("arjun", "date_cal is equal to " + date_cal.toString());

                firebase_date = fb_sdf.format(date_cal.getTime());
                String cur_date = sdf.format(date_cal.getTime());

                Log.d("arjun", "onDateSet Firebase date: " + firebase_date);
                Log.d("arjun", "onDateSet Display date: " + cur_date);


                date_cal.set(year, month, day-1);
                pay_period_date =  sdf.format(date_cal.getTime());
                date_textview.setText(pay_period_date);

                calculateTotalOvertime(firebase_date);
            }
        };

        date_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                if(alreadPicked){
                    year = sel_year;
                    month = sel_month;
                    day = sel_day;
                }

                DatePickerDialog dialog = new DatePickerDialog(Report.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDateSetListener,year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        back_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.d("arjun","Back Button was clicked");
                goHome();
            }
        });

        pay_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.d("arjun","Submit Pay Button was clicked");
                Map<String, Object> payment = new HashMap<>();
                payment.put("date", firebase_date);
                payment.put("timestamp", FieldValue.serverTimestamp());
                payment.put("overtime_amount", overtime_value_package);
                payment.put("total_overtime", total_overtime);

                db.collection("payments")
                        .add(payment)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("arjun", "DocumentSnapshot written with ID: " + documentReference.getId());
                                ShowPaymentSuccessPopup();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("arjun", "Error adding document", e);
                            }
                        });
            }
        });

        detail_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.d("arjun","Detail Button was clicked");
                goDetail();
            }
        });
    }

    public void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("date", "");
        intent.putExtra("start", "");
        intent.putExtra("end", "");
        startActivity(intent);
    }

    public void goPayment() {

    }

    public void goDetail() {
        Intent intent = new Intent(this, DetailViewer.class);
        intent.putExtra("admin",admin);
        startActivity(intent);
    }

    public void checkAdmin() {
        try{
            admin = getIntent().getExtras().getBoolean("admin");

            if(admin){
                pay_button.setVisibility(View.VISIBLE);
            } else {
                pay_button.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void calculateTotalOvertime(final String closing_date){

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        pay_button.setVisibility(View.INVISIBLE);

        try {
            Query query = db.collection("payments")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(1);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("arjun", document.getId() + " => " + document.getData());
                            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            DateFormat fb_df = new SimpleDateFormat("yyyy-MM-dd");
                            String timestamp = document.getString("date");

                            try {
                                Date pay_date = fb_df.parse(timestamp);
                                Date parsed_closing_date = fb_df.parse(closing_date);

                                timestamp_string = df.format(pay_date);
                                payment_date_display = df.format(parsed_closing_date);
                                payment_date = fb_df.format(parsed_closing_date);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                            Log.d("arjun","FINALSDASD " + timestamp_string);
                            Log.d("arjun","Set Payment Date:  " + payment_date);
                            Log.d("arjun","Set Closing Date:  " + closing_date);

                            since_date_package = timestamp_string;
                            since_textview.setText("Since " + timestamp_string);
                            date_textview.setText(payment_date_display);

                            Log.d("arjun", "Pay Date Actual " + pay_period_date);

                            db.collection("punches")
                                    .whereGreaterThanOrEqualTo("date", timestamp)
                                    .whereLessThan("date",closing_date)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                total_hours = new Long(0);
                                                total_minutes = new Long(0);
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d("arjun", document.getId() + " => " + document.getData());
                                                    Long ovrt_hours_cur = document.getLong("overtime_hours");
                                                    Long ovrt_minutes_cur = document.getLong("overtime_minutes");

                                                    total_hours = total_hours + ovrt_hours_cur;
                                                    total_minutes = total_minutes + ovrt_minutes_cur;

                                                };
                                                Log.d("arjun", "Total hours are " + total_hours);
                                                Log.d("arjun", "Total minutes are " + total_minutes);

                                                if(total_minutes > 60){
                                                    total_overtime = total_hours + (total_minutes/60);
                                                    remainder_minutes = total_minutes%60;
                                                    Log.d("arjun", "Total hours without remainder " + total_overtime);
                                                    Log.d("arjun", "Remainder minutes are " + remainder_minutes);

                                                    real_value_textview.setText(total_overtime + " hrs " + remainder_minutes + " mins");

                                                    if(remainder_minutes > 0){
                                                        total_overtime = total_overtime + 1;
                                                        remainder_minutes = 0L;
                                                    }
                                                } else {
                                                    total_overtime = total_hours + total_minutes;
                                                }
                                                Log.d("arjun", "Total hours are " + total_overtime);
                                                Log.d("arjun", "Remainder minutes are " + remainder_minutes);
                                                try {

                                                    report_textview.setText(Long.toString(total_overtime) + " hrs");
                                                } catch (java.lang.NullPointerException e){
                                                    Log.d("arjun", "NullPointerException: " + "Setting Values to Zero");
                                                    Toast.makeText(getApplicationContext(), "Error Calculating Overtime", Toast.LENGTH_SHORT).show();
                                                    report_textview.setText("0 hrs");
                                                }

                                                Long overtime_value = total_overtime * 15;
                                                overtime_value_package = overtime_value.toString();
                                            } else {
                                                Log.d("arjun", "Error getting documents: ", task.getException());
                                            }
                                            checkAdmin();
                                        }
                                    });
                            Log.d("arjun","Last Pay Date " + timestamp.toString());
                        }
                    } else {
                        Log.d("arjun", "Error getting documents: ", task.getException());
                    }
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ShowPaymentSuccessPopup(){
        paymentDialog.setContentView(R.layout.payment_popup_positive);

        date_payment_textview = paymentDialog.findViewById(R.id.date_payment_textview);
        total_payment_textview = paymentDialog.findViewById(R.id.total_payment_textview);
        total_time_textview = paymentDialog.findViewById(R.id.total_time_textview);
        okay_button = paymentDialog.findViewById(R.id.okay_button);

        date_payment_textview.setText("Date: " + since_date_package + " to " + pay_period_date);
        total_payment_textview.setText("Total Pay Out: RM " + overtime_value_package);
        total_time_textview.setText("Total Overtime: " + total_overtime.toString() + " hrs");

        okay_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goHome();
                paymentDialog.dismiss();
            }
        });

        paymentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        paymentDialog.show();
    }


}

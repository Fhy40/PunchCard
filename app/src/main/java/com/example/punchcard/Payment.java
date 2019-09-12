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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Payment extends AppCompatActivity {

    private static final String TAG = "Payment";

    Dialog paymentDialog;
    private TextView date_textview;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String firebase_date;
    String pay_period_date;
    Button submit_pay_button;
    Button back_button;
    Button date_button;
    Button okay_button;
    TextView date_payment_textview;
    TextView total_payment_textview;
    TextView total_time_textview;
    int sel_year;
    int sel_month;
    int sel_day;
    boolean alreadPicked;
    String since_date_package;
    String overtime_value_package;
    Long total_overtime_package;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentDialog = new Dialog(this);
        date_textview = (TextView) findViewById(R.id.date_textview);
        submit_pay_button = findViewById(R.id.submit_pay_button);
        back_button = findViewById(R.id.back_admin_button);
        date_button = findViewById(R.id.date_button);

        total_overtime_package = getIntent().getExtras().getLong("total_overtime_package");
        overtime_value_package = getIntent().getExtras().getString("overtime_value_package");
        since_date_package = getIntent().getExtras().getString("since_date_package");

        Log.d("arjun","OverTime Amount Calculated: " + overtime_value_package );
        Log.d("arjun","Total Overtime Calculated: " + total_overtime_package );

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

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

                DatePickerDialog dialog = new DatePickerDialog(Payment.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDateSetListener,year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                alreadPicked = true;
                sel_year = year;
                sel_month = month;
                sel_day = day;

                submit_pay_button.setVisibility(View.VISIBLE);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                SimpleDateFormat fb_sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

                Calendar date_cal = Calendar.getInstance();
                date_cal.set(year, month, day);
                Log.d("arjun", "date_cal is equal to " + date_cal.toString());

                firebase_date = fb_sdf.format(date_cal.getTime());
                String cur_date = sdf.format(date_cal.getTime());

                Log.d("arjun", "onDateSet Firebase date: " + firebase_date);
                Log.d("arjun", "onDateSet Display date: " + cur_date);
                date_textview.setText(cur_date);

                date_cal.set(year, month, day-1);
                pay_period_date =  sdf.format(date_cal.getTime());
            }
        };

        submit_pay_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("arjun","Submit Pay Button was clicked");
                Map<String, Object> payment = new HashMap<>();
                payment.put("date", firebase_date);
                payment.put("timestamp", FieldValue.serverTimestamp());
                payment.put("overtime_amount", overtime_value_package);
                payment.put("total_overtime", total_overtime_package);

                db.collection("payments")
                        .add(payment)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                ShowPaymentSuccessPopup();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }


        });

        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goReportAdmin();
            }
        });
    }

    public void goReportAdmin(){
        Intent intent = new Intent(this, Report.class);
        intent.putExtra("admin", true);
        startActivity(intent);
    }

    public void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("date", "");
        intent.putExtra("start", "");
        intent.putExtra("end", "");
        startActivity(intent);
    }

    public void ShowPaymentSuccessPopup(){
        paymentDialog.setContentView(R.layout.payment_popup_positive);

        date_payment_textview = paymentDialog.findViewById(R.id.date_payment_textview);
        total_payment_textview = paymentDialog.findViewById(R.id.total_payment_textview);
        total_time_textview = paymentDialog.findViewById(R.id.total_time_textview);
        okay_button = paymentDialog.findViewById(R.id.okay_button);

        date_payment_textview.setText("Date: " + since_date_package + " to " + pay_period_date);
        total_payment_textview.setText("Total Pay Out: RM " + overtime_value_package);
        total_time_textview.setText("Total Overtime: " + total_overtime_package.toString() + " hrs");

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

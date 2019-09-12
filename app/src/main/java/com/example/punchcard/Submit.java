package com.example.punchcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Submit extends AppCompatActivity {

    Button submit_button;
    Button edit_button;
    TextView date_input;
    TextView start_input;
    TextView end_input;
    TextView overtime_input;
    String start_time_package;
    String end_time_package;
    String date_package;
    String display_date_string;
    boolean azeroth;
    long overtime_hours;
    long overtime_minutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        submit_button = findViewById(R.id.submit_button);
        edit_button = findViewById(R.id.edit_button);
        date_input = findViewById(R.id.date_input);
        start_input = findViewById(R.id.start_input);
        end_input = findViewById(R.id.end_input);
        overtime_input = findViewById(R.id.overtime_input);


        date_package = getIntent().getExtras().getString("date");
        start_input.setText(getIntent().getExtras().getString("start"));
        end_input.setText(getIntent().getExtras().getString("end"));
        start_time_package = getIntent().getExtras().getString("s_package");
        end_time_package = getIntent().getExtras().getString("e_package");

        azeroth = getIntent().getExtras().getBoolean("azeroth");
        Log.d("arjun","Azeroth is currently " + azeroth);


        DateFormat ori_sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date display_date = ori_sdf.parse(date_package);
            display_date_string = sdf.format(display_date);
            date_input.setText(display_date_string);
        } catch (Exception e){
            e.printStackTrace();
        }





        try {
            java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm");
            Log.d("arjun", "start_time_package " + start_time_package);
            java.util.Date strt = df.parse(start_time_package);
            java.util.Date ed = df.parse(end_time_package);

            if (!azeroth) {
                long diff = ed.getTime() - strt.getTime();


                long timeInSeconds = diff / 1000;
                Log.d("arjun", "time in seconds: " + timeInSeconds);
                long hours, minutes, seconds;
                hours = timeInSeconds / 3600;
                Log.d("arjun", "Hours Pre: " + hours);
                timeInSeconds = timeInSeconds - (hours * 3600);
                minutes = timeInSeconds / 60;
                Log.d("arjun", "Minutes: " + minutes);

                hours = hours - 9;
                Log.d("arjun", "Hours Post: " + hours);

                String diffTime = (hours < 10 ? "0" + hours : hours) + " hrs " + (minutes < 10 ? "0" + minutes : minutes) + " mins";

                if (hours < 0) {
                    hours = 0;
                    minutes = 0;
                    diffTime = "0 hours and 0 minutes";
                }

                overtime_hours = hours;
                overtime_minutes = minutes;

                overtime_input.setText(diffTime);
            } else {
                long diff = ed.getTime() - strt.getTime();
                Log.d("arjun", "Pre 24 time diff " + diff);
                diff = 86400000 + diff;
                Log.d("arjun", "Time Difference Added to 24 hours " + diff);

                long timeInSeconds = diff / 1000;
                Log.d("arjun", "time in seconds: " + timeInSeconds);
                long hours, minutes, seconds;
                hours = timeInSeconds / 3600;
                Log.d("arjun", "Hours Pre: " + hours);
                timeInSeconds = timeInSeconds - (hours * 3600);
                minutes = timeInSeconds / 60;
                Log.d("arjun", "Minutes: " + minutes);

                hours = hours - 9;
                Log.d("arjun", "Hours Post: " + hours);

                String diffTime = (hours < 10 ? "0" + hours : hours) + " hrs " + (minutes < 10 ? "0" + minutes : minutes) + " mins";

                if (hours < 0) {
                    hours = 0;
                    minutes = 0;
                    diffTime = "0 hours and 0 minutes";
                }

                overtime_hours = hours;
                overtime_minutes = minutes;

                overtime_input.setText(diffTime);
            }

            } catch(java.text.ParseException e){
                e.printStackTrace();
            }




        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        submit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("arjun","Submit Button was clicked");
                Map<String, Object> punch = new HashMap<>();
                punch.put("date", dateConvert(date_package));
                punch.put("start_time", start_time_package);
                punch.put("end_time", end_time_package);
                punch.put("overtime_hours", overtime_hours);
                punch.put("overtime_minutes", overtime_minutes);
                punch.put("timestamp", FieldValue.serverTimestamp());

                db.collection("punches")
                        .add(punch)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("arjun", "DocumentSnapshot written with ID: " + documentReference.getId());

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("arjun", "Error adding document", e);
                            }
                        });
                goSend();
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("arjun","Edit Button Clicked");
                goEdit();
            }

        });

    }

    public String dateConvert(String proto_date) {
        Log.d("arjun", "PROTO DATE " + proto_date);
        DateFormat df = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);
        try {
            Date temp_proto_date = df.parse(proto_date);
            DateFormat db_df = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
            proto_date = db_df.format(temp_proto_date);
            Log.d("arjun", "DateCovert: " + proto_date);
            return proto_date;


        } catch (Exception e){
            Log.d("arjun", "DateCovert: Error parsing date " + proto_date);
            e.printStackTrace();
        }
        return proto_date;

    }

    public void goEdit() {
        Log.d("arjun","Opening Main Activity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("date", date_package);
        intent.putExtra("start", start_input.getText().toString());
        intent.putExtra("end", end_input.getText().toString());
        intent.putExtra("s_package", start_time_package);
        intent.putExtra("e_package", end_time_package);
        intent.putExtra("azeroth", azeroth);
        startActivity(intent);
    }

    public void goSend(){
        Log.d("arjun","Opening Main Activity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("date", "");
        intent.putExtra("start", "");
        intent.putExtra("end", "");
        startActivity(intent);
    }

}

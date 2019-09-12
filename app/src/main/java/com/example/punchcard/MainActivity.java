package com.example.punchcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Button continue_button;
    Button start_button;
    Button end_button;
    Button admin_button;
    Button report_button;
    Button power_button;
    EditText start_input;
    EditText end_input;
    EditText date_input;
    CheckBox azeroth_checkbox;
    TextView title_textview;
    TextView azeroth_textview;
    String start_time;
    String end_time;
    String date;

    String start_time_package;
    String end_time_package;
    String date_package;
    Boolean swtc;
    boolean azeroth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);

        continue_button = findViewById(R.id.continue_button);
        power_button = findViewById(R.id.power_button);
        date_input = findViewById(R.id.date_input);

        start_input = findViewById(R.id.start_input);
        end_input = findViewById(R.id.end_input);
        title_textview = findViewById(R.id.title_textview);
        azeroth_textview = findViewById(R.id.azeroth_textview);
        start_button = findViewById(R.id.start_button);
        end_button = findViewById(R.id.end_button);
        admin_button = findViewById(R.id.admin_button);
        report_button = findViewById(R.id.report_button);
        azeroth_checkbox = findViewById(R.id.azeroth_checkbox);
        swtc = true;



        try{
            date_input.setText(getIntent().getExtras().getString("date"));
            start_input.setText(getIntent().getExtras().getString("start"));
            end_input.setText(getIntent().getExtras().getString("end"));
            start_time_package = getIntent().getExtras().getString("s_package");
            end_time_package = getIntent().getExtras().getString("e_package");
            azeroth = getIntent().getExtras().getBoolean("azeroth");
            azeroth_checkbox.setChecked(azeroth);

        } catch (Exception e){

        }

        azeroth_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    azeroth_textview.setVisibility(View.VISIBLE);
                } else {
                    azeroth_textview.setVisibility(View.INVISIBLE);
                }
                }
            });


        ((EditText)findViewById(R.id.date_input)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("arjun","ON FOCUS CHANGED");
                if (!hasFocus) {
                    String proto_date = date_input.getText().toString();
                    if(proto_date.length() < 10) {
                        try {
                            Log.d("arjun","EDIT TEXT IS UNFOCUSED");
                            proto_date = new StringBuilder(proto_date).insert(proto_date.length() - 4, "/").toString();
                            proto_date = new StringBuilder(proto_date).insert(proto_date.length() - 7, "/").toString();

                            date_input.setText(proto_date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });





        continue_button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                date = date_input.getText().toString();

                goSubmit();

            }
        });

        start_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                swtc = true;
                date_input.clearFocus();
                DialogFragment startPicker = new TimePickerFragment();
                startPicker.show(getSupportFragmentManager(), "time picker");
                Log.d("arjun","SWITCH IS CURRENTLY " + swtc);

            }
        });

        end_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                swtc = false;
                date_input.clearFocus();
                DialogFragment startPicker = new TimePickerFragment();
                startPicker.show(getSupportFragmentManager(), "time picker");
                Log.d("arjun","SWITCH IS CURRENTLY " + swtc);
            }
        });

        admin_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                goReportAdmin();
            }
        });

        report_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                goReport();
            }
        });

        power_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.exit(0);
            }
        });



    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Log.d("arjun","SWITCH IS IN TIME SET " + swtc);
        String str_hourOfDay;
        String str_minute;
        boolean pm;

        if(hourOfDay > 12){
            pm = true;
        } else {
            pm = false;
        }

        if(hourOfDay < 10){
            str_hourOfDay = "0" + hourOfDay;
        }else {
            str_hourOfDay = Integer.toString(hourOfDay);
        }

        if(minute < 10){
            str_minute = "0" + minute;
        } else{
            str_minute = Integer.toString(minute);
        }

        if(swtc){
            Log.d("arjun","Currently Running Time Picker for Start Time");
            if(pm){
                start_time = str_hourOfDay + ":" + str_minute + " PM";
            } else {
                start_time = str_hourOfDay + ":" + str_minute + " AM";
            }

            start_input.setText(start_time);
            start_time_package = str_hourOfDay + ":" + str_minute;
        } else if(swtc == false) {
            Log.d("arjun","Currently Running Time Picker for End Time");
            end_time_package = str_hourOfDay + ":" + str_minute;
            if(pm){
                int int_hold = Integer.parseInt(str_hourOfDay) - 12;
                Log.d("arjun","Int Holder " + int_hold);
                if(int_hold < 10){
                    str_hourOfDay = "0" + Integer.toString(int_hold);
                }else {
                    str_hourOfDay = Integer.toString(int_hold);
                }

                end_time = str_hourOfDay + ":" + str_minute + " PM";
            } else {
                end_time = str_hourOfDay + ":" + str_minute + " AM";
            }
            end_input.setText(end_time);

        }


    }

    public void goSubmit() {
        if(checkField() && checkDate() && checkTime()) {
            Intent intent = new Intent(this, Submit.class);
            intent.putExtra("date", date_input.getText().toString());
            intent.putExtra("start", start_input.getText().toString());
            intent.putExtra("end", end_input.getText().toString());
            intent.putExtra("s_package", start_time_package);
            intent.putExtra("e_package", end_time_package);

            if(azeroth_checkbox.isChecked()){
                azeroth = true;
            } else {
                azeroth = false;
            }

            intent.putExtra("azeroth", azeroth);
            startActivity(intent);
        }
    }

    public void goReport(){
        Intent intent = new Intent(this, Report.class);
        intent.putExtra("admin", false);
        startActivity(intent);
    }

    public void goReportAdmin(){
        Intent intent = new Intent(this, Report.class);
        intent.putExtra("admin", true);
        startActivity(intent);
    }

    public boolean checkField(){
        String date_current = date_input.getText().toString();
        String start_current = start_input.getText().toString();
        String end_current = end_input.getText().toString();

        if (date_current.matches("") || start_current.matches("") || end_current.matches("") ) {
            Toast.makeText(this, "Missing Input", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public boolean checkTime(){
        java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm");
        Log.d("arjun", "start_time_package= " + start_time_package);
        try {
            java.util.Date strt = df.parse(start_time_package);
            java.util.Date ed = df.parse(end_time_package);

            long diff = ed.getTime() - strt.getTime();

            if(diff < 0 && !azeroth_checkbox.isChecked()){
                Toast.makeText(this, "Error: This is a negative duration", Toast.LENGTH_SHORT).show();
                return false;
            } else{
                return true;
            }
        } catch(java.text.ParseException e){
            e.printStackTrace();
            return false;
        }

    }

    public boolean checkDate() {
        String cur_date = date_input.getText().toString();
        Log.d("arjun","Current Input Date is " + cur_date);

        if (cur_date == null || cur_date.length() < 10) {
            Toast.makeText(this, "Error with Length of Date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.parseInt(cur_date.substring(0,2)) < 1 || Integer.parseInt(cur_date.substring(0,2)) > 31 ) {
            Toast.makeText(this, "Error with Day", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.parseInt(cur_date.substring(3,5)) < 1 || Integer.parseInt(cur_date.substring(3,5)) > 12 ) {
            Toast.makeText(this, "Error with Month", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.parseInt(cur_date.substring(6,10)) < 2017 || Integer.parseInt(cur_date.substring(6,10)) > 2030 ) {
            Toast.makeText(this, "Error with Year", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date date = format.parse(cur_date);
            Log.d("arjun","Current Date is " + date);
            return true;

        } catch (java.text.ParseException e) {
            Toast.makeText(this, "Date Entered Incorrectly", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }




}

package com.example.punchcard.Detail    ;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Detail {

    private int id;
    private String date;
    private int ovrt_hrs;
    private int ovrt_mins;
    private String start_time;
    private String end_time;
    public static int increment = 1;

    public Detail(){

    }

    public Detail(int id, String date, int ovrt_hrs, int ovrt_mins, String start_time, String end_time){
        this.id = id;
        this.date = date;
        this.ovrt_hrs = ovrt_hrs;
        this.ovrt_mins = ovrt_mins;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOvrt_hrs() {
        return ovrt_hrs;
    }

    public void setOvrt_hrs(int ovrt_hrs) {
        this.ovrt_hrs = ovrt_hrs;
    }

    public int getOvrt_mins() {
        return ovrt_mins;
    }

    public void setOvrt_mins(int ovrt_mins) {
        this.ovrt_mins = ovrt_mins;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public static ArrayList<Detail> getDetails() {

        ArrayList<Detail> DetailList = new ArrayList<>();
        DetailList.add(new Detail(1,"07/09/2019",4,30, "06:40", "16:40"));
        return DetailList;
    }

    public static ArrayList<Detail> getFirebaseDetails() {

        final ArrayList<Detail> DetailList = new ArrayList<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        return DetailList;
    }
}

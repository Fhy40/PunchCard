package com.example.punchcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.punchcard.Detail.Detail;
import com.example.punchcard.Detail.DetailAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DetailViewer extends AppCompatActivity implements DetailAdapter.OnDetailListener {

    private List<Detail> DetailList = Detail.getFirebaseDetails();
    private RecyclerView recyclerView;
    private DetailAdapter mAdapter;
    private Button back_button;
    private Button back_admin_button;
    private String degree_chosen;
    private String last_pay_date;
    Boolean admin;
    int increment = 0;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_viewer);

        back_button = (Button) findViewById(R.id.back_button);
        back_admin_button = (Button) findViewById(R.id.back_admin_button);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new DetailAdapter(DetailList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setVisibility(View.INVISIBLE);

        back_admin_button.setVisibility(View.INVISIBLE);
        back_button.setVisibility(View.INVISIBLE);

        db  = FirebaseFirestore.getInstance();

        Query query = db.collection("payments")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String last_pay_date = document.getString("date");
                        Log.d("arjun", "Last Pay Date for Viewer: " + last_pay_date);

                        db.collection("punches").whereGreaterThanOrEqualTo("date", last_pay_date).orderBy("date", Query.Direction.DESCENDING)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("arjun", "Successfully retrieved punches for Detail Viewer");
                                            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                            DateFormat fb_df = new SimpleDateFormat("yyyy-MM-dd");

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("arjun", document.getId() + " => " + document.getData());
                                                try {
                                                    increment = increment + 1;
                                                    Date proto_date = fb_df.parse(document.getString("date"));
                                                    String list_date = df.format(proto_date);
                                                    Long ovrt_hours_cur = document.getLong("overtime_hours");
                                                    Long ovrt_minutes_cur = document.getLong("overtime_minutes");
                                                    String start_time = document.getString("start_time");
                                                    String end_time = document.getString("end_time");


                                                    Log.d("arjun", "Detail Viewer Date to Add. Date: " + list_date);
                                                    DetailList.add(new Detail(increment, list_date, ovrt_hours_cur.intValue(), ovrt_minutes_cur.intValue(), start_time, end_time));

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            ;
                                            mAdapter.notifyDataSetChanged();
                                            recyclerView.setVisibility(View.VISIBLE);
                                            Log.d("arjun", "Final Detail List: " + DetailList);


                                        } else {
                                            Log.d("arjun", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }

                }




            }
        });

        checkAdmin();
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goReport();
            }
        });

        back_admin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goReportAdmin();
            }
        });
    }


    @Override
    public void onDetailClick(int position) {
        Log.d("arjun","Current Position: " + DetailList.get(position));
        Log.d("arjun","Current Degree: " + DetailList.get(position).getDate());
    }



    public void goReportAdmin(){
        Intent intent = new Intent(this, Report.class);
        intent.putExtra("admin", true);
        startActivity(intent);
    }

    public void goReport(){
        Intent intent = new Intent(this, Report.class);
        intent.putExtra("admin", false);
        startActivity(intent);
    }

    public void checkAdmin() {
        try{
            admin = getIntent().getExtras().getBoolean("admin");

            if(admin){
                back_admin_button.setVisibility(View.VISIBLE);
            } else {
                back_button.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

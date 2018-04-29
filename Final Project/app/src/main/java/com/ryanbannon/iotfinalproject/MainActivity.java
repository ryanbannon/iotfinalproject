package com.ryanbannon.iotfinalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView photosBtn;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Weight weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.weight_listView);
        photosBtn = findViewById(R.id.photoBtn);
        weight = new Weight();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReferenceFromUrl("https://iotfinalproject-cea51.firebaseio.com/weight/");
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,R.layout.weight,R.id.weight_info, list);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    weight = ds.getValue(Weight.class);
                    list.add("Date: "+ weight.getDate().toString() +"\n\n"+ "Weight: "+weight.getWeight().toString());
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        photosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PhotosActivity.class);
                startActivity(i);
            }
        });
    }
}

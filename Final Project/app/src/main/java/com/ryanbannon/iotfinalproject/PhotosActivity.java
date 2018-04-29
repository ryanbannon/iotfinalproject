package com.ryanbannon.iotfinalproject;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PhotosActivity extends AppCompatActivity {

    ListView listView;
    TextView weightBtn;
    TextView takePhoto;
    FirebaseDatabase database;
    FirebaseDatabase takePhotoDatabase;
    DatabaseReference databaseReference;
    DatabaseReference takePhotoDatabaseReference;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        listView = findViewById(R.id.photos_listView);
        weightBtn = findViewById(R.id.weightBtn);
        photo = new Photo();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReferenceFromUrl("https://iotfinalproject-cea51.firebaseio.com/photos/");
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,R.layout.photo,R.id.photos_info, list);

        takePhotoDatabase = FirebaseDatabase.getInstance();
        takePhotoDatabaseReference = takePhotoDatabase.getReferenceFromUrl("https://iotfinalproject-cea51.firebaseio.com/app_button/");
        takePhoto = findViewById(R.id.takePhotoBtn);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    photo = ds.getValue(Photo.class);
                    list.add("Timestamp: "+ photo.getTimestamp().toString() +"\n\n"+ "Photo URL: "+photo.getURL().toString());
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(photo.getURL().toString()));
                            startActivity(i);
                        }
                    });
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        weightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PhotosActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoDatabaseReference.child("button").setValue("True");
            }
        });
    }
}
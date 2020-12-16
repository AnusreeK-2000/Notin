package com.example.notin.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notin.R;
import com.example.notin.entities.UploadVideoDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ShowVideo extends AppCompatActivity {

    public RecyclerView mRecyclerView2;
    FirebaseDatabase mFirebaseDatabase2;
    DatabaseReference mref2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

        String name = getIntent().getExtras().get("name").toString();

        mRecyclerView2 = findViewById(R.id.recyclerview_ShowVideo);
        mRecyclerView2.setHasFixedSize(true);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mref2 = mFirebaseDatabase2.getReference("Videos");

    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UploadVideoDetails> options =
                new FirebaseRecyclerOptions.Builder<UploadVideoDetails>()
                        .setQuery(mref2,UploadVideoDetails.class)
                        .build();

        FirebaseRecyclerAdapter<UploadVideoDetails, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UploadVideoDetails, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull UploadVideoDetails model) {

                        holder.setDetails2(getApplication(),model.getName(),model.getUrl());
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row,parent,false);


                        return new ViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        mRecyclerView2.setAdapter(firebaseRecyclerAdapter);





    }
}
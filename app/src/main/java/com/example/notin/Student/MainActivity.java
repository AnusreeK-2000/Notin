package com.example.notin.Student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notin.Common.LoginActivity;
import com.example.notin.R;
import com.example.notin.Utils.SharedPrefUtil;
import com.example.notin.adapters.NotesAdapter;
import com.example.notin.database.NotesDatabase;
import com.example.notin.entities.Note;
import com.example.notin.listeners.NotesListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener, NavigationView.OnNavigationItemSelectedListener {

    //request code to add new note and update note
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_ALL_NOTES = 3;

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private int noteClickedPosition = -1;

    Boolean firstTime;

    //Variables
    ImageView menuIcon;

    SharedPrefUtil sharedPref;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String name_f;
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        sharedPref = new SharedPrefUtil(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        navigationDrawer();

        /*
        enable to view upload notes when clicked on floating action button..
        ImageView imageAddNoteMain=findViewById(R.id.imageAddNotes);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fp=new Intent(MainActivity.this,UploadNotesActivity.class);
                startActivity(fp);
            }
        });
        */

//        reference = FirebaseDatabase.getInstance().getReference().child("Member");
//
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        View header = navigationView.getHeaderView(0);
//        final TextView tv_username = header.findViewById(R.id.nav_username);
//        reference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                name_f = snapshot.child("name").getValue().toString();
//                if(currentUser.getDisplayName().toString() != "") {
//                    tv_username.setText(name_f);
//                }else{
//                    tv_username.setText("Hello User!");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        //String imgurl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
//        if(currentUser.getPhotoUrl() != null){
//            String imgurl = currentUser.getPhotoUrl().toString();
//            ImageView iv_userphoto = header.findViewById(R.id.userPhoto);
//            Glide.with(this).load(imgurl).into(iv_userphoto);
//        }

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNotes);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.example.notin.Student.MainActivity.this.startActivityForResult(
                        new Intent(com.example.notin.Student.MainActivity.this, CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });


        //saveNote();
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        notesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);


        getNotes(REQUEST_CODE_SHOW_ALL_NOTES,false);

        //for search notes
        EditText inputSearch=findViewById(R.id.searchField);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(noteList.size()!=0){
                    notesAdapter.searchNotes(s.toString());
                }

            }
        });
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);

    }

    private void getNotes(final int requestCode,final boolean isNoteDeleted ) {
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                //Log.d("MY_NOTES",notes.toString());
                /*adding to db and display in recyclerview initially
                if(noteList.size()==0){
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else{
                    noteList.add(0,notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                }
                notesRecyclerView.smoothScrollToPosition(0);
                 */
                if (requestCode == REQUEST_CODE_SHOW_ALL_NOTES) {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    noteList.remove(noteClickedPosition);
                    if(isNoteDeleted){
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }else{
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);

                    }
                }

            }
        }
        new GetNotesTask().execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE,false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));
            }

        }
    }

    //Navigation Drawer Functions
    private void navigationDrawer() {

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView tv_username = header.findViewById(R.id.nav_username);
        String n = sharedPref.getString("userName");
        if(currentUser != null){
            if(n != null){
                tv_username.setText(n);
            }
            else if (currentUser.getDisplayName() != "") {
                tv_username.setText(currentUser.getDisplayName());
            } else {
                tv_username.setText("Hello User!");
            }
            //String imgurl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
            if (currentUser.getPhotoUrl() != null) {
                String imgurl = currentUser.getPhotoUrl().toString();
                ImageView iv_userphoto = header.findViewById(R.id.userPhoto);
                Glide.with(this).load(imgurl).into(iv_userphoto);
            }
        }

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                //startActivity(new Intent(this, LoginActivity.class));
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                firstTime = true;
                editor.putBoolean("firstTime", firstTime);
                editor.apply();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);break;
            case R.id.nav_home:
                startActivity(new Intent(this, Home.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(this, UpdateProfile.class));
                break;
            case R.id.nav_upload:
                startActivity(new Intent(this, UploadActivity.class));
                break;
            case R.id.your_notes:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.create_note:
                startActivity(new Intent(this, CreateNoteActivity.class));
                break;

            default:
                return true;
        }
        return true;

    }
}

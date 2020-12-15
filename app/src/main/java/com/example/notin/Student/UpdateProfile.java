package com.example.notin.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notin.Common.LoginActivity;
import com.example.notin.R;
import com.example.notin.Utils.SharedPrefUtil;
import com.example.notin.entities.Member;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    //Variables
    ImageView menuIcon;

    Spinner spinnerSem;
    Spinner spinnerDept;
    String itemsem;
    String itemdept;


    private FirebaseAuth mAuth;
    Boolean firstTime;
    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseUser currentUser;

    TextView tv_username;
    TextView full_name_pro;
    TextInputEditText userr;

    TextView emailedit;
    TextView email3;

    DatabaseReference reference;
    Member member;

    SharedPrefUtil sharedPref;

    String name_f;
    String dept_f;
    String sem_f;
    String email_f;

    String teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        sharedPref = new SharedPrefUtil(UpdateProfile.this);
        teacher = sharedPref.getString("teacher");
        if (teacher.equals("1")) {
            reference = FirebaseDatabase.getInstance().getReference().child("Faculty");
        } else {
            reference = FirebaseDatabase.getInstance().getReference().child("Member");
        }

        member = new Member();


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        userr = findViewById(R.id.inputname);
        full_name_pro = findViewById(R.id.full_name);

        email3 = findViewById(R.id.email);
        emailedit = findViewById(R.id.editEmail);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        tv_username = header.findViewById(R.id.nav_username);

            if (currentUser.getDisplayName() != "") {
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


        // Spinner element
        spinnerSem = (Spinner) findViewById(R.id.spinnerSemPro);
        spinnerDept = (Spinner) findViewById(R.id.spinnerDeptPro);

        if(teacher.equals("1")){
            findViewById(R.id.spinnerLayoutU).setVisibility(View.GONE);
            spinnerSem.setVisibility(View.GONE);
        }

        // Spinner click listener
        spinnerSem.setOnItemSelectedListener(this);
        spinnerDept.setOnItemSelectedListener(this);

        // Spinner Drop down elements - for semester
        List<String> categoriesSem = new ArrayList<String>();
        categoriesSem.add("1");
        categoriesSem.add("2");
        categoriesSem.add("3");
        categoriesSem.add("4");
        categoriesSem.add("5");
        categoriesSem.add("6");
        categoriesSem.add("7");
        categoriesSem.add("8");

        // Spinner Drop down elements - for department
        List<String> categoriesDept = new ArrayList<String>();
        categoriesDept.add("Computer Science & Engineering");
        categoriesDept.add("Mechanical Engineering");
        categoriesDept.add("Civil Engineering");
        categoriesDept.add("Electrical & Electronics Engineering");
        categoriesDept.add("Electronics & Communication Engineering");
        categoriesDept.add("Industrial Engineering & Management");
        categoriesDept.add("Electronics & Telecommunication Engineering");
        categoriesDept.add("Information Science & Engineering");
        categoriesDept.add("Electronics & Instrumentation Engineering");
        categoriesDept.add("Medical Electronics Engineering");
        categoriesDept.add("Bio Technology");
        categoriesDept.add("Chemical Engineering");
        categoriesDept.add("Aerospace Engineering");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterSem = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesSem);
        ArrayAdapter<String> dataAdapterDept = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesDept);

        // Drop down layout style - list view with radio button
        dataAdapterSem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterDept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerSem.setAdapter(dataAdapterSem);
        spinnerDept.setAdapter(dataAdapterDept);

        String s="";
        if(!teacher.equals("1")){
            s = sharedPref.getString("userSem");
        }

        String d = sharedPref.getString("userDept");
        if (s != null && d != null){
            spinnerDept.setSelection(categoriesDept.indexOf(d));
            spinnerSem.setSelection(categoriesSem.indexOf(s));
            itemdept = d;
            itemsem = s;
        }else{
            itemdept = "Computer Science & Engineering";
            itemsem = "5";
        }




        updateProfile();
        navigationDrawer();


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        if (parent.getId() == R.id.spinnerSemPro) {
            // On selecting a spinner item
            itemsem = parent.getItemAtPosition(position).toString();

        } else if (parent.getId() == R.id.spinnerDeptPro) {
            // On selecting a spinner item
            itemdept = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void updateProfile() {

//        reference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                name_f = snapshot.child("name").getValue().toString();
//                sem_f = snapshot.child("semester").getValue().toString();
//                dept_f = snapshot.child("department").getValue().toString();
//                email_f = snapshot.child("email").getValue().toString();

                String name_f = sharedPref.getString("userName");

                String email_f = sharedPref.getString("userEmail");
                String dept_f = sharedPref.getString("userDept");

                if(!teacher.equals("1")){
                    String sem_f = sharedPref.getString("userSem");
                }

                tv_username.setText(name_f);
                userr.setText(name_f);
                full_name_pro.setText(name_f);

                email3.setText(email_f);
                emailedit.setText(email_f);


//                if(currentUser.getDisplayName() != "") {
//                    if(name_f != ""){
//                        tv_username.setText(name_f);
//                        userr.setText(name_f);
//                        full_name_pro.setText(name_f);
//                        sharedPref.saveString("userName", name_f);
//                    }
//                    else{
//                        tv_username.setText(currentUser.getDisplayName());
//                        userr.setText(currentUser.getDisplayName());
//                        full_name_pro.setText(currentUser.getDisplayName());
//                        sharedPref.saveString("userName", currentUser.getDisplayName());
//                    }
//
//                }else{
//                    tv_username.setText("Hello User!");
//                    full_name_pro.setText("User");
//                }
//
//                if(currentUser.getEmail() != "") {
//                    if (email_f != ""){
//                        email3.setText(email_f);
//                        emailedit.setText(email_f);
//                        sharedPref.saveString("userEmail", email_f);
//                    }else{
//                        email3.setText(currentUser.getEmail());
//                        emailedit.setText(currentUser.getEmail());
//                        sharedPref.saveString("userEmail", currentUser.getEmail());
//                    }
//
//                }else{
//                    email3.setText("");
//                    emailedit.setText("");
//                }
//

//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        if(currentUser.getPhotoUrl() != null){
            String imgurl = currentUser.getPhotoUrl().toString();
            ImageView iv_userphoto = findViewById(R.id.profile_image);
            Glide.with(this).load(imgurl).into(iv_userphoto);
        }

        updateToFirebase();
    }

    private void updateToFirebase(){

        final TextInputEditText emaill = findViewById(R.id.editEmail);
        final TextInputEditText namee = findViewById(R.id.inputname);
//        final TextInputEditText sem = findViewById(R.id.sem_et);
//        final TextInputEditText deptt = findViewById(R.id.dept_et);
        Button update_pro = findViewById(R.id.update_profile);
        update_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = namee.getText().toString().trim();
                String email = emaill.getText().toString().trim();
//                String dept = deptt.getText().toString().trim();
                String dept = itemdept;
//                String semester = sem.getText().toString().trim();


                tv_username.setText(name);
                email3.setText(email);
                TextView user = findViewById(R.id.full_name);
                user.setText(name);
                TextView emailedit = findViewById(R.id.email);
                emailedit.setText(email);

                member.setName(name);
                member.setDepartment(dept);
                member.setEmail(email);


                reference.child(currentUser.getUid()).setValue(member);

                sharedPref.saveString("userEmail",email);
                sharedPref.saveString("userName",name);
                sharedPref.saveString("userDept",dept);


                if(!teacher.equals("1")){
                    String semester = itemsem;
                    member.setSemester(semester);
                    sharedPref.saveString("userSem",semester);
                }

//                String b = sharedPref.getString("userDept");

                Toast.makeText(UpdateProfile.this, "Your profile has been successfully updated" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Navigation Drawer Functions
    private void navigationDrawer() {

//        String teacher = sharedPref.getString("teacher");

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        if(teacher.equals("1")){
            navigationView.getMenu().setGroupVisible(R.id.pri, false);
        }

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
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(this);
                //startActivity(new Intent(this, LoginActivity.class));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                firstTime = true;
                editor.putBoolean("firstTime", firstTime);
                editor.apply();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);break;
            case R.id.nav_profile:
                startActivity(new Intent(this, UpdateProfile.class));
                break;
            case R.id.nav_upload:
                startActivity(new Intent(this,UploadActivity.class));
                break;
            case R.id.your_notes:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.create_note:
                startActivity(new Intent(this,CreateNoteActivity.class));
                break;
            case R.id.nav_home:
                startActivity(new Intent(this, Home.class));
                break;

            default:
                return true;
        }
        return true;

    }


}
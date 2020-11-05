package com.example.notin.Student;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import com.example.notin.R;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button Confirm;
    private Button Cancel;
    private EditText Title;
    private ImageView info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        PopupWindow popUp = new PopupWindow(this);
        //To add the drop down menu of subjects
        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Subjects,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        /*FOR NOW ACCESSING CREATE VIA CLICKING CANCEL BUTTON
        Button btn = (Button)findViewById(R.id.Cancel_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Create.class));
            }
        });
*/
        //To disable confirm and cancel
        Confirm=findViewById(R.id.confirm_btn);
        Title=findViewById(R.id.TitleText);
        Cancel=findViewById(R.id.Cancel_btn);
        Title.addTextChangedListener(TitleEntry);
        Confirm.setEnabled(false);
        Cancel.setEnabled(false);
    }

    //Text watcher to disable button
    private TextWatcher TitleEntry = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (s.toString().equals("")) {
                Confirm.setEnabled(false);
                Cancel.setEnabled(false);
            } else {
                Confirm.setEnabled(true);
                Cancel.setEnabled(true);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),text, Toast.LENGTH_SHORT).show();
    }
    //For the spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), " Nothing    ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /*public void Create() {
        Intent intent;
        intent = new Intent(this, Create.class);
        startActivity(intent);
    }*/


}


package com.example.doc_app_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class sign_up_doctor_or_patient extends AppCompatActivity {
    private Boolean catcher;
    private EditText doctorField, patientField;
    private Intent intent;
    private TextView signIn;
    private Button signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_doctor_patient);
        doctorField = findViewById(R.id.edit_status_doctor);
        patientField = findViewById(R.id.edit_status_patient);
        signIn = findViewById(R.id.dp_sign_in);
        signUp = findViewById(R.id.btn_sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(sign_up_doctor_or_patient.this, "Functionality not added !", Toast.LENGTH_SHORT).show();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(sign_up_doctor_or_patient.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        catcher = getIntent().getBooleanExtra("Catch", false);

        if (catcher == false) {
            patientField.setVisibility(View.INVISIBLE);
        }
        if (catcher == true) {
            patientField.setVisibility(View.VISIBLE);
            doctorField.setVisibility(View.INVISIBLE);
        }
    }
}
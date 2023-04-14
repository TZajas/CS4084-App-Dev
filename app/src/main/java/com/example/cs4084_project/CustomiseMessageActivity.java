package com.example.cs4084_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CustomiseMessageActivity extends AppCompatActivity {
    private Button red_btn, yellow_btn, orange_btn, save_exit_btn;
    private Map<String, String> alert_messages;
    private EditText red_edit, orange_edit, yellow_edit;
    private FirebaseFirestore db;
    private DocumentReference doc;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customise_message);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        doc = db.collection("customAlerts").document(user.getEmail());

        alert_messages = new HashMap<>();

        save_exit_btn = findViewById(R.id.save_exit_btn);

        red_btn = findViewById(R.id.red_message_btn);
        orange_btn = findViewById(R.id.orange_message_btn);
        yellow_btn = findViewById(R.id.yellow_message_btn);

        red_edit = findViewById(R.id.edit_red);
        orange_edit = findViewById(R.id.edit_orange);
        yellow_edit = findViewById(R.id.edit_yellow);

        red_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                red_edit.setVisibility(View.VISIBLE);
            }
        });

        yellow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yellow_edit.setVisibility(View.VISIBLE);
            }
        });

        orange_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orange_edit.setVisibility(View.VISIBLE);
            }
        });

        save_exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orange_edit.getText().toString()!=null){
                    alert_messages.put("orange", orange_edit.getText().toString());
                }

                if(yellow_edit.getText().toString()!=null){
                    alert_messages.put("yellow", yellow_edit.getText().toString());
                }

                if(red_edit.getText().toString()!=null){
                    alert_messages.put("red", red_edit.getText().toString());
                }

                if(!alert_messages.isEmpty()){
                    doc.set(alert_messages, SetOptions.merge());
                }

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
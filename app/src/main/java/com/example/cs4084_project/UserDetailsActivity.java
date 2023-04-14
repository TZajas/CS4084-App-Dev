package com.example.cs4084_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {
    final private Calendar myCalendar= Calendar.getInstance();
    private EditText name,height,weight,dob,phone;
    private Button save_btn;
    private Button cancel_btn;
    private ListView user_details_list;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference doc;
    private CollectionReference userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        name = findViewById(R.id.user_details_name);
        height = findViewById(R.id.user_details_height);
        weight = findViewById(R.id.user_details_weight);
        dob = findViewById(R.id.user_details_dob);
        phone = findViewById(R.id.user_details_phone);

        save_btn = findViewById(R.id.user_details_submit);
        cancel_btn = findViewById(R.id.user_details_cancel);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        doc = db.collection("userDetails").document(user.getEmail());

        userDetails = db.collection("userDetails");

        user_details_list = findViewById(R.id.user_details_list);

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_modify_layout, arrayList);
        user_details_list.setAdapter(adapter);

        /**
         * Creates date picker object when date of birth field is clicked
         */
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UserDetailsActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        /**
         * Retrieves user details from database if they have been previously set
         */
        doc.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Map<String, Object> details = documentSnapshot.getData();

                            for (Map.Entry<String, Object> entry: details.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue().toString();
                                String detail = key + " : " + value;
                                arrayList.add(detail);
                            }
                            Collections.reverse(arrayList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(UserDetailsActivity.this, "No Details Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        /**
         * Saves user details to firestore database
         */
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean emptyValue = false;

                String n = name.getText().toString();
                String h = height.getText().toString();
                String w = weight.getText().toString();
                String d = dob.getText().toString();
                String p = phone.getText().toString();

                Map<String, String> usermap = new HashMap<>();

                usermap.put("Name", n);
                usermap.put("Height", h);
                usermap.put("Weight", w);
                usermap.put("Date of Birth", d);
                usermap.put("Phone", p);

                for (Map.Entry<String, String> set :
                        usermap.entrySet()) {

                    if (set.getValue().isEmpty()){
                        emptyValue=true;
                        break;
                    }
                }

                if(!emptyValue){
                    userDetails.document(user.getEmail()).set(usermap, SetOptions.merge());

                    Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(UserDetailsActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UserDetailsActivity.this, "Please fill in all details", Toast.LENGTH_LONG).show();
                }

            }
        });

        /**
         * Returns user to home activity
         */
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Updates date of birth field with specified format
     */
    private void updateLabel() {
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.UK);
        dob.setText(dateFormat.format(myCalendar.getTime()));
    }
}
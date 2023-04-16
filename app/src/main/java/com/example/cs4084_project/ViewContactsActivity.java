//Created By Tomasz Zajas: 20278748
package com.example.cs4084_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewContactsActivity extends AppCompatActivity {
    private ListView contact_list;
    private FirebaseFirestore db;
    private DocumentReference doc;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private ArrayList<String> arrayList;
    private ArrayList<String> contactNumbers;
    private ArrayAdapter<String> adapter;
    private String selected_name="";
    private Button back_btn;
    private Button delete_contact_btn;
    private TextView select_contact_textview, no_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        auth = FirebaseAuth.getInstance();

        back_btn = findViewById(R.id.back_btn);
        delete_contact_btn = findViewById(R.id.delete_contact_btn);
        select_contact_textview = findViewById(R.id.select_contact_textview);

        user = auth.getCurrentUser();

        contactNumbers = new ArrayList<>();

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.listview_modify_layout, arrayList);

        contact_list = findViewById(R.id.contact_list);

        contact_list.setAdapter(adapter);

        no_contacts = findViewById(R.id.no_contacts);

        db = FirebaseFirestore.getInstance();
        doc = db.collection("userContacts").document(user.getEmail());

        /**
         * Retrieves contacts from firestore database if they have previously been added
         * if not it displays warning message
         */
        doc.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Map<String, Object> numbers = documentSnapshot.getData();

                            if(numbers.isEmpty()){
                                no_contacts.setVisibility(View.VISIBLE);
                            }

                            for (Map.Entry<String, Object> entry: numbers.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue().toString();
                                String contact = key + " : " + value;
                                arrayList.add(contact);
                                contactNumbers.add(value);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ViewContactsActivity.this, "No Contacts Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        /**
         * When a list view item is clicked the variable selected_name os set depending
         * on if a name is single or double
         */
        contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String contact = adapterView.getItemAtPosition(i).toString();
                String[] split = contact.split(" ");

                if(split.length > 2){
                    selected_name = split[0] + " " + split[1];
                }else{
                    selected_name = split[0];
                }
                delete_contact_btn.setText("Delete " + selected_name);
                delete_contact_btn.setVisibility(View.VISIBLE);
                select_contact_textview.setVisibility(View.GONE);
            }
        });

        /**
         * Returns user to home page
         */
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Deletes contact that has previously been selected from list view
         */
        delete_contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> updates = new HashMap<>();
                updates.put(selected_name, FieldValue.delete());
                Toast.makeText(ViewContactsActivity.this, "Deleted: " + selected_name, Toast.LENGTH_SHORT).show();
                doc.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(getIntent());
                    }
                });
            }
        });
    }
}
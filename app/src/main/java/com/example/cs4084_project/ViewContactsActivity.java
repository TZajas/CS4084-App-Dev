package com.example.cs4084_project;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewContactsActivity extends AppCompatActivity {
    ListView contact_list;
    FirebaseFirestore db;
    DocumentReference doc;

    FirebaseUser user;

    FirebaseAuth auth;

    ArrayList<String> arrayList;
    ArrayList<String> contactNumbers;
    ArrayAdapter<String> adapter;

    String selected_name="";

    Button back_btn;

    Button delete_contact_btn;

    TextView select_contact_textview;

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

        db = FirebaseFirestore.getInstance();
        doc = db.collection("userContacts").document(user.getEmail());

        doc.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Map<String, Object> numbers = documentSnapshot.getData();

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

        contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String contact = adapterView.getItemAtPosition(i).toString();
                String[] split = contact.split(" ");
                selected_name = split[0];
                delete_contact_btn.setText("Delete " + selected_name);
                delete_contact_btn.setVisibility(View.VISIBLE);
                select_contact_textview.setVisibility(View.GONE);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        delete_contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> updates = new HashMap<>();
                updates.put(selected_name, FieldValue.delete());
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
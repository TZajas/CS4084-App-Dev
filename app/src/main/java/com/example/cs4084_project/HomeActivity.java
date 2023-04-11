package com.example.cs4084_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    Button yellowAlert;
    Button orangeAlert;
    Button redAlert;
    Button addContact;
    Button viewContacts;

    Button maps;

    ImageButton userDetails;
    FirebaseAuth auth;
    Button logoutButton;
    FirebaseUser user;
    FirebaseFirestore db;

    String[] permissions= new String[] {
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (!hasPermissions(HomeActivity.this,permissions)) {
            ActivityCompat.requestPermissions(HomeActivity.this,permissions,1);
        }


        db = FirebaseFirestore.getInstance();

        yellowAlert = findViewById(R.id.yellow_alert_btn);
        orangeAlert = findViewById(R.id.orange_alert_btn);
        redAlert = findViewById(R.id.red_alert_btn);

        maps = findViewById(R.id.campus_maps_btn);

        addContact = findViewById(R.id.add_contacts_btn);

        logoutButton = findViewById(R.id.logout_btn);

        viewContacts = findViewById(R.id.view_contacts_btn);

        userDetails = findViewById(R.id.user_details_btn);

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        if(user==null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewContactsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        userDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddContactActivity.class);
                intent.putExtra("email", user.getEmail());
                startActivity(intent);
                finish();
            }
        });

        yellowAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                intent.putExtra("alert", "Yellow");
                startActivity(intent);
                finish();
            }
        });

        orangeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                intent.putExtra("alert", "Orange");
                startActivity(intent);
                finish();
            }
        });

        redAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                intent.putExtra("alert", "Red");
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {

            for (String permission: PERMISSIONS){

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Permission is granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "SMS Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission is granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Location Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Contacts Permission is granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Contacts Permission is denied", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
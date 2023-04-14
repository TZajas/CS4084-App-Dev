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

    private Button yellowAlert, orangeAlert, redAlert, addContact, viewContacts, customiseMessage, maps, logoutButton, safetyTips;
    private ImageButton userDetails;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String[] permissions= new String[] {
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

        customiseMessage = findViewById(R.id.customise_message_btn);

        safetyTips = findViewById(R.id.safety_tips_btn);

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        if(user==null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        /**
         * Starts safety tips activity
         */
        safetyTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SafetyTipsActivity.class);
                startActivity(intent);
                finish();
            }
        });


        /**
         * Starts customise message activity
         */
        customiseMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomiseMessageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Starts maps activity
         */
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });


        /**
         * Logs out user
         */
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Starts view contacts activity
         */
        viewContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewContactsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Starts user details activity
         */
        userDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Starts add contact activity
         */
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddContactActivity.class);
                intent.putExtra("email", user.getEmail());
                startActivity(intent);
                finish();
            }
        });

        /**
         * Starts yellow alert activity
         */
        yellowAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                intent.putExtra("alert", "Yellow");
                startActivity(intent);
                finish();
            }
        });

        /**
         * Starts orange alert activity
         */
        orangeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                intent.putExtra("alert", "Orange");
                startActivity(intent);
                finish();
            }
        });

        /**
         * Starts red alert activity
         */
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

    /**
     * This functions checks permissions have been displayed previously
     * @param context
     * @param PERMISSIONS
     * @return
     */
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

    /**
     * Checks which permissions were granted and which were declined
     * @param requestCode The request code passed in
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
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
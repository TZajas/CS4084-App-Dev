package com.example.cs4084_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;


public class SendMessageActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String TAG = "MainActivity";

    private String userLocation;
    private Button cancelAlertBtn;
    private TextView timer;
    private TextView countdownMessage;
    private CountDownTimer countDownTimer;

    private FirebaseFirestore db;
    private DocumentReference doc;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<String> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        Bundle extras = getIntent().getExtras();
        String alert_colour = extras.getString("alert");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        doc = db.collection("userContacts").document(user.getEmail());

        contacts = getContacts();

        timer = findViewById(R.id.timer);
        countdownMessage = findViewById(R.id.countdown_message);
        String message = alert_colour + " Alert Sending In:";
        countdownMessage.setText(message);

        cancelAlertBtn = findViewById(R.id.cancel_alert_btn);

        requestLocationPermission();

        cancelAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlert();
            }
        });

        countDownTimer = new CountDownTimer(6000, 600) {
            @Override
            public void onTick(long l) {
                timer.setText(String.valueOf(l / 1000));
            }

            @Override
            public void onFinish() {
                timer.setVisibility(View.GONE);
                cancelAlertBtn.setEnabled(false);
                cancelAlertBtn.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        sendMessage(alert_colour, contacts);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 4000);
            }
        }.start();
    }

    private void cancelAlert() {
        countDownTimer.cancel();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        // Get the device's current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
                        userLocation = "Latitude: " + latitude + ", Longitude: " + longitude;
                    } else {
                        Log.e(TAG, "Location is null");
                    }
                }
            });
        } else {
            Log.e(TAG, "Location permission not granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Log.e(TAG, "Location permission not granted");
                }
                break;
            default:
                break;
        }
    }

    public ArrayList<String> getContacts() {
        ArrayList<String> result = new ArrayList<>();
        doc.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> numbers = documentSnapshot.getData();

                            for (Map.Entry<String, Object> entry : numbers.entrySet()) {
                                String value = entry.getValue().toString();
                                result.add(value);
                            }
                        } else {
                            Toast.makeText(SendMessageActivity.this, "No Contacts Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendMessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        return result;
    }

    public void sendMessage(String alert_colour, ArrayList<String> contacts) {
        String alert_message;

        if (alert_colour == "Red") {
            alert_message = "Red";
        } else if (alert_colour == "Orange") {
            alert_message = "Orange";
        } else {
            alert_message = "Yellow";
        }

        countdownMessage.setText(alert_colour + " Alert Sent!");
        Toast.makeText(this, userLocation, Toast.LENGTH_LONG).show();
        for (String num : contacts) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(num, null, alert_message, null, null);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error, message did not send", Toast.LENGTH_LONG).show();
            }
        }
    }
}
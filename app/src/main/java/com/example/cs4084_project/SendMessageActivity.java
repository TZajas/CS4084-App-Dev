//Created By Tomasz Zajas: 20278748
//and Dennis Kolomiyets: 20250762
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
    private String red_msg, orange_msg, yellow_msg, alert_colour, alert_message, user_location;
    private Button cancelAlertBtn;
    private TextView timer;
    private TextView countdownMessage;
    private CountDownTimer countDownTimer;
    private FirebaseFirestore db;
    private DocumentReference doc;
    private DocumentReference custom_message_doc;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<String> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        Bundle extras = getIntent().getExtras();
        alert_colour = extras.getString("alert");

        red_msg = "RED ALERT";
        orange_msg = "ORANGE ALERT";
        yellow_msg = "YELLOW ALERT";

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        doc = db.collection("userContacts").document(user.getEmail());
        custom_message_doc = db.collection("customAlerts").document(user.getEmail());

        customiseMessage();

        contacts = getContacts();

        timer = findViewById(R.id.timer);
        countdownMessage = findViewById(R.id.countdown_message);
        countdownMessage.setText(alert_colour + " Alert Sending In:");

        cancelAlertBtn = findViewById(R.id.cancel_alert_btn);

        requestLocationPermission();

        /**
         * calls cancelAlert Function
         */
        cancelAlertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlert();
            }
        });

        /**
         * A countdown timer that waits 5 seconds before sending alert message
         */
        countDownTimer = new CountDownTimer(6000, 600) {
            @Override
            public void onTick(long l) {
                timer.setText(String.valueOf(l / 1000));
            }

            /**
             * Sends alert message when timer is finished
             */
            @Override
            public void onFinish() {
                timer.setVisibility(View.GONE);
                cancelAlertBtn.setEnabled(false);
                cancelAlertBtn.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        sendMessage(contacts);
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


    /**
     * Cancels timer and returns user to home page
     */
    private void cancelAlert() {
        countDownTimer.cancel();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Takes an array of previosuly added contacts from database and uses the smsManager API
     * to send a customised messsage to each contact
     * @param contacts
     */
    public void sendMessage(ArrayList<String> contacts) {

        if (alert_colour.equals("Red")) {
            alert_message = red_msg;
        } else if (alert_colour.equals("Orange")) {
            alert_message = orange_msg;
        } else {
            alert_message = yellow_msg;
        }

        alert_message = alert_message + " " + user_location;

        countdownMessage.setText(alert_colour + " Alert Sent!");

        Toast.makeText(this, alert_message, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Alert Sent to Contacts", Toast.LENGTH_SHORT).show();
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

    /**
     * Fetches the customised messages from database if they are set and modifies the message variables
     */
    public void customiseMessage() {
        custom_message_doc.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Map<String, Object> details = documentSnapshot.getData();

                            for (Map.Entry<String, Object> entry: details.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue().toString();
                                if (key.equals("yellow") && value!="")  yellow_msg = value;
                                if (key.equals("orange") && value!="") orange_msg = value;
                                if (key.equals("red") && value!="") red_msg = value;
                            }

                        } else {
                            Toast.makeText(SendMessageActivity.this, "No Custom Message", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendMessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    
    //method for asking for permission to access users location
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        // Get the device's current location using LocationServices listener
        // on success gets the latitude and longitude
        // latitude and longitude are then stored in user_location which is a  link that will be sent to contacts and can be access by them to provide the location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
                        user_location = "https://www.google.com/maps/search/?api=1&query=" + latitude + "%2C" + longitude;
                    } else {
                        Log.e(TAG, "Location is null");
                    }
                }
            });
        } else {
            Log.e(TAG, "Location permission not granted");
        }
    }

    /**
     * Checks if location permission has been granted
     * @param requestCode The request code passed in
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
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


    /**
     * Fetches contacts that user has previously added from database and returns an array with those contacts
     * @return contact list
     */
    public ArrayList<String> getContacts() {
        ArrayList<String> result = new ArrayList<>();
        doc.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> numbers = documentSnapshot.getData();

                            if(numbers.isEmpty()){
                                Toast.makeText(SendMessageActivity.this, "No Contacts Added", Toast.LENGTH_LONG).show();
                                cancelAlert();
                            }else{
                                for (Map.Entry<String, Object> entry : numbers.entrySet()) {
                                    String value = entry.getValue().toString();
                                    result.add(value);
                                }
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
}

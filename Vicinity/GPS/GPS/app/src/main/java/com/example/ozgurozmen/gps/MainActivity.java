package com.example.ozgurozmen.gps;

//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

    public class MainActivity extends Activity implements OnClickListener {

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private String locationProvider = null;
    private Button btnGetLocation = null;
    private EditText editLocation = null;
    private ProgressBar pb =null;

    private static final String TAG = "Debug";
    private Boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        editLocation = (EditText) findViewById(R.id.editTextLocation);

        btnGetLocation = (Button) findViewById(R.id.btnLocation);
        btnGetLocation.setOnClickListener(this);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onClick(View v) {
        flag = displayGpsStatus();
        if (flag) {

            Log.v(TAG, "onClick");

            editLocation.setText("Waiting for Location...");

            pb.setVisibility(View.VISIBLE);
            locationListener = new MyLocationListener();
            locationProvider = LocationManager.NETWORK_PROVIDER;
            if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            }
            locationManager.requestLocationUpdates(locationProvider, 5*60*1000, 10, locationListener);
        } else {
            alertbox("Gps Status!!", "Your GPS is: OFF");
        }

    }

    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    /*----------Method to create an AlertBox ------------- */
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            editLocation.setText("");
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getBaseContext(),"Location changed : Lat: " +
                            loc.getLatitude()+ " Lng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " +loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " +loc.getLatitude();
            Log.v(TAG, latitude);

            double lat = loc.getLatitude();
            double lon = loc.getLongitude();

            double[][] loca = {
                    {39.872152, 32.751010},
                    {39.870167, 32.749594},
                    {39.868931, 32.747953},
                    {39.868166, 32.751654},
                    {39.866066, 32.748886},
                    {39.864658, 32.747326}
            };

            String [] locations = {"Bilkent EE Building", "Bilkent Library", "Bilkent B Building", "Bilkent MayFest Area", "Bilkent FF Building/Speed", "Bilkent 76th Dormitory" };

            int minIndex = -1;
            double min = 0.000000562;
            double temp;
            String s = null;

            for (int i = 0; i<locations.length; i++){
                temp  = Math.pow(Math.abs(loca[i][0] - lat),2) + Math.pow(Math.abs(loca[i][1] - lon),2);

                if(temp < min){
                    min = temp;
                    minIndex = i;
                }
            }
            if (minIndex == -1){
                s = longitude+"\n"+latitude+"\n"+"Location can not be identified!!";
            } else {
                s = longitude+"\n"+latitude+"\n"+locations[minIndex];
            }
            editLocation.setText(s);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}
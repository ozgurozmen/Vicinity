package cs562OSU.vicinity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import android.Manifest;

/**
 * Created on 02.14.2018.
 * this activity is what user sees after login
 */
public class UserAreaActivity extends AppCompatActivity {

    private Boolean flag = false;
    private String locationProvider = null;
    private Button btnGetLocation = null;
    private Button btnNewAnn = null;
    private Button btnUsersNear = null;
    private TextView editLocation = null;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private TextView coordinates = null;
    private Button btnRate = null;
    String gps= "";
    String username = "";
    String name = "";
    LinearLayout messages;
    String image = "";
    ImageView profile ;
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
    private static final int INITIAL_REQUEST=1337;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        final TextView welcomeMessage = (TextView)findViewById(R.id.tvWelcomeMsg);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        messages = (LinearLayout) findViewById(R.id.messages);

        final String message = "" + name +"" ;
        welcomeMessage.setText(message);
        username = intent.getStringExtra("username");


        // Location Work
        editLocation = (TextView) findViewById(R.id.editTextLocation);
        btnGetLocation = (Button) findViewById(R.id.btnLocation);
        btnNewAnn = (Button) findViewById(R.id.btnNewAnnoucement);
        btnRate = (Button) findViewById(R.id.btnRate);


        btnRate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UserAreaActivity.this, Tokenator.class);

                intent.putExtra("username", username);
                intent.putExtra("gps", gps);
                intent.putExtra("name", name);

                UserAreaActivity.this.startActivity(intent);

            }
        });

        profile = (ImageView) findViewById(R.id.userImage);

        if (!canAccessLocation()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(" GPS"," Value OnClick : ****** ****** =  " + gps );

                flag = displayGpsStatus();

                if (flag)
                {
                    editLocation.setText("Waiting for Location..."+ "");
                    locationListener = new MyLocationListener();
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                    if ( ContextCompat.checkSelfPermission(UserAreaActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
                    {}
                    locationManager.requestSingleUpdate(locationProvider, locationListener, null);


                    Log.d(" GPS", " Value After Location Manager : ****** ****** =  " + gps);

                }
                else
                {
                    alertbox("Gps Status!!", "Your GPS is: OFF");
                }


            }
        });

        btnNewAnn.setOnClickListener(new View.OnClickListener() {

            @Override   // Creating here a new message activity
            public void onClick(View v) {

                Intent intent = new Intent(UserAreaActivity.this, AnnouncementActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("gps", gps);
                intent.putExtra("name", name);
                UserAreaActivity.this.startActivity(intent);
            }
        });
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);

        if (gpsStatus)
        {
            return true;

        }
        else
        {
            return false;
        }
    }

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

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            editLocation.setText("");
            String longitude = "Longitude: " +loc.getLongitude();
            String latitude = "Latitude: " +loc.getLatitude();

            double lat = loc.getLatitude();
            double lon = loc.getLongitude();

            //Lat,Long
            double[][] loca = {
                    {44.567195, -123.278657},
                    {44.563137, -123.278617},
                    {44.564917, -123.278923},
                    {44.565298, -123.276014},
                    {44.576330, -123.280883},
                    {44.580325, -123.274623},
                    {44.593343, -123.269104}
            };

            String [] locations = {"Kelley Engineering Center", "Dixon Fitness Center", "Memorial Union", "The Valley Library", "Ozgur's Home", "1335 Kings", "Rouzbeh's Home" };

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

            gps = Integer.toString(minIndex);
            s = longitude+"\n"+latitude;
            String building = null;
            if (minIndex == -1){
                building = "Location can not be identified!!";
            } else {
                building = locations[minIndex];
            }
            editLocation.setText(building);
            //coordinates.setText(s);

            Response.Listener<String> responseListener4 = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        messages.removeAllViews();

                        JSONArray jsonarray = new JSONArray(response);

                        for(int i=0; i < jsonarray.length(); i++)
                        {
                            String bgColor = "#FFFFFF";
                            if(i%2==1)
                            {
                                bgColor="#f0f0f0";
                            }
                            JSONObject m1 = jsonarray.getJSONObject(i);
                            String username = m1.getString("username");
                            String title = m1.getString("title");
                            String message = m1.getString("message");
                            String strImg = m1.getString("image");
                            String strExpiredTime = m1.getString("ExpiredTimestamp");

                            long expTime  = (long)(Math.ceil((Long.parseLong(strExpiredTime)-TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) )/60.0));

                            byte[] decodedString = Base64.decode(strImg, Base64.DEFAULT);
                            Bitmap bp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);



                            String urlString = "http://www.clker.com/cliparts/B/R/Y/m/P/e/blank-profile-hi.png";
                            ImageRequest ir = new ImageRequest(urlString, new Response.Listener<Bitmap>(){

                                @Override
                                public void onResponse(Bitmap response) {

                                    profile.setImageBitmap(response);

                                }
                            },0,0,null,null);

                            MySingleton.getInstance(UserAreaActivity.this).addToRequestQueue(ir);

                            LinearLayout textImage = new LinearLayout(UserAreaActivity.this);
                            textImage.setOrientation(LinearLayout.HORIZONTAL);
                            textImage.setBackgroundColor(Color.parseColor(bgColor));
                            textImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            textImage.setPadding(10, 40, 10, 10);
                            textImage.setWeightSum(2f);

                            TextView titleText = new TextView(UserAreaActivity.this);
                            TextView userText = new TextView(UserAreaActivity.this);

                            View line = new View(UserAreaActivity.this);
                            line.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    5
                            ));
                            line.setBackgroundColor(Color.parseColor("#B3B3B3"));


                            TextView messageText = new TextView(UserAreaActivity.this);

                            ImageView im = new ImageView(UserAreaActivity.this);

                            im.setImageBitmap(bp);

                            titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
                            titleText.setTypeface(null, Typeface.BOLD);
                            titleText.setTextColor(Color.parseColor("#003B5C"));
                            titleText.setBackgroundColor(Color.parseColor(bgColor));
                            titleText.setText(title.toUpperCase());
                            titleText.setTypeface(Typeface.SANS_SERIF);
                            titleText.setPadding(10, 20, 10, 20);

                            messageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                            messageText.setPadding(20, 10, 10, 20);
                            messageText.setText(message);
                            userText.setTextColor(Color.parseColor("#000000"));
                            messageText.setBackgroundColor(Color.parseColor(bgColor));
                            messageText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                            im.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

                            userText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                            userText.setTypeface(null, Typeface.ITALIC);
                            userText.setPadding(10, 20, 10, 20);
                            userText.setText("Shared by " + username + " - Expired in "+expTime+" mins");
                            userText.setBackgroundColor(Color.parseColor(bgColor));
                            userText.setTextColor(Color.parseColor("#003B5C"));


                            messages.addView(titleText);
                            //
                            textImage.addView(messageText);

                            textImage.addView(im);
                            messages.addView(textImage);

                            messages.addView(userText);
                            messages.addView(line);

                        }

//                        System.out.println("******** Array Length       : **" + jsonObject.length());
                        //System.out.println("******** Array Objects (1)  : **" + ((arr.get(1))));
                        //System.out.println("******** Array Objects (1)  : **" + (m1.getString("message")));
//                        System.out.println("******** Array Objects (all): **" + arr ) ;


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            UpdateUserScreen updateUserScreen = new UpdateUserScreen(gps, responseListener4);
            RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
            queue.add(updateUserScreen);

            // Updating Server for GPS value
             Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                }
            };

            GpsUpdate gpsUpdate = new GpsUpdate( username, gps , responseListener );
            RequestQueue queue2 = Volley.newRequestQueue(UserAreaActivity.this);
            queue2.add(gpsUpdate);
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

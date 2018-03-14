package cs562OSU.vicinity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created on 02.14.2018.
 * Make an Announcement!
 */
public class AnnouncementActivity extends AppCompatActivity {

    private Button btSend, btnImageCamera, btnImageLibrary = null;
    private EditText etTitle = null;
    private EditText etMessage = null;
    private EditText etTTL = null;
    private TextView imStatus = null;
    private ImageView imView = null;
    private TextView imDelete = null;
    String username = null;
    String gps = null;
    String name = null;
    String image = "";
    private static final int CAMERA_REQUEST = 1888;
    private static final int IMG_LIBRARY_REQUEST = 1889;
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        gps = intent.getStringExtra("gps");
        name = intent.getStringExtra("name");
        btSend = (Button)findViewById(R.id.btSend);
        etTitle = (EditText)findViewById(R.id.etTitle);
        etTitle.setPadding(10, 10, 10, 10);
        etMessage = (EditText)findViewById(R.id.etAnnouncement);
        etTTL = (EditText)findViewById(R.id.etTTL);
        etTTL.setPadding(10, 10, 10, 10);
        imStatus = (TextView)findViewById(R.id.imStatus);
        imStatus.setPadding(10, 10, 10, 10);

        btnImageCamera = (Button)findViewById(R.id.btnImageCamera);
        btnImageLibrary =  (Button)findViewById(R.id.btnImageLibrary);

        imDelete = (TextView) findViewById(R.id.imDeleteNote);



        imView = (ImageView)findViewById(R.id.imView);

        imView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image!="")
                {
                    //display dialog
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    image="";
                                    imDelete.setText("");
                                    imStatus.setText("No Image Attached");
                                    imView.setImageResource(android.R.color.transparent);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Are you sure to delete?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            }
        });

        btnImageCamera.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        btnImageLibrary.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_LIBRARY_REQUEST);
            }
        });

        // Sending Server the Announcement
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final String title = etTitle.getText().toString();
                //final String username = etUsername.getText().toString();
                final String message = etMessage.getText().toString();
                final long ttl = Long.parseLong(etTTL.getText().toString());
                if(ttl > 1000)
                {
                    Toast.makeText(getApplicationContext(), "Duration is too long, please make it less than 1000",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(ttl < 1)
                {
                    Toast.makeText(getApplicationContext(), "Duration is too short, please make it more than 0",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(title.length()==0)
                {
                    Toast.makeText(getApplicationContext(), "Please input Title",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // SERVER SIDE JOBS

                Response.Listener<String> responseListener3 = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            Log.d("success : ", " aaaaa a aaa a a a  " + success );

                            if (success)
                            {
                                Intent Intent = new Intent(AnnouncementActivity.this, UserAreaActivity.class);
                                Intent.putExtra("username",username);
                                Intent.putExtra("name",name);
                                Intent.putExtra("gps",gps);
                                Intent.putExtra("image",image);
                                Intent.putExtra("ttl",ttl);
                                AnnouncementActivity.this.startActivity(Intent);
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AnnouncementActivity.this);
                                builder.setMessage("Sending Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                MessageRequest messageRequest = new MessageRequest(username, title, message, gps, image, ttl, responseListener3);
                RequestQueue queue = Volley.newRequestQueue(AnnouncementActivity.this);
                queue.add(messageRequest);

                Log.d("GPS: ", " GPS GPS GPS GPS GPS GPS GPS GPS  " + gps);
                Log.d("USER: " , " USER  USER USER USER USER USER  " + username);
                Log.d("title: " , " title title title title title  " + title);
                Log.d("message: " , " message message message message  " + message);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = null;
        if(resultCode == Activity.RESULT_CANCELED)
        {
            return;
        }

        try{
            if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
                bp = (Bitmap) data.getExtras().get("data");
            }
            else if(requestCode == IMG_LIBRARY_REQUEST && resultCode == Activity.RESULT_OK)
            {
                Uri targetUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(targetUri);
                bp = BitmapFactory.decodeStream(imageStream);
                bp = Bitmap.createScaledBitmap(bp, 480, 320, false);
            }

            imView.setImageBitmap(bp);

            image = getImageAsString(bp);
            imStatus.setText("Image is Attached!");
            imDelete.setText("(click to image to delete)");
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public String getImageAsString(Bitmap bp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bp.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }

}

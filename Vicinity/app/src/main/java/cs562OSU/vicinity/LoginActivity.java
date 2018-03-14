package cs562OSU.vicinity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


import android.widget.Toast;

/**
 * Created on 02.14.2018.
 * this activity is the first activity user sees to login or register
 */
public class LoginActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        final EditText etUsername = (EditText)findViewById(R.id.etUsername);
        final EditText etPassword = (EditText)findViewById(R.id.etPassword);
        final Button bLogin = (Button)findViewById(R.id.bLogin);
        final TextView registerLink = (TextView)findViewById(R.id.tvRegisterHere);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);

            }
        });

      //  txtOutput = (TextView) (findViewById(R.id.txtOutput));
       // webView = (WebView) (findViewById(R.id.webview));

        // webview may display web pages, refer to a callback object called "myapp", and run JS

       bLogin.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {

               final String username = etUsername.getText().toString();
               final String password = etPassword.getText().toString();

               if(username.length()==0)
               {
                   Toast.makeText(getApplicationContext(), "Please enter your username",
                           Toast.LENGTH_LONG).show();
                   return;
               }
               if(password.length() == 0)
               {
                   Toast.makeText(getApplicationContext(), "Please enter your password",
                           Toast.LENGTH_LONG).show();
                   return;
               }
               Response.Listener<String> responseListener = new Response.Listener<String>() {

                   @Override
                   public void onResponse(String response) {

                       try {
                           Log.i("tagconvertstr", "[" + response + "]");
                           JSONObject jsonResponse = new JSONObject(response);

                           boolean success = jsonResponse.getBoolean("success");

                           if(success){

                               String name = jsonResponse.getString("name");

                               Intent intent = new Intent(LoginActivity.this, UserAreaActivity.class);
                               intent.putExtra("name", name );
                               intent.putExtra("username", username);

                               LoginActivity.this.startActivity(intent);

                           }
                           else{
                               AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                               builder.setMessage("Login Failed")
                                       .setNegativeButton("Retry", null)
                                       .create()
                                       .show();
                           }

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }

                   }
               };

               LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
               RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
               queue.add(loginRequest);
           }
       });


    }
    // Here's the start of then token-request screens.



}

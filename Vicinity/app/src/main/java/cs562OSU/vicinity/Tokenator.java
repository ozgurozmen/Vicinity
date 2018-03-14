package cs562OSU.vicinity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Tokenator extends AppCompatActivity {


    String username = null;
    String gps = null;
    String name = null;
    String image = "";


    TextView txtOutput; // where we'll show messages to the user about current status
    WebView webView; // where we'll display the token-request screens and our secret image

    String token; // could load from file or savedInstanceState, if you want to persist

    private TokenStatus tokenStatus = TokenStatus.MISSING;
    private enum TokenStatus {
        MISSING, REQUESTING, UNVALIDATED, INVALID, READY
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokenator);

        /*findViewById(R.id.btnShow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDisplay();
            }
        });*/
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        gps = intent.getStringExtra("gps");
        name = intent.getStringExtra("name");
        txtOutput = (TextView) (findViewById(R.id.txtOutput));
        webView = (WebView) (findViewById(R.id.webview));

        // webview may display web pages, refer to a callback object called "myapp", and run JS
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "myapp");
        webView.loadUrl("about:blank");

        deleteCache(getApplicationContext());

        updateDisplay();
    }

    @Override
    protected void onDestroy() {
        try {
            deleteCache(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onDestroy();


    }

    // Here's the start of then token-request screens.
    // Use your own asid!!! Also, if your callback isn't called "myapp.setToken", change it.
    final String START_URL = "https://prometheus.eecs.oregonstate.edu/token?asid=6944184857188025&then=myapp.setToken";

    // Here's the URL that we will call to validate our token (which must be appended).
    // Use your own joinkey! BTW, you also can pass joinkey and token via http headers. See docs.
    final String VALIDATION_URL_BASE = "https://prometheus.eecs.oregonstate.edu/token/v1?joinkey=VIOUYBUK&token=";

    // Just a static image we'll show at the end. It's "the big secret". You could load from assets.
    final String SECRET_IMAGE = "http://web.engr.oregonstate.edu/~scaffidc/demo/25inShip.png";

    /**
     * Let the user know the current status. Set tokenStatus before calling this method.
     * Only call this method from the UI thread, please.
     */
    private void updateDisplay() {
        switch (tokenStatus) {
            case MISSING:
                txtOutput.setText("You'll need a token...");
                webView.loadUrl(START_URL);
                break;
            case REQUESTING:
                txtOutput.setText("Please do as told...");
                webView.loadUrl(START_URL);
                break;
            // when token becomes available, callback below is called, state -> UNVALIDATED
            case UNVALIDATED:
                txtOutput.setText("Checking token...");
                webView.loadUrl("about:blank");
                break;
            // on validation failure, we go to INVALID...
            case INVALID:
                txtOutput.setText("Please try again...");
                webView.loadUrl(START_URL);
                break;
            case READY:
                Intent intent = new Intent(this, UserAreaActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("gps", gps);
                intent.putExtra("name", name);
                this.startActivity(intent);
                // Go back to our app this point
                break;
            default:
                txtOutput.setText("invalid state");
                webView.loadUrl("about:blank");
                break;
        }
    }

    /**
     * Just calls updateDisplay, but makes sure that we're on the UI thread before calling.
     * If you don't know what this is or why, lurk moar before you write code. Start with this...
     * https://developer.android.com/guide/components/processes-and-threads.html
     * https://www.youtube.com/watch?v=oq8Ea-IAJG8
     */
    private void updateDisplayForceUiThread() {
        runOnUiThread(new Runnable() {
            public void run() {
                updateDisplay();
            }
        });
    }

    /**
     * Here's our JavaScript callback object that will be called when a token becomes available.
     * Read more about callback objects here...
     * https://developer.android.com/guide/webapps/webview.html
     */
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void setToken(String newToken) {
            token = newToken;
            tokenStatus = TokenStatus.UNVALIDATED;
            updateDisplayForceUiThread();
            validateToken();
        }
    }

    /**
     * Launches a thread to go validate the current token.
     */
    void validateToken() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    return;
                }
                if (token != null && token.length() > 0) {
                    String url = VALIDATION_URL_BASE + token;
                    JSONObject content = getJson(url);
                    boolean isvalid = false;
                    try {
                        isvalid = content.getBoolean("isvalid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //System.err.println("here: " + isvalid + "..." + content);
                    if (isvalid) {
                        tokenStatus = TokenStatus.READY;
                    } else {
                        token = "";
                        tokenStatus = TokenStatus.INVALID;
                    }
                    updateDisplayForceUiThread();
                } else {
                    token = "";
                    tokenStatus = TokenStatus.INVALID;
                    updateDisplayForceUiThread();
                }
            }
        }).start();
    }

    /**
     * Retrieves the textual content at the specified URL, parses it as JSON, and returns
     * the resulting JSON object. On exception, returns null.
     */
    public static JSONObject getJson(String url) {
        // https://stackoverflow.com/questions/43079460/how-to-read-json-data-from-url-in-android
        // https://stackoverflow.com/questions/9605913/how-to-parse-json-in-android
        HttpsURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpsURLConnection) u.openConnection();

            con.connect();


            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String json = sb.toString();
            JSONObject rv = new JSONObject(json);
            return rv;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

}

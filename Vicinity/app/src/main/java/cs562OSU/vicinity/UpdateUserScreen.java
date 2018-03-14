package cs562OSU.vicinity;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * Created on 02.14.2018.
 * this class takes messages from server
 */
public class UpdateUserScreen extends StringRequest {
  //  WebView webView; // where we'll display the token-request screens and our secret image

    private static final String REGISTER_REQUEST_URL = "http://www.cs562osu.co.nf/messageTaker.php";
    private Map<String, String> params;

    public UpdateUserScreen(String gps, Response.Listener<String> listener)
    {
        super(Method.POST , REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("gps", gps);
        // current timestamp
        String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
        params.put("timestamp",timeStamp);

    }

    @Override
    public Map<String, String> getParams()
    {
        return params;
    }
}

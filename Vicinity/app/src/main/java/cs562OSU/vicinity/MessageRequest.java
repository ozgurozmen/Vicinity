package cs562OSU.vicinity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created on 02.14.2018.
 */
public class MessageRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://www.cs562osu.co.nf/messagerequest.php";
    private Map<String, String> params;

    public MessageRequest(String username, String title, String message, String gps ,String image, long ttl, Response.Listener<String> listener){

        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("title", title);
        params.put("message",message);
        params.put("gps", gps);
        params.put("image",image);

        long expTime =  TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + ttl*60;
        String timeStamp =expTime + "";
        params.put("ExpiredTimestamp",timeStamp);
    }

    @Override
    public Map<String, String> getParams()
    {
        return params;
    }
}

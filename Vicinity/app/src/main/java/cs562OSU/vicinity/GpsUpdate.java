package cs562OSU.vicinity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created on 02.14.2018.
 * this class takes messages from server
 */
public class GpsUpdate extends StringRequest {

    private static final String GPS_UPDATE_REQUEST_URL = "http://www.cs562osu.co.nf/GpsUpdate.php";
    private Map<String, String> params;

    public GpsUpdate(String username, String gps, Response.Listener<String> listener) {
        super(Request.Method.POST, GPS_UPDATE_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("gps", gps);
    }

    @Override
    public Map<String, String> getParams() {
        return params;

    }
}
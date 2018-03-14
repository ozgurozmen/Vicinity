package cs562OSU.vicinity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 02.14.2018.
 * this class gives login credentials to server and gets the response
 */
public class LoginRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "http://www.cs562osu.co.nf/login3.php";
    private Map<String, String> params;

    public LoginRequest( String username, String password, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password",password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;

    }


}

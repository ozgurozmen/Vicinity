package cs562OSU.vicinity;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 02.14.2018.
 * this class register the user to the server
 */
public class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://www.cs562osu.co.nf/register3.php";
    private Map<String, String> params;

    public RegisterRequest(String name, String username, String password, Response.Listener<String> listener)
    {
        super(Method.POST , REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("username", username);
        params.put("password",password);
    }

    @Override
    public Map<String, String> getParams()
    {
        return params;
    }
}
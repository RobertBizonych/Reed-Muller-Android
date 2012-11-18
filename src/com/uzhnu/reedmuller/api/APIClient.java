package com.uzhnu.reedmuller.api;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.uzhnu.reedmuller.R;
import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

public class APIClient {
    public static final String PREFERENCES = "API_PREFERENCES";
    private static final String DEBUG_LOGIN = "ludwig@trash-mail.com";
    private static final String DEBUG_PASS = "ludwig";
    private String BASE_URL;
    private boolean DEBUG;

    private AsyncHttpClient client;
    private String login;
    private String password;
    private Context context;
    private static final int TIMEOUT = 5000;

    public APIClient(Context context, String _login, String _password) {
        init(context);

        if (DEBUG) {
            login = DEBUG_LOGIN;
            password = DEBUG_PASS;
        } else {
            login = _login;
            password = _password;
        }
    }

    public APIClient(Context context) {
        init(context);

        if (DEBUG) {
            login = DEBUG_LOGIN;
            password = DEBUG_PASS;
        } else {
            int mode = Activity.MODE_PRIVATE;
            SharedPreferences shPreferences = context.getSharedPreferences(APIClient.PREFERENCES, mode);
            login = shPreferences.getString("login", "");
            password = shPreferences.getString("password", "");
        }
    }

    private void init(Context _context) {
        context = _context;
        BASE_URL = context.getString(R.string.BASE_URL);
        client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);

        DEBUG = context.getResources().getBoolean(R.bool.DEBUG);
    }

    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        processGet(url, responseHandler);
    }

    public void getRemoteOrCached(BaseCacheHandler responseHandler) {
        if(isOnline()){
            processGet(responseHandler.getEndpoint(), responseHandler);
        } else {
            responseHandler.callSuccessWithCachedResponse();
        }
    }

    public void get(BaseHandler responseHandler) {
        processGet(responseHandler.getEndpoint(), responseHandler);
    }

    private void processGet(String url, AsyncHttpResponseHandler responseHandler) {
        boolean isOnline = APIClient.networkEnabled(context);

        if (isOnline) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
            Header header = BasicScheme.authenticate(credentials, "UTF-8", false);
            Header[] headers = {header};
            client.get(context, getAbsoluteUrl(url), headers, null, responseHandler);
        } else {
            Toast toast = Toast.makeText(context, "You should enable net connection", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private boolean isOnline() {
        return APIClient.networkEnabled(context);
    }

    public static Boolean networkEnabled(Context context) {
        String service = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(service);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();

        if (activeNetwork == null)
            return false;

        boolean isConnectedOrConnecting = activeNetwork.isConnectedOrConnecting();
        boolean isActiveAndAvailable = activeNetwork.isConnected() && activeNetwork.isAvailable();

        if (isConnectedOrConnecting || isActiveAndAvailable)
            return true;

        return false;
    }
}

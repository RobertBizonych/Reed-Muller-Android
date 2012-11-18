package com.uzhnu.reedmuller.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public abstract class BaseHandler extends JsonHttpResponseHandler {
    protected Toast toast;
    protected Context context;
    protected Activity activity;
    protected TransparentProgressDialog dialog;
    protected String endpoint;
    private String cacheDirPath;
    private String Tag = "BaseHandler";

    public BaseHandler(Activity _activity, String _endpoint) {
        endpoint =_endpoint;
        activity = _activity;
        context = activity.getApplicationContext();
        dialog = TransparentProgressDialog.show(activity, null, null);

        toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setDuration(10000);

        if (context.getExternalCacheDir() != null) {
            cacheDirPath = context.getExternalCacheDir().getParent();
        } else {
            cacheDirPath = context.getCacheDir().getParent();
        }
    }

    public void callSuccessWithCachedResponse() throws NoCachedFile, IOException {}

    @Override
    public void onSuccess(JSONObject response) {
        try {
            getFileCache().put(endpoint, response);
        } catch (IOException e) {
            Log.e((Tag + "onSuccess -> IOException"), e.getMessage());
        }
    }

    @Override
    public void onStart() {
        dialog.show();
    }

    @Override
    public void onFinish() {
        dialog.hide();
        dialog.dismiss();
    }

    @Override
    public void onFailure(Throwable error, JSONObject response) {
        String errorMessage;
        String errorCode;

        if(response == null){
            toast.setText(error.getMessage());
        }else {
            try {
                errorMessage = (String) response.get("errorMessage");
                errorCode = (String) response.get("errorCode");
                toast.setText("ErrorCode: " + errorCode + " ErrorMessage: " + errorMessage);
            } catch (JSONException e) {
                Log.e("BaseHandler -> onFailure", e.getMessage());
                toast.setText(e.getMessage());
            }
        }
        toast.show();
    }

    public String getEndpoint(){
        return endpoint;
    }

    protected JSONObject getCachedResponse() throws IOException, NoCachedFile {
        JsonFileCache fileCache = getFileCache();
        JSONObject response = fileCache.get(endpoint);

        if(response == null)
            throw new NoCachedFile(cacheDirPath.toString());

        return response;
    }


    private JsonFileCache getFileCache() throws IOException {
        return JsonFileCache.getFileCache(cacheDirPath);
    }

    public class NoCachedFile extends Exception {
        public NoCachedFile(String path) {
            super("There is no cached file with such path: " + path);
        }
    }
}

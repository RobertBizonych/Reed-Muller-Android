package com.uzhnu.reedmuller.api;


import android.app.Activity;
import com.uzhnu.reedmuller.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public abstract class BaseCacheHandler extends BaseHandler {
    public BaseCacheHandler(Activity _activity, String _endpoint) {
        super(_activity, _endpoint);
    }
    @Override
    public void onSuccess(JSONObject response) {
        super.onSuccess(response);
        handleSuccess(response);
    }

    @Override
    public void callSuccessWithCachedResponse() {
        try {
            handleSuccess(getCachedResponse());
        } catch (NoCachedFile noCachedFile) {
            onSuccessFailed("There is no cached content!");
        } catch (IOException e) {
            onSuccessFailed(e.getMessage());
        }
    }



    protected void onSuccessFailed(String message) {
        if (context.getResources().getBoolean(R.bool.DEBUG)) {
            toast.setText(message);
            toast.show();
            return;
        }

        toast.setText(context.getString(R.string.sth_went_wrong));
        toast.show();
    }

    protected String getString(JSONObject object, String key){
        try {
            return object.getString(key);
        } catch (JSONException e) {
            onSuccessFailed(e.getMessage());
            return "";
        }
    }

    protected Integer getInteger(JSONArray bytes, int i) {
        try {
            return bytes.getInt(i);
        } catch (JSONException e) {
            onSuccessFailed(e.getMessage());
            return -1;
        }
    }

    protected JSONObject getJSONObject(JSONObject object, String key){
        try {
            return object.getJSONObject(key);
        } catch (JSONException e) {
            onSuccessFailed(e.getMessage());
            return null;
        }
    }

    protected JSONArray getJSONArray(JSONObject object, String key){
        try {
            return object.getJSONArray(key);
        } catch (JSONException e) {
            onSuccessFailed(e.getMessage());
            return null;
        }
    }

    protected JSONObject getJSONObject(JSONArray array, int index){
        try {
            return array.getJSONObject(index);
        } catch (JSONException e) {
            onSuccessFailed(e.getMessage());
            return null;
        }
    }
    protected JSONArray getJSONArray(JSONArray array, int index){
        try {
            return array.getJSONArray(index);
        } catch (JSONException e) {
            onSuccessFailed(e.getMessage());
            return null;
        }
    }

    abstract void handleSuccess(JSONObject cachedResponse);
}

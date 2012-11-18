package com.uzhnu.reedmuller.api;

import android.app.Activity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class MullerHandler extends BaseCacheHandler {
    private OnSuccessCompletedListener successCallback;

    public MullerHandler(Activity _activity, String _endpoint) {
        super(_activity, _endpoint);
    }

    @Override
    void handleSuccess(JSONObject cachedResponse) {
        LinkedHashMap<String, LinkedList<Integer>> hashMap = new LinkedHashMap<String, LinkedList<Integer>>();

        JSONArray matrix = getJSONArray(cachedResponse, "matrix");
        for(int i = 0; i < matrix.length(); i++){
            JSONArray bytes = getJSONArray(matrix, i);
            LinkedList<Integer> list = new LinkedList<Integer>();
            for(int j = 0; j < bytes.length(); j++){
                list.add(getInteger(bytes, j));
            }
            hashMap.put(("v" + i), list);
        }
        successCallback.onSuccessCompleted(hashMap);
    }


    public void setOnSuccessCompleted(OnSuccessCompletedListener callback) {
        this.successCallback = callback;
    }

    public interface OnSuccessCompletedListener {
        public void onSuccessCompleted(LinkedHashMap<String, LinkedList<Integer>> hashMap);
    }
}

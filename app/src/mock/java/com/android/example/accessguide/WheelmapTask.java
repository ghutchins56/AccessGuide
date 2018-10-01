package com.android.example.accessguide;

import android.os.AsyncTask;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class WheelmapTask extends AsyncTask<JSONArray, Void, String> {
    private LatLng LatLng;
    private WheelmapTaskListener Listener;
    private CountingIdlingResource IdlingResource;

    public interface WheelmapTaskListener {
        void setAccess(String status);
    }

    WheelmapTask(CountingIdlingResource resource) {
        IdlingResource = resource;
    }

    @Override
    protected String doInBackground(JSONArray... jsonArrays) {
        int index = 0;
        double distance = 0;
        JSONArray nodes = jsonArrays[0];
        try {
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                double lat = node.getDouble("lat");
                double lon = node.getDouble("lon");
                double d = Math.pow((Math.pow(lat - LatLng.latitude, 2)
                        + Math.pow(lon - LatLng.longitude, 2)), 0.5);
                if ((i == 0) || (d < distance)) {
                    index = i;
                    distance = d;
                }
            }
            String wheelchair = nodes.getJSONObject(index).getString("wheelchair");
            if (wheelchair == null) wheelchair = "";
            return wheelchair;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Listener.setAccess(s);
        if (IdlingResource != null) IdlingResource.decrement();
    }

    void setLatLng(LatLng latLng) {
        LatLng = latLng;
    }

    void setListener(WheelmapTaskListener listener) {
        Listener = listener;
    }
}

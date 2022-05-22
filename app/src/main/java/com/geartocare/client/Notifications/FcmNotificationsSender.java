package com.geartocare.client.Notifications;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {

    String userFcmToken;

    Context mContext;
    Activity mActivity;
    JSONObject notificationData;

    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAAiwLAlwo:APA91bGOIFIMmpdYQKZ_8b5DkKJ0H9El4OxBGjbgnap5U_0fYHNsUiuUffVQz_PGGfvU6VCknfp-eCmk2x3M918QYE6uvuuwjeQm9zrwwLIIHlK0i_xCbkoHtXq5WEwqf5x3TeJonYbi";

    public FcmNotificationsSender(String userFcmToken, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;

        this.mContext = mContext;
        this.mActivity = mActivity;


    }

    public String getUserFcmToken() {
        return userFcmToken;
    }

    public void setUserFcmToken(String userFcmToken) {
        this.userFcmToken = userFcmToken;
    }

    public JSONObject getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(String title, String body){
        notificationData = new JSONObject();
        try {
            notificationData.put("title", title);
            notificationData.put("body", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);

            notificationData.put("icon", "icon"); // enter icon that exists in drawable only
            mainObj.put("data", notificationData);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {


                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;


                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void SendNotificationsTo(JSONArray ids) {

        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("registration_ids", ids);
            notificationData.put("icon", "icon");
            mainObj.put("data", notificationData);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response
                    Toast.makeText(mContext, response.toString(), Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {


                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;


                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

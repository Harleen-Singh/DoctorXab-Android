package com.example.doc_app_android.services;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.doc_app_android.Dialogs.dialogs;
import com.example.doc_app_android.Globals;
import com.example.doc_app_android.data_model.AppointmentData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppointmentService {
    private MutableLiveData<ArrayList<AppointmentData>> data_model;
    private Application app;
    private ArrayList<AppointmentData> appointmentData = new ArrayList<>();

    public LiveData<ArrayList<AppointmentData>> getApmntData(Application app) {
        this.app = app;
        if (data_model == null) {
            data_model = new MutableLiveData<>();
            loadData();
        }
        return data_model;
    }

    private void loadData() {
        final RequestQueue requestQueue = Volley.newRequestQueue(app.getApplicationContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Globals.appointment, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                        for(int i=0 ; i < response.length() ; i++){
                            String id, date, patient_id , doctor_id ;
                            JSONObject obj = response.getJSONObject(i);
                            id = obj.getString("id");
                            date = obj.getString("date");
                            patient_id = obj.getString("patient");
                            doctor_id = obj.getString("doctor");
                            appointmentData.add(new AppointmentData(id,date,patient_id,doctor_id));
                            Log.e("", "onResponse: "+obj );
                        }
                        data_model.setValue(appointmentData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(app,"Error Response : "+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences pref = app.getApplicationContext().getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s",pref.getString("username", ""),pref.getString("pass", ""));
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
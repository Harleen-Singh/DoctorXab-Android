package com.example.doc_app_android.services;

import android.app.Application;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doc_app_android.Adapter.ReportData;
import com.example.doc_app_android.Dialogs.dialogs;
import com.example.doc_app_android.Globals;
import com.example.doc_app_android.data_model.ProfileData;
import com.example.doc_app_android.databinding.LoadingDialogBinding;
import com.example.doc_app_android.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReportService {

    private int id, patientId;
    private String date, data, patient;
    private ReportData reportData1;
    private ReportData reportDataPatient;
    private MutableLiveData<ReportData> reportDataMutableLiveData;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String url;
    private Dialog dialog1;
    private LoadingDialogBinding loadingDialogBinding;
    private Application application;
    private Context context;
    private int report_id;


    public LiveData<ReportData> getReportData(Application application) {

        prefs = application.getApplicationContext().getSharedPreferences("tokenFile", Context.MODE_PRIVATE);

        url = Globals.report + prefs.getString("patient_id", "-1");
        Log.d("Testing", "Url for Patient history from doctor patient list: " + url);

        if (reportDataMutableLiveData == null) {
            reportDataMutableLiveData = new MutableLiveData<>();

            getReport(application, url);
        }
        return reportDataMutableLiveData;
    }

    public void updateReportData(Application application, ReportData reportData){
        prefs = application.getApplicationContext().getSharedPreferences("tokenFile", Context.MODE_PRIVATE);

        url = Globals.report + prefs.getString("patient_id", "-1");
        Log.d("Testing", "Url for Patient history from doctor patient list: " + url);
        editData(application, url, reportData, Request.Method.PUT);
    }


    public void editData(Context context, String url, ReportData reportData,  int method) {

        JSONObject writteData = null;
        try {
            writteData = new JSONObject();
            writteData.put("date", reportData.getDate());
            writteData.put("data", reportData.getData());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final RequestQueue updateReportRequestQueue = Volley.newRequestQueue(context);
        final JsonObjectRequest updateReportRequest = new JsonObjectRequest(method, url, writteData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Testing", "Updated Report: "+ response);
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                try {
                    String date = response.getString("date");
                    String data = response.getString("data");
                    int id = response.getInt("id");
                    report_id = id;
                    int patientId = response.getInt("patient");
                    reportData1 = new ReportData(id,patientId,date,data);

                    reportDataMutableLiveData.setValue(reportData1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("", "error.networkRespose.toString()" + error.networkResponse.toString());
                }
            }
        });
        updateReportRequestQueue.add(updateReportRequest);

        updateReportRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

    }

    public void getReport(Context context, String url) {

        final RequestQueue getReportRequestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest getReportRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    id = response.getInt("id");
                    date = response.getString("date");
                    data = response.getString("data");
                    patientId = response.getInt("patient");
                    reportData1 = new ReportData(id, patientId, date, data);
                    reportDataMutableLiveData.setValue(reportData1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                    dialogs.displayDialog("Not Connected to Internet", context);
                } else {
                    Log.d("", "error.networkRespose.toString()" + error.networkResponse.toString());
                }
            }
        });

        getReportRequestQueue.add(getReportRequest);

        getReportRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    public void addPatientReport(Application mApplication, Context mContext, com.example.doc_app_android.data_model.ReportData reportData, ReportData reportData1)  {

        context = mContext;
        application = mApplication;

        loadingDialogBinding = LoadingDialogBinding.inflate(LayoutInflater.from(mContext));
        loadingDialogBinding.getRoot().setBackgroundResource(android.R.color.transparent);
        dialog1 = new Dialog(context);
        dialog1.setCancelable(false);
        dialog1.setContentView(loadingDialogBinding.getRoot());
        loadingDialogBinding.updateProgress.setVisibility(View.VISIBLE);
        dialog1.show();
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        sp = context.getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
//        id = Integer.parseInt(sp.getString("doctor_id", ""));
        addNewReport(context,reportData1,reportData);


//        try {
//            uploaddatatodb(reportData);
//        } catch (IOException e){
//            Log.d("Testing", e.toString());
//        }


       // addXrayImage("hjs", reportData, mContext);
//        addReport("hsj", context);

    }
    private String encodeBitmapImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
    }

    private void uploaddatatodb(com.example.doc_app_android.data_model.ReportData reportData) throws IOException {

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), reportData.getUri());
        String encodedImage =  encodeBitmapImage(bitmap);
        StringRequest request=new StringRequest(Request.Method.POST, Globals.addNewXray, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Log.d("Testing", "Output From Add X-ray API Service: " + response.toString());
                dialog1.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Testing", "Error From Add X-ray API Service: " + error.toString());
                Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                dialog1.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String,String> params=new HashMap<String, String>();
                params.put("pic_id", reportData.getXray_id());
//                params.put("time", reportData.getTime());
//                params.put("date", reportData.getDate());
                params.put("report", String.valueOf(report_id));
                params.put("patient",  String.valueOf(reportData.getPatientId()));
                params.put("category", reportData.getCategory());
                Log.d("Testing", "Image Being sent: " + encodedImage);
               // params.put("image", encodedImage);
                return params;
            }
        };


        RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }


    public void uploadImage(com.example.doc_app_android.data_model.ReportData reportData) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Globals.addNewXray,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(context, "UploadedSuccessfully", Toast.LENGTH_SHORT).show();
                        Log.d("Testing", "Upload Image response" + response);
                        Log.d("Testing", "Upload Image response" + Arrays.toString(response.data));

                        Log.d("Testing", "Uploading Image Successful");
                        prefs = context.getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
                        editor = prefs.edit();
                        editor.putString("X-RAY-ID-Report","");
                        editor.putString("X-RAY-DATE-REPORT", "");
                        editor.putString("X-RAY-TIME-REPORT", "");
                        editor.putString("X-RAY-CATEGORY-REPORT", "");
                        editor.putString("X-RAY-BODYAREA-REPORT", "");
                        editor.putString("X-RAY-REPORT-REPORT", "");
                        editor.putString("X-RAY-IMAGE-REPORT", "");
                        editor.apply();
                        dialog1.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                        dialog1.dismiss();
                        Log.d("Testing", "Uploading Image makes error");
                    }
                }) {


            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("pic_id", reportData.getXray_id());
                params.put("time", reportData.getTime());
                params.put("date", reportData.getDate());
                params.put("report", String.valueOf(report_id));
                params.put("patient",  String.valueOf(reportData.getPatientId()));
                params.put("category", reportData.getCategory());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws IOException {
                Map<String, DataPart> params = new HashMap<>();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), reportData.getUri());
                long imagename = System.currentTimeMillis();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
                params.put("image", new DataPart("xray" + imagename + ".jpg", bytesOfImage));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(context).add(volleyMultipartRequest);
    }

//    public void editData(com.example.doc_app_android.data_model.ReportData data) {
//
////        NavHeaderBinding binding = NavHeaderBinding.inflate(LayoutInflater.from(context));
//        JSONObject details = null;
//
//        try {
//            details = new JSONObject();
//            details.put("pic_id", data.getXray_id());
////            details.put("time", data.getTime());
////            details.put("date", data.getDate());
//            details.put("report", String.valueOf(report_id));
//            Log.d("Testing", "Report Id: " + report_id);
//            details.put("patient",  String.valueOf(data.getPatientId()));
//            details.put("category", data.getCategory());
//            //details.put("image", data.getImage());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        final RequestQueue detailsRequestQueue = Volley.newRequestQueue(context);
//
//        JsonObjectRequest sendEditDetailsRequest = new JsonObjectRequest(Request.Method.POST, Globals.addNewXray, details, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("Testing", "Output From Report Add Service: " + response.toString());
//
//
////                    binding.navProfileName.setText(name);
////                    Picasso.get().load(image).placeholder(R.drawable.doctor_profile_image).into(binding.navProfileImage);
//
//                //int doctor_Id, int age, String userName, String email, String name, String phoneNumber, String address, String image
//                loadingDialogBinding.updateProgress.setVisibility(View.GONE);
//                dialog1.dismiss();
//                Toast.makeText(context, "UploadedSuccessfully", Toast.LENGTH_SHORT).show();
//                Log.d("Testing", "Uploading X-ray data Successful");
//
//
//                //dialogs.dismissDialog(progressDialog);
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dialog1.dismiss();
//                Log.d("Testing", "Uploading x-ray data error");
//
//                if (error instanceof NoConnectionError) {
//                    //dialogs.displayDialog("Not Connected to Internet", context);
//
//                }
//                if (error instanceof ServerError) {
//                    Toast.makeText(context, "ServiceError: " + error, Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.d("", "error.networkRespose.toString()" + error.networkResponse.toString());
//                }
//
//                //dialogs.dismissDialog(progressDialog);
//                //dialogs.dismissDialog(progressDialog);
//
//            }
//        });
////        {
////            @Nullable
////            @org.jetbrains.annotations.Nullable
////            @Override
////            protected Map<String, String> getParams() throws AuthFailureError {
////
////                Map<String, String> map = new HashMap<>();
////                map.put("image", editedProfileData.getImage());
////                return map;
////            }
////        };
//
//
//        detailsRequestQueue.add(sendEditDetailsRequest);
//
//        sendEditDetailsRequest.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 50000;
//            }
//
//            @Override
//            public int getCurrentRetryCount() {
//                return 50000;
//            }
//
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//
//            }
//        });
//
//
//    }


    public void addNewReport(Context context, ReportData reportData1, com.example.doc_app_android.data_model.ReportData reportData) {

        JSONObject writteData = null;
        try {
            writteData = new JSONObject();
            writteData.put("patient", String.valueOf(reportData.getPatientId()));
            writteData.put("date", reportData1.getDate());
            writteData.put("data", reportData1.getData());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final RequestQueue updateReportRequestQueue = Volley.newRequestQueue(context);
        final JsonObjectRequest updateReportRequest = new JsonObjectRequest(Request.Method.POST, Globals.addNewReport, writteData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Testing", "Updated Report: "+ response);
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                try {
                    String date = response.getString("date");
                    String data = response.getString("data");
                    int id = response.getInt("id");
                    report_id = id;
                    uploadImage(reportData);
                    //editData(reportData);
                    int patientId = response.getInt("patient");
                    reportDataPatient = new ReportData(id,patientId,date,data);

                    reportDataMutableLiveData.setValue(reportDataPatient);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("", "error.networkRespose.toString()" + error.networkResponse.toString());
                }
            }
        });
        updateReportRequestQueue.add(updateReportRequest);

        updateReportRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

    }







    // Do not delete this code, it's fro converting URI to Bitmap
    // Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
    //    }
}


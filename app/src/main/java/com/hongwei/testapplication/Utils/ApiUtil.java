package com.hongwei.testapplication.Utils;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.hongwei.testapplication.HongweitestApplication;
import com.hongwei.testapplication.LoginActivity;
import com.hongwei.testapplication.MainActivity;
import com.hongwei.testapplication.R;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class ApiUtil {
    public static void loginApi(LoginActivity loginActivity, String str_email, String str_password, CircularProgressButton btn_login){
        StringRequest myRequest = new StringRequest(
                Request.Method.POST,
                PrefConst.baseurl+"login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String json) {
                        Log.d("response=====", String.valueOf(json));
                        loginActivity.ParseloginResponse(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (/*error instanceof ServerError &&*/ response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                Log.d("errorres===", res);
                                JSONObject obj = new JSONObject(res);

                                if(obj.has("message")){
                                    String message = obj.getString("message");
                                    DynamicToast.makeError(loginActivity, message, 2500).show();
                                }else{
                                    btn_login.revertAnimation();
                                    DynamicToast.makeError(loginActivity, loginActivity.getString(R.string.errormessage), 2500).show();

                                }

                            } catch (UnsupportedEncodingException e1) {
                                DynamicToast.makeError(loginActivity, loginActivity.getString(R.string.errormessage), 2500).show();
                                e1.printStackTrace();
                            } catch (JSONException e2) {

                                DynamicToast.makeError(loginActivity, loginActivity.getString(R.string.errormessage), 2500).show();
                                e2.printStackTrace();
                            }
                        }
                        btn_login.revertAnimation();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", str_email);
                params.put("password", str_password);
                return params;
            }
        };
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        HongweitestApplication.getInstance().addToRequestQueue(myRequest, "tag");
    }

    public static void getbalanceApi(MainActivity mainActivity, String servertoken, ProgressBar progressBar){
        StringRequest myRequest = new StringRequest(
                Request.Method.GET,
                PrefConst.baseurl+"balance",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String json) {
                        Log.d("response=====", String.valueOf(json));
                        mainActivity.ParseBalanceResponse(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (/*error instanceof ServerError &&*/ response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                Log.d("errorres===", res);
                                JSONObject obj = new JSONObject(res);

                                if(obj.has("message")){
                                    String message = obj.getString("message");
                                    DynamicToast.makeError(mainActivity, message, 2500).show();
                                }else{
                                    DynamicToast.makeError(mainActivity, mainActivity.getString(R.string.errormessage), 2500).show();
                                }

                            } catch (UnsupportedEncodingException e1) {
                                DynamicToast.makeError(mainActivity, mainActivity.getString(R.string.errormessage), 2500).show();
                                e1.printStackTrace();
                            } catch (JSONException e2) {

                                DynamicToast.makeError(mainActivity, mainActivity.getString(R.string.errormessage), 2500).show();
                                e2.printStackTrace();
                            }

                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+servertoken);
                return params;
            }
        };
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        HongweitestApplication.getInstance().addToRequestQueue(myRequest, "tag");
    }

}

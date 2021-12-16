package com.hongwei.testapplication;

import static com.hongwei.testapplication.Utils.ApiUtil.loginApi;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;


import com.google.android.material.textfield.TextInputEditText;
import com.hongwei.testapplication.Utils.PrefConst;
import com.hongwei.testapplication.Utils.Preference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;


import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    CircularProgressButton btn_login;
    TextInputEditText etx_email, etx_password;
    CheckBox cb_saveinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(0,0);
        View relativeLayout=findViewById(R.id.login_container);
        Animation animation= AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        relativeLayout.startAnimation(animation);
        initializeviews();
    }

    private void initializeviews() {
        etx_email=(TextInputEditText) findViewById(R.id.etx_email);
        etx_password = (TextInputEditText)findViewById(R.id.etx_password);
        cb_saveinfo =(CheckBox)findViewById(R.id.cb_saveinfo);
        btn_login = (CircularProgressButton) findViewById(R.id.btn_login);

        String savedemailaddress = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USEREMAIL, "");
        String savedpassword = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERPWD, "");
        if(savedemailaddress.length()>0 && savedpassword.length()>0){
            cb_saveinfo.setChecked(true);
            etx_email.setText(savedemailaddress);
            etx_password.setText(savedpassword);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValid()){
                    //gotoMainactivity();
                    userlogin();
                }
            }
        });
    }
    
    private boolean checkValid(){
        if(etx_email.getText().toString().length()==0){
            String msg = getString(R.string.pleaseenteremail);
            etx_email.setError(msg);
            return false;
        }
        if(etx_password.getText().toString().length()==0){
            String msg = getString(R.string.pleaseenterpassword);
            etx_password.setError(msg);
            return false;
        }
        return true;
    }

    private void userlogin() {
        btn_login.startAnimation();
        loginApi(this, etx_email.getText().toString().trim(), etx_password.getText().toString(), btn_login);
    }

    public void ParseloginResponse(String json) {

        try {
            JSONObject response = new JSONObject(json);
            if(response.has(PrefConst.TOKEN_TYPE) && response.has(PrefConst.ACCESS_TOKEN) && response.has(PrefConst.REFRESH_TOKEN)){
                btn_login.doneLoadingAnimation(LoginActivity.this.getResources().getColor(R.color.white),  BitmapFactory.decodeResource(LoginActivity.this.getResources(), R.drawable.ic_done_white_48dp));

                if(cb_saveinfo.isChecked()){
                    Preference.getInstance().put(LoginActivity.this, PrefConst.PREFKEY_USEREMAIL, etx_email.getText().toString());
                    Preference.getInstance().put(LoginActivity.this, PrefConst.PREFKEY_USERPWD, etx_password.getText().toString());
                }else{
                    Preference.getInstance().put(LoginActivity.this, PrefConst.PREFKEY_USEREMAIL, "");
                    Preference.getInstance().put(LoginActivity.this, PrefConst.PREFKEY_USERPWD, "");
                }

                Preference.getInstance().put(LoginActivity.this, PrefConst.TOKEN_TYPE, response.getString(PrefConst.TOKEN_TYPE));
                Preference.getInstance().put(LoginActivity.this, PrefConst.ACCESS_TOKEN, response.getString(PrefConst.ACCESS_TOKEN));
                Preference.getInstance().put(LoginActivity.this, PrefConst.REFRESH_TOKEN, response.getString(PrefConst.REFRESH_TOKEN));
                gotoMainactivity();
            }else if(response.has("message")){
                String message = response.getString("message");
                DynamicToast.makeError(LoginActivity.this, message, 2500).show();
            }else{
                btn_login.revertAnimation();
                DynamicToast.makeError(LoginActivity.this, getString(R.string.errormessage), 2500).show();
            }

        } catch(JSONException e){
            e.printStackTrace();
            btn_login.revertAnimation();
            DynamicToast.makeError(LoginActivity.this, getString(R.string.errormessage), 2500).show();
        }
    }

    private void gotoMainactivity() {
        Intent intent1=new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
        finish();
    }
}
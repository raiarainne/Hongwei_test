package com.hongwei.testapplication;

import static com.hongwei.testapplication.Utils.ApiUtil.getbalanceApi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;


import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.appbar.AppBarLayout;
import com.hongwei.testapplication.Utils.PrefConst;
import com.hongwei.testapplication.Utils.Preference;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    TextView txv_balance;
    ProgressBar progressbar;
    LinearLayout lyt_balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFab = findViewById(R.id.flexible_example_fab);
        txv_balance = (TextView)findViewById(R.id.txv_balance);
        progressbar=(ProgressBar) findViewById(R.id.progressbar);
        lyt_balance =(LinearLayout)findViewById(R.id.lyt_balance);
        lyt_balance.setVisibility(View.INVISIBLE);
        

        Toolbar toolbar = (Toolbar) findViewById(R.id.flexible_example_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //onBackPressed();
                Toast.makeText(MainActivity.this, "Back Button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
        appbar.addOnOffsetChangedListener(this);

        getValance();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
            }
        }
    }

    public void start(View view) {
        Toast.makeText(MainActivity.this, "fab clicked", Toast.LENGTH_SHORT).show();
    }

    private void getValance(){
        String servertoken = Preference.getInstance().getValue(MainActivity.this, PrefConst.ACCESS_TOKEN, "");
        getbalanceApi(this, servertoken, progressbar);

    }

    public void ParseBalanceResponse(String json) {
        try {
            progressbar.setVisibility(View.INVISIBLE);
            JSONObject response = new JSONObject(json);
            if(response.has(PrefConst.BALANCE_DATA)){
                JSONObject data = response.getJSONObject(PrefConst.BALANCE_DATA);
                String balance = data.getString("balance");
                String currency = data.getString("currency");
                txv_balance.setText(balance+currency);
                lyt_balance.setVisibility(View.VISIBLE);
                
            }else if(response.has("message")){
                String message = response.getString("message");
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, getString(R.string.errormessage), Toast.LENGTH_SHORT).show();
            }

        } catch(JSONException e){
            e.printStackTrace();
            progressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.this, getString(R.string.errormessage), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        confirmclose();
    }

    public void confirmclose(){
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Caution!");
        builder.setMessage("Are you sure you want to close application now?");
        builder.setCancelable(false);
        AlertDialog finalAlertDialog = alertDialog;
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(finalAlertDialog != null) finalAlertDialog.dismiss();
                MainActivity.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(finalAlertDialog != null) finalAlertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
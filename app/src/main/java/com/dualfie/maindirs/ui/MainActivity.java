package com.dualfie.maindirs.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dualfie.maindirs.R;

public class MainActivity extends AppCompatActivity {
    int request_code = 1;
    String[] Permissions = new String[]{

            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION ,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button btn_master = findViewById(R.id.btn_master);
        btn_master.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent controlIntent = new Intent(getApplicationContext(), ControlActivity.class);
                finish();
                startActivity(controlIntent);
            }
        });

        Button btn_shutter = findViewById(R.id.btn_localcamera);
        btn_shutter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(getApplicationContext(), SlaveActivity.class);
                finish();
                startActivity(cameraIntent);
            }
        });
        locationPermissionCheck();


    }
    public void locationPermissionCheck(){


        if (!hasPermissions(this, Permissions)) {
            ActivityCompat.requestPermissions(this, Permissions, request_code);
        }


    }


    public static boolean hasPermissions(Context context, String... permissions) {

            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }

        return true;
    }




}

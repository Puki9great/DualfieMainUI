package com.dualfie.maindirs.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import com.dualfie.maindirs.R;

public class SlaveActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Camera2BasicFragment fragment = Camera2BasicFragment.newInstance();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }


    }
}
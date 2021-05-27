package com.dualfie.maindirs.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

public class SlaveActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Camera2BasicFragment fragment = Camera2BasicFragment.newInstance();
            //transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        /*
        XML :
        <LinearLayout
    android:id="@+id/sample_main_layout"
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <FrameLayout
        android:id="@+id/sample_content_fragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />

</LinearLayout>


         */
    }
}
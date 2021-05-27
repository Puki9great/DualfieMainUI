package com.dualfie.maindirs.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dualfie.maindirs.R;
import com.dualfie.maindirs.dconstants.Constants;
import com.dualfie.maindirs.helpers.EncryptionHelper;

import com.dualfie.maindirs.model.DeviceModel;
import com.dualfie.maindirs.model.MessageFormat;
import com.dualfie.maindirs.network.BluetoothComm;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class
ControlActivity extends AppCompatActivity implements View.OnClickListener {

    /*


     */
    private static final String TAG = "ControlActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private Handler mHandler;
    private BluetoothComm bluetoothMessageController;

    private EncryptionHelper mEncryption;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.master_image)
    ImageView display;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.start)
    Button mFetch;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.capture)
    Button mCapture;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.stop)
    Button mStop;


    private Bitmap mImage;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_main);
        ButterKnife.bind(this);
        boolean show = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish();
        }
        bluetoothMessageController = new BluetoothComm(this, mHandler);
        DeviceModel model = new DeviceModel();
        if (model.getPaired()) {
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(model.getAddress());
        } else { // not paired
        }
        bluetoothMessageController.connect(mDevice);


        mHandler = new Handler(new Handler.Callback() {
            JSONObject messageJSON = null;
            MessageFormat message2 = null;

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        Log.d(TAG, "State Change " + msg.arg1);

                        switch (msg.arg1) {
                            case Constants.STATE_CONNECTED:
                                setTitle(mDevice.getName());
                                break;
                            case Constants.STATE_CONNECTING:
                                setTitle("Connecting...");
                                break;
                            case Constants.STATE_LISTEN:
                            case Constants.STATE_NONE:
                                setTitle("Not connected to the Shutter");
                                break;
                        }
                        break;

                    case Constants.MESSAGE_WRITE:

                        break;


                    case Constants.MESSAGE_READ:

                        break;

                    case Constants.MESSAGE_DEVICE_OBJECT:
                        Log.d(TAG, "Message Device Object");
                        mDevice = msg.getData().getParcelable(Constants.DEVICE_OBJECT);
                        Toast.makeText(getApplicationContext(), "Connected to " + mDevice.getName(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.MESSAGE_TOAST:
                        Log.d(TAG, "Message Toast");
                        Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.MESSAGE_LOST:
                        Log.d(TAG, "Message Lost");
                        BluetoothComm.sleep(500);
                        Toast.makeText(getApplicationContext(), "Reconnected", Toast.LENGTH_SHORT).show();
                        bluetoothMessageController.connect(mDevice);
                        break;

                    case Constants.MESSAGE_FETCHING:
                        mImage = (Bitmap) msg.obj;
                        display.setImageBitmap(mImage);
                        Toast.makeText(ControlActivity.this, "Image Fetched", Toast.LENGTH_SHORT).show();
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
                return false;
            }
        });

        //JSON
        mCapture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                bluetoothMessageController.write(Constants.TAKE_PICTURE.getBytes());
            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bluetoothMessageController.write(Constants.STOP_CAMERA.getBytes());
            }
        });
        mFetch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bluetoothMessageController.write(Constants.FETCH.getBytes());
                bluetoothMessageController.readImage();
            }
        });

        // button save to gallery set visible when image is fetched
        // executes asyncTask for save to gallery

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        } else {
            bluetoothMessageController = new BluetoothComm(this, mHandler);
            bluetoothMessageController.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bluetoothMessageController != null) {
            if (bluetoothMessageController.getState() == Constants.STATE_NONE)
                bluetoothMessageController.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothMessageController != null)
            bluetoothMessageController.stop();
        mBluetoothAdapter.disable();

    }


    @Override
    public void onClick(View v) {

    }

    private static class FileTransferAsyncTask extends AsyncTask<Bitmap, Void, Void> {

        Context context;

        FileTransferAsyncTask(Context context) {
            this.context = context;
        }


        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            FileOutputStream fout = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/Dualfie");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = "CaptureNew";

                File outFile = new File(dir, fileName);
                fout = new FileOutputStream(outFile);
                bitmaps[0].compress(Bitmap.CompressFormat.JPEG,100,fout);
                fout.flush();
                fout.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(context, "Photo saved to gallery", Toast.LENGTH_SHORT).show();
        }
    }

}









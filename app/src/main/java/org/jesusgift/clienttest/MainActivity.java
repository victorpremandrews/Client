package org.jesusgift.clienttest;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jesusgift.clienttest.Helpers.MyUtility;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SampleTag";
    private static final int PERMISSION_READ_STORAGE = 124;
    Button btnStopSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStopSync = (Button) findViewById(R.id.btnStopService);
        if(btnStopSync != null)
            btnStopSync.setOnClickListener(this);

        //checking permissions and starting services
        checkPermission();
    }

    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    PERMISSION_READ_STORAGE);

        }else {
            initService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_READ_STORAGE:
                    if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        initService();
                    }
                    break;
        }
    }

    private void initService() {
        startService(new Intent(this, ClientService.class));
        hideLauncher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void hideLauncher() {
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        Toast.makeText(this, R.string.app_closed, Toast.LENGTH_SHORT).show();
        this.finish();
        System.exit(0);
    }

    /**
     * OnClick Handler
     * @param v View
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStopService:
                Log.d(TAG, "Onclick");
                stopService(new Intent(this, ClientService.class));
                break;
        }
    }
}

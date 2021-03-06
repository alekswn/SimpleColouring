package io.novikov.simplecolouring;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.View;

public class ShaderedCamera extends Activity {
    private final String TAG = "ShaderedCamera";
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    RenderView mRenderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.LogDebug(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int ui = getWindow().getDecorView().getSystemUiVisibility();
        ui = ui | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(ui);

        setContentView(R.layout.main_layout);
        mRenderView = (RenderView)findViewById(R.id.render_view);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
    }

    @Override
    protected void onResume()
    {
        Util.LogDebug(TAG, "onResume()");
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        mRenderView.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
    }

    @Override
    protected void onPause()
    {
        Util.LogDebug(TAG, "onPause()");
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mRenderView.onPause();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
    }

}

package com.sxt.chart.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import com.sxt.chart.R;
import com.sxt.chart.view.GLSurfaceViewRenderer;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends BaseActivity {

    private GLSurfaceView glSurfaceView;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGL();
        initBottomSheet();
    }

    private void initGL() {
        if (checkSurport()) {
            glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surfaceView);
            GLSurfaceViewRenderer renderer = new GLSurfaceViewRenderer();
            glSurfaceView.setRenderer(renderer);
        } else {
            try {
                throw (new Throwable("当前设备不支持GLSurfaceView"));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private boolean checkSurport() {
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        boolean isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"));

        return supportsEs2 || isEmulator;
    }

    private void initBottomSheet() {
        final BottomSheetBehavior<View> sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        findViewById(R.id.action_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glSurfaceView != null) glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) glSurfaceView.onPause();
    }
}

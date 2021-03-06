package android.rockchip.update.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.os.Handler;

public class OtaUpdateNotifyActivity extends Activity {
    private String TAG = "OtaUpdateNotifyActivity";
    private String mRemoteURI = null;
    private String mOtaPackageVersion = null;
    private String mSystemVersion = null;
    private String mOtaPackageName = null;
    private String mOtaPackageLength = null;
    private Context mContext;
    private Handler mAutoHandler;
    private Runnable mAutoRunnable;
    private long mAutoStartTime = 0;
    private int mAutoStartDelay = 15000;
    private int mAutoStartRefreshPeriod = 1000;

    protected void onCreate(Bundle savedInstanceState) {
        Log.d("OTAUPDATENOTIFyACTIVITY", "ON_CREATE");
	super.onCreate(savedInstanceState);
        mContext = this;
        //requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.notify_dialog);
        //getWindow().addFlags(LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
        Log.d("OTAUPDATENOTIFyACTIVITY", getWindow().toString());
	//getWindow().addFlags(
	//getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
         //   android.R.drawable.ic_dialog_alert);
        Intent startIntent = getIntent();
        mRemoteURI = startIntent.getStringExtra("uri");
        mOtaPackageVersion = startIntent.getStringExtra("OtaPackageVersion");
        mSystemVersion = startIntent.getStringExtra("SystemVersion");
        mOtaPackageName = startIntent.getStringExtra("OtaPackageName");
        mOtaPackageLength = startIntent.getStringExtra("OtaPackageLength");
        long packageSize = Long.valueOf(mOtaPackageLength).longValue();
        String packageSize_string = null;
        if (packageSize < 1024) {
            packageSize_string = String.valueOf(packageSize) + "B";
        } else if (packageSize / 1024 > 0 && packageSize / 1024 / 1024 == 0) {
            packageSize_string = String.valueOf(packageSize / 1024) + "K";
        } else if (packageSize / 1024 / 1024 > 0) {
            packageSize_string = String.valueOf(packageSize / 1024 / 1024) + "M";
        }

        TextView txt = (TextView) findViewById(R.id.notify);
        txt.setText(
            getString(R.string.ota_update) + mOtaPackageVersion +
            getString(R.string.ota_package_size) + packageSize_string);
        final Button btn_ok = (Button) findViewById(R.id.button_ok);
        Button btn_cancel = (Button) findViewById(R.id.button_cancel);

        // Automatic perform upgrade first version
        // after mAutoStartDelay seconds
        mAutoHandler  = new Handler();
        mAutoRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = (System.nanoTime() - mAutoStartTime) / 1000000;
                if (elapsed > mAutoStartDelay) {
                    btn_ok.performClick();
                } else {
                    int secRemaining = (int) Math.round((mAutoStartDelay-elapsed)/1000);
                    btn_ok.setText(getString(R.string.IFIA_btn_yes) + " (" + Integer.toString(secRemaining) + ")");
                    mAutoHandler.postDelayed(mAutoRunnable, mAutoStartRefreshPeriod);
                }
            }
        };

        if (mSystemVersion.trim().equals("1.0.0")) {
            mAutoStartTime = System.nanoTime();
            mAutoHandler.postDelayed(mAutoRunnable, mAutoStartRefreshPeriod);
        }

        btn_ok.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PackageDownloadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("uri", mRemoteURI);
                intent.putExtra("OtaPackageLength", mOtaPackageLength);
                intent.putExtra("OtaPackageName", mOtaPackageName);
                intent.putExtra("OtaPackageVersion", mOtaPackageVersion);
                intent.putExtra("SystemVersion", mSystemVersion);
                mContext.startActivity(intent);
                Log.d("OTAUPDATENOTIFyACTIVITY","finish b");
                mAutoHandler.removeCallbacks(mAutoRunnable);
                finish();
            }
        });

        Log.d("OTAUPDATENOTIFyACTIVITY","REQUESTING FOCUS FOR BTN");
        btn_cancel.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View v) {
                mAutoHandler.removeCallbacks(mAutoRunnable);
                finish();
            }
        });
    }

    protected void onStop() {
        mAutoHandler.removeCallbacks(mAutoRunnable);
        finish();
        super.onStop();
    }
}

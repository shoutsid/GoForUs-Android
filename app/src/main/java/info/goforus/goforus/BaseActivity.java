package info.goforus.goforus;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.google.android.gms.location.LocationSettingsStates;

import info.goforus.goforus.settings.Gps;
import info.goforus.goforus.settings.PermissionsHandler;
import info.goforus.goforus.tasks.LocationUpdatesTask;

public abstract class BaseActivity extends AppCompatActivity {
    public Application mApplication = Application.getInstance();
    private LocationUpdatesTask mLocationUpdatesTask;

    /* =========================== Class Overrides =========================== */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication.setCurrentActivity(this);

        if (!Gps.turnedOn()) {
            PermissionsHandler.checkGpsPermissions(this);
        } else {
            if (mLocationUpdatesTask == null)
                mLocationUpdatesTask = new LocationUpdatesTask();
            if (!mLocationUpdatesTask.running)
                mLocationUpdatesTask.start();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApplication.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        if (mLocationUpdatesTask != null && mLocationUpdatesTask.running)
            mLocationUpdatesTask.stop();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            case PermissionsHandler.GPS_PERMISSIONS_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All is good, let's continue
                        break;
                    case Activity.RESULT_CANCELED:
                        PermissionsHandler.getInstance().alertUserGpsIsRequired(this);
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionsHandler.GPS_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PermissionsHandler.gpsPermissionGranted();
                } else {
                    PermissionsHandler.alertUserGpsIsRequired(this);
                }
                return;
            }
        }
    }

    /* =========================== Class Specific =========================== */
    private void clearReferences() {
        Activity currActivity = mApplication.getCurrentActivity();
        if (this.equals(currActivity)) {
            mApplication.setCurrentActivity(null);
        }
    }

    /* ============================ Layout Related =========================== */
    public int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }
}

package info.goforus.goforus.tasks;

import android.os.Handler;
import com.orhanobut.logger.Logger;

import info.goforus.goforus.apis.Utils;
import info.goforus.goforus.apis.listeners.NearbyDriversResponseListener;
import info.goforus.goforus.models.accounts.Account;

public class DriverUpdatesTask implements Runnable {
    private static final String TAG = "DriverUpdatesTask";

    private static Handler mHandler;
    private static int mInterval;
    private final NearbyDriversResponseListener mListener;

    public DriverUpdatesTask(Handler handler, int intervalTimeInMilliseconds,
                             NearbyDriversResponseListener listener) {
        mHandler = handler;
        mInterval = intervalTimeInMilliseconds;
        mListener = listener;
    }

    @Override
    public void run(){
        Account account = Account.currentAccount();
        if (account != null) {
            Logger.d("getting nearby drivers");
            try {
                Utils.LocationApi.getNearbyDrivers(account.lat, account.lng, mListener);
            } catch (Exception e) {
                Logger.e(e.toString());
            }
        }
        mHandler.postDelayed(this, mInterval);
    }
}

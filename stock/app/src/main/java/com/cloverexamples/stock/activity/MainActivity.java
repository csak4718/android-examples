package com.cloverexamples.stock.activity;

import android.accounts.Account;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v3.order.OrderConnector;
import com.cloverexamples.stock.R;
import com.cloverexamples.stock.service.OrderListenService;
import com.cloverexamples.stock.utils.Constant;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static Activity mainActivity; // used in SettingActivity
    private Account mAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        setupAccount();


        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean doListen = mPref.getBoolean(Constant.PREF_DO_LISTEN, true);
        Log.d(TAG, "doListen: " + String.valueOf(doListen));
        Log.d(TAG, "service running: " + String.valueOf(isMyServiceRunning(OrderListenService.class)));
        if (doListen && !isMyServiceRunning(OrderListenService.class)) {
            startService(new Intent(this, OrderListenService.class));
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();


    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        Log.d(TAG, "after destroy, service running: " + String.valueOf(isMyServiceRunning(OrderListenService.class)));
        super.onDestroy();
    }

    private void setupAccount() {
        if (mAccount == null) {
            Log.d(TAG, "mAccount is null. call getAccount");
            mAccount = CloverAccount.getAccount(this);

            if (mAccount == null) {
                Toast.makeText(this, Constant.ERROR_NO_ACCOUNT, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static Activity getMainActivity() {
        return mainActivity;
    }
}

package com.cloverexamples.stock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.clover.sdk.v1.Intents;
import com.cloverexamples.stock.service.StockAPIService;
import com.cloverexamples.stock.service.StockService;
import com.cloverexamples.stock.utils.Constant;

/**
 * Created by dewei.kung on 6/10/16.
 */
public class CloverBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = CloverBroadcastReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (mPref.getBoolean(Constant.PREF_DO_LISTEN, true)) {
            Intent it = new Intent(context, StockService.class);
            it.putExtra(Intents.EXTRA_CLOVER_ORDER_ID, intent.getStringExtra(Intents.EXTRA_CLOVER_ORDER_ID));
            context.startService(it);
        }
    }


}

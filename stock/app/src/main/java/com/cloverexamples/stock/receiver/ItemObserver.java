package com.cloverexamples.stock.receiver;

/**
 * Created by dewei.kung on 6/29/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.clover.sdk.v1.Intents;
import com.cloverexamples.stock.loader.ItemListLoader;

/**
 * Used by the ItemListLoader. An observer that listens for
 * item created, item deleted, quantity updates (and notifies the loader when
 * these changes are detected).
 */
public class ItemObserver extends BroadcastReceiver {
    private static final String TAG = ItemObserver.class.getSimpleName();
    private static final boolean DEBUG = true;

    private ItemListLoader mLoader;

    public ItemObserver() {

    }

    public ItemObserver(ItemListLoader loader) {
        if (DEBUG) {
            Log.i(TAG, "Create an observer object");
            if (loader == null) {
                Log.w(TAG, "loader arg is null");
            }
        }
        mLoader = loader;

//        TODO: item created and deleted
        // Register for events related to item updates.
        IntentFilter filter = new IntentFilter(Intents.ACTION_PAYMENT_PROCESSED);
//        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
//        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("item");
        mLoader.getContext().registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEBUG) {
            Log.i(TAG, "+++ The observer has detected a change! Notifying Loader... +++");
        }

        // Tell the loader about the change.
        mLoader.onContentChanged();
    }
}
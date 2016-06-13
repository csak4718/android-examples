package com.cloverexamples.stock.service;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v3.inventory.InventoryConnector;
import com.clover.sdk.v3.inventory.Item;
import com.clover.sdk.v3.inventory.ItemStock;
import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;
import com.cloverexamples.stock.R;
import com.cloverexamples.stock.activity.MainActivity;
import com.cloverexamples.stock.utils.Constant;

import java.util.List;

/**
 * Created by dewei.kung on 6/13/16.
 */
public class StockAPIService extends Service {
    private static final String TAG = StockAPIService.class.getSimpleName();
    private OrderConnector mOrderConnector;
    private Account mAccount;
    private InventoryConnector mInventoryConnector;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        setupAccount();

        connect();




        new DataAsyncTask().execute(intent.getStringExtra(Intents.EXTRA_CLOVER_ORDER_ID));


        return super.onStartCommand(intent, flags, startId);
//        return START_NOT_STICKY;
//        return START_STICKY;
    }

    private class DataAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... orderId) {
            try {
                Order order = mOrderConnector.getOrder(orderId[0]);
                List<LineItem> lineItems = order.getLineItems();
                for (LineItem lineItem: lineItems) {
                    String itemId = lineItem.getItem().getId();
                    Item item = mInventoryConnector.getItem(itemId);
                    int orderedQty = lineItem.getUnitQty();
                    Log.d(TAG, "orderedQty: " + String.valueOf(orderedQty));

                    ItemStock itemStock = item.getItemStock();
                    if (itemStock.hasQuantity()) {
                        Log.d(TAG, "new Qty: " + String.valueOf(itemStock.getQuantity() - orderedQty));
                        itemStock.setQuantity(itemStock.getQuantity() - orderedQty);
                    } else {
                        itemStock.setQuantity(-1.0 * orderedQty);
                    }

//                    TODO: send notif only when meets spec
                    if (itemStock.getQuantity() <= 2.0) {
                        sendNotification(lineItem.getName());
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (BindingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            releaseResource();
            stopSelf();
        }
    }

    private void setupAccount() {
        if (mAccount == null) {
            Log.d(TAG, "mAccount is null. call getAccount");
            mAccount = CloverAccount.getAccount(this);

            if (mAccount == null) {
                Toast.makeText(this, Constant.ERROR_NO_ACCOUNT, Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        }
    }

    private void connect() {
        disconnect();
        if (mAccount != null) {
            mOrderConnector = new OrderConnector(this, mAccount, null);
            mOrderConnector.connect();
            mInventoryConnector = new InventoryConnector(this, mAccount, null);
            mInventoryConnector.connect();
        }
    }

    private void disconnect() {
        if (mOrderConnector != null) {
            mOrderConnector.disconnect();
            mOrderConnector = null;
        }
        if (mInventoryConnector != null) {
            mInventoryConnector.disconnect();
            mInventoryConnector = null;
        }
    }

    private void releaseResource() {
        disconnect();
        mAccount = null;
        stopSelf();
    }

    private void sendNotification(String lineItemName) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("Clover notification")
                        .setContentText(lineItemName);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "Clover Notify");
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

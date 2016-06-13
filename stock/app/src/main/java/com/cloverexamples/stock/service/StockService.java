package com.cloverexamples.stock.service;

import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
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
import com.clover.sdk.util.CloverAuth;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by dewei.kung on 6/13/16.
 */
public class StockService extends Service {
    private static final String TAG = StockService.class.getSimpleName();
    private OrderConnector mOrderConnector;
    private Account mAccount;
    private InventoryConnector mInventoryConnector;
    private CloverAuth.AuthResult mCloverAuth;
    private String baseUrl = "https://sandbox.dev.clover.com/v3/merchants/";
    private String itemStockUrl = "/item_stocks/";
    private int counter = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        setupAccount();

        connect();

//        String orderId = intent.getStringExtra(Intents.EXTRA_CLOVER_ORDER_ID);
//        try {
//            // Use mAccount to get the access token (and other Clover authentication data)
//            mCloverAuth = CloverAuth.authenticate(StockService.this, mAccount);
//            Log.d(TAG, mCloverAuth.authToken);
//
//            final Map<String, Integer> idToCount = new HashMap<>();
//            Order order = mOrderConnector.getOrder(orderId);
//            List<LineItem> lineItems = order.getLineItems();
//            for (LineItem lineItem: lineItems) {
//                String itemId = lineItem.getItem().getId();
//                if (idToCount.containsKey(itemId)) {
//                    idToCount.put(itemId, idToCount.get(itemId) + 1);
//                } else {
//                    idToCount.put(itemId, 1);
//                }
//            }
//            counter = idToCount.size();
//
//            for (final String itemId: idToCount.keySet()) {
//                final String url = baseUrl + mCloverAuth.merchantId + itemStockUrl + itemId;
//
//                AsyncHttpClient getClient = new AsyncHttpClient();
//                getClient.addHeader(Constant.HTTP_HEADER_KEY_AUTH, Constant.HTTP_HEADER_VAL_AUTH + mCloverAuth.authToken);
//                getClient.get(url, new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        postRequest(responseBody, itemId, url, idToCount);
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                    }
//                });
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (ClientException e) {
//            e.printStackTrace();
//        } catch (ServiceException e) {
//            e.printStackTrace();
//        } catch (BindingException e) {
//            e.printStackTrace();
//        } catch (OperationCanceledException e) {
//            e.printStackTrace();
//        } catch (AuthenticatorException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        new DataAsyncTask().execute(intent.getStringExtra(Intents.EXTRA_CLOVER_ORDER_ID));


        return super.onStartCommand(intent, flags, startId);
//        return START_NOT_STICKY;
//        return START_STICKY;
    }



    private class DataAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... orderId) {
            try {
                // Use mAccount to get the access token (and other Clover authentication data)
                mCloverAuth = CloverAuth.authenticate(StockService.this, mAccount);
                Log.d(TAG, mCloverAuth.authToken);

                final Map<String, Integer> idToCount = new HashMap<>();
                Order order = mOrderConnector.getOrder(orderId[0]);
                List<LineItem> lineItems = order.getLineItems();
                for (LineItem lineItem: lineItems) {
                    String itemId = lineItem.getItem().getId();
                    if (idToCount.containsKey(itemId)) {
                        idToCount.put(itemId, idToCount.get(itemId) + 1);
                    } else {
                        idToCount.put(itemId, 1);
                    }
                }
                counter = idToCount.size();

                for (final String itemId: idToCount.keySet()) {
                    final String url = baseUrl + mCloverAuth.merchantId + itemStockUrl + itemId;

                    SyncHttpClient getClient = new SyncHttpClient();
                    getClient.addHeader(Constant.HTTP_HEADER_KEY_AUTH, Constant.HTTP_HEADER_VAL_AUTH + mCloverAuth.authToken);
                    getClient.get(url, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d(TAG, "Http GET onSuccess");
                            postRequest(response, itemId, url, idToCount);
                        }


                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {

                        }
                    });

//                    getClient.get(url, new AsyncHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                            postRequest(responseBody, itemId, url, idToCount);
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                        }
//                    });
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (BindingException e) {
                e.printStackTrace();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            finishService();
        }
    }

    private void postRequest(JSONObject itemStock, final String itemId, String url, Map<String, Integer> idToCount) {
        try {
//            JSONObject itemStock = new JSONObject(new String(responseBody));
            final int newQty = itemStock.has(Constant.JSON_QUANTITY)
                    ? itemStock.getInt(Constant.JSON_QUANTITY) - idToCount.get(itemId)
                    : - idToCount.get(itemId);
            Log.d(TAG, "newQty: " + String.valueOf(newQty));

            SyncHttpClient postClient = new SyncHttpClient();
            postClient.addHeader(Constant.HTTP_HEADER_KEY_AUTH, Constant.HTTP_HEADER_VAL_AUTH + mCloverAuth.authToken);
            postClient.addHeader(Constant.HTTP_HEADER_KEY_CONTENT_TYPE, Constant.HTTP_HEADER_VAL_CONTENT_TYPE);


            JSONObject itemJson = new JSONObject();
            itemJson.put(Constant.JSON_ITEM_ID, itemId);

            JSONObject postJson = new JSONObject();
            postJson.put(Constant.JSON_ITEM, itemJson);
            postJson.put(Constant.JSON_QUANTITY, newQty);

            StringEntity entity = new StringEntity(postJson.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, Constant.HTTP_HEADER_VAL_CONTENT_TYPE));

            postClient.post(StockService.this, url, entity, Constant.HTTP_HEADER_VAL_CONTENT_TYPE, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "Http POST onSuccess: " + itemId);
                    try {
                        counter--;
                        if (newQty < 4) {
                            sendNotification(mInventoryConnector.getItem(itemId).getName(), newQty);
                        }
                        if (counter == 0) {
                            finishService();
                        }
                    } catch (ClientException e) {
                        e.printStackTrace();
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    } catch (BindingException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {

                }
            });

//            postClient.post(StockService.this, url, entity, Constant.HTTP_HEADER_VAL_CONTENT_TYPE, new AsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                    Log.d(TAG, "Http POST onSuccess: " + itemId);
//
//                    try {
//                        counter--;
//                        if (newQty < 4) {
//                            sendNotification(mInventoryConnector.getItem(itemId).getName(), newQty);
//                        }
//                        if (counter == 0) {
//                            finishService();
//                        }
//                    } catch (ClientException e) {
//                        e.printStackTrace();
//                    } catch (ServiceException e) {
//                        e.printStackTrace();
//                    } catch (BindingException e) {
//                        e.printStackTrace();
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                }
//            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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

    private void finishService() {
        Log.d(TAG, "finishService");
        disconnect();
        mAccount = null;
        stopSelf();
    }

    private void sendNotification(String itemName, int newQty) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(itemName)
                        .setContentText("only " + String.valueOf(newQty) + " left!");
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

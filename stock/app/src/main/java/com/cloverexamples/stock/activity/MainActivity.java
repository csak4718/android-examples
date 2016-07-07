package com.cloverexamples.stock.activity;

import android.accounts.Account;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clover.sdk.util.CloverAccount;
import com.cloverexamples.stock.R;
import com.cloverexamples.stock.adapter.ItemListAdapter;
import com.cloverexamples.stock.entry.ItemEntry;
import com.cloverexamples.stock.loader.ItemListLoader;
import com.cloverexamples.stock.utils.Constant;
import com.cloverexamples.stock.utils.Utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ItemEntry>> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Account mAccount;
    private ActionBar mActionBar;

    private ItemListAdapter mAdapter;
    @Bind (R.id.list_view) ListView mListView;
    ImageButton btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupActionBar();
        setupAccount();

        setupAdapter();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(R.layout.main_activity_action_bar);
        View actionBarView = mActionBar.getCustomView();
        TextView txvTitle = (TextView) actionBarView.findViewById(R.id.txv_action_bar_title);
        txvTitle.setText(Constant.TEXT_STOCK);

        btnSetting = (ImageButton) findViewById(R.id.btn_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoSettingActivity(MainActivity.this);
            }
        });
    }

    private void setupAdapter() {
        mAdapter = new ItemListAdapter(this, new ArrayList<ItemEntry>());
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        if (isNetworkAvailable()) {
            // Initialize a Loader with id 'Constant.MAIN_ACTIVITY_LOADER_ID'. If the Loader with this id already
            // exists, then the LoaderManager will reuse the existing Loader.
            getSupportLoaderManager().initLoader(Constant.MAIN_ACTIVITY_LOADER_ID, null, this);
        } else {
            Toast.makeText(this, "Internet not found! Please Check connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAccount() {
        if (mAccount == null) {
            mAccount = CloverAccount.getAccount(this);

            if (mAccount == null) {
                Toast.makeText(this, Constant.ERROR_NO_ACCOUNT, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public Loader<List<ItemEntry>> onCreateLoader(int id, Bundle args) {
        return new ItemListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<ItemEntry>> loader, List<ItemEntry> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<ItemEntry>> loader) {
        mAdapter.setData(null);
    }
}

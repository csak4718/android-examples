package com.cloverexamples.stock.utils;

import android.app.Activity;
import android.content.Intent;

import com.cloverexamples.stock.activity.SettingActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by dewei.kung on 6/13/16.
 */
public class Utils {
    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void gotoSettingActivity(Activity activity) {
        Intent it = new Intent(activity, SettingActivity.class);
        activity.startActivity(it);
    }
}

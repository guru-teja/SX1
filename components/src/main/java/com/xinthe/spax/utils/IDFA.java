package com.xinthe.spax.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

/**
 * Created by xinthe on 16-Nov-16.
 */
public class IDFA extends AsyncTask<Void, Void, String> {

        Context context;
        static boolean isIDFAAvailable;
        public static String idfa;

        public IDFA(Context context)
        {
            this.context = context;
            isIDFAAvailable = false;

        }

        public static boolean isIDFAAvailable()
        {
            return isIDFAAvailable;
        }

        public String getIDFA()
        {
            return idfa;
        }
        @Override
        protected String doInBackground(Void... params) {
            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient
                        .getAdvertisingIdInfo(context);
            } catch (IllegalStateException e) {
                Log.e("GMS", e.getMessage());
            } catch (GooglePlayServicesRepairableException e) {
                Log.e("GMS", e.getMessage());
            } catch (IOException e) {
                Log.e("GMS", e.getMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e("GMS", e.getMessage());
            }
            String info;
            if (adInfo == null) {
                info = "";
            } else {
                info = adInfo.getId();
            }
            return info;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            idfa = result;
            isIDFAAvailable = true;
        }


}

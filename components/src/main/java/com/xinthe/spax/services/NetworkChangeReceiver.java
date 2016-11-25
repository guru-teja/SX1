package com.xinthe.spax.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.xinthe.spax.R;
import com.xinthe.spax.utils.Utils;

/**
 * @author Koti
 *
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (!wifi.isConnected() && !mobile.isConnected()) {
			Log.e("NetworkReceiver", "No");
			Utils.setNetworkAvailabilityStatus(context, false);
			Intent conIntent = new Intent(
					context.getString(R.string.connection_changes));
			conIntent.putExtra("network", false);
			LocalBroadcastManager.getInstance(context).sendBroadcast(conIntent);
		} else if (wifi.isConnected() || mobile.isConnected()) {
			Log.e("NetworkReceiver", "Yes");
			Utils.setNetworkAvailabilityStatus(context, true);
			Intent conIntent = new Intent(
					context.getString(R.string.connection_changes));
			conIntent.putExtra("network", true);
			LocalBroadcastManager.getInstance(context).sendBroadcast(conIntent);
		}

	}
}

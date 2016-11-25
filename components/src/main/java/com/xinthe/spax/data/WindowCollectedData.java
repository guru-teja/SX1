package com.xinthe.spax.data;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.xinthe.spax.utils.IDFA;
import com.xinthe.spax.utils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Koti 
 * Format meta data and collected beacons data in single object to
 * form JSON
 */
public class WindowCollectedData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String mac = "", udid = "", idfa = "", idfv = "", ip_address = "",
			method = "", version = "", timestamp = "";
	public HashMap<String, String> location;
	public ArrayList<BeaconsCollectedData> beacons;
	public ArrayList<String> noise_reading;

	public WindowCollectedData(Context con) {
		mac = getMACAddress();
		udid = getUDID(con);
		if (IDFA.isIDFAAvailable())
			idfa = new String(IDFA.idfa);
		ip_address = getIPAddress(true);
		method = "post";
		version = getAppVersion(con);
	}

	/**
	 * @param con
	 * @return
	 */
	public String getIMEI(Context con) {
		TelephonyManager telephonyManager = (TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getDeviceId() != null)
			return telephonyManager.getDeviceId();
		else
			return "";
	}

	/**
	 * @param con
	 * @return
	 */
	public String getUDID(Context con) {
		return Secure.getString(con.getContentResolver(), Secure.ANDROID_ID);
	}

	/**
	 * @param con
	 * @return
	 */
	public String getSimSerialNo(Context con) {
		TelephonyManager telephonyManager = (TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getSimSerialNumber() != null)
			return telephonyManager.getSimSerialNumber();
		else
			return "";
	}

	/**
	 * @param con
	 * @return
	 */
	public String getSimCountry(Context con) {
		TelephonyManager telephonyManager = (TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getSimCountryIso() != null)
			return telephonyManager.getSimCountryIso();
		else
			return "";
	}

	/**
	 * @param con
	 * @return
	 */
	public String getNetworkCountry(Context con) {
		TelephonyManager telephonyManager = (TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getNetworkCountryIso() != null)
			return telephonyManager.getNetworkCountryIso();
		else
			return "";
	}

	/**
	 * @param con
	 * @return
	 */
	public String getNetworkType(Context con) {
		TelephonyManager telephonyManager = (TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = telephonyManager.getNetworkType();
		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return "1xRTT";
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return "CDMA";
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return "EDGE";
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return "eHRPD";
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return "EVDO rev. 0";
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return "EVDO rev. A";
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return "EVDO rev. B";
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return "GPRS";
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return "HSDPA";
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return "HSPA";
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return "HSPA+";
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return "HSUPA";
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return "iDen";
		case TelephonyManager.NETWORK_TYPE_LTE:
			return "LTE";
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return "UMTS";
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return "Unknown";
		}
		return "Unknown";
	}

	/**
	 * Get MAC address of device.
	 * @return
	 */
	public String getMACAddress() {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				if (!intf.getName().equalsIgnoreCase("wlan0")) {
					continue;
				}

				byte[] mac = intf.getHardwareAddress();
				if (mac == null) {
					return "";
				}
				StringBuilder buf = new StringBuilder();
				for (int idx = 0; idx < mac.length; idx++)
					buf.append(String.format("%02X:", mac[idx]));
				if (buf.length() > 0)
					buf.deleteCharAt(buf.length() - 1);
				return buf.toString();
			}
		} catch (Exception ex) {
		}
		return "";

	}

	/**
	 * Get IP address of device
	 * @param useIPv4
	 * @return
	 */
	public String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = (addr instanceof Inet4Address)? true: false;
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0,
										delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
		return "";
	}

	/**
	 * Get App Version using.
	 * @param context
	 * @return
	 */
	public String getAppVersion(Context context) {
		PackageInfo pInfo;
		try {
			pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @param context
	 * @return
	 */
	public String getIdfaAndroid(Context context) {
		Info adInfo = null;
		try {
			adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (GooglePlayServicesRepairableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
		if (adInfo != null)
			return adInfo.getId();
		else
			return "";
	}

}

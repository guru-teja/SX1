package com.xinthe.spax.data;

import java.io.Serializable;

/**
 * @author Koti 
 * Data collected from estimote Background service
 */
public class BeaconScanData implements Serializable {
	private static final long serialVersionUID = 1L;
	public String proximityUUID, bssid;
	public long timestamp;
	public int rssi, microseconds, txPower, proximity = 0, major, minor;
	public double accuracy = 0;
}

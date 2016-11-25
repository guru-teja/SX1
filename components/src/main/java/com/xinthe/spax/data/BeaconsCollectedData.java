package com.xinthe.spax.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Koti
 *
 */
public class BeaconsCollectedData implements Serializable {
	private static final long serialVersionUID = 1L;
	public String bssid, proximityUUID;
	public int major, minor, txPower;
	public ArrayList<ArrayList<Object>> packets = new ArrayList<ArrayList<Object>>();
}

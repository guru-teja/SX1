package com.xinthe.spax.db;

import java.io.UnsupportedEncodingException;

/**
 * @author Koti
 * Class to maintain beacons data while sending to server 
 * along with noOfAttempts binary data and timestamp
 */
public class CollectedData {
	private int noOfAttempts;
	private int errorCode = 0;
	private String timestamp;
	private byte[] data;
	private int id;

	public CollectedData() {

	}

	public CollectedData(int noOfAttempts, byte[] data) {
		this.noOfAttempts = noOfAttempts;
		this.data = data;
	}

	public void setNoOfAttempts(int noOfAttempts) {
		this.noOfAttempts = noOfAttempts;
	}

	public void setTimeStamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getNoOfAttempts() {
		return noOfAttempts;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public byte[] getData() {
		return data;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getErrorCode() {
		return errorCode;
	}
	public String getDataString() throws UnsupportedEncodingException {
		return new String(data, "UTF-8");
	}
}

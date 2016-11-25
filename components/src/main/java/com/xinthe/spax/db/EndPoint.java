package com.xinthe.spax.db;

/**
 * @author Koti
 *
 */
public class EndPoint {

	private String endPoint;
	private int id;

	public EndPoint() {
	}

	public EndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}

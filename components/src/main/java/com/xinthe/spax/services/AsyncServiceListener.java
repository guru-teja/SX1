package com.xinthe.spax.services;

import com.xinthe.spax.db.CollectedData;

/**
 * @author Koti
 *
 */
public interface AsyncServiceListener {

	public void onResponseSuccess(int requestType, CollectedData object);

	public void onResponseError(int errorCode, CollectedData data);
}

package com.xinthe.spax.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.xinthe.spax.LocationServerConfiguration;
import com.xinthe.spax.db.CollectedData;
import com.xinthe.spax.utils.Constants;
import com.xinthe.spax.utils.Webservices;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Koti
 * Makes network call to service to store data
 */
public class AsyncService {
	Context context;
	CollectedData data;
	AsyncServiceListener listener;
	HttpURLConnection httpURLConnection;
	LocationServerConfiguration locationServerConfiguration;

	public AsyncService(Context _context, AsyncServiceListener _listener, LocationServerConfiguration locationServerConfiguration) {
		context = _context;
		listener = _listener;
		this.locationServerConfiguration = locationServerConfiguration;
	}

	/**
	 * @param _requestType
	 * @param data
	 */
	public void saveBeaconData(CollectedData data) {

		this.data = data;

		new BackgroundTask().execute(locationServerConfiguration.URL);
	}

	/**
	 * @author Rahul Panuganti
	 *
	 */
	public class BackgroundTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... args) {
			String response="";
			if(args[0].startsWith("https://sqs")) {
				try {
					Log.e("Data Packet", data.getDataString());
					BasicAWSCredentials credentials = new BasicAWSCredentials(locationServerConfiguration.AccessID,
							locationServerConfiguration.SecretKey);
					AmazonSQSClient sqsClient = new AmazonSQSClient(credentials);
					//Region region = Region.getRegion(Regions.US_WEST_2);

					sqsClient.setRegion(Region.getRegion(locationServerConfiguration.AWSRegion) );
					SendMessageResult result = sqsClient.sendMessage(args[0], data.getDataString());
					response = result.getMessageId();
				} catch (UnsupportedEncodingException e) {
					Log.e("UnsupEncodingEception", e.getMessage());
				} catch(AmazonClientException e) {
					Log.e("ClientEception", e.getMessage());
				}
			}
			else if(args[0].startsWith("arn:aws:sns")) {
				try{
					Log.e("Data Packet", data.getDataString());
					AWSCredentials awsCredentials = new BasicAWSCredentials(locationServerConfiguration.AccessID,
							locationServerConfiguration.SecretKey);
					AmazonSNSClient pushClient = new AmazonSNSClient(awsCredentials);
					PublishRequest publishRequest = new PublishRequest();
					publishRequest.setSubject("Span");
					publishRequest.setTopicArn(args[0]);
					publishRequest.setMessage(data.getDataString());
					publishRequest.setMessageStructure(data.getDataString());
					MessageAttributeValue messageAttributeValue = new MessageAttributeValue();
					messageAttributeValue.setDataType("String");
					messageAttributeValue.setStringValue("MY ATTRIBUTE VALUE");
					HashMap<String, MessageAttributeValue> msgAttrHashmap = new HashMap<>();
					msgAttrHashmap.put("MY_ATTRIBUTE_NAME", messageAttributeValue);
					publishRequest.setMessageAttributes(msgAttrHashmap);
					pushClient.setRegion(Region.getRegion(locationServerConfiguration.AWSRegion));

					PublishResult result = pushClient.publish(publishRequest);
					response = result.getMessageId();
				}
				catch (Exception e){
					e.printStackTrace();
					Log.e("Exception", e.toString());
				}
			}
			else {
				httpURLConnection = null;
				try {
					Log.e("Data Packet", data.getDataString());
					URL url = new URL(args[0]);
					httpURLConnection = (HttpURLConnection) url.openConnection();
					httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setDoOutput(true);
					httpURLConnection.setDoInput(false);
					httpURLConnection.setReadTimeout(10000);
					httpURLConnection.setChunkedStreamingMode(0);
					httpURLConnection.connect();
					OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
					outputStream.write(data.getData());

					outputStream.close();
					response = httpURLConnection.getResponseMessage();
					Log.e("Response", response);
				} catch (Exception e) {
					Log.e("error ", e.toString());
					response = "";
				}
				finally {
					httpURLConnection.disconnect();
				}
			}

			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e("Response", result);
		}
	}

}

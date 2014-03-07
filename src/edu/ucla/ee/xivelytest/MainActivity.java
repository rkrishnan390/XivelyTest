package edu.ucla.ee.xivelytest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private EditText number_to_upload;
	private EditText xively_feed_id;
	private EditText xively_api_key;
	private CheckBox feed1_label;
	private CheckBox feed2_label;
	private ToggleButton toggleButtonUploadXively;
	private boolean uploadToXively = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		number_to_upload = (EditText) findViewById(R.id.number_input);
		feed1_label = (CheckBox) findViewById(R.id.feed1_label);
		feed2_label = (CheckBox) findViewById(R.id.feed2_label);
		toggleButtonUploadXively = (ToggleButton) findViewById(R.id.toggleButtonUploadXively);
		xively_feed_id = (EditText) findViewById(R.id.XivelyFeedID);
		xively_api_key = (EditText) findViewById(R.id.XivelyAPIkey);
		
		Log.i("feedID", xively_feed_id.toString());
		Log.i("apiKey", xively_api_key.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class CallXivelyApi extends AsyncTask<String, Void, String> {
		private Context context;

		// constructor for getting the context/main UI
		public CallXivelyApi(Context context) {
			this.context = context;
		}
		
		@Override
		protected String doInBackground(String... call) {
			String success = "";
			JSONObject Parent = new JSONObject();
			JSONArray ParentArray = new JSONArray();
			// JSON initialization for feed 1
			JSONObject jsonObjFeed1 = new JSONObject();
			JSONObject datapointsObjFeed1 = new JSONObject();
			JSONArray datapointsArrayFeed1 = new JSONArray();	
			// JSON initialization for feed 2
			JSONObject jsonObjFeed2 = new JSONObject();
			JSONObject datapointsObjFeed2 = new JSONObject();
			JSONArray datapointsArrayFeed2 = new JSONArray();	
			// Timestamping stuff
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
			dateFormat.setTimeZone(timeZone);
			String timeNow = dateFormat.format(new Date());
		
			/*
			// create our list of feeds
			feedLabelList[0] = feed1_label.toString();
			feedLabelList[1] = feed2_label.toString();
			
			// get the timestamps
			timestampsList1 = new String[numberOfValues];
			timestampsList2 = new String[numberOfValues];
			for (int i = 0; i < numberOfValues; i++) {
				timestampsList1[i] = "2014-03-04T00:35:43Z";
				timestampsList2[i] = "2014-03-04T00:35:43Z";
			}
			
			// get the values at those timestamps]
			valuesList1 = new String[numberOfValues];
			valuesList2 = new String[numberOfValues];
			for (int i=0; i < numberOfValues; i++) {
				valuesList1[i] = ("5");
				valuesList2[i] = ("5");
			}
			
			// get the current values
			currentValuesList = new String[numberOfValues];
			for (int i=0; i < numberOfFeeds; i++) {
				currentValuesList[i] = number_to_upload.getText().toString();
			}
			*/
			
			boolean created_json = false;
			//float input_number = Float.parseFloat(number_to_upload.getText().toString());
			
			try {	
				/*
				JsonWriter writer = new JsonWriter(null); 
				Log.i("crash", "issue here");
				writer.setIndent("  ");
				Log.i("crash", "issue here");
				writer.beginObject();
				Log.i("crash", "issue here");
				// write the version first
				writer.name("version").value("1.0.0");
				// write the datapoints stuff
				writer.name("datastreams");
				writer.beginArray();
				writeDatastreamArray(writer, feedLabelList, timestampsList1, timestampsList2, valuesList1, valuesList2, numberOfValues, currentValuesList);
				writer.endArray();
				writer.endObject();
				Log.i("JSON Parent", Parent.toString());
				*/
				//writeDatastreamArray(JsonWriter writer, String[] feedLabelList, String[] timestamps, String[] valuesList, int numberOfValues, String current_value)
				
				
				jsonObjFeed1.put("id", feed1_label.getText().toString());
				datapointsObjFeed1.put("value", number_to_upload.getText().toString());
				datapointsObjFeed1.put("at", timeNow);
				datapointsArrayFeed1.put(datapointsObjFeed1);
				jsonObjFeed1.put("current_value", number_to_upload.getText().toString());
				jsonObjFeed1.put("datapoints", datapointsArrayFeed1);
				
				jsonObjFeed2.put("id", feed2_label.getText().toString());
				datapointsObjFeed2.put("value", number_to_upload.getText().toString());
				datapointsObjFeed2.put("at", timeNow);
				datapointsArrayFeed2.put(datapointsObjFeed2);
				jsonObjFeed2.put("current_value", number_to_upload.getText().toString());
				jsonObjFeed2.put("datapoints", datapointsArrayFeed2);

				ParentArray.put(jsonObjFeed1);
				ParentArray.put(jsonObjFeed2);			
				
				Parent.put("datastreams", ParentArray);
				Parent.put("version", "2.0.0");
				
				created_json = true;
			} catch(Exception e) {
				Log.e("Error", "Something went wrong", e);
				e.printStackTrace();
			}
			
			// if we created our JSON file, start our HTTP uploading
			if(created_json) {
				Log.i("info", "created json");
				String url = new String("https://api.xively.com/v2/feeds/" + xively_feed_id.getText().toString());
				Log.i("string url", url);
				//String feeds = new String("https://api.xively.com/v2/feeds/1706842846/datastreams/");
				String xivelyApiKey = xively_api_key.getText().toString();
				HttpClient client = new DefaultHttpClient();
				HttpClient client_get = new DefaultHttpClient();
				HttpPut put = new HttpPut(url);
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				
				try {
					StringEntity se = new StringEntity(Parent.toString());
					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
					put.setHeader("X-ApiKey", xivelyApiKey);
					put.setEntity(se);
				} catch(Exception e) {
					Log.e("Error", "Something wrong with HTTP", e);
				}
			
				try {
					response = client.execute(put);
					Log.i("put response", response.getStatusLine().toString());
					
					get.setHeader("X-ApiKey", xivelyApiKey);
					response = client_get.execute(get);
					
					HttpEntity httpEntity = response.getEntity();
					InputStream inputStream = httpEntity.getContent();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder stringBuilder = new StringBuilder();
					String line = bufferedReader.readLine();
					while(line != null) {
						stringBuilder.append(line + " \n");
						line = bufferedReader.readLine();
					}
					bufferedReader.close();
					
					//JSONObject jsonObject = new JSONObject(stringBuilder.toString());
					Log.i("Response", response.getStatusLine().toString());
					Log.i("Response JSON", stringBuilder.toString());
					
					//Log.i("Response", response.getStatusLine().getReasonPhrase().toString());
					success = "Success";
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
			return success; 
		}
	
	@Override
	protected void onPostExecute(String result) {
		Toast.makeText(context, "Upload successful!", Toast.LENGTH_LONG).show();
	}
	
	/*
	public void writeDatastreamArray(JsonWriter writer, String[] feedLabelList, String[] timestampsListFeed1, String[] timestampsListFeed2, String[] valuesListFeed1, String[] valuesListFeed2, int numberOfValues, String[] currentValueList) throws IOException {
		for (int i=0; i < numberOfFeeds; i++) {
			writer.beginObject();
			writer.name("id").value(feedLabelList[i]);
			writer.name("datapoints");
			writeDatapointsArray(writer, timestampsListFeed1, timestampsListFeed2, numberOfValues, valuesListFeed1, valuesListFeed2);
			writer.name("current_value").value(currentValueList[i]);
			writer.endObject();
		}
	}
	
	public void writeDatapointsArray(JsonWriter writer, String[] timestampsListsFeed1, String[] timestampsListFeed2, int numberOfValues, String[] valuesListFeed1, String[] valuesListFeed2) throws IOException {
		writer.beginArray();
		for(int i = 0; i < numberOfValues; i++) {
			writer.beginObject();
			writer.name("at").value(timestampsListsFeed1[i]);
			writer.name("value").value(valuesListFeed1[i]);
			writer.endObject();
		}
		for(int i = 0; i < numberOfValues; i++) {
			writer.beginObject();
			writer.name("at").value(timestampsListFeed2[i]);
			writer.name("value").value(valuesListFeed2[i]);
			writer.endObject();
		}
		writer.endArray();
	}
	*/
}
	
	
	
	public void onClick(View view) {
		if(uploadToXively) {
			CallXivelyApi task = new CallXivelyApi(this);
			task.execute(new String[] {"Call"});
		}
		
		//DownloadWebPageTask task = new DownloadWebPageTask();
		//task.execute(new String[] { "http://www.vogella.com" });
		
		Toast.makeText(this, "Clicked the button!", Toast.LENGTH_LONG).show();
	}

	public void onClickXivelyToggle(View view) {
		uploadToXively = toggleButtonUploadXively.isChecked();
	}
	
	
}

package com.example.networktest;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity implements OnClickListener {

	public static final int SHOW_RESPONSE = 0;
	
	private Button 		sendRequest;
	private TextView 	responseText;
	
	private Handler 	handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_RESPONSE:
				String response = (String) msg.obj;
				// 在这里进行UI操作，将结果显示到界面上
				responseText.setText(response);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sendRequest 	= (Button) 		findViewById(R.id.send_request);
		responseText 	= (TextView) 	findViewById(R.id.response_text);
		
		sendRequest.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.send_request) {
			sendRequestWithHttpClient();
		}
	}

	private void sendRequestWithHttpClient() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					// 指定访问的服务器地址是电脑本机
					HttpGet httpGet = new HttpGet("http://7xkbha.com1.z0.glb.clouddn.com/get_data.json");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 请求和响应都成功了
						HttpEntity entity 	= httpResponse.getEntity();
						String response 	= EntityUtils.toString(entity, "utf-8");
						
						// GSON解析
						// parseJSONWithGSON(response);
						
						// JSObject解析
						parseJSONWithJSONObject(response);
					
						 Message message 	= new Message();
						 message.what 		= SHOW_RESPONSE;
						 message.obj		= response.toString();
						 handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// 使用gson解析
	private void parseJSONWithGSON(String jsonData) {
		Gson gson = new Gson();
		List<AppInfoModel> appList = gson.fromJson(jsonData, new TypeToken<List<AppInfoModel>>() {}.getType());
		
		for (AppInfoModel app : appList) {
			Log.d("andli", "id is " 		+ app.getId());
			Log.d("andli", "name is " 		+ app.getName());
			Log.d("andli", "version is " 	+ app.getVersion());
		}
	}

	// 使用JSONObject解析(没有转化为实体对象)
	private void parseJSONWithJSONObject(String jsonData) {
		String jsonstr = "[{\"id\":\"5\",\"version\":\"5.5\",\"name\":\"Angry Birds1111\"},{\"id\":\"6\",\"version\":\"7.8\",\"name\":\"WeChat1111\"},{\"id\":\"7\",\"version\":\"2.3\",\"name\":\"QQ1111\"}]";
		jsonData 	   = jsonstr;
		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				String id 		= jsonObject.getString("id");
				String name 	= jsonObject.getString("name");
				String version 	= jsonObject.getString("version");
				
				Log.d("andli", "id is " 		+ id);
				Log.d("andli", "name is " 		+ name);
				Log.d("andli", "version is " 	+ version);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

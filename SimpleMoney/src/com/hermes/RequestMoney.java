package com.hermes;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class RequestMoney extends Activity{
	
	private int width, height, user_id;	
	private UserModel user;
	
	public void onCreate(Bundle savedInstanceState) {
		user_id = getIntent().getExtras().getInt("User_ID");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_money);
        user = getUserData();
        
        WindowManager mWinMgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE); 
        width = mWinMgr.getDefaultDisplay().getWidth(); 
        height = mWinMgr.getDefaultDisplay().getHeight();
        
        EditText am = (EditText) findViewById(R.id.amount);
        am.setWidth((int)(width/1.5));
    	am.setHeight((int)(height/50));
    	
    	EditText em = (EditText) findViewById(R.id.email);
    	em.setWidth((int)(width/1.5));
    	em.setHeight((int)(height/50));
    	
    	EditText des = (EditText) findViewById(R.id.description);
        des.setWidth((int)(width/1.5));
    	des.setHeight((int)(height/25));
    }
	
	public void goToRootView(View view)
	{
		Intent myIntent = new Intent(getApplicationContext(), Root.class);
		myIntent.putExtra("User_ID", user_id);
	    startActivity(myIntent);
	}
	
	public void clearFields(View view)
	{
		EditText am = (EditText) findViewById(R.id.amount);
        am.setText("");
    	
    	EditText em = (EditText) findViewById(R.id.email);
    	em.setText("");
    	
    	EditText des = (EditText) findViewById(R.id.description);
    	des.setText("");
	}
	
	public UserModel getUserData()
	{
		try
		{
			URI uri = new URI("http://severe-leaf-6733.herokuapp.com/users/" + user_id);
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(uri);
			
			get.setHeader("Accept", "application/json");
			get.setHeader("Content-type", "application/json");
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			String responseString = client.execute(get, responseHandler);
			
			GsonBuilder g = new GsonBuilder();
			g.setDateFormat("E MMM d HH:mm:ss Z y");
			Gson gson = g.create();
			UserModel um = gson.fromJson(responseString, UserModel.class);
			return um;
		}
		catch (Exception e) {}
		return null;
	}
	
	public void requestMoney(View view)
	{
		EditText amo = (EditText) findViewById(R.id.amount);
    	EditText ema = (EditText) findViewById(R.id.email);
    	EditText des = (EditText) findViewById(R.id.description);
    	String amount = amo.getText().toString();
    	String email = ema.getText().toString();
    	String description = des.getText().toString();
    	try
    	{
    		URI uri = new URI("http://severe-leaf-6733.herokuapp.com/invoices");
    		HttpClient client = new DefaultHttpClient();
    		HttpPost post = new HttpPost(uri);
    		
    		JSONObject json = new JSONObject();
    		JSONObject m = new JSONObject();
    		m.put("sender_email", user.getEmail());
    		m.put("recipient_email", email);
    		m.put("description", description);
    		m.put("amount", amount);
    		json.put("user", m);
    		
    		StringEntity se = new StringEntity(json.toString());
    		post.setEntity(se);
    		post.setHeader("Accept", "application/json");
    		post.setHeader("Content-type", "application/json");
    		BasicResponseHandler responseHandler = new BasicResponseHandler();
    		String responseString = client.execute(post, responseHandler);
    		
    		if(responseString != null)
    		{
    			Toast.makeText(this, "Invoice Approved", Toast.LENGTH_LONG).show();
            } else {
            	Toast.makeText(this, "Request Denied", Toast.LENGTH_LONG).show();               
            }
    	}
    	catch (Exception e){
    		Log.e("eX",e.getMessage());
    	}
	}
}
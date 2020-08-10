package com.test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Test;


public class CreatingRequestBody {
	
	
@Test
public void postRequest() throws JSONException {
	
	JSONObject reqBody = new JSONObject();
	reqBody.put("customerId", "123");
	reqBody.put("customerName", "Scott");
	
	JSONObject account = new JSONObject();
	
	account.put("accountId", "A123D33");
	account.put("accountType", "Savings");
	
	JSONArray accountArray = new JSONArray();
	accountArray.put(account);
	
	reqBody.put("account", accountArray);
	
	System.out.println(reqBody.toString());
	
 }	
	


}
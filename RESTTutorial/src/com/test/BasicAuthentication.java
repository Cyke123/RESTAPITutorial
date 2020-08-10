package com.test;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
//import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BasicAuthentication {
	
  RequestSpecification httpRequest;
  String accesstoken;
  String baseURI;
  String contentType;
  String auth;
  
  @BeforeClass
  public void setUp() {
      
	  accesstoken = "iejB1LLbr0dJ90d3YDnkCc99Bua6LcFsF_eG";
	  baseURI = "https://gorest.co.in/public-api/users";
	  contentType = "application/json";
	  auth = "Bearer " + accesstoken;
  }
  
  @Test()
  public void addUser(ITestContext context) {		  

	  JSONObject jsonRequest = new JSONObject();
	  httpRequest = RestAssured.given();
	  
	  try {
		jsonRequest.put("first_name", "Scott");
		jsonRequest.put("last_name", "Summers");
		jsonRequest.put("gender", "male");
		jsonRequest.put("email", "scotty@abc.com");	
		
	} catch (JSONException e) {
		System.out.println(e.getMessage());
	}
	  
	  httpRequest.headers("Content-Type",contentType);
	  httpRequest.headers("Authorization",auth);
	  httpRequest.body(jsonRequest.toString());
	  
      Response response = httpRequest.post(baseURI);
      String respBody = response.getBody().asString();
      
      JsonPath jsonpath = response.jsonPath();
      int responseCode = jsonpath.get("_meta.code");
      String firstName = jsonpath.get("result.first_name");
      String lastName = jsonpath.get("result.last_name");
      
	  System.out.println("API Status : " + response.getStatusLine());
	  System.out.println("API response : " + respBody);
	  System.out.println("API response code : " + responseCode);
	  
	  Assert.assertEquals(responseCode, 201, "User Not Added");
	  Assert.assertEquals(firstName, "Scott", "correct first name not displayed");
	  Assert.assertEquals(lastName, "Summers", "correct last name not displayed");
	  
	  context.setAttribute("first_name", firstName);
	  
  }
	
  @Test(dependsOnMethods="addUser")
  public void getUser(ITestContext context) {	
	  
	  httpRequest = RestAssured.given();

	  String first_Name = (String) context.getAttribute("first_name");
	  String uri = baseURI + "?access-token=" + accesstoken + "&first_name=" + first_Name;
	  
      Response response = httpRequest.get(uri);
      String respBody = response.getBody().asString();
      
      JsonPath jsonpath = response.jsonPath();
      int responseCode = jsonpath.get("_meta.code");
      String firstName = jsonpath.get("result.first_name[0]");
      String lastName = jsonpath.get("result.last_name[0]");
      String id = jsonpath.get("result.id[0]");
      
      context.setAttribute("id", id);
      
	  System.out.println("API Status : " + response.getStatusLine());
	  System.out.println("API response : " + respBody);
	  System.out.println("API response code : " + responseCode);
	  System.out.println("API response code : " + responseCode);
	  System.out.println("Name : " + firstName+ " " +lastName);
	  
	  Assert.assertEquals(responseCode, 200, "Invalid Status code");
	  Assert.assertEquals(firstName, "Scott", "correct first name not displayed");
	  Assert.assertEquals(lastName, "Summers", "correct last name not displayed");
  }
  
  @Test(dependsOnMethods="getUser")
  public void deleteUser(ITestContext context) {
	  
	  httpRequest = RestAssured.given();

      String id = (String) context.getAttribute("id");
	  String uri = baseURI + "/" + id;
	  
	  httpRequest.headers("Content-Type",contentType);
	  httpRequest.headers("Authorization",auth);
	  
      Response response = httpRequest.delete(uri);
      String respBody = response.getBody().asString();

      JsonPath jsonpath = response.jsonPath();
      int responseCode = jsonpath.get("_meta.code");
      String responseMessage = jsonpath.get("_meta.message");
      
	  System.out.println("API Status : " + response.getStatusLine());
	  System.out.println("API response : " + respBody);
	  System.out.println("API response code : " + responseCode);
	  System.out.println("API response message : " + responseMessage);
	  
	  Assert.assertEquals(responseCode, 204, "Invalid Status code");
	  Assert.assertEquals(responseMessage, "The request was handled successfully and the response contains no body content.", "correct message not displayed");

  }
}

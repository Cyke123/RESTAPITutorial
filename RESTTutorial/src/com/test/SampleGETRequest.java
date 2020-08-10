package com.test;

import org.testng.annotations.Test;
import io.restassured.response.Response;
import io.restassured.RestAssured;

public class SampleGETRequest {
	
  @Test
  public void f() {
	  
	  Response response; //Create reference variable for Response class
	  
	  String uri = "https://reqres.in/api/users/2"; 
	  response = RestAssured.get(uri); // Make a request to REST API and stores response in Response class object
	  String responseBody = response.getBody().asString(); // Stores response body as a string
	  
	  System.out.println(responseBody);
  }
}
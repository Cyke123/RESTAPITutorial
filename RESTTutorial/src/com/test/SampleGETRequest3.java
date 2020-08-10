package com.test;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SampleGETRequest3 {

	
	@Test
	public void testAPIwithPathParm() {
		
		RequestSpecification httpRequest = RestAssured.given();
		String uri = "https://reqres.in/api/users/{userId}";
		
		httpRequest.pathParam("userId", 2);
		Response response = httpRequest.get(uri);
		
		System.out.println(response.getBody().asString());
		
	}

}




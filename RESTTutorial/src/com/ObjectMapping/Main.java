package com.ObjectMapping;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Main {

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		
          BookingDates bookingdates = new BookingDates("2018-01-01","2019-01-01");
          Payload payload = new Payload("Jim","Brown",111,true,"Breakfast",bookingdates);
          
          ObjectMapper objMapper = new ObjectMapper();
          String request = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
          System.out.println(request);
          
          RequestSpecification httprequest = RestAssured.given();
          httprequest.body(payload);
          httprequest.headers("Content-Type","application/json");
          
          String uri = "https://restful-booker.herokuapp.com/booking";
          Response response = httprequest.post(uri);
          System.out.println(response.getStatusLine());
          System.out.println(response.getBody().asString());
          
          

	}

}

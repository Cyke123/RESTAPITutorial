package com.test;

import org.testng.annotations.AfterTest;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import org.testng.Assert;
import org.testng.Reporter;

import io.restassured.response.Response;
import io.restassured.RestAssured;
import io.restassured.http.Method;
//import io.restassured.http.Header;
//import io.restassured.http.Headers;
import io.restassured.specification.RequestSpecification;
import io.restassured.path.json.JsonPath;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class SampleGETRequest2 {
	
	XSSFWorkbook wb;
	XSSFSheet inputSheet;
	XSSFSheet outputSheet;
	int inputRowCount;
	int outputRowCount;

  @BeforeTest
  public void beforeTest() throws IOException {
	  
	  String inputPath = "C:\\TestData\\TestData.xlsx";
	  FileInputStream fis = new FileInputStream(inputPath); //Create fileinput stream
	  
	  wb = new XSSFWorkbook(fis); // create workbook object
	  inputSheet = wb.getSheetAt(0); // create worksheet object
	  outputSheet = wb.getSheetAt(1);
	  
	  inputRowCount = inputSheet.getLastRowNum() - inputSheet.getFirstRowNum(); //get row count
	  outputRowCount = outputSheet.getLastRowNum() - outputSheet.getFirstRowNum();
	  
	  if(inputRowCount != outputRowCount) {
		  throw new IOException("Data Set up Not Correct");		 
	  }
	  
	  Reporter.log("Input Data Collected");
	  
  }

  @AfterTest
  public void afterTest() throws IOException {
	  wb.close();
	  Reporter.log("Test Ends");
	  }
 
  @Test
  public void testAPI() { 
	 
   for(int i=1;i<=inputRowCount;i++) {
	   
	   Reporter.log("Test for TC:" + i + " starts");
	   
	     double cellData = inputSheet.getRow(i).getCell(0).getNumericCellValue();
	     String inputData = String.valueOf(cellData);
	     
         RestAssured.authentication = RestAssured.preemptive().basic("sss", "sss"); // setting up preemptive authentication
	     RestAssured.baseURI = "https://reqres.in/api/users/";//Specify baseURL
		 RequestSpecification httpRequest = RestAssured.given(); //create request specification for the request to be sent
		 Response response = httpRequest.request(Method.GET,inputData); //Send Request[specfied method and input paramters]
		 
		 Reporter.log("Request Sent");
		 
		 String responseBody = response.getBody().asString();
		 String sessionId = response.getSessionId(); // get sessionid
		 String contentType = response.getContentType(); // get contenttype
		 int statusCode = response.getStatusCode(); //get status code
		 String statusLine = response.getStatusLine(); //get status line
		 //Headers headers = response.getHeaders(); // get all header values		 
		 String cookie = response.getHeader("Set-Cookie"); // get specified header value
		 
		 //Set expected Response values
		 String expectedEmail = outputSheet.getRow(i).getCell(0).getStringCellValue();
		 String expectedLastName = outputSheet.getRow(i).getCell(2).getStringCellValue();
		 String expectedCompany = outputSheet.getRow(i).getCell(3).getStringCellValue();
		 
		 //get Actual response values
		 JsonPath jsonpath = response.jsonPath(); // object to read json response nodes
		 String email = jsonpath.get("data.email"); // get value to individual node in json response
         String firstName = jsonpath.get("data.first_name");
         String lastName = jsonpath.get("data.last_name");
         String company = jsonpath.get("ad.company");
		 
		 System.out.println("Response=>" + responseBody);
		 System.out.println("Session Id=>" + sessionId);
		 System.out.println("Content Type=>" + contentType);
		 System.out.println("Status Code=>" + statusCode);
		 System.out.println("Status Line=>" + statusLine);
		 System.out.println("Cookie=>" + cookie); 
		 //for (Header header : headers) {
			// System.out.println("key :" + header.getName() + " Value :" + header.getValue());
		 //}
		 
		 //Logging for Report
		 Reporter.log("Response=>" + responseBody);
		 Reporter.log("Content Type=>" + contentType);
		 Reporter.log("Status Code=>" + statusCode);
		 Reporter.log("Status Line=>" + statusLine);
		 
		 
		 
		 //assertions
		  Assert.assertEquals(email, expectedEmail,"FAIL:incorrect email value");
		  Assert.assertEquals(lastName, expectedLastName,"FAIL:incorrect last name");
		  Assert.assertEquals(company, expectedCompany,"FAIL:incorrect company value");
		  Assert.assertEquals(statusCode, 200, "FAIL:Incorrect status code");
		  Assert.assertEquals(statusLine, "HTTP/1.1 200 OK", "FAIL: Incorrect status");
		  Assert.assertEquals(responseBody.contains(firstName), true, "FAIL: Response does not have correct name");
		 
	   
   }
	 

  }
}

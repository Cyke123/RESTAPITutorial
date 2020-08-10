package com.test;

import org.testng.Assert;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

//import org.testng.annotations.BeforeTest;
//import org.testng.annotations.AfterTest;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.*;

class SamplePOSTRequest {
	
	XSSFWorkbook wb;
	XSSFSheet inputSheet;
	XSSFSheet outputSheet;
	int inputRowCount;
	int outputRowCount;
	
	@BeforeTest
	  public void beforeTest() throws IOException {
		  
		  String inputPath = "C:\\TestData\\POSTdata.xlsx";
		  FileInputStream fis = new FileInputStream(inputPath); //Create fileinput stream
		  
		  wb = new XSSFWorkbook(fis); // create workbook object
		  inputSheet = wb.getSheetAt(0); // create worksheet object
		  outputSheet = wb.getSheetAt(1);
		  
		  inputRowCount = inputSheet.getLastRowNum() - inputSheet.getFirstRowNum(); //get row count
		  outputRowCount = outputSheet.getLastRowNum() - outputSheet.getFirstRowNum();
		  
		  if(inputRowCount != outputRowCount) {
			  throw new IOException("Data Set up Not Correct");		 
		  }
		  
	  }

	  @AfterTest
	  public void afterTest() throws IOException {  
		  wb.close();
		  }	
	
  @Test
  public void test() throws JSONException {
	
	  for(int i=1;i<=inputRowCount;i++) {
		  
		  String name = inputSheet.getRow(i).getCell(0).getStringCellValue();
		  String job = inputSheet.getRow(i).getCell(0).getStringCellValue();
		  
		  RestAssured.baseURI = "https://reqres.in/api/users";
		  RequestSpecification httpRequest = RestAssured.given();
		  httpRequest.headers("Content-Type","application/json");
		  
		  JSONObject jsonRequest = new JSONObject();
		  jsonRequest.put("name", name);
		  jsonRequest.put("job", job);
		  
		  httpRequest.body(jsonRequest.toString());
		  
		  Response response = httpRequest.request(Method.POST);
		  String responseBody = response.getBody().asString();
		  String statusLine = response.getStatusLine();
		  
		  System.out.println("Response: " + responseBody);
		  System.out.println("Status: " + statusLine);
		  
		  JsonPath jsonPath = response.jsonPath();
		  String actualName = jsonPath.get("name");
		  String actualJob = jsonPath.get("job");
		  String actualId = jsonPath.get("id");
		  
		  String expectedName = outputSheet.getRow(i).getCell(0).getStringCellValue();
		  String expectedJob = outputSheet.getRow(i).getCell(0).getStringCellValue();
		  
		  
		  Assert.assertEquals(statusLine, "HTTP/1.1 201 Created","FAIL:RequestFailed");
		  Assert.assertEquals(actualName, expectedName,"FAIL: Name is not displayed");
		  Assert.assertEquals(actualJob, expectedJob,"FAIL: Job is not displayed");
		  Assert.assertNotNull(actualId, "FAIL:ID not present in response");
	  }
	  	  
	  
	  
  }

}

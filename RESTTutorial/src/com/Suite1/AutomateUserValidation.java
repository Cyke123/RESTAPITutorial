package com.Suite1;

import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;

public class AutomateUserValidation {
	
  String baseURI;
  String contentType;
  
  
  @BeforeClass
  @Parameters({"UserCreationURI","Content-Type"})
  public void beforeClass(@Optional("https://reqres.in/api/users")String uri, @Optional("application/json")String contenttype) {	  
	  baseURI=uri;
	  contentType =contenttype;
  }
  
  @DataProvider(name="getUserInput")
  public Object[][] getUserInput() {
	  return ReusableFunctions.readInputDataFromExcel("C:\\TestData\\getUser\\getUserTestData.xlsx","getUserInput");	  
  }
  
  @DataProvider(name="getUserListInput")
  public Object[][] getUserListInput() {
	  return ReusableFunctions.readInputDataFromExcel("C:\\TestData\\getUser\\getUserTestData.xlsx","getUserListInput");	  
  }
  
  
  @Test(priority=1,dataProvider="getUserInput")
  public void getUser(String testCaseNum, String userId) {
	  
	  Map<String,String> expectedResult = new HashMap<>();
	  
	  String requestURI = baseURI+"/"+Integer.parseInt(userId); 
	  Response response = ReusableFunctions.sendRequest(requestURI, testCaseNum);
	  
	  JsonPath jsonpath = response.jsonPath();
	  
	  try {
		int actualId = jsonpath.get("data.id");
		String actualEmail = jsonpath.get("data.email");
		String actualFirstName = jsonpath.get("data.first_name");
		int actualStatusCode = response.getStatusCode();
		
		expectedResult = ReusableFunctions.readExpectedResult("C:\\TestData\\getUser\\getUserTestData.xlsx","getUserExpectedResult", testCaseNum);
		
		int expectedId = Integer.parseInt(expectedResult.get("data.id"));
		String expectedEmail = expectedResult.get("data.email");
		String expectedFirstName = expectedResult.get("data.first_name");
		int expectedStatusCode = Integer.parseInt(expectedResult.get("StatusCode"));
		
		Assert.assertEquals(actualStatusCode, expectedStatusCode,"FAIL:Request not successfull");
		Assert.assertEquals(actualId, expectedId, "FAIL:Id not matching expected");
		Assert.assertEquals(actualEmail, expectedEmail,"FAIL:e-mail not matching expected");
		Assert.assertEquals(actualFirstName, expectedFirstName,"FAIL:first name not matching expected");
		Assert.assertNotNull(jsonpath.get("ad.company"), "FAIL: Company name not displayed");	

	  } catch (Exception e) {
		 System.out.println(e.getMessage());
	  }
  
	  System.out.println("getUser-> " + testCaseNum + ": Passed");
	  
  }
  
  @Test(priority=0, dataProvider="getUserListInput")
  public void getUserList(String testCaseNum) {
	  
	  Map<String,String> expectedResult = new HashMap<>();
	  
	  String requestURI = baseURI;
	  Response response = ReusableFunctions.sendRequest(requestURI, testCaseNum);

	  JsonPath jsonpath = response.jsonPath();
	  List<Integer> numOfRecords = jsonpath.get("data.id");
	  int total = jsonpath.get("total");
	  int perPage = jsonpath.get("per_page");
	  
	  if(numOfRecords.size()<perPage){
		  Assert.assertEquals(numOfRecords.size(), total, "FAIL: Number of records dont match metadata");  
	  }
	  else {
		  Assert.assertEquals(numOfRecords.size(), perPage, "FAIL: Number of records dont match metadata"); 
	  }
  
	  try {
		expectedResult = ReusableFunctions.readExpectedResult("C:\\TestData\\getUser\\getUserTestData.xlsx","getUserExpectedResult", testCaseNum);
		
		  int expectedStatusCode = Integer.parseInt(expectedResult.get("StatusCode"));
		  int actualStatusCode = response.getStatusCode();
		  Assert.assertEquals(actualStatusCode, expectedStatusCode,"FAIL:Request not successful");
		  
		  for (int i=0; i<numOfRecords.size();i++) {
			     int id = jsonpath.get("data.id[i]");
			     String email = jsonpath.get("data.email[i]");
			     String fName = jsonpath.get("data.first_name[i]");  

			     Assert.assertTrue(id>0, "FAIL: Invalid id");
			     Assert.assertTrue(email.length()>0, "FAIL: Invalid email-id");
			     Assert.assertTrue(fName.length()>0, "FAIL: Invalid first name");		  
		  }

		  
	  } catch (Exception e) {
		  System.out.println(e.getMessage());
	  }
  

	  System.out.println("getUserList-> " + testCaseNum + ": Passed");
  }

  @AfterClass
  public void afterClass() {
  }

}

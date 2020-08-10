package com.Suite1;

import java.io.FileInputStream;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ReusableFunctions {
	
	private ReusableFunctions(){
		
	}

	public static Object[][] readInputDataFromExcel(String filepath, String sheetName){
		
		Object[][] data = null;
		DataFormatter dataformatter = new DataFormatter();
		
		try(FileInputStream fis = new FileInputStream(filepath);
				XSSFWorkbook workbook = new XSSFWorkbook(fis);) {
			
			XSSFSheet sheet = workbook.getSheet(sheetName);
			int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
            int columnCount = sheet.getRow(0).getLastCellNum();
            
            data = new Object[rowCount][columnCount];	
			
			for(int i=1;i<=rowCount;i++) {
			
				for(int j=0;j<columnCount;j++) {
					XSSFCell cell = sheet.getRow(i).getCell(j);		
					data[i-1][j]=dataformatter.formatCellValue(cell);
				}

			}		
			
		} catch (NullPointerException | IOException | ArrayIndexOutOfBoundsException | IllegalStateException e) {
			System.out.println("Error : " + e.getMessage());
		}

		return data;			
		
	}
	
	public static Map<String,String> readExpectedResult(String filepath, String sheetName, String testCaseName) throws Exception {
		
		Map<String,String> expectedResult = new HashMap<>();
		
		try(FileInputStream fis = new FileInputStream(filepath);
				XSSFWorkbook workbook = new XSSFWorkbook(fis);) {
			
			XSSFSheet sheet = workbook.getSheet(sheetName);
			
			int rowNum = sheet.getLastRowNum() - sheet.getFirstRowNum();
			int rowWithData=0;
	
			if(sheet.getRow(0).getCell(0).getStringCellValue().equalsIgnoreCase("TestCase")) {	
				
				int count=0;
				while (count < rowNum) {
					count++;
					if(sheet.getRow(count).getCell(0).getStringCellValue().equalsIgnoreCase(testCaseName)) {
						rowWithData = count;
						break;
					}						
				}
			
			 if (rowWithData==0){
				   throw new Exception("Test Case not present"); 
			 }
			 else {
				   DataFormatter dataformatter = new DataFormatter();
				   Iterator<Cell> iterateNode = sheet.getRow(0).cellIterator();
				   Iterator<Cell> iterateValue = sheet.getRow(rowWithData).cellIterator();
				 
				   while(iterateNode.hasNext()){			 
					   expectedResult.put(dataformatter.formatCellValue(iterateNode.next()),dataformatter.formatCellValue(iterateValue.next()));
				   }
			 }

				
			}
			else {
				throw new Exception("Expected Result Data not present");
			}
			
		}
		catch(NullPointerException | IOException | ArrayIndexOutOfBoundsException | IllegalStateException e) {
			System.out.println("Error : " + e.getMessage());
		}
		
		return expectedResult;
		
	}
	
	public static Response sendRequest(String uri, String testCaseNum) {
		
		  RequestSpecification httpRequest = RestAssured.given();

		  Response response = httpRequest.get(uri);
		  String respBody = response.getBody().asString();
		  System.out.println("Response for " + testCaseNum + ": " + respBody);
		  
		  return response;
		  
	}
	
	public static void main(String[] args) throws Exception {
		//ReusableFunctions.readExpectedResult("C:\\TestData\\getUser\\getUserTestData.xlsx","getUserExpectedResult", "TEST CASE 1");
	}

}


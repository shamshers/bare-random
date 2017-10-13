package com.infinityfw.libraries;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.testng.xml.XmlSuite;

import com.infinityfw.readlxls.ReadingDataSheetOfTestCase;
import com.infinityfw.readlxls.XLReader;


public class BootTestNG {
	public static ArrayList<XmlSuite> suitePassedTorunTestNGXMLForAllSuites = new ArrayList<XmlSuite>();
	public static XLReader masterTestSuiteXL = null;
	public static Hashtable<String, XLReader> currentTestSuiteXL = new Hashtable<String, XLReader>();
	private static final Logger logger = Logger.getLogger(ReadingDataSheetOfTestCase.class);

	public Hashtable<String, XLReader> currentTestSuiteXL(){
		String currentTestSuite;
		String currentTestName;
		int rowNum = 2; //Initializing to 2 because data will start from row 2
		ArrayList<String> testCasesWithinTestSuite = null;
		XLReader testSuiteWithRunModeY = null;
		logger.info("The path to MasterTestSuite" + DynamicQuery.CONFIG_EXCEL_FILE_PATH +PublicVariables.SEP + PublicVariables.MASTER_TEST_SUITE);
		try{
			masterTestSuiteXL = new XLReader(DynamicQuery.CONFIG_EXCEL_FILE_PATH +PublicVariables.SEP +PublicVariables.MASTER_TEST_SUITE);

		}catch(Exception e){
			logger.error("The file " + PublicVariables.MASTER_TEST_SUITE + "cannot be found int the mentioned path.");
		}
		int rowCount = masterTestSuiteXL.getRowCount(PublicVariables.MASTER_TEST_SUITE_SHEET_NAME);
		if(rowCount > 0){
			for(;rowNum <= rowCount;rowNum++){
				currentTestSuite = (String)masterTestSuiteXL.getCellData(PublicVariables.MASTER_TEST_SUITE_SHEET_NAME, PublicVariables.MASTER_TEST_SUITE_1ST_COLUMN, rowNum);
				testCasesWithinTestSuite = new ArrayList<String>();
				if(masterTestSuiteXL.getCellData(PublicVariables.MASTER_TEST_SUITE_SHEET_NAME, PublicVariables.MASTER_TEST_SUITE_RUNMODE_COLUMN, rowNum).equalsIgnoreCase(PublicVariables.RUNMODE_YESVALUE)){
					try{
						testSuiteWithRunModeY = new XLReader(DynamicQuery.CONFIG_EXCEL_FILE_PATH +PublicVariables.SEP +currentTestSuite+".xlsx");
					}catch(Exception e){
						logger.warn("Expected excel file" + currentTestSuite + " was not found");
					}
					logger.info("The current testsuite with Runmode = Y is " +currentTestSuite);
					logger.info("The path of the excel of current Test Suite" + DynamicQuery.CONFIG_EXCEL_FILE_PATH+PublicVariables.SEP+currentTestSuite+".xlsx");
					currentTestSuiteXL.put(currentTestSuite, testSuiteWithRunModeY);
					for(int i= 2; i <= testSuiteWithRunModeY.getRowCount(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME); i++ ){
						currentTestName = testSuiteWithRunModeY.getCellData(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME, 0, i);
						testCasesWithinTestSuite.add(currentTestName);
					} 
				}
			}
		}
		else{
			logger.info("There are no Test Suites in Master test suite excel");
		}
		return currentTestSuiteXL;
	}
}

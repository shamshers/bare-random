package com.infinityfw.readlxls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.infinityfw.libraries.ConfigFileReader;
import com.infinityfw.libraries.DataReader;
import com.infinityfw.libraries.DynamicQuery;
import com.infinityfw.libraries.PublicVariables;
import com.infinityfw.testcases.CreateTestCasesFromTestSuite;
import com.infinityfw.utillib.FwUtil;
public class ReadingTestSuiteXLWithRunmode {

	public static XLReader masterTestSuiteXL = null;
	public static Hashtable<String, XLReader> currentTestSuiteXL = new Hashtable<String, XLReader>();
	private static final Logger logger = Logger.getLogger(ReadingTestSuiteXLWithRunmode.class);

	public static void main(String[] args) throws EmptyStackException, IOException{
		DataReader obj = new DataReader();
		ConfigFileReader.loadProperties(obj.getProperty("INFINITY.CONFIG.Properties.Path"), false);
		String currentTestSuite;
		int rowNum = 2; 
		ArrayList<String> testCasesWithinTestSuite;
		XLReader testSuiteWithRunModeY = null;
		logger.trace(DynamicQuery.CONFIG_EXCEL_FILE_PATH + PublicVariables.SEP+PublicVariables.MASTER_TEST_SUITE);
		logger.info(DynamicQuery.CONFIG_EXCEL_FILE_PATH + PublicVariables.SEP+ PublicVariables.MASTER_TEST_SUITE);
		try{
			masterTestSuiteXL = new XLReader(DynamicQuery.CONFIG_EXCEL_FILE_PATH +PublicVariables.SEP+ PublicVariables.MASTER_TEST_SUITE);
		}catch(Exception e){
			logger.error("Exception in main method : The file '" + PublicVariables.MASTER_TEST_SUITE + "' cannot be found in the mentioned path - ' " +DynamicQuery.CONFIG_EXCEL_FILE_PATH + "'." + e);
			String errMsg = "An exception occurred while reading Master Suite File.";
			logger.error(errMsg + "Exception :" + e);
			logger.error(errMsg + "StackTrace :<br/>" + FwUtil.getStackTrace(e));
		}
		int rowCount = masterTestSuiteXL.getRowCount(PublicVariables.MASTER_TEST_SUITE_SHEET_NAME);
		if(rowCount > 0){
			for(;rowNum <= rowCount;rowNum++){
				currentTestSuite = (String)masterTestSuiteXL.getCellData(PublicVariables.MASTER_TEST_SUITE_SHEET_NAME, PublicVariables.MASTER_TEST_SUITE_1ST_COLUMN, rowNum);
				if(masterTestSuiteXL.getCellData(PublicVariables.MASTER_TEST_SUITE_SHEET_NAME, PublicVariables.MASTER_TEST_SUITE_RUNMODE_COLUMN, rowNum).equalsIgnoreCase(PublicVariables.RUNMODE_YESVALUE)){
					try{
						testSuiteWithRunModeY = new XLReader(DynamicQuery.CONFIG_EXCEL_FILE_PATH+PublicVariables.SEP+currentTestSuite+".xlsx");
					}catch(Exception e){
						logger.error("The file " + currentTestSuite+".xlsx" + "cannot be found in the mentioned path -" + DynamicQuery.CONFIG_EXCEL_FILE_PATH);
						String errMsg = "An exception occurred while reading Master Suite File.";					
						logger.error(errMsg + "StackTrace :<br/>" + FwUtil.getStackTrace(e));
					}
					logger.trace("The current TestSuite with Runmode Y is " + currentTestSuite);
					logger.trace("The path to the current testsuite xl file is "+ DynamicQuery.CONFIG_EXCEL_FILE_PATH +PublicVariables.SEP+ currentTestSuite+".xlsx");
					currentTestSuiteXL.put(currentTestSuite, testSuiteWithRunModeY);
					testCasesWithinTestSuite = CreateTestCasesFromTestSuite.CreateTestCases(currentTestSuite);
					if(testCasesWithinTestSuite.size() == 0){
						logger.trace("There are no testcases in the TestSuite " + currentTestSuite);				
					}
				}
			}
		}
		else{	
			logger.trace("There are no testsuites in the MasterTestSuite " + PublicVariables.MASTER_TEST_SUITE);
		}	
	}
}

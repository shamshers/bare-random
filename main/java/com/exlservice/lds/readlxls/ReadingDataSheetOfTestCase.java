package com.exlservice.lds.readlxls;

import java.util.Hashtable;
import com.exlservice.lds.libraries.DynamicQuery;
import com.exlservice.lds.libraries.PublicVariables;


/**
 * @author shamshersingh
 *
 */
public class ReadingDataSheetOfTestCase {


  public static Object[][] getData(String testName, String currentTestSuite, XLReader xls) {

    xls = new XLReader(
        DynamicQuery.CONFIG_EXCEL_FILE_PATH + PublicVariables.SEP + currentTestSuite + ".xlsx");
    String tmpStrTestSuiteDataSheetName = (PublicVariables.TEST_SUITE_DATA_SHEET_NAME);

    int counter = 0;
    int testStartRowNum = 1;
    while (!xls.getCellData(tmpStrTestSuiteDataSheetName, 0, testStartRowNum).equals(testName)
        && testStartRowNum < xls.getRowCount(tmpStrTestSuiteDataSheetName)) {
      testStartRowNum++;
      if (xls.getCellData(tmpStrTestSuiteDataSheetName, 0, testStartRowNum).equals(testName)) {
        counter++;
      }
    }

    if (counter == 0 && testStartRowNum != 1) {
      // Logging.log("There is no data for this test case in Data sheet --> Skipping the test");
      return null;
    }
    int colStartRowNum = testStartRowNum + 1;
    // Logging.log("Test starts from row number - "+testStartRowNum+", for test case -"+testName);

    // finding number of rows of data, stored in rows variable
    int dataStartRowNum = testStartRowNum + 2;
    int rows = 0;
    while (!xls.getCellData(tmpStrTestSuiteDataSheetName, 0, dataStartRowNum + rows).equals(""))
      rows++;
    // Logging.log("Total number of Data rows for test case - "+testName+"= "+rows);

    // finding no. of columns - stored indataStartRowNum testColumnCount variable
    int testColumnCount = 0;
    while (!xls.getCellData(tmpStrTestSuiteDataSheetName, testColumnCount, testStartRowNum + 1)
        .equals(""))
      testColumnCount++;
    // Logging.log("Total number of Columns for test case - "+testName+"= "+testColumnCount);

    Object data[][] = new Object[rows][1];
    Hashtable<String, String> testData = null;
    int index = 0;
    // extract data
    for (int RowNum = dataStartRowNum; RowNum < dataStartRowNum + rows; RowNum++) {
      testData = new Hashtable<String, String>();
      for (int cNum = 0; cNum < testColumnCount; cNum++) {
        String key = xls.getCellData(tmpStrTestSuiteDataSheetName, cNum, colStartRowNum);
        String value = xls.getCellData(tmpStrTestSuiteDataSheetName, cNum, RowNum);
        testData.put(key, value);
      }

      data[index][0] = testData;
      index++;

    }

    return data;
  }



}

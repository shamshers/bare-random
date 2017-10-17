package com.exlservice.lds.driver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.apache.log4j.Logger;
import com.exlservice.lds.libraries.DynamicQuery;
import com.exlservice.lds.libraries.Messages;
import com.exlservice.lds.libraries.PublicVariables;
import com.exlservice.lds.readlxls.XLReader;
import com.exlservice.lds.utillib.FwUtil;
import com.exlservice.lds.wrappers.WebObject;

/**
 * @author Shamsher This Class read the MasterTestSuite excel workbook to retrieve the name of
 *         testsuites with Yes runmode. After retrieving names of test suites it stores the name of
 *         suite and Corresponding workbook as an object of XLReader in ArrayList object. It uses
 *         CreateTestCasesFromTestSuite class to retrieve and create test cases files for testcases
 *         mentioned in each test suite.
 */
public class Controller {

  public static Object[] paramValues = null;
  public static Class<?>[] paramTypes = null;
  public static int lastTestStepRowExecuted = 0;
  public ArrayList<Integer> testResultSet;
  public static ArrayList<String> currentTestName = new ArrayList<String>();
  public static String TEST_DESCRIPTION;
  public static String DESCRIPTION;
  public static String strClassName;
  public static String OBJECT;
  public static String cellValue;
  public static String msg = "";
  public static String testlinkResult;
  public static String strKeywordName;
  private static final Logger LOGGER = Logger.getLogger(Controller.class);
  public static final Map<String, String> STOREHASHMAP = new HashMap<String, String>();
  boolean task = false;

  /**
   * @param testName
   * @param currentTestSuite
   * @param xls
   * @return
   */
  public static boolean getRunModeOfTestCase(String testName, String currentTestSuite,
      XLReader xls) {
    // Referring to Test suite workbook
    xls = new XLReader(
        DynamicQuery.CONFIG_EXCEL_FILE_PATH + PublicVariables.SEP + currentTestSuite + ".xlsx");

    String tmpStrTestSuiteTestCaseSheetName = PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME;
    String tmpStrTestCaseRunmodeCol = PublicVariables.RUNMODE_YESVALUE;
    // Traversing through whole test sheet to check run mode of specific test case
    int tmpTotalNumberOfRows = xls.getRowCount(tmpStrTestSuiteTestCaseSheetName);
    for (int rowNum = 2; rowNum <= tmpTotalNumberOfRows; rowNum++) {
      // Checking if any test case exist in the list with the given name
      if (xls.getCellData(tmpStrTestSuiteTestCaseSheetName, 0, rowNum).equals(testName)) {
        // Checking if the run mode of available test case is Y
        if (xls.getCellData(tmpStrTestSuiteTestCaseSheetName, 1, rowNum)
            .equalsIgnoreCase(tmpStrTestCaseRunmodeCol)) {
          return true;
        }
        // Break the traversing of sheet once we find the test case with the name passed to function
        break;
      }
    }
    // Returns false when we don't have the test case,with the name passed to function, in the test
    // suite
    return false;
  }

  // Static function to execute test steps
  /**
   * @param testName
   * @param testDesc
   * @param testLinkID
   * @param currentTestSuite
   * @param xls
   * @param data
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws InstantiationException
   * @throws ClassNotFoundException
   * @throws TestLinkAPIException
   */
  public static void executeTestStepsSerially(String testName, String testDesc, String testLinkID,
      String currentTestSuite, XLReader xls, Hashtable<String, String> data)
      throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
      InstantiationException, ClassNotFoundException, TestLinkAPIException {
    System.out.println("Updating OR************************");
    // Creating object of CreatePropertiesObjects class
    // Creating XLReader object corresponding to Test suite passed
    xls = new XLReader(
        DynamicQuery.CONFIG_EXCEL_FILE_PATH + PublicVariables.SEP + currentTestSuite + ".xlsx");
    // To store size of list storing name of test case
    int sizeOfcurrentTestName;
    int startTestCaseExecutionFromRow = 2;
    Controller obj = new Controller();
    int testStepsSheetRowCount = xls.getRowCount(PublicVariables.TEST_SUITE_TESTSTEPS_SHEET_NAME);

    // This block will get executed for two conditions for first and second iteration
    // For first time condition lastTestStepRowExecuted==0 will be true and for second iteration
    // condition currentTestName.get(0).equals(testName) will be true

    {
      // Adding the test case name to the list
      currentTestName.add(testName);
      // Retrieving size of the list
      sizeOfcurrentTestName = currentTestName.size();
      // Checking this condition because in the list test case name for each step is added and test
      // case name for multiple steps can be same
      {
        // Updating the start row counter
        // startTestCaseExecutionFromRow = lastTestStepRowExecuted+1;
        // Logging.log(String.format("Execution starting from %s and ending at %s row number" ,
        // startTestCaseExecutionFromRow, testStepsSheetRowCount));
        obj.executeKeywordsInTestCase(testName, testDesc, testLinkID, currentTestSuite, xls,
            startTestCaseExecutionFromRow, testStepsSheetRowCount, data);

        System.out
            .println("**************************executeKeywordsInTestCase() function finished...!");
      }

    }

  }

  private void executeKeywordsInTestCase(String testName, String TestDESC, String TestLink_TCID,
      String currentTestSuite, XLReader xls, int startFromRow, int endAtRow,
      Hashtable<String, String> data) throws InstantiationException, IllegalAccessException,
      ClassNotFoundException, TestLinkAPIException {
    int rowNum;
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy h-mm-ss a");
    String formatteddate = sdf.format(date);
    List<String> valueList = new ArrayList<String>();
    boolean rowResultProcessed = false;
    testResultSet = new ArrayList<Integer>();
    int rowResult = 1;
    // Temporary storing values retrieved from properties file
    String tmpStrTestSuiteTestStepsSheetName, ID;
    String tmpStrTaskSheetName = PublicVariables.TEST_SUITE_TASK_SHEET_NAME.trim();
    if (task == true) {
      tmpStrTestSuiteTestStepsSheetName = tmpStrTaskSheetName;
      ID = "TK_ID";
    } else {
      tmpStrTestSuiteTestStepsSheetName = PublicVariables.TEST_SUITE_TESTSTEPS_SHEET_NAME.trim();
      ID = "TCID";
    }
    LOGGER.trace("value of tmpStrTestSuiteTestStepsSheetName: " + tmpStrTestSuiteTestStepsSheetName
        + " " + ID);
    // This variable is used to skip the class loading and method matching corresponding to action
    // name when required data is available on test steps
    // sheet but not on data sheet
    boolean proceed = true;
    // Outer for-loop to traverse rows from test step sheet between startFromRow to endAtRow
    for (rowNum = startFromRow; rowNum <= endAtRow; rowNum++) {
      if (rowNum == startFromRow)
        System.out.println(
            "Executing steps..! from " + startFromRow + " to " + endAtRow + " at " + rowNum);
      // This condition is set to handle such conditions when there is no row in data now in excel
      // sheet but still poi library count them in row from excel sheet
      if (xls.getCellData(tmpStrTestSuiteTestStepsSheetName, ID, rowNum) == null) {
        System.out.println("No more data to be processed...!");
        break;
      }
      if (ID == "TK_ID") {
        if (!(xls.getCellData(tmpStrTestSuiteTestStepsSheetName, ID, rowNum).equals(testName))) {
          System.out.println("No more steps in this task to be processed...!");
          break;
        }
      }
      if (xls.getCellData(tmpStrTestSuiteTestStepsSheetName, ID, rowNum).equals(testName)) {
        strClassName = xls.getCellData(tmpStrTestSuiteTestStepsSheetName, "ELEMENT", rowNum);
        if (strClassName.matches("IF_.*")) {
          task = true;
          XLReader xls2 = new XLReader(DynamicQuery.CONFIG_EXCEL_FILE_PATH + PublicVariables.SEP
              + currentTestSuite + ".xlsx");
          int tmpNumRows = xls2.getRowCount(tmpStrTaskSheetName);
          int tmpi = 2, rowStart = 2;
          for (; tmpi <= tmpNumRows; tmpi++) {
            if (xls2.getCellData(tmpStrTaskSheetName, "TK_ID", tmpi) == null)
              continue;

            if (xls2.getCellData(tmpStrTaskSheetName, "TK_ID", tmpi).equals(strClassName)) {
              // To store the value of the row from which task steps get started
              // The first row where data will be equal to Task name will be heading row and steps
              // included in that task will be started from
              // next row, therefore tmpi+1
              rowStart = tmpi + 1;
              // To break the loop indirectly as break was breaking the outer loop construct even
              tmpi = tmpNumRows + 4;
            }
          }
          if (tmpi != tmpNumRows + 5) {
            rowResult = 1;
            LOGGER.info(
                "Specified Task " + strClassName + " is not available in Task Sheet of test suite");
          } else {
            // Retrieving conditional expression and variables from excel sheet
            String conditionalExpr = xls2.getCellData(tmpStrTaskSheetName, "ELEMENT", rowStart);
            LOGGER.debug("Retrieved conditional expression is: " + conditionalExpr);
            String varForExprr0 = xls2.getCellData(tmpStrTaskSheetName, "KEYWORD", rowStart);
            String varForExprr1 = xls2.getCellData(tmpStrTaskSheetName, "OBJECT", rowStart);
            String varForExprr2 = xls2.getCellData(tmpStrTaskSheetName, "DATA1", rowStart);
            String varForExprr3 = xls2.getCellData(tmpStrTaskSheetName, "DATA2", rowStart);
            String varForExprr4 = xls2.getCellData(tmpStrTaskSheetName, "DATA3", rowStart);
            // execute=1 denotes condition is true and we have to execute and execute=-1 denotes
            // should not execute IF task
            int execute = 1;
            if (conditionalExpr == null || conditionalExpr.trim().equals("")) {
              LOGGER.debug("Conditional expression is required for IF module");
              rowResult = 1;
              execute = -1;
            }
            if (!(varForExprr0 == null || varForExprr0.trim().equals(""))) {
              if (conditionalExpr.contains(varForExprr0))
                conditionalExpr =
                    conditionalExpr.replaceAll(varForExprr0, STOREHASHMAP.get(varForExprr0));
              else {
                rowResult = 1;
                execute = -1;
                LOGGER.trace("The first variable " + varForExprr0
                    + " passed is not available in conditional expression " + conditionalExpr);

              }
            }
            if (!(varForExprr1 == null || varForExprr1.trim().equals(""))) {
              if (conditionalExpr.contains(varForExprr1))
                conditionalExpr =
                    conditionalExpr.replaceAll(varForExprr1, STOREHASHMAP.get(varForExprr1));
              else {
                rowResult = 1;
                execute = -1;
                LOGGER.trace("The first variable " + varForExprr1
                    + " passed is not available in conditional expression " + conditionalExpr);
              }
            }
            if (!(varForExprr2 == null || varForExprr2.trim().equals(""))) {
              if (conditionalExpr.contains(varForExprr2))
                conditionalExpr =
                    conditionalExpr.replaceAll(varForExprr2, STOREHASHMAP.get(varForExprr2));
              else {
                rowResult = 1;
                execute = -1;
                LOGGER.trace("The first variable " + varForExprr2
                    + " passed is not available in conditional expression " + conditionalExpr);
              }
            }
            if (!(varForExprr3 == null || varForExprr3.trim().equals(""))) {
              if (conditionalExpr.contains(varForExprr3))
                conditionalExpr =
                    conditionalExpr.replaceAll(varForExprr3, STOREHASHMAP.get(varForExprr3));
              else {
                rowResult = 1;
                execute = -1;
                LOGGER.trace("The first variable " + varForExprr3
                    + " passed is not available in conditional expression " + conditionalExpr);
              }
            }
            if (!(varForExprr4 == null || varForExprr4.trim().equals(""))) {
              if (conditionalExpr.contains(varForExprr4))
                conditionalExpr =
                    conditionalExpr.replaceAll(varForExprr4, STOREHASHMAP.get(varForExprr4));
              else {
                rowResult = 1;
                execute = -1;
                LOGGER.trace("The first variable " + varForExprr4
                    + " passed is not available in conditional expression " + conditionalExpr);
              }
            }
            System.out.println("Updated conditional expression " + conditionalExpr);
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            Boolean bConditionRes = false;
            try {
              bConditionRes = (Boolean) engine.eval(conditionalExpr);
            } catch (Exception e) {
              e.printStackTrace();
              LOGGER.error("Some exception occured while evaluating conditional expression "
                  + conditionalExpr);
              rowResult = 1;
            }
            if (execute == 1 && bConditionRes.booleanValue()) {
              LOGGER.info("Condition specified " + conditionalExpr
                  + " is true so executing further steps...!");
              // Now after heading for task we will have first line to test the if condition
              // therefore rowStart+1
              executeKeywordsInTestCase(strClassName, TestDESC, TestLink_TCID, currentTestSuite,
                  xls2, rowStart + 1, tmpNumRows, data);
              rowResultProcessed = true;
              LOGGER.info("Executed the task successfully...!");
              task = false;
            } else {
              LOGGER.info("No further steps execution possible...as condition check result for "
                  + conditionalExpr + " is false");
            }
          }
        } else if (strClassName.matches("TK_.*")) {
          task = true;
          XLReader xls2 = new XLReader(DynamicQuery.CONFIG_EXCEL_FILE_PATH + PublicVariables.SEP
              + currentTestSuite + ".xlsx");
          int tmpNumRows = xls2.getRowCount(tmpStrTaskSheetName);
          int tmpi = 2, rowStart = 2;
          for (; tmpi <= tmpNumRows; tmpi++) {
            if (xls2.getCellData(tmpStrTaskSheetName, "TK_ID", tmpi) == null)
              continue;
            if (xls2.getCellData(tmpStrTaskSheetName, "TK_ID", tmpi).equals(strClassName)) {
              // To store the value of the row from which task steps get started
              // The first row where data will be equal to Task name will be heading row and steps
              // included in that task will be started from
              // next row, therefore tmpi+1
              rowStart = tmpi + 1;
              // To break the loop indirectly as break was breaking the outer loop construct even
              tmpi = tmpNumRows + 4;
            }
          }
          if (tmpi != tmpNumRows + 5) {
            rowResult = 1;
            LOGGER.info(
                "Specified Task " + strClassName + " is not available in Task Sheet of test suite");
          } else {
            System.out.println("Computed start end points in task: " + rowStart + " " + tmpNumRows);
            executeKeywordsInTestCase(strClassName, TestDESC, TestLink_TCID, currentTestSuite, xls2,
                rowStart, tmpNumRows, data);
            rowResultProcessed = true;
            System.out.println("Executed the task successfully...!");
            task = false;
          }
        } else {
          DESCRIPTION = xls.getCellData(tmpStrTestSuiteTestStepsSheetName, "DESCRIPTION", rowNum);
          strClassName = xls.getCellData(tmpStrTestSuiteTestStepsSheetName, "ELEMENT", rowNum);
          strKeywordName = xls.getCellData(tmpStrTestSuiteTestStepsSheetName, "KEYWORD", rowNum);
          OBJECT = xls.getCellData(tmpStrTestSuiteTestStepsSheetName, "OBJECT", rowNum);
          if (proceed) {
            for (int i = 5;; i++) {
              cellValue = xls.getCellData(tmpStrTestSuiteTestStepsSheetName, i, rowNum);
              if (cellValue.isEmpty() || cellValue == null || cellValue == "") {
                break;
              } else {
                valueList.add(data.get(cellValue));
              }
            }
            try {
              // Checking if function name is equal to Keyword retrieve from the test step
              // First inner if-construct inside inner for loop
              // Invoking function corresponding to Keyword and storing result in variable
              LOGGER.info("Value of proceed after invoking function for keyword: " + proceed + " "
                  + strKeywordName);
              LOGGER.trace(strKeywordName + " action invoked...!");
              try {
                String[] parameterValues = new String[valueList.size()];
                parameterValues = valueList.toArray(parameterValues);
                rowResult =
                    Controller.invokeKeyword(strClassName, strKeywordName, OBJECT, parameterValues);
                valueList.clear();
              } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Something went wrong while performing action " + strKeywordName
                    + " Probably element not located");
                rowResult = 1;
              }
              // break;
            } catch (Exception e) {
              LOGGER.trace("=================**************===================");
              System.out.println();
              LOGGER.trace("This test Failed....Refer to the Report");
              LOGGER.error(e);
            }
          }
        }
        LOGGER.info("value of rowResult before decision" + rowResult);
        /*
         * if(rowResultProcessed){ rowResultProcessed=false; }else { try { if(rowResult==
         * ConstVariables.ACTION_PASSED){ testlinkResult = TestLinkAPIResults.TEST_PASSED; msg =
         * TestDESC +" "+currentTestSuite+"_"+testName +"_rownum_" +rowNum; //int BUGID =
         * RedmineConnect.createIssue();
         * TestLinkHandler.reportTestCaseResult(DynamicQuery.CONFIG_PROJECT,
         * DynamicQuery.CONFIG_PLANNAME, TestLink_TCID, DynamicQuery.CONFIG_BUILD_NUMBER, msg,
         * testlinkResult, 0); proceed=true; } else if(rowResult == ConstVariables.ACTION_FAILED) {
         * lastTestStepRowExecuted = rowNum; ts.takeScreenShot(currentTestSuite+"_"+testName
         * +"_rownum_" +rowNum +"_"+ strKeywordName + "_"+formatteddate,currentTestSuite,
         * SetBaseState.driver); testlinkResult = TestLinkAPIResults.TEST_FAILED; msg =
         * TestDESC+" "+currentTestSuite+"_"+testName +"_rownum_" +rowNum; int BUGID =
         * 1;//RedmineConnect.createIssue();
         * TestLinkHandler.reportTestCaseResult(DynamicQuery.CONFIG_PROJECT,
         * DynamicQuery.CONFIG_PLANNAME, TestLink_TCID, DynamicQuery.CONFIG_BUILD_NUMBER, msg,
         * testlinkResult, BUGID); try{ Assert.fail("Failing the test case iteration as test " +
         * testName + " has failed at row - " + rowNum + " for KEYWORD - " +strKeywordName +
         * " and input data "); }catch(Throwable e){ LOGGER.error(e); } proceed=true; } else if
         * (rowResult==ConstVariables.ACTION_DEFECT) { lastTestStepRowExecuted = rowNum;
         * ts.takeScreenShot(currentTestSuite+"_"+testName +"_rownum_" +rowNum +"_"+ strKeywordName
         * + "_"+formatteddate,currentTestSuite, SetBaseState.driver); testlinkResult =
         * TestLinkAPIResults.TEST_FAILED; msg = TestDESC+" "+currentTestSuite+"_"+testName
         * +"_rownum_" +rowNum; int BUGID = 1;//RedmineConnect.createIssue();
         * TestLinkHandler.reportTestCaseResult(DynamicQuery.CONFIG_PROJECT,
         * DynamicQuery.CONFIG_PLANNAME, TestLink_TCID, DynamicQuery.CONFIG_BUILD_NUMBER, msg,
         * testlinkResult, BUGID); try{ Assert.fail("Failing the test case iteration as test " +
         * testName + " has failed at row - " + rowNum + " for KEYWORD - " +strKeywordName +
         * " and input data "); }catch(Throwable e){ LOGGER.error(e); } proceed=true; }
         * 
         * } catch (Exception e) { testlinkResult = TestLinkAPIResults.TEST_WRONG; String exception
         * = e.getMessage(); LOGGER.warn(exception); } }
         */

      }
      lastTestStepRowExecuted = rowNum;
    }
    if (lastTestStepRowExecuted < startFromRow) {
      LOGGER.warn("There are no test steps for the test case " + testName);
    }
  }

  /**
   * @param objInfo
   * @param argSize
   * @return
   */
  public static Class<?>[] getArgumentList(String objInfo, int argSize) {
    paramTypes = null;
    int isize = 0;
    if (!objInfo.isEmpty()) {
      paramTypes = new Class[argSize + 1];
      paramTypes[isize] = String.class;
      isize++;
    } else {
      paramTypes = new Class[argSize];
      isize = 0;
    }
    for (int j = 0; j < argSize; isize++) {
      paramTypes[isize] = String.class;
      j++;
    }
    return paramTypes;
  }



  /**
   * @param strClass
   * @param strFunc
   * @param objInfo1
   * @param parameterValues
   * @return
   */
  public static int invokeKeyword(String strClass, String strFunc, String objInfo1,
      String[] parameterValues) {

    int result = 1;
    result = getParameterTypesAndValues(objInfo1, parameterValues);
    LOGGER.trace("Before Invoke Keyword Through Reflection");
    if (result == 0)
      result = invokeKeywordThruReflection(strClass, strFunc);
    LOGGER.trace("After Invoke Keyword Through Reflection");
    return result;
  }


  /**
   * @param objObjInfo
   * @param parameterValues
   * @return
   */
  public static int getParameterTypesAndValues(String objObjInfo, String[] parameterValues) {
    int i = 0;
    int intArgumentSize = 0;
    int result = 0;
    try {
      if (parameterValues != null) {
        int paramSize = parameterValues.length;
        intArgumentSize = paramSize;
        if (!objObjInfo.isEmpty()) {
          intArgumentSize++;
        }
        paramValues = new Object[intArgumentSize];
        if (!objObjInfo.isEmpty()) {
          paramValues[i] = objObjInfo;
          i++;
        }
        for (int k = 0; k < paramSize; i++) {
          String strParamValue = null;
          strParamValue = parameterValues[k];
          strParamValue = strParamValue == null ? "" : strParamValue;

          String dataType = "string";
          if (dataType.equalsIgnoreCase("array")) {
            FwUtil.warningMsg = "";
            List<String> array = FwUtil.resolveDataSeparator(strParamValue, false, false);
            if (array == null) {
              result = 1;
              WebObject.REPORT.log(LogStatus.ERROR, "Action: " + strKeywordName
                  + "   Status: Failed   Message: " + FwUtil.errorMsg + " in test data.");
              return result;
            }
            if (!FwUtil.warningMsg.equalsIgnoreCase(""))
              WebObject.REPORT.log(LogStatus.WARNING, FwUtil.warningMsg);
            FwUtil.warningMsg = "";
            for (int a = 0; a < array.size(); a++) {
              if (((String) array.get(a)).length() > 0) {
                Object o = FwUtil.resolveSpecialCharactersAndVariables((String) array.get(a));
                String val = null;
                if (o != null) {
                  val = o.toString();
                } else {
                  result = 1;
                  WebObject.REPORT.log(LogStatus.WARNING, "Action: " + strKeywordName
                      + "   Status: Failed   Message: " + FwUtil.errorMsg + " in test data.");
                  return result;
                }
                array.set(a, val);
              }
            }
            if (!FwUtil.warningMsg.equalsIgnoreCase("")) {
              WebObject.REPORT.log(LogStatus.WARNING, FwUtil.warningMsg);
            }
            paramValues[i] = array;
          } else {
            String val = null;
            FwUtil.warningMsg = "";
            List<String> array = FwUtil.resolveDataSeparator(strParamValue, false, false);
            if (!FwUtil.warningMsg.equals("")) {
              WebObject.REPORT.log(LogStatus.WARNING, FwUtil.warningMsg);
            }
            if (array == null) {
              result = 1;
              WebObject.REPORT.log(LogStatus.WARNING, "Action: " + strKeywordName
                  + "   Status: Failed   Message: " + FwUtil.errorMsg + " in test data.");
              return result;
            }
            if (array.size() > 1) {
              result = 1;
              WebObject.REPORT.log(LogStatus.WARNING,
                  "Action: " + strKeywordName
                      + "   Status: Failed   Message: Syntax error in test data '" + '~'
                      + "' escape character required before '" + '^' + "'");
              return result;
            }
            FwUtil.warningMsg = "";
            Object o = FwUtil.resolveSpecialCharactersAndVariables((String) array.get(0));
            if (!FwUtil.warningMsg.equals("")) {
              LOGGER.warn(FwUtil.warningMsg);
            }
            if (o != null) {
              val = o.toString();
            } else {
              result = 1;
              LOGGER.error("Action: " + strKeywordName + "   Status: Failed   Message: "
                  + FwUtil.errorMsg + " in test data.");
              return result;
            }
            paramValues[i] = val;
          }
          k++;
        }
        getArgumentList(objObjInfo, paramSize);
      } else {
        paramTypes = null;
        paramValues = null;
        if (objObjInfo != null) {
          paramValues = new Object[1];
          paramValues[0] = objObjInfo;
          getArgumentList(objObjInfo, 0);
        }
      }
    } catch (Exception e) {
      result = 1;
      String err =
          "Exception occurred in method getParameterTypesAndValues of Class Controller\n" + e;
      LOGGER.error(err, e);
      LOGGER.error(
          "Exception occurred in method getParameterTypesAndValues of Class Controller. StackTrace :<br/>"
              + FwUtil.getStackTrace(e));
    }
    return result;
  }


  /**
   * @param strClass
   * @param strFunc
   * @return
   */
  public static int invokeKeywordThruReflection(String strClass, String strFunc) {
    int res = 1;
    try {
      if ((strClass != null) && (strClass != "")) {
        Class c = null;
        c = Class.forName(strClass);
        Method m = c.getMethod(strFunc, paramTypes);
        LOGGER.trace("KW Class=|| KW method=|" + strFunc);
        LOGGER.trace("Before Reflection Invoke");
        res = ((Integer) m.invoke(c.newInstance(), paramValues)).intValue();
        LOGGER.trace("After Reflection Invoke");
      } else {
        LOGGER.error("Object class name is null for strKeywordName= |" + strKeywordName + "|");
        LOGGER.error("Object class name is null");
        LOGGER.info("Action: " + strKeywordName + "    " + "Status: Failed    "
            + "Message: Object class name is null");
      }
    } catch (LinkageError e) {
      LOGGER.error("Linkage Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
          + strKeywordName + "|", e);
      LOGGER.error(e.toString());
      String errMsg = "Action: " + strKeywordName + "    " + "Status: Failed    "
          + "Message: An exception occured. Message:" + e.getMessage();
      LOGGER.info(errMsg);
    } catch (ClassNotFoundException e) {
      LOGGER.error(
          "ClassNotFoundException Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
              + strKeywordName + "|",
          e);
      DataLogger.writeToErrorLog(e.toString());
      String errMsg = "Action: " + strKeywordName + "    " + "Status: Failed    "
          + "Message: An exception occured. Message:" + e.getMessage();
      DataLogger.writeToInfoLog(errMsg);
    } catch (IllegalAccessException e) {
      LOGGER.error(
          "IllegalAccessException Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
              + strKeywordName + "|",
          e);
      String errMsg = "IllegalAccessException occurred in invokeKeywordThruReflection: Method "
          + strKeywordName + " cannot be accessed" + "\n" + e;
      DataLogger.writeToErrorLog(errMsg);
    } catch (InvocationTargetException e) {
      LOGGER.error(
          "InvocationTargetException Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
              + strKeywordName + "|",
          e);
      Throwable t = e.getTargetException();
      if ((t.toString().contains("SeleniumException"))
          && (t.getMessage().contains("Connection refused"))) {
        String expectionMsg =
            "Execution aborted. " + Messages.getProperty(Messages.SELENIUMSERVERNOTFOUND)
                + " , Exception:" + t.getLocalizedMessage();
        LOGGER.fatal(expectionMsg + ". Cannot Continue Execution. System will now Exit");

        String msg = "<span style='color:red;' ><strong>" + expectionMsg + "</strong></span>";
        DataLogger.writeToDebugAndErrorLogs(msg + " Hence a System exit is performed.");
        DataLogger.writeToInfoLog(msg);
        System.exit(3);
      }

      String errMsg = "Action: " + strKeywordName + "    " + "Status: Failed    "
          + "Message: An exception occured. Message:" + t.getMessage();
      DataLogger.writeToInfoLog(errMsg);
      DataLogger.writeToDebugAndErrorLogs("Action: " + strKeywordName + "    "
          + "Status: Failed    " + "Message: An exception occured. Target :" + t.toString()
          + "   ,  Message:" + t.getMessage());

    } catch (Exception e) {
      LOGGER.error("Generic Exception thrown in invokeKeywordThruReflection for strKeywordName= |"
          + strKeywordName + "|" + FwUtil.getStackTrace(e));


    }
    return res;
  }
}

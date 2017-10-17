package com.exlservice.lds.testcases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.exlservice.lds.libraries.PublicVariables;
import com.exlservice.lds.readlxls.ReadingTestSuiteXLWithRunmode;
import com.exlservice.lds.readlxls.XLReader;



/**
 * @author ssingh2
 *
 */
public class CreateTestCasesFromTestSuite {

  private static final Logger LOGGER = Logger.getLogger(CreateTestCasesFromTestSuite.class);

  /**
   * @param currentTestSuite
   * @return
   */
  public static ArrayList<String> CreateTestCases(String currentTestSuite) {
    XLReader xl = ReadingTestSuiteXLWithRunmode.currentTestSuiteXL.get(currentTestSuite);
    ArrayList<String> testCaseNames = new ArrayList<String>();
    ArrayList<String> testCaseDesc = new ArrayList<String>();
    ArrayList<String> testlinkTCID = new ArrayList<String>();
    File testSuiteFolder;
    File testCase = null;
    boolean flag;
    int rowCount = xl.getRowCount(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME);
    try {
      if (rowCount == 0) {
        LOGGER.info(
            "CANNOT PROCEED TO CREATE TESTCASES AS THERE ARE NO TESTCASES IN THIS TEST SUITE - "
                + currentTestSuite);
        return new ArrayList<String>();
      }
      for (int rowNum = 2; rowNum <= xl
          .getRowCount(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME); rowNum++) {
        LOGGER.info("The testcase name is "
            + xl.getCellData(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME, 0, rowNum));
        testCaseNames
            .add(xl.getCellData(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME, 0, rowNum));
        testCaseDesc.add(xl.getCellData(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME, 3, rowNum));
        testlinkTCID.add(xl.getCellData(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME, 4, rowNum));
        testSuiteFolder = new File(
            System.getProperty("user.dir") + PublicVariables.SEP + "src" + PublicVariables.SEP
                + "test" + PublicVariables.SEP + "java" + PublicVariables.SEP + currentTestSuite);
        if (!testSuiteFolder.exists()) {
          flag = testSuiteFolder.mkdir();
          LOGGER.info("Test Suite created true/false? " + flag);
        }
        testCase = testSuiteFolder;
        testCase = new File(System.getProperty("user.dir") + PublicVariables.SEP + "src"
            + PublicVariables.SEP + "test" + PublicVariables.SEP + "java" + PublicVariables.SEP
            + currentTestSuite + PublicVariables.SEP + testCaseNames.get(rowNum - 2) + ".java");
        if (!testCase.exists()) {
          testCase.createNewFile();
          LOGGER.info("File has been created " + testCaseNames.get(rowNum - 2));
          createJavaTestCaseFiles(testCase, currentTestSuite, xl, testCaseNames.get(rowNum - 2),
              testCaseDesc.get(rowNum - 2), testlinkTCID.get(rowNum - 2));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.warn("Unable to create Folder or java file");
    }
    return testCaseNames;
  }

  /**
   * @param newTestCase
   * @param currentTSname
   * @param xls
   * @param testCaseName
   * @param TESTDESC
   * @param TestLinkTCID
   */
  public static void createJavaTestCaseFiles(File newTestCase, String currentTSname, XLReader xls,
      String testCaseName, String TESTDESC, String TestLinkTCID) {
    StringBuilder sbimport = new StringBuilder();
    StringBuilder sbclass = new StringBuilder();
    StringBuilder sbclass2 = new StringBuilder();

    try {
      PrintWriter print = new PrintWriter(newTestCase);
      print.println("package " + currentTSname + ";");
      BufferedReader br = new BufferedReader(
          new FileReader(System.getProperty("user.dir") + PublicVariables.SEP + "resources"
              + PublicVariables.SEP + "templates" + PublicVariables.SEP + "import.template"));
      String line = br.readLine();

      while (line != null) {
        sbimport.append(line);
        sbimport.append('\n');
        line = br.readLine();
      }
      String everything = sbimport.toString();
      print.println(everything);
      print.println("public class " + testCaseName + " extends TestBase{");
      print.println("public String testName = \"" + testCaseName + "\";");
      print.println("public String currentTestSuite = \"" + currentTSname + "\";");
      print.println("public String testcaseDesc = \"" + TESTDESC + "\";");
      print.println("public String testlinkTCId = \"" + TestLinkTCID + "\";");
      br = new BufferedReader(
          new FileReader(System.getProperty("user.dir") + PublicVariables.SEP + "resources"
              + PublicVariables.SEP + "templates" + PublicVariables.SEP + "testclass1.template"));
      line = br.readLine();

      while (line != null) {
        sbclass.append(line);
        sbclass.append('\n');
        line = br.readLine();
      }
      everything = sbclass.toString();
      print.println(everything);
      print.println("public void do" + testCaseName
          + "(Hashtable<String,String> data) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException , Exception{");
      for (int row = 2; row <= xls
          .getRowCount(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME); row++) {
        if (xls.getCellData(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME, 0, row)
            .equals(testCaseName)) {
          if (xls.getCellData(PublicVariables.TEST_SUITE_TESTCASE_SHEET_NAME, 2, row)
              .equalsIgnoreCase("Y")) {
            br = new BufferedReader(new FileReader(System.getProperty("user.dir")
                + PublicVariables.SEP + "resources" + PublicVariables.SEP + "templates"
                + PublicVariables.SEP + "testclass2parallel.template"));
            line = br.readLine();

            while (line != null) {
              sbclass2.append(line);
              sbclass2.append('\n');
              line = br.readLine();
            }
            everything = sbclass2.toString();
            print.println(everything);
            print.flush();
          } else {
            br = new BufferedReader(new FileReader(System.getProperty("user.dir")
                + PublicVariables.SEP + "resources" + PublicVariables.SEP + "templates"
                + PublicVariables.SEP + "testclass2.template"));
            line = br.readLine();

            while (line != null) {
              sbclass2.append(line);
              sbclass2.append('\n');
              line = br.readLine();
            }
            everything = sbclass2.toString();
            print.println(everything);
            print.flush();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error(
          "Unable to locate text file import.template or testclass.template OR unable to locate the targeted .java file");
    }
  }
}

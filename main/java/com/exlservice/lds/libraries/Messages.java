package com.exlservice.lds.libraries;

import java.io.InputStream;
import java.util.Properties;

public class Messages {
  public static String INVALIDBITMAPFILEPATH = "InvalidBitmapFilePath";
  public static String INVALIDLOGFILEPATH = "InvalidLogFilePath";
  public static String CALLCONTROLLERERROR = "CallControllerError";
  public static String SELENIUMINVOKEERROR = "SeleniumInvokeError";
  public static String SUITEXMLNOTFOUND = "SuiteXMLNotFound";
  public static String MAPXMLNOTFOUND = "MapXMLNotFound";
  public static String INFINITYTESTEXECUTION = "InfinityTestExecution";
  public static String NOTESTCASESINSUITEXML = "NoTestcasesInSuiteXML";
  public static String SELENIUMSERVERNOTFOUND = "SeleniumServerNotFound";
  public static String CONFIGPARAMETERVALUEMISSING = "ConfigParameterValueMissing";
  public static String CONFIGNUMERICSYNCTIMEINSECONDS = "ConfigNumericSyncTimeInSeconds";
  public static String CONFIGERROR = "ConfigError";
  public static String IMPORTLOGCREATIONFAILURE = "ImportLogCreationFailure";
  public static String EXPORTLOGCREATIONFAILURE = "ExportLogCreationFailure";
  public static String IMPORTLOGERROR = "ImportLogError";
  public static String EXPORTLOGERROR = "ExportLogError";
  private static Properties messageList = new Properties();

  static {
    try {
      Messages singleObject = new Messages();
      InputStream input =
          singleObject.getClass().getClassLoader().getResourceAsStream("Messages.Properties");
      if (input != null)
        messageList.load(input);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static String getProperty(String key) {
    return messageList.getProperty(key);
  }
}

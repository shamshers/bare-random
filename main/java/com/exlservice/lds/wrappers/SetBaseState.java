package com.exlservice.lds.wrappers;


import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import com.exlservice.lds.libraries.ChromeLauncher;
import com.exlservice.lds.libraries.FirefoxLauncher;
import com.exlservice.lds.libraries.InternetExplorerLauncher;
import com.exlservice.lds.libraries.WebDriverHandler;

/**
 * @author Shamsher
 *
 */
public class SetBaseState extends WebObject {

  // public static WebDriver driver;
  public static WebElement query;
  public static long maximumFindObjectTime;
  public static long defaultFindObjectTime = 120000L;
  public static long IntervalTimeOut = 2000L;
  private static final Logger logger = Logger.getLogger(SetBaseState.class);
  public static String Browser = null;

  public int doSetBaseState(String Browser) {
    int result = 1;
    WebDriverHandler driverHandler = null;
    try {
      if (Browser.equalsIgnoreCase("firefox")) {
        driverHandler = new FirefoxLauncher();
        logger.debug("Before launching FirefoxLauncher");
        driverHandler.launchWebDriver();
        logger.debug("After launching FirefoxLauncher. Browser successfully launched");
      } else if (Browser.equalsIgnoreCase("iexplore")) {
        driverHandler = new InternetExplorerLauncher();
        logger.debug("Before launching InternetExplorerLauncher");
        driverHandler.launchWebDriver();
        logger.debug("After launching InternetExplorerLauncher. Browser successfully launched");
      } else if (Browser.equalsIgnoreCase("chrome")) {
        driverHandler = new ChromeLauncher();
        logger.debug("Before launching ChromeLauncher");
        driverHandler.launchWebDriver();
        logger.debug("After launching ChromeLauncher. Browser successfully launched");
      }
      REPORT.log(LogStatus.PASS, Browser + " Browser opened successfully");
      result = 0;
    } catch (Exception e) {
      String errMsg = "Exception in Launching browser";
      logger.error(errMsg, e);
      REPORT.log(LogStatus.FAIL, Browser + " Browser opened successfully");
      result = 1;
    }
    return result;
  }

  public void cleanUp(String Browser) {
    driver.quit();

    if (Browser.equals("chrome"))
      try {
        Runtime runtime = Runtime.getRuntime();
        logger.trace("About to kill google chrome as part of clean up");
        runtime.exec("taskkill /F /IM chromedriver.exe");
        logger.trace("google chrome killed successfully");
      } catch (Exception e) {
        logger.error("Exception while killing google chrome", e);
        String errMsg = "Unable to close the process chromedriver.exe.";
        REPORT.log(LogStatus.ERROR, errMsg + " Message:" + e.toString());
      }
  }

}


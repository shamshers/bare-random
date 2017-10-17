package com.exlservice.lds.libraries;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.chrome.ChromeDriver;
import com.exlservice.lds.wrappers.SetBaseState;


/**
 * @author shamshersingh
 *
 */
public class ChromeLauncher extends WebDriverHandler {
  public void launchWebDriver() {
    System.setProperty("webdriver.chrome.driver", DynamicQuery.CONFIG_CHROMEPATH);
    SetBaseState.driver = new ChromeDriver();
    SetBaseState.driver.manage().timeouts().implicitlyWait(SetBaseState.maximumFindObjectTime,
        TimeUnit.SECONDS);
    SetBaseState.driver.manage().timeouts().pageLoadTimeout(
        Integer.parseInt(DynamicQuery.CONFIG_PAGELOAD_PERFORMANCE_BENCHMARK), TimeUnit.SECONDS);
  }

  public void launchWebdriverCustomProfile(String profilePath) {}
}

package com.exlservice.lds.libraries;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.exlservice.lds.wrappers.SetBaseState;


public class FirefoxLauncher extends WebDriverHandler {
  public void launchWebDriver() {
    SetBaseState.driver = new FirefoxDriver();
    SetBaseState.driver.manage().timeouts().implicitlyWait(SetBaseState.maximumFindObjectTime,
        TimeUnit.SECONDS);
    SetBaseState.driver.manage().timeouts().pageLoadTimeout(
        Integer.parseInt(DynamicQuery.CONFIG_PAGELOAD_PERFORMANCE_BENCHMARK), TimeUnit.SECONDS);
  }


  public void launchWebdriverCustomProfile(String profilePath) {}
}

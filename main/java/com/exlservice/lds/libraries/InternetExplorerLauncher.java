package com.exlservice.lds.libraries;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.exlservice.lds.wrappers.SetBaseState;

public class InternetExplorerLauncher extends WebDriverHandler {
  public void launchWebDriver() {

    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    capabilities.setCapability(
        InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
    if (DynamicQuery.CONFIG_JAVASCRIPT_ENABLED.equalsIgnoreCase("true"))
      capabilities.setJavascriptEnabled(true);
    else
      capabilities.setJavascriptEnabled(false);
    if (!DynamicQuery.CONFIG_PROXY.equalsIgnoreCase(""))
      capabilities.setCapability(CapabilityType.PROXY, DynamicQuery.CONFIG_PROXY);
    System.setProperty("webdriver.ie.driver", DynamicQuery.CONFIG_IEPATH);
    SetBaseState.driver = new InternetExplorerDriver();
    SetBaseState.driver.manage().timeouts().implicitlyWait(SetBaseState.maximumFindObjectTime,
        TimeUnit.SECONDS);
    SetBaseState.driver.manage().timeouts().pageLoadTimeout(
        Integer.parseInt(DynamicQuery.CONFIG_PAGELOAD_PERFORMANCE_BENCHMARK), TimeUnit.SECONDS);
  }


  public void launchWebdriverCustomProfile(String profilePath) {}
}

package com.infinityfw.wrappers;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Shamsher
 *
 */
public class Page extends WebObject {
  public int DoubleClickLinkByText(String object, String link) {
    int result = 1;
    String strMethod = Controller.strKeywordName;

    if ((link == null) || (link.trim().equals(""))) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message: The link's text passed is null. Please Verify.");
      result = 1;
      return result;
    }

    WebElement locator = exist(object, true);
    if ((locator == null) && (!WebObject.EXISTLOCATORVALUE)) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message: Failed to retrieve the locator value.");
      result = 1;
      return result;

    }
    if (locator == null) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message: The page does not exist , please verify");
      result = 1;
      return result;
    }
    try {
      WebElement linkLoc = locator.findElement(By.linkText(link));
      Actions action = new Actions(SetBaseState.driver);
      action.doubleClick(linkLoc).perform();
      REPORT.log(LogStatus.PASS, "Action: " + strMethod + "    " + "Status: Passed    "
          + "Message: The '" + link + "' link is double clicked successfully.");
      result = 0;
      return result;

    } catch (NoSuchElementException abc) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message: Failed to find the link with text:'" + link + "' , please verify");
      result = 1;
      return result;

    } catch (ElementNotVisibleException enve) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message:  The link is not visible and hence cannot be double-click, please verify.");
      result = 1;
      return result;
    }
  }

  public int ClickLinkByText(String object, String link) {
    int result = 1;
    String strMethod = Controller.strKeywordName;

    if ((link == null) || (link.trim().equals(""))) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message: The link's text passed is null. Please Verify.");
      result = 1;
      return result;
    }

    WebElement locator = exist(object, true);
    if ((locator == null) && (!WebObject.EXISTLOCATORVALUE)) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message: Failed to retrieve the locator value.");
      result = 1;
      return result;
    }
    if (locator == null) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message: The page does not exist , please verify");
      result = 1;
      return result;
    }
    try {
      WebElement linkLoc = locator.findElement(By.linkText(link));
      linkLoc.click();
      REPORT.log(LogStatus.PASS, "Action: " + strMethod + "    " + "Status: Passed    "
          + "Message: The '" + link + "' link is clicked successfully.");
      result = 0;
      return result;
    } catch (NoSuchElementException abc) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message: Failed to find the link with text:'" + link + "' , please verify");
      result = 1;
      return result;
    } catch (ElementNotVisibleException enve) {
      REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    "
          + "Message:  The link is not visible and hence cannot be click, please verify.");
      result = 1;
      return result;
    }

  }

  public void scrollingToBottomofPage() {
    ((JavascriptExecutor) SetBaseState.driver)
        .executeScript("window.scrollTo(0, document.body.scrollHeight)");
  }

  public void scrollingToElementofPage(String object) {
    WebElement element = exist(object, true);
    ((JavascriptExecutor) SetBaseState.driver).executeScript("arguments[0].scrollIntoView();",
        element);
  }
}

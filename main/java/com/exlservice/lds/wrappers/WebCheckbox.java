package com.infinityfw.wrappers;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebElement;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Shamsher
 *
 */
public class WebCheckbox extends WebObject
{
  public int UnCheck(String object)
  {
    
    String objClass = getClass().getSimpleName().toString();
    String strMethod = Controller.strKeywordName;

    WebElement locator = exist(object, true);
    if ((locator == null) && (!WebObject.EXISTLOCATORVALUE)) {
       REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Failed to retrieve the locator value.");
       return 1;
    }if (locator == null) {
       REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The " + objClass + " does not exist , please verify");
       return 1;
    }

    try
    {
      if (locator.isEnabled()) {
        if (locator.isSelected())
          locator.click();
      }
      else {
         REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
          "Status: Failed    " + 
          "Message:  The " + objClass + " is disabled and cannot be un-check, please verify.");
         return 1;
      }
    } catch (ElementNotVisibleException enve) {
       REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message:  The " + objClass + " is not visible and hence cannot be un-check, please verify.");
       return 1;
    }
    try
    {
      Thread.sleep(SetBaseState.IntervalTimeOut);
    } catch (Exception localException) {
    }
    boolean checked = locator.isSelected();

    if (checked) {
       REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message:  The " + objClass + " is checked even after an un-check operation is performed , please verify.");
       return 1;
    } else {
       REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
        "Status: Passed    " + 
        "Message:  The " + objClass + " is un-checked.");
       return 0;
    }
    
  }
}

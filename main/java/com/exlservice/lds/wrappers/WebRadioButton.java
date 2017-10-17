package com.infinityfw.wrappers;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebElement;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.LogStatus;


/**
 * @author Shamsher
 *
 */
public class WebRadioButton extends WebObject
{
	  public int Check(String object)
	  {
		  int result = 1;       
	    String objClass = getClass().getSimpleName().toString();
	    String strMethod = Controller.strKeywordName;
	    WebElement locator = exist(object, true);
	    if ((locator == null) && (!WebObject.EXISTLOCATORVALUE)) {
	    	REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
	        "Status: Failed    " + 
	        "Message: Failed to retrieve the locator value.");
	    	 result = 1;
	         return result;
	    }if (locator == null) {
	    	REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
	        "Status: Failed    " + 
	        "Message: The " + objClass + " does not exist , please verify");
	    	 result = 1;
	         return result;
	    }

	    try
	    {
	      if (locator.isEnabled()) {
	        if (!locator.isSelected())
	          locator.click();
	      }
	      else {
	    	  REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
	          "Status: Failed    " + 
	          "Message:  The " + objClass + " is disabled and cannot be check, please verify.");
	      }
	    } catch (ElementNotVisibleException enve) {
	    	REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
	        "Status: Failed    " + 
	        "Message:  The " + objClass + " is not visible and hence cannot be select, please verify.");
	    	 result = 1;
	         return result;
	    }
	    try
	    {
	      Thread.sleep(SetBaseState.IntervalTimeOut);
	    } catch (Exception localException) {
	    }
	    boolean checked = locator.isSelected();

	    if (checked) {
	    	REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
	        "Status: Passed    " + 
	        "Message:  The " + objClass + " is checked.");
	    	 result = 0;
	         return result;
	    } else {
	    	REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
	        "Status: Failed    " + 
	        "Message:  The " + objClass + " is not checked , even after the check operation is performed ,please verify.");
	    	 result = 1;
	         return result;
	    }
	  }
	
}

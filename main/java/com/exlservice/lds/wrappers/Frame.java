package com.infinityfw.wrappers;

import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebElement;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Shamsher
 *
 */
public class Frame extends WebObject
{
	/**
	 * @param object
	 */
	public int SelectFrame(String object)
	{
	    int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;
		WebElement locator = exist(object, true);
		if ((locator == null) && (!WebObject.EXISTLOCATORVALUE)) {

			REPORT.log(LogStatus.FAIL, Controller.DESCRIPTION, "Action: " + strMethod + "    " + "Message: Failed to retrieve the locator value.");
		      result = 1;
		      return result;

		}if (locator == null) {
			REPORT.log(LogStatus.FAIL, Controller.DESCRIPTION,"Action: " + strMethod + "    " +
					"Message: The " + objClass + " does not exist , please verify");
		      result = 1;
		      return result;

		}
		try {
			SetBaseState.driver.switchTo().frame(locator);
			REPORT.log(LogStatus.PASS, Controller.DESCRIPTION,"Action: " + strMethod + "    " +  
					"Message: The " + objClass + " is selected successfully.");
			result=0;
		} catch (NoSuchFrameException nsfe) {
			REPORT.log(LogStatus.FAIL, Controller.DESCRIPTION,"Action: " + strMethod + "    " +
					"Message: Failed to select the frame. Either the " + objClass + " does not exist " + 
					"OR the given element is neither an IFRAME nor a FRAME element");
			  result = 1;
		      return result;
		}
		return	result;
	}
}

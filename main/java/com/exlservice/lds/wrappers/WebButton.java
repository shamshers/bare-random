package com.infinityfw.wrappers;

import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import com.exlservice.lds.utillib.CommonUtil;
import com.exlservice.lds.utillib.NumberUtil;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Shamsher
 *
 */
public class WebButton extends WebObject
{
	public int Click(String object)
	{
		int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			result = 1;
			return result;
		}if (query == null) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " does not exist , please verify");
			result = 1;
			return result;
		}

		query.click();
		REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " is clicked successfully.");
		result = 0;
		return result;
	}

	public int ClickAndWait(String object)
	{
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: Failed to retrieve the locator value.");
			return 1;

		}if (query == null) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: The " + objClass + " does not exist , please verify");
			return 1;

		}

		query.click();
		try
		{
			CommonUtil.Wait(SetBaseState.maximumFindObjectTime / 1000L);
		} catch (Exception localException) {
		}
		REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
				"Status: Passed    " + 
				"Message: The " + objClass + " is clicked successfully.");
		return 0;
	}

	public int ClickandchooseOkOnNextConfirmation(String object)
	{
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: Failed to retrieve the locator value.");
			return 1;

		}if (query == null) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: The " + objClass + " does not exist , please verify");
			return 1;

		}
		query.click();
		REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
				"Status: Passed    " + 
				"Message: The " + objClass + " is clicked successfully.");
		Alert alert;
		try
		{

			alert = SetBaseState.driver.switchTo().alert();
			alert.accept();
			REPORT.log(LogStatus.PASS,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Passed    " + 
					"Message: The dialog is accepted.");
			return 0;
		}
		catch (NoAlertPresentException e)
		{

			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: An alert/confirmation dialog does not exist, please verify.");
			return 1;
		}



	}

	public int DoubleClick(String object)
	{
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			return 1;

		}if (query == null) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " does not exist , please verify");

			return 1;
		}

		new Actions(SetBaseState.driver).doubleClick(query).build().perform();

		REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " is double clicked successfully.");

		return 0;
	}

	public int RightClick(String object, String option)
	{
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		if (option.equals("")) {
			REPORT.log(LogStatus.FAIL,"Since a valid value for the option to be selected is not given, assuming the default value as '1'");
			return 1;
		}
		else if (!NumberUtil.isInteger(option)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The option value '" + option + 
					"' is not a valid integer, please verify.");

			return 1;
		}

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			return 1;

		}if (query == null) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " does not exist , please verify");
			return 1;

		}

		int optionToClick = Integer.parseInt(option);
		Actions action = new Actions(SetBaseState.driver).contextClick(query);
		Actions finalAction = null;

		while (optionToClick != 0) {
			finalAction = action.sendKeys(new CharSequence[] { Keys.ARROW_DOWN });
			optionToClick--;
		}
		try {
			finalAction.sendKeys(new CharSequence[] { Keys.RETURN }).build().perform();
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " is right clicked successfully.");
			return 0;

		} catch (Exception e) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + 
					"Message: Failed to select the option. The option may not exist.Please verify" + e.getMessage());
			return 1;
		}

	}

	public int ClickIfExists(String object) {
		  int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			  result = 1;
		      return result;
		}if (query == null) {
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " does not exist, continue without click.");
			  result = 0;
		      return result;
		}
		query.click();
		REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " is clicked successfully.");
		  result = 0;
	      return result;

	}

	public int ClickAt(String object, String x, String y) {
		int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;
		int intX = 0;
		int intY = 0;

		if (x.equals("")) {
			REPORT.log(LogStatus.INFO,"Since a valid value for X cordinate is not given, assuming the default value as '0'");
			intX = 0; } else {
				if (!NumberUtil.isInteger(x.trim())) {
					REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The X value '" + x + 
							"' is not a valid positive integer, please verify.");
					result = 1;
					return result;
				}

				intX = Integer.parseInt(x.trim());
			}
		if (y.equals("")) {
			REPORT.log(LogStatus.INFO,"Since a valid value for Y cordinate is not given, assuming the default value as '0'");
			intY = 0; } else {
				if (!NumberUtil.isInteger(y.trim())) {
					REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The Y value '" + y + 
							"' is not a valid positive integer, please verify.");
					result = 1;
					return result;
				}

				intY = Integer.parseInt(y.trim());
			}

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			result = 1;
			return result;
		}if (query == null) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " does not exist , please verify");
			result = 1;
			return result;
		}

		Actions builder = new Actions(SetBaseState.driver);
		builder.moveToElement(query, intX, intY).click().build().perform();

		REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: Click is successful on " + objClass + " on the co-ordinates " + intX + 
				"," + intY);
		result =0;
		return result;

	}

}

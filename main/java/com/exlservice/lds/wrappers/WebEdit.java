package com.infinityfw.wrappers;


import org.openqa.selenium.WebElement;
import com.exlservice.lds.utillib.CommonUtil;
import com.exlservice.lds.utillib.FwUtil;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Shamsher
 *
 */
public class WebEdit extends WebObject
{
	public int StoreText(String object, String strKey)
	{
		int result = 1;
		String strMethod = Controller.strKeywordName;

		if ((strKey == null) || (strKey.trim().equals("")) || (strKey.isEmpty())) {
			REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    " + "Message: The Key passed is not valid. Please Verify.");
			result = 1;
			return result;
		}
		WebElement query = exist(object, true);
		if ((query == null) && (!WebObject.EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			result = 1;
			return result;
		}if (query == null) {
			REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    " + "Message: The webedit does not exist , please verify");
			result = 1;
			return result;
		}
		try {
			Thread.sleep(SetBaseState.IntervalTimeOut);
		} catch (Exception localException) {
		}
		String data = query.getAttribute("value");
		if (data.isEmpty()) {
			data = "";
		}
		if (FwUtil.storeData(strKey, data)) {
			REPORT.log(LogStatus.PASS, "Action: " + strMethod + "    " + "Status: Passed    " + "Message: The editbox value '" + data + 
					"' is stored successfully in the key '" + strKey + "'.");
			result = 0;
		} else {
			REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to store the the editbox value '" + data + "' in the key " + 
					strKey + "'.");
			result = 1;
		}
		return result;
	}

	public int Set(String object, String value)
	{
		int result = 1;
		String strMethod = Controller.strKeywordName;

		if (value != null) {
			if ((value.trim().equals("")) || (value.isEmpty())) {
				REPORT.log(LogStatus.WARNING, "Since a valid value for the parameter is not given, assuming the default value as a zero length empty string .");
				value = "";
			}
		} else {
			value = "";
			REPORT.log(LogStatus.WARNING, "Since a valid value for the parameter is not given, assuming the default value as a zero length empty string .");
		}

		WebElement query = exist(object, true);
		if ((query == null) && (!WebObject.EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			result = 1;
			return result;
		}if (query == null) {
			REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + "Status: Failed    " + "Message: The webedit does not exist , please verify");
			result = 1;
			return result;
		}

		query.clear();

		if (strMethod.equalsIgnoreCase("SecureSet"))
			try {
				String dValue = CommonUtil.getSecureData(value);
				query.sendKeys(new CharSequence[] { dValue });
			} catch (Exception e) {
				REPORT.log(LogStatus.ERROR, e.getMessage());
				REPORT.log(LogStatus.FAIL, "Action: " + strMethod + "    " + 
						"Status: Failed    " + 
						"Message: In correct data encryption format '" + value + "'.");
				result = 1;
				return result;
			}
		else {
			query.sendKeys(value);
		//	query.sendKeys(new CharSequence[] { value });
		}

		REPORT.log(LogStatus.PASS, "Action: " + strMethod + "    " + "Status: Passed    " + "Message: The value '" + value + "' is set successfully.");
		result = 0;

		return result;
	}
	
	public int doInputText(String Object, String value){
		int result = 1;

		WebElement element = exist(Object, true);
		try{		
			
			element.sendKeys(value);
			
		
		}catch(Exception e){
			
		
		}

		return result;
	}

}

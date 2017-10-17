package com.infinityfw.wrappers;


import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.omg.CORBA.Request;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import com.exlservice.lds.libraries.DynamicQuery;
import com.exlservice.lds.utillib.CommonUtil;
import com.exlservice.lds.utillib.FwUtil;
import com.exlservice.lds.utillib.NumberUtil;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Shamsher
 *
 */
public class General extends WebObject
{

	/**
	 * @param waitTime
	 * @return
	 */
	public int WaitForPageToLoad(String waitTime)
	{
		long timeToWait;
		if (waitTime.equalsIgnoreCase("")) {
			waitTime = Long.toString(SetBaseState.maximumFindObjectTime / 1000L);
			timeToWait = Long.parseLong(waitTime);
		} else {
			if (!NumberUtil.isPostiveInteger(waitTime)) {
				REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
						"Status: Failed\t\t" + 
						"Message: The wait time '" + waitTime + "' is not a valid integer. Please verify");
				return 1;
			}
			timeToWait = Long.parseLong(waitTime.trim());
		}
		if (CommonUtil.Wait(timeToWait))
		{
			REPORT.log(LogStatus.PASS,"Action: " + Controller.strKeywordName + "    " + 
					"Status: Passed\t\t" + 
					"Message: Page is loaded successfully.");
			return 0;
		}

		REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
				"Status: Failed\t\t" + 
				"Message: Timeout waiting for Page Load Request to complete '" + timeToWait + "' second.");
		return 1;
	}

	/**
	 * @param locator
	 * @param locatorType
	 * @return
	 */
	public int SelectFrame(String locator)
	{
		int result=1;
		String strMethod = Controller.strKeywordName;

		if (locator == null)
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: Either the Locator/LocatorType is invalid. Please verify.");


		}

		if (locator.equalsIgnoreCase("")) 
		{
			SetBaseState.driver.switchTo().defaultContent();
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status: Passed    " + 
					"Message:Switched to the default frame successfully.");
			result=0;
		}

		WebElement element = exist(locator, true);
		if (element == null)
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: Either the frame does not exist or an error occurred, please verify");
			result = 1;
			return result;
		}
		try {
			SetBaseState.driver.switchTo().frame(element);
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status: Passed    " + 
					"Message: The frame is selected successfully.");
			result =0;
		}
		catch (NoSuchFrameException nsfe) {
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: Failed to select the frame. Either the frame does not exist " + 
					"OR the given element is neither an IFRAME nor a FRAME element");
			result = 1;
			return result;
		}
		return result;
	}

	/**
	 * @return
	 */
	public int MaximizeBrowser() {
		int result;
		String strMethod = Controller.strKeywordName;
		try
		{
			SetBaseState.driver.manage().window().maximize();
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status: Passed    " + 
					"Message: The browser is maximize successfully.");
			result=0;

		} catch (WebDriverException wde) {
			SetBaseState.driver.manage().window().maximize();
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: Either the browser window does not exist or an error occurred. Please verify. " + 
					wde.getMessage());
			result=1;

		}
		return result;
	}

	/**
	 * @param windowName
	 * @return
	 */
	public int ClosePopUpBrowser(String windowName)
	{
		int result=1;
		String strMethod = Controller.strKeywordName;

		if ((windowName == null) || (windowName.trim().equalsIgnoreCase(""))) {
			SetBaseState.driver.manage().window().maximize();

			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: The window name/handle is invalid, please verify.");
			result=1;
		}
		try
		{
			SetBaseState.driver.switchTo().window(windowName).close();
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status: Passed    " + "Message: The pop-up browser " + 
					windowName + " is closed");
			result=0;
		} catch (WebDriverException wde) {
			SetBaseState.driver.manage().window().maximize();

			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: Failed to close the pop-up browser with name/handle: '" + 
					windowName + 
					"'. Either the browser window does not exist or an error occurred. Please verify. " + 
					wde.getMessage());
			result=1;
		}
		return result;
	}

	/**
	 * @param url
	 * @return
	 */
	public int OpenURL(){
		int result =1;
		int resp_code;
		String url = DynamicQuery.CONFIG_TestURL;
		String strMethod = Controller.strKeywordName;
		if ((url == null) || (url.trim().equals(""))) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + "Message:The url '" + url + 
					"' is invalid , please verify.");
			result =1;
		}
		try
		{
			 resp_code = Request.Get(DynamicQuery.CONFIG_TestURL).execute().returnResponse().getStatusLine().getStatusCode();
			 if(resp_code==200){
				 REPORT.log(LogStatus.PASS,"The TESTURL in Config.properties is a valid URL");
					SetBaseState.driver.get(url);
					REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
							"Status: Passed    " + "Message: The URL '" + url + 
							"' is set successfully.");
					result=0;
				}
				else{
					REPORT.log(LogStatus.FAIL,"The TESTURL in Config.properties is an INVALID URL, please check and update");
					result =1;
				}
		} catch (Exception localException) {
		}
		return result;
		
	}

	/**
	 * @param key
	 * @param data
	 * @return
	 */
	public int StoreVariable(String key, String data) {
		int result = 1;
		String strMethod = Controller.strKeywordName;

		if ((key == null) || (key.trim().equalsIgnoreCase("")))
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: The key passed is either null/empty , please verify.");
			result = 1;
			return result;
		}

		if (FwUtil.storeData(key, data)) {
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status: Passed    " + "Message: The data '" + data + 
					"' is stored succesfully in the key '" + key + "'");
			result=0;
		} else {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: Failed to store the data '" + data + 
					"' in the key '" + key + "'");
			result=1;
		}
		return result;

	}

	/**
	 * @param strSecsTime
	 * @return
	 */
	public int Sleep(String strSecsTime) {
		int result=1;
		String strMethod = Controller.strKeywordName;

		long strMilliSecs = SetBaseState.maximumFindObjectTime;

		if ((strSecsTime == null) || (strSecsTime.equalsIgnoreCase("")))
		{
			REPORT.log(LogStatus.INFO,"Message: The waitTime passed is " + 
					strSecsTime + ". Hence WaitTime parameter is considered " + 
					"as the default max sync time as per config setting :" + 
					strMilliSecs / 1000L + " seconds");
		} else {
			if (!CommonUtil.isNumericValue(strSecsTime)) {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
						"Status: Failed    " + "Message: The Seconds '" + 
						strSecsTime + 
						"' passed is not a valid number , please verify.");
				result = 1;
				return result;
			}
			strMilliSecs = Long.parseLong(strSecsTime.trim()) * 1000L;
		}
		try
		{
			//Thread.sleep(strMilliSecs);
			Thread.sleep(5000L);
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status: Passed    " + 
					"Message: Successfully pause for '" + 
					strMilliSecs / 1000L + "' seconds");
			result = 0;
		} catch (Exception e) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + "Message: Failed to pause for '" + 
					strMilliSecs / 1000L + 
					"' seconds.An exception occurred :" + e);
			result =1 ;
		}  
		return result;
	}

	public int SelectPage(String strWindowName) {
		int result=1;
		String strMethod = Controller.strKeywordName;

		if (strWindowName == null)
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: The windowName/handle passed is invalid , please verify.");
			result = 1;
			return result;
		}
		try {
			if (strWindowName.trim().equalsIgnoreCase("NULL"))
			{
				SetBaseState.driver.switchTo().defaultContent();
				REPORT.log(LogStatus.PASS,"Action: " + strMethod + 
						"    " + "Status: Passed    " + 
						"Message: The Main page is selected.");
			} else {
				SetBaseState.driver.switchTo().window(strWindowName);
				REPORT.log(LogStatus.PASS,"Action: " + strMethod + 
						"    " + "Status: Passed    " + 
						"Message: The page with name/handle :'" + 
						strWindowName + "'is selected.");
			}
			result = 0;
		}
		catch (NoSuchWindowException nswe) {
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: Failed to select the page with name/handle :'" + 
					strWindowName + 
					"' . Either the window does not exist or an error occurred, please verify.");
			result = 1;
		}
		return result;
	}

	public int PressKeys(String strKeys) {
		int result=1;
		String strMethod = Controller.strKeywordName;

		if ((strKeys == null) || (strKeys.equalsIgnoreCase("")))
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status : Failed \t\t" + 
					"Message : The Key send is either null/empty , please verify.");
			return result;
		}

		try
		{
			String strCommand = "\"Common\\ScriptingFunction.vbs\"  \"" + 
					strKeys.toLowerCase() + "\"";

			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("CSCRIPT " + strCommand);
			process.waitFor();
			int exitValue = process.exitValue();
			if (exitValue == 0) {
				result = 0;
				REPORT.log(LogStatus.PASS,"Action: " + strMethod + 
						"    " + "Status : Passed \t\t" + 
						"Message : The Key is pressed successfully.");
			}else {

				REPORT.log(LogStatus.FAIL,"Action: " + 
						strMethod + 
						"    " + 
						"Status : Failed \t\t" + 
						"Message : An error occured in the action PressKeys. The key send is '" + 
						strKeys + "', Please verify.");
			}
		}
		catch (Exception localException)
		{
		}
		if (result != 0)
		{
			WebObject.REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION, "Action: " + 
					strMethod + 
					"    " + 
					"Status : Failed \t\t" + 
					"Message : An error occured in the action PressKeys. The key send is '" + 
					strKeys + "', Please verify.");
		}
		return result;

	}

	public int StoreSubString(String strKey, String strMainString, String startIdx, String uptoLen)
	{
		String stSubString = "";
		String strMethod = Controller.strKeywordName;

		if ((strKey == null) || (strKey.trim().equalsIgnoreCase(""))) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: The Key passed is not valid. Please Verify.");
			return 1;
		}

		if (startIdx.equalsIgnoreCase("")) {
			startIdx = "1";

			REPORT.log(LogStatus.INFO,"Message: The start index data is empty. Hence its set to the default value '1'");
		} else {
			if (!NumberUtil.isPostiveInteger(startIdx)) {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
						"Status: Failed    " + "Message: The start index '" + 
						startIdx + "' is not a valid integer, please verify.");
				return 1;
			}
			startIdx = startIdx.trim();
		}

		if (uptoLen.equalsIgnoreCase("")) {
			uptoLen = String.valueOf(strMainString.length());

			REPORT.log(LogStatus.INFO,"Message: The upto length index passed is empty. Hence its set to the default value 'Main string length'= " + 
					uptoLen);
		} else {
			if (!NumberUtil.isPostiveInteger(uptoLen)) {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
						"Status: Failed    " + "Message: The length upto '" + 
						uptoLen + "' is not a valid integer, please verify.");
				return 1;
			}
			uptoLen = uptoLen.trim();
		}

		int intLen = Integer.parseInt(uptoLen);
		int intStart = Integer.parseInt(startIdx);
		int intLenMainStr = strMainString.length();
		if (intLenMainStr > 0) {
			if (intLenMainStr >= intLen) {
				if ((intStart > 0) && (intStart <= intLenMainStr)) {
					if (intLen >= intStart) {
						try {
							stSubString = strMainString.substring(intStart, 
									intLen);
							if (!FwUtil.storeData(strKey, stSubString)); //break label621;
							REPORT.log(LogStatus.PASS,"Action: " + 
									strMethod + 
									"    " + 
									"Status: Passed    " + 
									"Message: The data '" + 
									stSubString + 
									"' is stored succesfully in the key '" + 
									strKey + "'");
							return 0;
						}
						catch (Exception e)
						{
							REPORT.log(LogStatus.ERROR,"Exception occured in PressKeys : " + 
									e);
							return 1;
						}
					}
					else {
						REPORT.log(LogStatus.FAIL,"Action: " + 
								strMethod + 
								"    " + 
								"Status: Failed    " + 
								"Message: The start  position provided '" + 
								intStart + 
								"' is greater than the length of the substring '" + 
								intLen + "' . Please Verify.");
						return 1;
					}
				} else {
					REPORT.log(LogStatus.FAIL,"Action: " + strMethod + 
							"    " + "Status: Failed    " + 
							"Message:The start  position provided '" + 
							intStart + 
							"' is greater than the length of the substring '" + 
							intLen + "' . Please Verify.");
					return 1;
				}
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + 
						"    " + "Status: Failed    " + 
						"Message:The length provided '" + intLen + 
						"' is greater than the length of the String '" + 
						strMainString + "' . Please Verify.");
				return 1;
			}
		} else {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message:The String is Empty. Please Verify");
			return 1;
		}
	}

	public int StoreStringLength(String key, String strMainstr, String intValue)
	{
		int result = 1;
		String strMethod = Controller.strKeywordName;

		if ((key == null) || (key.trim().equalsIgnoreCase("")))
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: The key passed is either null/empty, please verify.");
			result = 1;
			return result;
		}

		if (intValue.equalsIgnoreCase("")) {
			intValue = "0";

			REPORT.log(LogStatus.WARNING,"Message: The increment value is empty. Hence its set to the default value '0'");
		} else {
			if (!NumberUtil.isInteger(intValue)) {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
						"Status: Failed    " + "Message: The value '" + intValue + 
						"' is not a valid integer, please verify.");
				result= 1;
			}
			intValue = intValue.trim();
		}

		int intLength = strMainstr.length() + Integer.parseInt(intValue);

		if (FwUtil.storeData(key, Integer.toString(intLength))) {
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status: Passed    " + "Message: The data '" + intLength + 
					"' is stored succesfully in the key '" + key + "'");
			result= 0;
		} else {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: Failed to store the data '" + intLength + 
					"' in the key '" + key + "'");
			result = 1;
		}
		return result;
	}

	public int ComparePattern(String strPattern, String strData, String blnCaseSensitive)
	{
		int result = 1;
		String strMethod = Controller.strKeywordName;

		if ((strPattern == null) || (strPattern.equalsIgnoreCase("")) || 
				(strData == null))
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status : Failed \t\t" + 
					"Message : Either the pattern/data sent are either Empty, Null or not a string. Please verify.");
			return result;
		}

		if (blnCaseSensitive.equalsIgnoreCase("")) {
			blnCaseSensitive = "True";

			REPORT.log(LogStatus.WARNING,"Message: The CaseSensitive data passed is empty. Hence its set to the default value 'True'");
		}
		else if (!CommonUtil.isBoolean(blnCaseSensitive)) {
			blnCaseSensitive = "True";

			REPORT.log(LogStatus.WARNING,"Message: The CaseSensitive data passed is invalid. Hence its set to the default value 'True'");
		} else {
			blnCaseSensitive = blnCaseSensitive.trim();
		}
		try
		{
			boolean caseSensitiveFlag = Boolean.parseBoolean(blnCaseSensitive);

			Pattern pattern = caseSensitiveFlag ? Pattern.compile(strPattern) : 
				Pattern.compile(strPattern, 2);

			Matcher matcher = pattern.matcher(strData);
			boolean match = matcher.matches();
			if (match) {
				result = 0;
				REPORT.log(LogStatus.PASS,"Action: " + strMethod + 
						"    " + "Status : Passed \t\t" + 
						"Message : The data '" + strData + 
						"' matches the Pattern  '" + strPattern + 
						"' With CaseSensitive comparison as : " + 
						blnCaseSensitive);
			} else {
				result = 1;
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + 
						"    " + "Status : Failed \t\t" + 
						"Message : The data '" + strData + 
						"' does not match the Pattern '" + strPattern + 
						"', With CaseSensitive comparison as : " + 
						blnCaseSensitive + ", Please Verify.");
			}
		} catch (IllegalArgumentException patSysExc) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status : Failed \t\t" + 
					"Message : A syntax error occurred in the pattern '" + 
					strPattern + "', Please Verify.");
		}
		return result;

	}

	public int VerifyStringContainsValue(String strMainString, String strSubString, String blnCaseSensitive)
	{
		int result=1;
		String strMethod = Controller.strKeywordName;

		if ((strMainString == null) || (strSubString == null))
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status : Failed \t\t" + 
					"Message : One / Both of the strings send are either Empty, Null or is not a string.Please verify.");
			return result;
		}

		if (blnCaseSensitive.equalsIgnoreCase("")) {
			blnCaseSensitive = "True";

			REPORT.log(LogStatus.INFO,"Message: The CaseSensitive data passed is empty. Hence its set to the default value 'True'");
		}
		else if (!CommonUtil.isBoolean(blnCaseSensitive)) {
			blnCaseSensitive = "True";

			REPORT.log(LogStatus.INFO,"Message: The CaseSensitive data passed is '" + 
					blnCaseSensitive + 
					"'. Hence its set to the default value 'True'");
		}
		else {
			blnCaseSensitive = blnCaseSensitive.trim();
		}

		strMainString = strMainString.replace("\n", " ");
		strSubString = strSubString.replace("\n", " ");
		boolean compareResult;

		if (Boolean.parseBoolean(blnCaseSensitive))
			compareResult = strMainString.contains(strSubString);
		else {
			compareResult = strMainString.toUpperCase().contains(
					strSubString.toUpperCase());
		}
		if (compareResult) {
			result = 0;
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status : Passed \t\t" + "Message : The String '" + 
					strMainString + "' contains  the substring '" + 
					strSubString + "' With CaseSensitive comparison as : " + 
					blnCaseSensitive);
		} else {
			result = 1;
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status : Failed" + "Message : The String '" + 
					strMainString + "' does not contains  the substring '" + 
					strSubString + "' With CaseSensitive comparison as : " + 
					blnCaseSensitive + ", Please Verify.");
		}
		return result;
	}

	public int CompareString(String firstString, String secondString, String blnCaseSensitive)
	{
		int result=1;
		String strMethod = Controller.strKeywordName;

		if ((firstString == null) || (secondString == null))
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status : Failed \t\t" + 
					"Message : One / Both of the strings send are either Empty, Null or is not a string.Please verify.");
			return result;
		}

		if (blnCaseSensitive.equalsIgnoreCase("")) {
			blnCaseSensitive = "True";

			REPORT.log(LogStatus.INFO,"Message: The CaseSensitive data passed is empty. Hence its set to the default value 'True'");
		}
		else if (!CommonUtil.isBoolean(blnCaseSensitive)) {
			blnCaseSensitive = "True";

			REPORT.log(LogStatus.INFO,"Message: The CaseSensitive data passed is '" + 
					blnCaseSensitive + 
					"'. Hence its set to the default value 'True'");
		} else {
			blnCaseSensitive = blnCaseSensitive.trim();
		}

		firstString = firstString.replace("\n", " ");
		secondString = secondString.replace("\n", " ");
		boolean compareResult;

		if (Boolean.parseBoolean(blnCaseSensitive))
			compareResult = firstString.equals(secondString);
		else {
			compareResult = firstString.equalsIgnoreCase(secondString);
		}
		if (compareResult) {

			result = 0;
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status : Passed \t\t" + "Message : The String '" + 
					firstString + "' and '" + secondString + 
					"' are equal." + 
					" With CaseSensitive comparison as : " + 
					blnCaseSensitive);
		} else {
			result=1;
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status : Failed \t\t" + "Message : The String '" + 
					firstString + "' and '" + secondString + 
					"' are not equal." + 
					" With CaseSensitive comparison as : " + blnCaseSensitive + 
					", Please Verify.");
		}
		return result;
	}
	
	public int CompareStoredString(String FirstString, String SecondString, String blnCaseSensitive)
	{
		int result=1;
		String firstString = Controller.STOREHASHMAP.get(FirstString);
		String secondString = Controller.STOREHASHMAP.get(SecondString);
		String strMethod = Controller.strKeywordName;

		if ((firstString == null) || (secondString == null))
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status : Failed \t\t" + 
					"Message : One / Both of the strings send are either Empty, Null or is not a string.Please verify.");
			return result;
		}

		if (blnCaseSensitive.equalsIgnoreCase("")) {
			blnCaseSensitive = "True";

			REPORT.log(LogStatus.INFO,"Message: The CaseSensitive data passed is empty. Hence its set to the default value 'True'");
		}
		else if (!CommonUtil.isBoolean(blnCaseSensitive)) {
			blnCaseSensitive = "True";

			REPORT.log(LogStatus.INFO,"Message: The CaseSensitive data passed is '" + 
					blnCaseSensitive + 
					"'. Hence its set to the default value 'True'");
		} else {
			blnCaseSensitive = blnCaseSensitive.trim();
		}

		firstString = firstString.replace("\n", " ");
		secondString = secondString.replace("\n", " ");
		boolean compareResult;

		if (Boolean.parseBoolean(blnCaseSensitive))
			compareResult = firstString.equals(secondString);
		else {
			compareResult = firstString.equalsIgnoreCase(secondString);
		}
		if (compareResult) {

			result = 0;
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status : Passed \t\t" + "Message : The String '" + 
					firstString + "' and '" + secondString + 
					"' are equal." + 
					" With CaseSensitive comparison as : " + 
					blnCaseSensitive);
		} else {
			result=1;
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status : Failed \t\t" + "Message : The String '" + 
					firstString + "' and '" + secondString + 
					"' are not equal." + 
					" With CaseSensitive comparison as : " + blnCaseSensitive + 
					", Please Verify.");
		}
		return result;
	}

	public int VerifyDifference(String int1, String int2, String differenceInt) {
		int result =1;
		String strMethod = Controller.strKeywordName;

		if (int1.equalsIgnoreCase("")) {
			int1 = "0";

			REPORT.log(LogStatus.INFO,"Message: The first integer data is empty. Hence its set to the default value '0'"); } else {
				if (!NumberUtil.isInteger(int1)) {
					REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
							"Status: Failed    " + "Message: The integer '" + int1 + 
							"' is not a valid integer, please verify.");
					result = 1;
					return result;
				}
				int1 = int1.trim();
			}

		if (int2.equalsIgnoreCase("")) {
			int2 = "0";

			REPORT.log(LogStatus.INFO,"Message: The second integer data is empty. Hence its set to the default value '0'"); } else {
				if (!NumberUtil.isInteger(int2)) {
					REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
							"Status: Failed    " + "Message: The integer '" + int2 + 
							"' is not a valid integer, please verify.");
					result = 1;
					return result;
				}
				int2 = int2.trim();
			}

		if (differenceInt.equalsIgnoreCase("")) {
			differenceInt = "0";

			REPORT.log(LogStatus.INFO,"Message: The difference data is empty. Hence its set to the default value '0'"); } else {
				if (!NumberUtil.isInteger(differenceInt)) {
					REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
							"Status: Failed    " + "Message: The difference '" + 
							differenceInt + 
							"' is not a valid integer, please verify.");
					result = 1;
					return result;
				}
				differenceInt = differenceInt.trim();
			}

		int diff = Integer.parseInt(int1) - Integer.parseInt(int2);
		if (diff == Integer.parseInt(differenceInt))
		{
			REPORT.log(LogStatus.PASS,"Action: " + 
					strMethod + 
					"    " + 
					"Status : Passed \t\t" + 
					"Message : The diffrence is equal to the intended difference " + 
					differenceInt);
			result=0;
		} else {
			diff = Integer.parseInt(int2) - Integer.parseInt(int1);
			if (diff == Integer.parseInt(differenceInt))
			{
				REPORT.log(LogStatus.PASS,"Action: " + 
						strMethod + 
						"    " + 
						"Status : Passed \t\t" + 
						"Message : The diffrence is equal to the intended difference " + 
						differenceInt);
				result = 0;
			}
			else {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						strMethod + 
						"    " + 
						"Status : Failed \t\t" + 
						"Message : The diffrence is not equal to the intended difference " + 
						differenceInt);
				result = 1;
			}
		}
		return result;
	}

	public int StoreSplitString(String strKey, String mainStr, String delimiter, String instance)
	{
		int result=1;
		String strMethod = Controller.strKeywordName;
		if ((strKey == null) || (strKey.trim().equalsIgnoreCase(""))) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: The Key passed is not valid. Please Verify.");

		}

		if ((mainStr == null) || (delimiter == null))
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					strMethod + 
					"    " + 
					"Status: Failed    " + 
					"Message: Either the main string to be split OR the delimiter is not valid. Please Verify.");
			result = 1;
			return result;
		}

		if (instance.equalsIgnoreCase("")) {
			instance = "1";

			REPORT.log(LogStatus.INFO,"Message: The Instance data is empty. Hence its set to the default value '1'");
		} else {
			if (!NumberUtil.isPostiveInteger(instance))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						strMethod + 
						"    " + 
						"Status: Failed    " + 
						"Message: The instance '" + 
						instance + 
						"' is not a valid integer, please verify.Instance to be store starts at 1.");
				result = 1;
				return result;
			}
			instance = instance.trim();
		}
		int inst = Integer.parseInt(instance);
		String[] splitData;
		try { splitData = mainStr.split(delimiter);
		}
		catch (PatternSyntaxException pe)
		{
			splitData = mainStr.split("\\" + delimiter);
		}
		if (inst > splitData.length) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: After spliting the string '" + mainStr + "' " + 
					splitData.length + " instance is created. " + inst + 
					" instance does not exist.");
			result = 1;
			return result;
		}
		String data = splitData[(inst - 1)];
		if (FwUtil.storeData(strKey, data)) {
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + 
					"Status: Passed    " + "Message: The data '" + data + 
					"' is stored successfully in the key '" + strKey + "'.");
			result=0;
		} else {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
					"Status: Failed    " + 
					"Message: Failed to store the data '" + data + 
					"' in the key " + strKey + "'.");
			result=1;
		}
		return result;
	}

	public int StoreCurrentDateInFormat(String format, String strDateKey)
	{
		int result = 1;
		try {
			if ((strDateKey == null) || (strDateKey.trim().equalsIgnoreCase("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The key passed is either null/empty , please verify.");
				return result;
			}

			if ((format == null) || (format.trim().equalsIgnoreCase("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The format passed is either null/empty , please verify.");
				return result;
			}

			String strCurrDate = CommonUtil.getCurrentDateTime(format);
			if ((strCurrDate.trim().isEmpty()) || (strCurrDate == null))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: Failed to get the current date in the format " + 
						format);
				result=1;
			}
			if (FwUtil.storeData(strDateKey, strCurrDate)) {
				REPORT.log(LogStatus.PASS,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Passed    " + "Message: The data '" + 
						strCurrDate + "' is stored succesfully in the key '" + 
						strDateKey + "'");
				result=0;
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: Failed to store the data '" + strCurrDate + 
						"' in the key '" + strDateKey + "'");
				result=1;
			}
		} catch (NullPointerException e) {

			result=1;
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + 
					"    " + 
					"Status: Failed    " + 
					"Message: Exception while getting the current date in the specified format " + 
					format + 
					"as the format maybe invalid.Please verify.Exception:" + 
					e.getMessage());

			REPORT.log(LogStatus.ERROR,"Exception while getting the current date in the specified format " + 
					format + 
					" . StackTrace: <br/>" + 
					FwUtil.getStackTrace(e));
		} catch (Exception e) {

			result=1;
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + 
					"    " + 
					"Status: Failed    " + 
					"Message: Exception while getting the current date in the specified format " + 
					format + "." + e.getMessage());

			REPORT.log(LogStatus.ERROR,"Exception while getting the current date in the specified format " + 
					format + 
					" . StackTrace: <br/>" + 
					FwUtil.getStackTrace(e));
		}
		return result;
	}

	public int StoreDateInFormat(String strDate, String sourceFormat, String resultFormat, String strDateKey)
	{
		int result=1;
		try {
			if ((strDateKey == null) || (strDateKey.trim().equalsIgnoreCase("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The key passed is either null/empty , please verify.");
				return result;
			}

			if ((strDate == null) || (strDate.trim().equalsIgnoreCase("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The source date passed is either null/empty , please verify.");
				return result;
			}

			if ((sourceFormat == null) || 
					(sourceFormat.trim().equalsIgnoreCase("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The source date format passed is either null/empty , please verify.");
				return result;
			}

			if ((resultFormat == null) || 
					(resultFormat.trim().equalsIgnoreCase("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The result date format passed is either null/empty , please verify.");
				return result;
			}

			String formattedDate = DateUtil.getDateInFormat(strDate, 
					sourceFormat, resultFormat);
			if ((formattedDate.isEmpty()) || (formattedDate.trim().equals(""))) {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: Failed to get the date " + strDate + 
						" in the format " + resultFormat + 
						".Check the source or the result format");
				return result;
			}
			if (FwUtil.storeData(strDateKey, formattedDate)) {
				REPORT.log(LogStatus.PASS,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Passed    " + "Message: The date '" + 
						formattedDate + 
						"' is stored succesfully in the key '" + strDateKey + 
						"'");
				result = 0;
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: Failed to store the date '" + formattedDate + 
						"' in the key '" + strDateKey + "'");
				result=1;
			}
		} catch (Exception e) {

			result=1;
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + 
					"    " + 
					"Status: Failed    " + 
					"Message: Exception while getting the date in the specified format.Exception: " + 
					e.getMessage());

			REPORT.log(LogStatus.ERROR,"Exception while getting the date in the specified format . StackTrace: <br/>" + 
					FwUtil.getStackTrace(e));
		}
		return result;

	}

	public int VerifyDates(String firstDate, String firstFormat, String secDate, String secondFormat, String operation)
	{
		int result=1;
		String message = "";
		try {
			if ((firstDate.trim().equals("")) || (secDate.trim().equals("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The first/second date are not valid.Plase verify.");
				return result;
			}

			if ((firstFormat.trim().equals("")) || (secondFormat.trim().equals("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The first/second date formats are not valid.Plase verify.");
				return result;
			}

			Date fstDate = CommonUtil.getDate(firstFormat, firstDate);
			Date secondDate = CommonUtil.getDate(secondFormat, secDate);

			if ((fstDate == null) || (secondDate == null))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: Failed to get the first/second date in the format specifed .Please Check the formats");
				return result;
			}

			if (operation.equalsIgnoreCase(""))
				operation = "==";
			else {
				operation = operation.trim();
			}

			if (DateUtil.compareDates(fstDate, secondDate, operation)) {
				if (operation.equals("=="))
					message = "Message: [Operation] : The dates are compared for equality .[Result] : The dates " + 
							firstDate + " and " + secDate + " are equal";
				else if (operation.equals(">"))
					message = "Message: [Operation] : The first is verified for greater than second date. [Result] : The  " + 
							firstDate + " is greater than the " + secDate;
				else if (operation.equals("<"))
					message = "Message: [Operation] : The first is verified for less than second date. [Result] :  The  " + 
							firstDate + " is less than the " + secDate;
				else {
					message = "Message: [Operation] : The dates are compared for inequality .[Result] : The dates " + 
							firstDate + " and " + secDate + " are not equal";
				}

				REPORT.log(LogStatus.PASS,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Passed    " + message);
				result = 0;
			} else {
				if (operation.equals("=="))
					message = "Message: [Operation] : The dates are compared for equality .[Result] : The dates " + 
							firstDate + "and " + secDate + " are not equal";
				else if (operation.equals(">"))
					message = "Message: [Operation] : The first is verified for greater than second date. [Result] : The  " + 
							firstDate + " is not greater than the " + secDate;
				else if (operation.equals("<"))
					message = "Message: [Operation] : The first date is verified for less then second date. [Result] : The  " + 
							firstDate + " is not less than the " + secDate;
				else {
					message = "Message: [Operation] : The dates are compared for inequality. [Result] : The dates " + 
							firstDate + " and " + secDate + " are equal";
				}

				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Defect    " + message);
				result = 1;
			}
		} catch (Exception e) {
			result = 1;

			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: Exception while verifying the dates.Exception:" + 
					e.getMessage());

			REPORT.log(LogStatus.ERROR,"Exception while verifying the dates . StackTrace: <br/>" + 
					FwUtil.getStackTrace(e));
		}
		return result;

	}

	public int ChangeDateAndStore(String date, String dateFormat, String dayValue, String monthValue, String yearValue, String strDateKey)
	{
		int status = 1;
		String modifiedDate = "";

		try {
			if ((strDateKey == null) || (strDateKey.trim().equals("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The key passed is invalid , please verify.");
				return status;
			}

			if ((!NumberUtil.isInteger(dayValue)) || 
					(!NumberUtil.isInteger(monthValue)) || 
					(!NumberUtil.isInteger(yearValue))) {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: Either the integer '" + dayValue + 
						"' OR integer '" + monthValue + "' OR '" + yearValue + 
						"' is not a valid integer, please verify.");

				return status;
			}

			modifiedDate = DateUtil.changeDate(
					CommonUtil.getDate(dateFormat, date), dateFormat, 
					Integer.parseInt(dayValue), Integer.parseInt(monthValue), 
					Integer.parseInt(yearValue));

			if ((modifiedDate.isEmpty()) || (modifiedDate == "")) {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: Failed to change the date " + date + 
						".Please verify the date/format " + dateFormat);
				return status;
			}
			if (FwUtil.storeData(strDateKey, modifiedDate)) {
				REPORT.log(LogStatus.PASS,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Passed    " + "Message: The data '" + 
						modifiedDate + 
						"' is stored succesfully in the key '" + 
						strDateKey + "'");
				status = 0;
			}
			else {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: Failed to store the data '" + 
						modifiedDate + "' in the key " + strDateKey + 
						"'.");
				status = 1;
			}
		}
		catch (Exception e) {
			status = 1;
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: Exception while changing the date " + date + 
					".Please verify the date/format " + dateFormat + 
					".Exception: " + e.getMessage());

			REPORT.log(LogStatus.ERROR,"Exception while changing the date " + 
					date + ".Please verify the date/format " + dateFormat + 
					".StackTrace: <br/>" + FwUtil.getStackTrace(e));
		}
		return status;

	}

	public int CalculateDifferenceInDatesAndStore(String startDate, String startFormat, String endDate, String endFormat, String strDiffKey)
	{
		int status = 1;
		try {
			if ((strDiffKey == null) || (strDiffKey.trim().equals("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The key passed is invalid , please verify.");
				return status;
			}

			if ((startDate == null) || (startDate.trim().equals("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The startDate passed is invalid , please verify.");
				return status;
			}

			if ((startFormat == null) || (startFormat.trim().equals("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The startFormat passed is invalid , please verify.");
				return status;
			}

			if ((endDate == null) || (endDate.trim().equals("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The endDate passed is invalid , please verify.");
				return status;
			}

			if ((endFormat == null) || (endFormat.trim().equals("")))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The endFormat passed is invalid , please verify.");
				return status;
			}

			String difference = DateUtil.CalculateDifferenceInDates(
					CommonUtil.getDate(startFormat, startDate), 
					CommonUtil.getDate(endFormat, endDate));
			if ((difference.isEmpty()) || (difference == null))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: Failed to calculate the difference between the dates " + 
						startDate + " and " + endDate);
				return status;
			}

			if (FwUtil.storeData(strDiffKey, difference)) {
				REPORT.log(LogStatus.PASS,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Passed    " + "Message: The data '" + 
						difference + "' is stored succesfully in the key '" + 
						strDiffKey + "'");
				status = 0;
			}
			else {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: Failed to store the data '" + difference + 
						"' in the key " + strDiffKey + "'.");
				status = 1;
			}
		} catch (Exception e) {
			status = 1;

			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + 
					"    " + 
					"Status: Failed    " + 
					"Message: Exception while calculating difference in the dates " + 
					startDate + " and " + endDate + ".Exception: " + 
					e.getMessage());

			REPORT.log(LogStatus.ERROR,"Exception while calculating difference in the dates " + 
					startDate + 
					" and " + 
					endDate + 
					". StackTrace: <br/>" + FwUtil.getStackTrace(e));
		}
		return status;
	}

	public int VerifyDialogExistence(String existence) {

		int result = 1;
		boolean exist = true;
		boolean isAlertPresent = false;
		if ((!CommonUtil.isBoolean(existence)) || (existence.equals("")) || 
				(existence.trim().isEmpty()))
		{
			REPORT.log(LogStatus.INFO,"Since a valid value for existence is not given, assuming the default value as 'TRUE'");
			exist = true;
		} else {
			exist = Boolean.parseBoolean(existence.trim());
		}
		try {
			SetBaseState.driver.switchTo().alert();
			isAlertPresent = true;
		}
		catch (NoAlertPresentException e) {
			isAlertPresent = false;
		}

		if (exist) {
			if (isAlertPresent) {
				REPORT.log(LogStatus.PASS,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Passed    " + "Message: A dialog exist.");
				result = 0;
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: A dialog exist.Please verify.");
				result = 1;
			}
		}
		else if (isAlertPresent) {
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + 
					"Status: Failed    " + 
					"Message: A dialog exist exist.Please verify.");
			result = 1;
		} else {
			REPORT.log(LogStatus.PASS,"Action: " + 
					Controller.strKeywordName + "    " + 
					"Status: Passed    " + 
					"Message: A dialog exist does not exist.");
			result = 0;
		}

		return result;
	}

	public int AcceptDialog() {

		try
		{
			Alert alert = SetBaseState.driver.switchTo().alert();
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
					"Message: An alert does not exist, please verify.");
			return 1;
		}

	}

	public int DismissDialog() {
		try
		{
			Alert alert= SetBaseState.driver.switchTo().alert();
			alert.dismiss();

			REPORT.log(LogStatus.PASS,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Passed    " + 
					"Message: Dialog is dismissed.");
			return 0;
		}
		catch (NoAlertPresentException e)
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: An alert does not exist, please verify.");
			return 1;
		}
	}

	public int AnswerForNextDialog(String answer) {
		int result =1;
		if ((answer == null) || (answer.equals("")))
		{
			REPORT.log(LogStatus.INFO,"The 'answer' parameter is empty, assuming the default value as zero length empty string");
			answer = "";
		}
		Alert alert;
		try
		{

			alert = SetBaseState.driver.switchTo().alert();
			alert.sendKeys(answer);
		}
		catch (NoAlertPresentException e)
		{

			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: An alert does not exist, please verify.");
			return 1;
		}
		catch (ElementNotVisibleException e) {
			if (e.getMessage()
					.contains(
							"Modal dialog did not have a text box - maybe it was an alert"))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: Modal dialog did not have a text box - maybe it was an alert, please verify.");
				return 1;

			}

		}

		REPORT.log(LogStatus.PASS,"Action: " + Controller.strKeywordName + 
				"    " + "Status: Passed    " + 
				"Message: Answer for next dialog is set as '" + 
				answer + "'.");
		return 0;

	}

	public int StoreDialogText(String strKey) {
		int result = 1;
		if ((strKey == null) || (strKey.trim().equals(""))) {
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: The key passed is invalid , please verify.");
			return result;
		}
		try
		{
			Alert  alert = SetBaseState.driver.switchTo().alert();
			String dialogText = alert.getText();
			if (FwUtil.storeData(strKey, dialogText)) {
				REPORT.log(LogStatus.PASS,"Action: " + 
						Controller.strKeywordName + "    " + "Status: Passed    " + 
						"Message: The data '" + dialogText + 
						"' is stored succesfully in the key '" + strKey + "'");
				return 0;
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + "Status: Failed    " + 
						"Message: Failed to store the data '" + dialogText + 
						"' in the key '" + strKey + "'");
				return 1;
			}
		}
		catch (NoAlertPresentException e)
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: An alert does not exist, please verify.");
			return 1;
		}
	}

	public int ExecuteFile(String batFilePath)
	{
		int result = 1;
		try {
			Runtime run = Runtime.getRuntime();

			if ((batFilePath.isEmpty()) || (batFilePath.trim() == "") || 
					(batFilePath == null))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The file is empty.Please enter a valid batch file path.");
				return 1;
			}

			File file = new File(batFilePath);
			if (!file.exists())
			{
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + 
						"    " + 
						"Status: Failed    " + 
						"Message: The system cannot find the file specified. Please verify the file " + 
						batFilePath + " exists.");
				return 1;
			}

			run.exec("cmd /c start /min " + batFilePath);

			REPORT.log(LogStatus.PASS,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Passed    " + 
					"Message: The file " + batFilePath + 
					" is executed successfully.");
			result = 0;
		}
		catch (IllegalThreadStateException e) {
			if (e.getMessage().contains("process has not exited")) {
				REPORT.log(LogStatus.PASS,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Passed    " + "Message: The file " + 
						batFilePath + " is executed successfully.");
				result = 0;
			}
		} catch (Exception e) {

			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: Exception while executing the file " + 
					batFilePath + ".Exception: " + e.getMessage());
			REPORT.log(LogStatus.ERROR,"Exception while executing the file " + 
					batFilePath + ". StackTrace: <br/>" + 
					FwUtil.getStackTrace(e));
			result = 1;
		}
		return result;

	}

	public int ComputeExpression(String expression, String strKey) {
		int result=1;
		if ((strKey == null) || (strKey.trim().equalsIgnoreCase(""))) {
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: The Key passed is not valid. Please Verify.");
			result = 1;
			return result;
		}
		try
		{
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("JavaScript");

			String exprResult = engine.eval(expression).toString();
			if (FwUtil.storeData(strKey, exprResult)) {
				REPORT.log(LogStatus.PASS,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Passed\t\t" + 
						"Message: Successfully evaluated the expression '" + 
						expression + "' and stored the result '" + exprResult + 
						"' in the key '" + strKey + "'.");
				result = 0;
			}
			else {
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: Failed to store the result '" + exprResult + 
						"' in the key '" + strKey + "'.");
				result = 1;
			}
		}
		catch (Exception e) {
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: Exception while evaluating the expression '" + 
					expression + "'.Exception: " + e.getMessage());
			REPORT.log(LogStatus.ERROR,"Action: " + Controller.strKeywordName + 
					"    " + "Status: Failed    " + 
					"Message: Exception while evaluating the expression '" + 
					expression + "'. StackTrace: <br/>" + 
					FwUtil.getStackTrace(e));

			result = 1;
		}
		return result;

	}

	public int StoreWindowHandle(String strKey)
	{
		if ((strKey == null) || (strKey.trim().equals(""))) {
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: The key passed is invalid , please verify.");
			return 1;
		}
		SetBaseState.driver.getWindowHandles();
		for (String handle : SetBaseState.driver.getWindowHandles()) {
			if (!Controller.STOREHASHMAP.containsValue(handle)) {
				if (FwUtil.storeData(strKey, handle)) {
					REPORT.log(LogStatus.PASS,"Action: " + 
							Controller.strKeywordName + "    " + 
							"Status: Passed\t\t" + "Message: The handle '" + 
							handle + "' is successfully stored in the key '" + 
							strKey + "'.");
					return 0;

				}
				REPORT.log(LogStatus.FAIL,"Action: " + 
						Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: Failed to store the handle '" + handle + 
						"' in the key '" + strKey + "'");
				return 1;
			}

		}

		REPORT.log(LogStatus.FAIL,"Action: " + 
				Controller.strKeywordName + 
				"    " + 
				"Status: Failed\t\t" + 
				"Message: No new windows were identified.Window Handle not stored int the key" + 
				strKey);
		return 1;
	}

	public int DeleteCookiesOnCurrentDomain()
	{
		try
		{
			SetBaseState.driver.manage().deleteAllCookies();
		}
		catch (Exception e) {
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: Exception while deleting the cookies. " + 
					" Exception: " + e.getMessage());
			REPORT.log(LogStatus.ERROR,"Action: " + Controller.strKeywordName + 
					"    " + "Status: Failed    " + 
					"Message: Exception while deleting the cookies. " + 
					" StackTrace: <br/>" + 
					FwUtil.getStackTrace(e));
			return 1;

		}

		REPORT.log(LogStatus.PASS,"Action: " + 
				Controller.strKeywordName + "    " + 
				"Status: Passed    " + 
				"Message: All the cookies deleted successfully.");
		return 0;
	}

	public int RefreshPage()
	{
		try
		{
			SetBaseState.driver.navigate().refresh();
		}
		catch (Exception e) {
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: Exception while refreshing the page. " + 
					" Exception: " + e.getMessage());
			REPORT.log(LogStatus.ERROR,"Action: " + Controller.strKeywordName + 
					"    " + "Status: Failed    " + 
					"Message: Exception while refreshing the page " + 
					" StackTrace: <br/>" + 
					FwUtil.getStackTrace(e));
			return 1;
		}

		REPORT.log(LogStatus.PASS,"Action: " + 
				Controller.strKeywordName + "    " + 
				"Status: Passed    " + 
				"Message: Page is refreshed successfully");
		return 0;
	}

	public int StoreChildCount(String locator, String key)
	{
		int childCount = 0;
		try
		{
			if ((key == null) || (key.trim() == ""))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: The key passed is invalid , please verify.");
				return 1;
			}

			if ((locator == null) || (locator.trim() == ""))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: The locator passed is invalid , please verify.");
				return 1;
			}

			if (!locator.startsWith("//"))
			{
				REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
						"Status: Failed    " + 
						"Message: The locator '" + locator + "' passed is not a valid xpath , please verify.");
				return 1;
			}
			childCount = getElementsList(locator, "xpath").size();
			if (FwUtil.storeData(key, String.valueOf(childCount)))
			{
				REPORT.log(LogStatus.PASS,"Action: " + Controller.strKeywordName + "    " + 
						"Status: Passed    " + 
						"Message: The data '" + String.valueOf(childCount) + "' is stored succesfully in the key '" + key + "'");
				return 0;
			}

			REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
					"Status: Failed    " + 
					"Message: Failed to store the data '" + String.valueOf(childCount) + "' in the key '" + key + "'");
			return 1;
		}
		catch (Exception e)
		{
			REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
					"Status: Failed    " + 
					"Message: Exception while retreiving the child count for the locator " + locator + ".Exception: " + e.getMessage());
			REPORT.log(LogStatus.ERROR,"Exception while retreiving the child count for the locator " + locator + ". StackTrace: <br/>" + FwUtil.getStackTrace(e));
			return 1;
		}
	}

	public int AcceptDialogAndStoreMessage(String strKey)
	{
		if ((strKey == null) || (strKey.trim().equalsIgnoreCase("")))
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + 
					"    " + 
					"Status: Failed    " + 
					"Message: The key passed is either null/empty , please verify.");
			return 1;
		}
		try
		{
			Alert alert = SetBaseState.driver.switchTo().alert();
			String message = alert.getText();
			alert.accept();
			if (FwUtil.storeData(strKey, message)) {
				REPORT.log(LogStatus.PASS,"Action: " + Controller.strKeywordName + "    " + 
						"Status: Passed    " + 
						"Message: The dialog is accepted and the message '" + message + "' is stored succesfully in the key '" + strKey + "'");
				return 0;
			}
			REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
					"Status: Failed    " + 
					"Message: The dialog is accepted but failed to store the message '" + message + "' in the key '" + strKey + "'. Please verify.");
			return 1;
		}
		catch (NoAlertPresentException e)
		{
			REPORT.log(LogStatus.FAIL,"Action: " + 
					Controller.strKeywordName + "    " + "Status: Failed    " + 
					"Message: An alert does not exist, please verify.");
			return 1;
		}


	}

	public int StoreValue(String object, String strKey)
	{
		WebElement query = exist(object, true);

		if ((strKey == null) || (strKey.trim().equalsIgnoreCase(""))) {
			WebObject.REPORT.log(LogStatus.WARNING, "The key passed is either null/empty , please verify.");
			return 1;
		}
		String strData = query.getAttribute("Value");

		if ((strData == null) || (strData.trim().equalsIgnoreCase(""))) {
			WebObject.REPORT.log(LogStatus.WARNING, "The data is either null/empty , please verify.");

			return 1;
		}

		try{
			FwUtil.storeData(strKey, strData);
			WebObject.REPORT.log(LogStatus.PASS, "Value of "+ object + " is "+ strData + " stored successfully ");
			return 0;
		}catch(Exception e){
			WebObject.REPORT.log(LogStatus.ERROR, "Value cannot be stored ,Please verify");
			return 1;
		}


	}

	public int StoreText(String Object, String strKey)
	{
		WebElement element = exist(Object, true);
		if ((strKey == null) || (strKey.trim().equalsIgnoreCase(""))) {
			WebObject.REPORT.log(LogStatus.WARNING, "The key passed is either null/empty , please verify.");
			return 1;
		}
		String strData = element.getText();
		if ((strData == null) || (strData.trim().equalsIgnoreCase(""))) {
			WebObject.REPORT.log(LogStatus.WARNING, "The data is either null/empty , please verify.");
			return 1;
		}
		try{
			FwUtil.storeData(strKey, strData);
			WebObject.REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION, "Value of "+ Object + " is "+ strData + " stored successfully ");
			return 0;
		}catch(Exception e){
			WebObject.REPORT.log(LogStatus.ERROR,"Value cannot be stored ,Please verify");
			return 1;
		}
	}

	public String SelectDatefromCalendar(String Month,String Date,String Year) {

		String result="";

		List<String> list = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
		// Expected Date, Month and Year
		int expMonth;
		int expYear;
		String expDate = null;
		// Calendar Month and Year
		String calMonth = null;
		String calYear = null;
		boolean dateNotFound;

		SetBaseState.driver.findElement(By.className("ui-datepicker-calendar")).click();
		dateNotFound = true;
		expMonth= Integer.parseInt(Month);
		expYear = Integer.parseInt(Year);
		expDate = Date;
		while(dateNotFound)
		{
			calMonth = SetBaseState.driver.findElement(By.className("ui-datepicker-month")).getText();
			calYear = SetBaseState.driver.findElement(By.className("ui-datepicker-year")).getText();
			if(list.indexOf(calMonth)+1 == expMonth && (expYear == Integer.parseInt(calYear)))
			{
				selectDate(expDate);
				dateNotFound = false;
			}
			else if(list.indexOf(calMonth)+1 < expMonth && (expYear == Integer.parseInt(calYear)) || expYear > Integer.parseInt(calYear))
			{
				SetBaseState.driver.findElement(By.xpath(".//*[@id='ui-datepicker-div']/div/a[2]/span")).click(); 
			}
			else if(list.indexOf(calMonth)+1 > expMonth && (expYear == Integer.parseInt(calYear)) || expYear < Integer.parseInt(calYear))
			{
				SetBaseState.driver.findElement(By.xpath(".//*[@id='ui-datepicker-div']/div/a[1]/span")).click(); 
			}
		}

		return result;
	}

	protected void selectDate(String date)
	{
		WebElement dateWidget;

		List<WebElement> columns;
		dateWidget = SetBaseState.driver.findElement(By.id("ui-datepicker-div"));
		dateWidget.findElements(By.tagName("tr"));
		columns=dateWidget.findElements(By.tagName("td"));

		for (WebElement cell: columns){
			if (cell.getText().equals(date)){
				cell.findElement(By.linkText(date)).click();

				break;
			}
		}
	}

	public String SelectDateCalendar(String Month,String Date,String Year) {

		String result="";

		/*	List<String> list = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
		// Expected Date, Month and Year
		int expMonth;
		int expYear;*/
		String expDate = null;
		// Calendar Month and Year
		String calMonth = Month;
		String calYear = Year;
		SetBaseState.driver.findElement(By.className("ui-datepicker-calendar")).click();
		expDate = Date;
		SetBaseState.driver.findElement(By.xpath("//*[@id='ui-datepicker-div']/div/div/select[1]")).sendKeys(calMonth);
		SetBaseState.driver.findElement(By.xpath("//*[@id='ui-datepicker-div']/div/div/select[2]")).sendKeys(calYear);
		selectDate(expDate);
		return result;
	}

	public int ScrollDown()
	{

		try
		{
			/*JavascriptExecutor jse = (JavascriptExecutor)SetBaseState.driver;
			jse.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");*/
			Actions actions = new Actions(SetBaseState.driver);
			actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform();
			WebObject.REPORT.log(LogStatus.INFO,"The Page is scrolled down.");
			return 0;


		}
		catch (NoAlertPresentException e)
		{
			WebObject.REPORT.log(LogStatus.WARNING, "please verify.");
			return 1;

		}
	}

	public int ScrollUP()
	{
		try
		{
			JavascriptExecutor jse = (JavascriptExecutor)SetBaseState.driver;
			jse.executeScript("window.scrollTo(Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight),0);");
			/*Actions actions = new Actions(SetBaseState.driver);
			actions.keyDown(Keys.CONTROL).sendKeys(Keys.UP).perform();*/
			WebObject.REPORT.log(LogStatus.INFO,"The Page is scrolled up.");
			return 0;


		}
		catch (NoAlertPresentException e)
		{
			WebObject.REPORT.log(LogStatus.INFO," please verify.");
			return 1;

		}
	}

	public int VerifyString(String FirstString,String SecondString) {

		int result=1;
		String blnCaseSensitive = "";
		String firstString = Controller.STOREHASHMAP.get(FirstString);
		String secondString =SecondString;
		if ((firstString == null) || (secondString == null))
		{
			WebObject.REPORT.log(LogStatus.WARNING, "One / Both of the strings send are either Empty, Null or is not a string.Please verify.");
			return result;
		}
		if (blnCaseSensitive.equalsIgnoreCase(""))
		{
			blnCaseSensitive = "True";
			WebObject.REPORT.log(LogStatus.WARNING,"The CaseSensitive data passed is empty. Hence its set to the default value 'True'");
		}
		else if (!CommonUtil.isBoolean(blnCaseSensitive))
		{
			blnCaseSensitive = "True";
			WebObject.REPORT.log(LogStatus.WARNING,"The CaseSensitive data passed is '" + 
					blnCaseSensitive + 
					"'. Hence its set to the default value 'True'");
		}
		else
		{
			blnCaseSensitive = blnCaseSensitive.trim();
		}
		firstString = firstString.replace("\n", " ");
		secondString = secondString.replace("\n", " ");
		boolean compareResult;
		if (Boolean.parseBoolean(blnCaseSensitive)) {
			compareResult = firstString.equals(secondString);
		} else {
			compareResult = firstString.equalsIgnoreCase(secondString);
		}
		if (compareResult)
		{
			WebObject.REPORT.log(LogStatus.PASS," The String '" + 
					firstString + "' and '" + secondString + 
					"' are equal." + 
					" With CaseSensitive comparison as : " + 
					blnCaseSensitive);
			return 0;
		}
		else
		{
			WebObject.REPORT.log(LogStatus.WARNING,"The String '" + 
					firstString + "' and '" + secondString + 
					"' are not equal." + 
					" With CaseSensitive comparison as : " + blnCaseSensitive + 
					", Please Verify.");
			return 1;
		}
	}

	public int VerifyVisibility(String object, String visibility) {
		int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;
		boolean objVisible = true;

		if ((!CommonUtil.isBoolean(visibility)) || (visibility.equals("")) || (visibility.trim().isEmpty())) {
			REPORT.log(LogStatus.FAIL,"Since a valid value for visibility is not given, assuming the default value as 'TRUE'");
			objVisible = true;
		} else {
			objVisible = Boolean.parseBoolean(visibility.trim());
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

		int intVisible = visible(query, true);
		if (intVisible == 0) {
			if (objVisible) {
				REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " is visible.");
				result = 0;
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " is visible.");
				result = 1;
			}

		}
		else if (objVisible) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " is not visible.");
			result = 1;
		} else {
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " is not visible.");
			result = 0;
		}

		return result;
	}
}
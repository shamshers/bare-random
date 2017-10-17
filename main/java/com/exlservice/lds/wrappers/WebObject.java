package com.infinityfw.wrappers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.swing.text.html.HTML;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.Select;
import com.exlservice.lds.libraries.PublicVariables;
import com.exlservice.lds.utillib.CommonUtil;
import com.exlservice.lds.utillib.FwUtil;
import com.exlservice.lds.utillib.NumberUtil;
import com.infinityfw.ObjectBank.enums.LocatableElements;
import com.infinityfw.ObjectBank.enums.XMLEscapeChars;
import com.infinityfw.ObjectBank.exceptions.ObjectBankException;
import com.infinityfw.ObjectBank.exceptions.ObjectFinderException;
import com.infinityfw.ObjectBank.objectbank.ObjectBankLoader;
import com.infinityfw.ObjectBank.objectbank.TestObject;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.thoughtworks.selenium.SeleniumException;


/**
 * @author Shamsher
 *
 */
public class WebObject
{
	public static WebDriver driver;
	public static String WebObjectMsg = "";
	public static boolean EXISTLOCATORVALUE;
	public static boolean setImplicitWait = false;
	public static boolean waitForObjectToExist = true;
	public static long implicitWaitTime = 0L;
	private String method; 
	public static ExtentTest REPORT;

	public int SetFocus(String object)
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

		new Actions(SetBaseState.driver).moveToElement(query).build().perform();

		REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The focus is set on the " + objClass + ".");
		result = 0;
		return result;
	}

	public int StorePropertyValue(String object, String strKey, String property)
	{
		int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		if ((strKey == null) || (strKey.trim().equals("")) || (strKey.isEmpty())) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The Key passed is not valid. Please Verify.");
			result = 1;
			return result;
		}

		if ((property == null) || (property.trim().equals("")) || (property.isEmpty())) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The property passed is not valid. Please Verify.");
			result = 1;
			return result;
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
		String prop = null;
		if (property.trim().equalsIgnoreCase("text")) {
			prop = query.getText();
		}
		else {
			prop = query.getAttribute(property);
		}

		if (prop == null) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The property " + "'" + property + "'" + 
					" does not exist for the object.Failed to store the '" + property + "' property value.");
			return 1;
		}
		if (FwUtil.storeData(strKey, prop)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: " + property + " property value '" + prop + 
					"' is stored successfully in the key '" + strKey + "'.");
			result = 0;
		} else {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to store the '" + property + "' property value.");
			result = 1;
		}
		return result;
	}

	public int VerifyExistence(String object, String existence)
	{
		int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;
		boolean objExist = true;

		if ((!CommonUtil.isBoolean(existence)) || (existence.equals("")) || (existence.trim().isEmpty())) {
			REPORT.log(LogStatus.FAIL,"Since a valid value for existence is not given, assuming the default value as 'TRUE'");
			objExist = true;
		} else {
			objExist = Boolean.parseBoolean(existence.trim());
		}

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE)) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			result = 1;
			return result;
		}

		if (query != null) {
			if (objExist) {
				REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " exists.");
				result = 0;
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " exists.");
				result = 1;
			}

		}
		else if (objExist) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " does not exists.");
			result = 1;
		} else {
			REPORT.log(LogStatus.PASS,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " does not exist.");
			result = 0;
		}

		return result;
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

	public int VerifyEnability(String object, String enablity)
	{
		int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		boolean objEnable = true;

		if ((!CommonUtil.isBoolean(enablity)) || (enablity.equals("")) || (enablity.trim().isEmpty())) {
			REPORT.log(LogStatus.FAIL,"Since a valid value for enability is not given, assuming the default value as 'TRUE'");
			objEnable = true;
		} else {
			objEnable = Boolean.parseBoolean(enablity.trim());
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

		int intEnable = enabled(query, true);
		if (intEnable == 0) {
			if (objEnable) {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " is enabled.");
				result = 0;
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " is enabled.");
				result = 1;
			}

		}
		else if (objEnable) {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " is disabled.");
			result = 1;
		} else {
			REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " is disabled.");
			result = 0;
		}

		return result;
	}

	protected WebElement exist(String object, boolean blnExistence) {

		WebElement query = null;
		try {
			query = findElement(object);
		} catch (ObjectBankException | ObjectFinderException e) {
			e.printStackTrace();
		}
		return query;
	}

	public List<WebElement> getElementsList(String locator, String locatorType)
	{
		List query = null;

		if (locator == null) {
			return query;
		}

		if ((locatorType == null) || (locatorType.trim().equals(""))) {
			REPORT.log(LogStatus.FAIL,"Warning: The object does not have a locator Type");
		}

		REPORT.log(LogStatus.FAIL,"Validating the existence of the object having Identifier as " + locatorType + " = '" + locator + "'");
		if (setImplicitWait)
			SetBaseState.driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
		else {
			SetBaseState.driver.manage().timeouts().implicitlyWait(SetBaseState.maximumFindObjectTime / 1000L, TimeUnit.SECONDS);
		}
		try
		{
			if (locatorType.equalsIgnoreCase("ID")) {
				query = SetBaseState.driver.findElements(By.id(locator));
			} else if (locatorType.equalsIgnoreCase("XPATH")) {
				query = SetBaseState.driver.findElements(By.xpath(locator));
			} else if (locatorType.equalsIgnoreCase("LINK")) {
				query = SetBaseState.driver.findElements(By.linkText(locator));
			}
			else if (locatorType.equalsIgnoreCase("CSS")) {
				query = SetBaseState.driver.findElements(By.cssSelector(locator));
			} else if (locatorType.equalsIgnoreCase("NAME")) {
				query = SetBaseState.driver.findElements(By.name(locator));
			} else {
				REPORT.log(LogStatus.FAIL,"Locator Type:'" + locatorType + "'" + " is not supported, please verify");
				return null;
			}
		}
		catch (NoSuchElementException e) {
			query = null;
		}
		catch (Exception e) {
			query = null;
		}

		return query;
	}

	public int visible(WebElement element, boolean blnVisible)
	{
		int stat = 1;

		boolean visible = false;
		visible = element.isDisplayed();
		if (blnVisible) {
			if (visible)
				stat = 0;
			else {
				stat = 2;
			}
		}
		else if (!visible)
			stat = 0;
		else {
			stat = 2;
		}

		return stat;
	}

	public int enabled(WebElement element, boolean blnEditable)
	{
		int stat = 1;

		boolean editable = false;
		editable = element.isEnabled();
		if (blnEditable) {
			if (editable)
				stat = 0;
			else {
				stat = 2;
			}
		}
		else if (!editable)
			stat = 0;
		else {
			stat = 2;
		}

		return stat;
	}

	protected String getExceptionMessage(Exception e)
	{
		String msg = "";
		msg = e.getMessage();
		if ((e.toString().contains("SeleniumException")) && 
				((e.getCause().toString().contains("ConnectException") & e.getMessage().contains("Connection refused")))) {
			throw new SeleniumException(e);
		}

		return msg;
	}

	public int WaitForObject(String object, String waitTime, String existence)
	{
		boolean shouldExist = true;
		String strMethod = Controller.strKeywordName;
		if (!CommonUtil.isBoolean(existence)) {
			REPORT.log(LogStatus.FAIL,"Since a valid value for existence is not given, assuming the default value of 'TRUE'");
			shouldExist = true;
		} else {
			shouldExist = Boolean.parseBoolean(existence.trim());
		}
		if ((waitTime.equals("")) || (waitTime.equals("")) || (waitTime.isEmpty()))
		{
			implicitWaitTime = Long.valueOf(SetBaseState.maximumFindObjectTime / 1000L).longValue();
			REPORT.log(LogStatus.FAIL,"Since waitime is empty, assuming the default value as =" + implicitWaitTime);
		} else if (!NumberUtil.isPostiveInteger(waitTime)) {
			if (waitTime.equals("0")) {
				implicitWaitTime = 0L;
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The waitTime value '" + waitTime + 
						"' is not a valid positive integer, please verify.");
				int result = 1;
				return result;
			}
		}
		else
		{
			implicitWaitTime = Long.parseLong(waitTime.trim());
		}

		setImplicitWait = true;
		WebElement element = null;
		if (shouldExist)
		{
			waitForObjectToExist = true;
			element = exist(object, shouldExist);
		}
		else
		{
			long sync = SetBaseState.IntervalTimeOut;
			long maxSync = implicitWaitTime * 1000L;

			waitForObjectToExist = false;
			do
			{
				element = exist(object, shouldExist);
				if (element == null)
					break;
				try
				{
					Thread.sleep(sync);
				}
				catch (Exception localException)
				{
				}

				maxSync -= sync;
			}

			while (maxSync >= sync);
		}

		String objClass = getClass().getSimpleName().toString();
		int result = 0;

		if (element != null)
		{
			int result1;
			if (shouldExist)
			{
				REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + "Status: Passed    " + "Message: The " + objClass + " is present");
				result1 = 0;
			} else {
				REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + "Status: Failed    " + "Message: The " + objClass + " is present.");
				result1 = 1;
			}
		}
		else
		{
			int result1;
			if (shouldExist) {
				REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + "Status: Failed    " + "Message: The " + objClass + " is not present.");
				result1 = 1;
			}
			else {
				REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + "Status: Passed    " + "Message: The " + objClass + " is not present.");
				result1 = 0;
			}
		}

		setImplicitWait = false;

		return result;
	}

	public int DragAndDrop(String object, String offsetX, String offsetY)
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

		if ((offsetX == null) || (offsetX.trim().equals(""))) {
			REPORT.log(LogStatus.FAIL,"Message: The offsetX passed is " + offsetX + " .Hence 'offsetX' parameter is considered as the default value '0'");
			offsetX = "0";
		} else {
			if (!NumberUtil.isInteger(offsetX)) {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
						"Status: Failed    " + 
						"Message: offsetX=" + offsetX + " is not a valid offset." + 
						"Please verify. Example of offsets are : 100, +100 , -230 , -500,....");

				return 1;
			}
			offsetX = offsetX.trim();
		}

		if ((offsetY == null) || (offsetY.trim().equals(""))) {
			REPORT.log(LogStatus.FAIL,"Message: The offsetY passed is " + offsetY + " .Hence 'offsetY' parameter is considered as the default value '0'");
			offsetY = "0";
		} else {
			if (!NumberUtil.isInteger(offsetY)) {
				REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
						"Status: Failed    " + 
						"Message: offsetY=" + offsetY + " is not a valid offset." + 
						"Please verify. Example of offsets are : 100, +100 , -230 , -500,....");

				return 1;
			}
			offsetY = offsetY.trim();
		}

		new Actions(SetBaseState.driver).dragAndDropBy(query, Integer.parseInt(offsetX), Integer.parseInt(offsetY)).build().perform();

		REPORT.log(LogStatus.FAIL,"Action: " + strMethod + "    " + 
				"Status: Passed    " + 
				"Message: Drag and drop is successful on " + objClass + " with offsets (" + offsetX + "," + offsetY + ")");

		return 0;
	}

	public String getMethod()
	{
		return this.method;
	}

	public void setMethod(String paramString)
	{
		this.method = paramString;
	}

	public WebElement findElement(String paramElement)
			throws ObjectBankException, ObjectFinderException
	{
		ObjectBankLoader localObjectBankLoader = new ObjectBankLoader(PublicVariables.OBJECTBANKDir + PublicVariables.SEP + "ObjectBank.xml");
		Map localMap = null;
		String str2 = null;
		Iterator localIterator = localObjectBankLoader.getObjects().keySet().iterator();
		Object localObject;
		while (localIterator.hasNext())
		{
			localObject = (TestObject)localIterator.next();
			if (((TestObject)localObject).getObjectName().equals(paramElement))
			{
				str2 = ((TestObject)localObject).getObjectType();
				localMap = (Map)localObjectBankLoader.getObjects().get(localObject);
				break;
			}
		}
		if (localMap.size() == 1)
		{
			int i = 0;
			localObject = (String)localMap.keySet().iterator().next();
			for (LocatableElements localLocatableElements : LocatableElements.values())
				if (localLocatableElements.getLocator().equalsIgnoreCase((String)localObject))
				{
					i = 1;
					break;
				}
			if (i != 0)
				return findTheRightElement((String)localObject, (String)localMap.get(localObject));
			return findTheRightElement(getTag(str2), localMap);
		}
		return findTheRightElement( getTag(str2), localMap);
	}

	public WebElement findTheRightElement( String paramString1, String paramString2)
	{
		if (paramString1.equalsIgnoreCase(LocatableElements.ID.getLocator()))
			return SetBaseState.driver.findElement(By.id(paramString2));
		if (paramString1.equalsIgnoreCase(LocatableElements.NAME.getLocator()))
			return SetBaseState.driver.findElement(By.name(paramString2));
		if (paramString1.equalsIgnoreCase(LocatableElements.LINKTEXT.getLocator()))
			return SetBaseState.driver.findElement(By.linkText(paramString2));
		if (paramString1.equalsIgnoreCase(LocatableElements.XPATH.getLocator()))
			return SetBaseState.driver.findElement(By.xpath(getWebDriverXPATH(paramString2)));
		if (paramString1.equalsIgnoreCase(LocatableElements.CSS.getLocator()))
			return SetBaseState.driver.findElement(By.cssSelector(paramString2));
		if (paramString1.equalsIgnoreCase(LocatableElements.PARTIALLINKTEXT.getLocator()))
			return SetBaseState.driver.findElement(By.partialLinkText(paramString2));
		return null;
	}

	public String getWebDriverXPATH(String paramString)
	{
		for (XMLEscapeChars localXMLEscapeChars : XMLEscapeChars.values())
			if (paramString.contains(localXMLEscapeChars.getXMLEscapeChar()))
				paramString = paramString.replace(localXMLEscapeChars.getXMLEscapeChar(), localXMLEscapeChars.getJavaChar());
		return paramString;
	}

	public String getTag(String paramString)
	{
		for (HTML localHTML : HTML.values())
			if (localHTML.getTagDescription().equals(paramString))
				return localHTML.getTag();
		return null;
	}

	public WebElement findTheRightElement(String paramString, Map<String, String> paramMap)
			throws ObjectFinderException
	{
		try
		{
			List<WebElement> localList = SetBaseState.driver.findElements(By.tagName(paramString));
			ArrayList localArrayList = new ArrayList();
			Set<String> localSet = paramMap.keySet();
			int i = localSet.size();
			Object localObject1 = localList.iterator();
			while (((Iterator)localObject1).hasNext())
			{
				WebElement localWebElement = (WebElement)((Iterator)localObject1).next();
				int j = 1;
				Iterator localIterator = localSet.iterator();
				while (localIterator.hasNext())
				{
					String str3 = (String)localIterator.next();
					if (localWebElement.getAttribute(str3) != null)
					{
						String str1 = localWebElement.getAttribute(str3).trim();
						String str2 = ((String)paramMap.get(str3)).trim();
						if (str2.equals(str1))
						{
							if (j == i)
								localArrayList.add(localWebElement);
							j++;
						}
					}
				}
			}
			if (localArrayList.isEmpty())
				throw new ObjectFinderException("Object { " + getMethod() + "() } was not found with the Given properties: " + paramMap);
			if (localArrayList.size() > 1)
				throw new ObjectFinderException("Multiple Objects { " + getMethod() + "() } found with the given Object Properties: " + paramMap);
			localObject1 = (WebElement)localArrayList.get(0);
			return (WebElement) localObject1;
		}
		finally
		{
			try
			{
				List localList = null;
				Set localSet = null;
				paramMap = null;
				paramString = null;
			}
			catch (Exception localException2)
			{
			}
		}
	}

	public Select select(WebElement paramWebElement)
	{
		Select localSelect = new Select(paramWebElement);
		return localSelect;
	}

	public int MouseUp(String object, String x, String y)
	{
		int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			result = 1;
			return result;
		}
		if (query == null)
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " does not exist , please verify");
			result = 1;
			return result;
		}
		if (x.equals(""))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Since a valid value for X  cordinate is not given, assuming the default value as '0'");
		}
		else if (!NumberUtil.isInteger(x))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The X value '" + x + "' is not a valid integer, please verify.");
			result = 1;
			return result;
		}
		if (y.equals(""))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Since a valid value for Y  cordinate is not given, assuming the default value as '0'");
		}
		else if (!NumberUtil.isInteger(y))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The Y value '" + y + "' is not a valid integer, please verify.");
			result = 1;
			return result;
		}
		Locatable hoverItem = (Locatable)query;
		Mouse mouse = ((HasInputDevices)SetBaseState.driver).getMouse();
		mouse.mouseUp(hoverItem.getCoordinates());
		REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: MouseUp is successful on " + objClass + ".");
		result = 0;
		return result;
	}

	public int MouseDown(String object, String x, String y)
	{
		int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");

			return 1;
		}
		if (query == null)
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " does not exist , please verify");

			return 1;
		}
		if (x.equals(""))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Since a valid value for X  cordinate is not given, assuming the default value as '0'");
		}
		else if (!NumberUtil.isInteger(x))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The X value '" + x + "' is not a valid integer, please verify.");

			return 1;
		}
		if (y.equals(""))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Since a valid value for Y  cordinate is not given, assuming the default value as '0'");
		}
		else if (!NumberUtil.isInteger(y))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The Y value '" + y + "' is not a valid integer, please verify.");
			return 1;
		}
		if (SetBaseState.Browser.equalsIgnoreCase("firefox")) {
			try
			{
				String code = 
						"var fireOnThis = arguments[0];var evObj = document.createEvent('MouseEvents');evObj.initEvent( 'mousedown', true, true );fireOnThis.dispatchEvent(evObj);";

				((JavascriptExecutor)SetBaseState.driver).executeScript(code, new Object[] { query });
				REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: MouseDown is successful on " + objClass + ".");

				return 0;
			}
			catch (Exception e)
			{
				REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " does not exist , please verify");

				return 1;
			}
		}
		Locatable hoverItem = (Locatable)query;
		Mouse mouse = ((HasInputDevices)SetBaseState.driver).getMouse();
		mouse.mouseDown(hoverItem.getCoordinates());
		REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: MouseDown is successful on " + objClass + ".");
		result = 0;
		return result;
	}

	public int MouseOver(String object)
	{
		int result = 1;
		String objClass = getClass().getSimpleName().toString();
		String strMethod = Controller.strKeywordName;

		WebElement query = exist(object, true);
		if ((query == null) && (!EXISTLOCATORVALUE))
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: Failed to retrieve the locator value.");
			result = 1;
			return result;
		}
		if (query == null)
		{
			REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Failed    " + "Message: The " + objClass + " does not exist , please verify");
			result = 1;
			return result;
		}
		Locatable hoverItem = (Locatable)query;
		Mouse mouse = ((HasInputDevices)SetBaseState.driver).getMouse();
		mouse.mouseMove(hoverItem.getCoordinates());

		REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + "Status: Passed    " + "Message: The " + objClass + " is hovered successfully.");
		result = 0;
		return result;
	}

	protected List<WebElement> Objectexist(String object, boolean blnExistence) {

		List<WebElement> query = null;
		try {
			query = findElements(object);
		} catch (ObjectBankException | ObjectFinderException e) {
			e.printStackTrace();
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	public List<WebElement> findElements(String paramElement)
			throws ObjectBankException, ObjectFinderException
	{
		ObjectBankLoader localObjectBankLoader = new ObjectBankLoader(PublicVariables.OBJECTBANKDir + PublicVariables.SEP + "ObjectBank.xml");
		Map localMap = null;
		String str2 = null;
		Iterator localIterator = localObjectBankLoader.getObjects().keySet().iterator();
		Object localObject;
		while (localIterator.hasNext())
		{
			localObject = (TestObject)localIterator.next();
			if (((TestObject)localObject).getObjectName().equals(paramElement))
			{
				str2 = ((TestObject)localObject).getObjectType();
				localMap = (Map)localObjectBankLoader.getObjects().get(localObject);
				break;
			}
		}
		if (localMap.size() == 1)
		{
			int i = 0;
			localObject = (String)localMap.keySet().iterator().next();
			for (LocatableElements localLocatableElements : LocatableElements.values())
				if (localLocatableElements.getLocator().equalsIgnoreCase((String)localObject))
				{
					i = 1;
					break;
				}
			if (i != 0)
				return findTheRightElements((String)localObject, (String)localMap.get(localObject));
			//return findTheRightElements(getTag(str2), localMap);
		}
		return (List<WebElement>) findTheRightElement( getTag(str2), localMap);
	}

	public List<WebElement> findTheRightElements( String paramString1, String paramString2)
	{
		if (paramString1.equalsIgnoreCase(LocatableElements.ID.getLocator()))
			return SetBaseState.driver.findElements(By.id(paramString2));
		if (paramString1.equalsIgnoreCase(LocatableElements.NAME.getLocator()))
			return SetBaseState.driver.findElements(By.name(paramString2));
		if (paramString1.equalsIgnoreCase(LocatableElements.LINKTEXT.getLocator()))
			return SetBaseState.driver.findElements(By.linkText(paramString2));
		if (paramString1.equalsIgnoreCase(LocatableElements.XPATH.getLocator()))
			return SetBaseState.driver.findElements(By.xpath(getWebDriverXPATH(paramString2)));
		if (paramString1.equalsIgnoreCase(LocatableElements.CSS.getLocator()))
			return SetBaseState.driver.findElements(By.cssSelector(paramString2));
		if (paramString1.equalsIgnoreCase(LocatableElements.PARTIALLINKTEXT.getLocator()))
			return SetBaseState.driver.findElements(By.partialLinkText(paramString2));
		return null;
	}

}

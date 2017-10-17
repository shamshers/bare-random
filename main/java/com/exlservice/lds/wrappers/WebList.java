package com.infinityfw.wrappers;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import com.exlservice.lds.utillib.CommonUtil;
import com.exlservice.lds.utillib.FwUtil;
import com.exlservice.lds.utillib.NumberUtil;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.LogStatus;

public class WebList extends WebObject
{
  public int VerifyNoItemsDuplication(String object)
  {
    int result = 1;
    String strMethod = Controller.strKeywordName;

    WebElement query = exist(object, true);
    if ((query == null) && (!WebObject.EXISTLOCATORVALUE)) {
     REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION	, "Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Failed to retrieve the locator value.");
      result = 1;
      return result;
    }if (query == null) {
      REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION, "Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The list does not exist , please verify");
      result = 1;
      return result;
    }

    String duplicateItems = "";
    boolean isduplicatePresent = false;
    Select listBox = new Select(query);
    List listOptions = listBox.getOptions();

    HashSet set = new HashSet();
    for (int i = 0; i < listOptions.size(); i++) {
      boolean val = set.add(((WebElement)listOptions.get(i)).getAttribute("text"));
      if (!val) {
        duplicateItems = duplicateItems + 
          ((WebElement)listOptions.get(i)).getAttribute("text") + " ";

        isduplicatePresent = true;
      }

    }

    if (isduplicatePresent) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The list contains  the duplicate values " + 
        duplicateItems + ".");
      return 1;
    }

    REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
      "Status: Passed    " + 
      "Message: The list contains no duplicate values.");
    return 0;
  }

  public int StoreSelectedItems(String object, String strKey)
  {
    int result = 1;
    String strMethod = Controller.strKeywordName;

    if ((strKey == null) || (strKey.trim().equals("")) || 
      (strKey.trim().isEmpty())) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The Key passed is not valid. Please Verify.");
      result = 1;
      return result;
    }

    WebElement query = exist(object, true);
    if ((query == null) && (!WebObject.EXISTLOCATORVALUE)) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Failed to retrieve the locator value.");
      result = 1;
      return result;
    }if (query == null) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The list does not exist , please verify");
      result = 1;
      return result;
    }

    Select listBox = new Select(query);
    List<WebElement> listOptions = listBox.getAllSelectedOptions();
    String[] items = new String[listOptions.size()];
    for (int i = 0; i < listOptions.size(); i++) {
      items[i] = ((WebElement)listOptions.get(i)).getAttribute("text");
    }

    if (items.length < 1)
    {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + 
        strMethod + 
        "    " + 
        "Status: Failed    " + 
        "Message: The list does not contain any selected items , please verify");
      result = 1;
      return result;
    }

    String listItems = FwUtil.convertToArrayString(items);
    if (FwUtil.storeData(strKey, String.valueOf(listItems)))
    {
        REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Passed    " + "Message:The items " + listItems + 
        " are stored successfully in the key " + strKey + ". " + 
        "The items are " + '^' + 
        " seperated.");
      result = 0;
    } else {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Fail to store the items " + listItems + 
        " in the key " + strKey + ". Please verify.");
      result = 1;
    }

    return result;
  }

  public int StoreItems(String object, String strKey) {
    int result = 1;
    String strMethod = Controller.strKeywordName;

    if ((strKey == null) || (strKey.trim().equals("")) || (strKey.isEmpty())) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The Key passed is not valid. Please Verify.");
      result = 1;
      return result;
    }

    WebElement query = exist(object, true);
    if ((query == null) && (!WebObject.EXISTLOCATORVALUE)) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Failed to retrieve the locator value.");
      result = 1;
      return result;
    }if (query == null) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The list does not exist , please verify");
      result = 1;
      return result;
    }

    Select listBox = new Select(query);
    List listOptions = listBox.getOptions();
    String[] items = new String[listOptions.size()];
    for (int i = 0; i < listOptions.size(); i++) {
      items[i] = ((WebElement)listOptions.get(i)).getAttribute("text");
    }

    if (items.length < 1)
    {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + 
        strMethod + 
        "    " + 
        "Status: Failed    " + 
        "Message: The list does not contain any items , please verify");
      result = 1;
      return result;
    }

    String listItems = FwUtil.convertToArrayString(items);
    if (FwUtil.storeData(strKey, String.valueOf(listItems)))
    {
        REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Passed    " + "Message:The items " + listItems + 
        " are stored successfully in the key " + strKey + ". " + 
        "The items are " + '^' + 
        " seperated.");
      result = 0;
    } else {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Fail to store the items " + listItems + 
        " in the key " + strKey + ". Please verify.");
      result = 1;
    }

    return result;
  }

  public int VerifySize(String object, String expectedSize) {
    int result = 1;
    String strMethod = Controller.strKeywordName;
    int expSize = 0;
    if (expectedSize.equals(""))
    {
        REPORT.log(LogStatus.WARNING,"Since a valid value for expectedSize is not given, assuming the default value as '0'");
      expSize = 0; } else {
      if (!NumberUtil.isPostiveInteger(expectedSize)) {
        if (expectedSize.equals("0")) {
          expSize = 0;
        }
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
          "Status: Failed    " + 
          "Message: The expectedSize value '" + expectedSize + 
          "' is not a valid positive integer, please verify.");
        result = 1;
        return result;
      }

      expSize = Integer.parseInt(expectedSize.trim());
    }

    WebElement query = exist(object, true);
    if ((query == null) && (!WebObject.EXISTLOCATORVALUE)) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Failed to retrieve the locator value.");
      result = 1;
      return result;
    }if (query == null) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The list does not exist , please verify");
      result = 1;
      return result;
    }
    Select listbox = new Select(query);
    int optionsCount = listbox.getOptions().size();

    if (optionsCount == 0)
    {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + 
        strMethod + 
        "    " + 
        "Status: Failed    " + 
        "Message: The list does not contains any items , please verify");
      result = 1;
      return result;
    }
    if (expSize == optionsCount)
    {
        REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION,"Action: " + 
        strMethod + 
        "    " + 
        "Status: Passed    " + 
        "Message: The actual number of items=Expected number of items=" + 
        expSize);
      result = 0;
    } else {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Expected number of items=" + expSize + 
        " ,The actual number of items=" + optionsCount);
      result = 1;
    }
    return result;
  }

  public int SelectItemByIndex(String object, String intIndex)
  {
    int result = 1;
    String strMethod = Controller.strKeywordName;
    int idx = 1;

    if (intIndex.equals(""))
    {
        REPORT.log(LogStatus.WARNING,"Since a valid value for index is not given, assuming the default value as '1'");
      idx = 1; } else {
      if (!NumberUtil.isPostiveInteger(intIndex))
      {
    	    REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
          "Status: Failed    " + "Message: The intIndex value '" + 
          intIndex + 
          "' is not a valid positive integer, please verify.");
        result = 1;
        return result;
      }

      idx = Integer.parseInt(intIndex.trim()) - 1;
    }

    WebElement query = exist(object, true);
    if ((query == null) && (!WebObject.EXISTLOCATORVALUE)) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Failed to retrieve the locator value.");
      result = 1;
      return result;
    }if (query == null) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The list does not exist , please verify");
      result = 1;
      return result;
    }

    Select listBox = new Select(query);

    listBox.selectByIndex(idx);

    List<WebElement> items = listBox.getAllSelectedOptions();

    for (WebElement webElement : items) {
        REPORT.log(LogStatus.INFO, Controller.TEST_DESCRIPTION,"The item " + 
        webElement.getText() + " is selected successfully");
    }
    try
    {
      Thread.sleep(SetBaseState.IntervalTimeOut);
    } catch (Exception localException) {
    }
    REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
      "Status: Passed    " + "Message: The item indexed '" + 
      intIndex + "' is selected successfully.");
    result = 0;
    return result;
  }

  public int SelectItemAndWait(String object, String item)
  {
    String strMethod = Controller.strKeywordName;

    WebElement query = exist(object, true);
    if ((query == null) && (!WebObject.EXISTLOCATORVALUE)) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Failed to retrieve the locator value.");

      return 1;
    }if (query == null) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The list does not exist , please verify");

      return 1;
    }
    try
    {
      Select listBox = new Select(query);
      listBox.selectByVisibleText(item);
    }
    catch (Exception e)
    {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + 
        strMethod + 
        "    " + 
        "Status: Failed    " + 
        "Message: The item to be selected may not be present in the list.Please verify");
      return 1;
    }

    CommonUtil.Wait(SetBaseState.maximumFindObjectTime);

    REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
      "Status: Passed    " + "Message: The item " + item + 
      " are selected successfully.");
    return 0;
  }

  public int SelectItems(String object, Object items)
  {
    int result = 1;
    String strMethod = Controller.strKeywordName;

    WebElement query = exist(object, true);
    if ((query == null) && (!WebObject.EXISTLOCATORVALUE)) {
      REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION, "Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: Failed to retrieve the locator value.");
      result = 1;
      return result;
    }if (query == null) {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Failed    " + 
        "Message: The list does not exist , please verify");
      result = 1;
      return result;
    }

    ArrayList itemsList = (ArrayList)items;

    Select listBox = new Select(query);

    if (itemsList.size() > 1)
    {
      if (!listBox.isMultiple())
      {
    	    REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + 
          strMethod + 
          "    " + 
          "Status: Failed    " + 
          "Message: Multiple select is not possible in this list, please verify");
        result = 1;
        return result;
      }
    }

    String strItems = "";

    List<WebElement> allOptions = listBox.getOptions();
    List optionsText = new ArrayList();
    for (WebElement webElement : allOptions) {
      optionsText.add(webElement.getText());
    }
    for (int i = 0; i < itemsList.size(); i++) {
      String item = itemsList.get(i).toString();
      if (optionsText.contains(item)) {
        listBox.selectByVisibleText(item);
        strItems = strItems + (i + 1) + ".  " + item;
      }
    }

    try
    {
      Thread.sleep(SetBaseState.IntervalTimeOut);
    }
    catch (Exception localException) {
    }
    if (strItems.length() != 0) {
        REPORT.log(LogStatus.PASS, Controller.TEST_DESCRIPTION,"Action: " + strMethod + "    " + 
        "Status: Passed    " + "Message: The items " + strItems + 
        " are selected successfully.");
      result = 0;
    }
    else {
        REPORT.log(LogStatus.FAIL, Controller.TEST_DESCRIPTION,"Action: " + 
        strMethod + 
        "    " + 
        "Status: Failed    " + 
        "Message: The items to be selected may not be present in the list.Please verify");
      result = 1;
    }
    return result;
  }
}

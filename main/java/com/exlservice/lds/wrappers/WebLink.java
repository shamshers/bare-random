package com.infinityfw.wrappers;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.infinityfw.driver.Controller;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Shamsher
 *
 */
public class WebLink extends WebObject
{
	  /**
	 * @param link
	 */
	public int VerifyLinkOnPage(String link)
	  {
	    if ((link == null) || (link.trim().equals("")) || (link.isEmpty())) {
	       REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
	        "Status: Failed    " + 
	        "Message: The link's text passed is null. Please Verify.");
	      return 1;
	    }

	    List<WebElement> links = SetBaseState.driver.findElements(By.tagName("a"));

	    for (WebElement webElement : links)
	    {
	      if (!webElement.getText().equalsIgnoreCase(link))
	        continue;
	       REPORT.log(LogStatus.PASS,"Action: " + Controller.strKeywordName + "    " + 
	        "Status: Passed    " + 
	        "Message: The link '" + link + "' is present on the page.");
	      return 0;
	    }

	     REPORT.log(LogStatus.FAIL,"Action: " + Controller.strKeywordName + "    " + 
	      "Status: Failed    " + 
	      "Message: The link '" + link + "' is not present on the page, please verify.");
	    return 1;
	  }
}
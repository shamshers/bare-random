package com.infinityfw.utillib;

import java.util.Date;

import com.relevantcodes.extentreports.ExtentReports;

public class ReportUtil {
	
	 static ExtentReports extent;
	    
	    public static ExtentReports getInstance() {
	    	  Date d = new Date();
				String date=d.toString().replaceAll(" ", "_");
	        if (extent == null) {
	            extent = new ExtentReports("d:\\temp\\report.html", true);
	            
	            // optional
	            extent.config()
	                .documentTitle("Automation Report")
	                .reportName("Regression")
	                .reportHeadline("");
	               
	            // optional
	            extent
	                .addSystemInfo("Selenium Version", "2.46")
	                .addSystemInfo("Environment", "QA");
	        }
	        return extent;
	    }

}

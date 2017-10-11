package com.infinityfw.utillib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.infinityfw.libraries.PublicVariables;
import com.infinityfw.wrappers.WebObject;
import com.relevantcodes.extentreports.LogStatus;

public class DateUtil
{
  public static String changeDate(Date date, String dtFormat, int incrDate, int incrMonth, int incrYear)
  {
    SimpleDateFormat dateFormat = null;
    Calendar calendar = null;
    try
    {
      dateFormat = new SimpleDateFormat(dtFormat);
      calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.add(5, incrDate);
      calendar.add(2, incrMonth);
      calendar.add(1, incrYear);
    }
    catch (Exception ex)
    {
    	 WebObject.REPORT.log(LogStatus.ERROR, "Exception while modifying the date . StackTrace: <br/>" + FwUtil.getStackTrace(ex));
      return "";
    }
    return dateFormat.format(calendar.getTime());
  }

  public static boolean compareDates(Date firstDate, Date secondDate, String operation)
  {
    boolean result = false;
    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    calendar1.setTime(firstDate);
    calendar2.setTime(secondDate);

    if (operation.equals("=="))
    {
      if (calendar1.equals(calendar2))
      {
        result = true;
      }

    }
    else if (operation.equals("<"))
    {
      if (calendar1.before(calendar2))
      {
        result = true;
      }
    }
    else if (operation.equals(">"))
    {
      if (calendar1.after(calendar2))
      {
        result = true;
      }
    }
    else if (operation.equals("!="))
    {
      if (!calendar1.equals(calendar2))
      {
        result = true;
      }

    }

    return result;
  }

  public static String getDateInFormat(String sourceDate, String sourceFormat, String resultFormat)
  {
    try
    {
      DateFormat dateFormat = null;

      dateFormat = new SimpleDateFormat(sourceFormat);
      Date date = dateFormat.parse(sourceDate);

      SimpleDateFormat sdfDestination = new SimpleDateFormat(resultFormat);

      sourceDate = sdfDestination.format(date);
    }
    catch (Exception ex)
    {
    	WebObject.REPORT.log(LogStatus.ERROR, "Exception while getting the date in the specified format . StackTrace: <br/>" + FwUtil.getStackTrace(ex));
      sourceDate = "";
    }
    return sourceDate;
  }

  public static String CalculateDifferenceInDates(Date startDate, Date endDate)
  {
    long difference = 0L;
    try
    {
      Calendar calendar1 = Calendar.getInstance();
      Calendar calendar2 = Calendar.getInstance();
      calendar1.setTime(startDate);
      calendar2.setTime(endDate);

      difference = Math.abs(calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / 86400000L;
    }
    catch (Exception ex)
    {
    	WebObject.REPORT.log(LogStatus.ERROR, "Exception while calculating the difference between the dates " + startDate + " and " + endDate + " . StackTrace: <br/>" + FwUtil.getStackTrace(ex));
      return null;
    }
    return Long.toString(difference);
  }
}

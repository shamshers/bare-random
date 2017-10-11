package com.infinityfw.utillib;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.infinityfw.libraries.PublicVariables;
import com.infinityfw.wrappers.SetBaseState;
import com.infinityfw.wrappers.WebObject;
import com.relevantcodes.extentreports.LogStatus;

public class CommonUtil
{
  public static String UtilMsg = "";
  public static long timeInMilliSeconds = 0L;
private static Throwable ex;
  public static List<Object> convertArrayToList(Object[] a)
  {
    List arr = new ArrayList();
    for (int i = 0; i < a.length; i++) {
      arr.add(a[i]);
    }
    return arr;
  }

  public static boolean copyList(List<Object> scr, List<Object> dest)
  {
    boolean stat = false;
    for (int i = 0; i < scr.size(); i++) {
      dest.add(scr.get(i));
    }
    return stat;
  }

  public static boolean isSubList(List<Object> mainList, List<Object> subList)
  {
    UtilMsg = "";
    boolean stat = false;
    if (subList.size() > mainList.size()) {
      stat = false;
      UtilMsg = "The subList have more Items than the main List";
      return stat;
    }

    ArrayList tempSubList = new ArrayList();
    ArrayList tempMainSubList = new ArrayList();
    copyList(subList, tempSubList);
    copyList(mainList, tempMainSubList);
    for (int i = 0; i < subList.size(); i++) {
      Object o = subList.get(i);
      if (tempMainSubList.contains(o)) {
        tempMainSubList.remove(o);
        tempSubList.remove(o);
        UtilMsg = UtilMsg + "\t\t" + ": The item '" + o.toString() + "' exist";
      } else {
        UtilMsg = UtilMsg + "\t\t" + ": The item '" + o.toString() + "' does not exist";
      }
    }

    if (tempSubList.isEmpty()) {
      stat = true;
    }
    return stat;
  }

  public static boolean isBoolean(String strValue)
  {
    UtilMsg = "";
    boolean flag = false;
    try {
      if ((strValue != null) && ((strValue.trim().equalsIgnoreCase("true")) || (strValue.trim().equalsIgnoreCase("false"))))
        flag = true;
    }
    catch (Exception e) {
      flag = false;
    }
    return flag;
  }

  public static String getCurrentDateTime(String format)
  {
    UtilMsg = "";
    DateFormat dateFormat = null;
    Date date = null;
    try {
      dateFormat = new SimpleDateFormat(format);
      date = new Date();
    } catch (Exception ex) {
    	WebObject.REPORT.log(LogStatus.ERROR,"Exception while getting the date in the specified format" + format + " . StackTrace: <br/>" + FwUtil.getStackTrace(ex));
      return null;
    }
    return dateFormat.format(date);
  }

  public static String getDifferenceInDates(Date startDate, Date endDate)
  {
    timeInMilliSeconds = 0L;
    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    calendar1.setTime(startDate);
    calendar2.setTime(endDate);

    if (startDate.after(endDate)) {
    	WebObject.REPORT.log(LogStatus.WARNING, "The start date " + startDate + " cannot be after the end date " + endDate + ". Please verify.");
      return null;
    }
    long millisec = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
    timeInMilliSeconds = millisec;
    return generateDateFormatString(millisec);
  }

  public static String generateDateFormatString(long millisec)
  {
    long hours = millisec / 3600000L;
    long minutes = millisec % 3600000L / 60000L;
    long seconds = millisec % 3600000L % 60000L / 1000L;

    String strMilli = Long.toString(millisec % 1000L);
    String strSeconds = Long.toString(seconds % 60L);
    String strMins = "";
    String strHours = "";

    if (strSeconds.length() < 2)
      strSeconds = "0" + strSeconds;
    else {
      strSeconds = strSeconds.substring(0, 2);
    }

    if (Long.toString(minutes).length() < 2)
      strMins = "0" + Long.toString(minutes);
    else {
      strMins = Long.toString(minutes).substring(0, 2);
    }
    if (Long.toString(hours).length() < 2)
      strHours = "0" + Long.toString(hours);
    else {
      strHours = Long.toString(hours).substring(0, 2);
    }

    if (strMilli.length() < 3)
      strMilli = "0" + strMilli;
    else {
      strMilli = strMilli.substring(0, 3);
    }

    return strHours + ":" + strMins + ":" + strSeconds + "." + strMilli;
  }

  public static Date getDate(String format, String date)
  {
    DateFormat dateFormat = null;
    Date formattedDate = null;
    try {
      dateFormat = new SimpleDateFormat(format);
      formattedDate = dateFormat.parse(date);
    } catch (Exception e) {
      formattedDate = null;
    }
    return formattedDate;
  }

  public static void copyFile(String src, String dest)
    throws IOException
  {
    UtilMsg = "";
    File inputFile = new File(src);
    File outputFile = new File(dest);

    FileReader in = new FileReader(inputFile);
    FileWriter out = new FileWriter(outputFile);
    int c;
    while ((c = in.read()) != -1)
    {
     // int c;
      out.write(c);
    }in.close();
    out.close();
  }

  public static boolean isNumericValue(String strValue)
  {
    UtilMsg = "";
    try {
      Double.parseDouble(strValue.trim());
      return true; } catch (NumberFormatException feException) {
    }
    return false;
  }

  public static boolean isLongValue(String strValue)
  {
    UtilMsg = "";
    try {
      Long.parseLong(strValue.trim());
      return true; } catch (NumberFormatException feException) {
    }
    return false;
  }

  public static Integer[] sortIntArray(Integer[] arr)
  {
    for (int i = 0; i < arr.length - 1; i++) {
      for (int j = i; j < arr.length - 1; j++) {
        int start = arr[i].intValue();
        if (start > arr[(j + 1)].intValue()) {
          arr[i] = arr[(j + 1)];
          arr[(j + 1)] = Integer.valueOf(start);
        }
      }
    }
    return arr;
  }

  public static boolean compareStringListInSequence(List<Object> list1, List<Object> list2, boolean bTrim, boolean bCase)
  {
    UtilMsg = "";
    boolean status = false;
    if (list1.size() != list2.size()) {
      status = false;
      UtilMsg = "The number of items in the Lists are not equal.";
      return status;
    }

    ArrayList arrList1 = (ArrayList)list1;
    ArrayList arrList2 = (ArrayList)list2;

    for (int i = 0; i < arrList1.size(); i++) {
      int j = i; if ((j >= arrList2.size()) || 
        (StringUtil.stringEqual(arrList1.get(i).toString(), arrList2.get(i).toString(), bTrim, bCase))) continue;
      UtilMsg = "The lists are not in sequence.";
      return status;
    }

    UtilMsg = "The lists are in sequence.";
    status = true;
    return status;
  }

  public static String getMYSQLFormatTime(String time) {
    String formatTime = time;
    String[] arrTime = (String[])null;
    if (formatTime.contains(".")) {
      arrTime = formatTime.split("\\.");
      formatTime = arrTime[0];
    }
    return formatTime;
  }

  private static String decode(String s) {
    return StringUtils.newStringUtf8(Base64.decodeBase64(s));
  }

  private static byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
  {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec secretKeySpecy = new SecretKeySpec(key, "AES");
    IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
    cipher.init(2, secretKeySpecy, ivParameterSpec);
    cipherText = cipher.doFinal(cipherText);
    return cipherText;
  }

  private static byte[] getKeyBytes(String key) throws UnsupportedEncodingException {
    byte[] keyBytes = new byte[16];
    byte[] parameterKeyBytes = key.getBytes("UTF-8");
    System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
    return keyBytes;
  }

  private static String decrypt(String encryptedText, String key)
    throws Exception
  {
    try
    {
      byte[] cipheredBytes = Base64.decodeBase64(encryptedText.getBytes("UTF-8"));
      byte[] keyBytes = getKeyBytes(key);
      return new String(decrypt(cipheredBytes, keyBytes, keyBytes), "UTF-8"); } catch (Exception ex) {
    }
    throw new Exception(ex.getMessage());
  }

  public static String getSecureData(String encString)
    throws Exception
  {
    if (encString == null) {
      throw new Exception("Missing Encrypted Data Exception");
    }

    String sep = "þ";
    String[] arrEncData = encString.split(sep);

    if (arrEncData.length == 1) {
      throw new Exception("UnSupported Encryption Format Exception");
    }

    String sKey = decode(arrEncData[1]);

    System.out.println("key=" + sKey + "\nEncData=" + arrEncData[0]);

    String sData = decrypt(arrEncData[0], sKey);
    return sData;
  }

  public static boolean Wait(long timeToWait) {

		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver d)
			{
				return Boolean.valueOf(((JavascriptExecutor)SetBaseState.driver).executeScript("return document.readyState", new Object[0]).equals("complete"));
			}

		};
		WebDriverWait wait = new WebDriverWait(SetBaseState.driver, timeToWait);
		try
		{
			wait.until(expectation);
			return true;
		}
		catch (Throwable error) {}
		return false;
	}
  
  public static String skipFileSpecialChars(String fileName)
  {
    String strName = fileName;

    if ((strName != null) && (!strName.isEmpty())) {
      Pattern pattern = Pattern.compile("[^ a-zA-z 0-9 ! \\$ % _ \\- \\. { } \\^ ~ [ ] \\( \\) ; ! @ # & = \\+ [\\\\\\\\]+]+");
      strName = pattern.matcher(fileName).replaceAll("");
    }

    return strName;
  }
}

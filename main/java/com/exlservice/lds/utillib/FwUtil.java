package com.exlservice.lds.utillib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.exlservice.lds.driver.Controller;
import com.test.ObjectInfo;
import com.test.ObjectPreference;

/**
 * @author shamshersingh
 *
 */

public class FwUtil {
  private static final Logger LOGGER = Logger.getLogger(FwUtil.class);
  public static String msg;
  public static String errorMsg;
  public static String warningMsg;
  public static String msg1 = "WARNING: Single '/' in ";
  public static String msg2 = " , if not followed by a special character will be ignored.";
  public static String FWPATH;

  /**
   * @param c
   * @return
   */
  public static String getPackage(Object c) {
    String packageName = null;
    msg = "";
    try {
      packageName = c.getClass().getPackage().getName();
    } catch (NullPointerException npe) {
      msg = "Either the Class '" + c.getClass()
          + "' is under a default package or no package is define. " + "Exception:" + npe;
    } catch (Exception e) {
      msg = "An exception occurred while retrieving the package : Exception :" + e;
    }
    return packageName;
  }

  /**
   * @param input
   * @param delimiter
   * @param doTrim
   * @param doCaps
   * @return
   */
  public static List<String> splitStringIntoArrayWithNullsOriginal(String input, String delimiter,
      boolean doTrim, boolean doCaps) {
    int i = 0;
    int index = 0;
    int lastIndex = 0;

    List a = new ArrayList();

    if (input.equals("")) {
      a.add("");
      return a;
    }

    while (true) {
      index = input.indexOf(delimiter, i);
      if (index == -1) {
        if (input.charAt(lastIndex) == delimiter.charAt(0))
          lastIndex++;
        String value = input.substring(lastIndex, input.length());
        if (doTrim)
          value = StringUtil.trim(value);
        if (doCaps)
          value = value.toUpperCase();
        a.add(value);
        break;
      }
      if (((index - lastIndex == 1) || (index == lastIndex))
          && ((lastIndex != 0) || (index == 0))) {
        a.add("");
      } else {
        if (input.charAt(lastIndex) == delimiter.charAt(0))
          lastIndex++;
        String value = input.substring(lastIndex, index);
        if (doTrim)
          value = StringUtil.trim(value);
        if (doCaps)
          value = value.toUpperCase();
        a.add(value);
      }
      lastIndex = index;
      i = index + 1;
    }
    return a;
  }


  /**
   * @param inputList
   * @return
   */
  public static List<String> listToUpper(List<String> inputList) {
    List outputList = new ArrayList();
    for (int i = 0; i < inputList.size(); i++) {
      outputList.add(i, ((String) inputList.get(i)).toString().toUpperCase());
    }
    return outputList;
  }

  /**
   * @param input
   * @return
   */
  public static Object resolveSpecialCharactersAndVariables(String input) {
    StringBuilder result = new StringBuilder();
    Boolean escape = Boolean.valueOf(false);
    int countOpenBraces = 0;
    StringBuilder varName = new StringBuilder();
    errorMsg = "";

    for (int i = 0; i < input.length(); i++) {
      char curr = input.charAt(i);
      switch (curr) {
        case '{':
          if (countOpenBraces > 0)
            varName.append(curr);
          if (escape.booleanValue()) {
            if (countOpenBraces < 1)
              result.append(curr);
            escape = Boolean.valueOf(false);
          } else {
            countOpenBraces++;
            varName.replace(0, varName.length(), "");
          }
          break;
        case '}':
          if (escape.booleanValue()) {
            if (countOpenBraces < 1)
              result.append(curr);
            escape = Boolean.valueOf(false);
          } else {
            if (countOpenBraces < 1) {
              errorMsg = "Syntax Error - escape character required before '}' in " + input;
              return null;
            }
            countOpenBraces--;
            String resolvedVarName =
                resolveSpecialCharactersOnly(varName.toString().trim()).toString();
            String val = (String) Controller.STOREHASHMAP.get(resolvedVarName.toUpperCase());
            if (val == null) {
              errorMsg = "Key " + varName.toString() + " does not exist";
              return null;
            }

            if (countOpenBraces < 1) {
              result.append(val);
            }
          }
          if (countOpenBraces <= 0)
            continue;
          varName.append(curr);
          break;
        case '~':
          if (countOpenBraces > 0)
            varName.append(curr);
          escape = Boolean.valueOf(toggle(escape.booleanValue()));
          if (escape.booleanValue())
            continue;
          if (countOpenBraces >= 1)
            continue;
          result.append(curr);

          break;
        case '|':
        default:
          if (escape.booleanValue())
            warningMsg = msg1 + input + msg2;
          if (countOpenBraces > 0)
            varName.append(curr);
          else
            result.append(curr);
          escape = Boolean.valueOf(false);
      }

    }

    if (countOpenBraces > 0) {
      errorMsg = "Syntax Error - escape character required before '{' in " + input;
      result = null;
    }

    if (escape.booleanValue())
      warningMsg = msg1 + input + msg2;
    return result;
  }

  private static boolean toggle(boolean b) {
    return !b;
  }

  /**
   * @param input
   * @return
   */
  public static Object resolveSpecialCharactersOnly(String input) {
    StringBuilder result = new StringBuilder();
    Boolean escape = Boolean.valueOf(false);

    for (int i = 0; i < input.length(); i++) {
      char curr = input.charAt(i);
      switch (curr) {
        case '{':
          if (escape.booleanValue()) {
            escape = Boolean.valueOf(false);
          }
          result.append(curr);

          break;
        case '}':
          if (escape.booleanValue()) {
            escape = Boolean.valueOf(false);
          }
          result.append(curr);
          break;
        case '~':
          escape = Boolean.valueOf(toggle(escape.booleanValue()));
          if (escape.booleanValue())
            continue;
          result.append(curr);
          break;
        case '|':
        default:
          if (escape.booleanValue())
            warningMsg = msg1 + input + msg2;
          result.append(curr);
          escape = Boolean.valueOf(false);
      }

    }

    if (escape.booleanValue()) {
      warningMsg = msg1 + input + msg2;
    }
    return result;
  }

  /**
   * @param objInfo
   * @return
   */
  public static boolean resolveObjectProperties(ObjectInfo objInfo) {
    boolean success = true;

    HashMap props = objInfo.getLocators();
    HashMap resultProps = new HashMap();

    warningMsg = "";
    for (Iterator localIterator = props.keySet().iterator(); localIterator.hasNext();) {
      Object propName = localIterator.next();
      String propValue = ((String) props.get(propName)).toString();
      Object o = resolveSpecialCharactersAndVariables(propValue);
      if (o == null) {
        success = false;
        LOGGER.info("Status: Failed   Message: " + errorMsg + " in property '" + propName
            + "' of Object '" + objInfo.getObjectName() + "'.");
        break;
      }
      String updatedPropValue = o.toString();
      resultProps.put(propName.toString(), updatedPropValue);
    }
    if (success) {
      objInfo.setUpdatedLocators(resultProps);
    }
    if (!warningMsg.equals("")) {
      LOGGER.warn(warningMsg);
    }
    return success;
  }

  /**
   * @param input
   * @param doTrim
   * @param uppercase
   * @return
   */
  public static List<String> resolveDataSeparator(String input, boolean doTrim, boolean uppercase) {
    StringBuilder item = new StringBuilder();
    boolean escape = false;
    List array = new ArrayList();

    errorMsg = "";
    item.replace(0, item.length(), "");
    for (int i = 0; i < input.length(); i++) {
      char curr = input.charAt(i);
      switch (curr) {
        case '^':
          if (escape) {
            item.append(curr);
            escape = false;
          } else {
            int len = item.length();
            if (doTrim)
              item.replace(0, len, item.toString().trim());
            if (uppercase)
              item.replace(0, len, item.toString().toUpperCase());
            array.add(item.toString());
            item.replace(0, item.length(), "");
          }
          break;
        case '~':
          if (escape) {
            char esc = '~';
            item.append(esc + esc);
          }
          escape = toggle(escape);
          break;
        default:
          if (escape) {
            item.append('~');
          }
          item.append(curr);
          escape = false;
      }

    }

    if (escape)
      warningMsg = msg1 + input + msg2;
    int len = item.length();
    if (doTrim)
      item.replace(0, len, item.toString().trim());
    if (uppercase)
      item.replace(0, len, item.toString().toUpperCase());
    array.add(item.toString());

    return array;
  }

  /**
   * @param strKey
   * @param strData
   * @return
   */
  public static boolean storeData(String strKey, String strData) {
    boolean stat = false;
    try {
      strKey = strKey.trim().toUpperCase();
      Controller.STOREHASHMAP.put(strKey, strData);
      stat = true;
    } catch (Exception e) {
      LOGGER.error("Failed to store the data:'" + strData + "' in the key " + strKey
          + " , an exception" + " occurred . Exception:" + e);
      stat = false;
    }
    return stat;
  }

  /**
   * @param LocatorType
   * @return
   */
  public static boolean isLocatorTypeSupported(String LocatorType) {
    boolean support = false;
    String[] Identifiers = ObjectPreference.getSupportedIdentifiers();
    for (int i = 0; i < Identifiers.length; i++) {
      if (StringUtil.stringEqual(Identifiers[i], LocatorType, true, false)) {
        support = true;
        break;
      }
    }
    return support;
  }

  /**
   * @param ex
   * @return
   */
  public static String getStackTrace(Exception ex) {
    StackTraceElement[] sEle = ex.getStackTrace();
    String stackString = "";
    for (int i = 0; i < sEle.length; i++) {
      stackString = stackString + sEle[i] + "<br/>";
    }
    return stackString;
  }

  /**
   * @param data
   * @return
   */
  public static String convertToArrayString(String[] data) {
    StringBuilder items = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      items.append(data[i]);
      if (data.length != i + 1) {
        items.append('^');
      }
    }
    return items.toString();
  }
}

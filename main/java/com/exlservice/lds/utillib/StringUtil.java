package com.exlservice.lds.utillib;

/**
 * @author shamshersingh
 *
 */
public class StringUtil {
  /**
   * 
   */
  public static String STRMSG;

  /**
   * @param str1
   * @param str2
   * @param bTrim
   * @param bCase
   * @return
   */
  public static boolean stringEqual(String str1, String str2, boolean bTrim, boolean bCase) {
    STRMSG = "";
    boolean equal = false;
    if (bTrim) {
      str1 = str1.trim();
      str2 = str2.trim();
    }
    if (!bCase) {
      str1 = str1.toUpperCase();
      str2 = str2.toUpperCase();
    }
    if (str1.equals(str2))
      equal = true;
    else {
      equal = false;
    }
    return equal;
  }

  /**
   * @param str1
   * @param str2
   * @param bTrim
   * @param bCase
   * @param bRemoveNewLine
   * @return
   */
  public static boolean stringEqual(String str1, String str2, boolean bTrim, boolean bCase,
      boolean bRemoveNewLine) {
    STRMSG = "";
    boolean equal = false;
    if (bTrim) {
      str1 = str1.trim();
      str2 = str2.trim();
    }
    if (!bCase) {
      str1 = str1.toUpperCase();
      str2 = str2.toUpperCase();
    }

    if (bRemoveNewLine) {
      str1 = str1.replace("\n", " ");
      str2 = str2.replace("\n", " ");
    }

    if (str1.equals(str2))
      equal = true;
    else {
      equal = false;
    }
    return equal;
  }

  /**
   * @param str1
   * @param str2
   * @param bTrim
   * @param bCase
   * @return
   */
  public static boolean stringContains(String str1, String str2, boolean bTrim, boolean bCase) {
    boolean equal = false;
    if (bTrim) {
      str1 = str1.trim();
      str2 = str2.trim();
    }
    if (!bCase) {
      str1 = str1.toUpperCase();
      str2 = str2.toUpperCase();
    }
    if (str1.contains(str2))
      equal = true;
    else {
      equal = false;
    }
    return equal;
  }

  /**
   * @param input
   * @return
   */
  public static String trim(String input) {
    STRMSG = "";
    int i = 0;
    if (input.equals(""))
      return input;
    int inputLen = input.length();
    while ((i < inputLen) && ((input.charAt(i) == '�') || (input.charAt(i) == ' '))) {
      i++;
    }
    String result = input.substring(i);
    i = result.length() - 1;
    while ((i > -1) && ((result.charAt(i) == '�') || (result.charAt(i) == ' '))) {
      i--;
    }
    result = result.substring(0, i + 1);
    return result;
  }
}

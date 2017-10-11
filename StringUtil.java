package com.infinityfw.utillib;

public class StringUtil
{
  public static String STRMSG;

  public static boolean stringEqual(String str1, String str2, boolean bTrim, boolean bCase)
  {
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

  public static boolean stringEqual(String str1, String str2, boolean bTrim, boolean bCase, boolean bRemoveNewLine) {
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

  public static String trim(String input)
  {
    STRMSG = "";
    int i = 0;
    if (input.equals(""))
      return input;
    int inputLen = input.length();
    while ((i < inputLen) && ((input.charAt(i) == 'Â') || (input.charAt(i) == ' ')))
    {
      i++;
    }
    String result = input.substring(i);
    i = result.length() - 1;
    while ((i > -1) && ((result.charAt(i) == 'Â') || (result.charAt(i) == ' ')))
    {
      i--;
    }
    result = result.substring(0, i + 1);
    return result;
  }
}
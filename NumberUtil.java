package com.infinityfw.utillib;

public class NumberUtil
{
  public static boolean isPostiveInteger(String str)
  {
    boolean stat = false;
    try {
      int x = Integer.parseInt(str.trim());
      if (x > 0)
        stat = true;
    }
    catch (NumberFormatException nfe) {
      stat = false;
    }
    return stat;
  }

  public static boolean isInteger(String str) {
    boolean stat = false;
    try
    {
      Integer.parseInt(str.trim());
      stat = true;
    }
    catch (NumberFormatException nfe) {
      stat = false;
    }
    return stat;
  }

  public static boolean isWholeNumber(String str) {
    boolean stat = false;
    try {
      int x = Integer.parseInt(str.trim());
      if (x > -1)
        stat = true;
    }
    catch (NumberFormatException nfe) {
      stat = false;
    }
    return stat;
  }
}

/**
 * 
 */
package com.exlservice.lds.utillib;

import java.util.Date;
import org.apache.log4j.FileAppender;

/**
 * @author shamshersingh
 *
 */
public class CustomFileAppender extends FileAppender {

  public CustomFileAppender() {}

  @Override
  public void setFile(String file) {
    super.setFile(prependDate(file));
  }

  private static String prependDate(String filename) {
    Date d = new Date();
    String date = d.toString().replaceAll(" ", "_");
    date = date.replaceAll(":", "_");
    date = date.replaceAll("\\+", "_");
    return filename + "_" + date + ".log";
  }
}

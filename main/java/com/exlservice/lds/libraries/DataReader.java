package com.exlservice.lds.libraries;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Shamsher This class is a utility class for reading a Properties File
 */
public class DataReader {
  FileInputStream proFileName;
  File sTempFile;
  String sValue, absPath;
  File fValue;
  Properties objproperties = new Properties();

  /**
   * This method is used to upload a properties file and basing on key value it returns a value from
   * a properties file.
   * 
   * @param sKey
   * @return string
   * @throws IOException
   */
  public String getProperty(String sKey) throws IOException {
    sTempFile = new File("./config/INFINITY.properties");
    proFileName = new FileInputStream(sTempFile.getAbsolutePath());
    objproperties.load(proFileName);
    sValue = objproperties.getProperty(sKey);
    return sValue;
  }

  public String getPath(String sPath) {
    // Path inputPath = Paths.get(args[0]);
    Path inputPath = Paths.get(sPath);
    Path fullPath = inputPath.toAbsolutePath();
    absPath = fullPath.toString();
    return absPath;
  }
}


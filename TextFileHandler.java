package com.infinityfw.libraries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TextFileHandler extends FileHandler
{
  public static String delimiter;

  public boolean writeFile(ResultSet results, String filePath)
  {
    boolean isFileWritten = false;
    Writer writer = null;

    if (FileHandler.fileCreated != null) {
      try {
        writer = new BufferedWriter(new FileWriter(filePath));

        for (int i = 1; i <= results.getMetaData().getColumnCount(); i++) {
          writer.write(results.getMetaData().getColumnName(i) + "\t");
        }
        writer.write("\n");

        while (results.next()) {
          for (int i = 1; i <= results.getMetaData().getColumnCount(); i++) {
            writer.write(String.valueOf(results.getObject(i)) + 
              "\t");
          }
          writer.write("\n");
        }

        writer.close();
        isFileWritten = true;
        return isFileWritten;
      }
      catch (Exception e) {
        isFileWritten = false;
        return isFileWritten;
      }
    }

    return false;
  }

  public ArrayList<String[]> convertToTableData(String file)
  {
    ArrayList<String[]> result = null;
    try
    {
      FileInputStream fstream = new FileInputStream(file);
      DataInputStream in = new DataInputStream(fstream);
      @SuppressWarnings("resource")
	BufferedReader br = new BufferedReader(new InputStreamReader(in));

      result = new ArrayList<String[]>();
      while ((br.readLine()) != null)
      {
        String strLine1 = null;
        @SuppressWarnings("null")
		String[] lines = strLine1.split("\t");
        result.add(lines);
      }
    }
    catch (Exception e) {
      result = null;
    }
    return result;
  }

  public boolean writeTableData(ArrayList<String[]> results, String file) {
    Writer writer = null;
    boolean status = false;
    try {
      writer = new BufferedWriter(new FileWriter(file));

      for (String[] result : results) {
        for (int i = 0; i < result.length; i++) {
          writer.write(result[i] + "\t");
        }

        writer.write("\n");
      }

      writer.close();
      status = true;
    }
    catch (Exception e) {
      status = false;
    }
    return status;
  }

  public void convertToXMLData()
  {
  }
}

package com.test;

import java.util.HashMap;

public class ObjectPreference {
  private static String[] PREFERENCES;

  public static String getXPATH(HashMap<String, String> objProperties) {
    String xpath = null;
    for (String key : objProperties.keySet()) {
      if (key.toString().trim().equalsIgnoreCase("XPATH")) {
        xpath = ((String) objProperties.get(key)).toString();
        return xpath;
      }
    }
    DataLogger.writeToDebugAndErrorLogs(
        "The object does not have an XPATH , please check the object properties.");
    return xpath;
  }

  public static String getPreferredLocator(HashMap<String, String> objProperties) {
    String locator = null;
    try {
      String[] Identifiers = getPreferences();
      String identifierType;
      for (int i = 0; i < Identifiers.length; i++) {
        identifierType = Identifiers[i];
        for (String key : objProperties.keySet()) {
          if (key.toString().trim().equalsIgnoreCase(identifierType)) {
            locator = ((String) objProperties.get(key)).toString().trim();
            return locator;
          }
        }

      }

      if (!objProperties.isEmpty()) {
        identifierType = objProperties.keySet().iterator();
        if (identifierType.hasNext()) {
          String key = (String) identifierType.next();
          locator = ((String) objProperties.get(key)).toString().trim();
        }
      } else {
        DataLogger.writeToDebugLog(
            "The object property is null , please check the properties of the object passed.");
        DataLogger.writeToInfoLog("The object does not have a property, please check.");
      }
    } catch (Exception e) {
      locator = null;
      DataLogger.writeToDebugLog(
          "Exception occurred while retrieving the object properties , Exception:" + e);
      DataLogger.writeToInfoLog("Failed to retrieve object properties.");
    }
    return locator;
  }

  protected static String[] getPreferences() {
    return PREFERENCES;
  }

  protected static void setPreferences() {
    PREFERENCES = new String[3];

    PREFERENCES[0] = "ID".toUpperCase();
    PREFERENCES[1] = "NAME".toUpperCase();
    PREFERENCES[2] = "XPATH".toUpperCase();
  }

  public static String[] getSupportedIdentifiers() {
    String[] supIden = new String[6];
    supIden[0] = "XPATH";
    supIden[1] = "ID";
    supIden[2] = "NAME";
    supIden[3] = "DOM";
    supIden[4] = "LINK";
    supIden[5] = "CSS";
    return supIden;
  }
}

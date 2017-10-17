package com.test;

import java.util.HashMap;
import java.util.Iterator;
import com.exlservice.lds.utillib.FwUtil;
import com.exlservice.lds.utillib.StringUtil;

public class ObjectInfo implements Cloneable {
  private String objectID = "0";

  private String objectParentID = "0";

  private String strObjectName = null;

  private String strObjectClass = null;

  private String QClass = null;

  private HashMap<String, String> objectLocators = null;

  private HashMap<String, String> objectLocatorsUpdated = null;

  private String strObjDescription = "";

  public String ObjectMsg = "";

  public void setObjectID(String objID) {
    this.objectID = objID;
  }

  public String getObjectID() {
    return this.objectID;
  }

  public void setParentID(String objID) {
    this.objectParentID = objID;
  }

  public String getParentID() {
    return this.objectParentID;
  }

  public String getObjectName() {
    return this.strObjectName;
  }

  public void setObjectName(String objName) {
    this.strObjectName = objName;
  }

  public void setObjectClass(String objClass) {
    this.strObjectClass = objClass;
  }

  public String getObjectClass() {
    return this.strObjectClass;
  }

  public void setObjectDescription(String objDescription) {
    this.strObjDescription = objDescription;
  }

  public String getObjectDescription() {
    return this.strObjDescription;
  }

  public void setLocators(HashMap<String, String> objProperties) {
    this.objectLocators = objProperties;
  }

  public HashMap<String, String> getLocators() {
    return this.objectLocators;
  }

  public String getUpdatedLocators() {
    this.ObjectMsg = "";

    String locValue = null;
    HashMap locators = new HashMap();
    String locatorType = getLocatorType();
    if (locatorType == null) {
      this.ObjectMsg = "The object does not have a LocatorType";
      return locValue;
    }
    if (!FwUtil.isLocatorTypeSupported(locatorType)) {
      this.ObjectMsg =
          ("The '" + locatorType + "' " + "LocatorType" + " is not a supported Identifier.");
      locValue = null;
      return locValue;
    }

    locators = this.objectLocatorsUpdated;
    for (Iterator localIterator = locators.keySet().iterator(); localIterator.hasNext();) {
      Object o = localIterator.next();
      if (!o.toString().trim().equalsIgnoreCase("LocatorValue")) {
        continue;
      }

      locValue = (String) locators.get(o);

      return locValue;
    }

    return locValue;
  }

  public void setUpdatedLocators(HashMap<String, String> objProperties) {
    this.objectLocatorsUpdated = objProperties;
  }

  public String getLocator() {
    this.ObjectMsg = "";

    String locValue = null;
    HashMap locators = new HashMap();
    String locatorType = getLocatorType();
    if (locatorType == null) {
      this.ObjectMsg = "The object does not have a LocatorType";
      return locValue;
    }
    if (!FwUtil.isLocatorTypeSupported(locatorType)) {
      this.ObjectMsg =
          ("The '" + locatorType + "' " + "LocatorType" + " is not a supported Identifier.");
      locValue = null;
      return locValue;
    }

    locators = this.objectLocators;
    for (Iterator localIterator = locators.keySet().iterator(); localIterator.hasNext();) {
      Object o = localIterator.next();
      if (o.toString().trim().equalsIgnoreCase("LocatorValue")) {
        if (StringUtil.stringEqual(locatorType, "LINK", true, false)) {
          locValue = "LINK".trim();
          locValue = locValue + "=" + ((String) locators.get(o)).toString();
        } else {
          locValue = (String) locators.get(o);
        }
        return locValue;
      }
    }
    return locValue;
  }

  public String getLocator(String locatorType) {
    String locValue = null;
    HashMap locators = new HashMap();
    this.ObjectMsg = "";

    if (!FwUtil.isLocatorTypeSupported(locatorType)) {
      this.ObjectMsg =
          ("The '" + locatorType + "' " + "LocatorType" + " is not a supported Identifier.");
      locValue = null;
      return locValue;
    }

    locators = this.objectLocatorsUpdated;

    for (Iterator localIterator1 = locators.keySet().iterator(); localIterator1.hasNext();) {
      Object o = localIterator1.next();
      if (o.toString().trim().equalsIgnoreCase("LocatorType")) {
        String locType = (String) locators.get(o);
        if (locType.trim().equalsIgnoreCase(locatorType.trim())) {
          for (Iterator localIterator2 = locators.keySet().iterator(); localIterator2.hasNext();) {
            Object keyValue = localIterator2.next();
            if (keyValue.toString().trim().equalsIgnoreCase("LocatorValue")) {
              if (StringUtil.stringEqual(locatorType, "LINK", true, false)) {
                locValue = "LINK".trim();
                locValue = locValue + "=" + ((String) locators.get(keyValue)).toString();
              } else {
                locValue = (String) locators.get(keyValue);
              }
              return locValue;
            }
          }
        }
      }

    }

    for (Iterator localIterator1 = locators.keySet().iterator(); localIterator1.hasNext();) {
      Object o = localIterator1.next();
      if (StringUtil.stringEqual(o.toString(), locatorType, true, false)) {
        locValue = (String) locators.get(o);
        return locValue;
      }
    }

    return locValue;
  }

  public String getLocatorType() {
    String locType = null;
    HashMap locators = new HashMap();
    locators = this.objectLocatorsUpdated;
    for (Iterator localIterator = locators.keySet().iterator(); localIterator.hasNext();) {
      Object o = localIterator.next();
      if (o.toString().trim().equalsIgnoreCase("LocatorType")) {
        locType = (String) locators.get(o);
        return locType;
      }
    }
    return locType;
  }

  public String hashcode() {
    return this.objectID;
  }

  public String getQClass() {
    return this.QClass;
  }

  public void setQClass(String strQClass) {
    this.QClass = strQClass;
  }

  public ObjectInfo clone() {
    ObjectInfo info = new ObjectInfo();
    info.setObjectID(this.objectID);
    info.setObjectDescription(this.strObjDescription);
    info.setObjectName(this.strObjectName);
    info.setObjectClass(this.strObjectClass);
    info.setParentID(this.objectParentID);
    info.setQClass(getQClass());

    HashMap properties = new HashMap();
    properties = this.objectLocators;
    info.setLocators(properties);

    return info;
  }
}

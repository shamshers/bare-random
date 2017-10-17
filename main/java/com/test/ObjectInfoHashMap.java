package com.test;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.exlservice.lds.libraries.ConfigFileReader;
import com.exlservice.lds.libraries.ConstVariables;
import com.exlservice.lds.libraries.DynamicQuery;
import com.exlservice.lds.utillib.XMLUtil;

public class ObjectInfoHashMap {
  private static String strQuery = "";

  private Map<String, ObjectInfo> hmObjInfo = null;

  private Map<String, String> typeID_QClassMap = null;

  private static final Logger logger = Logger.getLogger(ConfigFileReader.class);

  public ObjectInfoHashMap(boolean xml) throws Exception {
    if (xml) {
      logger.trace(DynamicQuery.CONFIG_XMLPATH + "\\Map.XML");
      XMLUtil xmlUtil = new XMLUtil(DynamicQuery.CONFIG_XMLPATH + "\\Map.XML");
      logger.trace("After xmlUtil object");
      NodeList objectList = xmlUtil.queryXML("//Object[@Tool='WebDriver']");
      ObjectInfo currObjInfo = null;
      this.hmObjInfo = new HashMap();

      for (int i = 0; i < objectList.getLength(); i++) {
        currObjInfo = new ObjectInfo();
        Element object = (Element) objectList.item(i);
        String strObjID = object.getAttribute("ObjectId");
        if (!strObjID.equals(""))
          currObjInfo.setObjectID(strObjID);
        else {
          currObjInfo.setObjectID("-1");
        }
        currObjInfo.setObjectClass(object.getAttribute("ObjectClass"));
        currObjInfo.setQClass(object.getAttribute("QClass"));
        currObjInfo.setObjectName(object.getAttribute("ObjectName"));
        String parentID = object.getAttribute("ParentId");
        if (parentID != "")
          currObjInfo.setParentID(parentID);

        NodeList propertyList = object.getElementsByTagName("Property");
        HashMap properties = new HashMap();
        for (int j = 0; j < propertyList.getLength(); j++) {
          Element propertyEle = (Element) propertyList.item(j);
          String propVal = propertyEle.getTextContent();

          String propName = propertyEle.getAttribute("Name").trim().toUpperCase();
          properties.put(propName, propVal);
        }
        currObjInfo.setLocators(properties);
        this.hmObjInfo.put(object.getAttribute("ObjectId"), currObjInfo);
      }

    } else {
      DBConnection db = new DBConnection();
      strQuery = ConstVariables.QrySelectObjectInfo;

      ResultSet rs = db.executeQuery(strQuery);
      this.hmObjInfo = new HashMap();

      ObjectInfo currObjInfo = null;
      HashMap arrProperty = null;
      String intOldObjID = "0";
      String intObjID = "0";

      while (rs.next()) {
        intObjID = rs.getString("ObjectID");

        if (currObjInfo == null) {
          currObjInfo = new ObjectInfo();
          arrProperty = new HashMap();
        } else if ((!this.hmObjInfo.containsKey(intObjID)) && (!intOldObjID.equals(intObjID))) {
          currObjInfo.setLocators(arrProperty);
          this.hmObjInfo.put(intOldObjID, currObjInfo);
          currObjInfo = new ObjectInfo();
          arrProperty = new HashMap();
        }
        currObjInfo.setObjectID(intObjID);
        currObjInfo.setObjectName(rs.getString("ObjectName"));
        currObjInfo.setParentID(rs.getString("ParentID"));
        currObjInfo.setObjectClass(rs.getString("ObjectClass"));
        currObjInfo.setQClass(rs.getString("QClass"));
        currObjInfo.setObjectDescription(rs.getString("ObjectDescription"));

        String strPropertyName = rs.getString("PropertyName");
        String strPropertyValue = rs.getString("PropertyValue");
        arrProperty.put(strPropertyName, strPropertyValue);
        intOldObjID = intObjID;
      }
      if (currObjInfo != null) {
        currObjInfo.setLocators(arrProperty);
        this.hmObjInfo.put(intOldObjID, currObjInfo);
      }

      ResultSet rs1 =
          db.executeQuery("select TypeId, ObjectClass from objecttype_master where Tool = 'SEL'");
      this.typeID_QClassMap = new HashMap();
      while (rs1.next()) {
        this.typeID_QClassMap.put(rs1.getString("TypeId"), rs1.getString("ObjectClass"));
      }
    }
  }

  public ObjectInfo getObjectProperties(String objid) {
    ObjectInfo obj = new ObjectInfo();
    obj = (ObjectInfo) this.hmObjInfo.get(objid);
    return obj;
  }

  public Map<String, ObjectInfo> getObjectInfoMapContents() {
    return this.hmObjInfo;
  }

  public String getQualitiaClassFromTypeId(String typeID) {
    return (String) this.typeID_QClassMap.get(typeID);
  }
}

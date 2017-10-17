package com.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.exlservice.lds.libraries.DynamicQuery;
import com.exlservice.lds.utillib.XMLUtil;

public class KeywordParameterInfoHashMap {
  private Map<String, KeywordParameterInfo> hmKeywordParameterInfo = new HashMap();
  private Map<String, String> hmKeywordInfo = new HashMap();
  private Map<String, String> hmKeywordName = new HashMap();

  public KeywordParameterInfoHashMap()
      throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
    XMLUtil xmlUtil = new XMLUtil(DynamicQuery.CONFIG_XMLPATH + "\\Map.XML");
    NodeList keywordList = xmlUtil.queryXML("//Action[@Tool='WebDriver']");

    for (int i = 0; i < keywordList.getLength(); i++) {
      Element keywordEle = (Element) keywordList.item(i);
      String keywordID = keywordEle.getAttribute("Id");
      this.hmKeywordInfo.put(keywordID, keywordEle.getAttribute("Function"));
      this.hmKeywordName.put(keywordID, keywordEle.getAttribute("Name"));
      NodeList argList = keywordEle.getElementsByTagName("Arg");
      int argSeq = 0;
      for (int j = 0; j < argList.getLength(); j++) {
        Element argEle = (Element) argList.item(j);
        argSeq++;
        KeywordParameterInfo kpi = new KeywordParameterInfo();
        kpi.setKeywordID(keywordID);
        kpi.setDataType(argEle.getAttribute("DataType"));
        kpi.setArgSeq(argSeq);
        kpi.setIsMandatory(argEle.getAttribute("Mandatory"));
        this.hmKeywordParameterInfo.put(keywordID + "_" + argSeq, kpi);
      }
    }
  }

  public boolean isKeywordParameterised(String keywordID) {
    boolean parameterised = false;
    try {
      parameterised = this.hmKeywordParameterInfo.containsKey(keywordID + "_1");
    } catch (Exception e) {
      // DataLogger.writeToErrorLog(e.toString());
    }
    return parameterised;
  }

  public String getParameterDataType(String key) {
    KeywordParameterInfo kpi = (KeywordParameterInfo) this.hmKeywordParameterInfo.get(key);
    String dataType = null;
    if (kpi != null) {
      dataType = kpi.getDataType();
    }
    return dataType;
  }

  public boolean parameterMapContainsKey(String key) {
    return this.hmKeywordParameterInfo.containsKey(key);
  }

  public boolean parameterIsMandatory(String key) {
    boolean isMandatory = false;
    KeywordParameterInfo kpi = (KeywordParameterInfo) this.hmKeywordParameterInfo.get(key);
    if (kpi != null) {
      String mandatory = kpi.getIsMandatory();
      if (mandatory.equalsIgnoreCase("true"))
        isMandatory = true;
    }
    return isMandatory;
  }

  public String getKeywordFunctionFromID(String KeywordID) {
    return (String) this.hmKeywordInfo.get(KeywordID);
  }

  public String getKeywordFunctionNameFromID(String KeywordID) {
    return (String) this.hmKeywordName.get(KeywordID);
  }
}

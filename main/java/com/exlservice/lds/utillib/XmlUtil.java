package com.exlservice.lds.utillib;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author shamshersingh
 *
 */
public class XmlUtil {
  Document doc;
  private static DocumentBuilderFactory dbf;
  private static DocumentBuilder db;
  private static XPathFactory xpathFactory;
  private static XPath xpath;

  /**
   * @param fileName
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public XmlUtil(String fileName) throws ParserConfigurationException, SAXException, IOException {
    if (db == null) {
      if (dbf == null) {
        dbf = DocumentBuilderFactory.newInstance();
      }
      db = dbf.newDocumentBuilder();
    }
    this.doc = db.parse(new File(fileName));
  }

  /**
   * @param fileName
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public void setDocument(String fileName)
      throws ParserConfigurationException, SAXException, IOException {
    if (db == null) {
      if (dbf == null)
        dbf = DocumentBuilderFactory.newInstance();
      db = dbf.newDocumentBuilder();
    }
    this.doc = db.parse(new File(fileName));
  }

  /**
   * @param query
   * @return
   * @throws XPathExpressionException
   */
  public NodeList queryXML(String query) throws XPathExpressionException {
    if (xpath == null) {
      if (xpathFactory == null)
        xpathFactory = XPathFactory.newInstance();
      xpath = xpathFactory.newXPath();
    }
    XPathExpression expr = xpath.compile(query);
    NodeList nodeList = (NodeList) expr.evaluate(this.doc, XPathConstants.NODESET);
    return nodeList;
  }

  /**
   * @param query
   * @param e
   * @return
   * @throws XPathExpressionException
   */
  public NodeList queryXML1(String query, Element e) throws XPathExpressionException {
    if (xpath == null) {
      if (xpathFactory == null)
        xpathFactory = XPathFactory.newInstance();
      xpath = xpathFactory.newXPath();
    }
    XPathExpression expr = xpath.compile(query);
    NodeList nodeList = (NodeList) expr.evaluate(e, XPathConstants.NODESET);
    return nodeList;
  }

  /**
   * @param ele
   * @param attr
   * @return
   */
  public String getAttribute(Element ele, String attr) {
    String value = null;

    if (ele.hasAttribute(attr)) {
      value = ele.getAttribute(attr).toString().trim();
    }

    return value;
  }
}

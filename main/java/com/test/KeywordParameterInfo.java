package com.test;

public class KeywordParameterInfo {
  private String keywordID;
  private String dataType;
  private int argSeq;
  private String mandatory;

  public String getKeywordID() {
    return this.keywordID;
  }

  public void setKeywordID(String keywordID) {
    this.keywordID = keywordID;
  }

  public int getArgSeq() {
    return this.argSeq;
  }

  public void setArgSeq(int argSeq) {
    this.argSeq = argSeq;
  }

  public String getIsMandatory() {
    return this.mandatory;
  }

  public void setIsMandatory(String isMandatory) {
    this.mandatory = isMandatory;
  }

  public String getDataType() {
    return this.dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }
}

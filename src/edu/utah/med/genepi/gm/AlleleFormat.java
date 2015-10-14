//******************************************************************************
// AlleleFormat.java
//******************************************************************************
package edu.utah.med.genepi.gm;

//==============================================================================
public interface AlleleFormat {
  public byte convertAllele(String inString);
  public String toString(byte b);
  public void setMissingData(String inString);
  public void setMissingData(byte inbyte);
  public byte getMissingData();
}

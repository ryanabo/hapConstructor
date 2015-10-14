//******************************************************************************
// GDef.java
//******************************************************************************
package edu.utah.med.genepi.gm;

//==============================================================================
public interface GDef {
  public int getLocusCount();
  public Locus getLocus(int index);
  public GtypeBuilder getGtypeBuilder();
  public void setAlleleFormat(AlleleFormat af);
  public AlleleFormat getAlleleFormat();

  public interface Locus {
    public int getID();
    public void setMarker(String inMarker);
    public String getMarker();
    public double getTheta();
    public boolean alleleOrderIsSignificant();
  }
}


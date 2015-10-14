//******************************************************************************
// LocusImp.java
//******************************************************************************
package edu.utah.med.genepi.gm;

//==============================================================================
public class LocusImp implements GDef.Locus {

  private final int     myID;
  public String  myMarker;
  private final double  myTheta;
  private final boolean allelesOrdered;
  private final int     geneID;
  //private Frequency[] frequency;

  //----------------------------------------------------------------------------
  public LocusImp (int id,
                   String marker,
                   double distance,
                   boolean alleles_ordered,
                   int gene_id )
  {
    myID = id;
    //if ( marker == null )
    //  myMarker = String.valueOf(id);
    //else myMarker = marker;
    myMarker = marker;
    myTheta = distance > 0.5 ? cM2Theta(distance) : distance;
    allelesOrdered = alleles_ordered;
    geneID = gene_id;
    //frequency = null;
  }

  /// GDef.Locus implementation ///

  //----------------------------------------------------------------------------
  public int getID() { return myID; }

  //----------------------------------------------------------------------------
  public void setMarker(String inMarker)
  {
    myMarker = inMarker;
  }

  //----------------------------------------------------------------------------
  public String getMarker() { return myMarker; }

  //----------------------------------------------------------------------------
  public double getTheta() { return myTheta; }

  //----------------------------------------------------------------------------
  public boolean alleleOrderIsSignificant() { return allelesOrdered; }

  /// Internals ///

  //----------------------------------------------------------------------------
  public int getGeneID()
  {
    return geneID;
  }

  //----------------------------------------------------------------------------
  //public void addFrequency(Frequency[] freq)
  //{
  //  frequency = freq;
  //}

  //----------------------------------------------------------------------------
  //public Frequency[] getFrequency()
  //{
  //  return frequency;
  //}
  //----------------------------------------------------------------------------
  private static double cM2Theta(double x)
  {
    return (Math.exp(-x / 50.0) - 1.0) / -2.0;
  }
}


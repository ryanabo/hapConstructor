//******************************************************************************
// GtypeBuilder.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class GtypeBuilder {

  private final List<AllelePair> lPairs = new ArrayList<AllelePair>();
  private final GDef gDef;
  private int        validPairCount;
  private double haploFreq = 0.0;
  private byte missingData;

  //----------------------------------------------------------------------------
  GtypeBuilder(GDef gd)
  {
    gDef = gd;
  }

  //----------------------------------------------------------------------------
  //public void addAllelePair(int a1, int a2)
  public void addAllelePair(String a1, String a2)
  {
    AllelePair ap = null;

    byte a1Code = Byte.parseByte(a1);
    byte a2Code = Byte.parseByte(a2);
    //if (a1Code > 0 && a2Code > 0)
    if (a1Code != missingData && a2Code != missingData )
    {
      GDef.Locus loc = gDef.getLocus(lPairs.size());
      ap = new AllelePair(
        a1Code, a2Code, loc.getTheta(), loc.alleleOrderIsSignificant()
      );
      validPairCount++;
    }

    lPairs.add(ap);
  }

  //----------------------------------------------------------------------------
  public void addAllelePair(byte a1, byte a2)
  {
    AllelePair ap = null;
    missingData = gDef.getAlleleFormat().getMissingData();
    
    //int a1 = Integer.parseInt(allele1);
    //int a2 = Integer.parseInt(allele2);

    if (a1 != missingData && a2 != missingData)
    {
      GDef.Locus loc = gDef.getLocus(lPairs.size());
      ap = new AllelePair(
        a1, a2, loc.getTheta(), loc.alleleOrderIsSignificant()
      );
      validPairCount++;
    }

    lPairs.add(ap);
  }

  //----------------------------------------------------------------------------
  public void addHaploFrequency(double freq)
  {
    haploFreq = freq;
  }

  //----------------------------------------------------------------------------
  public Gtype buildNext() throws GEException
  {
    Gtype gt = null;

    if (lPairs.size() != gDef.getLocusCount())
      throw new GEException(
        "invalid genotype (" + lPairs.size() + " of " + gDef.getLocusCount()
        + " allele pairs specified)"
      );

    if (validPairCount > 0)
    {
      gt = new Gtype((AllelePair[]) lPairs.toArray(new AllelePair[0]));
      gt.setHaploFrequency(haploFreq);
    }

    lPairs.clear();
    validPairCount = 0;

    return gt;
  }
  //----------------------------------------------------------------------------
  public void buildclean() 
  {
    lPairs.clear();
    validPairCount = 0;
  }

}

//******************************************************************************
// NumAlleleLocusExt.java
//******************************************************************************
package edu.utah.med.genepi.gchapext;

import alun.genio.LinkagePhenotype;
import alun.genio.NumberedAlleleLocus;
import alun.genio.NumberedAllelePhenotype;
//==============================================================================
public class NumAlleleLocusExt extends NumberedAlleleLocus
{
  public NumAlleleLocusExt()
  {
    super();
  }

  //----------------------------------------------------------------------------
  // Reads the phenotypic data in the format associated with this
  // type of locus from the given input formatter.
  public LinkagePhenotype readPhenotype(int a, int b)
  {
    if (a < 0 || a > freq.length)
    {
      System.out.println("allele code "+a+" is out of range "+0+" "+(freq.length)+"\n\tSetting to 0");
      a = 0;
    }
    if (b < 0 || b > freq.length)
    {
      System.out.println("allele code "+b+" is out of range "+0+" "+(freq.length)+"\n\tSetting to 0");
      b = 0;
    }
    return new NumberedAllelePhenotype(this,a,b);
  }
  
}

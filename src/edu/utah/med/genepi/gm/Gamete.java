//******************************************************************************
// Gamete.java
//******************************************************************************
package edu.utah.med.genepi.gm;

//==============================================================================
public class Gamete 
{
  //private final int[] myAlleles;
  private final byte[] myAlleles;

  //----------------------------------------------------------------------------
  //public Gamete(int[] alleles)
  public Gamete(byte[] alleles)
  {
    myAlleles = alleles;
  }

  //----------------------------------------------------------------------------
  //public int getAllele(int ilocus) { return myAlleles[ilocus]; }
  public byte getAllele(int ilocus) { return myAlleles[ilocus]; }
}


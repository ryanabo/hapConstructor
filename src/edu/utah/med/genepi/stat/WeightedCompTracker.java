//******************************************************************************
// WeightedCompTracker.java
//******************************************************************************
package edu.utah.med.genepi.stat;


//==============================================================================
class WeightedCompTracker extends ScalarCompTracker 
{
  private ChiSquareDist csd;
  private int degreeF;

  //----------------------------------------------------------------------------
  WeightedCompTracker(int df) 
  { 
    super(); 
    degreeF = df;
    //System.out.println("created weightedCompTracker with : " + df + " freedom");
  }

  //----------------------------------------------------------------------------
  public CCStat.Result getComparisonsResult()
  {
    if ( Double.isNaN(r0Val) )
      return new ResultImp.StringResult("-");
    else
    {
      csd = new ChiSquareDist(degreeF);
      double csValue = csd.barF(r0Val);
      //System.out.println("df : "+  degreeF + " obs " +  r0Val + " result : " + csValue);
      return new ResultImp.Real(csValue);
    }
  }

  //----------------------------------------------------------------------------
  //public void setDegreeOfFreedom(int df)
  //{ 
  //  degreeF = df;
  //}

  //----------------------------------------------------------------------------
  public int getDegreeOfFreedom()
  {
    return degreeF;
  }
}

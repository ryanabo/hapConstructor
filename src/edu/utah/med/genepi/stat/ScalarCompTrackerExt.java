//******************************************************************************
// HWECompTracker.java
//******************************************************************************
package edu.utah.med.genepi.stat;


//==============================================================================
// Need rewrite to extends from ScalarCompTracker 
class ScalarCompTrackerExt implements CCStat.ComparisonTracker 
{
  public int[]     notval;
  private int[]    hitCounts;
  public double[]  r0Vals;
  public int[]     comparisonCount;
  //public Vector<Vector<Double>> vRatio;
  public String    messages = null;
  public  CCStat.Result confIntervals = null;

  //----------------------------------------------------------------------------
  //HWECompTracker() {}

  //----------------------------------------------------------------------------
  public void setStartingResult(CCStat.Result r0)
  {
    if ( !(r0 instanceof ResultImp.StringSeries) )
    {
      assert r0 instanceof ResultImp.RealSeries;
      r0Vals = r0.doubleValues();
      comparisonCount = new int[r0Vals.length];
      notval = new int[r0Vals.length];
      hitCounts = new int[r0Vals.length];
      for ( int i = 0; i < r0Vals.length; i++ )
        comparisonCount[i] = notval[i] = hitCounts[i]= 0;
    }
  }

  //----------------------------------------------------------------------------
  public void compareResultAtX(CCStat.Result rx)
  {
    double[] rxVals = rx.doubleValues();
    for (int i = 0, n = rxVals.length; i < n; ++i)
    {
      if ( Double.isNaN(rxVals[i]) )
        notval[i]++;
      else
      {
        comparisonCount[i]++;
        if (Math.abs(rxVals[i]) >= Math.abs(r0Vals[i]))
        {
          hitCounts[i]++;
        }
      }
      //System.out.println("Ratio " + i + " sim result " + rxVals[i] + " hitcount : " + hitCounts[i]);
    }
  }

  //----------------------------------------------------------------------------
  public CCStat.Result getComparisonsResult()
  {
    double[] pvals = new double[hitCounts.length];

    for (int i = 0; i < pvals.length; ++i)
    {
      if ( Double.isNaN(r0Vals[i]))
        pvals[i] = r0Vals[i];
      else 
        pvals[i] =  hitCounts[i] /(double) comparisonCount[i];
    }
    return new ResultImp.RealSeries(pvals);
  }

  //----------------------------------------------------------------------------
  public int[] getComparisonCount()
  { return comparisonCount; }

  //----------------------------------------------------------------------------
  public int[] getnotval()
  { return notval; }

  //----------------------------------------------------------------------------
  public void setMessages(String inMessages)
  {
    messages = inMessages;
  }

  //----------------------------------------------------------------------------
  public String getMessages()
  { return messages; }

  public void setDegreeOfFreedom(int df)
  { }

  public int getDF()
  { return 4; }

}

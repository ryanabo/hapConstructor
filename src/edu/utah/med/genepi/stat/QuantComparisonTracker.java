//*****************************************************************************
// QuantComparisonTracker.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

//=============================================================================
public class QuantComparisonTracker implements CCStat.ComparisonTracker 
{
  public int[]    notval;
  private int[]   hitCounts;
  public double[] r0Vals;
  public int[]    comparisonCount;
  public String   messages = null;
  public int      degreeF;

  //-----------------------------------------------------------------------
  public void setStartingResult( CCStat.Result r0)
      {
        if ( !(r0 instanceof ResultImp.StringResult))
        {
          assert r0 instanceof CCStat.Result;
          int nResult = r0.elementCount();
          r0Vals = r0.doubleValues();
          hitCounts = new int[nResult];
          comparisonCount = new int[nResult];
          notval = new int[nResult];
          for ( int i =0; i < r0Vals.length; i++ )
            comparisonCount[i] = notval[i] = 0;
        }
      }

  //-----------------------------------------------------------------------
  public void compareResultAtX( CCStat.Result rx )
  {
    double[] rxVals = rx.doubleValues();

    for ( int i = 0; i < rxVals.length; i++ )
    {
      if ( Double.isNaN(rxVals[i]) || Double.isInfinite(rxVals[i]) )
        notval[i]++;
      else
      {  
        if ( r0Vals.length > 2 && i == ( rxVals.length - 1) )
        {
          if ( rxVals[i] > r0Vals[i] )
            hitCounts[i]++;
        }
        else
        {
          if ( r0Vals[i] > 0 )
          {
            if ( rxVals[i] > r0Vals[i] ||
               rxVals[i] < ( r0Vals[i] * -1 ) )
               hitCounts[i]++;
          }
          else if ( r0Vals[i] < 0 )
          {
            if ( rxVals[i] < r0Vals[i] ||
                 rxVals[i] > ( r0Vals[i] * -1 ) )
              hitCounts[i]++;
          }
        }
        comparisonCount[i]++;
      }
    }
  }

  //--------------------------------------------------------------------------
  public CCStat.Result getComparisonsResult()
      {
        //int resultsize = 1;
        //if ( hitCounts.length > 2 )
        //  resultsize = hitCounts.length - 1;
        double[] pvals = new double[hitCounts.length];

        for ( int i = 0; i < hitCounts.length; i++ )
        {
          if ( Double.isNaN(r0Vals[i]))
            pvals[i] = r0Vals[i];
          else if ( hitCounts.length > 2 && i == hitCounts.length - 1)
            pvals[i] = hitCounts[hitCounts.length - 1] /
                              (double) comparisonCount[i] ;
          else
            pvals[i] = hitCounts[i] / (double) comparisonCount[i];
        }

        return new ResultImp.RealSeries(pvals);
      }

  //------------------------------------------------------------------------
  public int[] getnotval()
  { return notval; }

  //------------------------------------------------------------------------
  public int[] getComparisonCount()
  {
    if ( comparisonCount.length > 2 )
    {
      int[] trueComparisonCount = new int[comparisonCount.length - 1];
      for ( int i = 0; i < comparisonCount.length - 1; i++ )
        trueComparisonCount[i] = comparisonCount[i];
      return trueComparisonCount; 
    }
    else 
      return comparisonCount;
  } 

  //------------------------------------------------------------------------
  public void setMessages( String inMessages )
  {
    messages = inMessages;
  }

  //------------------------------------------------------------------------
  public String getMessages()
  { return messages; }

  //------------------------------------------------------------------------
  public void setDegreeOfFreedom(int df)
  { 
    degreeF = df;
  }
}

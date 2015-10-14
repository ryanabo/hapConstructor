//******************************************************************************
// TallierImp.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.util.Counter;

//==============================================================================
public class TallierImp 
{
  static int nRows;
  static int nCols;
  Counter[][] theCounters = null;

  //----------------------------------------------------------------------------
  public TallierImp (int nR, int nC)
  {
    nRows = nR;
    nCols = nC;
  }

  //----------------------------------------------------------------------------
  void resetTallies(int value)
  { 
    for ( int irow = 0; irow < nRows; irow++ )
    {
      for ( int icol = 0; icol < nCols; icol++ )
        theCounters[irow][icol].set(0);
    }
  }

  //----------------------------------------------------------------------------
  void resetTallies(double value)
  {
    for ( int irow = 0; irow < nRows; irow++ )
    {
      for ( int icol = 0; icol < nCols; icol++ )
        theCounters[irow][icol].set(0.0);
    }
  }

  //----------------------------------------------------------------------------
  //QuantitativeTallier requires 2 sets of Counter[][]
  public static Counter[][] createCounters( int value )
  {
    Counter[][] counters = new Counter[nRows][nCols];
    for ( int irow = 0; irow < nRows; irow++ )
    {
      for ( int icol = 0; icol< nCols; icol++ )
        counters[irow][icol] = new Counter(value);
    }
    return counters;
  }
  
  //----------------------------------------------------------------------------
  public static Counter[][] createCounters( double value )
  {
    Counter[][] counters = new Counter[nRows][nCols];
    for ( int irow = 0; irow < nRows; irow++ )
    {
      for ( int icol = 0; icol< nCols; icol++ )
        counters[irow][icol] = new Counter(value);
    }
    return counters;
  }

  //----------------------------------------------------------------------------
  public Counter[][] getCounters()
  { return theCounters; }

  //----------------------------------------------------------------------------
  public GenotypeDataQuery[] getDataQuery(){ return null; }

  //----------------------------------------------------------------------------
  public boolean screenMissing( int i ){ return false; }

  //----------------------------------------------------------------------------
  public boolean processQueryResult(int[] queryResult, int i, int queryIndex) { return false; }
  
}

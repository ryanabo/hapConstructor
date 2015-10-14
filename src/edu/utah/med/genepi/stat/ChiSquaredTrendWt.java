//*****************************************************************************
// ChiSquaredTrendWt.java
//*****************************************************************************
package edu.utah.med.genepi.stat;


//=============================================================================
public class ChiSquaredTrendWt extends ChiSquaredTrend 
{
  WeightedCompTracker compTracker;
  //---------------------------------------------------------------------------
  public ChiSquaredTrendWt () 
  {
    super();
  }

  //---------------------------------------------------------------------------
  public String getCompStatTitle()
  { return "Chi Squared Table Value : "; }

  //---------------------------------------------------------------------------
  public ComparisonTracker newComparisonTracker(int df)
  {
    return new WeightedCompTracker(df);
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable(TableMaker tm)
  { 
    return tm.getWeightedIndexTable();
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable(TableMaker tm, Thread p)
  { 
    return tm.getWeightedIndexTable(p);
  }

  //---------------------------------------------------------------------------
  public void setDegreeOfFreedom(int ncol)
  {
    compTracker.setDegreeOfFreedom(1); 
  }
}

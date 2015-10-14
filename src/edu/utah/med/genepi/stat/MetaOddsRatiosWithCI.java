//*****************************************************************************
// MetaOddsRatiosWithCI.java
//*****************************************************************************
package edu.utah.med.genepi.stat;


//=============================================================================
public class MetaOddsRatiosWithCI extends MetaOddsRatios 
{
  //---------------------------------------------------------------------------
  public MetaOddsRatiosWithCI() 
  {
    super();
    infExtraStatTitle = "Empirical Confidence Intervals : ";
  }

  //---------------------------------------------------------------------------
  public ComparisonTracker newComparisonTracker()
  {
    compTracker = new ORCompTrackerExt();
    return compTracker;
  }

  public ComparisonTracker newComparisonTracker(int df)
  {
    return this.newComparisonTracker();
  }
  //----------------------------------------------------------------------------
  public Result getInfExtraStat(ComparisonTracker compTracker)
  {
    ORCompTrackerExt ORct = (ORCompTrackerExt) compTracker;
    return ORct.getConfidenceIntervals();
  }
}

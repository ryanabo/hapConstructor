//*****************************************************************************
// MetaOddsRatiosWithCI.java
//*****************************************************************************
package edu.utah.med.genepi.stat;


//=============================================================================
public class MetaInteractionORWithCI extends MetaInteractionOR 
{
  //---------------------------------------------------------------------------
  public MetaInteractionORWithCI() 
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

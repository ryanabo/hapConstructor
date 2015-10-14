//*****************************************************************************
// OddsRatiosWithCI.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import java.util.Vector;

//=============================================================================
public class OddsRatiosWithCI extends OddsRatios
{

  //---------------------------------------------------------------------------
  public OddsRatiosWithCI() 
  {
    super();
    infExtraStatTitle = "Empirical Confidence Intervals : ";
  }

  //---------------------------------------------------------------------------
  public ComparisonTracker newComparisonTracker()
  {
    return new ORCompTrackerExt(); 
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
  
  //----------------------------------------------------------------------------
  public Vector<Vector<Double>> getInfSimStat(ComparisonTracker compTracker)
  {
    ORCompTrackerExt ORct = (ORCompTrackerExt) compTracker;
    return ORct.getSimulatedResults();
  }
}

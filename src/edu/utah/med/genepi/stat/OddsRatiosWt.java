//*****************************************************************************
// OddsRatiosWt.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Ptype;
//=============================================================================
public class OddsRatiosWt extends OddsRatios 
{
  //---------------------------------------------------------------------------
  public OddsRatiosWt() 
  {
    title = "Odds Ratios, 2-Tailed Test";
    infExtraStatTitle = "";
  }

  //---------------------------------------------------------------------------
  public String getCompStatTitle()
  { return ""; }

  //---------------------------------------------------------------------------
  public Result computeAt0 ( CCAnalysis a,
			     CCAnalysis.Table t )
  {
    if ( a.checkDistinctRefWt() )
    {
      if ( StatUt.checkRefCell ( t.getRowFor(Ptype.CASE).getCells(), 
                                 t.getRowFor(Ptype.CONTROL).getCells(),
                                 a.getReferenceColumnIndex()           ) )
      {
        Result res = new  ResultImp.ResultsWithCISeries (
                         StatUt.oddsRatiosWithConfidence(
        	                  t.getRowFor(Ptype.CASE).getCells(),
                                  t.getRowFor(Ptype.CONTROL).getCells(),
                                  a.getReferenceColumnIndex() ) );
        return res;
      }
      else 
        return new ResultImp.StringResult
        ("WARNING: reference cell has 0 value, **test** has not been performed.");
    }
    else 
       return new ResultImp.StringResult
        ("WARNING: Analysis Table has more than one reference column, **test** has not been performed.");
  }

  //---------------------------------------------------------------------------
  public Result computeAtX ( CCAnalysis a,
			     CCAnalysis.Table t )
  {
    return computeAt0(a,t);
  }
  
  //----------------------------------------------------------------------------
  public synchronized Result getObservedResult( Result obsResult,
                                                int    refColIndex )
  {
    return obsResult;
  }

  //----------------------------------------------------------------------------
  public Result getInferentialResult(ComparisonTracker compTracker,
                                     int refColIndex )
  {
    return new ResultImp.StringResult("");
  }

  //----------------------------------------------------------------------------
  public Result getInfExtraStat(ComparisonTracker compTracker)
  { return null; }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm )
  {
    CCAnalysis.Table wtTable = tm.getWeightedIndexTable();
    return wtTable;
  }
  //---------------------------------------------------------------------------
  //Ryan 06-24-06 passed in thread.
  public CCAnalysis.Table getTable( TableMaker tm, Thread p )
  {
    CCAnalysis.Table wtTable = tm.getWeightedIndexTable(p);
    return wtTable;
  }

}

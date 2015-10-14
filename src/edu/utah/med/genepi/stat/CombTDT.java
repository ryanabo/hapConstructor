//*****************************************************************************
// CombTDT.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

//=============================================================================
public class CombTDT extends CCStatImp
{

  //---------------------------------------------------------------------------
  public CombTDT () 
  {
    title = "Combined TDT - Two Tailed Test (normal distribution)"; 
  }

  //---------------------------------------------------------------------------
  public ComparisonTracker newComparisonTracker()
  {
    return new ScalarCompTracker();
  }

  public ComparisonTracker newComparisonTracker(int df)
  {
    return new ScalarCompTracker();
  }

  //---------------------------------------------------------------------------
  public Result computeAt0 ( CCAnalysis a,
			     CCAnalysis.Table t )
  {
    if ( !a.getType().matches("allele") )
    {
      if ( t != null && t.getColumnCount() == 3 )
      {
        CCAnalysis.Table.Totals btotals = t.getTotals();
        resultAt0 = new ResultImp.Real(
            StatUt.combTDT( btotals.forColumns() ));
      } 
      else 
        resultAt0 = new ResultImp.StringResult 
         ( "WARNING : Failed to extract mean, var or #M frm affected from Trio TDT or Sib TDT, ***test*** has not been performed.");
    }
    else 
      resultAt0 = new ResultImp.StringResult
        ("WARNING : Unable to apply test to table type = " + a.getType() );
    return resultAt0;
  }

  //---------------------------------------------------------------------------
  public Result computeAtX ( CCAnalysis a,
			     CCAnalysis.Table t )
  {
    return computeAt0(a, t);
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm )
  {
    return tm.getCombTDTTable(); 
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm, Thread p )
  {
    return tm.getCombTDTTable(p);
  }

}

//*****************************************************************************
// SibTDT.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

//=============================================================================
public class SibTDT extends CCStatImp
{
  //---------------------------------------------------------------------------
  public SibTDT () 
  {
    title = "Sib TDT - Two Tailed Test"; 
    statType = "stat";
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
        SibTDTTable st = ( SibTDTTable) t;
        CCAnalysis.Table.Totals stotals = st.getTotals();
        resultAt0 = new ResultImp.QuartResults (
              StatUt.sibTDT( stotals.forColumns() ));
      } 
      else
        resultAt0 = new ResultImp.StringResult 
         ( "WARNING : Unable to calculate mean, variance or #M from affected, ***test*** has not been performed");
    }
    else 
       resultAt0 = new ResultImp.StringResult
         ( "WARNING : Unable to apply test to table type = " + a.getType() );
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
    return tm.getSibTDTTable(); 
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm, Thread p )
  {
    return tm.getSibTDTTable(p);
  }

}

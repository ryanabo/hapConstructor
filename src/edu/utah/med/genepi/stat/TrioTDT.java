//*****************************************************************************
// TrioTDT.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

//=============================================================================
public class TrioTDT extends CCStatImp
{

  //---------------------------------------------------------------------------
  public TrioTDT () 
  {
    title = "Case Parents Trio TDT (Chi-Squared Distribution)"; 
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
      if ( t != null && t.getColumnCount() == 3)
      {
        TrioTDTTable tt = ( TrioTDTTable) t;
        resultAt0 = new ResultImp.Real(
                        StatUt.trioTDT( tt.getCellb(), tt.getCellc() ));
      } 
      else 
        resultAt0 =  new ResultImp.StringResult 
         ( "WARNING : Table does not contain 3 columns; ***test*** has not been performed");
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
    return tm.getTrioTDTTable(); 
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm, Thread p )
  {
    return tm.getTrioTDTTable(p);
  }

}

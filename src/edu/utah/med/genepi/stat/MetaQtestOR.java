//*****************************************************************************
// MetaQtestOR.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Ptype;

//=============================================================================
public class MetaQtestOR extends CCStatImp 
{
  Number[][] caseCell;
  Number[][] controlCell;
  Number[][] ctotal;
  int refColIndex;

  //---------------------------------------------------------------------------
  public MetaQtestOR() 
  {
    title = "Meta Q test for Odds Ratios";
  }

  //---------------------------------------------------------------------------
  public ComparisonTracker newComparisonTracker()
  {
    compTracker = new ScalarCompTrackerExt();
    return compTracker;
  }

  public ComparisonTracker newComparisonTracker(int df)
  {
    return this.newComparisonTracker();
  }

  //---------------------------------------------------------------------------
  public Result computeAt0 ( CCAnalysis a,
			     CCAnalysis.Table[] t )
  {
    if ( a.checkDistinctRefWt() )
    {
      getdata(a, t);
      return new  ResultImp.RealSeries (
                   StatUt.metaQtestOR( caseCell,
                                       controlCell,
                                       ctotal,
                                       refColIndex )) ;
    } 
    else
       return new ResultImp.StringResult
        ("WARNING: Analysis Table has more than one reference column, **test** has not been performed.");
  }

  //---------------------------------------------------------------------------
  public void getdata( CCAnalysis a,
                       CCAnalysis.Table[] t )
  {
    CCAnalysis.Table[] metaTable = getNonZeroTables(a, t, compTracker);
    if ( metaTable.length == 0 )
      resultAt0 = new ResultImp.StringResult
      ("WARNING: no valid tables for this analysis");

    int mt          = metaTable.length;
    refColIndex = a.getReferenceColumnIndex();
    caseCell    = new Number[mt][];
    controlCell = new Number[mt][];
    ctotal      = new Number[mt][];
    for ( int i = 0 ; i < mt; i++ )
    {
      ContingencyTable        table  = (ContingencyTable) metaTable[i];
      CCAnalysis.Table.Totals totals = table.getTotals();
      caseCell[i]    = table.getRowFor(Ptype.CASE).getCells();
      controlCell[i] = table.getRowFor(Ptype.CONTROL).getCells();
      ctotal[i]      = totals.forColumns();
      for ( int j = 0; j < ctotal[i].length; j++ )
      {
        if ( ctotal[i][j].intValue() == 0 )
        {
          break;
        }
      }
    }
  }

  //---------------------------------------------------------------------------
  public Result computeAtX ( CCAnalysis a,
                             CCAnalysis.Table[] t )
  {
    return computeAt0(a, t);
  }

  //----------------------------------------------------------------------------
  public Result getObservedResult(Result obsResult,
                                  int    refColIndex )
  {
    if ( obsResult instanceof ResultImp.StringResult )
      return obsResult;
    else
    {
      double[] r0Result = obsResult.doubleValues();
      int numCol = r0Result.length + 1;
      return ResultImp.convertTriplet( obsResult, numCol, refColIndex );
    }
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm )
  {
    return tm.getContingencyTable();
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm, Thread p)
  {
    return tm.getContingencyTable(p);
  }

}

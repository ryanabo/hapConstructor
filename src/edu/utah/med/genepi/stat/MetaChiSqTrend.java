//*****************************************************************************
// MetaChiSqTrend.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Ptype;

//=============================================================================
public class MetaChiSqTrend extends CCStatImp 
{
  //---------------------------------------------------------------------------
  public MetaChiSqTrend () 
  {
    title = "Meta Chi-squared Trend";
  }

  //---------------------------------------------------------------------------
  public ComparisonTracker newComparisonTracker()
  {
    compTracker = new ScalarCompTracker();
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
    CCAnalysis.Table[] metaTable = getNonZeroTables(a, t, compTracker); 
    int mt = metaTable.length;
    Number[][] caseCell    = new Number[mt][];
    Number[][] columnTotal = new Number[mt][];
    Number[] caseTotal     = new Number[mt];
    Number[] tableTotal    = new Number[mt];

    if ( metaTable[0].getColumnCount() < 3 )
      resultAt0 =  new ResultImp.StringResult
                   ("WARNING: table has less than 3 columns, **test** has not been performed.");

    else 
    {
      for ( int i = 0 ; i < mt; i++ )
      {
        ContingencyTable table         = (ContingencyTable) metaTable[i];
        CCAnalysis.Table.Totals totals = table.getTotals();
        caseCell[i] = table.getRowFor(Ptype.CASE).getCells();
        columnTotal[i] = totals.forColumns();
        caseTotal[i] = 
            totals.forRows()[table.ptID2Ix[Integer.parseInt((Ptype.CASE).getID())]];
        tableTotal[i]  = totals.forTable();
   
      //if ( StatUt.checkRowColSum ( totals.forRows() [0],
      //                             totals.forColumns(),
      //                             totals.forTable()
      //                           ))
      }
      resultAt0 =  new ResultImp.Real(
                   StatUt.metaChiSqTrend( caseCell,
                                          columnTotal,
                                          caseTotal,
                                          tableTotal,
                                          a.getColumnWeights() ));
                                            
      //else 
      //  resultAt0 =  new ResultImp.StringResult
      // ("WARNING: row or column sum equals zero, **test** has not been performed.");
    }
    return resultAt0;
  }

  //---------------------------------------------------------------------------
  public Result computeAtX ( CCAnalysis a,
			     CCAnalysis.Table[] t )
  {
    return computeAt0(a, t);
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable(TableMaker tm)
  { 
    return tm.getContingencyTable();
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable(TableMaker tm, Thread p)
  { 
    return tm.getContingencyTable(p);
  }
}

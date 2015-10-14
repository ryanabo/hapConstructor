//*****************************************************************************
// MetaChiSquared.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Ptype;
//=============================================================================
public class MetaChiSquared extends CCStatImp
{
  //---------------------------------------------------------------------------
  public MetaChiSquared () 
  {
    title = "Meta Chi-Squared"; 
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
    if ( a.checkDistinctRefWt() )
    {
      // make sure tableTotal is non zero 
      CCAnalysis.Table[] metaTable = getNonZeroTables( a, t, compTracker );
      int mt = metaTable.length;
      int refColIndex = a.getReferenceColumnIndex();
      Number[] observed     = new Number[mt];
      Number[] caseTotal    = new Number[mt];
      Number[] controlTotal = new Number[mt];
      Number[] obsTotal     = new Number[mt];
      int[]    nonObsTotal  = new int[mt];
      Number[] tableTotal   = new Number[mt];
      //CCAnalysis.Table.Totals[] studyTotals = 
      //                 new CCAnalysis.Table.Totals[t.length]
  
      if ( metaTable[0].getColumnCount() > 2 )
        resultAt0 = new ResultImp.StringResult
                  ("WARNING: This test is designed for a 2 x 2 table only, **test** has not been performed.");
      else 
      {
        for ( int i = 0 ; i < mt; i++ )
        {
          ContingencyTable table = (ContingencyTable) metaTable[i];
          CCAnalysis.Table.Totals totals = table.getTotals();
          observed[i]     = 
              (table.getRowFor(Ptype.CASE).getCells())[refColIndex];
          caseTotal[i]    = 
              totals.forRows()[table.ptID2Ix[Integer.parseInt((Ptype.CASE).getID())]];
          controlTotal[i] = 
              totals.forRows()[table.ptID2Ix[Integer.parseInt((Ptype.CONTROL).getID())]];
          obsTotal[i]     = totals.forColumns()[refColIndex];
          tableTotal[i]   = totals.forTable();
          nonObsTotal[i]  = tableTotal[i].intValue() - obsTotal[i].intValue();
        }
    
        // Check Row and Column Totals before calculation.
        //if ( StatUt.checkRowColSum ( totals.forRows()[0],
        //                             totals.forColumns(),
        //                             totals.forTable()
        //                           ))
        resultAt0 = new ResultImp.Real(
                        StatUt.metaChiSquared( observed,
                                               caseTotal,
                                               controlTotal,
                                               obsTotal,
                                               nonObsTotal,
                                                 tableTotal   ));
      //else 
      //  resultAt0 = new ResultImp.StringResult
      //  ("WARNING: row or column sum equals zero, **test** has not been performed.");
      }
      return resultAt0; 
    } 
    else 
       return new ResultImp.StringResult
        ("WARNING: Analysis Table has more than one reference column, **test** has not been performed.");

  }

  //---------------------------------------------------------------------------
  public Result computeAtX ( CCAnalysis a,
			     CCAnalysis.Table[] t )
  {
    return computeAt0(a, t);
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm )
  {
    return tm.getContingencyTable(); 
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm, Thread p )
  {
    return tm.getContingencyTable(p); 
  }
}

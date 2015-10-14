//*****************************************************************************
// HWE.java
// Hardy-Weinbery Equilibrium 
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Ptype;

//=============================================================================
public class HWE extends CCStatImp
{
  //---------------------------------------------------------------------------
  public HWE() 
  {
    title = "Hardy-Weinbery Equilibrium"; 
  }

  //---------------------------------------------------------------------------
  public ComparisonTracker newComparisonTracker()
  {
    return new ScalarCompTrackerExt();
  }
  public ComparisonTracker newComparisonTracker(int df)
  {
    return new ScalarCompTrackerExt();
  }

  //---------------------------------------------------------------------------
  public Result computeAt0 ( CCAnalysis a,
			     CCAnalysis.Table t )
  {
    CCAnalysis.Table.Totals totals = t.getTotals();

    // check both row sum != zero
    double[] rowTotals = new double[2];
    rowTotals[0] = totals.forRows()[0].doubleValue();
    rowTotals[1] = totals.forRows()[1].doubleValue();
    if ( rowTotals[0] != 0.0 && rowTotals[1] != 0.0 )
    {
      resultAt0 = new ResultImp.RealSeries(
                      StatUt.hwe( t.getRowFor(Ptype.CASE).getCells(),
                                  t.getRowFor(Ptype.CONTROL).getCells(),
                                  rowTotals ));
    }
    else 
    { //System.out.println("failed at checkRowColSum");
      resultAt0 = new ResultImp.StringResult
      ("WARNING: row sum equals to zero, **test** has not been performed.");
    }
    return resultAt0; 
  }

  //---------------------------------------------------------------------------
  public Result computeAtX ( CCAnalysis a,
			     CCAnalysis.Table t )
  {
    return computeAt0(a, t);
  }

  //---------------------------------------------------------------------------
  public synchronized Result getObservedResult( Result obsResult,
                                                int    rowIndex )
  {
    if ( obsResult instanceof ResultImp.StringResult )
      return obsResult;
    else
    {
      double[] r0Result = obsResult.doubleValues();
      int rows = r0Result.length; 
      String[][] resultPair = new String[rows][2];
      for ( int i = 0 ; i < rows; i++ )
      {
        resultPair[i][0] = new String("row " + (i+1));
        resultPair[i][1] = String.valueOf(r0Result[i]);
      }
      return new ResultImp.PairSeries(resultPair);
    }
  }


  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable( TableMaker tm )
  {
    return tm.getContingencyTable(); 
  }
  //---------------------------------------------------------------------------
  //Ryan 06-24-06 passed in thread.
  public CCAnalysis.Table getTable( TableMaker tm, Thread p )
  {
    return tm.getContingencyTable(p);
  }

}

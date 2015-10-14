//*****************************************************************************
// ChiSquaredWt.java
//*****************************************************************************
package edu.utah.med.genepi.stat;


//=============================================================================
public class ChiSquaredWt extends ChiSquared
{
  //---------------------------------------------------------------------------
  public ChiSquaredWt () 
  {
    super(); 
  }

  //---------------------------------------------------------------------------
  public String getCompStatTitle() { return "Chi Squared Table Value : "; }

  //---------------------------------------------------------------------------
  public ComparisonTracker newComparisonTracker(int df)
  {
    return new WeightedCompTracker(df);
  }

  //---------------------------------------------------------------------------
  //public Result computeAt0 ( CCAnalysis a,
  //                           CCAnalysis.Table t )
  //{
  //  CCAnalysis.Table.Totals totals = t.getTotals();

    // Check Row and Column Totals before calculation.
    //if ( StatUt.checkRowColSum ( totals.forRows()[0],
    //                             totals.forColumns(),
    //                             totals.forTable()
    //                           ))
    //{
      //Number[] caser = t.getRowFor(Ptype.CASE).getCells();
      //Number[] contr = t.getRowFor(Ptype.CONTROL).getCells();

      //resultAt0 = new ResultImp.Real(
      //                StatUt.chiSquared( t.getRowFor(Ptype.CASE).getCells(),
      //                                   totals.forRows()[0],
      //                                   totals.forColumns(),
      //                                   totals.forTable() ) );
        //System.out.println(" Chi Squared computeAt0 " + resultAt0);
    //}
    //else 
    //{ //System.out.println("failed at checkRowColSum");
    //  resultAt0 = new ResultImp.StringResult
    //  ("WARNING: row or column sum equals zero, **test** has not been performed.");
    //}
    //return resultAt0; 
  //}

  //---------------------------------------------------------------------------
  //public Result computeAtX ( CCAnalysis a,
  //                           CCAnalysis.Table t )
  //{
    //do nothing
  //}

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

  //---------------------------------------------------------------------------
  public int getDegreeOfFreedom(int ncol)
  {
    return (ncol-1);
  }
  //---------------------------------------------------------------------------
  //public WeightedCompTracker getCompTracker()
  //{ 
  //  return this.compTracker;
  //}
}

//******************************************************************************
// TrioTDTTable.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.util.Counter;

//==============================================================================
public class TrioTDTTable extends TableImp {
 
  private final Counter[][] myCounters;
  public int  cellB, cellC;
  public int myTrioCount;

  //----------------------------------------------------------------------------
  TrioTDTTable (  Column[] cols, Counter[][] counters, int trio )
  {
    myRows = new Row[counters.length];
    myCols = cols;
    myCounters = counters;
    myTrioCount = trio;
    myTableName = "Trio TDT Table";
    myColumnHeading = "Case's GenoType";
    myRowHeading = "Parents GenoType";
    myColumnLabels = new String[cols.length];
    Number[] rtotals = new Number[myRows.length];
    Number[] ctotals = new Number[myCols.length];
    Number gtotal = new Integer(0);
    String[] myRowLabel = new String[counters.length];
    
    
    String mm = "mm";
    String mM = "mM";
    String MM = "MM";
    String x  = " x ";

    myColumnLabels[0] = mm;
    myColumnLabels[1] = mM;
    myColumnLabels[2] = MM;
    myRowLabel[0] = mm + x + mm;
    myRowLabel[1] = mm + x + mM;
    myRowLabel[2] = mm + x + MM;
    myRowLabel[3] = mM + x + mM;
    myRowLabel[4] = mM + x + MM;
    myRowLabel[5] = MM + x + MM;
    
    for ( int icol = 0; icol < myCols.length; icol++ )
      ctotals[icol] = new Integer(0);

    for (int irow = 0; irow < myRows.length; ++irow)
    {
      myRows[irow] = new RowImp(myRowLabel[irow], myCounters[irow]);
      rtotals[irow] = new Integer(0);
      for (int icol = 0; icol < myCols.length; ++icol)
      {
        int cell = myCounters[irow][icol].current().intValue();
        rtotals[irow] = new Integer( rtotals[irow].intValue() + cell );
        ctotals[icol] = new Integer( ctotals[icol].intValue() + cell );
        gtotal = new Integer( gtotal.intValue() + cell );
      }
    }

    myMessage = "(# case trios analyzed / total # case trios in dataset : " +
                 gtotal.toString() + " / " + Integer.toString(trio) + ")";
    myTotals = new TotalsImp( rtotals, ctotals, gtotal );
  }
    
  //----------------------------------------------------------------------------
  public int getCellb() 
  { 
    int cellB = 0;
    if ( myCols.length == 3 )
      cellB = ( 2 * myCounters[3][2].current().intValue() +
                    myCounters[1][1].current().intValue() +
                    myCounters[3][1].current().intValue() +
                    myCounters[4][2].current().intValue() );
    return cellB;
  }

  //----------------------------------------------------------------------------
  public int getCellc()
  {
    int cellC = 0;
    if ( myCols.length == 3 )
      cellC = ( 2 * myCounters[3][0].current().intValue() +
                    myCounters[1][0].current().intValue() +
                    myCounters[3][1].current().intValue() +
                    myCounters[4][1].current().intValue() );
    return cellC;
  }

  //----------------------------------------------------------------------------
  public Counter[][] getCounters()
  { return myCounters; }

  public Column[] getColumns()
  { return myCols; }
  
  public int getTrioCount()
  { return myTrioCount; }
}

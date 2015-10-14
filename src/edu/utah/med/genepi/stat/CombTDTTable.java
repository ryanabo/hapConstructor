//******************************************************************************
// CombTDTTable.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.util.Counter;

//==============================================================================
public final class CombTDTTable extends TableImp {

  private final Counter[][] myCounters;
  public String[] myRowLabels;

  //----------------------------------------------------------------------------
  CombTDTTable ( CCAnalysis.Table.Column[] columns,
                 String[] rows, String[] cols, Counter[][] counters )
  {
    myRows = new Row[rows.length];
    myCols = columns;
    myCounters = counters;
    myTableName = "Combined TDT Table";
    myColumnHeading = "Overall Statistics";
    myRowHeading = "Trio/Sib"; 
    myColumnLabels = cols;
    myRowLabels = rows;

    Number[] rtotals = new Number[myRows.length];
    Number[] ctotals = new Number[myColumnLabels.length];
    Number   gtotal = new Double(0.0);

    for ( int icol = 0; icol < myColumnLabels.length; icol++ )
      ctotals[icol] = new Double(0.0);

    for ( int irow = 0; irow < myRows.length; irow++ )
    {
      myRows[irow] = new RowImp(rows[irow], myCounters[irow]);
      rtotals[irow] = new Double(0.0);
     
      for (int icol = 0; icol < myColumnLabels.length; ++icol)
      {
        double cell = myCounters[irow][icol].current().doubleValue();
        rtotals[irow] = new Double(rtotals[irow].doubleValue() + cell);
        ctotals[icol] = new Double(ctotals[icol].doubleValue() + cell);
        gtotal = new Double( gtotal.doubleValue() + cell);
      }
    }
   
    myTotals = new TotalsImp( rtotals, ctotals, gtotal ); 
  }

  //----------------------------------------------------------------------------
  public Column[] getColumns()
  { return myCols; }

  public Counter[][] getCounters()
  { return myCounters; }

  public String[] getRowLabels()
  { return myRowLabels; }

  public String[] getColumnLabels()
  { return myColumnLabels; }
}

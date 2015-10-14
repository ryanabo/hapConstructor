//******************************************************************************
// SibTDTTable.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.util.Counter;

//==============================================================================
public final class SibTDTTable extends TableImp {

  private final Counter[][] myCounters;
  public Number[] rtotals;
  public Number[] ctotals;
  public Number gtotal;

  //----------------------------------------------------------------------------
  SibTDTTable ( CCAnalysis.Table.Column[] cols, Counter[][] counters )
  {
    myRows = new Row[1];
    myCols = cols;
    myCounters = counters;
    myTableName = "Sib TDT Table";
    myColumnHeading = "Overall Statistics";
    myRowHeading = "Sib";
    myColumnLabels = new String[] {"Mean", "Variance" , "# M from affected" };

    rtotals = new Number[myRows.length];
    ctotals = new Number[myColumnLabels.length];
    gtotal = new Double(0.0);

    for ( int icol = 0; icol < myColumnLabels.length; icol++ )
      ctotals[icol] = new Double(0.0);

    myRows[0] = new RowImp("", myCounters[0]);
    rtotals[0] = new Double(0.0);
     
    for (int icol = 0; icol < myColumnLabels.length; ++icol)
    {
      double cell = myCounters[0][icol].current().doubleValue();
      rtotals[0] = new Double(rtotals[0].doubleValue() + cell);
      ctotals[icol] = new Double(ctotals[icol].doubleValue() + cell);
      gtotal = new Double( gtotal.doubleValue() + cell);
    }
   
    myTotals = new TotalsImp( rtotals, ctotals, gtotal ); 
  }

  //----------------------------------------------------------------------------
  public Number[] getColumnTotal()
  { return ctotals; }

  //----------------------------------------------------------------------------
  public Number[] getRowTotal()
  { return rtotals; }

  //----------------------------------------------------------------------------
  public Number getOverallTotal()
  { return gtotal; }

  //----------------------------------------------------------------------------
  public Counter[][] getCounters()
  { return myCounters; }
  
  public Column[] getColumns()
  { return myCols; }

}

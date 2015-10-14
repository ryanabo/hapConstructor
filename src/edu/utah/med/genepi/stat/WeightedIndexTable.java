//******************************************************************************
// WeightedIndexTable.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.util.Counter;

//==============================================================================
public class WeightedIndexTable extends TableImp 
{
  private final Counter[][] myCounters;

  //----------------------------------------------------------------------------
  public WeightedIndexTable ( Ptype[] pts,
                              Column[] cols,
                              Counter[][] counters,
                              int[] ptype_id2ix )
  {
    myRows = new Row[pts.length];
    myCols = cols;
    myCounters = counters;
    ptID2Ix = ptype_id2ix;
    myTableName = "Weighted Index Table";
    myColumnHeading = "Col(wt)";
    myRowHeading = "Pheno Type";
    myColumnLabels = new String[myCols.length];

    Number[] rtotals = new Number[myRows.length];
    Number[] ctotals = new Number[myCols.length];
    double   gtotal = 0.0;

    for ( int icol = 0; icol < myCols.length; icol++ )
    {
      myColumnLabels[icol] = (icol+1) + "(" + myCols[icol].getWeight() + ")";
      ctotals[icol] = new Integer(0);
    }

    for (int irow = 0; irow < myRows.length; ++irow)
    {
      //myRows[irow] = new RowExt(pts[irow], counters[irow]);
      myRows[irow] = new RowImp(pts[irow].toString(), myCounters[irow]);
      rtotals[irow] = new Integer(0);
     
      for (int icol = 0; icol < myCols.length; ++icol)
      {
        double cell = myCounters[irow][icol].current().doubleValue();
        rtotals[irow] = new Double(rtotals[irow].doubleValue() + cell);
        ctotals[icol] = new Double(ctotals[icol].doubleValue() + cell);
        gtotal += cell;
      }
    }
   
    myTotals = new TotalsImp( rtotals, ctotals, new Double(gtotal) ); 
  }

}

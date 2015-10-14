//******************************************************************************
// QuantitativeTable.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.util.Counter;

//==============================================================================
public class QuantitativeTable extends TableImp {
 
  public final int[]     col_n;
  public final Counter[][] mygroups;
  public final Counter[][] mysqgroups;

  //----------------------------------------------------------------------------
  QuantitativeTable ( 	int[] quant_id,
	        	Column[] cols, 
	        	Counter[][] sumgroups,
                        Counter[][] sqvalgroups ) 
  {
    myRows = new Row[quant_id.length];
    myCols = cols;
    mygroups = sumgroups;
    mysqgroups = sqvalgroups;
    myTableName = "Quantitative Table";
    myRowHeading = "CoVar Mean";
    myColumnHeading = "Col(wt)";
    myColumnLabels = new String[myCols.length];

    Number[] rtotals = new Number[myRows.length];
    Number[] ctotals = new Number[myCols.length];
    Number[] cellsqcoltotals = new Number[myCols.length];
    double gtotal = 0.0;
    double cellsqtable = 0.0;
    col_n = new int[myCols.length];

    for ( int icol = 0; icol < myCols.length; icol++ )
    {
      ctotals[icol]   = new Double(0.0);
      cellsqcoltotals[icol] = new Double(0.0);
      myColumnLabels[icol] = (icol+1) + "(" + myCols[icol].getWeight() + ")";
    }

    for (int irow = 0; irow < myRows.length; ++irow)
    {
      rtotals[irow] = new Double(0.0);
      
      for (int icol = 0; icol < myCols.length; ++icol)
      {
        double cell = (mygroups[irow][icol]).current().doubleValue();
        double sqcell = (mysqgroups[irow][icol]).current().doubleValue();
        rtotals[irow] = new Double(rtotals[irow].doubleValue() + cell );
        cellsqcoltotals[icol] = new Double( cellsqcoltotals[icol].doubleValue() 
                                    + sqcell ); 
        ctotals[icol] = new Double( ctotals[icol].doubleValue() + cell );
        col_n[icol] += (mygroups[irow][icol]).get_n();
        gtotal += cell;
        cellsqtable += sqcell;
 
      }

      String rowlabel = "CoVar-" + irow;
      myRows[irow] = new RowImp(rowlabel, mygroups[irow]);
    }


    myTotals = new TotalsExt( rtotals, ctotals,
			      cellsqcoltotals,  
			      col_n, 
                              new Double(gtotal), 
                              new Double(cellsqtable) );
  }

  //----------------------------------------------------------------------------
  //public Row getRowFor() { return myRows[0]; }

  //----------------------------------------------------------------------------
  /*public class RowExt extends RowImp 
  {
  
    //--------------------------------------------------------------------------
    RowExt ( String rlabel,  SumGroup[] sumgroups )
    {
      super(rlabel, tmpcounter);
      //myQuantids = rownum;
      double[] myCell = new double[sumgroups.length];

      for ( int i = 0; i < myCell.length; i++ )
      {
        myCell[i] = sumgroups[i].current();
        myprintableCell[i] = Double.toString(sumgroups[i].current());
      }
    }
 
    //--------------------------------------------------------------------------
    //public int getquantids() { return myQuantids; }

    //--------------------------------------------------------------------------
    public String[] getprintableCell() { return myprintableCell; }
    
  }
**/

  //----------------------------------------------------------------------------
  public class TotalsExt extends TotalsImp 
  {
    protected int[]   colnTotals;
    protected Number[] cellsqcolTotals;
    protected Number  cellsqTable;

    TotalsExt ( Number[] r_totals,
		Number[] c_totals,
    		Number[] cellsqColtotals,
                int[] col_n_count,
		Number g_total,
		Number cellsqtable)
    {
      super(r_totals, c_totals, g_total);
      cellsqcolTotals = cellsqColtotals;
      colnTotals = col_n_count;
      cellsqTable = cellsqtable;
    }

    //--------------------------------------------------------------------------
    public Number[] forCellSqColumns() { return cellsqcolTotals; }
    public int[] forCol_counts() { return colnTotals; }
    public Number forCellSqTable() { return cellsqTable; }
  }
}

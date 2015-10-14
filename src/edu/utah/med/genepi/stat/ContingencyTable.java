//******************************************************************************
// ContingencyTable.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.util.Counter;

//==============================================================================
public class ContingencyTable extends TableImp {

	private final Counter[][] myCounters;

	//----------------------------------------------------------------------------
	public ContingencyTable ( Ptype[] pts, Column[] cols, Counter[][] counters, int[] ptype_id2ix )
	{
		myRows = new Row[pts.length];
		myCols = cols;
		myCounters = counters;
		ptID2Ix = ptype_id2ix;
		myTableName = "Contingency Table";
		myColumnHeading = "Col(wt)";
		myRowHeading = "Pheno Type";
		myColumnLabels = new String[myCols.length];
		int nRows = myRows.length;
		int nCols = myCols.length;

		int[] rtotals = new int[nRows];
		int[] ctotals = new int[nCols];
		int gtotal = 0;
		Number[] rt = new Number[nRows];
		Number[] ct = new Number[nCols];
		Number   gt = new Integer(0);

		for ( int icol = 0; icol < nCols; icol++ )
		{
			myColumnLabels[icol] = (icol+1) + "(" + myCols[icol].getWeight() + ")";
			ctotals[icol] = 0;
		}

		for (int irow = 0; irow < nRows; ++irow)
		{
			myRows[irow] = new RowImp(pts[irow].toString(), myCounters[irow]);
			rtotals[irow] = 0;

			for (int icol = 0; icol < nCols; ++icol)
			{
				int cell = myCounters[irow][icol].current().intValue();
				rtotals[irow] += cell;
				ctotals[icol] += cell;
				gtotal += cell;
			}
		}

		for ( int irow = 0; irow < nRows; irow++ )
			rt[irow] = new Integer(rtotals[irow]);
		for ( int icol = 0; icol < nCols; icol++ )
			ct[icol] = new Integer(ctotals[icol]);
		gt = new Integer(gtotal);

		myTotals = new TotalsImp(rt,ct,gt); 
	}
}

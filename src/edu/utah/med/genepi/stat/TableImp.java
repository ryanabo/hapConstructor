//******************************************************************************
// TableImp.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.util.Counter;

//==============================================================================
public class TableImp implements AnalysisTable {

	Row[]    myRows = null;
	Column[] myCols = null; 
	Totals   myTotals = null;
	int[]    ptID2Ix = null;
	String   myTableName = null;
	String   myColumnHeading = null;
	String   myRowHeading = null;
	String[] myColumnLabels = null;
	String   myMessage = null;

	//----------------------------------------------------------------------------
	public String getTableName() { return myTableName; }

	//----------------------------------------------------------------------------
	public String getColumnHeading() { return myColumnHeading; }

	//----------------------------------------------------------------------------
	public String getColumnLabelAt(int index) { return myColumnLabels[index]; }

	//----------------------------------------------------------------------------
	public int getColumnCount() { return myCols.length; }

	//----------------------------------------------------------------------------
	public Column getColumnAt(int index) { return myCols[index]; }

	//----------------------------------------------------------------------------
	public Row getRowAt(int index) { return myRows[index]; }

	//----------------------------------------------------------------------------
	public Row getRowFor(Ptype pt) 
	{ 
		int r = Integer.parseInt(pt.getID());
		return myRows[ptID2Ix[r]];
	}

	//----------------------------------------------------------------------------
	public int getRowCount() { return myRows.length; }

	//----------------------------------------------------------------------------
	public String getRowHeading() { return myRowHeading; }

	//----------------------------------------------------------------------------
	public Totals getTotals() { return myTotals; }

	//----------------------------------------------------------------------------
	public String getAttachMessage() { return myMessage; }

	//============================================================================
	public class RowImp implements Row 
	{
		Ptype myPt = Ptype.CASE;
		Number[] myCell = null;
		int[] myCellN;
		String myLabel  = null;

		//--------------------------------------------------------------------------
		public RowImp (String label, Counter[] count )
		{
			myLabel = label;
			myCell  = new Number[count.length];
			myCellN = new int[count.length];
			for ( int i = 0; i < myCell.length; i++ )
			{
				myCell[i] = (Number) count[i].current();
				myCellN[i] = count[i].get_n();
			}
		}

		//--------------------------------------------------------------------------
		public Ptype getPtype() { return myPt; }

		//--------------------------------------------------------------------------
		public Number[] getCells() { return myCell; }

		//--------------------------------------------------------------------------
		public int[] getCellsN() { return myCellN; }

		//--------------------------------------------------------------------------
		public String getLabel() { return myLabel; }

	}

	//============================================================================
	public static class TotalsImp implements Totals 
	{

		Number[] rowTotals = null;
		Number[] colTotals = null;
		Number   grandTotal = null;

		//--------------------------------------------------------------------------
		public TotalsImp ( Number[] row_totals, Number[] col_totals, Number grand_total )
		{
			rowTotals = row_totals;
			colTotals = col_totals;
			grandTotal = grand_total;
		}

		//--------------------------------------------------------------------------
		public Number[] forRows() { return rowTotals; }

		//--------------------------------------------------------------------------
		public Number[] forColumns() { return colTotals; }

		//--------------------------------------------------------------------------
		public Number forTable() { return grandTotal; }
	}
}


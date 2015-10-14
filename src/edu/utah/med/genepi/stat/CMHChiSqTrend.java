//*****************************************************************************
// CMHChiSqTrend.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

//=============================================================================
public class CMHChiSqTrend extends CCStatImp 
{
	//---------------------------------------------------------------------------
	public CMHChiSqTrend () 
	{
		title = "Cochran Mantel Haenszel Chi-Squared Trend"; 
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
	public int[] getColumnWeights(Analysis a)
	{ 
		int[] colWt = a.getColWts();
		return colWt;
	}

	//---------------------------------------------------------------------------
	public boolean checkColumnReq(AnalysisTable t)
	{
		if ( t.getColumnCount() < 3 )
			return false;
		else 
			return true;
	}
	
	//---------------------------------------------------------------------------
	public Result computeStat ( Analysis a, AnalysisTable[] t, int[] simIndices )
	{
		AnalysisTable[] metaTable = getNonZeroTables(a, t, compTracker);
		if ( metaTable.length == 0 )
			resultAt0 = new ResultImp.StringResult
			("WARNING: no valid tables for this analysis");

		int mt                  = metaTable.length;
		Number[][]    rowTotals = new Number[mt][];
		Number[][] columnTotals = new Number[mt][];
		Number[]     tableTotal = new Number[mt];
		Number[][]    casecells = new Number[mt][];
		Number[][] controlcells = new Number[mt][];
		int[]          columnwt = getColumnWeights(a);

		for ( int i = 0 ; i < mt; i++ )
		{
			ContingencyTable table         = (ContingencyTable) metaTable[i];
			AnalysisTable.Totals totals    = table.getTotals();
			rowTotals[i]                   = totals.forRows();
			columnTotals[i]                = totals.forColumns();
			tableTotal[i]                  = totals.forTable();
			casecells[i]                   = table.getRowFor(Ptype.CASE).getCells();
			controlcells[i]                = table.getRowFor(Ptype.CONTROL).getCells();
		}
		try
		{
			if ( checkColumnReq(t[0]) )
				resultAt0 = new ResultImp.Real(
						StatUt.cmhChiSqTrend ( casecells,
								controlcells,
								rowTotals,
								columnTotals,
								tableTotal,
								constantJ,
								columnwt ));

			else
				resultAt0 = new ResultImp.StringResult
				("WARNING: table has less than 3 columns, **test** has not been performed.");

		}
		catch ( Exception e )
		{
			resultAt0 = new ResultImp.StringResult
			( "WARNING : " + e );
			//  ("WARNING: can not calculate var[n|H0], **test** has not been performed.");
		}
		finally
		{ return resultAt0; }
	}

	//---------------------------------------------------------------------------
//	public Result computeAt0 ( CCAnalysis a,
//			CCAnalysis.Table[] t )
//	{
//		CCAnalysis.Table[] metaTable = getNonZeroTables(a, t, compTracker);
//		if ( metaTable.length == 0 )
//			resultAt0 = new ResultImp.StringResult
//			("WARNING: no valid tables for this analysis");
//
//		int mt                  = metaTable.length;
//		Number[][]    rowTotals = new Number[mt][];
//		Number[][] columnTotals = new Number[mt][];
//		Number[]     tableTotal = new Number[mt];
//		Number[][]    casecells = new Number[mt][];
//		Number[][] controlcells = new Number[mt][];
//		int[]          columnwt = getColumnWeights(a);
//
//		for ( int i = 0 ; i < mt; i++ )
//		{
//			ContingencyTable table         = (ContingencyTable) metaTable[i];
//			CCAnalysis.Table.Totals totals = table.getTotals();
//			rowTotals[i]                   = totals.forRows();
//			columnTotals[i]                = totals.forColumns();
//			tableTotal[i]                  = totals.forTable();
//			casecells[i]                   = table.getRowFor(Ptype.CASE).getCells();
//			controlcells[i]                = table.getRowFor(Ptype.CONTROL).getCells();
//		}
//		try
//		{
//			if ( checkColumnReq(t[0]) )
//				resultAt0 = new ResultImp.Real(
//						StatUt.cmhChiSqTrend ( casecells,
//								controlcells,
//								rowTotals,
//								columnTotals,
//								tableTotal,
//								constantJ,
//								columnwt ));
//
//			else
//				resultAt0 = new ResultImp.StringResult
//				("WARNING: table has less than 3 columns, **test** has not been performed.");
//
//		}
//		catch ( Exception e )
//		{
//			resultAt0 = new ResultImp.StringResult
//			( "WARNING : " + e );
//			//  ("WARNING: can not calculate var[n|H0], **test** has not been performed.");
//		}
//		finally
//		{ return resultAt0; }
//	}
//
//	//---------------------------------------------------------------------------
//	public Result computeAtX ( CCAnalysis a,
//			CCAnalysis.Table[] t )
//	{
//		return computeAt0(a, t);
//	}

	//---------------------------------------------------------------------------
	public AnalysisTable getTable( Analysis analysis, int studyIndex, int[] simIndices )
	{
		return (AnalysisTable) analysis.getContingencyTable(studyIndex,simIndices); 
	}

}

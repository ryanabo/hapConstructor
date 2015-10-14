//*****************************************************************************
// CMHChiSquared.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

//=============================================================================
public class CMHChiSquared extends CCStatImp
{
	//---------------------------------------------------------------------------
	public CMHChiSquared () 
	{
		title = "Cochran Mantel Haenszel Chi-Squared"; 
	}

	//---------------------------------------------------------------------------
	public ComparisonTracker newComparisonTracker()
	{
		compTracker = new ScalarCompTracker();
		return compTracker;
	}

	public ComparisonTracker newComparisonTracker(int df)
	{
		return newComparisonTracker();
	}

	//---------------------------------------------------------------------------
	//public boolean checkColumnReq( CCAnalysis.Table t )
	//{
	//  return true;
	//}
	//
	
	//---------------------------------------------------------------------------
	public Result computeStat ( Analysis a, AnalysisTable[] t, int[] simIndices )
	{
		// Notes : our table is setup where rows and columns are opposite to  
		// CMH's table. We will reverse the row and column totals in the CMH
		// statistic 

		AnalysisTable[] metaTable = getNonZeroTables(a, t, compTracker);
		if ( metaTable == null )
			resultAt0 = new ResultImp.StringResult
			("WARNING: no valid tables for this analysis");

		int mt                  = metaTable.length;
		Number[][]    rowTotals = new Number[mt][];
		Number[][] columnTotals = new Number[mt][];
		Number[]     tableTotal = new Number[mt];
		Number[][]    casecells = new Number[mt][];
		Number[][] controlcells = new Number[mt][];

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
			resultAt0 = new ResultImp.Real(
					StatUt.cmhChiSquared ( casecells,
							controlcells,
							rowTotals,
							columnTotals,
							tableTotal, 
							constantJ ));
		}
		catch ( Exception e )
		{ 
			resultAt0 = new ResultImp.StringResult
			("WARNING: cannot calculate var[n|H0], test has not been performed.");
		}
		finally
		{ return resultAt0; }		
	}
	
	//---------------------------------------------------------------------------
//	public Result computeAt0 ( CCAnalysis a,
//			CCAnalysis.Table[] t )
//	{
//		// Notes : our table is setup where rows and columns are opposite to  
//		// CMH's table. We will reverse the row and column totals in the CMH
//		// statistic 
//
//		CCAnalysis.Table[] metaTable = getNonZeroTables(a, t, compTracker);
//		if ( metaTable == null )
//			resultAt0 = new ResultImp.StringResult
//			("WARNING: no valid tables for this analysis");
//
//		int mt                  = metaTable.length;
//		Number[][]    rowTotals = new Number[mt][];
//		Number[][] columnTotals = new Number[mt][];
//		Number[]     tableTotal = new Number[mt];
//		Number[][]    casecells = new Number[mt][];
//		Number[][] controlcells = new Number[mt][];
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
//
//		try 
//		{
//			resultAt0 = new ResultImp.Real(
//					StatUt.cmhChiSquared ( casecells,
//							controlcells,
//							rowTotals,
//							columnTotals,
//							tableTotal, 
//							constantJ ));
//		}
//		catch ( Exception e )
//		{ 
//			resultAt0 = new ResultImp.StringResult
//			("WARNING: cannot calculate var[n|H0], test has not been performed.");
//		}
//		finally
//		{ return resultAt0; }
//	}

	//---------------------------------------------------------------------------
//	public Result computeAtX ( CCAnalysis a,
//			CCAnalysis.Table[] t )
//	{
//		return computeAt0(a, t);
//	}

	//  //---------------------------------------------------------------------------
	//  public CCAnalysis.Table getTable( TableMaker tm )
	//  {
	//    return tm.getContingencyTable(); 
	//  }

	//---------------------------------------------------------------------------
	public AnalysisTable getTable( Analysis analysis, int studyIndex, int[] simIndices )
	{
		return (AnalysisTable) analysis.getContingencyTable(studyIndex,simIndices); 
	}

}

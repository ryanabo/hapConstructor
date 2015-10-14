//*****************************************************************************
// MetaOddsRatios.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

//=============================================================================
public class MetaOddsRatios extends CCStatImp 
{
	Number[][] caseCell;
	Number[][] controlCell;
	Number[][] ctotal;
	int refColIndex;
	//---------------------------------------------------------------------------
	public MetaOddsRatios() 
	{
		title = "Meta Odds Ratios, 2-Tailed Test";
	}

	//---------------------------------------------------------------------------
	public ComparisonTracker newComparisonTracker()
	{
		compTracker = new ORComparisonTracker();
		return compTracker;
	}

	public ComparisonTracker newComparisonTracker(int df)
	{
		return newComparisonTracker();
	}

	//---------------------------------------------------------------------------
	public void getdata( Analysis a, AnalysisTable[] t )
	{
		AnalysisTable[] metaTable = getNonZeroTables(a,t,compTracker);
		if ( metaTable == null ) resultAt0 = new ResultImp.StringResult ("WARNING: no valid tables for this analysis");

		int mt = metaTable.length;
		refColIndex = a.getRefColIndex();
		caseCell = new Number[mt][];
		controlCell = new Number[mt][];
		ctotal = new Number[mt][];
		for ( int i = 0 ; i < mt; i++ )
		{
			ContingencyTable        table  = (ContingencyTable) metaTable[i];
			AnalysisTable.Totals totals = table.getTotals();
			caseCell[i]    = table.getRowFor(Ptype.CASE).getCells();
			controlCell[i] = table.getRowFor(Ptype.CONTROL).getCells();
			ctotal[i]      = totals.forColumns();
			for ( int j = 0; j < ctotal[i].length; j++ )
			{
				if ( ctotal[i][j].intValue() == 0 ) break;
			}
		}
	}
	
	//---------------------------------------------------------------------------
	public Result computeStat( Analysis a, AnalysisTable[] tables, int[] simIndices )
	{
		if ( a.checkDistinctRefWt() )
		{
			getdata(a,tables);
			return new  ResultImp.RealSeries(StatUt.metaOddsRatios( caseCell,controlCell,ctotal,refColIndex)) ;
		} 
		else
			return new ResultImp.StringResult
			("WARNING: Analysis Table has more than one reference column, **test** has not been performed.");
	}

	//---------------------------------------------------------------------------
//	public Result computeAt0 ( CCAnalysis a,
//			CCAnalysis.Table[] t )
//	{
//		if ( a.checkDistinctRefWt() )
//		{
//			getdata(a,t);
//			return new  ResultImp.RealSeries(StatUt.metaOddsRatios( caseCell,controlCell,ctotal,refColIndex)) ;
//		} 
//		else
//			return new ResultImp.StringResult
//			("WARNING: Analysis Table has more than one reference column, **test** has not been performed.");
//
//	}

	//---------------------------------------------------------------------------
//	public Result computeAtX ( CCAnalysis a,
//			CCAnalysis.Table[] t )
//	{
//		return computeAt0(a, t);
//	}

	//----------------------------------------------------------------------------
	public Result getObservedResult(Result obsResult,
			int    refColIndex )
	{
		if ( obsResult instanceof ResultImp.StringResult ) return obsResult;
		else
		{
			double[] r0Result = obsResult.doubleValues();
			int numCol = r0Result.length + 1;
			return ResultImp.convertTriplet( obsResult, numCol, refColIndex );
		}
	}

	//----------------------------------------------------------------------------
	public Result getInferentialResult(ComparisonTracker compTracker,
			int refColIndex )
	{
		ORComparisonTracker ORct = (ORComparisonTracker) compTracker;
		double[] rr = (ORct.getComparisonsResult()).doubleValues();
		int numCol = rr.length + 1;
		return ResultImp.convertTriplet(ORct.getComparisonsResult(),
				numCol,
				refColIndex );
	}

	//----------------------------------------------------------------------------
	public AnalysisTable getTable( Analysis analysis, int studyIndex, int[] simIndices )
	{
		return (AnalysisTable) analysis.getContingencyTable(studyIndex,simIndices); 
	}

}

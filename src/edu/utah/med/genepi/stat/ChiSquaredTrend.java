//*****************************************************************************
// ChiSquaredTrend.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

//=============================================================================
public class ChiSquaredTrend extends CCStatImp 
{
	//---------------------------------------------------------------------------
	public ChiSquaredTrend () 
	{
		title = "Chi-squared Trend";
	}

	//---------------------------------------------------------------------------
	public ComparisonTracker newComparisonTracker()
	{
		return new ScalarCompTracker();
	}

	public ComparisonTracker newComparisonTracker(int df)
	{
		return new ScalarCompTracker();
	}

	//---------------------------------------------------------------------------
	public Result computeStat ( Analysis a, AnalysisTable[] tables, int[] simIndices )
	{
		AnalysisTable t = tables[0];
		AnalysisTable.Totals totals = t.getTotals();
		if ( StatUt.checkRowColSum ( totals.forRows() [0],
				totals.forColumns(),
				totals.forTable()
		))
			resultAt0 =  new ResultImp.Real(
					StatUt.chiSquaredTrend(
							t.getRowFor(Ptype.CASE).getCells(), 
							totals.forRows()[0],
							totals.forColumns(), 
							totals.forTable(), 
							a.getColWts() ) );
		else 
			resultAt0 =  new ResultImp.StringResult
			("WARNING: row or column sum equals zero, **test** has not been performed.");
		return resultAt0;
	}

	//---------------------------------------------------------------------------
	public Result computeAt0 ( CCAnalysis a,
			CCAnalysis.Table t )
	{
		CCAnalysis.Table.Totals totals = t.getTotals();
		if ( StatUt.checkRowColSum ( totals.forRows() [0],
				totals.forColumns(),
				totals.forTable()
		))
			resultAt0 =  new ResultImp.Real(
					StatUt.chiSquaredTrend(
							t.getRowFor(Ptype.CASE).getCells(), 
							totals.forRows()[0],
							totals.forColumns(), 
							totals.forTable(), 
							a.getColumnWeights() ) );
		else 
			resultAt0 =  new ResultImp.StringResult
			("WARNING: row or column sum equals zero, **test** has not been performed.");
		return resultAt0;
	}

	//---------------------------------------------------------------------------
	public Result computeAtX ( CCAnalysis a,
			CCAnalysis.Table t )
	{
		return computeAt0(a, t);
	}

	//---------------------------------------------------------------------------
	public AnalysisTable getTable( Analysis analysis, int studyIndex, int[] simIndices )
	{
		return (AnalysisTable) analysis.getContingencyTable(studyIndex,simIndices); 
	}
	
	

}

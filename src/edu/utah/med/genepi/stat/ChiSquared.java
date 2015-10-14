//*****************************************************************************
// ChiSquared.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

//=============================================================================
//public class ChiSquared implements CCStat {

public class ChiSquared extends CCStatImp
{
	//---------------------------------------------------------------------------
	public ChiSquared (){ title = "Chi-squared"; }

	//---------------------------------------------------------------------------
	public ComparisonTracker newComparisonTracker()
	{
		return new ScalarCompTracker();
	}

	//---------------------------------------------------------------------------
	public ComparisonTracker newComparisonTracker(int df)
	{
		return new ScalarCompTracker();
	}

	//---------------------------------------------------------------------------
	public Result computeStat ( Analysis a, AnalysisTable[] tables, int[] simIndices )
	{
		AnalysisTable t = tables[0];
		AnalysisTable.Totals totals = t.getTotals();

		// Check Row and Column Totals before calculation.
		if ( StatUt.checkRowColSum (totals.forRows()[0],totals.forColumns(),totals.forTable()))
		{
			resultAt0 = new ResultImp.Real(StatUt.chiSquared(t.getRowFor(Ptype.CASE).getCells(),
					totals.forRows()[0],
					totals.forColumns(),
					totals.forTable()));
		}
		else 
		{
			resultAt0 = new ResultImp.StringResult
			("WARNING: row or column sum equals zero, **test** has not been performed.");
		}
		return resultAt0; 
	}

	//---------------------------------------------------------------------------
	public AnalysisTable getTable( Analysis analysis, int studyIndex, int[] simIndices )
	{
		return (AnalysisTable) analysis.getContingencyTable(studyIndex,simIndices); 
	}

}

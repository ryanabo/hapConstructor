//******************************************************************************
// CCStatImp.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.stat.CCAnalysis.Table;

//==============================================================================
public class CCStatImp implements CCStat 
{
	String title = null;
	String statType = "stat";
	//String studyNoData;
	String obsExtraStatTitle = null;
	String infExtraStatTitle = null;
	int    constantJ = 0;
	Result resultAt0, resultAtX, obsExtraStatResult, infExtraStatResult, resultInf = null;
	Vector<Vector<Double>> infExtraSimResult;
	public ComparisonTracker compTracker;
	AnalysisTable newTable = null;

	public String getName()
	{ return title; }

	public String getStatType()
	{ return statType; }

	public String getCompStatTitle()
	{ return "Empirical p-Value : "; }

	public ComparisonTracker newComparisonTracker()
	{ return compTracker; }

	public ComparisonTracker newComparisonTracker(int df) 
	{ return compTracker;}

	public Result computeAt0 ( CCAnalysis a, CCAnalysis.Table t )
	{ return resultAt0; }

	public Result computeAt0 ( CCAnalysis a, CCAnalysis.Table[] t )
	{ return resultAt0; }

	public Result computeAtX ( CCAnalysis a, CCAnalysis.Table t )
	{ return resultAtX; }

	public Result computeAtX ( CCAnalysis a, CCAnalysis.Table[] t )
	{ return resultAtX; }

	public Result getObservedResult (Result resultAt0, int refCol)
	{ return resultAt0; }

	public Result getInferentialResult (ComparisonTracker compTracker,
			int refCol )
	{ return compTracker.getComparisonsResult(); }

	public String getObsExtraStatTitle()
	{ return obsExtraStatTitle; }

	public String getInfExtraStatTitle()
	{ return infExtraStatTitle; }

	public Result getObsExtraStat (Result result)
	{ return obsExtraStatResult; }

	public Result getInfExtraStat (ComparisonTracker compTracker)
	{ return infExtraStatResult; }

	public Vector<Vector<Double>>  getInfSimStat (ComparisonTracker compTracker)
	{ return infExtraSimResult; }

	public AnalysisTable getTable(TableMaker tm)
	{ return newTable; }

	public AnalysisTable getTable(TableMaker tm, Thread p)
	{ return newTable; }

	public AnalysisTable[] getNonZeroTables(Analysis a, AnalysisTable[] t,
			ComparisonTracker comparisonTracker)
	{
		ArrayList<String> studyNoData = new ArrayList<String>();
		ArrayList<AnalysisTable> ltable = new ArrayList<AnalysisTable>();
		
		for ( int i = 0; i < t.length; i++ )
		{
			AnalysisTable.Totals totals = t[i].getTotals();
			if ( totals.forTable().intValue() == 0 ) studyNoData.add(a.getStudyName(i));
			else ltable.add(t[i]);
		}
		
		if ( !studyNoData.isEmpty() )
		{
			String message = new String(); 
			for ( Iterator snd = studyNoData.iterator(); snd.hasNext(); ) message += snd.next() + " ";
			comparisonTracker.setMessages("WARNING - study : " + message + " has no data, not included in this test");
		}
		
		AnalysisTable[] tables = ltable.toArray(new AnalysisTable[0]);
		
		if ( tables.length == 0 )
		{
			System.out.println("All tables are zero ");
			return null;
		}
		else return tables;
	}

	//----------------------------------------------------------------------------
	public int getDegreeOfFreedom( int ncol)
	{ return ncol-1; }

	public Result computeStat(Analysis a, AnalysisTable[] t, int[] simIndices) {
		// TODO Auto-generated method stub
		return null;
	}

	public AnalysisTable getTable(Analysis analysis, int studyIndex, int simIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public AnalysisTable getTable(Analysis analysis, int studyIndex,
			int[] simIndices) {
		// TODO Auto-generated method stub
		return null;
	}

	public Table[] getNonZeroTables(CCAnalysis a, Table[] t,
			ComparisonTracker compTracker) {
		// TODO Auto-generated method stub
		return null;
	}

}

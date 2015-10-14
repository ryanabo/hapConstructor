package edu.utah.med.genepi.analysis;

import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.hapconstructor.analysis.ComboSet;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.stat.CCStat.Result;
import edu.utah.med.genepi.stat.ResultImp;

public class Analysis {

	private ColumnManager columnmanager = null;
	private String tableType = null;
	private int[] studyIndex = null;
	private CCStat stat = null;
	private String model = null;
	private final CCStat.ComparisonTracker compTracker;
	private boolean statFailed = false;
	private CCStat.Result r0Result = null;
	private CCStat.Result observedResult = null;
	private GenieDataSet genieDataSet = null;
	private int[] loci = null;
	private AnalysisTable[] statTables = null;
	
	public Analysis( GenieDataSet gd, ColumnManager cmanager, String itableType, int[] istudyIndex, 
			         CCStat istat, String imodel )
	{
		columnmanager = cmanager;
		tableType = itableType;
		studyIndex = istudyIndex;
		stat = istat;
		model = imodel;
		compTracker = stat.newComparisonTracker();
		genieDataSet = gd;
		loci = columnmanager.getLoci();
	}
	
	//---------------------------------------------------------------------------
	public int[] getColWts(){ return columnmanager.getWts(); }
	
	//---------------------------------------------------------------------------
	public String getAnalysisType(){ return columnmanager.getAnalysisType(); }
	
	//---------------------------------------------------------------------------
	public boolean analyze( int simIndex ){ return analyze(new int[]{simIndex,-1}); }
	
	//---------------------------------------------------------------------------
	public boolean analyze( int[] simIndices )
	{
		// simIndices 0 contains the simIndex for Monte Carlo null distribution 
		// simIndices 1 contains the hapConstructor significanceTesting index
		int nStudies = studyIndex.length;
		AnalysisTable[] tables = new AnalysisTable[nStudies];
		
		for ( int i=0; i < nStudies; i++ )
		{
			AnalysisTable statTable = (AnalysisTable) stat.getTable(this,i,simIndices);
			tables[i] = statTable;
		}
		
		if ( !statFailed )
		{
			CCStat.Result result = stat.computeStat(this,tables,simIndices);
			if ( result.toString().contains("WARNING") || result.toString().equals("-") )
			{
				int x = 0;
			}
			compTracker.setResult(result,simIndices[0]);
			if ( simIndices[0] == 0 )
			{
				statTables = tables;
				r0Result = result;
				if ( result instanceof ResultImp.StringResult || r0Result.toString().equals("-") )
					statFailed = true;
				observedResult = stat.getObservedResult(result,columnmanager.getRefColIndex());
			}
		}
		return statFailed;
	}
	
	//---------------------------------------------------------------------------
	public String getStudyName( int studyIndex ){ return genieDataSet.getStudyName(studyIndex); }
	
	//---------------------------------------------------------------------------
	public int getRefColIndex(){ return columnmanager.getRefColIndex(); }
	
	//---------------------------------------------------------------------------	
	public boolean checkDistinctRefWt(){ return columnmanager.checkDistinctRefWt(); }
	
	//---------------------------------------------------------------------------
	public boolean failed(){ return statFailed; }
	
	//---------------------------------------------------------------------------
	public int[] getLoci(){ return loci; }
	
	//---------------------------------------------------------------------------
	public String getModel(){ return model; }
	
	/** Returns the unit of analysis for the table.
	 * 
	 * @return String value of allele or genotype.
	 */
	//---------------------------------------------------------------------------
	public String getTableType(){ return columnmanager.getType(); }
	
	/** Returns pseudocontrol or explicit control (original)
	 * 
	 * @return String value of how controls are tallied.
	 */
	//---------------------------------------------------------------------------
	public String getTallierType(){ return tableType; }
	
	//---------------------------------------------------------------------------
	public AnalysisTable[] getTables(){ return statTables; }
	
	//---------------------------------------------------------------------------
	public Result getObservedResult(){ return observedResult; }
	
	//---------------------------------------------------------------------------
	public String getStatName(){ return stat.getName(); }
	
	//---------------------------------------------------------------------------
	public Result getComparisonResult(){ return compTracker.getComparisonsResult(); }
	
	//---------------------------------------------------------------------------
	public AnalysisTable getContingencyTable( int studyIndex, int[] simIndices )
	{
		AnalysisTable at = null;
		if ( tableType.equals("pseudo") ) at = genieDataSet.getTable(studyIndex,simIndices,"pseudo",columnmanager);
		//else if ( tableType.equals("x-chr") ) return genieDataSet.getXContingencyTable(); 
		else if ( tableType.equals("original") ) at = genieDataSet.getTable(studyIndex,simIndices,"contingency",columnmanager);
		return at;
	}
	
	//---------------------------------------------------------------------------
	public AnalysisTable getQuantitativeTable( int studyIndex, int[] simIndices )
	{ 
		return genieDataSet.getTable(studyIndex,simIndices,"quantitative",columnmanager); 
	}
	
	//---------------------------------------------------------------------------
	public AnalysisTable getIntxLDTable( int studyIndex, int[] simIndices )
	{ 
		return genieDataSet.getTable(studyIndex,simIndices,"interactionld",columnmanager); 
	}
	
	//---------------------------------------------------------------------------
	public ComboSet getComboSet(){ return columnmanager.getComboSet(); }
	
	//---------------------------------------------------------------------------
	public AnalysisReport[] getReport()
	{
		AnalysisReport report = new AnalysisReport(this);
		return new AnalysisReport[] {report};
	}
	
	//---------------------------------------------------------------------------
	public CCStat.Result getR0Result(){ return r0Result; }
	
	//---------------------------------------------------------------------------
	public CCStat getStat(){ return stat; }
	
	//---------------------------------------------------------------------------
	public CCStat.ComparisonTracker getCompTracker(){ return compTracker; }
	
	
}

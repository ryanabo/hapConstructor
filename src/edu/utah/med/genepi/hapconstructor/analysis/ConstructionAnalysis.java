package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.analysis.ExpandedAnalysis;
import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.hapCResultsManager;

public class ConstructionAnalysis implements ExpandedAnalysis{
	
	private AnalysisFiller aFiller = null;
	private AnalysisFormat aFormat = null;

	public ConstructionAnalysis(AnalysisFiller afiller, AnalysisFormat aformat)
	{
		aFiller = afiller;
		aFormat = aformat;
	}
	
	//----------------------------------------------------------------------------
	public void run( int simIndex, GenieDataSet genieDataSet, hapCResultsManager hapconresults )
	{
		int nStudies = genieDataSet.getNStudy();
		Integer[] stats = aFormat.getStatIndices();
		Integer[] metas = aFormat.getMetaIndices();
		String model = aFormat.getModel();
		String type = aFormat.getType();
		
		for ( int r = 0; r < aFiller.getNMarkerCombos(); r++ ) 
		{
			ColumnManager columnmanager = aFormat.getColumnManager(aFiller,r);
			for ( int s = 0; s < nStudies; s++ ) 
			{
				for (int i = 0; i < stats.length; ++i) 
				{
					int[] studyIndex = new int[] {s};
					Analysis a = new Analysis(genieDataSet,columnmanager,null,
							studyIndex,genieDataSet.getStat(stats[i],false),model);
				}
			}
			for ( int i = 0 ; i < metas.length; i++ ) 
			{
				int[] studyIndices = new int[] {nStudies};
				for ( int ss=0; ss < nStudies; ss++ ) studyIndices[ss] = ss;
				Analysis a = new Analysis(genieDataSet,columnmanager,null,
						studyIndices,genieDataSet.getStat(metas[i],true),model);
				a.analyze(simIndex);
			}
		}
	}
	
	//----------------------------------------------------------------------------
	public void report()
	{
		
	}
}

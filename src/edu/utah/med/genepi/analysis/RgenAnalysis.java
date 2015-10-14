package edu.utah.med.genepi.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.genie.GenieReporter;

public class RgenAnalysis implements ExpandedAnalysis{

	private ColumnManager columnmanager = null;
	private String model = null;
	private int[] stats = null;
	private int[] metas = null;
	private int[][] repeat = null;
	private String[] tableTypes = null;
	private Analysis[] allAnalyses = null;
	
	//---------------------------------------------------------------------------
	public RgenAnalysis(ColumnManager cmanager, String imodel, int[] istats, int[]
	          imetas, int[][] repeatGroup, String[] selected_tabletype)
	{
		columnmanager = cmanager;
		model = imodel;
		stats = istats;
		metas = imetas;
		repeat = repeatGroup;
		tableTypes = selected_tabletype;
	}
	
	//---------------------------------------------------------------------------
	public void run( int simIndex, GenieDataSet genieDataSet )
	{	
		if ( simIndex > 0 )
		{
			for( int i=0; i < allAnalyses.length; i++) allAnalyses[i].analyze(simIndex);
		}
		else
		{
			List<Analysis> analysesList = new ArrayList<Analysis>();

			int nStudies = genieDataSet.getNStudy();
			for ( int r = 0; r < repeat.length; r++ ) 
			{
				updateRepeat(r);
				for ( int t = 0; t < tableTypes.length; t++) 
				{
					for ( int s = 0; s < nStudies; s++ ) 
					{
						for (int i = 0; i < stats.length; ++i) 
						{
							int[] studyIndex = new int[] {s};
							Analysis a = new Analysis(genieDataSet,columnmanager,tableTypes[t],
									studyIndex,genieDataSet.getStat(stats[i],false),model);
							a.analyze(simIndex);
							analysesList.add(a);
						}
					}
					for ( int i = 0 ; i < metas.length; i++ ) 
					{
						int[] studyIndices = new int[nStudies];
						for ( int ss=0; ss < nStudies; ss++ ) studyIndices[ss] = ss;
						Analysis a = new Analysis(genieDataSet,columnmanager,tableTypes[t],
								studyIndices,genieDataSet.getStat(metas[i],true),model);
						a.analyze(simIndex);
						analysesList.add(a);
					}
				}
			}
			allAnalyses = analysesList.toArray(new Analysis[0]);
		}
	}
	
	//---------------------------------------------------------------------------
	public void report( GenieDataSet gs )
	{
		try 
		{
			GenieReporter gReporter = new GenieReporter( gs, allAnalyses );
		} catch (IOException e) 
		{
			e.printStackTrace();
		}		 
	}
	
	//---------------------------------------------------------------------------	
	public Analysis[] getAllAnalyses(){ return allAnalyses; }
	
	//---------------------------------------------------------------------------	
	private void updateRepeat( int repeatIndex ){ columnmanager.updateRepeat(repeatIndex,repeat); }
	
	
}

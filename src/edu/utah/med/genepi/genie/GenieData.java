package edu.utah.med.genepi.genie;

import java.io.IOException;

import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

public class GenieData {

	private StudyData[] sd = null;
	private boolean[] relatedDataSets = null;
	
	public GenieData( GenieParameterData gp, String[][] fileNames ) throws IOException
	{		
	    sd = new StudyData[fileNames.length];
	    relatedDataSets = new boolean[fileNames.length];
	    gp.setLocusMap(fileNames.length);
	    for ( int i=0; i < fileNames.length; i++ )
	    {
	    	sd[i] = new StudyData(fileNames[i],gp,i);
	    	relatedDataSets[i] = sd[i].hasRelatedSubjects();
	    }
	}
	
	//--------------------------------------------------------------------------------
	public boolean anyRelatedDataSets()
	{
		boolean relatedSubjects = false;
		for ( int i=0; i < relatedDataSets.length; i++ )
		{
			if ( relatedDataSets[i] )
			{
				relatedSubjects = true;
				break;
			}
		}
		return relatedSubjects;
	}
	
	//--------------------------------------------------------------------------------
	public String getStudyName( int studyIndex){ return sd[studyIndex].getName(); }
	
	//--------------------------------------------------------------------------------
	public String getDataSourceName( int studyIndex ){ return sd[studyIndex].getDataSourceName(); }
	
	//--------------------------------------------------------------------------------
	public boolean anyUnrelatedDataSets()
	{
		boolean relatedSubjects = false;
		for ( int i=0; i < relatedDataSets.length; i++ )
		{
			if ( relatedDataSets[i] )
			{
				relatedSubjects = true;
				break;
			}
		}
		return relatedSubjects;
	}
	
	//--------------------------------------------------------------------------------
	public AnalysisTable getTable( int studyIndex, int[] simIndices, String table, ColumnManager cm, GenieParameterData gp )
	{ 
		return sd[studyIndex].getTable(simIndices,table,cm,gp); 
	}
	
	//--------------------------------------------------------------------------------
	public int getNStudy(){ return sd.length; }
	
	//--------------------------------------------------------------------------------
	public void setIndShuffle()
	{
		for ( int i=0; i < sd.length; i++ ) sd[i].setIndShuffle();
	}
	
	//--------------------------------------------------------------------------------
	public void simNullData( GenieParameterData gp, boolean relateds, boolean simulateAll )
	{
		for ( int i=0; i < relatedDataSets.length; i++ )
		{
			if ( relatedDataSets[i] == relateds ) sd[i].simNullData(gp,simulateAll);
		}
	}
	
	//--------------------------------------------------------------------------------
	public void loadNullData()
	{
		for ( int i=0; i < sd.length; i++ ) sd[i].loadNullData();
	}
	
	//--------------------------------------------------------------------------------
	public void simNullData(GenieParameterData gp, boolean simulateAll)
	{
		for ( int i=0; i < sd.length; i++ ) sd[i].simNullData(gp,simulateAll);
	}
	
	//--------------------------------------------------------------------------------
	public void phase( GenieParameterData gp, int simIndex )
	{
		for ( int i=0; i < sd.length; i++ ) sd[i].phase(gp,simIndex);
	}
}

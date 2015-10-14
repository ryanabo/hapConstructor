package edu.utah.med.genepi.gm;

import java.util.HashMap;

public class Loci {

	Locus[] loc = null;
	
	//----------------------------------------------------------------------------
	public Loci( int nloci )
	{
		loc = new Locus[nloci];
	}
	
	//----------------------------------------------------------------------------
	public void add_locus( int index, String marker, double distance, boolean ordered, int gene )
	{
		loc[index] = new Locus(index,marker,distance,ordered,gene);
	}
	
	//----------------------------------------------------------------------------
	public void setLocusMap( int nstudies )
	{
		for ( int i=0; i < loc.length; i++ ) loc[i].setAlleleCounts(nstudies);
	}
	
	//----------------------------------------------------------------------------
	public int getCount(){ return loc.length; }
	
	//----------------------------------------------------------------------------
	public Locus getLocus( int index ){ return loc[index]; }
	
	//----------------------------------------------------------------------------
	public void storeLocusMap( HashMap<String,Integer>[] locusMap, int nTotalAlleleCount,int studyIndex )
	{
		for ( int i=0; i < locusMap.length; i++ )
		{
		  loc[i].storeLocusMap(locusMap[i],nTotalAlleleCount,studyIndex);
		}
	}
	
	//----------------------------------------------------------------------------
	public int getAlleleCode( int studyIndex, int locusIndex, String alleleValue )
	{
		return loc[locusIndex].getAlleleCode(studyIndex,alleleValue);
	}
}

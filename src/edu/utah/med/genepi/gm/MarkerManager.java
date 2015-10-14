package edu.utah.med.genepi.gm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarkerManager {

	private Gene[] genes = null;
	
	public MarkerManager( Gene[] g )
	{
		genes = g;
	}
	
	//----------------------------------------------------------------------------
	public int[] getMarkerIndices( int geneIndex )
	{
		return genes[geneIndex].getMarkerIndices();
	}
	
	//----------------------------------------------------------------------------
	public Gene[] getGenes(){ return genes; }
	
	//----------------------------------------------------------------------------
	public void setLocusMap( int nstudies )
	{
		for ( int i=0; i < genes.length; i++ ) genes[i].setLocusMap(nstudies);
	}
	
	//----------------------------------------------------------------------------
	public Locus[] getLoci( int geneId )
	{
		Locus[] loci = null;
		
		if ( geneId == -1 )
		{
			List<Locus> lociLst = new ArrayList<Locus>();
			for ( int i=0; i < genes.length; i++ )
			{
				Locus[] geneLoci = genes[i].getLoci();
				for ( int j = 0; j < geneLoci.length; j++ )
				{
					lociLst.add(geneLoci[j]);
				}
			}
			loci = lociLst.toArray(new Locus[0]);
		}
		else loci = genes[geneId].getLoci();
		return loci;
	}
	
	//----------------------------------------------------------------------------
	public int getLociCount( int geneId )
	{
		int nLoci = 0;
		if ( geneId == -1 )
		{
			for ( int i=0; i < genes.length; i++ ) nLoci += genes[i].getLociCount();			
		}
		else nLoci = genes[geneId].getLociCount();
		return nLoci; 
	}
	
	//----------------------------------------------------------------------------
	public Locus getLocus( int geneId, int index )
	{
		Locus loc = null;
		if ( geneId == -1 )
		{
			// Use index as overall index			
			int sum = 0;
			int lastSum = 0;
			for ( int i=0; i < genes.length; i++ )
			{
				Locus[] loci = genes[i].getLoci();
				sum += loci.length;
				int diff = sum - index;
				if ( diff > 0 )
				{
					int newIndex = index - lastSum;
					loc = loci[newIndex];
					break;
				}
				lastSum = sum;
			}
		}
		else
		{
			// Use index as relative gene index.
			loc = genes[geneId].getLoci()[index];
		}
		return loc; 
	}
	
	//----------------------------------------------------------------------------
	public void storeLocusMap( HashMap<String,Integer>[] locusMap,int studyIndex )
	{
		Locus[] allLoci = getLoci(-1);
		for ( int i=0; i < locusMap.length; i++ )
		{
		  allLoci[i].storeLocusMap(locusMap[i],studyIndex);
		}
	}
	
	//----------------------------------------------------------------------------
	public int getAlleleCode( int studyIndex, int locusIndex, String alleleValue )
	{
		Locus[] allLoci = getLoci(-1);
		return allLoci[locusIndex].getAlleleCode(studyIndex,alleleValue);
	}
}

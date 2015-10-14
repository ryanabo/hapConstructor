package edu.utah.med.genepi.hapconstructor.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.gm.Locus;

public class GeneUnit {
	
	private String geneId = null;
	private MarkerUnit[] markerunits = null;
	
	//---------------------------------------------------------------------------
	public GeneUnit( MarkerUnit[] mu )
	{
		markerunits = mu;
	}
	
	//---------------------------------------------------------------------------
	public GeneUnit( Locus l )
	{
		markerunits = new MarkerUnit[]{new MarkerUnit(l)};
	}
	
	//---------------------------------------------------------------------------
	public int getId( GenieDataSet gd )
	{
		Locus firstLocus = gd.getLocus(-1,markerunits[0].getLoci()[0]);
		return firstLocus.getGeneId();
	}
	
	//---------------------------------------------------------------------------
	public MarkerUnit getMarkerUnit(int index){ return markerunits[index]; }
	
	//---------------------------------------------------------------------------
	public MarkerUnit[] getMarkerUnits(){ return markerunits;}
	
	//---------------------------------------------------------------------------
	public int getNLoci()
	{
		int nloci = 0;
		for ( int i=0; i < markerunits.length; i++) nloci += markerunits[i].getNLoci();
		return nloci;
	}
	
	//---------------------------------------------------------------------------
	public void add( int markerUnitIndex, int index ){ markerunits[markerUnitIndex].add(index); }
	
	//---------------------------------------------------------------------------
	public int remove( int markerUnitIndex, int index )
	{
		MarkerUnit mu = markerunits[markerUnitIndex];
		int[] loci = mu.getLoci();
		int returnCode = 0;
		//System.out.println("Remove " + index + " loci length " + loci.length);
		if ( loci.length > 1 )
		{
			int[] newLoci = new int[loci.length-1];
			int iter = 0;
			for ( int i=0; i < loci.length; i++ )
			{
				if ( loci[i] != index )
				{
					newLoci[iter] = loci[i];
					iter++;
				}
			}
			mu.setLoci(newLoci);
		}
		else returnCode = -1;
		return returnCode;
	}
	
	//---------------------------------------------------------------------------
	public void add( MarkerUnit mu )
	{
		List<MarkerUnit> muLst = new ArrayList<MarkerUnit>(Arrays.asList(markerunits));
		muLst.add(mu);
		markerunits = muLst.toArray(new MarkerUnit[0]);
	}
}

package edu.utah.med.genepi.hapconstructor.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utah.med.genepi.gm.Locus;
import edu.utah.med.genepi.util.Ut;

public class MarkerCombo {

	private GeneUnit[] geneunits = null;
	
	//---------------------------------------------------------------------------
	public MarkerCombo( GeneUnit[] gu )
	{
		geneunits = gu;
	}
	
	//---------------------------------------------------------------------------
	public MarkerCombo ( Locus l )
	{
		//int geneId = l.getGeneId();
		geneunits = new GeneUnit[]{new GeneUnit(l)};
	}
	
	//---------------------------------------------------------------------------
	public MarkerCombo ( Locus l1, Locus l2 )
	{
		geneunits = new GeneUnit[2];
		geneunits[0] = new GeneUnit(l1);
		geneunits[1] = new GeneUnit(l2);
	}
	
	//---------------------------------------------------------------------------
	public String getBufferKey()
	{
		StringBuffer key = new StringBuffer();
		MarkerUnit[] markerunits = getMarkerUnits();
		
		for ( int i=0; i < markerunits.length; i++ )
		{
			key.append("(");
			int[] loci = markerunits[i].getLoci();
			key.append(Ut.array2str(loci,"-"));
			key.append(") ");
		}
		return key.toString();
	}
	
	//---------------------------------------------------------------------------
	public long getLocusAddress()
	{
		long locusAddress = 0;
		int[] loci = getLoci();
		long one = 1;
		for ( int i=0; i < loci.length; i++ ) 
			locusAddress |= (one << (loci[i]));
			//locusAddress |= (1 << (loci[i]));
		return locusAddress;
	}
	
	//---------------------------------------------------------------------------
	public MarkerUnit[] getMarkerUnits()
	{
		List<MarkerUnit> markerunits = new ArrayList<MarkerUnit>();
		for ( int i=0; i < geneunits.length; i++ )
		{
			MarkerUnit[] mus = geneunits[i].getMarkerUnits();
			for ( int j=0; j < mus.length; j++) markerunits.add(mus[j]);
		}
		return markerunits.toArray(new MarkerUnit[0]); 
	}
	
	//---------------------------------------------------------------------------
	public int[] getLoci()
	{
		int nloci = 0;
		for ( int i=0; i < geneunits.length; i++ ) nloci += geneunits[i].getNLoci();
		int[] loci = new int[nloci];
		int iter = 0;
		for ( int i=0; i < geneunits.length; i++ )
		{
			MarkerUnit[] markerunits = geneunits[i].getMarkerUnits();
			for ( int j=0; j < markerunits.length; j++ )
			{
				int[] l = markerunits[j].getLoci();
				for ( int k=0; k < l.length; k++ )
				{
					loci[iter] = l[k];
					iter++;
				}
			}
		}
		return loci;
	}
	
	//---------------------------------------------------------------------------
	public GeneUnit[] getGeneUnits(){ return geneunits; }
	
	//---------------------------------------------------------------------------
	public GeneUnit getGeneUnit( int index ){ return geneunits[index]; }
	
	//---------------------------------------------------------------------------
	public int getOverallMarkerUnitIndex( int geneIndex, int markerIndex )
	{
		int index = 0;
		if ( geneIndex == 0 ) index = markerIndex;
		else
		{			
			int iter = geneunits[0].getMarkerUnits().length; 
			for ( int i=1; i < geneunits.length; i++ )
			{
				if ( i == geneIndex )
				{
					index = iter + markerIndex;
					break;
				}
			}			
		}
		return index;
	}
	
	//---------------------------------------------------------------------------
	public MarkerCombo copy()
	{
		GeneUnit[] guCopy = new GeneUnit[geneunits.length];
		for ( int i=0; i < geneunits.length; i++ )
		{
			MarkerUnit[] markerunits = geneunits[i].getMarkerUnits();
			MarkerUnit[] muCopy = new MarkerUnit[markerunits.length];
			for ( int j=0; j < markerunits.length; j++ )
			{
				int[] loci = markerunits[j].getLoci();
				muCopy[j] = new MarkerUnit(loci);
			}
			guCopy[i] = new GeneUnit(muCopy);
		}
		MarkerCombo mc = new MarkerCombo(guCopy);
		return mc;
	}
	
	//---------------------------------------------------------------------------
	public void remove( ComboAddress ca )
	{
		int code = geneunits[ca.getGeneUnitIndex()].remove(ca.getMarkerUnitIndex(),ca.getIndex());
		if ( code == -1 )
		{
			GeneUnit[] gu = new GeneUnit[geneunits.length-1];
			
			int iter = 0;
			for ( int i=0; i < geneunits.length; i++ )
			{
				//System.out.println("removed geneunit index " + ca.getGeneUnitIndex());
				if ( i != ca.getGeneUnitIndex() )
				{
					gu[iter] = geneunits[i];
					//System.out.println(gu[iter].getMarkerUnits().length);
					iter++;
				}
			}
			geneunits = gu;
		}
	}

	//---------------------------------------------------------------------------
	public void add( ComboAddress ca ) 
	{
		boolean[] unitCreations = ca.getCreations();
		
		if ( unitCreations[0] )
		{
			// Create new geneUnit, append it to end of geneunits
			MarkerUnit[] mu = new MarkerUnit[1];
			mu[0] = new MarkerUnit(new int[]{ca.getIndex()});
			GeneUnit gu = new GeneUnit(mu);
			List<GeneUnit> guLst = new ArrayList<GeneUnit>(Arrays.asList(geneunits));
			guLst.add(gu);
			geneunits = guLst.toArray(new GeneUnit[0]);
		}
		else if ( unitCreations[1] )
		{
			// Create a new markerUnit, append it to end of markerunits within geneunit
			MarkerUnit mu = new MarkerUnit(new int[]{ca.getIndex()});
			geneunits[ca.getGeneUnitIndex()].add(mu);
		}
		else
		{
			// Add index to preexisting geneunit and markerunit.
			geneunits[ca.getGeneUnitIndex()].add(ca.getMarkerUnitIndex(),ca.getIndex());
		}			
	}
}

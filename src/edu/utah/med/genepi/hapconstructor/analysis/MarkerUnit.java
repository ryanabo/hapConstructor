package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.gm.Locus;

public class MarkerUnit {

	private int[] indices = null;
	
	public MarkerUnit( int[] i )
	{
		indices = i;
	}
	
	public MarkerUnit( Locus l )
	{
		indices = new int[]{l.getIndex()};
	}
	
	public int[] getLoci(){ return indices; }
	
	public int getNLoci(){ return indices.length; }
	
	public void add( int index )
	{		
		int[] newindices = new int[indices.length+1];
		int indexShift = 0;		
		for ( int i=0; i < indices.length; i++ )
		{
			if ( indexShift == 0 )
			{
				if ( index < indices[i] )
				{
					newindices[i] = index;
					indexShift = 1;
				}
			}
			newindices[i+indexShift] = indices[i];
		}
		if ( indexShift == 0 ) newindices[newindices.length-1] = index;
		indices = newindices;
	}
	
	public void setLoci( int[] loci )
	{
		indices = loci;
	}
	
}

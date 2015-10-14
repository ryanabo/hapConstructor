package edu.utah.med.genepi.gm;

public class Gene {

	private int geneId = -1;
	private Locus[] loci = null;
	
	public Gene( int gId, Locus[] markers )
	{
		loci = markers;
		geneId = gId;
	}
	
	//----------------------------------------------------------------------------
	public void setLocusMap( int nstudies )
	{
		for ( int i=0; i < loci.length; i++ ) loci[i].setAlleleCounts(nstudies);
	}
	
	//----------------------------------------------------------------------------
	public int getLociCount(){ return loci.length; }
	
	//----------------------------------------------------------------------------
	public Locus[] getLoci(){ return loci; }
	
	//----------------------------------------------------------------------------
	public int[] getMarkerIndices()
	{
		int[] indices = new int[loci.length];
		for ( int i=0; i < loci.length; i++ )
		{
			indices[i] = loci[i].getIndex();
		}
		return indices;
	}
	
}

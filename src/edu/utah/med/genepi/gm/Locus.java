package edu.utah.med.genepi.gm;

import java.util.HashMap;

public class Locus {

	private int index = -1;
	private String name = null;
	private double distance = 0.0;
	private int gene = -1;
	private AlleleCounts[] ac = null;
	
	//----------------------------------------------------------------------------
	public Locus(int positionIndex, int markerId, String marker, double d, boolean ordered, int g)
	{
		index = positionIndex;
		name = marker;
		distance = d;
		gene = g;
	}
	
	//----------------------------------------------------------------------------
	public void setAlleleCounts( int nstudies ){ ac = new AlleleCounts[nstudies]; }
	
	//----------------------------------------------------------------------------
	public int getIndex(){ return index; }
	
	//----------------------------------------------------------------------------
	public void storeLocusMap( HashMap<String,Integer> lMap,int studyIndex )
	{
		ac[studyIndex] = new AlleleCounts();
		ac[studyIndex].storeAlleles(lMap);
	}
	
	//----------------------------------------------------------------------------
	public int getGeneId(){ return gene; }

	//----------------------------------------------------------------------------
	public double[] getFreqs( int studyIndex ){ return ac[studyIndex].getFreqs(); }
	
	//----------------------------------------------------------------------------
	public int getAlleleCode( int studyIndex, String alleleValue ){ return ac[studyIndex].getCode(alleleValue); }
	
	//----------------------------------------------------------------------------
	public double getTheta(){ return distance; }
	
	//----------------------------------------------------------------------------
	public String getName(){ return name; }
	
}

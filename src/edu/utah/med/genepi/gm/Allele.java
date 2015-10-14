package edu.utah.med.genepi.gm;


public class Allele implements Comparable {

	private String allele = null;
	private int count = 0;
	
	public Allele( String a, int c )
	{
		count = c;
		allele = a;
	}
	
	public int compareTo(Object a) 
	{
		int c = ((Allele) a).getCount();
		return this.count - c;
	}
	
	public int getCount(){ return count; }
	
	public String getValue(){ return allele; }
	
}

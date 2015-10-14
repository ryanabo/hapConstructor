package edu.utah.med.genepi.genie;

import java.util.Comparator;

public class Phenotype implements Comparable {

	private int phenotype = 0;
	private int indIndex = -1;
	
	public Phenotype( int p, int index )
	{
		phenotype = p;
		indIndex = index;
	}
	
	public int compareTo(Object phen) 
	{
		int i = ((Phenotype) phen).getIndex();
		return this.indIndex - i;
	}
	
	public int getIndex(){ return indIndex; }
	
	public int getPhenotype(){ return phenotype; }

	public static Comparator PhenotypeComparator = new Comparator() 
	{
		public int compare(Object phen1, Object phen2) {
			int p1 = ((Phenotype) phen1).getPhenotype();
			int p2 = ((Phenotype) phen2).getPhenotype();
			if ( p1 > p2 ) return 1;
			else if ( p1 < p2 ) return -1;
			else return 0;
		}
	};
	
}

package edu.utah.med.genepi.gm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class AlleleCounts {

	HashMap<String,int[]> AlleleMap = new HashMap<String,int[]>();
	int total = 0;
	
	public AlleleCounts(){}
	
	public void storeAlleles( HashMap<String,Integer> alleleMap )
	{
		int nAlleles = alleleMap.keySet().size();
		Allele[] alleles = new Allele[nAlleles];
		
		int iterCount = 0;
		for( Iterator<String> it = alleleMap.keySet().iterator(); it.hasNext(); )
		{
		  String alleleValue = it.next();
		  int alleleCount = alleleMap.get(alleleValue);
		  total += alleleCount;
		  Allele a = new Allele(alleleValue,alleleCount);
		  alleles[iterCount] = a;
		  iterCount += 1;
		}
		Arrays.sort(alleles);
		
		for ( int i=0; i < alleles.length; i++)
		{
			Allele a = alleles[i];
			int[] v = new int[]{alleles.length - i-1, a.getCount()};
			AlleleMap.put(a.getValue(),v);
		}
	}
	
	public int getCode( String alleleValue )
	{ 
		int[] v = AlleleMap.get(alleleValue);
		return v[0]; 
	}
	
	public double[] getFreqs()
	{
		double[] freqs = new double[AlleleMap.keySet().size()];
		int iter = 0;
		for ( Iterator<String> it = AlleleMap.keySet().iterator(); it.hasNext(); )
		{
			String alleleValue = it.next();
			int[] alleleCodes = AlleleMap.get(alleleValue);
			freqs[iter] = (double)alleleCodes[1] / (double)total;
			iter++;
		}
		double[] frev = new double[] {freqs[1],freqs[0]};
		return frev;
	}
}

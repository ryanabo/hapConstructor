package edu.utah.med.genepi.linkageext;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import alun.genio.GeneticDataSource;
import alun.linkage.AffectionStatusLocus;
import alun.linkage.AffectionStatusPhenotype;
import alun.linkage.LinkageIndividual;
import alun.linkage.LinkageLocus;
import alun.linkage.LinkageParameterData;
import alun.linkage.NumberedAlleleLocus;
import alun.linkage.NumberedAllelePhenotype;
import alun.linkage.QuantitativeLocus;
import alun.linkage.QuantitativePhenotype;
import alun.util.IntArray;

public class GCHapDataSource implements GeneticDataSource {

	private LinkageParameterData linkageParams = null;
	private LinkageIndividual[] linkageInds = null;
	private Map<IntArray,double[][]> naps = null;
	
	public GCHapDataSource( LinkageParameterData lp, LinkageIndividual[] individuals )
	{
		linkageParams = lp;
		linkageInds = individuals;
		naps = new LinkedHashMap<IntArray,double[][]>();
	}
	
	public GCHapDataSource( GCHapDataSource ds, int[] x )
	{
		LinkageIndividual[] inds = ds.getIndividuals();
		linkageParams = new LinkageParameterData(ds.getLinkageParams(),x);
		linkageInds = new LinkageIndividual[inds.length];
		for ( int i=0; i < inds.length; i++ ) linkageInds[i] = new LinkageIndividual(inds[i],x);
	}
	
	public LinkageParameterData getLinkageParams(){ return linkageParams; }
	
	public LinkageIndividual[] getIndividuals(){ return linkageInds; }

	public int indAllele(int i, int j, int k)
	{
		if (!(linkageParams.getLoci()[j] instanceof NumberedAlleleLocus))
		{
			double[][] p = penetrance(i,j);
			if (p == null)
				return 0;
			int count = 0;
			int s = 0;
			int t = 0;
			for (int ii=0; ii<p.length; ii++)
				for (int jj=0; jj<=ii; jj++)
					if (p[ii][jj] > 0)
					{
						count ++;
						s = ii;
						t = jj;
					}
	
			if (count != 1)
				return -1;
			else
				return k == 0 ? s+1 : t+1;
		}
		
		LinkageIndividual x = linkageInds[i];
		NumberedAllelePhenotype y = (NumberedAllelePhenotype)(x.getPhenotypes()[j]);
		if (k == 0)
			return y.a1-1;
		else if (k == 1)
			return y.a2-1;
		else 
			return -1;
	}

	public String call(int i,int j)
	{
		return linkageInds[i].getPhenotypes()[j].toString();
	}

	public int nLoci()
	{
		return linkageParams.nLoci();
	}

	public int nAlleles(int j)
	{
		return alleleFreqs(j).length;
	}

	public int nPhenotypes(int j)
	{
		LinkageLocus l = linkageParams.getLoci()[j];
		if (l instanceof NumberedAlleleLocus)
		{
			int a = nAlleles(j);
			return (a*(a-1))/2;
		}
		else if (l instanceof AffectionStatusLocus)
		{
			return 2;
		}
		else if (l instanceof QuantitativeLocus)
		{
			return 1;
		}
		else
			return 0;
	}

	public double[] alleleFreqs(int j)
	{
		LinkageLocus[] locs = linkageParams.getLoci();
		LinkageLocus ll = locs[j];
		return ll.alleleFrequencies();
	}

/** 
	Returns the recombination fraction between the ith and jth loci.
*/
	public double getMaleRecomFrac(int i, int j)
	{
		return linkageParams.maleTheta(i,j);
	}

	public double getFemaleRecomFrac(int i, int j)
	{
		return linkageParams.femaleTheta(i,j);
	}

	public String name()
	{
		return "GCHap Data source";
	}

	public int nIndividuals()
	{
		return linkageInds.length;
	}

	public String indComment(int i)
	{
		return linkageInds[i].comment;
	}

	public String id(int i)
	{
		LinkageIndividual x = linkageInds[i];
		return x.pedid+" "+x.id;
	}

	public int proband(int i)
	{
		LinkageIndividual x = linkageInds[i];
		return x.proband;
	}

	public int nProbands()
	{
		int n = 0;
		LinkageIndividual[] x = linkageInds;
		for (int i=0; i<x.length; i++)
			if (x[i].proband == 1)
				n++;
		return n;
	}

	public String locName(int j)
	{
		return linkageParams.getLoci()[j].locName();
	}

	public int[] canonicalOrder()
	{
		Set<Integer> s = new LinkedHashSet<Integer>();
		Set<Integer> t = new LinkedHashSet<Integer>();
		
		for (int i=0; i<nIndividuals(); i++)
			s.add(new Integer(i));

		Integer ff = new Integer(-1);
		t.add(ff);

		while (!s.isEmpty())
		{
			for (Integer i : s)
			{
				int p = new Integer(pa(i.intValue()));
				int m = new Integer(ma(i.intValue()));
				if (t.contains(p) && t.contains(m))
					t.add(i);
			}

			s.removeAll(t);
		}

		t.remove(ff);

		int[] x = new int[t.size()];
		int n = 0;
		for (Integer i : t)
			x[n++] = i.intValue();
		
		return x;
	}

	public double[][] penetrance(int i, int j)
	{
		LinkageLocus l = linkageParams.getLoci()[j];
		if (l instanceof NumberedAlleleLocus)
		{
			NumberedAllelePhenotype nap = (NumberedAllelePhenotype) linkageInds[i].pheno[j];
			int p = nap.a1-1;
			int q = nap.a2-1;
			return napTable(nAlleles(j),p,q);
		}
		else if (l instanceof AffectionStatusLocus)
		{
			AffectionStatusLocus lal = (AffectionStatusLocus) l;
			AffectionStatusPhenotype asp = (AffectionStatusPhenotype) linkageInds[i].pheno[j];
/*
			double[] f = lal.liab[asp.liability-1];
*/
			double[] f = null;
			if (asp.status > 0)
				f = lal.liab[asp.liability-1];
			return affTable(nAlleles(j),asp.status,f);
		}
		else if (l instanceof QuantitativeLocus)
		{
			QuantitativeLocus q = (QuantitativeLocus) l;
			QuantitativePhenotype p = (QuantitativePhenotype) linkageInds[i].pheno[j];
			return q.densityOf(p.v);
		}

		return null;
	}

	private double[][] affTable(int na, int stat, double[] f)
	{
		if (stat == 0)
			return null;

		double[][] x = new double[na][];
		for (int i=0; i<x.length; i++)
			x[i] = new double[na];

		switch (stat)
		{
		case 0:
			for (int i=0; i<x.length; i++)
				for (int j=0; j<x[i].length; j++)
					x[i][j] = 1;
			break;
		case 1:
			for (int i=0; i<x.length; i++)
				for (int j=0; j<x[i].length; j++)
					x[i][j] = 1-f[i+j];
			break;
		case 2:
			for (int i=0; i<x.length; i++)
				for (int j=0; j<x[i].length; j++)
					x[i][j] = f[i+j];
			break;
		}

		return x;
	}

	private double[][] napTable(int na, int p, int q)
	{
		if (p < 0  && q < 0)
			return null;

		IntArray a = new IntArray(na,p,q);
		double[][] x = (double[][])naps.get(a);

		if (x == null)
		{
			x = new double[na][];
			for (int i=0; i<x.length; i++)
				x[i] = new double[na];

			if (p < 0)
			{
				if (q < 0)
					for (int i=0; i<x.length; i++)
						for (int j=0; j<x[i].length; j++)
							x[i][j] = 1;
				else
					for (int i=0; i<x.length; i++)
						x[i][q] = x[q][i] = 1;
			}
			else
			{
				if (q < 0)
					for (int i=0; i<x.length; i++)
						x[i][p] = x[p][i] = 1;
				else
					x[p][q] = x[q][p] = 1;
			}

			naps.put(a,x);
		}

		return x;
	}

/*
 From old Genetic data interface.
*/
	public String toPhase()
	{
		StringBuffer s = new StringBuffer();

		s.append(nIndividuals());
		s.append("\n");
		s.append(nLoci());
		s.append("\n");
		for (int i=0; i<nLoci(); i++)
			s.append(nAlleles(i) == 2 ? "S" : "M");
		s.append("\n");

		for (int i=0; i<nIndividuals(); i++)
		{
			s.append("#"+i);
			s.append("\n");
			for (int k=0; k<2; k++)
			{
				for (int j=0; j<nLoci(); j++)
				{
					s.append(" ");
					if (indAllele(i,j,k) == -1)
						s.append( nAlleles(j) == 2 ? "?" : "-1" );
					else
						s.append(indAllele(i,j,k));
				}
				s.append("\n");
			}
		}

		s.deleteCharAt(s.length()-1);
		return s.toString();
	}

	public String toFastPhase()
	{
		String space = " ";
		StringBuffer s = new StringBuffer();

		s.append(nIndividuals());
		s.append("\n");
		s.append(nLoci());
		s.append("\n");
		for (int i=0; i<nLoci(); i++)
		{
			if (nAlleles(i) != 2)
				System.err.println("Warning: Locus "+i+" is not diallelic. Allele numbers > 1 will be set to 1");
			s.append("S");
		}
		s.append("\n");

		for (int i=0; i<nIndividuals(); i++)
		{
			s.append("# id "+i);
			s.append("\n");
			for (int k=0; k<2; k++)
			{
				for (int j=0; j<nLoci(); j++)
				{
					int allele = indAllele(i,j,k);
					s.append(space);
					s.append( allele == -1 ? "?" : ( allele > 1 ? 1 : allele ) );
				}
				s.append("\n");
			}
		}

		s.deleteCharAt(s.length()-1);
		return s.toString();
	}

	public int[] kids(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public int ma(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[][] nuclearFamilies() {
		// TODO Auto-generated method stub
		return null;
	}

	public int pa(int i) {
		// TODO Auto-generated method stub
		return 0;
	}
}

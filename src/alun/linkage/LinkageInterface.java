package alun.linkage;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import alun.genio.Family;
import alun.genio.GeneticDataSource;
import alun.util.IntArray;
//import java.util.Hashtable;

public class LinkageInterface implements GeneticDataSource
{
	public LinkageInterface(LinkageDataSet l)
	{
		L = l;
		naps = new LinkedHashMap<IntArray,double[][]>();
	}

	public int indAllele(int i, int j, int k)
	{
		if (!(L.getParameterData().getLoci()[j] instanceof NumberedAlleleLocus))
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
		
		LinkageIndividual x = L.getPedigreeData().getIndividuals()[i];
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
		return L.getPedigreeData().getIndividuals()[i].getPhenotypes()[j].toString();
	}

	public int nLoci()
	{
		return L.getParameterData().nLoci();
	}

	public int nAlleles(int j)
	{
		return alleleFreqs(j).length;
	}

	public int nPhenotypes(int j)
	{
		LinkageLocus l = L.getParameterData().getLoci()[j];
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
		LinkageParameterData p = L.getParameterData();
		LinkageLocus[] locs = p.getLoci();
		LinkageLocus ll = locs[j];
		return ll.alleleFrequencies();
	}

/** 
	Returns the recombination fraction between the ith and jth loci.
*/
	public double getMaleRecomFrac(int i, int j)
	{
		return L.getParameterData().maleTheta(i,j);
	}

	public double getFemaleRecomFrac(int i, int j)
	{
		return L.getParameterData().femaleTheta(i,j);
	}

	public String name()
	{
		return L.name();
	}

	public int nIndividuals()
	{
		return L.getPedigreeData().getIndividuals().length;
	}

	public String indComment(int i)
	{
		return L.getPedigreeData().getIndividuals()[i].comment;
	}

	public int pa(int i)
	{
		LinkageIndividual x = L.getPedigreeData().getIndividuals()[i];
		x = (LinkageIndividual)L.getPedigreeData().getPedigree().getTriplet(x).y;
		return x == null ? -1 : x.index;
	}

	public int ma(int i)
	{
		LinkageIndividual x = L.getPedigreeData().getIndividuals()[i];
		x = (LinkageIndividual)L.getPedigreeData().getPedigree().getTriplet(x).z;
		return x == null ? -1 : x.index;
	}

	public String id(int i)
	{
		LinkageIndividual x = L.getPedigreeData().getIndividuals()[i];
		return x.pedid+" "+x.id;
	}

	public int proband(int i)
	{
		LinkageIndividual x = L.getPedigreeData().getIndividuals()[i];
		return x.proband;
	}

	public int nProbands()
	{
		int n = 0;
		LinkageIndividual[] x = L.getPedigreeData().getIndividuals();
		for (int i=0; i<x.length; i++)
			if (x[i].proband == 1)
				n++;
		return n;
	}

	public String locName(int j)
	{
		return L.getParameterData().getLoci()[j].locName();
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

	public int[][] nuclearFamilies()
	{
		Family[] f = L.getPedigreeData().getPedigree().nuclearFamilies();
		int[][] n = new int[f.length][];
		for (int i=0; i<n.length; i++)
		{
			LinkageIndividual pa = (LinkageIndividual) f[i].getPa();
			LinkageIndividual ma = (LinkageIndividual) f[i].getMa();
			Object[] k = f[i].getKids();
			n[i] = new int[k.length+2];
			n[i][0] = pa == null ? -1 : pa.index;
			n[i][1] = ma == null ? -1 : ma.index;
			for (int j=0; j<k.length; j++)
				n[i][j+2] =  ((LinkageIndividual)k[j]).index;
	}
	
		return n;
	}

	public int[] kids(int i)
	{
		LinkageIndividual x = L.getPedigreeData().getIndividuals()[i];
		Object[] kk = L.getPedigreeData().getPedigree().kids(x);
		int[] kkk = new int[kk.length];
		for (int j=0; j<kkk.length; j++)
			kkk[j] = ((LinkageIndividual)kk[j]).index;
		return kkk;
	}

	public double[][] penetrance(int i, int j)
	{
		LinkageLocus l = L.getParameterData().getLoci()[j];
		if (l instanceof NumberedAlleleLocus)
		{
			NumberedAllelePhenotype nap = (NumberedAllelePhenotype) L.getPedigreeData().getIndividuals()[i].pheno[j];
			int p = nap.a1-1;
			int q = nap.a2-1;
			return napTable(nAlleles(j),p,q);
		}
		else if (l instanceof AffectionStatusLocus)
		{
			AffectionStatusLocus lal = (AffectionStatusLocus) l;
			AffectionStatusPhenotype asp = (AffectionStatusPhenotype) L.getPedigreeData().getIndividuals()[i].pheno[j];
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
			QuantitativePhenotype p = (QuantitativePhenotype) L.getPedigreeData().getIndividuals()[i].pheno[j];
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


// Private data.

	private LinkageDataSet L = null;
	private Map<IntArray,double[][]> naps = null;

/**
 Test main.
*/
	public static void main(String[] args)
	{
		try
		{
			LinkageDataSet l = new LinkageDataSet(args[0],args[1]);
			LinkageInterface d = new LinkageInterface(l);
	
			System.out.println("# Loci = "+d.nLoci());
			for (int i=0; i<d.nLoci(); i++)
			{
				System.out.print(d.nAlleles(i)+": ");
				for (int j=0; j<d.nAlleles(i); j++)
					System.out.print(" "+d.alleleFreqs(i)[j]);
				System.out.println();
			}

			for (int i=1; i<d.nLoci(); i++)
			{
				System.out.print(" "+d.getMaleRecomFrac(i,i-1));
			}
			System.out.println();

			System.out.println("# Individuals = "+d.nIndividuals());
			for (int i=0; i<d.nIndividuals(); i++)
			{
				System.out.println(i+" "+d.pa(i)+" "+d.ma(i));
				for (int j=0; j<d.nLoci(); j++)
				{
					double[][] p = d.penetrance(i,j);
					for (int x=0; x<p.length; x++)
					{
						for (int y=0; y<p[x].length; y++)
							System.out.print(" "+p[x][y]);
						System.out.print(" :");
					}
					System.out.println();
				}
			}

			System.out.println();
			System.out.println();
			int[][] fams = d.nuclearFamilies();
			for (int i=0; i<fams.length; i++)
			{
				for (int j=0; j<fams[i].length; j++)
					System.out.print(" "+fams[i][j]);
				System.out.println();
			}
		}
		catch (Exception e)
		{
			System.err.println("Caught in LinkageInterface:main()");
			e.printStackTrace();
		}
	}
}

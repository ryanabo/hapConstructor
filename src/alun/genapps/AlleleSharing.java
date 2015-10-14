package alun.genapps;

import alun.genio.GeneticDataSource;

public class AlleleSharing
{
	public static int[] hetSharing(GeneticDataSource x)
	{
		int[] probs = probands(x);
		int[] s = new int[x.nLoci()];
			
		for (int i=0; i<s.length; i++)
		{
			int[] c = new int[x.nAlleles(i)];
			
			for (int j=0; j<probs.length; j++)
			{
				int a = x.indAllele(probs[j],i,0);
				int b = x.indAllele(probs[j],i,1);
				
				if (a < 0 || b < 0)
					for (int k=0; k<c.length; k++)
						c[k]++;
				else if (a == b)
					c[a]++;
				else
				{
					c[a]++;
					c[b]++;
				}
			}

			s[i] = 0;
			for (int k=0; k<c.length; k++)
				if (s[i] < c[k])
					s[i] = c[k];
		}

		return s;
	}

	public static int[] homSharing(GeneticDataSource x)
	{
		int[] probs = probands(x);
		int[] s = new int[x.nLoci()];
			
		for (int i=0; i<s.length; i++)
		{
			int[] c = new int[x.nAlleles(i)];
			
			for (int j=0; j<probs.length; j++)
			{
				int a = x.indAllele(probs[j],i,0);
				int b = x.indAllele(probs[j],i,1);
				
				if (a < 0 || b < 0)
					for (int k=0; k<c.length; k++)
						c[k]++;
				else if (a == b)
					c[a]++;
			}

			s[i] = 0;
			for (int k=0; k<c.length; k++)
				if (s[i] < c[k])
					s[i] = c[k];
		}

		return s;
	}

	public static int[] homozygotes(GeneticDataSource x)
	{
		int[] probs = probands(x);

		int[] s = new int[x.nLoci()];
		for (int i=0; i<s.length; i++)
		{
			for (int j=0; j<probs.length; j++)
			{
				int a = x.indAllele(probs[j],i,0);
				int b = x.indAllele(probs[j],i,1);
				if (a < 0 || b < 0 || a == b)
					s[i]++;
			}
		}

		return s;
	}

	private static int[] probands(GeneticDataSource x)
	{
		int[] probs = new int[x.nProbands()];
		int np = 0;
		for (int i=0; i<x.nIndividuals(); i++)
			if (x.proband(i) == 1)
				probs[np++] = i;

		return probs;
	}
			
	public static int[] runs(int[] s, int n)
	{
		int[] u = new int[s.length];
		if (s[0] >= n)
			u[0] = 1;
		for (int i=1; i<s.length; i++)
			if (s[i] >= n)
				u[i] = u[i-1] + 1;

		int[] v = new int[s.length];
		if (s[s.length-1] >= n)
			v[s.length-1] = 1;
		for (int i=s.length-2; i>=0; i--)
			if (s[i] >= n)
				v[i] = v[i+1] +1;

		for (int i=0; i<s.length; i++)
		{
			u[i] = u[i] +v[i]-1;
			if (u[i] < 0)
				u[i] = 0;
		}

		return u;
	}

	public static int max(int[] x)
	{
		int m = 0;
		for (int i=0; i<x.length; i++)
			if (m < x[i])
				m = x[i];
		return m;
	}
}

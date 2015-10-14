package alun.genapps;

import java.util.Vector;

import alun.genepi.Genotype;
import alun.genio.GeneticDataSource;
import alun.markov.Function;
import alun.util.StringFormatter;

public class FindErrorProbs extends ObligatoryErrors
{
	public static void outputError(GeneticDataSource d, int pi, int lj, int k, double prob, Genotype g, Function post)
	{
		double repthresh = 0.95;
		StringFormatter fm = new StringFormatter();
		System.out.println("\t\t Individual (#"+(1+k)+") "+d.id(k));
		System.out.println("\t\t Observation = "+d.call(k,lj));
		System.out.println("\t\t P(Error) = "+fm.format(prob,1,3));
		System.out.println("\t\t Probable states (paternal allele,maternal allele):");

		double[] x = new double[g.getNStates()];
		Vector<String> v = new Vector<String>();
		int ii = 0;
		for (g.init(); g.next(); ii++)
		{
			x[ii] = post.getValue();
			v.add((1+g.pat())+","+(1+g.mat()));
			for (int jj=ii; jj > 0; jj--)
			{
				if (x[jj] > x[jj-1])
				{
					double t = x[jj];
					x[jj] = x[jj-1];
					x[jj-1] = t;
					String s = v.get(jj);
					v.set(jj,v.get(jj-1));
					v.set(jj-1,s);
				}
			}
		}

		double tot = 0;
		for (ii=0; ii<x.length && tot < repthresh; ii++)
		{
			tot += x[ii];
			System.out.println("\t\t\t"+"P("+v.get(ii)+") = "+fm.format(x[ii],1,3));
		}
	}

	public static void outputErrorUnordered(GeneticDataSource d, int pi, int lj, int k, double prob, Genotype g, Function post)
	{
		double repthresh = 0.95;
		StringFormatter fm = new StringFormatter();
		System.out.println("\t\t Individual (#"+(1+k)+") "+d.id(k));
		System.out.println("\t\t Observation = "+d.call(k,lj));
		System.out.println("\t\t P(Error) = "+fm.format(prob,1,3));
		System.out.println("\t\t Probable genotypes:");

		MyResult[][] res = new MyResult[d.nAlleles(lj)][d.nAlleles(lj)];
		Vector<MyResult> v = new Vector<MyResult>();
		for (int i=0; i<res.length; i++)
			for (int j=0; j<=i; j++)
			{
				res[i][j] = new MyResult(i,j);
				v.add(res[i][j]);
			}

		for (g.init(); g.next(); )
		{
			int i = g.pat();
			int j = g.mat();
			if (j > i)
			{
				int kk = j;
				j = i;
				i = kk;
			}
			res[i][j].x += post.getValue();
		}
		
		for (int i=0; i<v.size(); i++)
			for (int j=i; j>0; j--)
				if (v.get(j).x > v.get(j-1).x)
				{
					
					MyResult t = v.get(j);
					v.set(j,v.get(j-1));
					v.set(j-1,t);
				}

		double tot = 0;
		for (int i=0; i<v.size() && tot < repthresh; i++)
		{
			tot += v.get(i).x;
			System.out.println("\t\t\t"+"P("+v.get(i).s+") = "+fm.format(v.get(i).x,1,3));
		}
	}
}

class MyResult
{
	public MyResult(int i, int j)
	{
		s = (1+j)+","+(1+i);
	}

	public String s = null;
	public double x = 0;
}

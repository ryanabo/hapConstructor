package alun.genepi;

import alun.markov.GraphicalModel;
import alun.markov.Product;
import alun.util.Curve;

public class MaxLoder implements Curve
{
	public MaxLoder(LociVariables l, int j)
	{
		this(array(l),j);
	}

	public MaxLoder(LociVariables[] l, int j)
	{
		this(l,0,j);
	}

	public MaxLoder(LociVariables[] l, int k, int j)
	{
		reclaim = l.length > 1;

		mtheta = new OneTheta();
		mtheta.fix(0.5);
		ftheta = new OneTheta();
		ftheta.fix(0.5);

		lhalf = 0;
		lhalfs = new double[l.length];

		m = new GraphicalModel[l.length];

		for (int i=0; i<m.length; i++)
		{
			Product p = l[i].unrelatedLocusProduct(j);
			m[i] = new GraphicalModel(p);
			m[i].allocateOutputTables();
			m[i].allocateInvolTables();
			m[i].reduceStates();
			m[i].clearInvolTables();
			m[i].clearOutputTables();
	
			p = l[i].locusProduct(j);
			m[i] = new GenotypeModel(p);
			m[i].allocateOutputTables();
			m[i].allocateInvolTables();
			m[i].reduceStates();
			m[i].clearInvolTables();
			m[i].clearOutputTables();
				
			p = l[i].twoPointProduct(k,j,mtheta,ftheta);
			p.triangulate();
			m[i] = new GenotypeModel(p);
			m[i].allocateOutputTables();

			lhalfs[i] = m[i].logPeel();
			lhalf += lhalfs[i];

			if (reclaim)
				m[i].clearOutputTables();
		}
	}

	private static LociVariables[] array(LociVariables l)
	{
		LociVariables[] out = {l};
		return out;
	}

	public double[][] hetLods(double[] alphas, double[] thetas)
	{
		double[][] hlod = new double[alphas.length][thetas.length];
		for (int ped=0; ped < m.length; ped++)
		{
			double[][] pedhl = hetLods(alphas,thetas,ped);

			for (int i=0; i<hlod.length; i++)
				for (int j=0; j<hlod[i].length; j++)
					hlod[i][j] += pedhl[i][j];

		}
		return hlod;
	}


	public double[][] hetLods(double[] alphas, double[] thetas, int ped)
	{
		double[][] hlod = new double[alphas.length][thetas.length];
		double[] lod = f(thetas,ped);

		for (int i=0; i<hlod.length; i++)
			for (int j=0; j<hlod[i].length; j++)
				hlod[i][j] = Math.log10( alphas[i] * Math.pow(10,lod[j]) + (1-alphas[i]) );

		return hlod;
	}

	public double f(double x)
	{
		return f(x,x);
	}

	public double[] f(double[] x)
	{
		double[] lod = new double[x.length];
		for (int i=0; i<x.length; i++)
			lod[i] = f(x[i]);
		return lod;
	}

	public double f(double mrec, double frec)
	{
		mtheta.fix(mrec);
		ftheta.fix(frec);
		double y = 0;
		for (int i=0; i<m.length; i++)
		{
			if (reclaim)
				m[i].allocateOutputTables();
			
			y += m[i].logPeel();

			if (reclaim)
				m[i].clearOutputTables();
		}
		y = (y - lhalf)/log10;
		return y;
	}

	public double[] f(double[] x, int ped)
	{
		double[] lod = new double[x.length];
		for (int i=0; i<x.length; i++)
			lod[i] = f(x[i],x[i],ped);
		return lod;
	}

	public double f(double x, int ped)
	{
		return f(x,x,ped);
	}

	public double f(double mrec, double frec, int i)
	{
		mtheta.fix(mrec);
		ftheta.fix(frec);

		double y = 0;
		if (reclaim)
			m[i].allocateOutputTables();
		y += m[i].logPeel();
		if (reclaim)
			m[i].clearOutputTables();

		y = (y-lhalfs[i])/log10;
		return y;
	}

	private double log10 = Math.log(10);
	private boolean reclaim = false;
	private double lhalf = 0;
	private double[] lhalfs = null;
	private GraphicalModel[] m = null;
	private OneTheta mtheta = null;
	private OneTheta ftheta = null;
}

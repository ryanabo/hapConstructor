package alun.linkage;

import java.io.IOException;

import alun.util.StringFormatter;

public class QuantitativeLocus extends LinkageLocus
{
	public QuantitativeLocus(LinkageFormatter b) throws IOException
	{
		type = QUANTITATIVE_VARIABLE;

		int na = b.readInt("number of alleles",0,true,true);
/*
		if (na != 2)
			b.crash("Quantitative variables are only implemented for diallelic traits");
*/

		freq = new double[na];
		line1comment = b.restOfLine();

		b.readLine();
		for (int i=0; i<freq.length; i++)
		{
			freq[i] = b.readDouble("frequency "+(i+1),0,true,true);
			if (freq[i] < 0)
				b.crash("Negative allele frequency "+freq[i]);
		}
		line2comment = b.restOfLine();

		b.readLine();
		int nvars = b.readInt("number of quantitative variables",0,true,true);
		line3comment = b.restOfLine();

		int ng = freq.length;
		ng = (ng*(ng+1))/2;

		comment = new String[nvars];
		mean = new double[ng][nvars];
		covar = new double[nvars][nvars];
		
		for (int i=0; i<nvars; i++)
		{
			b.readLine();
			for (int j=0; j<ng; j++)
				mean[j][i] = b.readDouble("genotype mean "+(i+1)+" "+(j+1),0,true,true);
			comment[i] = b.restOfLine();
		}

		b.readLine();
		for (int i=0; i<nvars; i++)
		{
			for(int j=i; j<nvars; j++)
			{
				covar[i][j] = b.readDouble("variance/covariance "+(i+1)+" "+(j+1),0,true,true);
				covar[j][i] = covar[i][j];
			}
		}
		line4comment = b.restOfLine();

		b.readLine();
		mult = b.readDouble("hetrozygous var/covar multiplier",1,true,false);
		line5comment = b.restOfLine();

		makeInverse(b);
	}

	public QuantitativeLocus(QuantitativeLocus a)
	{
		super(a);

		type = QUANTITATIVE_VARIABLE;
		mean = new double[a.mean.length][a.mean[0].length];
		for (int i=0; i<mean.length; i++)
			for (int j=0; j<mean[i].length; j++)
				mean[i][j] = a.mean[i][j];

		covar = new double[a.covar.length][a.covar[0].length];
		for (int i=0; i<covar.length; i++)
			for (int j=0; j<covar[i].length; j++)
				covar[i][j] = a.covar[i][j];

		inver = new double[a.inver.length][a.inver[0].length];
		for (int i=0; i<inver.length; i++)
			for (int j=0; j<inver[i].length; j++)
				inver[i][j] = a.inver[i][j];

		determinant = a.determinant;
		mult = a.mult;

		line2comment = a.line2comment;
		line3comment = a.line3comment;
		line4comment = a.line4comment;
		line5comment = a.line5comment;
		comment = new String[a.comment.length];
		for (int i=0; i<comment.length; i++)
			comment[i] = a.comment[i];
	}

/**
 Reads the data for a phenotype at this locus from the given input
 formatter.
*/
	public LinkagePhenotype readPhenotype(LinkageFormatter f)
	{
		double[] vars = new double[mean[0].length];
		for (int i=0; i<vars.length; i++)
			vars[i] = f.readDouble("quantitative variable",0,true,false);

		return new QuantitativePhenotype(this,vars);
	}

	public double[][] densityOf(double[] v)
	{
		for (int i=0; i<v.length; i++)
			if (Math.abs(v[i]) < 0.000000001)
				return null;

		int nvar = mean[0].length;
		double con = Math.pow(2*Math.PI,nvar/2.0) * Math.sqrt(Math.abs(determinant)) ;

		double[][] q = new double[freq.length][freq.length];
		int k = 0;
		for (int i=0; i<q.length; i++)
			for (int j=i; j<q[i].length; j++)
			{
				double[] mu = mean[k];
				double mul = ( i == j ? 1 : mult );

				double x = 0;
				for (int a=0; a<nvar; a++)
					for (int b=0; b<nvar; b++)
						x += (v[a]-mu[a]) * covar[a][b] * (v[b]-mu[b]);

				q[i][j] =  Math.exp(- x / 2 * mul) / con / Math.pow(mul,nvar/2.0);
				q[j][i] = q[i][j];
				k++;
			}

		double z = q[0][0];
		for (int i=0; i<q.length; i++)
			for (int j=0; j<q[i].length; j++)
				q[i][j] /= z;

		return q;
	}

	public double[][] mean = null;
	public double[][] covar = null;
	public double[][] inver = null;
	public double determinant = 0;
	public double mult = 0;

	public String line2comment = null;
	public String line3comment = null;
	public String line4comment = null;
	public String line5comment = null;
	public String comment[] = null;

	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append(type+" "+freq.length+" "+line1comment+"\n");

		for (int i=0; i<freq.length; i++)
			s.append(StringFormatter.format(freq[i],2,6)+" ");
		s.append(line2comment+"\n");

		s.append(mean[0].length+" "+line3comment+"\n");
		for (int i=0; i<mean[0].length; i++)
		{
			for (int j=0; j<mean.length; j++)
				s.append(StringFormatter.format(mean[j][i],4,6)+" ");
			s.append(comment[i]+"\n");
		}
		
		for (int i=0; i<covar.length; i++)
			for (int j=i; j<covar[i].length; j++)
				s.append(StringFormatter.format(covar[i][j],4,6)+" ");
		s.append(line4comment+"\n");
		s.append(mult+" "+line5comment+"\n");

		return s.toString();
	}

	private void makeInverse(LinkageFormatter b)
	{
		double[][] x = new double[covar.length][2*covar.length];
		for (int i=0; i<x.length; i++)
		{
			for (int j=0; j<covar.length; j++)
				x[i][j] = covar[i][j];
			x[i][i+covar.length] = 1;
		}

		for (int i=0; i<x.length; i++)
		{
			if (x[i][i] == 0)
				b.crash("Covariance matrix is not invertible");

			for (int j=0; j<x.length; j++)
				if (j != i)
				{
					double z = x[j][i]/x[i][i];
					for (int k=0; k<x[j].length; k++)
						x[j][k] -= x[i][k]*z;
				}
		}

		determinant = 1;
		for (int i=0; i<x.length; i++)
		{
			double z = x[i][i];
			for (int j=0; j<x[i].length; j++)
				x[i][j] /= z;
			determinant *= z;
		}

		inver = new double[covar.length][covar.length];
		for (int i=0; i<inver.length; i++)
			for (int j=0; j<inver[i].length; j++)
				inver[i][j] = x[i][j+inver.length];
	}

}

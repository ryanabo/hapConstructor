package alun.gchap;

import alun.util.StringFormatter;

/**
 This classes represents a genetic locus.
*/
public class Locus
{
/**
 Creates a new locus with the given number of alleles.
 Uses default names and frequencies.
*/
	public Locus(int n)
	{
		this(defaultFrequencies(n),defaultNames(n));
	}

/**
 Creates a new locus with an allele for each specified frequency.
 Uses default names.
*/
	public Locus(double[] f)
	{
		this(f,defaultNames(f.length));
	}

/**
 Creates a new locus with an allele for each specified frequency.
 Names the alleles according to the given string.
 May brake if the two arrays are not of the same length.
*/
	public Locus(double[] f, String[] n)
	{
		setAlleles(f,n);
	}

/** 
 Returns the number of alleles at the locus.
*/
	public int nAlleles()
	{
		return afreq.length;
	}

/**
 Returns the frequencies of the alleles at the locus.
*/
	public double[] alleleFrequencies()
	{
		return afreq;
	}

/**
 Returns the names of the alleles at the locus.
*/
	public String[] alleleNames()
	{
		return alleleNames;
	}

/**
 Resets the frequencies and names of the alleles at the locus.
*/
	public void setAlleles(double[] f, String[] s)
	{
		if (f.length != s.length)
			throw new RuntimeException("Allele frequency and name array length mismatch");

		afreq = new double[f.length];
		alleleNames = new String[f.length];
		double tot = 0;
		for (int i=0; i<afreq.length; i++)
		{
			afreq[i] = f[i];
			tot += afreq[i];
			alleleNames[i] = s[i];
		}

		for (int i=0; i<afreq.length; i++)
			afreq[i] /= tot;
	}

/**
 Resets the allele frequencies.
*/
	public void setAlleleFrequencies(double[] f)
	{
		if (f.length != afreq.length)
			throw new RuntimeException("Allele frequency array length mismatch");

		double tot = 0;
		for (int i=0; i<afreq.length; i++)
		{
			afreq[i] = f[i];
			tot += afreq[i];
		}

		for (int i=0; i<afreq.length; i++)
			afreq[i] /= tot;
	}

/** 
 Returns a string representation of the alleles and frequencies.
*/
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		//s.append("Alleles:\n");
		for (int i=0; i<afreq.length; i++)
		//	s.append(alleleNames[i]+"\t"+StringFormatter.format(100*afreq[i],4)+"\n");
		//	s.append(afreq[i]+"\t"+alleleNames[i]+"\n");
			s.append(StringFormatter.format(100*afreq[i],5)+"\t"+alleleNames[i]+"\n");
		s.setLength(s.length()-1);
		return s.toString();
	}

// Private data and methods.

	private double[] afreq = null;
	private String[] alleleNames = null;

	private static String[] defaultNames(int n)
	{
		String[] s = new String[n];
		for (int i=0; i<s.length; i++)
			s[i] = (i+1)+"";
		return s;
	}

	private static double[] defaultFrequencies(int n)
	{
		double[] f = new double[n];
		for (int i=0; i<f.length; i++)
			f[i] = 1.0/n;
		return f;
	}
}

package alun.gchap;

import java.util.Vector;

import alun.util.Sorter;

/**
 A genetic trait links together a locus with phenotypes.
*/
abstract public class GeneticTrait
{
/**
 Creates a new genetic trait for the given locus.
*/
	public GeneticTrait(Locus loc)
	{
		l = loc;
		h = new Vector();
	}

/**
 Returns the number of  phenotype for the trait at this locus.
*/
	public int nPhenotypes()
	{
		return h.size();
	}

/**
 Returns the asssociated locus.
*/
	public Locus getLocus()
	{
		return l;
	}

/**
 Removes from the locus any alleles that have zero frequency.
*/
	public void downCode()
	{
		// First find all alleles that have positive counts with current
		// phenotype frequencies and remove phenotype with zero count.
		boolean[] a = new boolean[getLocus().nAlleles()];
		for (int i=0; i<a.length; i++)
			a[i] = false;

		Phenotype[] o = getPhenotypes();
		for (int i=0; i<o.length; i++)
		{
			if (o[i].getFrequency() > 0)
				o[i].markAlleles(a);
			else
				removePhenotype(o[i]);
		}

		// Count how many have positive counts
		// and map them to the new allele numbers.
		int n = 0;
		int[] map = new int[a.length];
		for (int i=0; i<a.length; i++)
		{
			if (a[i]) 
				map[i] = n++;
			else
				map[i] = -1;
		}

		if (n == a.length)
			return;

		// Set the new locus for the model.
		double[] g = getLocus().alleleFrequencies();
		double[] f = new double[n];
		String[] m = getLocus().alleleNames();
		String[] mm = new String[n];
		n = 0;
		for (int i=0; i<a.length; i++)
			if (a[i])
			{
				mm[n] = m[i];
				f[n++] = g[i]; 
			}
		getLocus().setAlleles(f,mm);

		// Set the genotype numbers in the phenotypes to the new genotypes.
		o = getPhenotypes();
		for (int i=0; i<o.length; i++)
			o[i].resetGenotypes(map);
	}

/**
 Removes from the locus any alleles that have frequency less than the
 the given value.
*/
	public void parsDownCode()
	{
		// Set to zero the frequency of all allleles less frequent
		// than the least frequent needed to ensure that all phenotypes
		// are possible.

		Phenotype[] o = getPhenotypes();

		double[] freq = getLocus().alleleFrequencies();
		double[] fx = new double[freq.length];
		int[] al = new int[freq.length];
		boolean[] a = new boolean[al.length];

		for (int i=0; i<al.length; i++)
		{
			al[i] = i;
			fx[i] = freq[i];
			a[i] = true;
		}

		Sorter.sort(al,fx);

		for (int i=0; i<al.length; i++)
		{
			double x = freq[al[i]];
			freq[al[i]] = 0;

			boolean ok = true;
			for (int j=0; j<o.length && ok; j++)
				ok = o[j].expectedFrequency() > 0.000000001; 
			
			if (!ok)
				freq[al[i]] = x;
			else
				a[al[i]] = false;
		}

		// Count how many have positive counts
		// and map them to the new allele numbers.
		int n = 0;
		int[] map = new int[a.length];
		for (int i=0; i<a.length; i++)
		{
			if (a[i]) 
				map[i] = n++;
			else
				map[i] = -1;
		}

		if (n == a.length)
			return;

		// Set the new locus for the model.
		double[] g = getLocus().alleleFrequencies();
		double[] f = new double[n];
		String[] m = getLocus().alleleNames();
		String[] mm = new String[n];
		n = 0;
		for (int i=0; i<a.length; i++)
			if (a[i])
			{
				mm[n] = m[i];
				f[n++] = g[i]; 
			}
		getLocus().setAlleles(f,mm);

		// Set the genotype numbers in the phenotypes to the new genotypes.
		o = getPhenotypes();
		for (int i=0; i<o.length; i++)
			o[i].resetGenotypes(map);
	}

	public void downCode(double tol)
	{
		// First find all alleles that have positive counts with current
		// phenotype frequencies and remove phenotype with zero count.
		boolean[] a = new boolean[getLocus().nAlleles()];
		for (int i=0; i<a.length; i++)
			a[i] = false;

		Phenotype[] o = getPhenotypes();
		for (int i=0; i<o.length; i++)
		{
			if (o[i].getFrequency() > 0 && o[i].expectedFrequency() > 0)
				o[i].markAlleles(a);
			else
				removePhenotype(o[i]);
		}

		double[] freq = getLocus().alleleFrequencies();
		for (int i=0; i<a.length; i++)
			a[i] = a[i] && freq[i] > tol;

		// Count how many have positive counts
		// and map them to the new allele numbers.
		int n = 0;
		int[] map = new int[a.length];
		for (int i=0; i<a.length; i++)
		{
			if (a[i]) 
				map[i] = n++;
			else
				map[i] = -1;
		}

		if (n == a.length)
			return;

		// Set the new locus for the model.
		double[] g = getLocus().alleleFrequencies();
		double[] f = new double[n];
		String[] m = getLocus().alleleNames();
		String[] mm = new String[n];
		n = 0;
		for (int i=0; i<a.length; i++)
			if (a[i])
			{
				mm[n] = m[i];
				f[n++] = g[i]; 
			}
		getLocus().setAlleles(f,mm);

		// Set the genotype numbers in the phenotypes to the new genotypes.
		o = getPhenotypes();
		for (int i=0; i<o.length; i++)
			o[i].resetGenotypes(map);
	}

/**
 Runs n iterations of the gene counting algorithm for the trait.
*/
	public void geneCount(int n)
	{
		for (int s=0; s<n; s++)
		{ 
			double[] a = new double[getLocus().nAlleles()];

			Phenotype[] o = getPhenotypes();
			for (int i=0; i<o.length; i++)
				o[i].distributeCount(a);

			getLocus().setAlleleFrequencies(a);
		}	
	}

/**
 Returns a string representation of the trait.
*/
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append(getLocus()+"\n");
		s.append("Phenotypes:\n");
		Phenotype[] o = getPhenotypes();
		for (int i=0; i<o.length; i++)
		{
			s.append(o[i]+"\t"+o[i].getFrequency()+"\t");
			s.append("\n");
		}
		s.setLength(s.length()-1);
		return s.toString();
	}

// Protected methods.

/**
 Returns the array of phenotype associated with this trait.
*/
	protected Phenotype[] getPhenotypes()
	{
		return (Phenotype[]) h.toArray(new Phenotype[h.size()]);
	}

/**
 Adds the given phenotype to the trait.
*/

	protected void putPhenotype(Phenotype o)
	{
		h.addElement(o);
	}

/**
 Removes the given phenotype from the trait.
*/
	protected void removePhenotype(Phenotype p)
	{
		h.remove(p);
	}
	
// Private data.

	private Vector h = null;
	private Locus l = null;
}

package alun.gchap;


/**
 Base class from which single and multi locus phenotypes are 
 derived.
*/
abstract public class Phenotype 
{
/**
 Create a new phenotype for the specified locus with the given name.
*/
	public Phenotype(Locus l, String s)
	{
		setName(s);
		setLocus(l);
	}

/**
 This method is implemented differently by single and multi-locus
 phenotype classes.
*/
	abstract public void resetGenotypes(int[] map);

/**
 Returns the name of the phenotype.
*/
	public String toString()
	{
		return name;
	}

/**
 Returns the number of times the phenotype has been observed.
*/
	public int getFrequency()
	{
		return frequency;
	}

/**
 Increases the count for the phenotype.
*/
	public void incFrequency(int i)
	{
		frequency += i;
	}

/**
 Marks all alleles that are involved in genotypes that can
 give rise to the phenotype.
*/
	public void markAlleles(boolean[] a)
	{
		Genotype g = null;
		for (initGenotypes(); (g = nextGenotype()) != null; )
		{
			a[g.a] = true;
			a[g.b] = true;
		}
	}

/**
 Returns the expected frequency of the phenotype derived from the
 current allele frequencies.
*/
	public double expectedFrequency()
	{
		double[] f = loc.alleleFrequencies();
		double t = 0;

		Genotype g = null;
		for (initGenotypes(); (g = nextGenotype()) != null; )
		{ 
			if (g.a != g.b)
				t += 2*f[g.a]*f[g.b];
			else
				t += f[g.a]*f[g.b];
		}
		return t;
	}

/**
 Distributes the count for this phenotype to the alleles that are involved
 in geneotype that can give rise to the phenotype.
 This is where the gene counting actually happens.
*/
	public void distributeCount(double[] a)
	{
		double[] f = loc.alleleFrequencies();
		double t = expectedFrequency();

		Genotype g = null;
		for (initGenotypes(); (g = nextGenotype()) != null; )
		{
			double p = 2*f[g.a]*f[g.b]*frequency/t;
			a[g.a] += p;
			if (g.b != g.a)
				a[g.b] += p;
		}
	}

/**
 Returns the log likelihood for the phenotype.
*/
	public double logLikelihood()
	{
		return getFrequency()*Math.log(expectedFrequency());
	}

// Protected data and methods.

/**
 Initialises an iterator through the genotypes that can give rise
 to the phenotype.
*/
	protected void initGenotypes()
	{
		g = 0;
	}

/**
 Advances the genotype iterator.
*/
	protected Genotype nextGenotype()
	{
		if (g == genos.length)
			return null;
		else
			return genos[g++];
	}

/**
 Returns the number of genotypes that can give rise to the phenotype.
*/
	protected int nGenotypes()
	{
		return genos.length;
	}

/**
 Reset the genotype list to the given array.
*/
	protected void setGenotypes(Genotype[] g)
	{
		genos = g;
	}

/**
 Returns the locus associated with the phenotype.
*/
	protected Locus getLocus()
	{
		return loc;
	}

/**
 Sets the locus associated with the phenotype.
*/
	protected void setLocus(Locus l)
	{
		loc = l;
	}

/**
 Returns the name of the phenotype.
*/
	protected String getName()
	{
		return name;
	}

/**
 Sets the name of the phenotype.
*/
	protected void setName(String s)
	{
		name = s;
	}

// Private data and methods.

	private Locus loc = null;
	private String name = null; private int frequency = 0;
	private Genotype[] genos = null;
	private int g = 0;
}

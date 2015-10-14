package alun.genepi;

/**
 A variable representing the ordered genotype for an
 individual as some genetic locus.
*/
public class Genotype extends GeneticVariable
{
	public Genotype(int n)
	{
		super(n*n);
		na = n;
	}

/**
 Returns the state of the paternal allele for this genotype.
*/
	public int pat()
	{
		return getState()/na;
	}

/**
 Return the state of the maternal allele for this genotype.
*/
	public int mat()
	{
		return getState()%na;
	}

/**
 Sets the state of the genotype to match the given states for 
 the paternal and maternal alleles.
*/
	public boolean setState(int i, int j)
	{
		return setState(i*na+j);
	}

//	public String toString()
//	{
//		return super.toString()+" "+getNStates();
//	}

	public void setId(int n)
	{
		id = n;
	}

	public int getId()
	{
		return id;
	}

// Private data.

	protected int na = 0;
	private int id = 0;
}

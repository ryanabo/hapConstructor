package alun.gchap;

/**
 Represents a genotype as an un ordered pair of allele indexes.
*/
public class Genotype
{
/** 
 Creates a new genotype from the given allele numbers.
 The alleles are unordered so using (i,j) and (j,i) are
 equivalent.
*/
	public Genotype(int i, int j)
	{
		if (i < j)
		{
			a = i;
			b = j;
		}
		else
		{
			a = j;
			b = i;
		} 
	}

/**
 The first allele index.
*/
	public int a = 0;
/**
 The second allele index.
*/
	public int b = 0;
}

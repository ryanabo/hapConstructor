package alun.linkage;

/**
 This class represents the data for an individual for a locus
 in the linkage numbered allele input format.
*/
public class NumberedAllelePhenotype extends LinkagePhenotype
{
/** 
 Creates a new phenotype given the specified allele numbers and
 checks that the data is consistent with the parameters for 
 the given locus.
*/
	public NumberedAllelePhenotype(NumberedAlleleLocus l, int allele1, int allele2)
	{
		setLocus(l);

		if (allele1 < 0 || allele1 > l.alleleFrequencies().length)
			throw new LinkageException("Allele number out of range "+allele1);
		else
			a1 = allele1;
		
		if (allele2 < 0 || allele2 > l.alleleFrequencies().length)
			throw new LinkageException("Allele number out of range "+allele2);
		else
			a2 = allele2;
	}

	public LinkagePhenotype nullCopy()
	{
		return new NumberedAllelePhenotype((NumberedAlleleLocus) getLocus(),0,0);
	}

/**
 Returns a string giving the numbers of the alleles at this
 locus for the individual whose data this is.
*/
	public String toString()
	{
		return f.format(a1,2)+" "+f.format(a2,2);
	}

	public void reCode(int[] c)
	{
		a1 = c[a1];
		a2 = c[a2];
	}

/**
 The first allele at this locus for the individual whose data this is.
*/
	public int a1 = 0;
/**
 The second allele at this locus for the individual whose data this is.
*/
	public int a2 = 0;
}

package alun.genio;

public interface GeneticDataSource extends BasicGeneticData
{
/**
	The identifier of this data source.
*/
	public String name();

/**
 Returns a String identifier for the jth locus.
*/
	public String locName(int j);

/**
 Returns a string representation of the observation for the ith individuals at 
 the jth locus.
*/
	public String call(int i,int j);

/**
 The number of phenotypes at the jth genetic locus. 
 Must be greater than zero and may be infinite.
 Phenotype is used to mean any observation for the locus, including
 genotype information. So for a co-dominant genetic marker nPhenotypes(j)
 should return the same as nAlleles(j)*(nAlleles(j)-1)/2.
*/
	public int nPhenotypes(int j);

/**
 Returns a String identifyer for the ith person in the list.
*/
	public String id(int i);

/**
 Returns the proband status of the ith individual in the list.
*/
	public int proband(int i);

/**
 Returns the number of individuals whose proband status > 0.
*/
	public int nProbands();

/**
 If k = 0, returns the first allele for the ith individual
 at the jth locus. If k = 1, returns the second allele
 for the ith individual at the jth locus.
 Allele numbers should start at 0. If such a clear allele call
 is not possible return -1.
*/
	public int indAllele(int i, int j, int k);
}

package alun.genio;

public interface ParameterData
{
/**
 The number of genetic loci for which data is available.
 The loci will be accessed by index from 0 to nLoci()-1
 and are assumed to be in corresponding physical order.
*/
	public int nLoci();

/**
 The number of alleles at the jth genetic locus.
*/
	public int nAlleles(int j);

/**
 The frequencies of the alleles at the jth genetic locus.
*/
	public double[] alleleFreqs(int j);

/**
 Returns the probability of a recombination between loci i and j in
 a single meiosis in females.
*/
	public double getFemaleRecomFrac(int i, int j);

/**
 Returns the probability of a recombination between loci i and j in
 a single meiosis in males.
*/
	public double getMaleRecomFrac(int i, int j);
}

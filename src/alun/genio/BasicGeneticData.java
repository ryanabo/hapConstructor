package alun.genio;

public interface BasicGeneticData extends ParameterData, PedigreeData
{
/**
 Returns the string representing the ith individual in the pedigree data.
*/
	public String id(int i);
/**
 Returns the penetrance function for the ith person in the list
 at the jth genetic locus.
 This is a matrix of dimension nAlleles(j) x nAlleles(j) where the
 entry at row a, column b is the probability of the individual's 
 observed phenotype given that they have allele number a on the
 paternal chromosome and allele number b on the maternal chromosome.
 Again, phenotype is used in a very general way so this function is
 also used to express genotypic information. If an individual i
 is observed with genotype (x,y) at locus j then 
	penetrance(i,j)[x,y] = penetrance(i,j)[y,x] = 1
 with zeros elsewhere.
 If the locus is not perfectly co-dominant, or there is error
 in the observation, this should be expressed appropriately in this
 matrix.
 If no information is present for the ith individual at the jth locus
 this method can return null.
*/
	public double[][] penetrance(int i, int j);
}

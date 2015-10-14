package alun.genio;

public interface PedigreeData
{
/**
 Returns the number of individuals who are being considered.
 Individuals will be accessed by integers between 0 and 
 nIndiviuals()-1 so repeated calls to the methods below must
 give consistent responses.
*/
	public int nIndividuals();

/**
 Returns the position in the list of individuals of the father
 of the individual in the ith position in the list. If father
 is not in the list -1 should be returned.
*/
	public int pa(int i);

/**
 Returns the position in the list of individuals of the mother
 of the individual in the ith position in the list. If mother
 is not in the list -1 should be returned.
*/
	public int ma(int i);

/**
 Returns the positions in the list of individuals of the children
 of the individual in the ith position in the list. If there
 are no offspring an array of length zero is returned.
*/
	public int[] kids(int i);

/**
 Returns a matrix of integers indexing the positions of a
 nuclear family. One row per family. The first element in
 the row is the father, the second the mother and the remainder
 are the indexes of the children.
**/
	public int[][] nuclearFamilies();
}

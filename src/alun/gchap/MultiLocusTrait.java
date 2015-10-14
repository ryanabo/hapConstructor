package alun.gchap;

import alun.util.HashOfHash;

/**
 This represents a multi locus trait. It links the loci to the phenotypes.
*/
public class MultiLocusTrait extends GeneticTrait
{
/**
 Creates a new multi locus trait from two given genetic traits,
 each of which may itself be a multi locus trait.
*/
	public MultiLocusTrait(GeneticTrait a, GeneticTrait b)
	{
		super(new MultiLocus(a.getLocus(),b.getLocus()));
	
		Phenotype[] pa = a.getPhenotypes();
		Phenotype[] pb = b.getPhenotypes();
		hh = new HashOfHash();

		for (int i=0; i<pa.length; i++)
			for (int j=0; j<pb.length; j++)
			{
				Phenotype p = new MultiLocusPhenotype((MultiLocus)getLocus(),pa[i],pb[j]);
				hh.put(pa[i],pb[j],p);
				putPhenotype(p);				
			}
	}

/**
 Returns the phenotype of the multilocus trait that represents
 the same information as those specified for the two constituent traits.
*/
	public Phenotype findPhenotype(Phenotype a, Phenotype b) 
	{
		return (Phenotype) hh.get(a,b);
	}

// Private data.

	private HashOfHash hh = null;

/**
 Test main.
*/
	public static void main(String[] args)
	{
		GeneticTrait a = new SNP();
		GeneticTrait b = new Marker(2);
		GeneticTrait c = new MultiLocusTrait(a,b);
		System.out.println(c);
	}
}

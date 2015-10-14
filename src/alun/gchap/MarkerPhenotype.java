package alun.gchap;

import java.util.Vector;

/**
 Represents a phenotype at a marker.
*/
public class MarkerPhenotype extends Phenotype
{
/**
 Creates a new marker phenotype for the given locus with the
 name specified by the string.
 The final argument is a matrix with two columns. Each row
 of the matrix specifies the alleles of a genotype that
 gives rise to this phenotype.
 Since this is a codominant marker a genotype can give rise
 to exactly one phenotype.
*/
	public MarkerPhenotype(Locus l, String s, int[][] g)
	{
		super(l,s);
		Genotype[] gn = new Genotype[g.length];
		for (int i=0; i<g.length; i++)
			gn[i] = new Genotype(g[i][0],g[i][1]);
		setGenotypes(gn);
	}

/**
 Resets the genotype that give rise to this phenotype to 
 a subset specified by the indexes in the given array.
*/
	public void resetGenotypes(int[] m)
	{
		Vector v = new Vector();
		Genotype gg = null;
		for (initGenotypes(); (gg=nextGenotype()) != null; )
		{
			int na1 = m[gg.a];
			int na2 = m[gg.b];
			if (na1 > -1 && na2 > -1)
			{
				Genotype q = new Genotype(na1,na2);
				v.addElement(q);
			}
		}
		setGenotypes((Genotype[])v.toArray(new Genotype[v.size()]));
	}
}

package alun.gchap;

import java.util.Vector;

/**
 This class represents a multi locus phenotype, or 
 list of phase unknown genotypes.
*/
public class MultiLocusPhenotype extends Phenotype
{
/**
 Creates a new phentpe for the given locus with with the given name.
*/
	public MultiLocusPhenotype(MultiLocus l, String s)
	{
		super(l,s);
	}

/**
 Creates a new phenotype from the phenotypes at the constituent loci.
*/
	public MultiLocusPhenotype(MultiLocus l, Phenotype a, Phenotype b)
	{
		super(l,a.toString()+","+b.toString());

try{

		Vector v = new Vector();
		Genotype ag = null;
		Genotype bg = null;
		for (a.initGenotypes(); (ag=a.nextGenotype()) != null; )
			for (b.initGenotypes(); (bg=b.nextGenotype()) != null; )
			{
				Genotype[] ggg = jointGenotypes(ag,bg);
				for (int i=0; i<ggg.length; i++)
					v.addElement(ggg[i]);
			}

		setGenotypes((Genotype[]) v.toArray(new Genotype[v.size()]));
}
catch (OutOfMemoryError e)
{
			System.err.println("Allocating MultiLocusPhenotype:");
			e.printStackTrace();
			throw (e);
}
	}

/**
 Resets the genotypes that give rise to this phenotype to the subset
 indicated by the indices in the given array.
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
		setGenotypes((Genotype[]) v.toArray(new Genotype[v.size()]));
	}

/*
	public void resetGenotypes(int[] m)
	{
		Genotype g = null;
		for (initGenotypes(); (g=nextGenotype()) != null; )
		{
			g.a = m[g.a];
			g.b = m[g.b];
		}
	}
*/

// Protected data and methods.

	protected Genotype[] jointGenotypes(Genotype i, Genotype j)
	{
		if (i.a != i.b && j.a != j.b)
		{
			Genotype[] result = new Genotype[2];
			result[0] = new Genotype(((MultiLocus)getLocus()).jointAllele(i.a,j.a),((MultiLocus)getLocus()).jointAllele(i.b,j.b));
			result[1] = new Genotype(((MultiLocus)getLocus()).jointAllele(i.a,j.b),((MultiLocus)getLocus()).jointAllele(i.b,j.a));
			return result;
		}
		else
		{
			Genotype[] result = new Genotype[1];
			result[0] = new Genotype(((MultiLocus)getLocus()).jointAllele(i.a,j.a),((MultiLocus)getLocus()).jointAllele(i.b,j.b));
			return result;
		}
	}
}

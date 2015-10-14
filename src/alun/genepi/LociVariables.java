package alun.genepi;

import alun.genio.BasicGeneticData;
import alun.markov.Product;

public class LociVariables
{
	public LociVariables(BasicGeneticData data, double errorpri)
	{
		d = data;

		loc = new LocusVariables[d.nLoci()];
		for (int i=0; i<loc.length; i++)
		{
			loc[i] = new LocusVariables(d,i,errorpri);
		}
	}

	public LociVariables(BasicGeneticData data)
	{
		this(data, -1);
	}

	public Product locusProduct(int i)
	{
		return loc[i].makeInheritanceLocusProduct();
	}

	public Product unrelatedLocusProduct(int i)
	{
		return loc[i].makeUnrelatedProduct();
	}

	public Product twoPointProduct(int a, int b, Theta mrec, Theta frec)
	{
		Product p = new Product();
		p.add(locusProduct(a).getFunctions());
		p.add(locusProduct(b).getFunctions());
		
		for (int i=0; i<loc[a].genotypes().length; i++)
		{
			if (getInheritance(i,a,0) != null)
				p.add(new Recombination(getInheritance(i,a,0),getInheritance(i,b,0),mrec));
			if (getInheritance(i,a,1) != null)
				p.add(new Recombination(getInheritance(i,a,1),getInheritance(i,b,1),frec));
		}

		return p;
	}

	public Product twoPointProduct(int a, int b, Theta t)
	{
		Product p = new Product();
		p.add(locusProduct(a).getFunctions());
		p.add(locusProduct(b).getFunctions());
		
		for (int i=0; i<loc[a].genotypes().length; i++)
		{
			if (getInheritance(i,a,0) != null)
				p.add(new Recombination(getInheritance(i,a,0),getInheritance(i,b,0),t));
			if (getInheritance(i,a,1) != null)
				p.add(new Recombination(getInheritance(i,a,1),getInheritance(i,b,1),t));
		}

		return p;
	}

	public LocusVariables[] getLocusVariables()
	{
		return loc;
	}

	public Inheritance getInheritance(int i, int j, int k)
	{
		Inheritance[] t = ( k == 0 ? loc[j].patInheritances() : loc[j].matInheritances() );
		if (t != null)
			return t[i];
		else 
			return null;
	}

	public Error getError(int i, int j)
	{
		return loc[j].error(i);
	}

	public Genotype fathersGenotype(int i, int j)
	{
		Genotype g = loc[j].genotypes()[i];
		return loc[j].pa.get(g);
	}

	public Genotype mothersGenotype(int i, int j)
	{
		Genotype g = loc[j].genotypes()[i];
		return loc[j].ma.get(g);
	}

	public Genotype getGenotype(int i, int j)
	{
		return loc[j].genotypes()[i];
	}

	public boolean isFounder(int i)
	{
		return loc[0].isFounder(i);
	}

	public int nLoci()
	{
		return loc.length;
	}

	public int nAlleles(int j)
	{
		return d.nAlleles(j);
	}

	public int nIndividuals()
	{
		return loc[0].genotypes().length;
	}

	public int nFounders()
	{
		return loc[0].founderGenotypes().length;
	}

	public int nFamilies()
	{
		return d.nuclearFamilies().length;
	} 

	public Product traitProduct(Theta theta)
	{
		Product p = locusProduct(0);

		for (int j=0; j<nIndividuals(); j++)
		{
			if (getInheritance(j,0,0) != null)
				p.add(new VariableRecombination(getInheritance(j,0,0),theta));
			if (getInheritance(j,0,1) != null)
				p.add(new VariableRecombination(getInheritance(j,0,1),theta));
		}

		return p;
	}

	public BasicGeneticData getDataSource()
	{
		return d;
	}

// Private data.

	protected BasicGeneticData d = null;
	protected LocusVariables[] loc = null;
}

package alun.genepi;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import alun.genio.BasicGeneticData;
import alun.markov.GraphicalModel;
import alun.markov.Product;

public class LocusVariables
{
	public LocusVariables(BasicGeneticData d, int j)
	{
		this(d,j,0);
	}

	public LocusVariables(BasicGeneticData d, int j, double prob_err)
	{
		geno = new Genotype[d.nIndividuals()];
		for (int i=0; i<geno.length; i++)
		{
			geno[i] = new Genotype(d.nAlleles(j));
			geno[i].setId(i);
			//geno[i].setName(d.id(i));
		}

		prior = d.alleleFreqs(j);
		pen = new double[geno.length][][];
		
		pa = new LinkedHashMap<Genotype,Genotype>();
		ma = new LinkedHashMap<Genotype,Genotype>();

		for (int i=0; i<geno.length; i++)
		{
			pen[i] = d.penetrance(i,j);

			if (d.pa(i) >= 0)
				pa.put(geno[i],geno[d.pa(i)]);

			if (d.ma(i) >= 0)
				ma.put(geno[i],geno[d.ma(i)]);
		}

		if (prob_err > 0)
		{
			errpri = prob_err;
			error = new Error[geno.length];
			for (int i=0; i<geno.length; i++)
				if (pen[i] != null)
					error[i] = new Error();
		}
	}

	public Product makeUnrelatedProduct()
	{
		Product p = new Product();
		addAllPriors(p);
		addPenetrances(p);
		return p;
	}

/*
	protected void addNoErrorPenetrances(Product p)
	{
		for (int i=0; i<geno.length; i++)
			if (pen[i] != null)
				p.add(new Penetrance(geno[i],pen[i]));
	}

	public Product makeNoErrorUnrelatedProduct()
	{
		Product p = new Product();
		addAllPriors(p);
		addNoErrorPenetrances(p);
		return p;
	}

	public Product makeNoErrorLocusProduct()
	{
		Product p = new Product();
		addTransmissions(p);
		p.triangulate();
		addFounderPriors(p);
		addNoErrorPenetrances(p);
		return p;
	}
*/

	public Product makeLocusProduct()
	{
		Product p = new Product();
		addTransmissions(p);
		p.triangulate();
		addFounderPriors(p);
		addPenetrances(p);
		return p;
	}

	public Product makeUnrelatedUnorderedProduct()
	{
                Product p = new Product();
                for (int i=0; i<geno.length; i++)
                	p.add(new UnorderedPrior(geno[i],prior));
                addPenetrances(p);
                return p;
	}

        public Product makeUnorderedProduct()
        {
                Product p = new Product();

                for (int i=0; i<geno.length; i++)
                {
                        if (!isFounder(i))
                                p.add(new UnorderedTransmission(geno[i],pa.get(geno[i]),ma.get(geno[i])));
                        else
                                p.add(new UnorderedPrior(geno[i],prior));
                }

                addPenetrances(p);
                return p;
        }


	public void makeGenotypesUnordered()
	{
                Map<Genotype,Genotype> map = new HashMap<Genotype,Genotype>();
                Genotype[] g = new Genotype[geno.length];
                for (int i=0; i<g.length; i++)
                {
                        g[i] = new UnorderedGenotype(prior.length);
                        g[i].setId(geno[i].getId());
                        map.put(geno[i],g[i]);
                }

                Set<Genotype> s = new LinkedHashSet<Genotype>(pa.keySet());
                for (Genotype o : s)
                {
                        pa.put(map.get(o),map.get(pa.get(o)));
                        pa.remove(o);
                }

                s = new LinkedHashSet<Genotype>(ma.keySet());
                for (Genotype o : s)
                {
                        ma.put(map.get(o),map.get(ma.get(o)));
                        ma.remove(o);
                }

                geno = g;
	}

	public Product makeInheritanceLocusProduct()
	{
//		if (patin == null)
//			makeInheritances();

		Product p = makeLocusProduct();
		addInheritanceTerms(p);
		return p;
	}

/*
        private Product fixedInheritancesProduct()
        {
		if (fixedinprod == null)
		{
//			if (patin == null)
//				makeInheritances();

			Product p = new Product();
			addTransmissions(p);
			p.triangulate();
			addInheritanceTerms(p);
			addFounderPriors(p);
			addPenetrances(p);
	
                	for (int i=0; i<patin.length; i++)
                	{
                        	if (patin[i] != null)
                                	p.removeVariable(patin[i]);
                        	if (matin[i] != null)
                                	p.removeVariable(matin[i]);
                	}

			p.triangulate();

			fixedinprod = p;
		}
		return fixedinprod;
        }
*/

	public GraphicalModel fixedInheritanceGM()
	{
		if (fixedingm == null)
		{
			Product p = new Product();
			addTransmissions(p);
			p.triangulate();
			addInheritanceTerms(p);
			addFounderPriors(p);
			addPenetrances(p);
	
                	for (int i=0; i<patin.length; i++)
                	{
                        	if (patin[i] != null)
                                	p.removeVariable(patin[i]);
                        	if (matin[i] != null)
                                	p.removeVariable(matin[i]);
                	}

			p.triangulate();

			fixedingm = new GraphicalModel(p);
			fixedingm.allocateOutputTables();
		}

		return fixedingm;
	}

/*
	public Product fixedInheritancesProduct()
	{
		if (patal == null)
			makeAlleles();
		
		Product p = new Product();

		for (int i=0; i<geno.length; i++)
		{
			if (pa.get(geno[i]) == null)
				p.add(new AllelePrior(patal[i],prior));
			else 
				patal[i] = null;

			if (ma.get(geno[i]) == null)
				p.add(new AllelePrior(matal[i],prior));
			else
				matal[i] = null;
		}

		for (int i=0; i<geno.length; i++)
		{
			if (pen[i] != null)
			{
				setPat(i);
				setMat(i);
				if (error != null)
					p.add(new AlleleErrorPenetrance(patal[i],matal[i],error[i],pen[i]));
				else
					p.add(new AllelePenetrance(patal[i],matal[i],pen[i]));
			}
		}

		return p;
	}
*/

	public Genotype[] founderGenotypes()
	{
		LinkedHashSet<Genotype> f = new LinkedHashSet<Genotype>();
		for (int i=0; i<geno.length; i++)
			if (isFounder(i))
				f.add(geno[i]);
		return (Genotype[]) f.toArray(new Genotype[f.size()]);
	}

	public Genotype[] genotypes()
	{
		return geno;
	}

	public Inheritance[] patInheritances()
	{
		return patin;
	}

	public Inheritance[] matInheritances()
	{
		return matin;
	}

	public Error[] errors()
	{
		return error;
	}

	public Error error(int i)
	{
		return error[i];
	}
	
	public void save()
	{
/*
		if (geno != null)
			for (int i=0; i<geno.length; i++)
				if (geno[i] != null)
					geno[i].save();
*/
		if (patin != null)
			for (int i=0; i<patin.length; i++)
				if (patin[i] != null)
					patin[i].save();
		if (matin != null)
			for (int i=0;i<matin.length; i++)
				if (matin[i] != null)
					matin[i].save();
/*
		if (error != null)
			for (int i=0; i<error.length; i++)
				if (error[i] != null)
					error[i].save();
*/
	}

	public void restore()
	{
/*
		for (int i=0; i<geno.length; i++)
			if (geno[i] != null)
				geno[i].restore();
*/
		if (patin != null)
			for (int i=0; i<patin.length; i++)
				if (patin[i] != null)
					patin[i].restore();
		if (matin != null)
			for (int i=0;i<matin.length; i++)
				if (matin[i] != null)
					matin[i].restore();

/*
		if (error != null)
			for (int i=0; i<error.length; i++)
				if (error[i] != null)
					error[i].restore();
*/
	}

	public boolean isFounder(int i)
	{	
		return  pa.get(geno[i]) ==  null || ma.get(geno[i]) == null;
	}

// Private data.

	protected Genotype[] geno = null;
	protected Allele[] patal = null;
	protected Allele[] matal = null;
	protected Inheritance[] patin = null;
	protected Inheritance[] matin = null;
	protected Error[] error = null;
	private double errpri = 0;

	private Product fixedinprod = null;
	private GraphicalModel fixedingm = null;

	protected double[] prior = null;
	protected double[][][] pen = null;
	protected LinkedHashMap<Genotype,Genotype> pa = null;
	protected LinkedHashMap<Genotype,Genotype> ma = null;

	protected void setPat(int i)
	{
		if (patal[i] != null)
			return;

		int p = pa.get(geno[i]).getId();
		setPat(p);
		setMat(p);
		
		patal[i] = patin[i].getState() == 0 ? patal[p] : matal[p];
	}

	protected void setMat(int i)
	{
		if (matal[i] != null)
			return;

		int m = ma.get(geno[i]).getId();
		setPat(m);
		setMat(m);
		
		matal[i] = matin[i].getState() == 0 ? patal[m] : matal[m];
	}

	protected void addAllPriors(Product p)
	{
		for (int i=0; i<geno.length; i++)
			p.add(new GenotypePrior(geno[i],prior));
	}

	protected void addFounderPriors(Product p)
	{
		for (int i=0; i<geno.length; i++)
			if (isFounder(i))
				p.add(new GenotypePrior(geno[i],prior));
	} 

	protected void addPenetrances(Product p)
	{
		if (error == null)
		{
			for (int i=0; i<geno.length; i++)
				if (pen[i] != null)
					p.add(new Penetrance(geno[i],pen[i]));
		}
		else
		{
			for (int i=0; i<geno.length; i++)
				if (pen[i] != null)
				{
					p.add(new ErrorPenetrance(geno[i],error[i],pen[i]));
					p.add(new ErrorPrior(error[i],errpri));
				}
		}
	}

	protected void addTransmissions(Product p)
	{
		for (int i=0; i<geno.length; i++)
			if (!isFounder(i))
				p.add(new MendelianTransmission(geno[i],pa.get(geno[i]),ma.get(geno[i])));
	}

	protected void addInheritanceTerms(Product p)
	{
		if (patin == null)
			makeInheritances();

		for (int i=0; i<geno.length; i++)
		{
			if (patin[i] != null)
				p.add(new PaternalInheritance(patin[i],pa.get(geno[i]),geno[i]));
			if (matin[i] != null)
				p.add(new MaternalInheritance(matin[i],ma.get(geno[i]),geno[i]));
		}
	}

	protected void makeInheritances()
	{
		patin = new Inheritance[geno.length];
		matin = new Inheritance[geno.length];
		for (int i=0; i<patin.length; i++)
		{
			if (pa.get(geno[i]) != null && ma.get(geno[i]) != null)
			{
				patin[i] = new Inheritance();
				patin[i].setName(geno[i]+"pat");
				matin[i] = new Inheritance();
				matin[i].setName(geno[i]+"mat");
			}
		}
	}

	protected void makeAlleles()
	{
		patal = new Allele[geno.length];
		matal = new Allele[geno.length];

		for (int i=0; i<geno.length; i++)
		{
			if (pa.get(geno[i]) == null)
			{
				patal[i] = new Allele(prior.length);
				patal[i].setId(i);
			}

			if (ma.get(geno[i]) == null)
			{
				matal[i] = new Allele(prior.length);
				matal[i].setId(100+i);
			}
		}
	}
}

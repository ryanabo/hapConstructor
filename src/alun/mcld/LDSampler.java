package alun.mcld;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import alun.genepi.Allele;
import alun.genepi.Genotype;
import alun.genepi.GenotypeModel;
import alun.genepi.GenotypePrior;
import alun.genepi.IndependentLocusSampler;
import alun.genepi.LinkageVariables;
import alun.markov.Function;
import alun.markov.GraphicalModel;
import alun.markov.Product;
import alun.markov.Variable;
import alun.util.StringFormatter;

public class LDSampler extends IndependentLocusSampler implements DataHaplotypeSource
{
	public LDSampler(LinkageVariables l)
	{
		this(l,0);
	}

	public LDSampler(LinkageVariables l, int first, LDModel ldmod)
	{
		this(l,first);
		setLDModel(ldmod);
	}
	
	public LDSampler(LinkageVariables l, int first)
	{
		setInitialBlocks(independentLocusBlocks(l,first));
		setThrifty(true);
		initialize();

		prod = l.completeProduct(first);
		for (Function f : prod.getFunctions())
			if (f instanceof GenotypePrior)
				prod.remove(f);

		Set<Variable>[] alls = (Set<Variable>[]) new Set[l.nLoci()];
		for (int i=first; i<alls.length; i++)
			alls[i] = new LinkedHashSet<Variable>();

		haps = new Vector<Vector<Allele>>();
		for (int i=0; i<l.nIndividuals(); i++)
		{
			if (l.isFounder(i))
			{
				Vector<Allele> pat = new Vector<Allele>();
				Vector<Allele> mat = new Vector<Allele>();
				
				for (int j=first; j<l.nLoci(); j++)
				{
					Genotype gg = l.getGenotype(i,j);

					Allele pp = new Allele(l.nAlleles(j));
					pp.setState(gg.pat());
					pat.add(pp);

					Allele mm = new Allele(l.nAlleles(j));
					mm.setState(gg.mat());
					mat.add(mm);

					alls[j].add(pp);
					alls[j].add(mm);
					
					prod.add(new AllelesToGenotype(pp,mm,gg));
				}

				haps.add(pat);
				haps.add(mat);
			}
		}

		blocks = new Vector<Set<Variable>>();
		for (int i=first; i<l.nLoci(); i++)
		{
			Set<Variable> sss = l.locusProduct(i).getVariables();
			sss.addAll(alls[i]);
			sss.retainAll(prod.getVariables());
			blocks.add(sss);
		}
		//gms = new Vector<GraphicalModel>();
		gms = new LinkedHashSet<GraphicalModel>();

		locs = new LinkedHashSet<Locus>();
		for (int i=first; i<l.nLoci(); i++)
		{
			Locus ll = new Locus(i-first,l.nAlleles(i),(1.0+i-first));
			ll.setName(""+i);
			locs.add(ll);
		}

		setLocusBlocks(gms);
	}

	public void setLDModel(LDModel m)
	{
		if (ldprod != null)
			prod.removeProduct(ldprod);

		if (m != null)
		{
			ldprod = new Product();
			for (Vector<Allele> h : haps)
				ldprod.addProduct(m.replicate(h));
			prod.addProduct(ldprod);
		}

		gms.clear();
		for (Set<Variable> b : blocks)
		{
			Product pp = prod.subProduct(b);
			pp.triangulate();

			GenotypeModel gm = new GenotypeModel(pp);
			gms.add(gm);
		}

		
	}

/*
	public void sample()
	{
		for (int i=0; i<gms.size(); i++)
		{
			GraphicalModel g = gms.get(i);
			g.allocateOutputTables();
			g.allocateInvolTables();
			g.collect();
			g.drop();
			g.clearOutputTables();
			g.clearInvolTables();
		}
	}
*/
	
	public boolean update(LDModel m)
	{
		setLDModel(m);
		for (int i=0; i<10; i++)
			sample();
		return true;
	}

	public boolean maximize(LDModel m)
	{
		return update(m);	
	}

	public int[] getHaplotype(int i)
	{
		int[] x = new int[haps.get(i).size()];
		for (int j=0; j<x.length; j++)
			x[j] = haps.get(i).get(j).getState();
		return x;
	}

	public int getAllele(int i, int j)
	{
		return haps.get(i).get(j).getState();
	}

	public int nHaplotypes()
	{
		return haps.size();
	}
	
	public Set<Locus> getLoci()
	{
		return locs;
	}

	public int nLoci()
	{
		return locs.size();
	}

	public int nAlleles(int j)
	{
		Vector<Locus> ll = new Vector<Locus>(locs);
		return ll.get(j).getNStates();
	}

	public Product getProduct()
	{
		return prod;
	}

       public String toString()
        {
                StringBuffer s = new StringBuffer();
                for (int i=0; i<haps.size(); i++)
                {
                        int[] t = getHaplotype(i);
                        for (int j=0; j<t.length; j++)
                                s.append(StringFormatter.format((1+t[j]),2));
                        s.append("\n");
                }

                if (haps.size() > 0)
                        s.deleteCharAt(s.length()-1);

                return s.toString();
        }

// Private data.

	private Set<Locus> locs = null;
	private Vector<Vector<Allele>> haps = null;
	private Product prod = null;
	private Product ldprod = null;
	private Vector<Set<Variable>> blocks = null;
	//private Vector<GraphicalModel> gms = null;
	private Set<GraphicalModel> gms = null;
}

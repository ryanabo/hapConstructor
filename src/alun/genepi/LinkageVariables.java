package alun.genepi;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import alun.genio.BasicGeneticData;
import alun.markov.GraphicalModel;
import alun.markov.Product;
import alun.markov.Variable;

public class LinkageVariables extends LociVariables
{
	public LinkageVariables(BasicGeneticData data, double errorpri)
	{
		super(data,errorpri);
	}

	public LinkageVariables(BasicGeneticData data)
	{
		this(data, -1);
	}

	public Product connectedLocusProduct(int i)
	{
		return connectedLocusProduct(i,true,true);
	}

	public Product genericPriorProduct(int first, Genotype[] genos)
	{
		Product p = new Product();

		for (int i=first; i<loc.length; i++)
			p.add(new GenotypePrior(genos[i],d.alleleFreqs(i)));

		return p;
	}

	public void makeInheritances(int first)
	{
		for (int i=first; i<loc.length; i++)
			if (loc[i].patin == null)
				loc[i].makeInheritances();
	}

	public Product genericMeiosisProduct(int first, Inheritance[] pats, Inheritance[] mats)
	{
		Product p = new Product();
		
		for (int i=first; i<loc.length; i++)
		{
			if (loc[i].patin == null)
				loc[i].makeInheritances();

			if (i > first)
			{
				p.add(new FixedRecombination(pats[i],pats[i-1],d.getMaleRecomFrac(i,i-1)));
				p.add(new FixedRecombination(mats[i],mats[i-1],d.getFemaleRecomFrac(i,i-1)));
			}
		}

		return p;
	}

	public Product connectedLocusProduct(int i, boolean connectBackward, boolean connectForward)
	{
		Product p = locusProduct(i);
		if (connectBackward && i > 0)
		{
			for (int j=0; j<loc[i].genotypes().length; j++)
			{
				if (getInheritance(j,i,0) != null)
				{
					p.add(new FixedRecombination(getInheritance(j,i,0),getInheritance(j,i-1,0),d.getMaleRecomFrac(i,i-1)));
					p.removeVariable(getInheritance(j,i-1,0));
				}
				if (getInheritance(j,i,1) != null)
				{
					p.add(new FixedRecombination(getInheritance(j,i,1),getInheritance(j,i-1,1),d.getFemaleRecomFrac(i,i-1)));
					p.removeVariable(getInheritance(j,i-1,1));
				}
			}
		}
		if (connectForward && i+1 < loc.length)
		{
			for (int j=0; j<loc[i].genotypes().length; j++)
			{
				if (getInheritance(j,i,0) != null)
				{
					p.add(new FixedRecombination(getInheritance(j,i+1,0),getInheritance(j,i,0),d.getMaleRecomFrac(i+1,i)));
					p.removeVariable(getInheritance(j,i+1,0));
				}
				if (getInheritance(j,i,1) != null)
				{
					p.add(new FixedRecombination(getInheritance(j,i+1,1),getInheritance(j,i,1),d.getFemaleRecomFrac(i+1,i)));
					p.removeVariable(getInheritance(j,i+1,1));
				}
			}
		}

		return p;
	}

	public Product unconditionalLocusProduct(int i, boolean connectBackward, boolean connectForward)
	{
		if (loc[i].patin == null)
			loc[i].makeInheritances();
		Product p = new Product();

		if (!connectBackward && !connectForward)
		{
			for (int j=0; j<loc[i].genotypes().length; j++)
			{
				if (getInheritance(j,i,0) != null)
					p.add(new TrivialFunction(getInheritance(j,i,0)));
				if (getInheritance(j,i,1) != null)
					p.add(new TrivialFunction(getInheritance(j,i,1)));
			}
		}
		
		if (connectBackward && i > 0)
		{
			for (int j=0; j<loc[i].genotypes().length; j++)
			{
				if (getInheritance(j,i,0) != null)
				{
					p.add(new FixedRecombination(getInheritance(j,i,0),getInheritance(j,i-1,0),d.getMaleRecomFrac(i,i-1)));
					p.removeVariable(getInheritance(j,i-1,0));
				}
				if (getInheritance(j,i,1) != null)
				{
					p.add(new FixedRecombination(getInheritance(j,i,1),getInheritance(j,i-1,1),d.getFemaleRecomFrac(i,i-1)));
					p.removeVariable(getInheritance(j,i-1,1));
				}
			}
		}
		if (connectForward && i+1 < loc.length)
		{
			for (int j=0; j<loc[i].genotypes().length; j++)
			{
				if (getInheritance(j,i,0) != null)
				{
					p.add(new FixedRecombination(getInheritance(j,i+1,0),getInheritance(j,i,0),d.getMaleRecomFrac(i+1,i)));
					p.removeVariable(getInheritance(j,i+1,0));
				}
				if (getInheritance(j,i,1) != null)
				{
					p.add(new FixedRecombination(getInheritance(j,i+1,1),getInheritance(j,i,1),d.getFemaleRecomFrac(i+1,i)));
					p.removeVariable(getInheritance(j,i+1,1));
				}
			}
		}

		return p;
	}

	public Product individualMeiosisProduct(int i, int first)
	{
		Product p = new Product();
		AuxOneGenVariable pv = null;
		for (int j=first; j<loc.length; j++)
		{
			AuxOneGenVariable cv = new AuxOneGenVariable(getInheritance(i,j,0),getInheritance(i,j,1),loc[j]);
			p.add(new AuxLocusFunction(cv));
			if (pv != null)
				p.add(new AuxLinkFunction(cv,pv,d.getMaleRecomFrac(j,j-1),d.getFemaleRecomFrac(j,j-1)));
			pv = cv;
		}
		return p;
	}

	public Product founderPriorProduct(int first)
	{
		Product p = new  Product();
		LocusVariables[] l = getLocusVariables();
		for (int i=first; i<l.length; i++)
			l[i].addFounderPriors(p);
		return p;
	}

	public Product familyMeiosisProduct(int i, int first)
	{
		int[] fam = d.nuclearFamilies()[i];
		if (fam.length < 4)
			return null;

		Product p = new Product();
		AuxOneGenVariable pv = null;
		for (int j=first; j<loc.length; j++)
		{
			Set<Inheritance> pats = new LinkedHashSet<Inheritance>();
			Set<Inheritance> mats = new LinkedHashSet<Inheritance>();
			for (int k=2; k<fam.length; k++)
			{
				pats.add(getInheritance(fam[k],j,0));
				mats.add(getInheritance(fam[k],j,1));
			}
			AuxOneGenVariable cv = new AuxOneGenVariable(pats,mats,loc[j]);
			p.add(new AuxLocusFunction(cv));
			if (pv != null)
				p.add(new AuxLinkFunction(cv,pv,d.getMaleRecomFrac(j,j-1),d.getFemaleRecomFrac(j,j-1)));
			pv = cv;
		}
		return p;
	}

	public Product threeGenMeiosisProduct(int i, int first)
	{
		int[] fam = d.nuclearFamilies()[i];
                int ngrand = 0;
                for (int k=2; k<fam.length; k++)
                	ngrand += d.kids(fam[k]).length;
                if (ngrand <= 0)
			return null;

		Product p= new Product();
		AuxThreeGenVariable pv = null;
		for (int j=first; j<loc.length; j++)
		{
			Set<Inheritance> hp = new LinkedHashSet<Inheritance>();
			Set<Inheritance> hm = new LinkedHashSet<Inheritance>();
			Set<Inheritance> lp = new LinkedHashSet<Inheritance>();
			Set<Inheritance> lm = new LinkedHashSet<Inheritance>();
					
			for (int k=2; k<fam.length; k++)
			{
				hp.add(getInheritance(fam[k],j,0));
				hm.add(getInheritance(fam[k],j,1));
				int[] kids = d.kids(fam[k]);
				for (int kk=0; kk<kids.length; kk++)
				{
					if (fam[k] == d.pa(kids[kk]))
						lp.add(getInheritance(kids[kk],j,0));
					if (fam[k] == d.ma(kids[kk]))
						lm.add(getInheritance(kids[kk],j,1));
				}
			}

			AuxThreeGenVariable cv = new AuxThreeGenVariable(hp,hm,lp,lm,loc[j]);
			p.add(new AuxLocusFunction(cv));
			if (pv != null)
				p.add(new AuxLinkFunction(cv,pv,d.getMaleRecomFrac(j,j-1),d.getFemaleRecomFrac(j,j-1)));
			pv = cv;
		}
		return p;
	}

/*
	// This version is doesn't depend on thep prepeeling of the locus products
	// which may be useful for debugging.
	public Product peeledSetOfMeiosesProduct(int[] x, int first)
	{	
		Product p = new Product();
		for (int j=first; j<loc.length; j++)
		{
			p.add(locusProduct(j).getFunctions());
			if (j > first)
			{
				for (int i=0; i<x.length; i++)
				{
					p.add(new FixedRecombination(getInheritance(x[i],j,0),getInheritance(x[i],j-1,0),d.getMaleRecomFrac(j,j-1)));
					p.add(new FixedRecombination(getInheritance(x[i],j,1),getInheritance(x[i],j-1,1),d.getFemaleRecomFrac(j,j-1)));
				}
			}
		}
		return p;
	}
*/

       public Product peeledSetOfMeiosesProduct(int[] x, int first)
        {
                Product p = new Product();
                LinkedHashSet<Inheritance> prevpat = null;
                LinkedHashSet<Inheritance> prevmat = null;

                for (int j=first; j<loc.length; j++)
                {
                        LinkedHashSet<Inheritance> pats = new LinkedHashSet<Inheritance>();
                        LinkedHashSet<Inheritance> mats = new LinkedHashSet<Inheritance>();
                        for (int i=0; i<x.length; i++)
                        {
                                pats.add(getInheritance(x[i],j,0));
                                mats.add(getInheritance(x[i],j,1));
                        }
                        LinkedHashSet<Variable> curall = new LinkedHashSet<Variable>();
                        curall.addAll(pats);
                        curall.addAll(mats);

                        Product q = locusProduct(j);
                        for (Iterator<Variable> i = q.getVariables().iterator(); i.hasNext(); )
                        {
                                Variable v = i.next();
                                if (v instanceof Inheritance && !curall.contains(v))
                                        q.removeVariable(v);
                        }
                        q.triangulate(curall);

                        GraphicalModel g = new GenotypeModel(q,curall);
                        p.add(g.meanGetFinals().getFunctions());

                        if (prevpat != null)
                        {
                                for (Iterator<Inheritance> cur=pats.iterator(), prv = prevpat.iterator(); cur.hasNext() && prv.hasNext(); )
                                        p.add(new FixedRecombination(cur.next(),prv.next(),d.getMaleRecomFrac(j,j-1)));
                                for (Iterator<Inheritance> cur=mats.iterator(), prv = prevmat.iterator(); cur.hasNext() && prv.hasNext(); )
                                        p.add(new FixedRecombination(cur.next(),prv.next(),d.getFemaleRecomFrac(j,j-1)));
                        }
                        prevpat = pats;
                        prevmat = mats;
                }
                p.triangulate();
                return p;
        }

	public Product completeProduct()
	{
		return completeProduct(1);
	}

	public Product completeProduct(int first)
	{
		Product p = new Product();
		for (int i=first; i<loc.length; i++)
			p.addProduct(locusProduct(i));

		for (int i=first+1; i<loc.length; i++)
		{
			for (int j=0; j<loc[i].genotypes().length; j++)
			{
				if (getInheritance(j,i,0) != null)
					p.add(new FixedRecombination(getInheritance(j,i,0),getInheritance(j,i-1,0),d.getMaleRecomFrac(i,i-1)));
				if (getInheritance(j,i,1) != null)
					p.add(new FixedRecombination(getInheritance(j,i,1),getInheritance(j,i-1,1),d.getFemaleRecomFrac(i,i-1)));
			}
		}

		return p;
	}

	public void report()
	{
		for (int i=1; i<loc.length; i++)
		{
			for (int j=0; j<loc[i].genotypes().length; j++)
				if (getInheritance(j,i,0) != null)
					System.out.print(getInheritance(j,i,0).getState()+""+getInheritance(j,i,1).getState()+" ");
			System.out.println();
		}
		System.out.println();
	}

	public void save(int first)
	{
		for (int i=first; i<loc.length; i++)
			loc[i].save();
	}
		
	public void restore(int first)
	{
		for (int i=first; i<loc.length; i++)
			loc[i].restore();
	}
}

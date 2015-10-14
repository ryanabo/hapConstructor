package alun.genepi;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import alun.markov.GraphicalModel;
import alun.mcld.LDModel;

public class GenotypeOnlyDropSampler
{
	public GenotypeOnlyDropSampler(LinkageVariables lv)
	{
		this(lv, 1);
	}

	public GenotypeOnlyDropSampler(LinkageVariables v, int f)
	{
		this(v,f,null);
	}

	public GenotypeOnlyDropSampler(LinkageVariables lv, LDModel ld)
	{
		this(lv,1,ld);
	}

	public GenotypeOnlyDropSampler(LinkageVariables lv, int f, LDModel lldd)
	{
		ld = lldd; 
		vars = lv;
		first = f;

		mrec = new double[vars.nLoci()];
		frec = new double[vars.nLoci()];
		for (int j=first+1; j<vars.nLoci(); j++)
		{
			mrec[j] = vars.getDataSource().getMaleRecomFrac(j,j-1);
			frec[j] = vars.getDataSource().getFemaleRecomFrac(j,j-1);
		}

		if (ld == null)
		{
			genos = new Genotype[vars.nLoci()];
			for (int i=first; i<genos.length; i++)
				genos[i] = new Genotype(vars.nAlleles(i));

			priorgm = new GraphicalModel(vars.genericPriorProduct(first,genos));
			priorgm.allocateOutputTables();
			priorgm.allocateInvolTables();
			priorgm.collect();
		}
		else
		{

			if (lv.nLoci() + first != ld.getVariables().size())
				throw new RuntimeException("Mismatch between number of variables in LD model and number of loci.");

			patal = new Allele[vars.nLoci()];
			matal = new Allele[vars.nLoci()];

			for (int i=first; i<matal.length; i++)
			{
				matal[i] = new Allele(vars.nAlleles(i));
				patal[i] = new Allele(vars.nAlleles(i));
			}
		}

		list = dropOrder(vars);
	}

	public void sample()
	{
		for (int k=0; k<list.length; k++) 
		{
			int i = list[k];

			if (vars.isFounder(i))
			{
				if (priorgm != null)
				{
					priorgm.drop();
					for (int j=first; j<vars.nLoci(); j++)
						vars.getGenotype(i,j).setState(genos[j].getState());
				}
				else
				{
					ld.simulate();
					for (int j=first; j<vars.nLoci(); j++)
						patal[j].setState(ld.getLocus(j).getState());

					ld.simulate();
					for (int j=first; j<vars.nLoci(); j++)
						matal[j].setState(ld.getLocus(j).getState());

					for (int j=first; j<vars.nLoci(); j++)
						vars.getGenotype(i,j).setState(patal[j].getState(),matal[j].getState());
				}
			}
			else
			{
				int pat = 0;
				int mat = 0;

				for (int j=first; j<vars.nLoci(); j++)
				{
					if (j == first)
					{
						pat = ( Math.random() <= 0.5 ? 0 : 1 );
						mat = ( Math.random() <= 0.5 ? 0 : 1 );
					}
					else
					{
						if ( Math.random() <= mrec[j] )
							pat = 1-pat;
						if ( Math.random() <= frec[j] )
							mat = 1-mat;
					}

					int x = ( pat == 0 ? vars.fathersGenotype(i,j).pat() : vars.fathersGenotype(i,j).mat() );
					int y = ( mat == 0 ? vars.mothersGenotype(i,j).pat() : vars.mothersGenotype(i,j).mat() );

					vars.getGenotype(i,j).setState(x,y);
				}
			}
		}
	}

	// Private data and methods.

	protected int first = 0;
	protected LinkageVariables vars = null;
	double[] mrec = null;
	double[] frec = null;

	private LDModel ld = null;
	private int[] list = null;
	private GraphicalModel priorgm = null;
	private Genotype[] genos = null;
	private Allele[] patal = null;
	private Allele[] matal = null;

	private int[] dropOrder(LinkageVariables v)
	{
		LocusVariables lv = v.getLocusVariables()[0];
		Genotype[] g = lv.genotypes();
		LinkedHashSet<Genotype> s = new LinkedHashSet<Genotype>();
		LinkedHashSet<Genotype> t = new LinkedHashSet<Genotype>();
		LinkedHashMap<Genotype,Integer> h = new LinkedHashMap<Genotype,Integer>();

		for (int i=0; i<g.length; i++)
		{
			s.add(g[i]);
			h.put(g[i],i);
		}

		while (!s.isEmpty())
		{
			for (Iterator<Genotype> y = s.iterator(); y.hasNext(); )
			{
				Genotype x = y.next();
				if (!s.contains(lv.pa.get(x)) && !s.contains(lv.ma.get(x)))
				{
					t.add(x);
					y.remove();
				}
			}
		}

		int[] l = new int[t.size()];
		int k = 0;
		for (Genotype x : t)
			l[k++] = h.get(x).intValue();

		return l;
	}
}

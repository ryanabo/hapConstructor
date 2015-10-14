package alun.mcld;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import alun.genepi.Allele;
import alun.genepi.AlleleErrorPenetrance;
import alun.genepi.Error;
import alun.genepi.ErrorPrior;
import alun.genio.BasicGeneticData;
import alun.markov.GraphicalModel;
import alun.markov.Product;
import alun.markov.Variable;

public class Diplotypes extends ImperfectHaplotypes
{
	public Diplotypes(BasicGeneticData dat, double errprob) throws IOException
	{
		this(dat,errprob,0);
	}

	public Diplotypes(BasicGeneticData dat, double errprob, int frst) throws IOException
	{
		data = dat;
		first = frst;

		v = new Vector<int[]>();
		for (int i=0; i<dat.nIndividuals(); i++)
		{
			v.add(new int[data.nLoci()-first]);
			v.add(new int[data.nLoci()-first]);
		}


		pats = new Vector<Allele>();
		mats = new Vector<Allele>();
		pen = new Vector<AlleleErrorPenetrance>();

		locs = new Vector<Locus>();
		prod = new Product[data.nLoci()-first];

		phash = new HashMap<Variable,Variable>();
		mhash = new HashMap<Variable,Variable>();

		for (int j=first; j<data.nLoci(); j++)
		{
			prod[j-first] = new Product();

			Error e = new Error();
			prod[j-first].add(new ErrorPrior(e,errprob));

			Allele p = new Allele(data.nAlleles(j));
			Allele m = new Allele(data.nAlleles(j));
			AlleleErrorPenetrance aep = new AlleleErrorPenetrance(p,m,e,null);
			prod[j-first].add(aep);

			pen.add(aep);
			pats.add(p);
			mats.add(m);

			Locus ll = new Locus(j-first,dat.nAlleles(j),(1.0+j-first));
			ll.setName(""+j);
			locs.add(ll);

			phash.put(ll,p);
			mhash.put(ll,m);
		}

		update(null);
	}

	public boolean update(LDModel m)
	{
		return update(m,true);
	}

	public boolean maximize(LDModel m)
	{
		return update(m,false);
	}

	public boolean update(LDModel model, boolean sample)
	{
		if (model == null)
			return startup(sample);
		
		Product ldprod = new Product();

		ldprod.addProduct(model.replicate(phash));
		ldprod.addProduct(model.replicate(mhash));

		for (Variable var : model.getVariables())
			ldprod.addProduct(prod[((Locus)var).getIndex()]);
		
		ldprod.triangulate();

		GraphicalModel gm = new GraphicalModel(ldprod);
		gm.allocateOutputTables();
		gm.allocateInvolTables();
		
		for (int i=0; i<data.nIndividuals(); i++)
		{
			for (Variable var : model.getVariables())
			{
				int j = ((Locus)var).getIndex();
				pen.get(j).fix(data.penetrance(i,j+first));
			}

			if (sample)
				gm.collect();
			else
				gm.max();
			gm.drop();

			for (Variable var : model.getVariables())
			{
				int j = ((Locus)var).getIndex();
				v.get(2*i)[j] = pats.get(j).getState();
				v.get(2*i+1)[j] = mats.get(j).getState();
			}
		}

		gm.clearOutputTables();
		gm.clearInvolTables();

		return true;
	}

	private boolean startup(boolean sample)
	{
		for (int j=0; j<prod.length; j++)
		{
			GraphicalModel gm = new GraphicalModel(prod[j]);
			gm.allocateOutputTables();
			gm.allocateInvolTables();
		
			for (int i=0; i<data.nIndividuals(); i++)
			{
				pen.get(j).fix(data.penetrance(i,j+first));
	
				if (sample)
					gm.collect();
				else
					gm.max();
				gm.drop();
	
				v.get(2*i)[j] = pats.get(j).getState();
				v.get(2*i+1)[j] = mats.get(j).getState();
			}

			gm.clearOutputTables();
			gm.clearInvolTables();
		}

		return true;
	}

// Private data.
	
	private BasicGeneticData data = null;
	private int first = 0;
	private Product[] prod = null;
	private Vector<Allele> pats = null;
	private Vector<Allele> mats = null;
	private Vector<AlleleErrorPenetrance> pen = null;
	private Map<Variable,Variable> phash = null;
	private Map<Variable,Variable> mhash = null;
}

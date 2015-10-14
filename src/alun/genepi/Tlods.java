package alun.genepi;

import java.util.LinkedHashMap;

import alun.markov.Function;
import alun.markov.GraphicalModel;
import alun.markov.Product;
import alun.markov.Variable;

public class Tlods
{
	public Tlods(LociVariables linkv, double[] t)
	{
		lv = linkv;
		theta = new Theta(t);
		Product tp = lv.traitProduct(theta);

		for (Variable v : tp.getVariables())
			if (v != theta)
				tp.fillIn(v,theta);

		twoptmod = new GenotypeModel(tp,theta);

		hash = new LinkedHashMap<Inheritance,VariableRecombination>();
		for (Function f : tp.getFunctions())
			if (f instanceof VariableRecombination)
			{
				VariableRecombination vr = (VariableRecombination) f;
				hash.put(vr.getInheritance(),vr);
			}
	}

	public int nThetas()
	{
		return theta.getNStates();
	}

	public LociVariables getVariables()
	{
		return lv;
	}

	public double[] twoPointLikelihood(int j)
	{
		LociVariables lv = getVariables();
/*
		for (int i=0; i<lv.nIndividuals(); i++)
		{
			Inheritance h = null;
			h = lv.getInheritance(i,j,0);
			if (h != null)
				lv.recombination(i,0).fix(h.getState());
			h = lv.getInheritance(i,j,1);
			if (h != null)
				lv.recombination(i,1).fix(h.getState());
		}
*/
		for (int i=0; i<lv.nIndividuals(); i++)
		{
			Inheritance h = lv.getInheritance(i,j,0);
			if (h != null)
				hash.get(lv.getInheritance(i,0,0)).fix(h.getState());
			h = lv.getInheritance(i,j,1);
			if (h != null)
				hash.get(lv.getInheritance(i,0,1)).fix(h.getState());
		}

		twoptmod.allocateOutputTables();
		twoptmod.peel();
		Product marg = twoptmod.getFinals();
		double[] lik = new double[nThetas()];
		for (theta.init(); theta.next(); )
			lik[theta.getState()] = marg.getValue();
		twoptmod.clearOutputTables();

		return lik;
	}

/*
	public double[] combine(double[][] x)
	{
		double thresh = 0.000001;
		double[] z = new double[x[0].length];
		for (int j=0; j<z.length; j++)
			z[j] = 1;
		
		for (double error = 1000; error > thresh; )
		{
			double[] y = new double[z.length];
			for (int i=0; i<x.length; i++)
			{
				double sum = 0;
				for (int j=0; j<z.length; j++)
					sum += x[i][j]/z[j];

				for (int j=0; j<y.length; j++)
					y[j] += x[i][j]/sum;
			}

			error = 0;
			for (int j=0; j<y.length; j++)
				error += Math.abs(y[j]-z[j]); 
			z = y;
		}

		return z;
	}
*/

// Private data.

	private LinkedHashMap<Inheritance,VariableRecombination> hash = null;
	protected Theta theta = null;
	private LociVariables lv = null;
	private GraphicalModel twoptmod = null;
}

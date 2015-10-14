package alun.genepi;

import java.util.Collection;
import java.util.LinkedHashSet;

import alun.markov.Function;
import alun.markov.GraphicalModel;
import alun.markov.Product;
import alun.markov.Variable;

public class AuxiliaryModel extends GraphicalModel
{
	public AuxiliaryModel(Product p)
	{
		super(p);
		var = new LinkedHashSet<AuxiliaryVariable>();
		fun = new LinkedHashSet<AuxiliaryFunction>();
		for (Function f : p.getFunctions())
		{
			fun.add((AuxiliaryFunction)f);
			for (Variable v : f.getVariables())
				var.add((AuxiliaryVariable)v);
		}
	}

	public void collect()
	{
		for (AuxiliaryVariable v : var)
			v.remember();
		for (AuxiliaryFunction f : fun)
			f.preCompute();
		super.collect();
	}

	public void drop()
	{
		super.drop();
		for (AuxiliaryVariable v : var)
			v.set();
	}

// Private data.

	private Collection<AuxiliaryVariable> var = null;
	private Collection<AuxiliaryFunction> fun = null;
}

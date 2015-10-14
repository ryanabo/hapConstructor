package alun.genepi;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import alun.markov.BasicClique;
import alun.markov.Clique;
import alun.markov.Function;
import alun.markov.GraphicalModel;
import alun.markov.Product;
import alun.markov.Variable;

public class GenotypeModel extends GraphicalModel
{
	public GenotypeModel(Product p)
	{
		super(p);
	}

	public GenotypeModel(Product p, Collection<Variable> keep)
	{
		super(p,keep);
	}

	public GenotypeModel(Product p, Variable keep)
	{
		super(p,keep);
	}

	public Clique makeClique(Set<Variable> v, Clique next, Product p)
	{
		Set<Variable> t = null;

		if ((t=getTripletClique(v,p)) != null)
			return new TripletClique(t,next);

		return new BasicClique(sortinvol(v,p),next);
	}

	private LinkedHashSet<Variable> sortinvol(Set<Variable> v, Product p)
	{
		LinkedHashSet<Variable> w = new LinkedHashSet<Variable>();
		LinkedHashSet<Variable> u = new LinkedHashSet<Variable>(v);
		Set<Function> f = p.getFunctions(v);
		for (Iterator<Function> i = f.iterator(); i.hasNext(); )
		{
			Function s = i.next();
			if (s instanceof MendelianTransmission && v.containsAll(s.getVariables()))
				w.addAll(s.getVariables());
		}
		w.addAll(u);	
		return w;
	}

	public Set<Variable> getTripletClique(Set<Variable> v,Product p)
	{
		if (v.size() == 3)
		{
			Set<Function> f = p.getFunctions(v);
			for (Iterator<Function> i = f.iterator(); i.hasNext(); )
			{
				Function s = i.next();
				if (s instanceof MendelianTransmission && s.getVariables().equals(v))
					return s.getVariables();
			}
		}
		return null;
	}
}

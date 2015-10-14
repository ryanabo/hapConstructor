package alun.genepi;

import java.util.LinkedHashSet;
import java.util.Set;

import alun.markov.GraphicalModel;

abstract public class AuxiliaryVariable extends GeneticVariable
{
	abstract public void set();

	public AuxiliaryVariable(int i, LocusVariables lig)
	{
		super(i);
		gm = lig.fixedInheritanceGM();
	}

	public double getValue()
	{
		return gm.peel();
	}

	public void remember()
	{
		for (Inheritance i : pats)
			i.remember();
		for (Inheritance i : mats)
			i.remember();
	}

	public void recall()
	{
		for (Inheritance i : pats)
			i.recall();
		for (Inheritance i : mats)
			i.recall();
	}

	public LinkedHashSet<Inheritance> getPats()
	{
		return pats;
	}
	
	public LinkedHashSet<Inheritance> getMats()
	{
		return mats;
	}

// Protected data and methods.

	private GraphicalModel gm = null;
	protected LinkedHashSet<Inheritance> pats = null;
	protected LinkedHashSet<Inheritance> mats = null;

	protected void flip(Set<Inheritance> s)
	{
		for (Inheritance i : s)
			i.flip();
	}
}

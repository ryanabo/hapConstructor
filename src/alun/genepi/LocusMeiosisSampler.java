package alun.genepi;

import java.util.LinkedHashSet;
import java.util.Set;

import alun.markov.GraphicalModel;
import alun.markov.Product;

public class LocusMeiosisSampler extends LinkedLocusSampler
{
	public LocusMeiosisSampler()
	{
	}

	public LocusMeiosisSampler(LinkageVariables vars)
	{
		this(vars,2,1);
	}

	public LocusMeiosisSampler(LinkageVariables vars, int level)
	{
		this(vars,level,1);
	}

	public LocusMeiosisSampler(LinkageVariables vars, int level, int first)
	{
		setInitialBlocks(independentLocusBlocks(vars,first));
		setLocusBlocks(linkedLocusBlocks(vars,first));
		Set<GraphicalModel>  s = new LinkedHashSet<GraphicalModel>();

		switch(level)
		{
		case 2: 
			s.addAll(threeGenMeiosisBlocks(vars,first));
		case 1: 
			s.addAll(familyMeiosisBlocks(vars,first));
		case 0:
		default: 
			s.addAll(individualMeiosisBlocks(vars,first));
		}
		setMeiosisBlocks(s);

		setThrifty(true);
		//initialize();
	}

	protected Set<GraphicalModel> individualMeiosisBlocks(LinkageVariables l, int first)
	{
		Set<GraphicalModel> s = new LinkedHashSet<GraphicalModel>();
		for (int i=0; i<l.nIndividuals(); i++)
		{
			if (!l.isFounder(i))
			{
				Product p = l.individualMeiosisProduct(i,first);
				s.add(new AuxiliaryModel(p));
			}
		}
		return s;
	}

	protected LinkedHashSet<GraphicalModel> familyMeiosisBlocks(LinkageVariables l, int first)
	{
		LinkedHashSet<GraphicalModel> s = new LinkedHashSet<GraphicalModel>();
		for (int i=0; i<l.nFamilies(); i++)
		{
			Product p = l.familyMeiosisProduct(i,first);
			if (p != null)
				s.add(new AuxiliaryModel(p));
		}
		return s;
	}

	protected LinkedHashSet<GraphicalModel> threeGenMeiosisBlocks(LinkageVariables l, int first)
	{
		LinkedHashSet<GraphicalModel> s = new LinkedHashSet<GraphicalModel>();
		for (int i=0; i<l.nFamilies(); i++)
		{
			Product p = l.threeGenMeiosisProduct(i,first);
			if (p != null)
				s.add(new AuxiliaryModel(p));
		}
		return s;
	}
}

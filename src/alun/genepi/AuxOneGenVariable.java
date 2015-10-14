package alun.genepi;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class AuxOneGenVariable extends AuxiliaryVariable
{
	public AuxOneGenVariable(Inheritance p, Inheritance m, LocusVariables lg)
	{
		this(Collections.singleton(p),Collections.singleton(m),lg);
	}

	public AuxOneGenVariable(Set<Inheritance> p, Set<Inheritance> m, LocusVariables lg)
	{
		super(4,lg);
		pats = new LinkedHashSet<Inheritance>(p);
		mats = new LinkedHashSet<Inheritance>(m);
	}

	public String toString()
	{
		return "C("+pats+","+mats+")";
	}

	public void set()
	{
		recall();

		switch(getState()) 
		{
		case 0: break;
		case 1: flip(mats);
			break;
		case 2: flip(pats);
			break;
		case 3: flip(pats);
			flip(mats);
			break;
		default:
			throw new RuntimeException("Invalid state for control variable = "+getState());
		}
	}
}

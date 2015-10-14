package alun.genepi;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class AuxThreeGenVariable extends AuxiliaryVariable
{
	public AuxThreeGenVariable(Set<Inheritance> hp, Set<Inheritance> hm, Set<Inheritance> lp, Set<Inheritance> lm, LocusVariables lg)
	{
		super(2,lg);

		pats = new LinkedHashSet<Inheritance>(hp);
		pats.addAll(lp);
		mats = new LinkedHashSet<Inheritance>(hm);
		mats.addAll(lm);

		hipats = new LinkedHashSet<Inheritance>(hp);
		himats = new LinkedHashSet<Inheritance>(hm);
		lopats = new LinkedHashSet<Inheritance>(lp);
		lomats = new LinkedHashSet<Inheritance>(lm);
	} 

	public String toString()
	{
		return "TG"+getName();
	}

	public void set()
	{
		recall();

		switch(getState()) 
		{
		case 0: break;
		case 1: flip(lomats);
			flip(lopats);
			for (Iterator<Inheritance> i=hipats.iterator(), j=himats.iterator(); i.hasNext() && j.hasNext(); )
			{
				Inheritance pat = i.next();
				Inheritance mat = j.next();
				int t = pat.getState();	
				pat.setState(mat.getState());
				mat.setState(t);
			}
			break;
		default:
			throw new RuntimeException("Invalid state for control variable = "+getState());
		}
	}

// Private data.

	private LinkedHashSet<Inheritance> hipats = null;
	private LinkedHashSet<Inheritance> himats = null;
	private LinkedHashSet<Inheritance> lopats = null;
	private LinkedHashSet<Inheritance> lomats = null;
}

package alun.markov;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

abstract public class Clique 
{
	abstract public double peel(); 
	abstract public void collect();
	abstract public void max();
	abstract public void drop();
	//abstract public LinkedHashMap<Variable,Table> distribute();
	abstract public void distribute();
	
	public void freeUp()
	{
		for (Function f : inputs)
			if (f instanceof Table)
				((Table)f).freeSpace();
	}

	public Clique(Set<Variable> inv, Clique nx)
	{
		invol = new LinkedHashSet<Variable>(inv);
		inputs = new LinkedHashSet<Function>();
		prev = new LinkedHashSet<Clique>();
		next = nx;
		if (next != null)
			next.prev.add(this);

		output = TableMaker.makeTable(cutset(),false);
		rinvol = TableMaker.makeTable(invol(),true);
	}
 
	public LinkedHashSet<Clique> previous()
	{
		return prev;
	}

	public Clique next()
	{
		return next;
	}

	public LinkedHashSet<Variable> invol()
	{
		return invol;
	}

	public LinkedHashSet<Variable> cutset()
	{
		LinkedHashSet<Variable> c = new LinkedHashSet<Variable>();
		if (next != null)
			c.addAll(next.invol);
		c.retainAll(invol);
		return c;
	}

	public LinkedHashSet<Variable> peeled()
	{
		LinkedHashSet<Variable> c = new LinkedHashSet<Variable>(invol);
		if (next != null)
			c.removeAll(next.invol);
		return c;
	}

	public LinkedHashSet<Function> inputs()
	{
		return inputs;
	}

	public double scale()
	{	
		return scale;
	}

	public Table getOutputTable()
	{
		return output;
	}

	public Table getInvolTable()
	{
		return rinvol;
	}
		
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append(invol()+" = "+peeled()+" -> "+cutset());
		return s.toString();
	}

	public Function[][] orderInputs(Variable[] v)
	{
		LinkedHashSet<Function> f = new LinkedHashSet<Function>(inputs);
		for (Iterator<Clique> i=prev.iterator(); i.hasNext(); )
		{
			Clique c = i.next();
			f.add(c.output);
		}
		return orderInputs(v,f);
	}

	public Function[][] orderInputs(Variable[] v, Set<Function> f)
	{
		LinkedHashSet<Function>[] u = (LinkedHashSet<Function>[])new LinkedHashSet[v.length];
		for (int i=0; i<u.length; i++)
			u[i] = new LinkedHashSet<Function>();
		for (Iterator<Function> i = f.iterator(); i.hasNext(); )
		{
			Function fi = i.next();
			for (int j=v.length-1; j>=0; j--)
				if (fi.getVariables().contains(v[j]))
				{
					u[j].add(fi);
					break;
				}
		}

		Function[][] ff = new Function[u.length][];
		for (int i=0; i<ff.length; i++)
			ff[i] = (Function[]) u[i].toArray(new Function[0]);
		return ff;
	}

	public double involSize()
	{
		double x = 1;
		for (Variable v : invol)
			x *= v.getNStates();
		return x;
	}

	public double outputSize()
	{
		double x = 1;
		for (Variable v : cutset())
			x *= v.getNStates();
		return x;
	}

// Protected data.

	private LinkedHashSet<Variable> invol = null;
	private LinkedHashSet<Function> inputs = null;
	private LinkedHashSet<Clique> prev = null;
	private Clique next = null;

	protected double scale = 0;
	protected Table output = null;
	protected Table rinvol = null;

	protected Variable[] arrayOf(Set<Variable> s)
	{
		return (Variable[]) s.toArray(new Variable[0]);
	}

	protected void init(Variable[] u)
	{
		for (int i=0; i<u.length; i++)
			u[i].init();
	}
}

package alun.markov;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import alun.util.DoubleValue;
import alun.util.IntArray;

public class VerySparseTable implements Table
{
	public VerySparseTable(Function f)
	{
		this(f.getVariables());
		IteratorFunction i = new IteratorFunction(f.getVariables());
		for (i.init(); i.next(); )
		{
			double x = f.getValue();
			if (x > 0)
				setValue(x);
		}
	}

	public VerySparseTable(Collection<? extends Variable> vars)
	{
		v = new LinkedHashSet<Variable>(vars);
		h = new LinkedHashMap<IntArray,DoubleValue>();
	}

	public Set<Variable> getVariables()
	{
		return v;
	}

	public double getValue()
	{
		DoubleValue d = h.get(state());
		return d == null ? 0 : d.x;
	}

	public void setValue(double d)
	{
		if (d != 0)
			h.put(state(),new DoubleValue(d));
	}

	public void increase(double x)
	{
		if (x == 0)
			return ;

		IntArray s = state();
		DoubleValue d = h.get(s);
		if (d == null)
			h.put(s,new DoubleValue(x));
		else
			d.x += x;
	}

	public void multiply(double x)
	{
		if (x == 0)
		{
			h.remove(state());
		}
		else
		{
			DoubleValue d = h.get(state());
			if (d != null)
				d.x *= x;
		}
	}

	public void initToZero()
	{
		h.clear();
	}

	public double sum()
	{
		double x = 0;
		for (DoubleValue d : h.values())
			x += d.x;
		return x;
	}

	public void scale(double x)
	{
		for (DoubleValue d : h.values())
			d.x *= x;
	}

	public void invert()
	{
		for (DoubleValue d : h.values())
			if (d.x > 0)
				d.x = 1/d.x;
	}

	public void init()
	{
		iter = h.keySet().iterator();
	}

	public boolean next()
	{
		boolean ok = false;
		while ((ok = iter.hasNext()) && !setStates(iter.next())) { iter.remove(); }
		return ok;
	}

	public int size()
	{
		return h.keySet().size();
	}

	public void allocateSpace()
	{
	}

	public void freeSpace()
	{
		h.clear();
	}

	public String toString()
	{
		return "VerySparseTable: " + getVariables().toString();
	}

// Private data and methods.

	private Set<Variable> v = null;
	private Map<IntArray,DoubleValue> h = null;
	private Iterator<IntArray> iter = null;

	private IntArray state()
	{
		int[] a = new int[v.size()];
		int i = 0;
		for (Variable u : v)
			a[i++] = u.getState();
		return new IntArray(a);
	}

	private boolean setStates(IntArray a)
	{
		int i=0;
		for (Variable u : v)
			if (!u.setState(a.get(i++)))
				return false;
		return true;
	}

/**
 	Test main.
*/
	public static void main(String[] args)
	{
		Set<Variable> v = new LinkedHashSet<Variable>();
		v.add(new BasicVariable(5));
		v.add(new BasicVariable(4));

		Table t = new VerySparseTable(v);

		for (Variable u : t.getVariables())
			u.setState(2);
		t.setValue(4); 
		for (Variable u : t.getVariables())
			u.setState(3);
		t.setValue(5); 

		for (t.init(); t.next(); )
		{
			for (Variable u : t.getVariables())
				System.out.print(u+" ("+u.getState()+")"+"\t");
			System.out.println(t.getValue());
		}
		

		BasicVariable uu = new BasicVariable(3);
		uu.setName("uu");
		BasicVariable vv = new BasicVariable(4);
		vv.setName("vv");
		t = new VerySparseTable(new EqualityFunction(uu,vv));
		for (t.init(); t.next(); )
		{
			for (Variable u : t.getVariables())
				System.out.print(u+" ("+u.getState()+")"+"\t");
			System.out.println(t.getValue());
		}
	}
}

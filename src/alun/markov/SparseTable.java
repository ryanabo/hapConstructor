package alun.markov;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import alun.util.DoubleValue;
import alun.util.IntValue;

public class SparseTable extends AbstractTable
{
	public SparseTable(Function f)
	{
		this(f.getVariables());
		IteratorFunction i = new IteratorFunction(f.getVariables());
		for (i.init(); i.next(); )
			setValue(f.getValue());
	}

	public SparseTable(Collection<? extends Variable> vars)
	{
		super(vars);
		h = new HashMap<IntValue,DoubleValue>();
	}

	public String toString()
	{
		return "SparseTable: "+getVariables();
	}

	public final double getValue()
	{
		DoubleValue d = h.get(new IntValue(getStateIndex()));
		return d == null ? 0 : d.x;
	}

	public final void setValue(double d)
	{
		if (d != 0)
			h.put(new IntValue(getStateIndex()),new DoubleValue(d));
	}

	public final void increase(double d)
	{
		if (d == 0)
			return;
		IntValue i = new IntValue(getStateIndex());
		DoubleValue v = h.get(i);
		if (v == null)
			h.put(i,new DoubleValue(d));
		else
			v.x += d;
	}

	public final void multiply(double d)
	{
		if (d == 0)
			h.remove(new IntValue(getStateIndex()));
		else
		{
			DoubleValue v = h.get(new IntValue(getStateIndex()));
			if (v != null)
				v.x *= d;
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
		iter = new LinkedHashSet<IntValue>(h.keySet()).iterator();
	}

	public boolean next()
	{
		boolean ok = false;
		while ((ok=iter.hasNext()) && !setStateIndex(iter.next().i)) { iter.remove(); }
		return ok;
/*
		boolean ok = false;
		boolean goon = true;

		while (goon)
		{
			ok = iter.hasNext();
			if (!ok)
				goon = false;
			else
			{
				IntValue cur = iter.next();
				if (setStateIndex(cur.i))
					goon = false;
				else
				{
					//iter.remove();
					h.remove(cur);
					goon = true;
				}
			}
		}


		return ok;
*/

	}

	public int size()
	{
		return h.keySet().size();
	}

	public void freeSpace()
	{
		h.clear();
	}

	public void allocateSpace()
	{
	}

// Private data and methods

	private Map<IntValue,DoubleValue> h = null;
	private Iterator<IntValue> iter = null;
}

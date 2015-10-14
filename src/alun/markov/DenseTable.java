package alun.markov;

import java.util.Collection;

public class DenseTable extends AbstractTable
{
	public DenseTable(Function f)
	{
		this(f.getVariables());
		IteratorFunction i = new IteratorFunction(f.getVariables());
		for (i.init(); i.next(); )
			setValue(f.getValue());
	}

	public DenseTable(Collection<? extends Variable> vars)
	{
		this(vars,true);
	}


	public DenseTable(Collection<? extends Variable> vars, boolean allocate)
	{
		super(vars);

		try
		{
			if (allocate)
				allocateSpace();
		}
		catch (OutOfMemoryError e)
		{
			System.err.println("Allocating a dense table: " + getMaxStates());
			System.err.println(vars);
			throw(e);
		}
	}

	public DenseTable(Collection<? extends Variable> vars, double[] tab)
	{
		super(vars);
		table = tab;
	}

	public String toString()
	{
		return "DenseTable: "+getVariables();
	}

	public final double getValue()
	{
		return table[getStateIndex()];
	}

	public final void init()
	{
		count = -1;
	}

	public final boolean next()
	{
		while (++count < table.length && (!(table[count] > 0) || !setStateIndex(count))) { table[count] = 0; }
		return count < table.length;
	}
 
	public void setValue(double d)
	{
		table[getStateIndex()] = d;
	}

	public void increase(double d)
	{
		table[getStateIndex()] += d;
	}

	public void multiply(double d)
	{
		table[getStateIndex()] *= d;
	}

	public void initToZero()
	{
		for (int i=0; i<table.length; i++)
			table[i] = 0;
	}

	public double sum()
	{
		double t = 0;
		for (int i=0; i<table.length; i++)
			t += table[i];
		return t;
	}

	public void scale(double d)
	{
		for (int i=0; i<table.length; i++)
			table[i] *= d;
	}

	public void invert()
	{
		for (int i=0; i<table.length; i++)
			if (table[i] != 0)
				table[i] = 1/table[i];
	}

	public int size()
	{
		return table.length;
	}

	public void freeSpace()
	{
		table = null;
	}

	public void allocateSpace()
	{
		table = new double[getMaxStates()];
	}

	public double[] getArray()
	{
		return table;
	}

// Private data.

	private double[] table = null;
	private int count = 0;

/**
 	Test main.
*/
	public static void main(String[] args)
	{
		BasicVariable u = new BasicVariable(4);
		u.setName("u");
		BasicVariable v = new BasicVariable(3);
		v.setName("v");
		Function f = new EqualityFunction(u,v);
		DenseTable t = new DenseTable(f);
		DenseTable s = new DenseTable(t.getVariables(),t.getArray());

		for (t.init(); t.next(); )
			System.out.println(t+"\t"+t.getValue());
		for (s.init(); s.next(); )
			System.out.println(s+"\t"+s.getValue());
	}
}

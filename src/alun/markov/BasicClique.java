package alun.markov;

import java.util.Set;

public class BasicClique extends Clique
{
	public BasicClique(Set<Variable> inv, Clique nx)
	{
		super(inv,nx);
	}

	private Variable[] u = null;
	private Function[][] f = null;

	public void compute(Table[] t)
	{
		if (u == null)
		{
			u = arrayOf(invol());
			f = orderInputs(u);
		}

		for (int j=0; j<t.length; j++)
			t[j].initToZero();

		for (int j=0; j<u.length; j++)
			u[j].init();

		double[] x = new double[u.length+1];
		x[0] = 1;

		for (int i=0; i>=0; )
		{
			if (!u[i].next())
			{
				u[i].init();
				i--;
			}
			else
			{
				x[i+1] = x[i];
				for (int j=0; x[i+1] > 0 && j<f[i].length; j++)
					x[i+1] *= f[i][j].getValue();


				if (!(x[i+1] > 0)) 
					continue;

				if (++i == u.length)
				{
					for (int j=0; j<t.length; j++)
						t[j].increase(x[i]);
					i--;
				}
			}
		}
	}

	public double peel()
	{
		Table[] t = {output};
		compute(t);
		scale = output.sum();
		if (scale > 0)
			output.scale(1/scale);
		return scale;
	}

	public void collect()
	{
		Table[] t = {rinvol, output};
		compute(t);
		for (rinvol.init(); rinvol.next(); )
		{
			double x = output.getValue();
			if (x > 0)
				rinvol.multiply(1/x);
		}
			
		scale = output.sum();
		if (scale > 0)
			output.scale(1/scale);
	}

	public void distribute()
	{
		for (Clique c : previous())
			c.output.initToZero();

		for (rinvol.init(); rinvol.next(); )
		{
			double y = rinvol.getValue()*output.getValue();
		//	rinvol.setValue(y);
			if (y > 0)
				for (Clique c : previous())
					c.output.increase(y);
		}
	}

	public void max()
	{
		Table[] t = {rinvol};
		compute(t);

		output.initToZero();
		for (rinvol.init(); rinvol.next(); )
		{
			double y = rinvol.getValue();
			if (output.getValue() < y)
				output.setValue(y);
		}

		Table nmaxes = new SparseTable(output.getVariables());
		for (rinvol.init(); rinvol.next(); )
		{
			double max = output.getValue();
			if (max > 0)
			{
				if (rinvol.getValue()/max > 0.999999999999)
				{
					rinvol.setValue(1);
					nmaxes.increase(1);
				}
				else
				{
					rinvol.setValue(0);
				}
			}
		}

		for (rinvol.init(); rinvol.next(); )
		{
			double y = nmaxes.getValue();
			if (y > 0)
				rinvol.multiply(1/y);
		}

		scale = output.sum();
		if (scale > 0)
			output.scale(1/scale);
	}

	public void drop()
	{
		Variable[] u = arrayOf(peeled());
		init(u);
		double x = Math.random();
		double t = 0;

		for (int i=0; t<x && i>= 0; )
		{
			if (!u[i].next())
			{
				u[i].init();
				i--;
			}
			else
			{
				if (++i == u.length)
				{
					t += rinvol.getValue();
					i--;
				}
			}
		}

		if (t < x)
			throw new RuntimeException("No possible states when sampling. t = "+t+" x = "+x);
	}
}

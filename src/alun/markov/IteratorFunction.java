package alun.markov;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class IteratorFunction implements Function, Iterable
{
	public IteratorFunction(Collection<Variable> vars)
	{
		Set<Variable> vv = new LinkedHashSet<Variable>(vars);
		v = (Variable[]) vv.toArray(new Variable[0]);
	}

	public Set<Variable> getVariables()
	{
		Set<Variable> vv = new LinkedHashSet<Variable>();
		for (int i=0; i<v.length; i++)
			vv.add(v[i]);
		return vv;
	}

	public double getValue()
	{
		return 1;
	}

	public void init()
	{
		for (int i=0; i<v.length; i++)
			v[i].init();
		count = 0;
	}
	
	public boolean next()
	{
		while (count >=0)
		{
			if (count == v.length)
			{
				count--;
				return true;
			}

			if (v[count].next())
				count++;
			else
				v[count--].init();
		}
		return false;
	}


	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append("IteratorFunction:\t");
		for (int i=0; i<v.length; i++)
			s.append(v[i]+"="+v[i].getState()+"\t");
		return s.toString();
	}

// Private data
	
	private Variable[] v = null;
	private int count = 0;

/** 
	Test main.
*/
	public static void main(String[] args)
	{
		BasicVariable t = new BasicVariable(2);
		t.setName("t");
		BasicVariable u = new BasicVariable(3);
		u.setName("u");
		BasicVariable v = new BasicVariable(5);
		v.setName("v");

		Set<Variable> s = new LinkedHashSet<Variable>();
		s.add(t);
		s.add(u);
		s.add(v);

		IteratorFunction f = new IteratorFunction(s);
		for (f.init(); f.next(); )
			System.out.println(f);
	}
}

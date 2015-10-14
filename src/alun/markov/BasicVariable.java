package alun.markov;

/**
 A BasicVariable can take only positive integer values, and
 setStates can only restrict the possible states among those
 that were possible on construction.
*/
public class BasicVariable implements Variable
{
	public BasicVariable(int n)
	{
		m = new int[n];
		v = new int[n];
		for (int i=0; i<v.length; i++)
			m[i] = v[i] = i;
		name = "BasicVariable ("+(count++)+") ";
	}

	public void setName(String s)
	{
		name = s;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return name; 
	}

	public void init()
	{
		state = -1;
	}

	public boolean next()
	{
		if (++state < v.length)
			return true;
		else
		{
		//	state = -1;
			return false;
		}
// return ++state < v.length;
	}

	public int getState()
	{
		if (state < 0 || state >= v.length)
			System.err.println("Getting state out of bounds ("+name+") "+state+" "+v.length);
		return v[state];
	}

	public int getNStates()
	{
		return v.length;
	}

	public boolean setState(int i)
	{
//		if (i >= m.length || i < 0)
//			System.err.println("Setting state out of bounds ("+this+") "+i+" "+m.length);
//
//		if (m[i] < 0)
//		{
//			System.err.print("Setting invalid state ("+this+") "+i+" "+m[i]+" "+m.length+" [");
//			for (int j=0; j<m.length; j++)
//				System.err.print(" "+m[j]);
//			System.err.println("]");
//		}

		if (i >= m.length || i < 0)
			System.err.println("Outofbournds");

		if (i >= m.length || i < 0 || m[i] < 0)
			return false;
		
		state = m[i];
		return true;
	}

	public void setStates(int[] x)
	{
		for (int i=0; i<m.length; i++)
			m[i] = -1;

		v = new int[x.length];
		for (int i=0; i<v.length; i++)
		{
			v[i] = x[i];
			m[v[i]] = i;
		}
	}

	public int[] getStates()
	{
		return v;
	}

	public void setStates(Function f)
	{
		boolean[] b = new boolean[m.length];
		for (init(); next(); )
			b[getState()] = f.getValue() > 0;

		int c = 0;
		for (int i=0; i<b.length; i++)
			if (b[i])
				c++;

		int[] x = new int[c];
		c = 0;
		for (int i=0; i<b.length; i++)
			if (b[i])
				x[c++] = i;

		setStates(x);
	}

// 	Private data and methods.

	private int state = 0;
	private int[] v = null;
	private int[] m = null;
	private String name = null;

	private static int count = 0;

/**
 	Test main.
*/
	public static void main(String[] args)
	{
		Variable b = new BasicVariable(5);
		b.setState(2);
		System.out.println("2 = "+b.getState());

		for (b.init(); b.next(); )
			System.out.print("\t"+b.getState());
		System.out.println();

	}
}


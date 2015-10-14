package alun.util;

import java.util.Vector;

/** 
 This structure allows objects to be stored as if on an infinite
 integer lattice. One object per lattice point is allowed.
 This is done by wrapping around a finite square lattice. The size
 of the finite lattice can be controlled.
*/
public class Lattice<E>
{
/**
 Creates a new lattice with default actual size of 100x100.
*/
	public Lattice()
	{
		this(100);
	}

/**
 Creates a new lattice with actual size as specified.
*/
	public Lattice(int k)
	{
		n = k;
		m = (Vector<Holder<E>>[][]) new Vector[n][n];
	}

/**
 Puts the Object o at the point (ii, jj).
*/
	public void put(int ii, int jj, E o)
	{
		Holder<E> h = holder(ii,jj);
		if (h != null)
			h.obj = o;
		else
		{
			int i = map(ii);
			int j = map(jj);
			if (m[i][j] == null)
				m[i][j] = new Vector<Holder<E>>();
			m[i][j].add(new Holder<E>(ii,jj,o));
		}
	}

/**
 Gets the Object currently stored at the point (i,j).
 If there is no Object there it returns null.
*/
	public E get(int i, int j)
	{
		Holder<E> h = holder(i,j);
		return h == null ? null : h.obj;
	}

/**
 Removes the E currently stored at the point (i,j).
*/
	public void remove(int i, int j)
	{
		Holder<E> h = holder(i,j);
		if (h != null)
			m[map(i)][map(j)].remove(h);
	}

// Private data methods and classes.

	private Vector<Holder<E>>[][] m = null;
	private int n = 0;

	private Holder<E> holder(int ii, int jj)
	{
		int i = map(ii);
		int j = map(jj);
		if (m[i][j] != null)
		{
			for (int k=0; k<m[i][j].size(); k++)
			{
				Holder<E> h = m[i][j].elementAt(k);
				if (h.i == ii && h.j == jj)
					return h;
			}
		}
		return null;
	}

	private int map(int ii)
	{
		return ((ii%n)+n)%n;
	}

	private class Holder<E>
	{
		public Holder(int ii, int jj, E o)
		{
			i = ii;
			j = jj;
			obj = o;
		}
	
		public int i = 0;
		public int j = 0;
		public E obj = null;
	}

/**
 Test main.
*/
	public static void main(String[] args)
	{
		Lattice<String> m = new Lattice<String>();
		m.put(10,10,"10x10");
		m.put(10,10,"10x10a");
		m.put(1,-50,"1x-50");
		m.put(2,-50,"2x-50");
		m.put(0,0,"Origin");
		m.put(100000,-88888888,"Distant");
		m.put(100000,-88888888,"MoreDistant");

		System.out.println(m.get(10,10));
		System.out.println(m.get(1,-50));
		System.out.println(m.get(1,1));
		System.out.println(m.get(0,0));
		System.out.println(m.get(100000,-88888888));
		System.out.println(m.get(2,-50));
		m.remove(2,-50);
		System.out.println(m.get(2,-50));
	}
}

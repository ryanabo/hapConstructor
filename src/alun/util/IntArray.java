package alun.util;

public class IntArray
{
	public IntArray(int[] b)
	{
		a = new int[b.length];
		hash = 0;
		for (int i=0; i<a.length; i++)
		{
			a[i] = b[i];
			hash = hash + hash + a[i];
		}
	}

	public IntArray(IntArray b)
	{
		this(b.asArray());
	}

	public IntArray(int a, int b, int c)
	{
		this(toArray(a,b,c));
	}

	public final int hashCode()
	{
		return hash;
	}

	public final int size()
	{
		return a.length;
	}

	public final int get(int i)
	{
		return a[i];
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		for (int i=0; i<a.length; i++)
			s.append((1+a[i])+" ");
		s.deleteCharAt(s.length()-1);
		return s.toString();
	}

	public final boolean equals(Object y)
	{
		if (!(y instanceof IntArray))
			return false;

		IntArray x = (IntArray) y;

		if (a.length != x.a.length)
			return false;

		for (int i=0; i<a.length; i++)
			if (a[i] != x.a[i])
				return false;
	
		return true;
	}

	public final int[] asArray()
	{
		return a;
	}

	public static int[] toArray(int a, int b, int c)
	{
		int[] x = {a, b, c};
		return x;
	}

	private int[] a = null;
	private int hash = 0;
}

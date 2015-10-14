package alun.util;

public class Triplet<E,F,G> extends Pair<E,F>
{
	public G z = null;

	public Triplet()
	{
	}

	public Triplet(E a, F b, G c)
	{
		super(a,b);
		z = c;
	}

	public String toString()
	{
		return x+" "+y+" "+z;
	}
}

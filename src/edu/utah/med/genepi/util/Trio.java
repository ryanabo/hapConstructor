package edu.utah.med.genepi.util;

public class Trio<E,F,G> extends Pair<E,F>
{
	public G z = null;

	public Trio()
	{
	}

	public Trio(E a, F b, G c)
	{
		super(a,b);
		z = c;
	}

	public String toString()
	{
		return x+" "+y+" "+z;
	}
}

package alun.mcld;

public class ComponentMarker
{
	private static int count = 0;
	private int index = 0;

	public ComponentMarker()
	{
		index = count++;
	}

	public String toString()
	{
		return "<"+index+">";
	}
}

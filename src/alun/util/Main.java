package alun.util;

public class Main
{
	public static String[] strip(String[] a, String s)
	{
		if (a != null)
		{
			for (int i=0; i<a.length; i++)
			{
				if (a[i].equals(s))
				{
					String[] b = new String[a.length-1];
					for (int j=0; j<i; j++)
						b[j] = a[j];
					for (int j=i+1; j<a.length; j++)
						b[j-1] = a[j];
						return b;
				}
			}
		}
		return a;
	}
}

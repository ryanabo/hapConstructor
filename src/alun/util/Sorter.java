package alun.util;

/**
 This class sorts stuff. It uses heap sort as given by
 pages ?? in Numerical Recipes in C. It sorts in place
 in order n log(n) time.
*/
public class Sorter
{
/*
	public static void sort(double[] d)
	{
		for (int i=1; i<d.length; i++)
			for (int j=i; j>0; j--)
				if (d[j] < d[j-1])
				{
					double t = d[j];
					d[j] = d[j-1];
					d[j-1] = t;
				}
	}

	public static void sort(Object[] x, double[] d)
	{
		for (int i=1; i<d.length; i++)
			for (int j=i; j>0; j--)
				if (d[j] < d[j-1])
				{
					double t = d[j];
					d[j] = d[j-1];
					d[j-1] = t;
					Object o = x[j];
					x[j] = x[j-1];
					x[j-1] = o;
				}
	}
*/

/**
 Sorts the array of doubles in assending order.
*/
	public static void sort(double[] x)
	{	
		int n = x.length;
		int i = 0;
		int ir = 0;
		int j = 0;
		int k = 0;
		double rra = 0;
	
		if (n < 2)
			return ;
		k = n/2;
		ir = n-1;
		
		while(true)
		{
			if (k > 0)
				rra = x[--k];
			else
			{
				rra = x[ir];
				x[ir] = x[0];
				if (--ir == 0)
				{
					x[0] = rra;
					break;
				}
			}
			
			i = k;
			j = 2*k + 1;
			while (j <= ir)
			{
				if (j < ir && x[j] < x[j+1])
					j++;
				if (rra < x[j])
				{
					x[i] = x[j];
					i = j;
					j = 2*j + 1;
				}
				else
					j = ir+1;
			}

			x[i] = rra;
		}
	}

/**
 Sorts the array of objects and the array of doubles together
 the order being determined by the assending order of the
 doubles.
 Will just break if the arrays are not of equal length.
*/ 
	public static void sort(Object[] q, double[] x)
	{	
		int n = x.length;
		int i = 0;
		int ir = 0;
		int j = 0;
		int k = 0;
		double rra = 0;
		Object rrq = null;
	
		if (n < 2)
			return ;
		k = n/2;
		ir = n-1;
		
		while(true)
		{
			if (k > 0)
			{
				rra = x[--k];
				rrq = q[k];
			}
			else
			{
				rra = x[ir];
				rrq = q[ir];
				x[ir] = x[0];
				q[ir] = q[0];
				if (--ir == 0)
				{
					x[0] = rra;
					q[0] = rrq;
					break;
				}
			}
			
			i = k;
			j = 2*k + 1;
			while (j <= ir)
			{
				if (j < ir && x[j] < x[j+1])
					j++;
				if (rra < x[j])
				{
					x[i] = x[j];
					q[i] = q[j];
					i = j;
					j = 2*j + 1;
				}
				else
					j = ir+1;
			}

			x[i] = rra;
			q[i] = rrq;
		}
	}

	public static void sort(int[] a, double[] d)
	{
		Holder[] h = new Holder[a.length];
		for (int i=0; i<h.length; i++)
			h[i] = new Holder(a[i]);
		sort(h,d);
		
		for (int i=0; i<a.length; i++)
			a[i] = h[i].value;
	}
}

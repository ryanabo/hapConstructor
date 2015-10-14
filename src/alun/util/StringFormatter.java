package alun.util;

/**
 Formats data as strings for neat output.
*/
public class StringFormatter
{
/**
 Returns the given int, x, as a String in s character places.
 Leading zeros are added as padding if necessary. If the
 number of places is not large enough the String will be 
 longer than s characters.
*/
	public static String format(int x, int s)
	{
		return format(x,s,' ');
	}

	public static String format(double[] x, int a, int b)
	{
		StringBuffer f = new StringBuffer();
		f.append(format(x[0],a,b));
		for (int i=1; i<x.length; i++)
			f.append(" "+format(x[i],a,b));
		return f.toString();
	}

	public static String format(double[][] x, int a, int b)
	{
		StringBuffer f = new StringBuffer();
		f.append(format(x[0],a,b));
		for (int i=1; i<x.length; i++)
			f.append("\n"+format(x[i],a,b));
		return f.toString();
	}

	public static String space(int x, int n, char c)
	{
		StringBuffer f = new StringBuffer();
		int y = x;
		if (y < 0)
			y = -y;
		int m = 1;
		while (y >= 10)
		{
			m++;
			y /= 10;
		}

		for (int i=m; i<n; i++)
			f.append(c);
		return f.toString();
	}

	public static String format(int x, int n, char c)
	{
		StringBuffer f = new StringBuffer();
		
		if (x>=0)
		{
			f.append(space(x,n,c));
			f.append(x);
		}
		else
		{
			f.append(space(x,n-1,c));
			f.append("-");
			f.append(Math.abs(x));
		}

		return f.toString();

/*
		int y = x;
		int m = 1;
		if (x < 0)
		{
			y = -y;
			m += 1;
		}

		while (y >= 10)
		{
			m++;
			y /= 10;
		}

		m = n-m;
		if (m < 0)
			m = 0;

		StringBuffer b = new StringBuffer();
		while (m-- > 0)
			b.append(c);
		b.append(x);

		return b.toString();
*/
	}

/**
 Returns a String representation of the given double to
 s decimal places.
*/

	public static String format(double x, int b)
	{
		return format(x,-1,b);
	}

	public static String format(double x, int a, int b)
	{
		StringBuffer f = new StringBuffer();

		if (x == Double.POSITIVE_INFINITY)
			return " +Infinity";
		if (x == Double.NEGATIVE_INFINITY)
			return " -Infinity";

		if (x >= 0)
		{
			f.append(space((int)x,a,' '));
			f.append((int)x);
		}
		else
		{
			f.append(space(Math.abs((int)x),a-1,' '));
			f.append('-');
			f.append(Math.abs(((int)x)));
		}

		if (b > 0)
		{
			f.append('.');
			double r = Math.abs(x -(int)x);
			for (int i=0; i<b; i++)
				r *= 10;
			f.append(format((int)r,b,'0'));
		}

/*
		if (x < 0)
		{
			f.
			f.append("-");
			int aa = a;
			if (aa > 0)
				aa--;
			f.append(format(-x,aa,b));
		}
		else
		{
			f.append(space((int)x,
			f.append(format((int)x,a));
			f.append(".");
			double r = x - (int)x;
			for (int i=0; i<b; i++)
				r *= 10;
			r += 0.5;
			f.append(format((int)r,b,'0'));
		}
*/
/*
		int s = 1;
		double y = x;
		if (x < 0)
		{
			s = -s;
			y = -y;
		}

		if (a < 0)
			f.append(s*(int)y);
		else
			f.append(format(s*(int)y,a));

		if (b > 0)
		{
			f.append(".");
			double r = y-(int)y;
			for (int i=0; i<b; i++)
				r *= 10;
			// Add 0.5 to round up.
			//f.append(format((int)(r+0.5),b,'0'));
			f.append(format((int)(r),b,'0'));
		}
*/
	
		return f.toString();
	}

	public static String format(String s, int x)
	{
		StringBuffer b = new StringBuffer();
		for (int i=x; i>s.length(); i--)
			b.append(" ");
		b.append(s);
		return b.toString();
	}

/**
 Test main.
*/
	public static void main(String[] args)
	{
		for (int i= -200; i< 200; i+= 10)
			System.out.println(StringFormatter.format(i,5));
		System.out.println();

		for (double x = -10; x <10; x += Math.PI/4)
			System.out.println(StringFormatter.format(x,5,3));
		System.out.println();

		for (double x = -100; x <100; x += 5*Math.PI/4)
			System.out.println(StringFormatter.format(x,2));
		System.out.println();

		System.out.println(StringFormatter.format(0,5,3));
		System.out.println(StringFormatter.format(-12.34578,5,3));
		System.out.println(StringFormatter.format(-12.34579,3,3));
		System.out.println(StringFormatter.format(-12.34579,2,3));
		System.out.println(StringFormatter.format(-12.34579,2,0));
		System.out.println(StringFormatter.format(-12.34579,0));
		System.out.println(StringFormatter.format(-12.34579,2));
		System.out.println(StringFormatter.format(12.34579,2));

		System.out.println(StringFormatter.format(-12.34579,5,2));
		System.out.println(StringFormatter.format(12.34579,5,2));

		double x = 0.9999999999999999999999;
		System.out.println(StringFormatter.format(x,1,4));
		System.out.println(StringFormatter.format(x,1,6));

		System.out.println(StringFormatter.format(-0.1,3,3));
		System.out.println(StringFormatter.format(-1.1,3,3));


		System.out.println(space(10,3,'='));

	}
}

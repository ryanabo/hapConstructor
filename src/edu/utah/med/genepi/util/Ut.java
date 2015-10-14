//******************************************************************************
// Ut.java
//******************************************************************************
package edu.utah.med.genepi.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//==============================================================================
public final class Ut {

	public static final String N = System.getProperty("line.separator");
	public static final String FS = File.separator;

	//----------------------------------------------------------------------------
	public static String pkgOf(Object o)
	{
		return pkgOf(o.getClass());
	}

	//----------------------------------------------------------------------------
	public static String pkgOf(Class c)
	{
		return longestPrefixOf(c.getName(), ".");
	}

	//----------------------------------------------------------------------------
	public static String uqNameOf(Object o)
	{
		return suffixOf(o.getClass().getName(), ".");
	}

	//----------------------------------------------------------------------------
	public static String stemOf(File f)
	{
		return Ut.longestPrefixOf(f.getName(), ".");
	}

	//----------------------------------------------------------------------------
	public static File fExtended(String path, String ext)
	{
		return new File(
				path + (Ut.suffixOf(path, ".").equalsIgnoreCase(ext) ? "" : ("." + ext))
		);
	}

	//----------------------------------------------------------------------------
	public static String prefixOf(String s, String after)
	{
		try { return s.substring(0, s.indexOf(after)); }
		catch (Exception e) { return s; }
	}

	//----------------------------------------------------------------------------
	public static String longestPrefixOf(String s, String after)
	{
		try { return s.substring(0, s.lastIndexOf(after)); }
		catch (Exception e) { return s; }
	}

	//----------------------------------------------------------------------------
	public static String suffixOf(String s, String before)
	{
		try { return s.substring(s.lastIndexOf(before) + before.length()); }
		catch (Exception e) { return ""; }
	}

	//----------------------------------------------------------------------------
	public static String join(Object[] stringables, String glue)
	{
		return join(Arrays.asList(stringables), glue);
	}

	//----------------------------------------------------------------------------
	public static String join(List stringables, String glue)
	{
		if (stringables == null || stringables.size() == 0)
			return "";

		StringBuffer sb = new StringBuffer(stringables.get(0).toString());
		for (int i = 1, n = stringables.size(); i < n; ++i)
		{
			sb.append(glue);
			sb.append(stringables.get(i));
		}

		return sb.toString();
	}

	//----------------------------------------------------------------------------
	public static String format(Object[] stringables, String fmt, char replaceable)
	{
		return format(Arrays.asList(stringables), fmt, replaceable);
	}

	//----------------------------------------------------------------------------
	public static String format(List stringables, String fmt, char replaceable)
	{
		if (stringables == null || stringables.size() == 0)
			return "";

		StringBuffer sb = new StringBuffer();
		for (int i = 0, n = stringables.size(); i < n; ++i)
		{
			int insert_pos = fmt.indexOf(replaceable);
			if (insert_pos == -1)
				throw new IllegalArgumentException(
						"< " +  stringables.size() + " replaceable tokens in '" + fmt + "'"
				);

			sb.append(fmt.substring(0, insert_pos));
			sb.append(stringables.get(i));
			if (insert_pos < fmt.length())
				fmt = fmt.substring(insert_pos + 1);
		}

		if (fmt.length() > 0)
			sb.append(fmt);

		return sb.toString();
	}

	//----------------------------------------------------------------------------
	/** A mapping function that adds 'delta' to each member of an array. */
	public static int[] mapAdd(int[] a, int delta)
	{
		int[] b = new int[a.length];
		for (int i = 0, n = b.length; i < n; ++i)
			b[i] = a[i] + delta;
		return b;
	}

	//----------------------------------------------------------------------------
	public static int[] identityArray(int n)
	{
		int[] a = new int[n];
		for (int i = 0; i < n; ++i)
			a[i] = i;
		return a;
	}

	//----------------------------------------------------------------------------
	public static int indexOfMin(int[] vals)
	{
		int imin = 0;
		for (int i = 0, min = Integer.MAX_VALUE, n = vals.length; i < n; ++i)
			if (vals[i] < min)
			{
				imin = i;
				min = vals[imin];
			}
		return imin;
	}

	//----------------------------------------------------------------------------
	public static String ljust(String s, int field_width)
	{
		return fit(s, field_width, ' ', true);
	}

	//----------------------------------------------------------------------------
	public static String rjust(String s, int field_width)
	{
		return fit(s, field_width, ' ', false);
	}

	//----------------------------------------------------------------------------
	public static String fit(String s, int fwidth, char padch, boolean to_left)
	{
		int length = s.length();
		if (length < fwidth)
			return insertChars(s, fwidth, to_left ? length : 0, padch);
		return s.substring(0, fwidth);
	}

	//----------------------------------------------------------------------------
	public static String insertChars(String s, int fwidth, int offset, char c)
	{
		StringBuffer sb = new StringBuffer(s);
		int          padlen = fwidth - s.length();

		for (int i = 0; i < padlen; ++i)
			sb.insert(offset, c);

		return sb.toString();
	}

	//----------------------------------------------------------------------------
	public static Object newModule(String sdefpkg, String classname)
	throws GEException
	{  return newModule(sdefpkg, classname, null); }

	//----------------------------------------------------------------------------
	public static Object newModule( String sdefpkg,
			String classname,
			String suffix )
	throws GEException
	{
		String name = classname;
		if ( suffix != null )
			name += suffix;
		try {
			return Class.forName(
					classname.indexOf('.') == -1 ? (sdefpkg + "." + name ) : name
			).newInstance();
		} catch (Exception e) {
			throw new GEException("Can't get class instance: ", e);
		}
	}

	//----------------------------------------------------------------------------
	public static String strAppend( String s1, String s2 )
	{
		return strAppend(s1,s2,"");
	}

	//----------------------------------------------------------------------------
	public static String strAppend( String s1, String s2, String sep )
	{
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(s1);
		strBuffer.append(sep);
		strBuffer.append(s2);
		return strBuffer.toString();
	}

	//----------------------------------------------------------------------------
	public static String array2str( String[] lst, String sep )
	{
		StringBuffer sb = new StringBuffer();
		sb.append(lst[0]);
		for ( int i=1; i < lst.length; i++ )
		{
			sb.append(sep);
			sb.append(lst[i]);
		}
		return sb.toString();
	}

	//----------------------------------------------------------------------------
	public static String array2str( int[] lst, String sep )
	{
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString(lst[0]));
		for ( int i=1; i < lst.length; i++ )
		{
			sb.append(sep);
			sb.append(Integer.toString(lst[i]));
		}
		return sb.toString();
	}

	//----------------------------------------------------------------------------
	public static Object[] permute( Object[] objects )
	{
		int nobjs = objects.length;
		List<Integer> shuffledIndices = new ArrayList<Integer>();
		for ( int i=0; i < nobjs; i++ ) shuffledIndices.add(i);

		Collections.shuffle(shuffledIndices);

		Object[] shuffledObj = new Object[nobjs];

		for (int i=0; i< nobjs; i++){ shuffledObj[i] = objects[shuffledIndices.get(i)]; }
		return shuffledObj;
	}

	//----------------------------------------------------------------------------
	public static int[] permute( int[] objects )
	{
		int nobjs = objects.length;
		List<Integer> shuffledIndices = new ArrayList<Integer>();
		for ( int i=0; i < nobjs; i++ ) shuffledIndices.add(i);

		Collections.shuffle(shuffledIndices);

		int[] shuffledObj = new int[nobjs];

		for (int i=0; i< nobjs; i++){ shuffledObj[i] = objects[shuffledIndices.get(i)]; }
		return shuffledObj;
	}
}

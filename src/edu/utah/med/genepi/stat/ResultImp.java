//******************************************************************************
// ResultImp.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.util.Ut;

//==============================================================================
public class ResultImp 
{

	//----------------------------------------------------------------------------
	public static class Real implements CCStat.Result 
	{
		private static final NumberFormat doubFmt = NumberFormat.getInstance();
		static { doubFmt.setMaximumFractionDigits(6); }
		private final double theVal;

		//--------------------------------------------------------------------------
		public Real ( double val ){ theVal = val; }

		//--------------------------------------------------------------------------
		public int elementCount() { return 1; }
		public double[] doubleValues() { return new double[] {theVal}; }

		//--------------------------------------------------------------------------
		public String toString() 
		{ 
			if (Double.isNaN(theVal) || Double.isInfinite(theVal))
				return "-";
			else
			{ 
				return doubFmt.format(theVal); 
			}
		}
	}

	//----------------------------------------------------------------------------
	static class Series implements CCStat.Result {
		private final List<CCStat.Result> lElems = new ArrayList<CCStat.Result>();
		private final String fmtStr;
		Series() { this(null); }
		Series(String format_string) { fmtStr = format_string; }
		public int elementCount() { return lElems.size(); }
		public double[] doubleValues() {

			List<double[]> l = new ArrayList<double[]>();
			int  val_ct = 0;
			for (int i = 0, n = elementCount(); i < n; ++i)
			{
				double[] subvals = ((CCStat.Result) lElems.get(i)).doubleValues();
				l.add(subvals);
				val_ct += subvals.length;
			}

			double[] vals = new double[val_ct];
			for (int i = 0, ivals = 0, n = l.size(); i < n; ++i)
			{
				double[] subvals = (double[]) l.get(i);
				for (int j = 0; j < subvals.length; ++j)
					vals[ivals++] = subvals[j];
			}

			return vals;
		}
		public String toString() { return createStringRep(lElems, fmtStr); }
		protected final void addElement(CCStat.Result r) { lElems.add(r); }
		private String createStringRep(List<CCStat.Result> elems, String fmt) {
			return fmt != null ? Ut.format(elems, fmt, '?') : Ut.join(elems, " ");
		}
	}

	static class RealSeries extends Series {
		RealSeries(double[] elem_vals) { this(elem_vals, null); }
		RealSeries(double[] elem_vals, String format_string) {
			super(format_string);
			for (int i = 0; i < elem_vals.length; ++i)
				addElement(new Real(elem_vals[i]));
		}
	}

	public static class StringResult implements CCStat.Result
	{
		private final String theString;
		public StringResult ( String s )
		{
			theString = s;
		}
		public int elementCount() { return 1 ; }
		public String toString () { return theString; }
		public double[] doubleValues() { return new double[] {0.0}; }
	}

	static class StringSeries implements CCStat.Result
	{
		private final List<CCStat.Result> lString = new ArrayList<CCStat.Result>();
		StringSeries ( String[] ss )
		{
			for ( int i = 0; i < ss.length; i++ )
				addtoList( new StringResult (ss[i]) );
		}
		protected final void addtoList ( CCStat.Result r ) 
		{
			lString.add(r);
		}
		public int elementCount() { return lString.size() ; }
		public double[] doubleValues() { return new double[] {0.0}; }
		public String toString () { return convertStringList (lString); }
		public String convertStringList(List ls) 
		{
			return Ut.join ( ls, " " );
		}
	}


	static class RatioSeries extends RealSeries
	{  RatioSeries ( double[] ratios ) { super (ratios); } }

	static class Pair extends RealSeries
	{
		//Pair(double[] pair) { super( pair, "(?, ?)" ); } 
		Pair(double[] pair) { super( pair, "(? ?)" ); } 
	}

	static class PairString extends Series
	{
		PairString( String[] pair , String format_string) 
		{ super ( format_string ); 
		for ( int i = 0; i <pair.length; ++ i )
			addElement( new StringResult(pair[i]) );
		}
	}

	static class PairSeries extends Series
	{
		PairSeries ( double[][] pairs )
		{
			for ( int i = 0; i < pairs.length; i++ )
				addElement( new Pair(pairs[i]) );
		}
		PairSeries ( String[][] pairs )
		{
			for ( int i = 0; i < pairs.length; i++ )
				addElement( new PairString(pairs[i], "(?, ?)" ) );
		}
	}

	//static class Triplet extends RealSeries
	//{
	//  Triplet(String[] triplet) { super(triplet, "?v?:?"); }
	//}
	static class Triplet extends Series
	{
		Triplet( String[] triplet ) 
		{
			super("?v?:?");
			for ( int i = 0; i < triplet.length; i++ )
			{
				addElement( new StringResult(triplet[i]) );
			}
		}
	}

	static class TripletSeries extends Series
	{
		TripletSeries(String[][] triplets) {
			for (int i = 0; i < triplets.length; ++i)
				addElement(new Triplet(triplets[i]));
		}
	}

	public static CCStat.Result convertTriplet( CCStat.Result result,
			int numCol,
			int refColIndex )
	{
		double[] r0 = result.doubleValues();
		String[][] resultSeries = new String[r0.length][3];
		String[] r0Vals = new String[r0.length];
		for ( int r = 0; r < r0.length; r++ )
		{
			if ( Double.isNaN(r0[r]) )
				r0Vals[r] = "-";
			else
				r0Vals[r] = (new ResultImp.Real(r0[r])).toString();
		}
		for ( int j = 0, r = 0; j < numCol; j++ )
		{ 
			if ( j != refColIndex && r < r0Vals.length )
			{
				resultSeries[r][0] = Integer.toString(j + 1);
				resultSeries[r][1] = Integer.toString(refColIndex + 1);
				resultSeries[r][2] = r0Vals[r];
				r++;
			}
		}
		return new TripletSeries(resultSeries);
	}

	//---------------------------------------------------------------------------
	static class QuartResults extends RealSeries
	{
		QuartResults ( double[] four )
		{
			super ( four, "?(mean : ?, variance : ?, #M from Affected : ?)" );
		}
	}

	static class QuartSeries extends Series
	{
		QuartSeries ( double[][] fourseries )
		{
			for ( int i = 0; i < fourseries.length; i++ )
				addElement( new QuartResults(fourseries[i]) );
		}

	}

	//---------------------------------------------------------------------------
	static class ResultsWithCI extends RealSeries
	{
		ResultsWithCI ( double[] five )
		{
			super ( five, " ?v? : ? (? ?)" );
		}
	}

	static class ResultsWithCISeries extends Series
	{
		ResultsWithCISeries ( double[][] resultswithci )
		{ 
			for ( int i = 0; i < resultswithci.length; i++ )
				addElement( new ResultsWithCI(resultswithci[i]) );
		}
	}
}

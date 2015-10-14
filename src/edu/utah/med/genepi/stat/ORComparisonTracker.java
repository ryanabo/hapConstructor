//*****************************************************************************
// ORComparisonTracker.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

//=============================================================================
public class ORComparisonTracker implements CCStat.ComparisonTracker 
{
	public int[] notval;
	protected int[] hitCounts;
	public double[] r0Vals;
	public int[] comparisonCount;
	public String messages = null;
	protected int degreeF;

	//----------------------------------------------------------------------------
	public void setResult( CCStat.Result r, int simIndex )
	{
		if ( simIndex == 0 )
		{
			if ( !(r instanceof ResultImp.StringResult) || r.toString().equals("-") )
			{
				assert r instanceof ResultImp.RealSeries;
				r0Vals = r.doubleValues();
				comparisonCount = new int[r0Vals.length];
				notval = new int[r0Vals.length];
				hitCounts = new int[r0Vals.length];
				for ( int i = 0; i < r0Vals.length; i++ )
				{
					comparisonCount[i] = notval[i] = hitCounts[i]= 0;
				}
			}
//			else
//			{
//				System.out.println("Failed stat");
//			}
		}
		else
		{
			double[] rxVals = r.doubleValues();
			for (int i = 0, n = rxVals.length; i < n; ++i)
			{
				double obs  = r0Vals[i];
				double sim  = rxVals[i];
				double obs1 = 1 / obs;
				if ( Double.isNaN(sim) ) notval[i]++;
				else 
				{
					if ( obs > 1.0 && sim >= obs ) hitCounts[i]++;
					else if ( obs < 1.0 && sim <= obs ) hitCounts[i]++;
					comparisonCount[i]++;
				}
			}
		}
	}

	//--------------------------------------------------------------------------
	public CCStat.Result getComparisonsResult()
	{
		double[] pvals = new double[hitCounts.length];

		for (int i = 0; i < pvals.length; ++i)
		{
			if ( Double.isNaN(r0Vals[i]))
				pvals[i] = r0Vals[i];
			else if ( r0Vals[i] == 1.0 )
				pvals[i] = 1.0;
			else if ( ( hitCounts[i] / (double) comparisonCount[i] ) < 0.5 )
				pvals[i] =  2 * hitCounts[i] /(double) comparisonCount[i];
			else pvals[i] = 1.0;
		}
		return new ResultImp.RealSeries(pvals);
	}

	//------------------------------------------------------------------------
	public int[] getnotval(){ return notval; }

	//------------------------------------------------------------------------
	public int[] getComparisonCount(){ return comparisonCount; }

	//------------------------------------------------------------------------
	public void setMessages(String inMessages){ messages = inMessages; }

	//------------------------------------------------------------------------
	public String getMessages(){ return messages; }

	//------------------------------------------------------------------------
	public void setDegreeOfFreedom(int df){ degreeF = df; }

}

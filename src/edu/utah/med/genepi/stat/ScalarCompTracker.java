//******************************************************************************
// ScalarCompTracker.java
//******************************************************************************
package edu.utah.med.genepi.stat;

//==============================================================================
class ScalarCompTracker implements CCStat.ComparisonTracker {

	protected double r0Val;
	protected int[]  comparisonCount;
	protected int    gteCount;
	public  int[]  notval;
	public  CCStat.Result confIntervals = null;
	public  String messages = null;

	//----------------------------------------------------------------------------
	ScalarCompTracker() {}

	//----------------------------------------------------------------------------
	public void setResult( CCStat.Result r, int simIndex )
	{
		if ( simIndex == 0 )
		{
			assert r.elementCount() == 1;
			r0Val = r.doubleValues()[0];
			comparisonCount = new int[1];
			notval = new int[1];
			comparisonCount[0] = gteCount = notval[0] = 0;
		}
		else
		{
			assert r.elementCount() == 1;
			if ( r instanceof ResultImp.StringResult ) notval[0]++;
			else 
			{
				comparisonCount[0]++;
				if (Math.abs(r.doubleValues()[0]) >= Math.abs(r0Val)) ++gteCount;
			}
		}
	}

	//----------------------------------------------------------------------------
	public CCStat.Result getComparisonsResult()
	{
		if ( Double.isNaN(r0Val) ) return new ResultImp.StringResult("-");
		else return new ResultImp.Real(gteCount / (double) comparisonCount[0] );
	}

	//----------------------------------------------------------------------------
	public int[] getComparisonCount(){ return comparisonCount; }

	//----------------------------------------------------------------------------
	public int[] getnotval(){ return notval; }

	//----------------------------------------------------------------------------
	public void setMessages(String inMessages){ messages = inMessages; }

	//----------------------------------------------------------------------------
	public String getMessages(){ return messages; }

	public void setDegreeOfFreedom(int df){ }
}

package edu.utah.med.genepi.hapconstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class hapCResultsBuffer {

	private HashMap<String,hapCResult[]> results = null;
	private int storedStep = -1;
	
	public hapCResultsBuffer(int step) 
	{
		storedStep = step;
	}

	//----------------------------------------------------------------------------
	public void storeResult( hapCResult result )
	{
		if ( results == null ) results = new HashMap<String,hapCResult[]>();
		
		String resultKey = result.getBufferKey();
		if ( !results.containsKey(resultKey) )
		{
			results.put(resultKey,new hapCResult[]{result});
		}
		else
		{
			hapCResult[] storedResults = results.get(resultKey);
			List<hapCResult> resultsLst = new ArrayList<hapCResult>(Arrays.asList(storedResults));
			resultsLst.add(result);
			results.put(resultKey,resultsLst.toArray(new hapCResult[0]));
		}
	}
	
	//----------------------------------------------------------------------------
	public hapCWrappedResult[] getResults()
	{
		int nWrappedResults = results.keySet().size();
		hapCWrappedResult[] wrappedResults = new hapCWrappedResult[nWrappedResults];
		int iter = 0;
		for ( Iterator<String> it = results.keySet().iterator(); it.hasNext(); )
		{
			String key = it.next();
			hapCResult[] storedResults = results.get(key);
			wrappedResults[iter] = new hapCWrappedResult(key,storedResults);
			iter++;
		}
		return wrappedResults; 
	}
	
	//----------------------------------------------------------------------------
	public int getStoredStep(){ return storedStep; }

}

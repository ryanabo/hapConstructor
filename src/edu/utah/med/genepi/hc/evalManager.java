package edu.utah.med.genepi.hc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.utah.med.genepi.stat.CCAnalysis;

public class evalManager {
	
	public List<CCAnalysis> originalAnalysis;
	public String process_id = "build";
	public Map<String, List> obsSigs = new HashMap<String, List>();
	public Map<String, Integer> evalMap = new HashMap<String, Integer>();

	public evalManager(CCAnalysis[] analyses) {
		originalAnalysis = Arrays.asList(analyses);
	}

	public void saveObserved(Map<String, List> sigs) {
		Set keys = sigs.keySet();
		for (Iterator it = keys.iterator(); it.hasNext();) {
			String k = (String) it.next();
			List lst = sigs.get(k);
			obsSigs.put(k, lst);
			//evalMap.put(k, 0);
		}
	}

	public Map getSigMap() {
		return obsSigs;
	}

	public void eval(String sigKey) {
		if (evalMap.containsKey(sigKey)) {
		  int ii = evalMap.get(sigKey);
		  ii++;
		  evalMap.put(sigKey, ii);
		}
	}	

}

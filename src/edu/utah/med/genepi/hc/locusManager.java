package edu.utah.med.genepi.hc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.utah.med.genepi.gm.GDef;

public class locusManager {
	private GDef gd;

	private Map<String, Integer> reverse = new HashMap<String, Integer>();

	private List<Integer> allIDs = new ArrayList<Integer>();

	private Map<Integer, Integer> locInd = new HashMap<Integer, Integer>();

	public locusManager(GDef gdef) {
		gd = gdef;
		int lcount = gd.getLocusCount();
		// Create reverse lookup for marker names and loci ids
		for (int i = 0; i < lcount; i++) {
			// Extract the markername and the locus ID.
			String markername = gd.getLocus(i).getMarker();
			int locID = gd.getLocus(i).getID();
			// Add the locus ID and the index of the locus in the GDef list.
			if (markername == null) {
				markername = String.valueOf(gd.getLocus(i).getID());
			}
			reverse.put(markername, locID);
			allIDs.add(locID);
			locInd.put(locID, i);
		}
	}

	public List<Integer> converttoIDs(String[] markers) {
		List<Integer> ids = new ArrayList<Integer>();
		if (markers[0] == "ALL MARKERS") {
			ids.addAll(getAllIDs());
		} else {
			for (int i = 0; i < markers.length; i++) {
				if (reverse.containsKey(markers[i])) {
					ids.add(reverse.get(markers[i]));
				}
			}
		}
		return ids;
	}

	public int[] getLocInd(Set iloci) {
		// Parse through set and find the value in locInd map.
		// return value.
		int[] ia = new int[iloci.size()];
		int counter = 0;
		for (Iterator it = iloci.iterator(); it.hasNext();) {
			int ss = (Integer) it.next();
			ia[counter] = locInd.get(ss);
			counter++;
		}
		return ia;
	}

	public String[] getMarkers(Set lociSet) {
		List<String> lmarker = new ArrayList<String>();
		for (Iterator it = lociSet.iterator(); it.hasNext();) {
			int v = (Integer) it.next();
			//int v = Integer.parseInt(val);
			String markername = gd.getLocus((v - 1)).getMarker();
			if (markername == null)
				markername = String.valueOf(gd.getLocus((v - 1)).getID());
			lmarker.add(markername);
		}
		String[] markers = (String[]) lmarker.toArray(new String[0]);
		return markers;
	}

	public List<Integer> getAllIDs() {
		return allIDs;
	}

	public int getLocusCount() {
		return gd.getLocusCount();
	}

}

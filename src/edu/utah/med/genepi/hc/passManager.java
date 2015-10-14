package edu.utah.med.genepi.hc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.utah.med.genepi.app.rgen.IOManager;
import edu.utah.med.genepi.app.rgen.PedGenie;

public class passManager {
	public boolean bSetsExist = false;

	public int bSetsFirst = 0;

	public Map<Integer,Set> backSets;

	public final int pindex = 1;

	private int step;

	public passManager(int bstep) {
		backSets = new HashMap<Integer, Set>();
		step = bstep;
	}

	public Set getBackSets(int stp) {
		step = stp;
		Set s = backSets.get(step);
		backSets.remove(step);
		if (backSets.size() == 0) {
			bSetsExist = false;
		}
		if (s == null) {
			s = new TreeSet();
		}
		return s;
	}

	public void storeBackSets(IOManager iom, PedGenie pdg, Set oset, Map tested) {
		Set<Set> newSet = new HashSet<Set>();
		for (Iterator it = oset.iterator(); it.hasNext();) {
			SortedSet wSet = (SortedSet) it.next();
			for (Iterator it2 = wSet.iterator(); it2.hasNext();) {
				SortedSet clonnie = new TreeSet();
				clonnie.addAll(wSet);
				// Set clonnie = ((Set) ((HashSet) wSet).clone());
				int m = (Integer) it2.next();
				clonnie.remove(m);
				if (!tested.containsKey(clonnie))
					newSet.add(clonnie);
			}
		}
		if (newSet.size() > 0) {
			if (!bSetsExist) {
				bSetsExist = true;
				bSetsFirst = step - 1;
			}
			backSets.put(step - 1, newSet);
		}
	}

	public void update(int s) {
		step = s;
	}

}

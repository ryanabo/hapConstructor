package edu.utah.med.genepi.hapconstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HaplotypeResultStorage {

	HashMap<Long,Long[]> testedHaps = null;
	
	public HaplotypeResultStorage()
	{
		
	}
	
	public void storeHaplotype( long locusAddress, long haplotype )
	{
		if ( testedHaps == null ) testedHaps = new HashMap<Long,Long[]>();
		if ( !testedHaps.containsKey(locusAddress) )
		{
			Long[] hapstore = new Long[]{haplotype};
			testedHaps.put(locusAddress,hapstore);
		}
		else
		{
			Long[] haps = testedHaps.get(locusAddress);
			List<Long> hapstore = new ArrayList<Long>(Arrays.asList(haps));
			if ( !hapstore.contains(haplotype) ) hapstore.add(haplotype);
			testedHaps.put(locusAddress,hapstore.toArray(new Long[0]));
		}
	}
}

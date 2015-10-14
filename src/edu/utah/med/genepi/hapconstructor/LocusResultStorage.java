package edu.utah.med.genepi.hapconstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocusResultStorage {
	
	private Long[] locusAddress = null;
	
	public LocusResultStorage(){}
	
	public void addAddress( long address )
	{
		if ( locusAddress == null ) locusAddress = new Long[]{address};
		else
		{
			List<Long> la = new ArrayList<Long>(Arrays.asList(locusAddress));
			if ( !la.contains(address) )la.add(address);
			locusAddress = la.toArray(new Long[0]);
		}
	}
	
	public Long[] getLociAddresses(){ return locusAddress; }
}

package edu.utah.med.genepi.hapconstructor.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.utah.med.genepi.analysis.ModelCombo;

public class HaplotypeFillerBuffer implements FillerBuffer {

	// Key1 = Haplotype, Key2 = LocusAddress
	HashMap<Long, HashMap<Long, ComboSet>> buffer = new HashMap<Long,HashMap<Long,ComboSet>>();
	
	public HaplotypeFillerBuffer()
	{
	}
	
	//----------------------------------------------------------------------------
	public void add( ComboSet cset )
	{
		Long haplotype = cset.getHaplotype(false);
		Long locusAddress = cset.getLocusAddress();
		if ( !buffer.containsKey(haplotype) )
		{
			HashMap<Long,ComboSet> innerBuffer = new HashMap<Long,ComboSet>();
			innerBuffer.put(locusAddress,cset);
			buffer.put(haplotype,innerBuffer);
		}
		else
		{
			HashMap<Long,ComboSet> innerBuffer = buffer.get(haplotype);
			if ( !innerBuffer.containsKey(locusAddress) )
			{
				innerBuffer.put(locusAddress,cset);
				buffer.put(haplotype,innerBuffer);
			}
		}
	}

	//----------------------------------------------------------------------------
	public AnalysisFiller[] getFillers()
	{
		List<AnalysisFiller> aFillers = new ArrayList<AnalysisFiller>();
		for ( Iterator<Long> hapIt = buffer.keySet().iterator(); hapIt.hasNext(); )
		{
			Long haplotype = hapIt.next();
			ValueCombo vc = null;
			ModelCombo[] modelcombos = new ModelCombo[]{new ModelCombo(new String[]{null})};
			HashMap<Long,ComboSet> innerBuffer = buffer.get(haplotype);
			MarkerCombo[] mc = new MarkerCombo[innerBuffer.keySet().size()];
			int iter = 0;
			for ( Iterator<Long> markerIt = innerBuffer.keySet().iterator(); markerIt.hasNext(); )
			{
				Long markerset = markerIt.next();
				ComboSet cset = innerBuffer.get(markerset);
				if ( vc == null ) vc = cset.getValueCombo(); 
				mc[iter] = cset.getMarkerCombo();
				iter++;
			}
			AnalysisFiller af = new AnalysisFiller(vc,mc,modelcombos);
			aFillers.add(af);
		}
		buffer.clear();
		return aFillers.toArray(new AnalysisFiller[0]);
	}
}

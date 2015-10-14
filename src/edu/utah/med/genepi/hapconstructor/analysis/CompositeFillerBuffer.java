package edu.utah.med.genepi.hapconstructor.analysis;

import java.util.HashMap;
import java.util.Iterator;

import edu.utah.med.genepi.analysis.ModelCombo;

public class CompositeFillerBuffer implements FillerBuffer {

	HashMap<String,AnalysisFiller> buffer = null;
	
	public CompositeFillerBuffer(){}

	//----------------------------------------------------------------------------
	public void add( ComboSet cset )
	{
		if ( buffer == null ) buffer = new HashMap<String,AnalysisFiller>();
			
		MarkerCombo[] mc = new MarkerCombo[]{cset.getMarkerCombo()};
		ValueCombo vc = cset.getValueCombo();
		ModelCombo[] modelcombos = new ModelCombo[]{cset.getModelCombo()};
		
		String key = buildKey(cset);
		
		AnalysisFiller aFiller = new AnalysisFiller(vc,mc,modelcombos);
		if ( !buffer.containsKey(key) ) buffer.put(key,aFiller);
	}
	
	//----------------------------------------------------------------------------
	private String buildKey( ComboSet cset )
	{
		MarkerCombo mc = cset.getMarkerCombo();
		GeneUnit[] gu = mc.getGeneUnits();
		long locusAddress = mc.getLocusAddress();
		StringBuffer markerstr = new StringBuffer();
		markerstr.append(locusAddress);
//		for ( int i=0; i < gu.length; i++ )
//		{
//			MarkerUnit[] mu = gu[i].getMarkerUnits();
//			StringBuffer gustr = new StringBuffer();
//			for ( int j=0; j < mu.length; j++ )
//			{
//				int[] indices = mu[j].getLoci();
//				StringBuffer markerunitstr = new StringBuffer();
//				for ( int k=0; k < indices.length; k++ )
//				{
//					markerunitstr.append(indices[k]);
//					if ( k != (indices.length-1) ) markerunitstr.append("-");
//				}
//				gustr.append("(");
//				gustr.append(markerunitstr);
//				gustr.append(")");
//			}
//			markerstr.append("(");
//			markerstr.append(gustr);
//			markerstr.append(")");
//		}
		
		ValueCombo vc = cset.getValueCombo();
		ValueUnit[] vu = vc.getValueUnits();
		StringBuffer valuestr = new StringBuffer();
		for ( int i=0; i < vu.length; i++ )
		{
			byte[][] values = vu[i].getValues();
			StringBuffer unitstr = new StringBuffer();
			for ( int j=0; j < values.length; j++ )
			{
				unitstr.append(values[j][0]);
				if ( j != (values.length-1) ) unitstr.append("-");
			}
			valuestr.append("(");
			valuestr.append(unitstr);
			valuestr.append(")");
		}
		
		ModelCombo modelcombo = cset.getModelCombo();
		String[] models = modelcombo.getModels();
		StringBuffer modelstr = new StringBuffer();
		for ( int i=0; i < models.length; i++ )
		{
			if ( models[i] != null )
			{
				modelstr.append("(");
				modelstr.append(models[i]);
				modelstr.append(")");
			}
		}
		
		StringBuffer fullstr = new StringBuffer();
		fullstr.append(markerstr);
		fullstr.append(":");
		fullstr.append(valuestr);
		if ( modelstr.length() > 0 )
		{
			fullstr.append(":");
			fullstr.append(modelstr);
		}
		return fullstr.toString();
	}
	
	//----------------------------------------------------------------------------
	public AnalysisFiller[] getFillers()
	{
		AnalysisFiller[] fillers = new AnalysisFiller[buffer.keySet().size()];
		int iter = 0;
		for ( Iterator<String> it = buffer.keySet().iterator(); it.hasNext(); )
		{
			fillers[iter] = buffer.get(it.next());
			iter++;
		}
		buffer.clear();
		return fillers; 
	}
	
	
}

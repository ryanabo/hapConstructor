package edu.utah.med.genepi.hapconstructor.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.utah.med.genepi.analysis.ModelCombo;

public class CGFillerBuffer extends CompositeFillerBuffer {

	HashMap<String,AnalysisFiller> buffer = null;
	
	public CGFillerBuffer()
	{}
	
	//----------------------------------------------------------------------------
	public void add( ComboSet cset )
	{
		if ( buffer == null ) buffer = new HashMap<String,AnalysisFiller>();
			
		MarkerCombo[] mc = new MarkerCombo[]{cset.getMarkerCombo()};
		ValueCombo vc = cset.getValueCombo();
		ModelCombo[] modelcombos = fillModelCombos(cset);
		
		String key = buildKey(cset);
		
		AnalysisFiller aFiller = new AnalysisFiller(vc,mc,modelcombos);
		if ( !buffer.containsKey(key) ) buffer.put(key,aFiller);
	}
	
	//----------------------------------------------------------------------------
	public ModelCombo[] fillModelCombos( ComboSet cset )
	{
		ModelCombo modelcombo = cset.getModelCombo();
		String[] models = modelcombo.getModels();
		List<Integer> missingIndices = new ArrayList<Integer>();
		
		for ( int i=0; i < models.length; i++ )
		{
			if ( models[i] == null ) missingIndices.add(i);
		}
		
		Integer[] missing = missingIndices.toArray(new Integer[0]);
		int nmissing = missing.length;
		int ncombos = (int) Math.pow(2.0,nmissing);
		
		
		List<ModelCombo> newmodelcombos = new ArrayList<ModelCombo>();
		for ( int i =0; i < ncombos; i++ )
		{
			String[] newmodelset = models.clone();
			for ( int j=0; j < nmissing; j++ )
			{
				int bit = (i >> j) & 1;
				String modelValue = "Dom";
				if ( bit == 0 ) modelValue = "Rec";
				newmodelset[missing[j]] = modelValue;
			}			
			newmodelcombos.add(new ModelCombo(newmodelset));
		}
		return newmodelcombos.toArray(new ModelCombo[0]);
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
	
	//----------------------------------------------------------------------------
	private String buildKey( ComboSet cset )
	{
		MarkerCombo mc = cset.getMarkerCombo();
		GeneUnit[] gu = mc.getGeneUnits();
		long locusAddress = mc.getLocusAddress();
		StringBuffer markerstr = new StringBuffer();
		markerstr.append(locusAddress);
		
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
	
}

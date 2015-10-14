package edu.utah.med.genepi.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utah.med.genepi.hapconstructor.analysis.ComboAddress;

public class ModelCombo {

	private String[] models = null; 
	
	public ModelCombo( String[] m )
	{
		models = m;
	}
	
	//---------------------------------------------------------------------------	
	public ModelCombo copy()
	{
		String[] copymodels = null;
		if ( models != null )
		{
			copymodels = new String[models.length];
			for ( int i=0; i < models.length; i++ ) copymodels[i] = models[i];
		}
		return new ModelCombo(copymodels);
	}
	
	//---------------------------------------------------------------------------
	public void add( ComboAddress ca )
	{
		boolean[] creations = ca.getCreations();
		if ( creations[0] || creations[1] )
		{
			List<String> modelLst = new ArrayList<String>(Arrays.asList(models));
			modelLst.add(null);
			models = modelLst.toArray(new String[0]);
		}
	}
	
	//---------------------------------------------------------------------------
	public void remove( int removeIndex )
	{
		String[] newmodels = new String[models.length-1];
		if ( removeIndex > -1 )
		{
			int shiftIndex = 0;
			for ( int i=0; i < models.length; i++ )
			{
				if ( i == removeIndex )
				{
					shiftIndex = -1;
				}
				else newmodels[i+shiftIndex] = models[i]; 
			}
		}
	}
	
	//---------------------------------------------------------------------------
	public String[] getModels(){ return models; }
}

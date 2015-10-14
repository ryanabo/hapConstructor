package edu.utah.med.genepi.interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.sim.GSimulator;
import edu.utah.med.genepi.util.GEException;

public class InteractionParams {

  private int nloci;
  private int[] markerSetBounds;
  // Contains the 0-based indices of the loci combinations to test for interaction between
  private int[][] markerCombos;
  private int[][] modelCombos;
  private String[] statistics;
  private int nullCycles = 0;
  private int nmetas = 0;
  private final GSimulator.Top  tSim;
  private final GSimulator.Drop dSim;
  private final GDef gdef;
  private final AlleleFormat af;
  private Integer[] loci_indices;
  private boolean caseOnly = false;
  
  //----------------------------------------------------------------------------  
  public InteractionParams( Specification spec, Study[] study ) throws GEException
  {
	nullCycles = spec.getNumberOfSimulations();
	Map<String,String> params = spec.get_intx_params();
	nloci = spec.getGDef().getLocusCount();
    if ( ! params.containsKey("interaction_statistic") ) statistics = new String[] {"all"};
    else
    {
      String[] intx_stats = params.get("interaction_statistic").split(",");
      statistics = new String[intx_stats.length];
      for ( int i=0; i < intx_stats.length; i++ ) statistics[i] = intx_stats[i];
    }
    if ( params.containsKey("interaction_caseonly") )
    {
      if ( params.get("interaction_caseonly").equals("true") ) caseOnly = true;
    }
    if ( ! params.containsKey("interaction_markerSets") ) markerSetBounds = new int[] {nloci};
    else
    {
      String markerBounds = params.get("interaction_markerSets");
      String[] m = markerBounds.split(",");
      markerSetBounds = new int[m.length];
      for ( int i =0; i < m.length; i++ ) markerSetBounds[i] = Integer.parseInt(m[i]);
    }
    if ( params.containsKey("interaction_modelCombos") )
    {
      //List of model combinations, A-A (Additive x Additive),A-D (Additive x Dominant), D-A (Dominant x Additive)
      String[] mCombos = params.get("interaction_modelCombos").split(",");
      modelCombos = new int[mCombos.length][2];
      for ( int i=0; i < mCombos.length; i++ )
      {
    	String[] models = mCombos[i].split("-");
    	for ( int j = 0; j < models.length; j++ )
    	{
    	  int m = 0;
    	  if ( models[j].equals("D")) m = 1;
    	  else if ( models[j].equals("R")) m = 2;
    	  modelCombos[i][j] = m;
    	}
      }
    }
    else
    {
      modelCombos = new int[][]{{0,0}};
    }
    if ( params.containsKey("interaction_markerCombos") )
    {
      String[] markerCombinations = params.get("interaction_markerCombos").split(",");
      markerCombos = new int[markerCombinations.length][2];
      List<Integer> iloci = new ArrayList<Integer>();
      for ( int i = 0; i < markerCombinations.length; i++ )
      {
        String[] lociCombo = markerCombinations[i].split("-");
        int loci1 = Integer.parseInt(lociCombo[0])-1;
        int loci2 = Integer.parseInt(lociCombo[1])-1;
        markerCombos[i][0] = loci1;
        markerCombos[i][1] = loci2;
        if ( !iloci.contains(loci1)) iloci.add(loci1);
        if ( !iloci.contains(loci2)) iloci.add(loci2);
      }
      loci_indices = iloci.toArray(new Integer[0]);
    }
    else
    {
      int lastbound = 0;
      List<List<Integer>> markerSets = new ArrayList<List<Integer>>();
      int[] setLengths = new int[markerSetBounds.length];
      for ( int i=0; i < markerSetBounds.length; i++ )
      {
        int markerbound = markerSetBounds[i];
        int nmarkers = markerbound - lastbound;
        setLengths[i] = nmarkers;
        List<Integer> markerSet = new ArrayList<Integer>();
        for ( int j=lastbound; j < nmarkers; j++ )
        {
          markerSet.add(j);  
        }
        lastbound = markerbound;
        markerSets.add(markerSet);
      }
      
      int total_combos = 1;
      for ( int i=0; i < markerSets.size(); i++)
      {
        for ( int j = i+1; j < (markerSets.size()-i-1); j++ ) total_combos += setLengths[i]*setLengths[j];	
      }
      markerCombos = new int[total_combos][2];
      List<Integer> iloci = new ArrayList<Integer>();
      if ( markerSets.size() == 1 )
      {
    	int comboCounter = 0;
    	// Create combos out of the single set
    	List<Integer> mSet = markerSets.get(0);
        
        for ( int i=0; i < mSet.size(); i++ )
        {
          for ( int j=i+1; j < (mSet.size()-i); j++ )
          {
            int loci1 = mSet.get(i);
            int loci2 = mSet.get(j);
  	        markerCombos[comboCounter][0] = loci1;
	        markerCombos[comboCounter][1] = loci2;
  	        if ( ! iloci.contains(loci1) ) iloci.add(loci1);
	        if ( ! iloci.contains(loci2) ) iloci.add(loci2);
	        comboCounter++;
          }
        }
      }
      else
      {
      int comboCounter = 0;
      for ( int i=0; i < markerSets.size(); i++)
      {
        for ( int j = i+1; j < (markerSets.size()-i-1); j++ )
        {
    	  List<Integer> markerSet1 = markerSets.get(i);
    	  List<Integer> markerSet2 = markerSets.get(j);
    	  for ( int k=0; k < markerSet1.size(); k++ )
    	  {
    	    for ( int l=0; l < markerSet2.size(); l++ )
    	    {
    	      int loci1 = markerSet1.get(k);
    	      int loci2 = markerSet2.get(l);
    	      markerCombos[comboCounter][0] = loci1;
    	      markerCombos[comboCounter][1] = loci2;
    	      comboCounter ++;
    	      if ( ! iloci.contains(loci1) ) iloci.add(loci1);
    	      if ( ! iloci.contains(loci2) ) iloci.add(loci2);
    	    }
    	  }
        }
      }
      }
      loci_indices = iloci.toArray(new Integer[0]);
    }

    if ( params.containsKey("interaction_metas") ) nmetas = Integer.parseInt(params.get("interaction_metas"));

    tSim = spec.getTopSim();
    dSim = spec.getDropSim();
    initializeSimulator(tSim, spec, study);
    initializeSimulator(dSim, spec, study);
    
    gdef = spec.getGDef();
    af = gdef.getAlleleFormat();
  }
  
  //----------------------------------------------------------------------------
  public boolean get_caseOnly(){ return caseOnly; }
  
  //----------------------------------------------------------------------------
  public Integer[] get_loci_indices(){ return loci_indices; }
  
  //----------------------------------------------------------------------------
  public GSimulator.Top getTsim(){ return tSim; }
  
  //----------------------------------------------------------------------------
  public GSimulator.Drop getDsim(){ return dSim; }
  
  //----------------------------------------------------------------------------
  public GDef get_gdef(){ return gdef; }
  
  //----------------------------------------------------------------------------
  public AlleleFormat get_af(){ return af; }
  
  //----------------------------------------------------------------------------  
  public String[] get_statistics(){ return statistics; }
  
  //----------------------------------------------------------------------------
  public int get_nmetas(){ return nmetas; }
  
  //----------------------------------------------------------------------------
  public int[] get_markerBounds(){ return markerSetBounds; }
  
  //----------------------------------------------------------------------------
  public int[][] get_markerCombos(){ return markerCombos; }
  
  //----------------------------------------------------------------------------
  public int[][] get_modelCombos(){ return modelCombos; }
  
  //----------------------------------------------------------------------------
  public int get_nullcycles(){ return nullCycles; }
  
  //----------------------------------------------------------------------------
  private void initializeSimulator( GSimulator gs,Specification spec, Study[] study )
          throws GEException
  {
    gs.setUserParameters(spec, study);
    gs.setPedData();
    gs.setGDef(spec.getGDef());
    try { gs.preProcessor(); }
    catch ( GEException e )
    { throw new GEException ( e.getMessage() ); }
  }
}

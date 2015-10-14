package edu.utah.med.genepi.hc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.stat.CCStatRun;

public class analysisResults {
  private double[]           tHold;
  private double[]           screentHold = {};
  private locusManager       lm;
  private int                step;
  private int                sigrun;
  private int                nsims;
  private Set                sigResults = new HashSet<Set>();
  private Map<String,List>   obsSigs = new HashMap<String,List>();
  public Map<Set,Set>        screenSigs = new HashMap<Set,Set>();
  private Map<Integer, List> simPvals = new HashMap<Integer, List>();
  private Map<Integer, List> obsPvals = new HashMap<Integer, List>();
  private hapConstructor     hc;
//  public boolean             mostsigreached = false;
//  private Map                hinc_map = new HashMap();
//  private Map                hdec_map = new HashMap();
//  private Map                hnon_map = new HashMap();
//  private Map                cinc_map = new HashMap();
//  private Map                cdec_map = new HashMap();
//  private Map                cnon_map = new HashMap();  
//  private Map                haps_storage = new HashMap();
  
  //----------------------------------------------------------------------------
  public analysisResults( CCAnalysis[] a, Map mParams, GDef gd, int stp, int num_sims, int significancerun, int screentest, hapConstructor hapC )
  {
	hc = hapC;
    lm = new locusManager(gd);
    step = stp;
    sigrun = significancerun;
    nsims = num_sims;
	
    if( mParams.containsKey("hapc_threshold") )
    {
      String[] sthold = ((String) mParams.get("hapc_threshold")).split(",");
      tHold = new double[sthold.length];
      
      for(int i=0; i < sthold.length; i++)
      {
        tHold[i] = Double.parseDouble(sthold[i]);
      }
    }
    
    if( mParams.containsKey("hapc_screenthreshold") )
    {
      String[] sthold = ((String) mParams.get("hapc_screenthreshold")).split(",");
      screentHold = new double[sthold.length];
      
      for(int i=0; i < sthold.length; i++)
      {
        screentHold[i] = Double.parseDouble(sthold[i]);
      }
    }
	
    loadResults( a, mParams,screentest );
  }
  
  //----------------------------------------------------------------------------
  public void loadResults( CCAnalysis[] a, Map mParams, int screentest )
  {
//    if(screentest == 1)
//    {
//      obsSigs = new HashMap();
//    }
    //Process the analyses results. This code is derived from Reporter.java
    Map haps_storage = new HashMap();
    Map cgts_storage = new HashMap();
    Map nonhd = null;
    Map noncd = null;
    boolean sigs_occurred = false;
    Set previous_lociset = null;
    int nrepeat = 1;
    if ( a.length > 0 )
    {
      nrepeat = a[0].getRepeat();
    }
    
    for( int i=0; i < a.length; i++ )
    {
      nrepeat = a[i].getRepeat();
      CCAnalysis.Table[][][][] tables = a[i].getObservedTable();
      //Key for results HashMap
      //Key consists of loci-id:model for generalMap HashMap
      //Use getLoci and them convert it back to markername
      //use getLoci instead
      //String[] markers = a[i].getMarkers();
      //List<Integer> ids = lm.converttoIDs(markers);
      
      for ( int r = 0; r < nrepeat; r++ )
      {
        List<Integer> ids = new ArrayList<Integer>();
        int[] iloci = a[i].getLoci(r);
        if(step > 1)
        {
          iloci = a[i].getLoci();
        }
        for ( int j = 0; j < iloci.length; j++ )
        {
          ids.add(Integer.valueOf(iloci[j])+1);
        }
        Set sloci = get_lociset(iloci);
          if(i==0){
            previous_lociset = sloci; 
          nonhd = new HashMap();
          noncd = new HashMap();
        }
        if(sigs_occurred && (!sloci.equals(previous_lociset))){
          sigs_occurred = false;
          determine_nond(haps_storage,nonhd, previous_lociset);
          determine_nond(cgts_storage,noncd, previous_lociset);
          previous_lociset = sloci;
          nonhd = new HashMap();
          noncd = new HashMap();
        }
        String model = a[i].getModel();
        String key = generateKey(ids,model);
        //System.out.println("Key: " + key);
        for( int j=0; j < tables[r].length; j++ )
        {
          CCStatRun.Report[] stat_reports = a[i].getStatReports(r,j);
          CCStatRun.Report[] reports = stat_reports;
          if ( a[i].getMetaReports(r).length > 0 && j == 0 ) 
          {
            CCStatRun.Report[] meta_reports = a[i].getMetaReports(r);
            List<CCStatRun.Report> temp = new ArrayList<CCStatRun.Report>(stat_reports.length + meta_reports.length);
            temp.addAll(Arrays.asList(meta_reports));
            temp.addAll(Arrays.asList(stat_reports));
            reports = (CCStatRun.Report[]) temp.toArray(new CCStatRun.Report[temp.size()]);
            //j = tables[r].length;
          }
          
          for( int k=0; k < reports.length; k++ )
          {    
            //Need to put a check in here to make sure that the analyses in the 
            //first step are each done on single SNPs rather than sets of SNPs.
            String statsTitle = reports[k].getTitle();
            String sTitle = statsTitle.split(",")[0];
            //The key consists of the loci set, model, and statistical test used.
            String sigKey = key + ":" + sTitle;
            //Need to extract the proper pvalues.
            String pvalue = reports[k].getInferentialReport();
            //Extract the observed value for the Odds Ratios to find the risk.
            String obsvalue = reports[k].getObservationalReport();
            String[] pvs = pvalue.split(" ");
            String[] obs = obsvalue.split(" ");
            List<String> sigs = sigPs(pvs, obs, sigKey, screentest, model, sloci);
            if(sigs.size() > 0){
              if( step > 1 )
               {
                String sletter = (String) model.substring(1,2);
                String fletter = (String) model.substring(0,1);
                int lp = 0;
                sigs_occurred = true;
                // Store a specific haplotype or a composite genotype score. This means
                // Haplotype global and composite genotype global are not allowed.
                if ( (!sletter.equals("G")) || (fletter.equals("M") && sletter.equals("G") && !(sTitle.indexOf("Chi") > -1))) 
                {
                  store_specific_haps(sigs,sigKey,haps_storage, cgts_storage,nonhd,noncd);    
                }
              }
              obsSigs.put(sigKey,sigs);
            }
          }
        }
      }
    }
    if( step > 1 )
    {
      hc.restore_savedhaps(haps_storage,"haplotypes");
      hc.restore_savedhaps(cgts_storage,"composite");
    }
  }
  
  //----------------------------------------------------------------------------  
  private Set get_lociset( int[] lociset )
  {
    Set s = new TreeSet();
    for ( int j=0; j < lociset.length; j++ )
    {
      s.add(lociset[j]+1);	
    }
    return s;
  }
  
  //----------------------------------------------------------------------------
  private void determine_nond(Map storage, Map temp, Set lociset){
    if(storage.containsKey(lociset)){
      String[][] values = (String[][]) storage.get(lociset);
      String modelinc = values[0][0];
      String modeldec = values[0][1];
	  double lowest = 1.1;
	  for(Iterator it = temp.keySet().iterator();it.hasNext();){
	    String nond_model = (String) it.next();
	    if(!nond_model.equals(modelinc) && !nond_model.equals(modeldec)){
	      double pv = (Double) temp.get(nond_model);
	      if(pv < lowest){
	        values[0][2] = nond_model;
	      }
	    }
	  }
    }
  }
    
  //----------------------------------------------------------------------------
  private void store_specific_haps( List<String> sigs, String sigkey, Map hstorage, Map cstorage, Map temph, Map tempc )
  {
    //sigKey is output with loci no.:model:stat test
    //sigs is output with comparison:observed value:pvalue
	//Extract what loci set you are looking at first, then you need to
	//determine which map you need to look at depending on the direction of the
	//odds ratio.
	String[] sig_key_elements = sigkey.split(":");
	List lmodel = new ArrayList();
	String model = sig_key_elements[1];
	//String model = sig_key_elements[1];
	String model_type = model.substring(0,1);
	Map m = hstorage;
	Map temp = temph;
	String storage_type = "haplotype";
	//You need to storage both composite genotype and haplotype / monotype separate.
	if( model_type.equals("C") )
	{
	  m = cstorage;
	  temp = tempc;
	  lmodel.add(model);
	  storage_type = "composite";
	}
	else if( model_type.equals("H") )
	{
	  lmodel.add(model.substring(4, model.length()));  
	}
	else if( model_type.equals("M") && model.substring(1,2).equals("G") )
	{
	  for (int i = 0; i < sigs.size(); i ++)
	  {
	    String mdl = sigs.get(i).split(":")[0];
	    lmodel.add(mdl.split("v")[0]);
	  }
	}
	else
	{
	  lmodel.add(model.substring(4, model.length())); 
	}
	String stat_test = sig_key_elements[2];
	String[] loci = sig_key_elements[0].split("-");
	Set lociset = new TreeSet();
	for( int j = 0; j < loci.length; j++ )
	{
	  lociset.add(Integer.parseInt(loci[j]));
	}
	//This will determine if you store the current values in the temp_storage map or not
	//The temp storage map has key = loci set and value = 2-d array
	//The 2-d array is a 2 x 3 array. The first row has the 
	//Increasing haplotype, decreasing haplotype number, non-directional haplotype.
	//The second row contains the lowest p-value from the tests associated 
	//with these haplotypes.
	check_storage(m,stat_test,lmodel,lociset,sigs,temp);
  }
  
  //----------------------------------------------------------------------------
  private void check_storage( Map m, String stat_test,List model, Set lociset, List sigs, Map temp )
  {
	String[][] values = new String[3][3];
	for( int k = 0; k < values.length; k++ )
	{
      values[k][0] = "0";
      values[k][1] = "0";
      values[k][2] = "0";
	}
    if( m.containsKey(lociset) )
    {
      values = (String[][]) m.get(lociset);
    }
	for( int i =0; i < sigs.size(); i++ )
	{
	  String mdl = (String) model.get(0);
	  
	  if ( model.size() > 1)
	  {
	    mdl = (String) model.get(i);  	  
	  }
	  
	  String sig = (String) sigs.get(i);
      String[] split_sig = sig.split(":");
	  double observed = 0;
	  double pval = 0;
	  if( split_sig.length > 2 )
	  {
		//In this case the statistic has a comparison
		observed = Double.parseDouble(split_sig[1]);
		pval = Double.parseDouble(split_sig[2]);
	  }
	  else
	  {
	    observed = Double.parseDouble(split_sig[0]);
		pval = Double.parseDouble(split_sig[1]);
	  }
	  int store_col = -1;
      if( stat_test.indexOf("Odds") > -1 )
      {
        if(observed > 1){
          store_col = 0;
        }
        else{
      	  store_col = 1;
        }
	  } 
      else
      {
    	if( temp.containsKey(mdl) )
    	{
    	  if( (Double) temp.get(mdl) > pval )
    	  {
            temp.put(mdl,pval);  
    	  }
    	}
    	else
    	{
          temp.put(mdl,pval);    	
    	}
      }
      
      if( store_col >= 0 )
      {
      //If p-values are 0, then store no matter what.
    	boolean store_model = false;
        if( values[0][store_col].equals("0") | (pval < Double.parseDouble(values[1][store_col])) )
        {
          values[2][store_col] = observed + "";
          values[1][store_col] = pval + "";
          values[0][store_col] = mdl;
          store_model = true;
        }
        else if( pval == Double.parseDouble(values[1][store_col]) )
        {
          if( store_col == 0 && observed > Double.parseDouble(values[2][0]) )
          {
            values[2][store_col] = observed + "";
            values[1][store_col] = pval + "";
            values[0][store_col] = mdl;
            store_model = true;
          }
          else if( store_col == 1 && observed < Double.parseDouble(values[2][1]) )
          {
            values[2][store_col] = observed + "";
            values[1][store_col] = pval + "";
            values[0][store_col] = mdl;
            store_model = true;
          }
        }
        
        if ( store_model && values[0][2].equals(mdl) )
        {
          values[0][2] = "0";
          values[1][2] = "0";
          values[2][2] = "0";
        }
        
      }
    }
	if( !temp.isEmpty() )
	{
	  values = findnond(temp,values);	
	}
	m.put(lociset,values);
  }
  
  //----------------------------------------------------------------------------  
  private String[][] findnond( Map temp, String[][] values )
  {
    Set keys = temp.keySet();
    String incmodel = values[0][0];
    String decmodel = values[0][1];
    if( keys.contains(incmodel) )
    {
      keys.remove(incmodel);
    }
    if( keys.contains(decmodel) )
    {
      keys.remove(decmodel);
    }
    if( keys.size() != 0 )
    {
      double lowestpval = 1.0;
      String lowestmodel = "0";
      for( Iterator it = keys.iterator(); it.hasNext(); )
      {
    	String model = (String) it.next();
        double pval = (Double) temp.get(model);
        if( pval < lowestpval )
        {
          lowestpval = pval;
          lowestmodel = model;
        }
      }
      values[0][2] = lowestmodel;
      values[1][2] = lowestpval +  "";
    }
    return values;
  }
  
  //----------------------------------------------------------------------------
  private Object process_pv(String model, String pvalue){
	//If there are more than 2 cols then there will be multiple p-values
	//for the odds-ratio test. Store these in a list.
    String[] sp = pvalue.split(":");
    if(sp.length > 1){
      List ps = new ArrayList();
      for(int i=1;i<sp.length;i++){
    	String[] ss = sp[i].split(" ");
        double pv = 0;
        if(ss[0].equals("-")){
          pv = 1;
        }
        else{
          pv = Double.valueOf(ss[0]).doubleValue();
        }
        ps.add(pv);
      }
      return ps;
    }
    else{
      String dm = sp[0];
      double ps = 0;
      if(dm.equals("-")){
        ps = 1;
      }
      else{
        ps = Double.valueOf(dm).doubleValue();
      }
      return ps;
    }
  }

  //----------------------------------------------------------------------------
  //This function determines if p-values are significant based on a given threshold.
  //If the p-value is significant, a string is created with the observed value and added
  //to the list to return as the value for the obsSigs dictionary.
  private List<String> sigPs(String[] pvals, String[] obs, String sigKey, int screentest, String model, Set lociset){
	//Create a list of strings which are the values in the obsSigs dictionary
	List<String> screensigs = new ArrayList<String>();
	List<String> sigs = new ArrayList<String>();
	List<String> simulated_pvals = new ArrayList<String>();
	List<String> obs_pvals = new ArrayList<String>();
    for(int i=0;i<pvals.length;i++){
      String x = pvals[i];
      String obs1 = obs[i];
      String sig_val = "";
      String obs_val = "";
      if(x.split(":").length > 1){
        sig_val = x.split(":")[1];
        obs_val = obs1.split(":")[1];
      }
      else{
        sig_val = x;
      }
      
      double pv = 0;
      if(sig_val.equals("-") || obs_val.equals("-")){
    	//System.out.println("Failed test");
        pv = -1;
      }
      else{
        pv = Double.valueOf(sig_val).doubleValue();
      }
      if(pv == 0.0){
        pv = 1.0 / nsims;
      }
      if(pv == 1.0 / nsims){
        hc.mostsigreached = true;  
      }
      String complete_val = obs1 + ":" + pv;
      //Determine which threshold to use for which step.
      //Use all the values in the thold array until it reaches
      //the last value in the array.
      double[] thresh = getTHOLD(screentest);
      if ( pv <= thresh[1] && pv > -1 )
      {
        sigs.add(complete_val);
      }
      if ( pv <= thresh[0] && pv > -1 && model.substring(0,1).equals("H") && screentest == 0 && step > 1 )
      {
        screensigs.add(complete_val);
      }
      //Store all simulated p-values to find FDR
      if(sigrun > -1 && pv > -1.0){
    	simulated_pvals.add(complete_val);
      }
      else if(sigrun == -1 && pv > -1.0){
        obs_pvals.add(complete_val);
      }
    }
    if(sigrun != -1){
      if(simulated_pvals.size() > 0){
        store_simPVals(step, sigrun + ":" + sigKey,simulated_pvals);
      }
    }
    else{
      if(obs_pvals.size() > 0){
        store_obsPVals(step, sigrun + ":" + sigKey,obs_pvals);
      }
    }
    if(screensigs.size() > 0)
    {
      String[] key = sigKey.split(":");
      String hap = "";
      if(model.substring(0,1).equals("H"))
      {
        hap = model.substring(4,model.length());
      }
      Set haps = new TreeSet();
      if ( screenSigs.containsKey(lociset))
      {
        haps = (Set) screenSigs.get(lociset);  
      }
      haps.add(hap);
      screenSigs.put(lociset, haps);
    }
    return sigs;
  }
  
  private double[] getTHOLD (int screentest)
  {
	  double[] tholds = tHold;
	  double[] val = new double[2];
	  boolean inpt_screenthrsh = false;
	  if ( screentest == 0 && screentHold.length > 0 )
	  {
	    tholds = screentHold;
	    inpt_screenthrsh = true;
	  }
      
	  int array_loc = 0;
	  if ( step < (1 + tholds.length) )
	  {
	    array_loc = step - 1;
	  }
	  else
	  {
	    array_loc = (tholds.length - 1);
	  }
	  
	  val[0] = tholds[array_loc];
	  val[1] = tHold[array_loc];
	  
	  if ( !inpt_screenthrsh && screentest == 0)
	  {
//        int nterm = 1;
//        if ( nsims >= 1000 && nsims < 10000)
//        {
//          nterm = nsims / 1000;
//        }
//        else if ( nsims >= 10000)
//        {
//          nterm = (nterm / 1000) + 5;
//        }
//        val[0] = val[0] * nterm;
		  val[0] = Math.min(1.0, (tHold[array_loc]*10) / Math.pow(2, (step-2)));
	  }
	  
	  return val;
  }
  
  public void storeSigs()
  {
    for(Iterator it = obsSigs.entrySet().iterator();it.hasNext();){
      //Set<String> sigSet = new HashSet<String>();
      SortedSet<Integer> sigSet = new TreeSet<Integer>();
      Map.Entry entry = (Map.Entry)it.next();
      String key = (String)entry.getKey();
      String[] keyLoci = key.split(":");
      String iloci = keyLoci[0];
      String[] locIDs = iloci.split("-");
      for(int i =0;i<locIDs.length;i++){
        sigSet.add(Integer.parseInt(locIDs[i]));
      }
      sigResults.add(sigSet);
    }
    obsSigs = new HashMap();
  }
  
  //----------------------------------------------------------------------------
  public Set<Set> getSigs()
  {
    return sigResults;
  }

  //---------------------------------------------------------------------------- 
  public locusManager getLocusManager(){
    return lm;
  }
  
  //----------------------------------------------------------------------------
  private String generateKey(List<Integer> ids, String model){
	int key = ids.get(0);
	String k = key + "";
    for(int i=1;i<ids.size();i++){
      k = k + "-" + ids.get(i);
    }
    k = k + ":" + model;
    return k;
  }
  
  //----------------------------------------------------------------------------
  public Map getSigMap(){
    return obsSigs;
  }

  public void store_simPVals(int step, String key, List<String> value) {
    List lvl = new ArrayList();
	if(simPvals.containsKey(step-1)){
	 lvl = simPvals.get(step-1);
	}
	String p = value.get(0);
	for(int m=1;m < value.size();m++){
	  p = p + ":" + value.get(m);
	}

	lvl.add(key + ":" + p);
	simPvals.put(step-1, lvl);
  }

  public void store_obsPVals(int step, String key, List<String> value) {
    List lvl = new ArrayList();
	if(obsPvals.containsKey(step-1)){
	 lvl = obsPvals.get(step-1);
	}
	//simSigs.put(key, value);
	String[] ss = value.get(0).split(":");
	//System.out.println("sim pval: " + ss[ss.length - 1]);
	String p = value.get(0);
	for ( int m=1; m < value.size(); m++ )
	{
	  p = p + ":" + value.get(m);
	}
	//simulatedpvals.add(key + ":" + p);
	lvl.add(key + ":" + p);
	obsPvals.put(step-1, lvl);
  }

  //For now return a list of all pvals
  public Map<Integer,List> get_simPvals() {
	return simPvals;
  }

  //For now return a list of all pvals
  public Map<Integer,List> get_obsPvals() {
	return obsPvals;
  }
  
  public void clear_Pvals( String filename )
  {
	if(filename.equals("all_obs"))
	{
      obsPvals = new HashMap();
	}
	else
	{
      simPvals = new HashMap();
	}
  }
  
  
}

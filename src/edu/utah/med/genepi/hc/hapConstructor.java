package edu.utah.med.genepi.hc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.utah.med.genepi.app.rgen.IOManager;
import edu.utah.med.genepi.app.rgen.PedGenie;
import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Ut;

public class hapConstructor {
  private int                 step;
  private final Specification spec;
  private Map<Set,List> 	  testedSets = new HashMap<Set,List>();
  private analysisResults 	  aResults;
  private final String 		  app_id;
  private Set 				  newLociSet;
  private evalManager         eManager;
  private passManager         pManager;
  private Calendar            inDate;
  private String              outpathStem;
  public int                  screentest = -1;
  private boolean             screentesting = false;
  private List                analysis_mdls;
  public String               step_dir;
  
  public boolean             mostsigreached = false;
  private Map                prev_hinc_map = new HashMap();
  private Map                prev_hdec_map = new HashMap();
  private Map                prev_hnon_map = new HashMap();
  private Map                prev_cinc_map = new HashMap();
  private Map                prev_cdec_map = new HashMap();
  private Map                prev_cnon_map = new HashMap();
  private Map                new_hinc_map = new HashMap();
  private Map                new_hdec_map = new HashMap();
  private Map                new_hnon_map = new HashMap();
  private Map                new_cinc_map = new HashMap();
  private Map                new_cdec_map = new HashMap();
  private Map                new_cnon_map = new HashMap();  
  private Map                haps_storage = new HashMap();

  //----------------------------------------------------------------------------
  /**
   * @author Ryan Abo
   * 
   *hapConstructor is the main manager for the haplotype building. One hapBuilder instance
   *is used to build the haplotypes for each set of observed data. The hapBuilder  
   *keeps track of the evaluation of all the significant haplotypes built for the observed 
   *data with the evalManager object, as well as all the backSets with the passManager.
   *There are two constructors: one that creates a new evalManager, and one that passes 
   *the previously created evalManager. 
   */
  
  public hapConstructor ( String appid, Specification sp, 
		  CCAnalysis[] analyses, String reporterStem, boolean st ) throws GEException, IOException
  {
    app_id = appid;
    step = 1;
    spec = sp;
    //The eManager manages the significant loci from the observed data set and 
    //tracks the significant results from the evaluations.
    eManager = new evalManager(analyses);
    pManager = new passManager(step);
    outpathStem = reporterStem;
    screentesting = st;
  }
  
  //----------------------------------------------------------------------------
  /*Constructor for the hapBuilder class. This creates a new hapBuilder instance
  but it uses the previously created evalManager. This constructor is used during
  evaluation of the observed data.
  */
//  public hapConstructor(String appid, Specification sp, 
//		  CCAnalysis[] analyses, String reporterStem, evalManager eval) throws GEException, IOException
//  {
//	app_id = appid;
//    step = 1;
//    spec = sp;
//    eManager = eval;
//    pManager = new passManager(step);
//    outpathStem = reporterStem;
//  }
  
  //----------------------------------------------------------------------------
  //Function returns the evalManager object.
  public evalManager getEval()
  {
    return eManager;
  }
  
  //----------------------------------------------------------------------------  
  public void updateStep ( int s )
  {
    step = s;
    pManager.update(step);
  }  
  
  //----------------------------------------------------------------------------
  /*This function is called in checkBuild function in PedGenie.java. It takes the 
  processed results from the previous analyses stored in the aResults object and
  creates the new loci sets for the next analyses, and also determines if there are
  backward sets to be tested in the next pass. It returns a boolean value to determine
  if analyses should be performed for a new set of loci or not.
  */ 
  public boolean beginBuild() throws GEException, IOException
  {
    //Obtain the significant loci sets from previous analysis.
    Set sigLoci = aResults.getSigs();
    //Change sigLoci from indices to marker ids before making the newSet()
    //Get all marker ids.
    List<Integer> lallIDs = aResults.getLocusManager().getAllIDs();
//	List<Integer> markers = new ArrayList<Integer>();
//	for(int i =0; i < lallIDs.size(); i++){
//	  int m = Integer.parseInt(lallIDs.get(i));
//	  markers.add(m);
//	}
    
    //Create all possible sets with the sigLoci and list of all loci.
    Set<Set> sigs = newSet(sigLoci,lallIDs);
    //Check if there are backsets from previous pass.
    int yy = 0;
    Set bSets = pManager.getBackSets(step);
    //Incorporate backsets into new analysis set.
    if ( bSets.size() > 0 )
    {
      for ( Iterator it = bSets.iterator(); it.hasNext(); )
      {
        Set s = (Set) it.next();
        sigs.add(s);
      }
    }
    //Save the new loci sets that will be tested.
    newLociSet = saveSets(sigs);
    //if(newLociSet.size() == 0 | mostsigreached)
    //if ( newLociSet.size() == 0 )
    if (newLociSet.size() == 0 |
       (mostsigreached && spec.gethapC_check_mostsignificant()))
    {		
      return false;
    }
    else
    {
      return true;
    }
  }
  
  //----------------------------------------------------------------------------
  /*This function is called from checkBuild function in PedGenie.java. The results from
  the previous analysis are passed into this function to pass on to be processed for the 
  hapBuilder wrapper.
  */
  public void updateResults ( int sigrun, int num_sims, CCAnalysis[] analyses,Map mParams, GDef gd, int stp, Calendar iDate, PedGenie ped, compressGtype[] cGtypes, int new_analysis )
  {
    //step = stp;
    inDate = iDate;
    //pManager.update(step);
    for (int i =0 ; i < cGtypes.length; i++) 
    {
      cGtypes[i].clear_bad_analyses();
    }
 
    //Create an analysisResults object to extract significant results.
    if(screentest < 1 && new_analysis == 1)
    {
      aResults = new analysisResults(analyses, mParams, gd, step, num_sims, sigrun, screentest,this);
      if(screentesting && step == 1)
      {
        screentest++;
      }
    }
    else
    {
      aResults.loadResults(analyses, mParams, screentest);
//      if(screentesting)
//      {
//        screentest = 0;
//      }
    }
    //Save the significant results from the observed data in the eManager.
    int obssigs_size = aResults.getSigMap().size();
    int screensigs_size = aResults.screenSigs.size();
    if ( (sigrun == -1) && (screentest < 1 || screensigs_size == 0 ) )
    {
      //The observed significant tests are saved in analysisResults
      //eManager.saveObserved(aResults.getSigMap());
      //Write out the results from each analysis in to a file.
      try 
      {
        writeReport(sigrun + 1);
        write_pvals("all_obs");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else if( (sigrun != -1) && (screentest < 1 || screensigs_size == 0) )
    {
      try
      {
        write_pvals("all_sims");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }    
  }
  
  public boolean checkscreentest()
  {
    boolean checked = false;
    if ( aResults.screenSigs.size() > 0 )
    {
      checked = true;
    }
    return checked;
  }
  
  public Set getScreenedSets()
  {
    Set s = aResults.screenSigs.keySet();
    return s;
  }
  
  //----------------------------------------------------------------------------
  //Return passManager member variable to determine if backsets exist.
  public boolean backSetExists ()
  {
    if ( pManager.backSets.size() == 0 )
    {
      return false;
    }
    else
    {
      return true;
    }
  }
  
  //----------------------------------------------------------------------------
  //Write information from each analysis to a file.
  private void writeReport ( int cycle ) throws IOException
  {
    //First write the observed sig file
    String pathStem = outpathStem + "_" + step;
    File freport = new File(pathStem + "-" + cycle + ".build");	  
    Map sigs = aResults.getSigMap();
    //aResults.storeSigs();
    int mapsize = sigs.size();
    Iterator kvp = sigs.entrySet().iterator();
    //Get the evalMap to check if any sigs are in evalMap
	  
    if ( freport.exists() )
    {
      String fname= pathStem + "-" + cycle + ".build";
      boolean append = true;
      FileWriter fw = new FileWriter(fname,append);
      for ( int i = 0; i < mapsize; i++ )
      {
        Map.Entry entry = (Map.Entry) kvp.next();
        String key = (String) entry.getKey();
        List value = (List) entry.getValue();
        for ( int j = 0; j < value.size(); j++ )
        {
          String xx = key + ":" + value.get(j);
          //Check to only output pvals
          fw.write(xx + "\n");
        } 
      }
      fw.close();
    }
    else
    {
      freport  = Ut.fExtended(pathStem + "-" + cycle, "build");
      PrintWriter pwReport  = new PrintWriter(new BufferedWriter(new FileWriter(freport)), true);
      for (int i = 0; i < mapsize; i++ )
      {
        Map.Entry entry = (Map.Entry) kvp.next();
        String key = (String) entry.getKey();
        List value = (List) entry.getValue();
        for ( int j = 0; j < value.size(); j++ )
        {
          String xx = key + ":" + value.get(j);
          pwReport.println(xx);
        } 
      }
      //putElapseTime(pwReport);
      closeReport(pwReport, freport);
    }
  }
  
  //----------------------------------------------------------------------------  
  private void write_pvals ( String filename ) throws IOException
  {
    //Write to all_obs file
    Map<Integer,List> map_pvals = aResults.get_simPvals(); 
    
    if ( filename.equals("all_obs") )
    {
      map_pvals = aResults.get_obsPvals(); 
    }
    aResults.clear_Pvals(filename);
    aResults.storeSigs();
    int mapsize = map_pvals.size();
    Iterator kvp = map_pvals.entrySet().iterator();
    String pather = filename;
    File fall_obs = new File(pather + ".final");
	
    if ( fall_obs.exists() )
    {
      String fname= filename + ".final";
      boolean append = true;
      FileWriter fw = new FileWriter(fname,append);
      
      for ( int i = 0; i < mapsize; i++ )
      {
        Map.Entry entry = (Map.Entry) kvp.next();
        List value = (List) entry.getValue();
        for ( int j = 0; j < value.size(); j++ )
        {
          String xx = (String) value.get(j);
          //Check to only output pvals
          fw.write(xx + "\n");
        } 
      }
      fw.close();
    }
    else
    {
      PrintWriter pwReport  = new PrintWriter(new BufferedWriter(new FileWriter(fall_obs)), true);
      for ( int i = 0; i < mapsize; i++ )
      {
        Map.Entry entry = (Map.Entry) kvp.next();
        List value = (List) entry.getValue();
        for ( int j = 0; j < value.size(); j++ )
        {
          String xx = (String) value.get(j);
          //Check to only output pvals
          pwReport.write(xx + "\n");
        } 
      }	    
      //putElapseTime(pwReport);
      closeReport(pwReport, fall_obs);
    }
  }
  
  //----------------------------------------------------------------------------
  /* Code taken from Reporter.java to write reports specific to the builder.
   */
  void closeReport(PrintWriter pw, File file)
  {
    pw.close();
    //System.out.println("Report written to '" + file + "'." + Ut.N);
  }
  
  //----------------------------------------------------------------------------
  /* Code taken from Reporter.java to write reports specific to the builder.
  */
  void putElapseTime(PrintWriter pwReport)
  {
    long[] times = getElapsedTime();
    pwReport.println();
    pwReport.println("Elapse Time : " + times[0] + " h : " + 
                     times[1] + " m : " +
                     times[2] + " s" );
  }
  
  //----------------------------------------------------------------------------
  /* Code taken from Reporter.java to write a report specific for the builder.
  */
  public long[] getElapsedTime()
  {
    long outSecs  = Calendar.getInstance().getTimeInMillis();
    long inSecs   = inDate.getTimeInMillis();
    long diffSecs = outSecs - inSecs; 
    long hours    = diffSecs / ( 1000 * 60 * 60 );
    long mins     = diffSecs / ( 1000 * 60 ) - hours * 60;
    long secs     = diffSecs / 1000 - mins * 60; 
   
    return new long[] { hours, mins, secs };
  }
  
  //----------------------------------------------------------------------------
  public int backSetLevel ()
  {
    Set<Integer> ss = (Set<Integer>) pManager.backSets.keySet();
    Iterator it = ss.iterator();
    int first = (Integer) it.next();
    while ( it.hasNext() )
    {
      int item = (Integer) it.next();
      if ( item < first )
      {
        first = item;
      }
    }
    System.out.println("Backset" + pManager.backSets.toString());
    System.out.println("Backset first element: " + first);
    return first;
  }
  
  //----------------------------------------------------------------------------
  public Set getBackSet ( int indx )
  {
    step = indx;
    return pManager.getBackSets(step);
  }
  
  //----------------------------------------------------------------------------
  public List getOriginalAnalysis ()
  {
    return eManager.originalAnalysis;
  }
  
  //----------------------------------------------------------------------------
  public String getProcess ()
  {
    return eManager.process_id;
  }
  
  //----------------------------------------------------------------------------
  public Map<Integer,List> getsimPvals ()
  {
    return aResults.get_simPvals();
  }
  
  //----------------------------------------------------------------------------
  public Map<Integer,List> getobsPvals ()
  {
    return aResults.get_obsPvals();
  }
  
  //----------------------------------------------------------------------------
  public void updateProcess ()
  {
    eManager.process_id = "eval";
  }
  
  //----------------------------------------------------------------------------
  public Set getNewLoci ()
  {
    return newLociSet;
  }

  //----------------------------------------------------------------------------
  public void controller ( IOManager iom, PedGenie pdg, Set set, compressGtype[] cGtypes, int sigrun, String step_type ) throws GEException, IOException
  {
    Iterator it = set.iterator();
    Set x = (Set) it.next();
    int sze = x.size();
    
    if ( sze > 2 )
    {
      //Check if there are backsets to save for the next go around.
      pManager.storeBackSets(iom, pdg, set, testedSets);
    }
    List<String> sAnalyses = setAnalyses(pdg.hapmodels);
    analysis_mdls = setAnalyses(pdg.hapmodels);
    newLociSet = set;
    step_dir = step_type;
    //Set up analyses for the new set of loci. 
    //createAnalyses(set, sAnalyses, cGtypes, sigrun, step_type);
  }
  
  //---------------------------------------------------------------------------
  public List get_amdls()
  {
    return analysis_mdls;
  }
  
  //----------------------------------------------------------------------------
  public Set saveSets ( Set set )
  {
	Set<SortedSet> removalSet = new HashSet<SortedSet>();
   
    for ( Iterator it = set.iterator(); it.hasNext(); )
    {
      SortedSet s = (SortedSet)it.next();
      if ( testedSets.containsKey(s) )
      {
        removalSet.add(s);
      }
      else
      {
        List l = new ArrayList();
        testedSets.put(s,l); 
      }
    }
    for ( Iterator it2 = removalSet.iterator(); it2.hasNext(); )
    {
      SortedSet rs = (SortedSet)it2.next();
      set.remove(rs);
    }
    return set;
  }
  
  public int get_screentest()
  {
    return screentest;
  }
  
  //----------------------------------------------------------------------------
  //This function is called from controller function. This function sends a Set of 
  //Sets of new loci to create analyses for, and updates the specification object with 
  //the new analyses.
  private void createAnalyses ( Set loci, List a, compressGtype[] cGtypes, int sigrun, String step_type ) throws GEException
  {
    List<CCAnalysis> analyses = new ArrayList<CCAnalysis>();
    //Iterate through loci, which is a Set of Sets (i.e. [[1,2],[1,3],[2,3]]). 
    //Each nested Set contains a set of loci to be considered for an analysis.
    int counter = 0;
    for ( Iterator it=loci.iterator(); it.hasNext(); )
	{
      List a_copy = new ArrayList();
      a_copy.addAll(a);    	
      Set lociSet = (Set)it.next();
      int[] iloci = aResults.getLocusManager().getLocInd(lociSet);
      String[] markers = aResults.getLocusManager().getMarkers(lociSet);
      //For each new set of loci, create the appropriate analyses for them.
      //Modify the a list for each loci set...preprocess counts for HDom, HRec, and HAdd, MSpec
      //If counts fail don't do those analyses.
      //List new_a = new ArrayList();

      if ( screentesting && screentest == 1 )
      {
    	a_copy = onlyScreenedHaps(a_copy,lociSet);
      }
      if (cGtypes.length > 1) 
      {
        for ( int i = 0; i < cGtypes.length; i ++ ) 
        {
          cGtypes[i].prescreen_analyses(a_copy, lociSet, iloci, sigrun);	
        }
      }
      else
      {
        a_copy = cGtypes[0].prescreen_analyses(a_copy, lociSet, iloci, sigrun);
      }
      //You can only do the specific haplotype testing when moving forward since it is using
      //information from previous runs to choose the specific haplotypes from each loci set.
      counter = counter + 1;

      if ( step > 2 && step_type.equals("forward") && screentest < 1 )
      {
        a_copy = select_testing_haps(iloci,lociSet,a_copy);
      }
	  
      String[] global_list = new String[] {"MGlobal","HGlobal","CGlobal"};
      for( int l = 0; l < global_list.length; l++ )
      {
    	String mdl = global_list[l];
        if ( a_copy.contains(mdl + "OR") && a_copy.contains(mdl + "CS") )
        {
          a_copy.remove(mdl+"OR");
          a_copy.remove(mdl + "CS");
          a_copy.add(mdl+"OR/CS");
        }
      }
      
      for ( int i=0; i < a_copy.size();i++ )
      {
        analyses.add(new analysisSet(app_id, i, spec, markers, iloci, lociSet,(String) a_copy.get(i),step, sigrun, screentest).getAnalysis());
      }
      //Update the specification with the new analyses, since PedGenie function gets the 
      //new analyses through the specification object.
      spec.updateAnalyses(analyses);	  
    }
//    CCAnalysis[] la = new CCAnalysis[analyses.size()];
//    
//    for ( int i = 0; i < analyses.size(); i++ ) 
//    {
//      la[i] = analyses.get(i);
//    }
//    return la;
  }
  
  public analysisResults getaresults()
  {
    return aResults;
  }
  
  public List onlyScreenedHaps ( List analyses, Set lociset )
  {
    Set hapset = aResults.screenSigs.get(lociset);
    List new_a = new ArrayList();
    for ( int j=0; j < analyses.size(); j++ )
    {
      String model = (String) analyses.get(j);
      String matchinghap = "";
      int l = model.length();
      if(model.substring(0,1).equals("C") || model.substring(1,2).equals("G"))
      {
        new_a.add(analyses.get(j));
      }
      else if(model.substring(0,1).equals("M"))
      {
        matchinghap = model.substring(8,model.length());
      }
      else if(model.substring(0,1).equals("H"))
      {
        matchinghap = model.substring(4,model.length());    	  
      }
      if( hapset.contains(matchinghap) )
      {
        new_a.add(analyses.get(j));
      }
    }
    return new_a;
  }

  //----------------------------------------------------------------------------
  /* Create a list of analyses depending on the step that it is on.
  */
  public List<String> setAnalyses(Map amodels)
  {
    //Allow user to specify which models to test.
	List am = new ArrayList();
	if ( !screentesting)
	{
      am = (List) amodels.get(0);
	}
	else
	{
	  am = (List) amodels.get(screentest);
	}
    String[] al = new String[am.size()];
    
    for ( int i=0; i < am.size(); i++ )
    {
      String model = (String) am.get(i);
      al[i] = model;
    }
    List<String> a = new ArrayList<String>();
	
    for ( int k=0; k < al.length; k++ )
    {
      //Determine the model for the analysis.
      String mdl = al[k];
      if ( mdl == "HDom" || mdl == "MSpecRed" || mdl == "HRec" || mdl == "HAdd" )
      {
        //Use the step to determine how many specific analyses there will be for each of the above models
        double dstep = Double.valueOf(step).doubleValue();
        double lim = Math.pow(2.0,dstep);
        //Add new model to the list.
        for ( int j=0; j < (int)lim; j++ )
        {
          a.add(mdl + "" + (j+1));
        }
      }
      else if ( mdl == "CDom" )
      {
        //Create a new list, copy it and combine them.
        //String[] mods = new String[] {"CDom","CRec","CAdd"};
        String[] mods = new String[] {"CDom","CRec"};
        List<String> lmods = Arrays.asList(mods);
        List<String> cmods = new ArrayList<String>();
        cmods.addAll(lmods);
        for ( int l=0; l < (step-1); l++)
        {
          cmods = combine(lmods,cmods);	
        }
        //Add the new set of models to the list a
        for ( int l=0; l < cmods.size(); l++ )
        {
          a.add(cmods.get(l));
        }
      }
      else
      {
        a.add(mdl);
      }
    }
    //Return the list of models to use for the new set of loci.
    return a;
  }

  //----------------------------------------------------------------------------
  /* This function is called from setAnalyses function to     create a set of analyses
  from a list of models. It will pass in two of the same lists (i.e. {1,2,3} and {1,2,3})
  and return a combined list (i.e. {1/1, 1/2, 1/3, 2/2, 2/3, 3/3}). 
  */
  public List<String> combine ( List<String> l1, List<String> l2 )
  {
    List<String> combined = new ArrayList<String>();
	
    for ( int i=0; i < l1.size(); i++ )
    {
	  for ( int j=0; j < l2.size(); j++ )
      {
        String together = l1.get(i) + "/" + l2.get(j);
        combined.add(together);
      }
    }
    return combined;
  }

  //----------------------------------------------------------------------------
  public Set<Set> newSet ( Set sigResults, List aMarkers )
  {
    Set<Set> finset = new HashSet<Set>();
	
    for ( Iterator it = sigResults.iterator();it.hasNext(); )
    {
      SortedSet smarker = (SortedSet) it.next();
      for ( int i = 0; i < aMarkers.size(); i++ )
      {
        int omarker = (Integer) aMarkers.get(i);
        if ( !smarker.contains(omarker) )
        {
          SortedSet newset = new TreeSet();
          newset.addAll(smarker);
          newset.add(omarker);
          finset.add(newset);
        }
      }
    }
    return finset;
  }
  
  //----------------------------------------------------------------------------
  /*This function is called in PedGenie to return the evalMap object, which
  is a HashMap with the string keys of loci sets:model:stat that were significant
  and values being the number of times in the evaluation that the same keys were
  found to be significant.
  */
  public Map reportEval ()
  {
    return eManager.evalMap;
  }
  
  //----------------------------------------------------------------------------
  public List select_testing_haps ( int[] iloci, Set loci, List screened_tests )
  {
    //Find all n-1 subsets in loci set passed in.
    //Note that iloci contains the zero based loci.
    //System.out.println("Loci " + iloci[0] + " " + iloci[1] + " " + iloci[2]);
    List new_a = new ArrayList();
    List all_sets = new ArrayList();
    int[] reverse_iloci = new int[iloci.length];
    List reverse_loci = new ArrayList();
	
    for ( int i =0; i < iloci.length; i++ )
    {
      reverse_iloci[i] = iloci[iloci.length - 1 - i];
      reverse_loci.add(iloci[iloci.length - 1 - i] + 1);
      Set subset = new TreeSet();
      subset.addAll(loci);
      subset.remove(iloci[iloci.length - 1 - i] + 1);
      all_sets.add(subset);
    }
    List missing_hinc = new ArrayList();
    List missing_hdec = new ArrayList();
    List missing_hnon = new ArrayList();
    Set found_hinc = new HashSet();
    Set found_hdec = new HashSet();
    Set found_hnon = new HashSet();
    Map hinc_alleles = new HashMap();
    Map hdec_alleles = new HashMap();
    Map hnon_alleles = new HashMap();
	
    Set new_cgs = new HashSet();
    int cnt = 0;
    //This for loop determines which loci need to have both alleles considered
    //in the haplotypes.
    for ( Iterator it = all_sets.iterator(); it.hasNext(); )
    {
      Set s = (Set) it.next();
      missing_hinc = store_alleles(missing_hinc,found_hinc,s,hinc_alleles,"haplotype","inc");
      missing_hdec = store_alleles(missing_hdec,found_hdec,s,hdec_alleles,"haplotype","dec");
      missing_hnon = store_alleles(missing_hnon,found_hnon,s,hnon_alleles,"haplotype","non");
      //For composite genotypes, the result will either be, come back with a new 
      //test or not. 
      new_cgs = store_cg(new_cgs,s,reverse_iloci,cnt, "inc", "composite");
      new_cgs = store_cg(new_cgs,s,reverse_iloci,cnt,"dec","composite");
      new_cgs = store_cg(new_cgs,s,reverse_iloci,cnt,"non","composite");
      cnt = cnt  + 1;
    }
	
    cnt = 0;
    int inc_previous = -2;
    int dec_previous = -2;
    int non_previous = -2;
    Set new_haptests = new HashSet();
    
    for ( Iterator it = all_sets.iterator(); it.hasNext(); )
    {
      //Retrieve haplotypes to test for each map.
      Set s = (Set) it.next();
      int hap_binary_inc = Integer.parseInt(get_hap(s,"inc","haplotype"))-1;
      int hap_binary_dec = Integer.parseInt(get_hap(s,"dec","haplotype"))-1;
      int hap_binary_non = Integer.parseInt(get_hap(s,"non","haplotype"))-1;      
      
      if ( inc_previous == -2 && hap_binary_inc != -1 )
      {
        inc_previous = getHap(cnt,s,hap_binary_inc);
      }
      else if( hap_binary_inc != -1 )
      {
        inc_previous = getHap(cnt,s,hap_binary_inc) | inc_previous;    	  
      }
      
      if ( dec_previous == -2 && hap_binary_dec != -1 )
      {
        dec_previous = getHap(cnt,s,hap_binary_dec);
      }
      else if ( hap_binary_dec != -1 )
      {
        dec_previous= getHap(cnt,s,hap_binary_dec) | dec_previous;    	  
      }
      
      if ( non_previous == -2 && hap_binary_non != -1 )
      {
        non_previous = getHap(cnt,s,hap_binary_non);
      }
      else if ( hap_binary_non != -1 )
      {
        non_previous= getHap(cnt,s,hap_binary_non) | non_previous;    	  
      }
      cnt = cnt + 1;
    }
    int p = 0;
    new_haptests = generate_uncertains(inc_previous,new_haptests,missing_hinc,reverse_loci);
    new_haptests = generate_uncertains(dec_previous,new_haptests,missing_hdec,reverse_loci);
    new_haptests = generate_uncertains(non_previous,new_haptests,missing_hnon,reverse_loci);
    new_a = assemble_analyses(new_a,new_haptests,new_cgs, screened_tests);
    return new_a;
  }
  
  public String get_hap(Set key,String dir, String type){
    String value = "";
    Map m = prev_hnon_map;
    if(type.equals("haplotype")){
      if(dir.equals("inc")){
        m = prev_hinc_map;
      }
      else if(dir.equals("dec")){
        m = prev_hdec_map;
      }
    }
    else{
      if(dir.equals("inc")){
        m = prev_cinc_map;
      }
      else if(dir.equals("dec")){
  	    m = prev_cdec_map;
      }
      else{
  	    m = prev_cnon_map;
      }    	
    }
    if(m.containsKey(key)){
      value = (String) m.get(key);
    }
    else{
      value = "0";
    }
    return value;
  }
  
  //----------------------------------------------------------------------------
  void restore_savedhaps( Map m,String type )
  {
    //The map contains keys with locisets, these will be the same for the map
	//that is storing the new haplotypes.
	Set keyset = m.keySet();
	
	for( Iterator it = m.keySet().iterator(); it.hasNext(); )
	{
	  Set ss = (Set) it.next();
	  String[][] values = (String[][]) m.get(ss);
	  if( type.equals("haplotypes") )
	  {
		if( Integer.parseInt(values[0][0]) != 0 ) { new_hinc_map.put(ss,values[0][0]); }
	    if( Integer.parseInt(values[0][1]) != 0 ) { new_hdec_map.put(ss,values[0][1]); }
	    if( Integer.parseInt(values[0][2]) != 0 ) { new_hnon_map.put(ss,values[0][2]); }
	  }
	  else
	  {
	    if( values[0][0] != "0" ) { new_cinc_map.put(ss,values[0][0]); }
		if( values[0][1] != "0" ) { new_cdec_map.put(ss,values[0][1]); }
		if( values[0][2] != "0" ) { new_cnon_map.put(ss,values[0][2]); }
	  }
	}
  }  

  //----------------------------------------------------------------------------
  private List assemble_analyses ( List a, Set new_haptests, Set new_cgs, List screened_tests )
  {
    for ( Iterator it = new_cgs.iterator();it.hasNext(); )
    {
      String item = (String) it.next();
      a.add(item);
    }
    for ( Iterator it = new_haptests.iterator(); it.hasNext(); )
    {
      int hap = (Integer) it.next();
      String dip_dom = "HDom" + (hap+1);
      
      if ( screened_tests.contains(dip_dom) ) { a.add(dip_dom); }
      String dip_rec = "HRec" + (hap+1);
      if ( screened_tests.contains(dip_rec) ) { a.add(dip_rec); }
      String dip_add = "HAdd" + (hap+1);
      if ( screened_tests.contains(dip_add) ) { a.add(dip_add); }
      String monotype = "MSpecRed" + (hap+1);
      if ( screened_tests.contains(monotype) ) { a.add(monotype); }
    }
    if ( screened_tests.contains("MGlobalOR") )
    {
      a.add("MGlobalOR");
    }
    if ( screened_tests.contains("MGlobalCS") )
    {
      a.add("MGlobalCS");
    }
    if ( screened_tests.contains("HGlobalOR") )
    {
      a.add("HGlobalOR");
    }
    if ( screened_tests.contains("HGlobalCS") )
    {
      a.add("HGlobalCS");
    }
    if ( screened_tests.contains("CGlobalOR") )
    {
      a.add("CGlobalOR");
    }
    if ( screened_tests.contains("CGlobalCS") )
    {
      a.add("CGlobalCS");
    }    
    return a;
  }
  
  //---------------------------------------------------------------------------------------
  private Set generate_uncertains ( int hap, Set new_tests, List missing, List reverse_loci )
  {
    if ( hap != -2 )
    {	
      int hap1 = hap;
      int hap2 = hap;
     
      for ( int i=0; i < missing.size(); i++ )
      {
        int l = (Integer) missing.get(i);
        int indx = reverse_loci.indexOf(l);
        int mask = 1 << indx;
        int mask_result = hap & mask;
        if ( mask_result == 0 ) 
        {
          hap1 = hap;
          hap2 = mask | hap;
        }
        else
        {
          hap1 = hap;
          mask = mask ^ 1;
          hap2 = mask & hap;
        }		
      }
      new_tests.add(hap1);
      new_tests.add(hap2);
    }	
    return new_tests;
  }
  
  //-------------------------------------------------
  private int getHap ( int cnt, Set s, int hap_binary )
  {
    int hap = hap_binary;
    if ( cnt == 0 )
    {
      hap = hap << 1;
    }
    else if ( cnt < s.size() )
    {
      int left_mask = ((int) Math.pow(2.0,(s.size() - cnt))) - 1;
      left_mask = left_mask << cnt;
      left_mask = left_mask & hap_binary;
      left_mask = left_mask << 1;
      int right_mask = ((int) Math.pow(2.0, cnt)) - 1;
      right_mask = right_mask & hap_binary;
      hap = left_mask | right_mask;
    }
    return hap;
  }
  
  //-------------------------------------------------------------------------------------
  private List store_alleles ( List<Integer> missing, Set found, Set s, Map<Integer,Set> alleles, String type, String dir )
  {
    int hap_col = Integer.parseInt(get_hap(s, dir, type));
    if ( hap_col > 0 )
    {
      int [][] haps = create_haps(s.size());
      int count = 0;
      for ( Iterator it = s.iterator(); it.hasNext(); )
      {
        int val = haps[count][hap_col-1] + 1;
        int key = (Integer) it.next();
        if ( alleles.containsKey(key) )
        {
          Set<Integer> as = (Set) alleles.get(key);
          as.add(val);
        }
        else
        {
          Set<Integer> as = new TreeSet<Integer>();
          as.add(val);
          alleles.put(key, as);
        }
        Set ss = (Set) alleles.get(key);
        if ( missing.contains(key) )
        {
          if ( ss.size() == 1 ) 
          {
            missing.remove(missing.indexOf(key));	
          }	    
        }
        else
        {
          if ( ss.size() > 1 )
          {
            missing.add(key);	  
          }
        }
        count++;
      }
    }
    else
    {
      for ( Iterator it = s.iterator(); it.hasNext(); )
      {
        //Need to check missing before adding to it.
        int key = (Integer) it.next();
        if ( !missing.contains(key) && (!alleles.containsKey(key)) )
        {
          missing.add(key); 
        }
      }
    }
    return missing;	
  }
  
  //-------------------------------------------------------------------------------------
  private Set store_cg ( Set<String> new_cgs, Set<Integer> s, int[] reverse_loci, int counter, String dir, String type )
  {
    String cg_model = get_hap(s, dir, type);
    
    if ( !cg_model.equals("0") )
    {
      int missing_loci = reverse_loci[counter] + 1;
      s.add(missing_loci);
      SortedSet complete = new TreeSet();
      complete.addAll(s);
      String[] added_strings = {"CRec","CDom"};
      String[] mdls = cg_model.split("/");
	  
      for ( int i=0; i < added_strings.length; i++ )
      {
        int cnter = 0;
        String new_mdl = "";
        for ( Iterator it = complete.iterator(); it.hasNext(); )
        {
          int loci = (Integer) it.next();
          if ( loci == missing_loci )
          {
            new_mdl = new_mdl + added_strings[i];
            cnter = cnter - 1;
            if ( new_mdl.split("/").length != complete.size() )
            {
              new_mdl = new_mdl + "/";
            }
          }
          else
          {
            new_mdl = new_mdl + mdls[cnter];
            String[] nm = new_mdl.split("/");
            if ( nm.length != complete.size() )
            {
              new_mdl = new_mdl + "/";
            }
          }
          cnter = cnter + 1;
        }
        new_cgs.add(new_mdl);
      }
    }
    return new_cgs;  
  }
  
  //-------------------------------------------------------------------------------------  
  private int[][] create_haps ( int step )
  {
    double dStep = Double.valueOf(step).doubleValue();
    double dNumcols = Math.pow(2.0, dStep);
    int col_dim = (int) dNumcols;
    int row_dim = step;
    int[][] m = new int[row_dim][col_dim];
    // Fill the 2d array with the proper pattern of 1's and 2's.
    for ( int i = 0; i < row_dim; i++ )
    {
      int fill = 0;
      int current_fill = 0;
      int last_fill = 1;
      int switchCount = 0;
      double cc = i + 1.0;
      double denom = Math.pow(2.0, cc);
      int divid = (int) (Math.pow(2.0, dStep) / denom);
      for ( int j = 0; j < col_dim; j++ )
      {
        if ( switchCount == divid )
        {
          fill = last_fill;
          last_fill = current_fill;
          current_fill = fill;
          switchCount = 0;
        }
        m[i][j] = fill;
        switchCount++;
      }
    }
    return m;
  }
  
  public void refresh_hapstorage()
  {
    prev_hinc_map = new_hinc_map;
	prev_hdec_map = new_hdec_map;
	prev_hnon_map = new_hnon_map;
	prev_cinc_map = new_cinc_map;
	prev_cdec_map = new_cdec_map;
	prev_cnon_map = new_cnon_map;  
	new_hinc_map = new HashMap();
	new_hdec_map = new HashMap();
	new_hnon_map = new HashMap();
	new_cinc_map = new HashMap();
	new_cdec_map = new HashMap();
	new_cnon_map = new HashMap();  
  }
  
}


  


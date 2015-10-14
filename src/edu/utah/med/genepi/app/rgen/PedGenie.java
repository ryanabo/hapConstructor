//******************************************************************************
// PedGenie.java
//******************************************************************************
package edu.utah.med.genepi.app.rgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.hc.Evalsigs;
import edu.utah.med.genepi.hc.analysisResults;
import edu.utah.med.genepi.hc.analysisSet;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.hc.hapConstructor;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.sim.GSimulator;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
/** The application class per-se, in that it embodies the core functionality as
 *  decoupled from IO.  Created by Main within the present package for running
 *  under a console, but potentially runnable by other means.
 */
public class PedGenie {

  private final GSimulator.Top  tSim;
  private final GSimulator.Drop dSim;
  private CCAnalysis[]          theAnalyses;
  private final int             nCycles;
  private final int		        sigCycles;
  private int                   progMeter;
  private int                   numGDef;
  private String                app_id;
  private hapConstructor		hc;
  public  Map<Integer,List>     hapmodels = new HashMap<Integer,List>();
  private IOManager	       	    io_mgr;
  private compressGtype	        cGtype;
  private compressGtype[]       study_Gtypes;
  private int                   screening = -1;
  private boolean               screenpass = false;
  private boolean               screentesting = false;
  private Specification         spec;

  //----------------------------------------------------------------------------
  //public PedGenie(Specification spec, PedData pd) throws GEException
  public PedGenie ( IOManager im, String application_id ) throws GEException
  {
    io_mgr = im;
    spec = io_mgr.getSpecification();
    Study[] study = io_mgr.getStudy();
    tSim = spec.getTopSim();
    dSim = spec.getDropSim();
    theAnalyses = spec.getCCAnalyses();
    nCycles = spec.getNumberOfSimulations();
    
    if( nCycles < 1000 )
    {
      sigCycles = nCycles;
    }
    else
    {
      sigCycles = 1000;
    }
    
    numGDef = spec.getGDef().getLocusCount();
    app_id = application_id;
    
    initializeSimulator(tSim, spec, study);
    initializeSimulator(dSim, spec, study);
    
    if( app_id.equals("hapConstructor") )
    {
      int[] loci = new int[numGDef];
      
      for( int j =0; j < numGDef; j ++ )
      {
        loci[j] = j;
      }
      
      Study[] istudy = spec.getStudy();
      study_Gtypes = new compressGtype[istudy.length];
      initiate_cGtypes(istudy,loci);
      
      String[] models = spec.gethapC_models();
      List<String>a = new ArrayList<String>();
      
      for( int j = 0; j < models.length; j++ )
      {
        a.add(models[j]);
      }
      setupModels(a, spec);
    }
    
    if ( theAnalyses.length > 0 )
    {
      System.out.println("Getting subjects...");
      for ( int i = 0; i < theAnalyses.length; ++i )
      {
        theAnalyses[i].setStudySubjects(study);
      }
    }
  }
  
  //----------------------------------------------------------------------------
  private void initiate_cGtypes (Study[] istudy, int[] loci)
  {
	  
	for ( int i = 0; i < istudy.length; i++ )
	{
      PedData peddata = istudy[i].getPedData();
      Indiv[] caseIndiv = peddata.getIndividuals( PedQuery.IS_CASE, true, loci );
      Indiv[] controlIndiv = peddata.getIndividuals( PedQuery.IS_CONTROL, true, loci );
      cGtype = new compressGtype(numGDef,nCycles,caseIndiv.length,controlIndiv.length, i);
	  double case_cells = Math.ceil(caseIndiv.length / 16.0); 
	  int case_cels = (int)Math.ceil(case_cells);
	  double control_cells = Math.ceil(controlIndiv.length / 16.0); 
	  int control_cels = (int)Math.ceil(control_cells);
      cGtype.initiate_obsStorage(case_cels,"case", caseIndiv);
      cGtype.initiate_obsStorage(control_cels, "control",controlIndiv);
      //cGtype.dump_haps(i);
      //if ( cGtype != null )
      //System.out.println("initiate cGtype is not null ");
      study_Gtypes[i] = cGtype;
	}
	
  }
  
  //----------------------------------------------------------------------------
  private void setupModels( List amodels, Specification spec )
  {
    List<String> screen1 = new ArrayList<String>();
    List<String> screen2 = new ArrayList<String>();
     if( ( spec.gethapC_screen() == 1 && amodels.contains("HAdd") ) ) // || spec.gethapC_screen() == 1 )
    {
      try
      {
        boolean or = false;
        or = prescreen_check( amodels, spec);
        screentesting = true;
        if( amodels.contains("HAdd") )
        {
          screen1.add("HAdd");
          if ( or )
          {
            screen2.add("HAdd");
          }
        }
        if( amodels.contains("M") )
        {
          screen2.add("MSpecRed");
        }

        if( amodels.contains("HDom") )
        {
          screen2.add("HDom");
        }
        if( amodels.contains("HRec") )
        {
          screen2.add("HRec");
        }       
      }
      catch(RuntimeException x)
      {
        System.out.println(x);
        System.exit(0);
      }
    }
    else
    {
      if( amodels.contains("HAdd") )
      {
        screen1.add("HAdd");
      }
      if( amodels.contains("M") )
      {
        screen1.add("MSpecRed");
      }
      if( amodels.contains("HDom") )
      {
        screen1.add("HDom");
      }
      if( amodels.contains("HRec") )
      {
        screen1.add("HRec");
      }       
    }
    if( amodels.contains("MG") )
    {
      screen1.add("MGlobalOR");
      screen1.add("MGlobalCS");
    }
    if( amodels.contains("HG") )
    {
      screen1.add("HGlobalOR");
      screen1.add("HGlobalCS");
    }
    if( amodels.contains("CGG") )
    {
      screen1.add("CGlobalOR");
      screen1.add("CGlobalCS");
    }
    if( amodels.contains("CG") )
    {
      screen1.add("CDom");
    }
   
    if( screentesting )
    {
      hapmodels.put(0,screen1);
      hapmodels.put(1,screen2);
    }
    else
    {
      hapmodels.put(0,screen1);   
    }
       
  }       
  
  //----------------------------------------------------------------------------
  private boolean prescreen_check( List amodels, Specification spec ) throws RuntimeException
  {
    CCStat[] cstats = spec.getCCStats();
    CCStat[] mstats = spec.getMetaStats();
    boolean flg = false;
    boolean or = false;
    for ( int i=0; i < cstats.length; i++ )
    {
      String statname = cstats[i].getName();
      if ( statname.indexOf("Trend") != -1 )
      {
        flg = true;
      }
      if ( statname.indexOf("Odd") != -1 )
      {
        or = true;
      }
    }
    for ( int i=0; i < mstats.length; i++ )
    {
      String statname = mstats[i].getName();
      if ( statname.indexOf("Trend") != -1 )
      {
        flg = true;
      }
      if ( statname.indexOf("Odd") != -1 )
      {
        or = true;
      }      
    }
    if( !amodels.contains("HAdd") || !flg )
    {
      throw new RuntimeException("Need to include HAdd model and Chi-square trend test in .rgen specifications if screening is set to true.");
    }
    return or;
  }

  //----------------------------------------------------------------------------
//  public void run(IOManager io_mgr) throws GEException, IOException
//  {
//    start(io_mgr);
//    System.out.println("Executing " + nCycles + " simulation cycles...");
//    
//    for (int icycle = 0; icycle < nCycles; ++icycle)
//      update(icycle, io_mgr);
//
//    if (!io_mgr.dumpingEnabled())
//      System.out.println();
//
//    io_mgr.writeReport();
//    
//    //Ryan 06-10-07
//    checkBuild(io_mgr,0);
//  }
  
  //Ryan 09-15-07 Overloaded to be able to evaluate hapBuilder results
  //Note: idx is the simulation data set that is being used as the "observation data set."
  //So there are nCycles*nCycles number of calls to run().
  //----------------------------------------------------------------------------
  public int[] run( int[] params ) throws GEException, IOException
  {
	int sigrun = params[0];
	int step = params[1];
      //Ryan 05-07-08
	int[] nparams = new int[2];
	int pmeter = 0;
      //Specification spec = io_mgr.getSpecification();
      //Ryan added if statement for loading stored null data.
	if(app_id.equals("hapConstructor") && 
           spec.gethapC_sigtesting_only() && 
           sigrun < 0){
	  if(spec.gethapC_loadnulls())
	  {
		System.out.println("Loading nulls...");
	    for( int l = 0; l < study_Gtypes.length; l ++ )
	    {    	
          study_Gtypes[l].loadnulls(l); 
	    }
	  }
	  else
	  {
        for ( int icycle = 0; icycle < nCycles; ++icycle )
        {
          if (icycle / (double) nCycles >= pmeter * 0.1 )
          {
            System.out.print(pmeter % 2 == 0 ? "/" : "\\");
            ++pmeter;
          }
          //System.out.println("Generating null sim " + icycle);
          create_nullsims(sigrun, step, icycle);
        }
	  }
	}
	else{
	  if(app_id.equals("hapConstructor") && step > 1 )
	  {
	    create_analyses( sigrun, step );
	  }
	  else
	  {

        start(sigrun,step);    
        System.out.println("Executing " + nCycles + " simulation cycles..."); 
    
        for ( int icycle = 0; icycle < nCycles; ++icycle )
        {
          boolean flg = false;
          if( sigrun > -1 && sigrun != icycle )
          {
            flg = true;
          }
          else if( sigrun == -1 )
          {
            flg = true;
          }
          if( flg )
          {
            update(sigrun, step, icycle);
          }
        }
	
        if (!io_mgr.dumpingEnabled())
          System.out.println();

        if(sigrun == -1)
        {
          io_mgr.writeReport();
        }
	  }
	}
    nparams = checkBuild(sigrun, step);	 
    return nparams;
  }

  /*
  public int[] run( int[] params ) throws GEException, IOException
  {
	int sigrun = params[0];
	int step = params[1];
	int[] nparams = {};
    start(sigrun);    
    System.out.println("Executing " + nCycles + " simulation cycles...");
    
//    int len = nCycles;
//    int half = len / 2;
//    int quarter = half / 2;
//    int three_quarter = half + quarter;
//    RunRunner strun = new RunRunner(sigrun,0,step,len,this,theAnalyses);
//    RunRunner strun2 = new RunRunner(sigrun,quarter,step,len,this,theAnalyses);
//    RunRunner strun3 = new RunRunner(sigrun,half,step,len,this,theAnalyses);
//    RunRunner strun4 = new RunRunner(sigrun,three_quarter,step,len,this,theAnalyses);
//
//    try{
//      strun.t.join();
//      strun2.t.join();
//      strun3.t.join();
//      strun4.t.join();
//    } catch(InterruptedException e) {
//      System.out.println("Interrupted");
//    } 
    
    for ( int icycle = 0; icycle < nCycles; ++icycle )
    {
      boolean flg = false;
      if( sigrun > -1 && sigrun != icycle )
      {
        flg = true;
      }
      else if( sigrun == -1 )
      {
        flg = true;
      }
      if( flg )
      {
        update(sigrun, step, icycle);
      }
    }

    if (!io_mgr.dumpingEnabled())
      System.out.println();
    
    if(sigrun == -1){
      io_mgr.writeReport();
    }
    
    nparams = checkBuild(sigrun, step);
    return nparams;
  }
  */
  
  //----------------------------------------------------------------------------
  // This is a new function that I added. It reroutes hapConstructor to dump the analysis results as they are being processed. This was to deal with the memory limitations. 

  //Ryan added 05-07-08
  private void create_analyses( int sigrun, int step ) 
          throws GEException, IOException
  {
    progMeter = 0;	  
	int new_analysis = 1;
	List a = hc.get_amdls();
	Set loci = hc.getNewLoci();
    List<CCAnalysis> analyses = new ArrayList<CCAnalysis>();
    Specification spec = io_mgr.getSpecification();
    Map mParams = spec.getAllGlobalParameters();
    GDef gd = spec.getGDef();
    //Iterate through loci, which is a Set of Sets (i.e. [[1,2],[1,3],[2,3]]). 
    //Each nested Set contains a set of loci to be considered for an analysis.
    int counter = 0;
    for ( Iterator it=loci.iterator(); it.hasNext(); )
	{
      List a_copy = new ArrayList();
      a_copy.addAll(a);    	
      Set lociSet = (Set)it.next();
      analysisResults aResults = hc.getaresults();
      int[] iloci = aResults.getLocusManager().getLocInd(lociSet);
      String[] markers = aResults.getLocusManager().getMarkers(lociSet);
      //For each new set of loci, create the appropriate analyses for them.
      //Modify the a list for each loci set...preprocess counts for HDom, HRec, and HAdd, MSpec
      //If counts fail don't do those analyses.
      //List new_a = new ArrayList();

      if ( screentesting && hc.get_screentest() == 1 )
      {
    	a_copy = hc.onlyScreenedHaps(a_copy,lociSet);
      }
      if (study_Gtypes.length > 1) 
      {
        for ( int i = 0; i < study_Gtypes.length; i ++ ) 
        {
          study_Gtypes[i].prescreen_analyses(a_copy, lociSet, iloci, sigrun);	
        }
      }
      else
      {
        a_copy = study_Gtypes[0].prescreen_analyses(a_copy, lociSet, iloci, sigrun);
      }
      //You can only do the specific haplotype testing when moving forward since it is using
      //information from previous runs to choose the specific haplotypes from each loci set.
      counter = counter + 1;
      if ( step > 2 && hc.step_dir.equals("forward") && hc.get_screentest() < 1 )
      {
        a_copy = hc.select_testing_haps(iloci,lociSet,a_copy);
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
        CCAnalysis cca = new analysisSet(app_id, i, spec, markers, iloci, lociSet,(String) a_copy.get(i),step, sigrun, hc.get_screentest()).getAnalysis();
        cca.setStudySubjects(spec.getStudy());
        cca.startObsRuns(sigrun,-1,study_Gtypes,step);
        for(int j = 0; j < nCycles; j++)
        {
          int simcycle = j;
          int index = 0;
          if ( app_id.equals("hapConstructor") )
          {
            index = simcycle;
          }

          if ( step == 1 && sigrun == -1) 
          {
            tSim.simulateFounderGenotypes(0);
            dSim.simulateDescendantGenotypes(index,study_Gtypes,step);
          }
          else if ( sigrun == -1 && step < 2 )
          {
            tSim.simulateFounderGenotypes(index);
            dSim.simulateDescendantGenotypes(index);
          }
          io_mgr.dumpData(simcycle + 1,index);
//          if (simcycle / (double) nCycles >= progMeter * 0.1 )
//          {
//            if ( !io_mgr.dumpingEnabled() )
//              System.out.print(progMeter % 2 == 0 ? "/" : "\\");
//            ++progMeter;
//          }
          cca.startsimRuns(sigrun,simcycle,study_Gtypes, step);
        }
        CCAnalysis[] ccas = new CCAnalysis[] {cca};
        theAnalyses = ccas;
        hc.updateResults(sigrun, nCycles, theAnalyses, mParams, gd, step, io_mgr.getinDate(),this, study_Gtypes, new_analysis);
        if (counter / (double) loci.size() >= progMeter * 0.1 )
        {
          if ( !io_mgr.dumpingEnabled() )
            System.out.print(progMeter % 2 == 0 ? "/" : "\\");
          ++progMeter;
        }
        new_analysis = 0;
      }	  
    }
    if( screentesting && hc.get_screentest() == 0 )
    {
      hc.screentest = 1;	
    }
    else if( screentesting && hc.get_screentest() == 1 )
    {
      hc.screentest = 0;	
    }
  }

  //----------------------------------------------------------------------------
  // The changes to checkBuild() were from the changes to store the null sims or load the null sims to only perform significance runs. There also a number of new parameters passed some of the functions. These are updated in the hc package.
 //Ryan 06-14-07
  /* This function is called after an analysis is performed. If the application id is hapBuilder proceed to determine significant loci with a set threshold (default is 0.1 for all levels). 
  */
  private int[] checkBuild( int sigrun, int step ) throws GEException, IOException
  {
	//Check if application is for building haplotypes.
	int[] params = new int[2];
    if ( app_id.equals("hapConstructor") )
    {
	  //step++;
      Specification spec = io_mgr.getSpecification();
      //Global parameters are needed to build new loci sets from significant analyses.
      Map mParams = spec.getAllGlobalParameters();
      GDef gd = spec.getGDef();
      //The first time around create a new hapBuilder if building observation data.
      //If it is evaluating rather than building, then create a new instance of the 
      //hapBuilder.
      if ( step == 1)
      {
        hc = new hapConstructor(app_id, spec, theAnalyses, io_mgr.getoutpathStem(), screentesting);
        if(!spec.gethapC_sigtesting_only() || sigrun != -1)
        {
          hc.updateResults(sigrun, nCycles, theAnalyses, mParams,gd, step, io_mgr.getinDate(),this, study_Gtypes,1);
        }
      }
      else
      {
        hc.refresh_hapstorage();        
      }
      //Stores results from previous analyses in analysisResults object.
      
      if(screening != 0)
      {
        step++;
        hc.updateStep(step);
      }
      
      //Create a boolean to track whether a new analysis needs to be performed with a new set of loci.
      boolean flg = false;
      //Check if there are new loci sets to test from either back sets or new 
      //sets created from significant loci.
      if ( screentesting && screening != 0 && hc.beginBuild() )
      {
    	screenpass = false;
    	screening = hc.screentest;
        //Controller setups up the new analyses and updates them in the specification 
        //object.
        hc.controller(io_mgr, this, hc.getNewLoci(), study_Gtypes, sigrun, "forward");
        flg = true;
        //params[0] = -2;
        //params[1] = 0;
        //flg = false;
      }
      else if ( screening == 0 && hc.checkscreentest() )
      {
        screenpass = true;
        screening = hc.screentest;
        hc.controller(io_mgr, this, hc.getScreenedSets(), study_Gtypes, sigrun, "forward");
        flg = true;
      }
      else if( !screentesting && (!spec.gethapC_sigtesting_only() || sigrun != -1) && hc.beginBuild() )
      {
        hc.controller(io_mgr, this, hc.getNewLoci(), study_Gtypes, sigrun, "forward");
        flg = true;
      }
      //Check if there are backsets to test.
      else if ( hc.backSetExists() && spec.gethapC_backsets() )
      {
        //Reset the step to the backset level.
        step = hc.backSetLevel();
        System.out.println("Evaluating backsets");
        Set bsets = hc.getBackSet(step);
        bsets = hc.saveSets(bsets);
        if ( bsets.size() == 0 )
        {
          //Set params
          //This situation shouldn't happen, the hb.backSetExists should return false
          //in this section.
          //The problem is the backset step member variable is screwed up.
          flg = false;
        }
        else
        {
          hc.controller(io_mgr, this, bsets, study_Gtypes, sigrun, "backward");
          flg = true;
        }
      }
      //Check if the building for observation data is done and now 
      //time to start evaluation.
      else if ( sigrun < 0 && spec.gethapC_sigtesting() )
      {
    	if(spec.gethapC_sigtesting_only())
    	{
          sigrun = spec.gethapC_sigtesting_start();
    	}
    	else
    	{
    	  sigrun = 0;
    	}
        System.out.println("Starting evaluation");
        //Change process status from build to eval
        hc.updateProcess();
        //Iterate through each simulated data set and use it
        //as if it were the observation data.
        //Thread this...
        spec.updateAnalyses(hc.getOriginalAnalysis());
        theAnalyses = spec.getCCAnalyses();
        //The reason you can't multithread is because every object in ca is the same
        //when you pass it to the two threads. So when one thread manipulates the object
        //the other object is affected.If you could truly create a new copy of ca and pass it
        //to the second thread then it would work.
        step = 1;
        screening = -1;
        System.out.println("Evaluation no. " + sigrun);
        params[0] = sigrun;
        params[1] = 1;
      }
	  //Done with analysis and not sig testing
      else if ( sigrun == -1 ) 
      {
        params[0] = -2;
        params[1] = 0;
      }
      //Sigtesting and completed all analyses for sigtest, go to next sim set.
      //The sigCycles are limited to 1000 because for the FDR this should
      //be plenty of simulated sets.
      else if ( sigrun < sigCycles - 1 )
      {
        spec.updateAnalyses(hc.getOriginalAnalysis());
        theAnalyses = spec.getCCAnalyses();
        params[0] = sigrun+1;
        screening = -1;
        System.out.println("Continuing significance runs");
        params[1] = 1;
      }
      else if ( sigrun == sigrun -1 )
      {
        Evalsigs es = new Evalsigs();
    	es.readfile("all_obs.final");
    	es.readfile("all_sims.final");
    	es.all_efdr();
      }
      //Done with everything, stop
      else
      {
        params[0] = -2;
        params[1] = 0;
      }
      //If flg is true then continue with analysis process.
      if ( flg )
      {
        theAnalyses = spec.getCCAnalyses();
        if ( theAnalyses.length > 0 )
        {
          System.out.println("Getting subjects...");
          for ( int i = 0; i < theAnalyses.length; ++i )
          {
            theAnalyses[i].setStudySubjects(spec.getStudy());
          }
        }
        params[0] = sigrun;
        //System.out.println("Starting " + sigrun + " with step: " + step);
        params[1] = step;
      }
    }
    //Not hapConstructing
    else
    {
      params[0] = -2;
      params[1] = 0;
    }
    return params;
  }


  //Ryan 06-18-07 Overloaded the start function for evaluation of 
  //hapBuilder results
  //----------------------------------------------------------------------------
  public void start(int sigrun, int step) throws IOException, GEException
  {
    io_mgr.dumpData(0);
    progMeter = 0;

    if (theAnalyses.length > 0 )
      System.out.println("Starting analyses...");

    //Ryan 06-22-07 multi-thread the computation of observed data.
//    int len = theAnalyses.length;
//    int half = len / 2;
//    StatRunner strun = new StatRunner(theAnalyses,0,-1,"stat",sigrun, cGtype);
//    StatRunner strun2 = new StatRunner(theAnalyses,half,-1,"stat",sigrun, cGtype);
//    
//    try{
//      strun.t.join();
//      strun2.t.join();
//    } catch(InterruptedException e) {
//      System.out.println("Interrupted");
//    }
//    System.out.println("Main is running"); 
    for ( int i = 0; i < theAnalyses.length; ++i )
    {
      if( app_id.equals("hapConstructor") )
      {
        theAnalyses[i].startObsRuns(sigrun,-1,study_Gtypes,step);
      }
      else
      {
        theAnalyses[i].startStatRuns(sigrun);
      }
    }
  }
  
  //----------------------------------------------------------------------------
  public void create_nullsims(int sigrun, int step, int simcycle) 
         throws IOException, GEException
  {
    int index = 0;
    if ( app_id.equals("hapConstructor") )
    {
      index = simcycle;
    }
    if ( app_id.equals("hapConstructor") && step == 1 && sigrun == -1) 
    {
      tSim.simulateFounderGenotypes(0);
      dSim.simulateDescendantGenotypes(index,study_Gtypes,step);
    }
    else if ( sigrun == -1 && step < 2 )
    {
      tSim.simulateFounderGenotypes(index);
      dSim.simulateDescendantGenotypes(index);
    }
    for(int j = 0; j < study_Gtypes.length; j++)
    {
      study_Gtypes[j].file_sims(index,j);
    }
  }

  //----------------------------------------------------------------------------
  public void update(int sigrun, int step, int simcycle) 
         throws IOException, GEException
  {
    int index = 0;
    if ( app_id.equals("hapConstructor") )
    {
      index = simcycle;
    }

    if ( app_id.equals("hapConstructor") && step == 1 && sigrun == -1) 
    {
      tSim.simulateFounderGenotypes(0);
      dSim.simulateDescendantGenotypes(index,study_Gtypes,step);
    }
    else if ( sigrun == -1 && step < 2 )
    {
      tSim.simulateFounderGenotypes(index);
      dSim.simulateDescendantGenotypes(index);
    }
    
    io_mgr.dumpData(simcycle + 1,index);
    if (simcycle / (double) nCycles >= progMeter * 0.1 )
    {
      if ( !io_mgr.dumpingEnabled() )
        System.out.print(progMeter % 2 == 0 ? "/" : "\\");
      ++progMeter;
    }
    
    for ( int i = 0; i < theAnalyses.length; ++i )
    { 
      if ( app_id.equals("hapConstructor") )
      {
        theAnalyses[i].startsimRuns(sigrun,simcycle,study_Gtypes, step);
      }
      else
      {
        theAnalyses[i].updateStatRuns(index);    		
      }
    }
  }
  
  //----------------------------------------------------------------------------
  //Ryan added 10/14/07  
  //Jathine removed 2-25-08
  /*
  public void callsims ( int step, int sigrun, int index ) throws GEException
  {
    if ( app_id.equals("hapConstructor") && step == 1 && sigrun == -1 )
    {
      tSim.simulateFounderGenotypes(0);
      dSim.simulateDescendantGenotypes(index,study_Gtypes,step);
    }
    else if ( sigrun == -1 && step < 2 )
    {
      tSim.simulateFounderGenotypes(index);
      dSim.simulateDescendantGenotypes(index);
    }
}
  **/
  //Ryan 06-18-07 Overloaded for evaluating hapBuild
  //results
  //----------------------------------------------------------------------------
//  public void update(int icyc, IOManager iom, int idx) throws GEException, IOException
//  {
//    int index = 0;
//    if ( app_id.equals("hapBuilder") )
//      index = icyc; 
//
////    tSim.simulateFounderGenotypes(index,cGtype,step);
////    dSim.simulateDescendantGenotypes(index,cGtype,step);
//
//    iom.dumpData(icyc + 1);
//    System.out.println(icyc);
//    if (icyc / (double) nCycles >= progMeter * 0.1)
//    {
//      if (!iom.dumpingEnabled())
//        System.out.print(progMeter % 2 == 0 ? "/" : "\\");
//      ++progMeter;
//    }
//    
//    //Ryan 06-22-07 Let's try to multithread...
//    int len = theAnalyses.length;
//    int half = len / 2;
//    StatRunner ustrun = new StatRunner(theAnalyses,0,index,"update",process, cGtype);
//    StatRunner ustrun2 = new StatRunner(theAnalyses,half,index,"update",process, cGtype);
//    
//    try{
//      //System.out.println("Waiting for threads to finish.");
//      ustrun.t.join();
//      ustrun2.t.join();
//    } catch(InterruptedException e) {
//      System.out.println("Interrupted");
//    }
//    
//    //Ryan 06-18-07 added if statement to exclude the index that is 
//    //currently being used for obs data.
////    for (int i = 0; i < theAnalyses.length; ++i){
////      if(process == "eval"){
////        if(i != idx){
////          theAnalyses[i].updateStatRuns(index);
////        }
////      }
////      else{
////    	if(step == 3){
////          theAnalyses[i].updateStatRuns(index);
////    	}
////      } 
////    }
////    for (int i = half; i < (half+half); ++i){
////      if(process == "eval"){
////        if(i != idx){
////          theAnalyses[i].updateStatRuns(index);
////        }
////      }
////      else{
////        theAnalyses[i].updateStatRuns(index);
////      } 
////    }
//  }
  
  //----------------------------------------------------------------------------
  //private void initializeSimulator(GSimulator gs, Specification sp, PedData pd)
  private void initializeSimulator( GSimulator gs,
                                    Specification spec,
                                    Study[] study )
          throws GEException
  {
    //gs.setUserParameters(sp.getAllGlobalParameters());
    gs.setUserParameters(spec, study);
    //gs.setPedData(pd);
    gs.setPedData();
    gs.setGDef(spec.getGDef());
    //  for ( int i = 0; i < theAnalyses.length; i++ )
    //  {
        try { gs.preProcessor(); }
        catch ( GEException e )
          { throw new GEException ( e.getMessage() ); }
     // }
  }
  
  public CCAnalysis[] getAnalyses(){
    return theAnalyses;
  }
  //----------------------------------------------------------------------------
  /* GeneCounterTopSim implement GSimulator.Top
  public void geneCounter( Study[] study, LinkageParameterData lpd )
  {
    LinkageLocus[] loc = lpd.getLoci();
    FreqDataSet[][] freq = new FreqDataSet[numGDef][];
    // make sure genecounter and GDef are pointed at the same locus
    for ( int i = 0; i < numGDef; i++ )
    {
      GDef.Locus specLocus = gdef.getLocus();

      for ( int j = 0; j < loc.length; j++ )
      {
        if ( specLocus.getID() == j + 1 )
        {
          double[] freqvalues = loc[j].alleleFrequencies();
          freq[i] = new FerqDataSet[freqvalues.length];
          for ( int k = 0; k < freqvalues.length; k++ )
            freq[i][k] = new FreqDataSet( freqvalues[k],
                                          k+1,
                                          "GeneCounter" );
        }
      }
    }
  }
**/
}


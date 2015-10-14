package edu.utah.med.genepi.interaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.utah.med.genepi.app.rgen.IOManager;
import edu.utah.med.genepi.app.rgen.Reporter;
import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeMatcher;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.sim.GSimulator;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.stat.CCStatRun.Report;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Ut;

public class InteractionOddsRatios {

  private final int nmetas;
  private int[][] markerCombos;
  private int[][] modelCombos;
  private int nullCycles;
  private AlleleFormat af;
  private int[] loci_indices;
  private Study[] study;
  private final GSimulator.Top  tSim;
  private final GSimulator.Drop dSim;
  private IORAnalysis[][] analyses;
  
  public InteractionOddsRatios( Study[] std, InteractionParams intx_params )
  {
    nmetas = intx_params.get_nmetas();
    markerCombos = intx_params.get_markerCombos();
    modelCombos = intx_params.get_modelCombos();
    nullCycles = intx_params.get_nullcycles();
    af = (AlleleFormat) intx_params.get_af();
    Integer[] iloci = intx_params.get_loci_indices();
    loci_indices = new int[iloci.length];
    for ( int i=0; i < iloci.length; i++ ) loci_indices[i] = iloci[i];
    study = std;
    tSim = intx_params.getTsim();
    dSim = intx_params.getDsim();
  }
  
  private static class ColumnImp implements CCAnalysis.Table.Column 
  {
    private final GtypeMatcher[] gtMatchers;
    private final int            theWt;
    //private final String 	 theType;
    private ColumnImp( List<GtypeMatcher> gt_matchers,  int weight )
    {
      gtMatchers = gt_matchers.toArray(new GtypeMatcher[0]);
      theWt = weight;
    }
    // pass each allele separately for type = Allele
    public int subsumesAtype(Gtype gt, boolean first) 
    {
      for ( int j = 0; j < gtMatchers.length; j++ )
      {
        if ( gtMatchers[j].matchesGtype(gt, first) )
          return 1;
      }
      return 0;
    }

    public int subsumesGtype(Gtype gt)
    {
      for (int j = 0; j < gtMatchers.length; ++j)
      {
        if ( gtMatchers[j].matchesGtype(gt) )
          return 1;
      }
      return 0;
    }

    public int getWeight() { return theWt; }
    public GtypeMatcher[] getGtypeMatcher() { return gtMatchers; }
  }
  
  private class IORAnalysis
  {
    CCAnalysis[] subAnalyses;
    // Rows contain interaction odds ratio term observed stat, hitcount, simcount
    double [][] results;
    public IORAnalysis( preAnalysis pA, int[] markerCombo )
    {
      Map<String,List<List<String>>>[] pDefs = pA.getPDefs();
      String type = pA.getType();
      int ncols = pA.getNcols();
      
      subAnalyses = new CCAnalysis[pDefs.length];
      
      results = new double[pDefs.length][3];
      for ( int i =0 ; i < pDefs.length; i++ )
      {
        try 
        {
          subAnalyses[i] = buildDefs(pDefs[i],type,ncols,pA.getModel(),markerCombo);
        } catch (GEException e) 
        {
          e.printStackTrace();
        }          
      }
    }
    
    public CCAnalysis[] getCCAnalyses(){ return subAnalyses; }
    
    public void analyze( int iter )
    {
      for ( int i=0; i < subAnalyses.length; i++ )
      {
        if ( iter == -1 ) subAnalyses[i].startStatRuns();
        else subAnalyses[i].updateStatRuns(0);
        //processInteraction(subAnalyses[i],i,iter);
      }
    }
    
    public void report()
    {
      String model = subAnalyses[0].getModel();
      System.out.println("Interaction odds ratios for model: " + model);
      String[] labels = new String[results.length];
      if ( results.length == 4 )
      {
        labels[0] = "OR11";
        labels[1] = "OR12";
        labels[2] = "OR21";
        labels[3] = "OR22";
      }
      else if ( results.length == 2 )
      {
        if ( model.equals("Additive x Dominant") || model.equals("Additive x Recessive") )
        {
          labels[0] = "OR11";
          labels[1] = "OR21";
        }
        else if ( model.equals("Dominant x Additive") || model.equals("Recessive x Additive") )
        {
          labels[0] = "OR11";
          labels[1] = "OR12";
        }
      }
      else labels[0] = "OR11";
      for ( int i=0; i < results.length; i++ )
      {
    	double pval = (double) (double)results[i][1] / (double)results[i][2];
        System.out.println(labels[i] + " obs val: " + results[i][0] + " pval: " + pval);
      }
    }
    
    //---------------------------------------------------------------------------
    public double processInteraction( CCAnalysis[] analyses, int row, int iter )
    {
  	double[] intermediateOR = new double[3];
  	for ( int i=0; i < analyses.length; i++ )
  	{
  	  for ( int j=0; j < study.length; j++)
  	  {
          Report[] reports = analyses[i].getStatReports(0,0,j);
          for ( int k=0; k < reports.length; k++ )
          {
            Report report = reports[k];
            String obsVal = report.getObsValue();
            double obs_value = 0.0;
            if ( !obsVal.contains("-") ) obs_value = Double.parseDouble(report.getObsValue());
            intermediateOR[i] = obs_value;
            //System.out.println(obs_value);
          }
  	  }
  	}
  	double iOR = intermediateOR[2] / (intermediateOR[0]*intermediateOR[1]);
  	if ( iter == -1 ) results[row][0]=  iOR;
  	else
  	{
      double obsResult = results[row][0];
      double simResult = iOR;
      //System.out.println(obsResult + " " + simResult);
      if ( obsResult <= 1.0 && simResult <= obsResult )
      {
        //System.out.println("Increment");
        results[row][1]++;
      }
      else if ( obsResult >= 1.0 && simResult >= obsResult )
      {
        //System.out.println("Increment: " + iOR);        	
        results[row][1]++;
      }
      results[row][2]++;  	  
  	}
  	//System.out.println("Interaction odds ratios: "+ iOR);
  	return iOR;
    }
  }
  
  private class modelDefs
  {
	private List<String[]> colGt = new ArrayList<String[]>();
	private List<int[]> riskFactors = new ArrayList<int[]>();
    public modelDefs()
    {
      colGt.add(new String[] {"1/1","(2/1)|(1/2)","2/2"});
      colGt.add(new String[] {"1/1","(2/.)|(./2)"});
      colGt.add(new String[] {"(1/.)|(./1)","2/2"});
      riskFactors.add(new int[] {1,2});
      riskFactors.add(new int[] {1});
      riskFactors.add(new int[] {1});
    }
    public int[][] getRiskFactors( int[] modelCombo )
    {
      int[] l1 = riskFactors.get(modelCombo[0]);
      int[] l2 = riskFactors.get(modelCombo[1]);
      int nInteractionTerms = l1.length * l2.length;
      int[][] rf = new int[nInteractionTerms][2];
      int counter = 0;
      for ( int i=0; i < l1.length; i++ )
      {
    	int nRiskAlleles1 = l1[i];
        for ( int j=0; j < l2.length; j++ )
        {
          int nRiskAlleles2 = l2[j];
          rf[counter][0] = nRiskAlleles1;
          rf[counter][1] = nRiskAlleles2;
          counter++;
        }
      }
      return rf;
    }
    
    public String[] getGt( int[] models, int[] riskCols )
    {
      String[] cGt = new String[2];
      cGt[0] = colGt.get(models[0])[riskCols[0]];
      cGt[1] = colGt.get(models[1])[riskCols[1]];
      return cGt;
    }
  }
  
  //--------------------------------------------------------------------------- 
  private class preAnalysis
  {
	private Map<String,List<List<String>>>[] pDefs;
	private String type = "genotype";
	private int ncols = 4;
    private int[] model;
    
    public preAnalysis( int[] modelCombo )
    {
      model = modelCombo;
      modelDefs md = new modelDefs();
      int[][] riskFactors = md.getRiskFactors(modelCombo);
      pDefs = new Map[riskFactors.length];
      for ( int i=0; i < riskFactors.length; i++ )
      {
        pDefs[i] = buildTable(md,modelCombo,riskFactors[i]);
      }
    }
    
    public String getModel()
    {
      StringBuffer modelStr = new StringBuffer();
      for ( int i=0; i < model.length; i++ )
      {
        if ( model[i] == 0 ) modelStr.append("Additive");
        else if ( model[i] == 1 ) modelStr.append("Dominant");
        else modelStr.append("Recessive");
        if ( i ==0 ) modelStr.append(" x ");
      }
      return modelStr.toString();
    }
    
    public Map<String,List<List<String>>>[] getPDefs(){ return pDefs; }
    
    public String getType(){ return type; }
    
    public int getNcols(){ return ncols; }
    
    private Map<String,List<List<String>>> buildTable( modelDefs md, int[] modelCombo, int[] rf )
    {
      String[] colTag = new String[] {"0:0","1:1","2:2","3:3"};
      int[][] inpt = new int[3][2];
      inpt[0] = new int[]{rf[0],0};
      inpt[1] = new int[]{0,rf[1]};
      inpt[2] = new int[]{rf[0],rf[1]};
      
      String[] refGt = md.getGt(modelCombo,new int[]{0,0});
      String[][] gtCols = new String[4][2];
      gtCols[0] = refGt;
      for( int i=0; i < inpt.length; i++ ) gtCols[i+1] = md.getGt(modelCombo,inpt[i]);
      return build_cols(colTag,gtCols);    	
    }
  }
  
  //---------------------------------------------------------------------------  
  public void run( IOManager iom ) throws GEException
  {
	int nmodelCombos = modelCombos.length;
	int nmarkerCombos = markerCombos.length;
	
	analyses = new IORAnalysis[nmarkerCombos][nmodelCombos];
	preAnalysis[] preAnalyses = new preAnalysis[nmodelCombos];
	
	for ( int i=0; i < nmodelCombos; i++ ) preAnalyses[i] = new preAnalysis(modelCombos[i]);
	
	for ( int i=0; i < nmarkerCombos; i++ )
	{
	  int[] markerCombo = markerCombos[i];
      for ( int j =0; j < nmodelCombos; j++ ) analyses[i][j] = new IORAnalysis(preAnalyses[j],markerCombo);
	}

	// Analyze observed data.
	analyze(-1);
	
    for ( int i = 0; i < nullCycles; i++ )
    {
      tSim.simulateFounderGenotypes(0);
      dSim.simulateDescendantGenotypes(0);   
      analyze(i);
    }
    
    List<CCAnalysis> ccanalyses = new ArrayList<CCAnalysis>();
    for ( int i=0; i < analyses.length; i++ )
    {
      for ( int j=0; j < analyses[i].length; j++ )
      {
        CCAnalysis[] ioAnalyses = analyses[i][j].getCCAnalyses();
        for ( int k=0; k < ioAnalyses.length; k++ ) ccanalyses.add(ioAnalyses[k]);
      }
    }
    CCAnalysis[] arrayAnalyses = ccanalyses.toArray(new CCAnalysis[0]);
    
    try {
		Reporter r = new Reporter(iom.getAppId(),iom.getAppVer(),iom.getSpecification(),study,iom.getinDate(),arrayAnalyses,iom.getoutpathStem());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//    for ( int i=0; i < analyses.length; i++)
//    {
//      IORAnalysis[] markerAnalyses = analyses[i];
//      for ( int j = 0; j < markerAnalyses.length; j++ )
//      {
//        markerAnalyses[j].report();
//      }
//    }
  }
  
  //---------------------------------------------------------------------------
  public void analyze( int iter )
  {
	for ( int i=0; i < analyses.length; i++ )
	{
	  IORAnalysis[] markerAnalyses = analyses[i];
	  for ( int j=0; j < markerAnalyses.length; j++ )
	  {
	    markerAnalyses[j].analyze(iter);
	  }
	}
  }
  
  //---------------------------------------------------------------------------
//  public double processInteraction( CCAnalysis[] analyses )
//  {
//	double[] intermediateOR = new double[3];
//	for ( int i=0; i < analyses.length; i++ )
//	{
//	  for ( int j=0; j < study.length; j++)
//	  {
//        Report[] reports = analyses[i].getStatReports(0,0,j);
//        for ( int k=0; k < reports.length; k++ )
//        {
//          Report report = reports[k];
//          double obs_value = Double.parseDouble(report.getObsValue());
//          intermediateOR[i] = obs_value;
//          System.out.println(obs_value);
//        }
//	  }
//	}
//	double iOR = intermediateOR[2] / (intermediateOR[0]*intermediateOR[1]);
//	System.out.println("Interaction odds ratios: "+ iOR);
//	return iOR;
//  }
  
  //---------------------------------------------------------------------------    
//  public CCAnalysis[] setAnalyses( int[] markerCombo ) 
//  {
//	CCAnalysis[] orAnalyses = new CCAnalysis[3];
//    for ( int i = 0; i < 3; i++ ) orAnalyses[i] = setAnalysis(i,markerCombo);
//    return orAnalyses;
//  }
  
  //---------------------------------------------------------------------------    
//  private CCAnalysis setAnalysis( int code, int[] markerCombo )
//  {
//	Map<String,List<List<String>>> pDefs = new HashMap<String,List<List<String>>>();
//	String model = "allelic";
//	String itype = "allele";
//	int ncols = 2;	
////	if ( model.equals("dominant") )
////	{
////	  itype = "Genotype";
////      pDefs = build_cols(new String[]{"1:0","0:1"}, new String[]{"(2/.)|(./2)","./."});
////	}
////	else if ( model.equals("recessive") )
////	{
////	  itype = "Genotype";
////      pDefs = build_cols(new String[]{"1:0","0:1"}, new String[]{"2/2","./."});
////	}
////	else if ( model.equals("additive") )
////	{
////	  ncols = 3;
////	  itype = "Genotype";
////      pDefs = build_cols(new String[]{"2:0","1:1","0:2"}, new String[]{"(2/2)","(2/1)|(1/2)","./."});
////	}
////	else 
//	if ( model.equals("allelic") )
//	{
//	  String[][] alleleCombos = new String[][]{{"2","1"},{"1","1"}};
//	  if ( code == 1 ) alleleCombos[0] = new String[]{"1","2"};
//	  else if ( code == 2 ) alleleCombos[0] = new String[]{"2","2"};
//      pDefs = build_cols(new String[]{"1:0","0:1"},alleleCombos);
//	}
//	CCAnalysis analysis = null;
//	try 
//	{
//	  analysis = buildDefs(pDefs,itype,ncols,model,markerCombo);
//	} catch (GEException e) 
//	{
//	  e.printStackTrace();
//	}
//	return analysis;
//  }
  
  //---------------------------------------------------------------------------    
  private CCAnalysis buildDefs ( Map<String,List<List<String>>> defs, String type, int ncols, String model, int[] markerCombo ) throws GEException
  {
	CCAnalysis.Table.Column[] cdefs = new CCAnalysis.Table.Column[ncols];
	CCStat[] stats = new CCStat[] {(CCStat) Ut.newModule(Ut.pkgOf(CCStat.class),"InteractionOddsRatios","")};
	CCStat[] metas = new CCStat[0];
	if ( nmetas > 0 ) metas = new CCStat[] {(CCStat) Ut.newModule(Ut.pkgOf(CCStat.class),"InteractionMetaOR","")};

	int col_counter = 0;
	Map<Integer,CCAnalysis.Table.Column> holder = new HashMap<Integer,CCAnalysis.Table.Column>();
    for (Iterator<String> it = defs.keySet().iterator(); it.hasNext();) 
    {
	  String key = (String) it.next();
	  List<List<String>> coldef = defs.get(key);
	  String[] wtcl = key.split(":");
	  int colWt = Integer.parseInt(wtcl[0]);
	  int colIndex = Integer.parseInt(wtcl[1]);
      List<GtypeMatcher> lgtmatchers = new ArrayList<GtypeMatcher>();
	  for (int i = 0; i < coldef.size(); i++) 
	  {
	    List<String> gt_values = new ArrayList<String>();
		gt_values = coldef.get(i);
  	    GtypeMatcher gtm = new GtypeMatcher(markerCombo);
		for (int j = 0; j < gt_values.size(); j++) gtm.setRegex(j,gt_values.get(j),af);
	    lgtmatchers.add(gtm);
	  }
	  CCAnalysis.Table.Column ctc = createNewColumn(lgtmatchers,colWt);
      holder.put(colIndex,ctc);
	  col_counter++;
    }
	for (int l = 0; l < col_counter; l++) cdefs[l] = holder.get(l);
	int[] covarIDs = new int[] {};
	int[][] repeatGroup = new int[][]{markerCombo};
	CCAnalysis analysis = new CCAnalysis(cdefs,model,stats,metas,type,covarIDs,50,repeatGroup,new String[]{"original"},"pedgenie");    
	analysis.setStudySubjects(study);
	return analysis;
  }
  
  //---------------------------------------------------------------------------  
  public ColumnImp createNewColumn(List<GtypeMatcher> lgtmatchers, int wt){ return new ColumnImp(lgtmatchers, wt); }
  
  //---------------------------------------------------------------------------  
  private Map<String,List<List<String>>> build_cols( String[] col_tags, String[][] col_gts )
  {
	// col_gt contains the risk column gt groups and the ref col gt groups for two loci
	Map<String,List<List<String>>> pDefs = new HashMap<String,List<List<String>>>();
	for ( int i=0; i < col_tags.length; i++ )
	{
	  String[] col_gt = col_gts[i];
	  List<String> gt = new ArrayList<String>();
	  for ( int j=0; j < col_gt.length; j++ )
	  {
	    gt.add(col_gt[j]);
	  }
	  List<List<String>> col = new ArrayList<List<String>>();
	  col.add(gt);
	  pDefs.put(col_tags[i],col);
	}
    return pDefs;
  }
	
}

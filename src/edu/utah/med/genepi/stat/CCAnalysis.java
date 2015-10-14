//*****************************************************************************
// CCAnalysis.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeMatcher;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hc.binAnalysis;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.util.Counter;
import edu.utah.med.genepi.util.Ut;

//=============================================================================
public class CCAnalysis {

  public interface Table {
    public int getColumnCount();
    public Column getColumnAt(int index);
    public Row getRowAt(int index);
    public Row getRowFor(Ptype pt);
    public Totals getTotals();
    public String getTableName();
    public String getColumnHeading();
    public String getRowHeading();
    public String getColumnLabelAt(int index);
    public int getRowCount();
    public String getAttachMessage();
    
    public interface Column {
      public int subsumesAtype(Gtype gt, boolean first);
      public int subsumesGtype(Gtype gt);
      public int getWeight();
      //public String getType();
      public GtypeMatcher[] getGtypeMatcher();
    }
    public interface Row {
      public Ptype getPtype();
      public Number[] getCells();
      public String getLabel();
      public int[] getCellsN();
    }
    public interface Totals {
      public Number[] forColumns();
      public Number[] forRows();
      public Number forTable();
    }
  }
 
  public Study[]          study;
  private int             nstudy, nstats, nmetas, nmetasTDT, percent;
  private final String    myModel, myType;
  private final String[]  myTableType;
  private CCStat[]        ccstat, metastat, metaTDTstat;
  private CCStat          myStat = null;
  private CCStatRun[][][][] statRuns;
  private CCStatRun[][][]   metaRuns;
  private CCStatRun[][][]   metaTDTRuns;
  private final int[]     colWeights;
  private final int       iRefCol;
  private static int[]    quantids;
  private Indiv[][]       caseInds, controlInds, unknownInds;
  private Indiv[]         caseTDTInds, controlTDTInds, unknownTDTInds;
  private Table.Column[]  col_def;
  private Table[][][][]   observedTable;
  private Table[][][]     obsMetaTDTTable;
  private Table           statTable = null;
  private int[]           loci;
  private String          application_id;
  private int[][]         repeatGroup;
  private int             nrepeat;
  private int             ntabletype;

  //---------------------------------------------------------------------------
  //public CCAnalysis(int index, Table.Column[] column_defs, String[] markers,
  //public CCAnalysis(Table.Column[] column_defs, String[] markers,
  public CCAnalysis(Table.Column[] column_defs, 
                    String model, CCStat[] stats, CCStat[] metas,
                    String type, int[] quantIDs, int percentage, 
                    int[][] repeat, String[] tabletype, String app_id)
  {
    //myIndex = index;
    //myMarkers = markers;
    myModel = model;
    myType = type;
    myTableType = tabletype;
    ccstat = stats;
    nstats = stats.length;
    col_def = column_defs;
    percent = percentage;
    repeatGroup = repeat;
    nrepeat = repeatGroup.length;
    ntabletype = tabletype.length;
    application_id = app_id;
    if ( quantIDs != null )
      quantids = quantIDs;
    else 
      quantids = new int[1];
    colWeights = extractWeights(column_defs);
    iRefCol = Ut.indexOfMin(colWeights);
    // separate metaTDT from other meta analysis
    if ( metas.length > 0 ) {
      ArrayList<CCStat> metalist = new ArrayList<CCStat>();
      ArrayList<CCStat> metaTDTlist = new ArrayList<CCStat>();
      for (CCStat ms : metas ) {
        if ( (ms.getStatType()).equals("metaTDT") ) {
	  metaTDTlist.add(ms);
        }
	else { 
	  metalist.add(ms);
        }
      }
      if ( metalist.size() > 0 ) {
	metastat = (CCStat[]) metalist.toArray(new CCStatImp[0]);
        nmetas = metastat.length;
      }
      if ( metaTDTlist.size() > 0 ) {
	metaTDTstat = (CCStat[]) metaTDTlist.toArray(new CCStatImp[0]);
        nmetasTDT = metaTDTstat.length;
      }
    }
  }

  //---------------------------------------------------------------------------
  public int[] getLoci() 
  {
    GtypeMatcher[] gtm = col_def[0].getGtypeMatcher();
    return gtm[0].iLoci;
  }
    
  public int[] getLoci(int index) 
  {
    return repeatGroup[index];
  }
  
  public int[] getiLoci(int index) 
  {
    return loci;
  }

  //---------------------------------------------------------------------------
  //public int getIndex() { return myIndex; }

  //---------------------------------------------------------------------------
  //Use getLoci and then convert it back to MarkerName thru' GDef
  //public String[] getMarkers() { return myMarkers; }

  //---------------------------------------------------------------------------
  public String getModel() { return myModel; }

  //---------------------------------------------------------------------------
  public String getType() { return myType; }

  //---------------------------------------------------------------------------
  public String[] getTableType() { return myTableType; }

  //---------------------------------------------------------------------------
  public int[] getColumnWeights() { return colWeights; }

  //---------------------------------------------------------------------------
  public int getReferenceColumnIndex() { return iRefCol; }

  //---------------------------------------------------------------------------
  //public Table[][] getTables() { return ltable; }

  //---------------------------------------------------------------------------
  public Table[][][][] getObservedTable() { return observedTable; }

  //---------------------------------------------------------------------------
  public Table[][][] getObsMetaTDTTable() { return obsMetaTDTTable; }

  //---------------------------------------------------------------------------
  //public Table getTrioTDTTable() { return trioTDTTable; }
 
  //---------------------------------------------------------------------------
  //public void setStudySubjects( Indiv[] cases, Indiv[] controls,
  //                              Indiv[] unknowns )
  public void setStudySubjects( Study[] std )
  {
    study  = std;
    nstudy = std.length;
    statRuns = new CCStatRun[nrepeat][ntabletype][nstudy][nstats];
    metaRuns = new CCStatRun[nrepeat][ntabletype][nmetas];
    metaTDTRuns = new CCStatRun[nrepeat][ntabletype][nmetasTDT];
    caseInds = new Indiv[nstudy][];
    controlInds = new Indiv[nstudy][];
    unknownInds = new Indiv[nstudy][];
    ArrayList<Indiv> caseTDTList = new ArrayList<Indiv>();
    ArrayList<Indiv> controlTDTList = new ArrayList<Indiv>();
    ArrayList<Indiv> unknownTDTList = new ArrayList<Indiv>();
    //add new array for #repeat
    for ( int i = 0; i < nstudy; i++ )
    {
      caseInds[i] = null;
      controlInds[i] = null;
      unknownInds[i] = null;
      PedData pd    = study[i].getPedData();
      // hapMC - the selected subset data should has =>50% genotype data 
      loci = getLoci();

      // only select ind's gtype has been phased = true
      //if ( application_id.equals("hapmc") &&
      if ( !application_id.equals("pedgenie") &&
           loci.length > 1)
      {
        caseInds[i]    = pd.getIndividuals( PedQuery.IS_CASE,
                                            true,  
                                            loci );
        controlInds[i] = pd.getIndividuals( PedQuery.IS_CONTROL,
                                            true,  
                                            loci );
        unknownInds[i] = pd.getIndividuals( PedQuery.IS_UNKNOWN,
                                            true,  
                                            loci );
	if ( nmetasTDT > 0 ) {
   	  caseTDTList.addAll(Arrays.asList(caseInds[i])); 
   	  controlTDTList.addAll(Arrays.asList(controlInds[i])); 
   	  unknownTDTList.addAll(Arrays.asList(unknownInds[i])); 
	}
      }
      //Ryan changed 10/11/07 because the cases and controls don't need to be
      //set with compressed genotype data.
      //else if ( !application_id.equals("hapBuilder") )
      else 
      {
        caseInds[i]    = pd.getIndividuals(PedQuery.IS_CASE);
        controlInds[i] = pd.getIndividuals(PedQuery.IS_CONTROL);
        unknownInds[i] = pd.getIndividuals(PedQuery.IS_UNKNOWN);
        if ( nmetasTDT > 0 ) {
          caseTDTList.addAll(Arrays.asList(caseInds[i]));
          controlTDTList.addAll(Arrays.asList(controlInds[i]));
          unknownTDTList.addAll(Arrays.asList(unknownInds[i]));
        }
      }
    }
    caseTDTInds = (Indiv[]) caseTDTList.toArray(new Indiv[0]);
    controlTDTInds = (Indiv[]) controlTDTList.toArray(new Indiv[0]);
    unknownTDTInds = (Indiv[]) unknownTDTList.toArray(new Indiv[0]);

    for ( int r = 0; r < nrepeat; r++ )
    {
      for ( int t = 0; t < ntabletype; t++ )
      {
        for ( int s = 0; s < nstudy; s++ )
        {
          for (int j = 0; j < nstats; ++j)
          {
            statRuns[r][t][s][j] = new CCStatRun(this, ccstat[j], null);
          }
        }
    
        for ( int i = 0; i < nmetas; i++ ){
          metaRuns[r][t][i] = new CCStatRun(this, metastat[i], null);
	}
        for ( int i = 0; i < nmetasTDT; i++ )  {
	  metaTDTRuns[r][t][i] = new CCStatRun(this, metaTDTstat[i], null );
        }
      }
    }
  }

  //-------------------------------------------------------------
  public void startObsRuns( int sigrun, int simcycle, compressGtype[] cGtypes,
                            int step )
  {
    Table[][][][] metaTable = new Table[nmetas][nrepeat][ntabletype][nstudy];
    Table[][][] metaTDTTable = new Table[nmetas][nrepeat][ntabletype];
    obsMetaTDTTable = new Table[nrepeat][ntabletype][];
    observedTable = new Table[nrepeat][ntabletype][nstudy][];
    CCStatRun[][][][] statruns = this.statRuns;
    String datatype = "sim";
    //If simcycle is -1 then always use "obs"
    if( simcycle == -1 )
      datatype = "obs";
   
    for ( int r = 0; r < nrepeat; r++ ) {
      if ( nrepeat > 1 ) {
        for ( int c = 0; c < col_def.length; c++ ) {
          GtypeMatcher[] gtm = col_def[c].getGtypeMatcher();
          for ( int g = 0; g < gtm.length; g++ ) {
            gtm[g].updateSelectedLocus(repeatGroup[r]);
          }
        }
      }
      for ( int t = 0; t < ntabletype; t++ ) {
        for ( int s = 0; s < nstudy; s++ ) {
          ArrayList<Table> Ltable = new ArrayList<Table>();
          loci = getLoci();
          for (int i = 0; i < nstats; ++i) {
            myStat = statRuns[r][t][s][i].myStat;
            statTable = (Table) crazyAces( sigrun,simcycle,datatype,
                                           cGtypes[s], step);
            boolean addtabletolist = true;
            if (statTable != null ) {
              if (i > 0)
              addtabletolist = false;
              if ( addtabletolist )
                Ltable.add(statTable);
            }
            statRuns[r][t][s][i].computeObserved(statTable);
            //String result = statRuns[r][s][i].getObservationalReport();
            String result = statRuns[r][t][s][i].observedResult.toString();
            String name = statRuns[r][t][s][i].getStatName();
            String [] results = result.split(":");
            String obsvalue = "";
            if( results.length > 1 ) {
              if( results[0].equals("WARNING") ) {
                obsvalue = "-1";
              } else {
                obsvalue = results[1];
              }
            } else {
              obsvalue = results[0];
            }
            if( obsvalue.equals("-") ) {
              obsvalue = "-1";
            }
            if( obsvalue.equals("-1") ) {
              //String l = "";
              String l = loci[0] + "";
              for(int k=0; k < loci.length; k++) {
                int snp = loci[k] + 1;
                //l = l + snp;
                l = l + "-" + snp;
              }
              String val = l + ":" + myModel + ":" + i;
              cGtypes[s].store_badanalyses_indices(val);
            }
          }
          observedTable[r][t][s] = (Table[]) Ltable.toArray(new Table[0]);
       
          for ( int i = 0 ; i < nmetas; i++ ) {
            //CCStat metaStat = metaRuns[r][t][i].myStat;
            if(i == 0) {
              metaTable[i][r][t][s] = (Table) crazyAces(sigrun,simcycle,
						datatype,
                                                cGtypes[s], step);             
            } else {
              metaTable[i][r][t][s] = metaTable[0][r][t][s];     
            }
          }
        }
       
        for ( int i = 0 ; i < nmetas; i++ ) 
          metaRuns[r][t][i].computeObserved(metaTable[i][r][t]);

	// need modification for cGtypes[s], s can not be use
	/*
        for ( int i = 0 ; i < nmetasTDT; i++ )
        {
          //CCStat metaTDTStat = metaTDTRuns[r][t][i].myStat;
          if(i == 0) {
            metaTDTTable[i][r][t] = (Table) crazyAces(sigrun,simcycle,datatype,
                                                  cGtypes[s], step);             
          } else {
            metaTDTTable[i][r][t] = metaTDTTable[0][r][t];
          }
          boolean addtabletolist = true;
          if ( metaTDTTable[i][r][t] != null && 
		!Mtable.contains(metaTDTTable[i][r][t]) )
              Mtable.add(metaTDTTable[i][r][t]);
            else
              addtabletolist = false;
          metaTDTRuns[r][t][i].computeObserved(metaTDTTable[i][r][t]);
        }
        **/
      }
    }
  }
  
  //----------------------------------------------------------
  public void startsimRuns ( int sigrun, int simcycle,
                             compressGtype[] cGtypes,
                             int step)
  {
    Table[][][] metaTable = new Table[nmetas][ntabletype][nstudy];
    //System.out.println("Startsimruns Thread: " + p.getId());
    //added extra loop for repeat runs
    for ( int r = 0; r < nrepeat; r++ )
    {
      if ( nrepeat > 1 )
      {
        for ( int c = 0; c < col_def.length; c++ )
        {
          GtypeMatcher[] gtm = col_def[c].getGtypeMatcher();
          for ( int g = 0; g < gtm.length; g++ )
            gtm[g].updateSelectedLocus(repeatGroup[r]);
        }
      }
      for ( int t = 0; t < ntabletype; t++ )
      {
        for ( int s = 0; s < nstudy; s++ )
        {
          ArrayList<Table> Ltable = new ArrayList<Table>();
          loci = getLoci();
         
          for (int i = 0; i < nstats; ++i)
          {
            if(!cGtypes[s].check_bad_analyses(loci, this.myModel, i))
            {
              myStat = statRuns[r][t][s][i].myStat;
              //System.out.println("Started Analysis: " + this.myModel);
              Table table = (Table) crazyAces(sigrun,simcycle,"sim",cGtypes[s],step);
              statRuns[r][t][s][i].computeSimulated(table);
            }
          }
       
          for ( int i = 0 ; i < nmetas; i++ )
          {
            CCStat metaStat = metaRuns[r][t][i].myStat;
            if(i == 0)
            {
              metaTable[i][t][s] = (Table) crazyAces(sigrun,simcycle,"sim",
                                                  cGtypes[s], step);             
            }
            else
            {
              metaTable[i][t][s] = metaTable[0][t][s];
            }
          }
        }
        for ( int i = 0 ; i < nmetas; i++ )
          metaRuns[r][t][i].computeSimulated(metaTable[i][t]);

	// need modification on cGtypes can not has array s
	/*
        for ( int i = 0 ; i < nmetasTDT; i++ )
        {
          //CCStat metaTDTStat = metaTDTRuns[r][t][i].myStat;
          if(i == 0) {
            metaTDTTable[i][r][t] = (Table) crazyAces(sigrun,simcycle,"sim",
                                                  cGtypes[s], step);             
          } else {
            metaTDTTable[i][r][t] = metaTDTTable[0][r][t];
          }
          metaTDTRuns[r][t][i].computeObserved(metaTDTTable[i][r][t]);
	}
	**/
      }
    }
  }
  /*
  public void startObsRuns( int sigrun, int simcycle, compressGtype[] cGtype,
                            int analysis_index )
  {
    CCStatRun[][][] statruns = this.statRuns;
    observedTable = new Table[nrepeat][nstudy][];
    String datatype = "sim";
    //If simcycle is -1 then always use "obs"
    if( simcycle == -1 )
    {
      datatype = "obs";
    }
    
    for ( int r = 0; r < nrepeat; r++ )
    {
      if ( nrepeat > 1 )
      {
        for ( int c = 0; c < col_def.length; c++ )
        {
          GtypeMatcher[] gtm = col_def[c].getGtypeMatcher();
          
          for ( int g = 0; g < gtm.length; g++ )
          {
            gtm[g].updateSelectedLocus(repeatGroup[r]);
          }
        }
      }
      for ( int s = 0; s < nstudy; s++ )
      {
        ArrayList<Table> Ltable = new ArrayList<Table>();
        loci = getLoci();
        //boolean failed = false;
        //List failed_tests = new ArrayList();
        for (int i = 0; i < nstats; ++i)
        {
          myStat = statRuns[r][s][i].myStat;
          statTable = (Table) crazyAces(sigrun,simcycle,datatype,cGtype);
          boolean addtabletolist = true;
          if (statTable != null )
          {
            if (i > 0)
              addtabletolist = false;
            if ( addtabletolist )
              Ltable.add(statTable);
          }
          statRuns[r][s][i].computeObserved(statTable);
          //String result = statRuns[r][s][i].getObservationalReport();
          String result = statRuns[r][s][i].observedResult.toString();
          String name = statRuns[r][s][i].getStatName();
          String [] results = result.split(":");
          String obsvalue = "";
          if( results.length > 1 )
          {
            if( results[0].equals("WARNING") )
            {
              obsvalue = "-1";
            }
            else
            {
              obsvalue = results[1];
            }
          }
          else
          {
            obsvalue = results[0];
          }
          if( obsvalue.equals("-") )
          {
            obsvalue = "-1";
          }
          if( obsvalue.equals("-1") )
          {
            String val = analysis_index + ":" + i;
            cGtype.store_badanalyses_indices(val);
          }
        }
        observedTable[r][s] = (Table[]) Ltable.toArray(new Table[0]);
      }
    }
  }

  //----------------------------------------------------------
  public void startsimRuns ( int sigrun, int simcycle,
                             compressGtype cGtype,
                             int analysis_index)
  {
    //System.out.println("Startsimruns Thread: " + p.getId());
    //added extra loop for repeat runs
    for ( int r = 0; r < nrepeat; r++ )
    {
      if ( nrepeat > 1 )
      {
        for ( int c = 0; c < col_def.length; c++ )
        {
          GtypeMatcher[] gtm = col_def[c].getGtypeMatcher();
          for ( int g = 0; g < gtm.length; g++ )
            gtm[g].updateSelectedLocus(repeatGroup[r]);
        }
      }
      for ( int s = 0; s < nstudy; s++ )
      { 
        ArrayList<Table> Ltable = new ArrayList<Table>();
        loci = getLoci();
        for (int i = 0; i < nstats; ++i)
        {
          if(!cGtype.check_bad_analyses(analysis_index, i))
          {
            myStat = statRuns[r][s][i].myStat;
            //System.out.println("Started Analysis: " + this.myModel);
            Table t = (Table) crazyAces(sigrun,simcycle,"sim",cGtype);
            statRuns[r][s][i].computeSimulated(t);
          }
        }
      //System.out.println("Ended Analysis: " + this.myModel);
      }
    }
  }
  **/

  //---------------------------------------------------------------------------
  public CCAnalysis.Table crazyAces ( int sigrun,
                                      int simcycle,
                                      String datatype,
                                      compressGtype cGtype,
                                      int step )
  {
    Table t = null;
    String fletter = myModel.substring(0,1);
    String sletter = myModel.substring(1,2);
    if(simcycle == -1 && sletter.equals("G") && fletter.equals("M")){
      t = fill_counters("M", cGtype, -1);
    }
    else if(simcycle == -1 && fletter.equals("H") && !sletter.equals("G"))// && step > 1)
      {
        int hap = Integer.parseInt(myModel.substring(4,myModel.length())) - 1;
        t = fill_counters(sletter, cGtype, hap);
      }
   
    else
    {
      binAnalysis bA = new binAnalysis(sigrun,simcycle,datatype,col_def,this.myModel,this.loci,cGtype);
      t = bA.create_table();
    }
    return t;
  }

  //---------------------------------------------------------------------------
  public void startStatRuns()
  {
    startStatRuns(-1, null, 0);
  }

  //---------------------------------------------------------------------------
  public void startStatRuns(int index)
  {
    startStatRuns(index, null, 0);
  }

  //---------------------------------------------------------------------------
  public void startStatRuns(int index, Thread p, int xls)
  {
    Table[][][][] metaTable = new Table[nmetas][nrepeat][ntabletype][nstudy];
    //Table[][] metaTDTTable = new Table[nmetasTDT][ntabletype];
    //TableMaker[][][] obsTM  = new TableMaker[nrepeat][ntabletype][nstudy];
    //TableMaker[][] obsMetaTDTTM  = new TableMaker[nrepeat][ntabletype];
    observedTable         = new Table[nrepeat][ntabletype][nstudy][];
    obsMetaTDTTable       = new Table[nrepeat][ntabletype][];
    Indiv.GtSelector selector = Indiv.GtSelector.OBS;
    if ( index >= 0 ) selector = Indiv.GtSelector.SIM;

    for ( int r = 0; r < nrepeat; r++ ) {
      if ( nrepeat > 1 ) {
        for ( int c = 0; c < col_def.length; c++ ) {
          GtypeMatcher[] gtm = col_def[c].getGtypeMatcher();
          for ( int g = 0; g < gtm.length; g++ )
            gtm[g].updateSelectedLocus(repeatGroup[r]);
        }
      }
      for ( int t = 0; t < ntabletype; t++) {
        ArrayList<Table> Mtable = new ArrayList<Table>();
        for ( int s = 0; s < nstudy; s++ ) {
          ArrayList<Table> Ltable = new ArrayList<Table>();
          TableMaker obsTM = new TableMaker ( caseInds[s],
                                            controlInds[s], 
                                            unknownInds[s],
                                            selector,
                                            index,
                                            col_def,
                                            quantids, 
	        	  	            myType,
                                            myTableType[t] );
   
          for (int i = 0; i < nstats; ++i) {
            myStat = statRuns[r][t][s][i].myStat;
            statTable = (Table) myStat.getTable(obsTM, p);
            boolean addtabletolist = true;
  
            if (statTable != null ) {
              for ( int j = 0; j < Ltable.size(); j++ ) {
                if ( statTable == Ltable.get(j) )  {
                  addtabletolist = false;
                  break;
                }
              }
              if ( addtabletolist )
                Ltable.add(statTable);
            }
            statRuns[r][t][s][i].computeObserved(statTable);
          }
          observedTable[r][t][s] = (Table[]) Ltable.toArray(new Table[0]);
     
          for ( int i = 0 ; i < nmetas; i++ ) {
            CCStat metaStat = metaRuns[r][t][i].myStat;
            metaTable[i][r][t][s] = (Table) metaStat.getTable(obsTM);
          }
        }
        for ( int i = 0 ; i < nmetas; i++ ) {
          metaRuns[r][t][i].computeObserved(metaTable[i][r][t]);
        }

	// meta TDT - comb all studies individuals 
    	if ( nmetasTDT > 0 ) { 
          TableMaker obsTM = new TableMaker ( caseTDTInds,
                                          controlTDTInds,
                                          unknownTDTInds,
                                          selector,
                                          index,
                                          col_def,
                                          quantids,
                                          myType,
                                          myTableType[t] );

  	  // what happens to repeat for meta? 
	  for ( int i = 0; i < nmetasTDT; i++ ) {
	    CCStat metaTDTStat = metaTDTRuns[r][t][i].myStat;
	    Table mt = (Table) metaTDTStat.getTable(obsTM);
            boolean addtabletolist = true;
            if ( mt != null && !Mtable.contains(mt) )
	      Mtable.add(mt);
	    else
	      addtabletolist = false;

            metaTDTRuns[r][t][i].computeObserved(mt);
          }
          if ( Mtable != null )
	    obsMetaTDTTable[r][t] = (Table[]) Mtable.toArray(new Table[0]);
        }
      }
    }
  }

  //---------------------------------------------------------------------------
  public void updateStatRuns(int index)
  {
    updateStatRuns(index, null, 0);
  }

  //---------------------------------------------------------------------------
  public void updateStatRuns(int index, Thread p, int xls)
  {
    // need modification for metaTDT
    Table[][][] metaTable = new Table[nmetas][ntabletype][nstudy];
    //Table[][] metaTDTTable = new Table[nmetasTDT][ntabletype];
    //TableMaker[][][] simTM = new TableMaker[nrepeat][ntabletype][nstudy];
    for ( int r = 0; r < nrepeat; r++ )
    {
      if ( nrepeat > 1 )
      {
        for ( int c = 0; c < col_def.length; c++ )
        {
          GtypeMatcher[] gtm = col_def[c].getGtypeMatcher();
          for ( int g = 0; g < gtm.length; g++ )
            gtm[g].updateSelectedLocus(repeatGroup[r]);
        }
      }
 
      for ( int t = 0; t < ntabletype; t++ )
      {
        for ( int s = 0; s < nstudy; s++ )
        {
          TableMaker simTM = new TableMaker ( caseInds[s],
                                            controlInds[s],
                                            unknownInds[s],
                                            Indiv.GtSelector.SIM,
                                            index,
                                            col_def,
                                            quantids,
                                            myType,
                                            myTableType[t] ); 

          for (int i = 0; i < nstats; ++i)
          {
            myStat = statRuns[r][t][s][i].myStat;
            statTable = myStat.getTable(simTM);
            statRuns[r][t][s][i].computeSimulated(statTable);
          }

          for ( int i = 0 ; i < nmetas; i++ )
          {
            CCStat metaStat = metaRuns[r][t][i].myStat;
            metaTable[i][t][s] = (Table) metaStat.getTable(simTM);
          }
        }
  
        for ( int i = 0 ; i < nmetas; i++ )
          metaRuns[r][t][i].computeSimulated(metaTable[i][t]);

        // meta TDT - comb all studies individuals 
        if ( nmetasTDT > 0 ) {
          TableMaker simTM = new TableMaker ( caseTDTInds,
                                          controlTDTInds,
                                          unknownTDTInds,
                                            Indiv.GtSelector.SIM,
                                          index,
                                          col_def,
                                          quantids,
                                          myType,
                                          myTableType[t] );

          // what happens to repeat for meta? 
          for ( int i = 0; i < nmetasTDT; i++ ) {
            CCStat metaTDTStat = metaTDTRuns[r][t][i].myStat;
            CCAnalysis.Table mt = (Table) metaTDTStat.getTable(simTM);
            metaTDTRuns[r][t][i].computeSimulated(mt);
          }
        }
      }
    }
  }

  //---------------------------------------------------------------------------
  public CCStatRun.Report[] getStatReports(int irepeat, int itabletype,
                                           int studyID)
  {
    CCStatRun.Report[] report = new CCStatRun.Report[nstats];
    for (int i = 0; i < nstats; ++i)
    {
      //l[s].add(statRuns[s][i].newReport());
      report[i] = statRuns[irepeat][itabletype][studyID][i].newReport();
    }
    //return (CCStatRun.Report[]) l.toArray(new CCStatRun.Report[0]);
    return report;
  }

  //---------------------------------------------------------------------------
  public CCStatRun.Report[] getMetaReports(int irepeat, int itabletype)
  {
    CCStatRun.Report[] report = new CCStatRun.Report[nmetas];
    for ( int i = 0 ; i < nmetas; i ++ )
      report[i] = metaRuns[irepeat][itabletype][i].newReport();
    return report;
  }

  //---------------------------------------------------------------------------
  public CCStatRun.Report[] getMetaTDTReports(int irepeat, int itabletype)
  {
    CCStatRun.Report[] report = new CCStatRun.Report[nmetasTDT];
    for ( int i = 0 ; i < nmetasTDT; i ++ )
      report[i] = metaTDTRuns[irepeat][itabletype][i].newReport();
    return report;
  }

  //---------------------------------------------------------------------------
  public int getRepeat()
  {
    return nrepeat;
  }

  //---------------------------------------------------------------------------
  /*private void tallyExpressionEvents(Indiv.GtSelector gs)
  {
    gpTallier.resetTallies();
    for (int i = 0; i < caseInds.length; ++i)
      gpTallier.countExpressionEvent(
        caseInds[i].getGtype(gs), Ptype.CASE
      );
    for (int i = 0; i < controlInds.length; ++i)
      gpTallier.countExpressionEvent(
        controlInds[i].getGtype(gs), Ptype.CONTROL
      );
  }

  //---------------------------------------------------------------------------
  private void tallyQuantExpEvents(Indiv.GtSelector gs)
  {
    quantTallier.resetTallies();
    for (int i = 0; i < caseInds.length; ++i)
    {
      for ( int j = 0 ; j < quantids.length; j++ )
      {
        //if ( (caseInds[i].quant_val).getQdata(j) != 0.0  )
        if ( caseInds[i].quant_val != null )
          quantTallier.sumExpressionEvent(
            caseInds[i].getGtype(gs), j, 
            (caseInds[i].quant_val).getQdata(j) );
      }
    }
    for (int i = 0; i < controlInds.length; ++i)
    {
      for ( int j = 0 ; j < quantids.length; j++ )
      {
        if  ( controlInds[i].quant_val != null )
        quantTallier.sumExpressionEvent(
          		controlInds[i].getGtype(gs), j,
			(controlInds[i].quant_val).getQdata(j) );
      }
    }
  }
  **/
  //---------------------------------------------------------------------------
  private static int[] extractWeights(Table.Column[] cols)
  {
    int[] wts = new int[cols.length];
    for (int i = 0; i < cols.length; ++i)
      wts[i] = cols[i].getWeight();
    return wts;
  }
  //---------------------------------------------------------------------------
  public boolean checkDistinctWeights()
  {
    int[] wt = getColumnWeights();
    ArrayList<Integer> wtlist = new ArrayList<Integer>();
    for ( int i = 0; i < wt.length - 1; i++)
    {
      //System.out.println("wt at " + i + " is " + wt[i]);
      //System.out.println( "******************" );
      //for ( Iterator it = wtlist.iterator(); it.hasNext(); )
      //  System.out.println("list has : " + it.next() );
      if ( wtlist.contains(new Integer(wt[i])))
        return false;
      else 
      {
        wtlist.add(new Integer(wt[i]));
        //System.out.println("add : " + wt[i] + " to list");
      }
    }
    return true;
  }

  //---------------------------------------------------------------------------
  public boolean checkDistinctRefWt()
  {
    int flag = 0;
    for ( int i = 0; i < colWeights.length; i++)
    {
      if ( colWeights[i] == colWeights[iRefCol] )
        flag++;
      if ( flag > 1 )
        return false;
    }
    return true;
  }

  //----------------------------------------------------------------------------
  // added 2-25-08 Ryan Abo
  private Table fill_counters(String model, compressGtype cGtype, int hap)
  {
	Ptype[] thePtypes = new Ptype[] {Ptype.CASE, Ptype.CONTROL};
	int numcols = col_def.length;
	int numrows = thePtypes.length;
	int[] pid2RowIx = {0,1,0};
	int[][] vals = null;
	
    SortedSet store = (SortedSet) new TreeSet();
	  
    for( int i=0; i < this.loci.length; i++ )
    {
      store.add(this.loci[i] + 1);
    }
    
	if(model.equals("M"))
	{
      vals = cGtype.loci_monotype_prescreens.get(store.toString());
	}
	else if(model.equals("A"))
	{
      int[][] hets = cGtype.loci_diphet_prescreens.get(store.toString());
	  int[][] homs = cGtype.loci_diphom_prescreens.get(store.toString());
	  int[] case_cells = {homs[0][hap],hets[0][hap],cGtype.num_cases};
	  case_cells[2] = case_cells[2] - case_cells[0] - case_cells[1];
	  int[] control_cells = {homs[1][hap],hets[1][hap],cGtype.num_controls};
	  control_cells[2] = control_cells[2] - control_cells[0] - control_cells[1];
	  int[][] v = {case_cells, control_cells};
	  vals = v;
	}
	else if(model.equals("D"))
	{
      int[][] hets = cGtype.loci_diphet_prescreens.get(store.toString());
	  int[][] homs = cGtype.loci_diphom_prescreens.get(store.toString());
	  int[] case_cells = {homs[0][hap] + hets[0][hap], cGtype.num_cases};
	  case_cells[1] = case_cells[1] - case_cells[0];
	  int[] control_cells = {homs[1][hap] + hets[1][hap], cGtype.num_controls};
	  control_cells[1] = control_cells[1] - control_cells[0];
	  int[][] v = {case_cells, control_cells};
	  vals = v;    
	}
	else if(model.equals("R"))
	{
      int[][] hets = cGtype.loci_diphet_prescreens.get(store.toString());
	  int[][] homs = cGtype.loci_diphom_prescreens.get(store.toString());
	  int[] case_cells = {homs[0][hap], cGtype.num_cases};
	  case_cells[1] = case_cells[1] - case_cells[0];
	  int[] control_cells = {homs[1][hap], cGtype.num_controls};
	  control_cells[1] = control_cells[1] - control_cells[0];
	  int[][] v = {case_cells, control_cells};
	  vals = v;    
	}	
	Counter[][] theCounters = set_counters(numrows, numcols, vals);
	return new ContingencyTable(thePtypes, col_def, theCounters, pid2RowIx);
  }
 
  //----------------------------------------------------------------------------
  private Counter[][] set_counters(int nRows, int nCols, int[][] vals)
  {
    Counter[][] counters = new Counter[nRows][nCols];
    for ( int irow = 0; irow < nRows; irow++ )
    {
      for ( int icol = 0; icol < nCols; icol++ )
      {
        counters[irow][icol] = new Counter();
        counters[irow][icol].set(vals[irow][icol]);
      }
    }
    return counters;     
  }
}

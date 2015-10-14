package edu.utah.med.genepi.hc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.GtypeMatcher;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.stat.CCStat;
import edu.utah.med.genepi.util.GEException;

public class analysisSet {
  
  private int                       setsize;
  private int[]                     istats = new int[] {};
  private int[]                     imetas = new int[] {};
  private String                    imodel = null;
  private String                    itype = null;
  private int                       ncols;
  private CCAnalysis.Table.Column[] cdefs;
  private int                       step;
  private CCAnalysis                analysis = null;
  private final Specification       specie;
  private final AlleleFormat        af;
  private int                       screentest;

  //---------------------------------------------------------------------------
  // This is the constructor for the analysisSet class. It is called from the
  // createAnalyses function in hapBuilder.java.
  // This class uses the significant loci sets from the previous analyses to
  // produce a new set of analyses to perform on
  // the new set of loci.
  public analysisSet(String app_id, int indx, Specification spec,
	                 String[] markers, int[] aloci, Set lociset, String aCode,
	                 int buildstep, int sigrun, int stest) throws GEException
  {
    // Store specification object.
    specie = spec;
    screentest = stest;
    // Store the step / level (how many loci are in a set)
    step = buildstep;
    // How many loci are in the set.
    setsize = step;
    // Model of the analysis.
    imodel = aCode;
    String sLetter = aCode.substring(1, 2);
    String fLetter = aCode.substring(0, 1);
    // Map of definitions to send to processing function
    Map pDefs = new HashMap();
    double dstep = Double.valueOf(step).doubleValue();
    // Retrieve AlleleFormat for GtypeBuilder
    af = spec.getGDef().getAlleleFormat();

    setStats(fLetter, sLetter, imodel);
       
    if ( sLetter.equals("G") )
    {
      imodel = imodel.substring(0,7);
      // Call specific global functions
      if ( fLetter.equals("H") ) 
      {
        double numcols = (Math.pow(2.0, dstep) * (Math.pow(2.0, dstep) + 1.0)) / 2.0;
        ncols = (int) numcols;
        itype = "Genotype";
        pDefs = dHapGlobal();
      } 
      else if ( fLetter.equals("C") )
      {
        double numcols = Math.pow(3.0, dstep);
        ncols = (int) numcols;
        itype = "Genotype";
        pDefs = dCGGlobal();
      }
      else if ( fLetter.equals("M") )
      {
        double numcols = Math.pow(2.0, dstep);
        ncols = (int) numcols;
        itype = "Allele";
        pDefs = mGlobal();
	  }
    } 
    else 
    {
      ncols = 2;
      if ( fLetter.equals("H") )
      {
        itype = "Genotype";
        pDefs = dHapModel();
      }
      else if ( fLetter.equals("C") )
      {
        itype = "Genotype";
        pDefs = dCGModel();
	  }
      else if ( fLetter.equals("M") ) 
      {
        itype = "Allele";
        pDefs = mSpecRed();
      }
	}
    cdefs = new CCAnalysis.Table.Column[ncols];
    buildDefs(app_id, indx, pDefs, aloci, markers);
  }

  //---------------------------------------------------------------------------
  /*
  * This function is called from the analysisSet function. In general, this
  * function builds the specific data structures needed to create a new
  * instance of CCAnalysis. It stores the new analyses in the private member
  * variable analysis. Once all the CCAnalyses have been built, the spec will
  * be updated to then run all the analyses. A HashMap of column definitions
  * is passed in and processed to construct the gtmatchers.
  */
  private void buildDefs ( String app_id, int indx, Map defs, int[] iloci,
                         String[] markers ) throws GEException
  {
    // This obtains the stats defined in the original specification file
    // (rgen file). They are indexed in an array by
    // their order in the specification file.
    CCStat[] stats = specie.getCCStats();
    // This is a list of metas that is not necessarily needed by this
    // module, but is needed by to create an
    // instance of CCAnalysis
    List<Object> lMetas = new ArrayList<Object>();
    CCStat[] metas = specie.getMetaStats();

    // Iterate through table columns
    int col_counter = 0;
    Map<Integer, CCAnalysis.Table.Column> holder = new HashMap<Integer, CCAnalysis.Table.Column>();
  
    // Iterate through the HashMap passed in. Each key is a new column
    // table, and the value contains a List of lists that define 
    // the g tags and a tags.
    for (Iterator it = defs.entrySet().iterator(); it.hasNext(); )
    {
      // Syntax to iterate through a map. First obtain entry.
      Map.Entry entry = (Map.Entry) it.next();
      // Obtain key
      String key = (String) entry.getKey();
      // Obtain value, which is a List of lists.
      List<List> value = (List) entry.getValue();
      // Split the key with : to obtain the weight and column index.
      String[] wtcl = key.split(":");
      String sWt = wtcl[0];
      String sCl = wtcl[1];
      int wt = Integer.parseInt(sWt);
      int cl = Integer.parseInt(sCl);
      // Create a list of GtypeMatchers for each column in the table.
      List<GtypeMatcher> lgtmatchers = new ArrayList<GtypeMatcher>();
      // Iterate through the list to obtain the lists of g tags
      for ( int i = 0; i < value.size(); i++ ) 
      {
        List<String> gs = new ArrayList<String>();
        gs = value.get(i);
        // Create a GtypeMatcher array to add the genotype patterns to it.
        GtypeMatcher gtm = new GtypeMatcher(iloci);
        // Iterate through the g tag list to obtain the a tags, which are the
        // specific genotype patterns to match.
        for ( int j = 0; j < gs.size(); j++ )
        {
          gtm.setRegex(j, gs.get(j), af);
        }
        lgtmatchers.add(gtm);
      }
      CCAnalysis.Table.Column ctc = specie.createNewColumn( lgtmatchers, wt );
      // holder HashMap is used to hold the ctc so that they can be added to the
      // cdefs array in order of their column index.
      holder.put(cl, ctc);
      // cdefs[col_counter] = specie.createNewColumn(lgtmatchers, wt, itype);
      col_counter++;
    }
  
    // Iterate through holder to add the ctcs to cdefs in column order.
    for ( int l = 0; l < col_counter; l++ )
    {
      cdefs[l] = holder.get(l);
    }

    // Create a list of new stats.
    List<CCStat> newStats = new ArrayList<CCStat>();
    for ( int iistat = 0; iistat < istats.length; ++iistat )
    {
      newStats.add(stats[istats[iistat]]);
    }
  
    CCStat[] selected_stats = (CCStat[]) newStats.toArray(new CCStat[0]);

    // Create a list of new metas. This is not used for program, but is
    // needed to create a new CCAnalysis.
    List<CCStat> newMetas = new ArrayList<CCStat>();
    for ( int iimeta = 0; iimeta < imetas.length; ++iimeta )
    {
      newMetas.add(metas[imetas[iimeta]]);
    }
  
    CCStat[] selected_metas = (CCStat[]) newMetas.toArray(new CCStat[0]);

    // This is not used, but is needed for CCAnalysis.
    int[] covarIDs = new int[] {};
    int[][] repeatGroup = new int[1][iloci.length];
    // Create and store the analysis.
    analysis = new CCAnalysis( cdefs, imodel, selected_stats, selected_metas, itype, covarIDs, 
                             specie.getPercent(), repeatGroup, app_id );
  }

  //---------------------------------------------------------------------------
  // Returns a 2d array with the possible haplotypes given the step. If step
  // is 2 then there are 4 possible haplotypes.
  private String[][] create_haps()
  {
    double dStep = Double.valueOf(step).doubleValue();
    double dNumcols = Math.pow(2.0, dStep);
    int col_dim = (int) dNumcols;
    int row_dim = step;
    String[][] m = new String[row_dim][col_dim];
    
    // Fill the 2d array with the proper pattern of 1's and 2's.
    for ( int i = 0; i < row_dim; i++ )
    {
      String fill = "1";
      String current_fill = "1";
      String last_fill = "2";
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

  //---------------------------------------------------------------------------
  private Map dHapGlobal()
  {
    String[][] m = create_haps();
    List<String> ind = new ArrayList<String>();
    double dstep = Double.valueOf(step).doubleValue();
    int numcols = (int) Math.pow(2.0, dstep);
    for ( int i = 0; i < numcols; i++ )
    {
      ind.add(i + "");
	}
    List<String> clonnie = new ArrayList<String>();
    clonnie.addAll(ind);
    List<List> mList = new ArrayList<List>();
    for ( int j = 0; j < clonnie.size(); j++ )
    {
      String ii = clonnie.get(j);
      int ss = ind.size();
      for ( int k = 0; k < ss; k++ )
      {
        List<String> subList = new ArrayList<String>();
        subList.add(ii);
        subList.add(ind.get(k));
        mList.add(subList);
      }
      ind.remove(0);
    }  
    Map aDefs = new HashMap();
    // Iterate through the column indice pairs (0,0), (0,1), (0,2) etc...
    // These are all the diplotype haplotypes.
    // Create a list of lists.
    // This list will contain the g tags. In this case there is only one
    // list per column in the table.
    for ( int i = 0; i < mList.size(); i++ )
    {
      List<String> ll = mList.get(i);
      String cc1 = ll.get(0);
      int c1 = Integer.parseInt(cc1);
      String cc2 = ll.get(1);
      int c2 = Integer.parseInt(cc2);
      // Create a list of genotype for the g tags. In this case there are
      // going to be 1 g tag for each iteration, which is the number of loci 
      // in the set being considered.
      List<String> gTags = new ArrayList<String>();
      List tCol = new ArrayList();
      // Iterate down through the columns of each diplotype haplotype pair
	  // to extract the possible genotypes.
      for ( int j = 0; j < step; j++ )
      {
        String sAllele = m[j][c1];
        String fAllele = m[j][c2];
        String store = "";
        if ( sAllele == "1" && fAllele == "2" || sAllele == "2"	&& fAllele == "1" )
        {
          store = "(2/1)|(1/2)";
        } 
        else
        {
          store = fAllele + "/" + sAllele;
        }
        gTags.add(store);
      }
      tCol.add(gTags);
      aDefs.put(i + ":" + i, tCol);
    }
    return aDefs;
  }

  //---------------------------------------------------------------------------
  private Map dCGGlobal()
  {
    Map aDefs = new HashMap();
    List<List> gts = new ArrayList<List>();
    gts = create_Globals();
	List<List> ll = gts.get(0);
	for (int i = 0; i < ll.size(); i++)
	{
      List t = ll.get(i);
      List<List> oo = new ArrayList<List>();
      oo.add(t);
      aDefs.put(i + ":" + i, oo);
    }
    return aDefs;
  }

  //---------------------------------------------------------------------------  
  private Map mGlobal()
  {
    String[][] m = create_haps();
    Map aDefs = new HashMap();
    for ( int i = 0; i < ncols; i++ )
	{
      List<List> tCol = new ArrayList<List>();
      List<String> gTag = new ArrayList<String>();
      for ( int j = 0; j < step; j++ )
      {
        String allele = m[j][i];
        gTag.add(allele);
      }
      tCol.add(gTag);
      aDefs.put(i + ":" + i, tCol);
	}
	return aDefs;
  }
  
  //---------------------------------------------------------------------------
  /*
  * This function is called when a haplotype diplotype model is being
  * performed (Recessive, dominant, or recessive). A specific function is
  * called for each model and returns a map with the column definitions.
  */
  private Map dHapModel()
  {
    // Since for each Dom, Rec, and Add model there are 2^step analyses to
    // perform.
    // The model code that is passed in, such as HDom1, will be used to
    // determine which column from all the possible haplotypes to choose from.
    String[][] m = create_haps();
    String num = imodel.substring(4, imodel.length()); 
    int numb = Integer.parseInt(num); 
    String model = imodel.substring(1, 2);
    Map aDefs = new HashMap();
    numb--;
    
    if ( model.equals("D") )
    {
      aDefs = dHDom(numb, m);
    }
    else if ( model.equals("R") )
    {
      aDefs = dHRec(numb, m);
    }
    else
    {
      aDefs = dHAdd(numb, m);
      ncols = 3;
    }
    
    return aDefs;
  }

  //---------------------------------------------------------------------------
  private Map dHDom( int col, String[][] m )
  {
    Map aDefs = new HashMap();
    List<List> col1 = new ArrayList<List>();
    List<List> col2 = new ArrayList<List>();
    String compare = "./.";
    List<String> gTag1 = new ArrayList<String>();
    List<String> gTag2 = new ArrayList<String>();
    List<String> clst = new ArrayList<String>();
    for ( int i = 0; i < step; i++ )
    {
      clst.add(compare);
      String allele = m[i][col];
      String aTag1 = allele + "/.";
      String aTag2 = "./" + allele;
      gTag1.add(aTag1);
      gTag2.add(aTag2);
    }
    col2.add(clst);
    col1.add(gTag1);
    col1.add(gTag2);
    aDefs.put("1:0", col1);
    // Key is weight:table column
    aDefs.put("0:1", col2);
    return aDefs;
  }

  //---------------------------------------------------------------------------
  private Map dHRec( int col, String[][] m )
  {
    Map aDefs = new HashMap();
    List<List> col1 = new ArrayList<List>();
    List<List> col2 = new ArrayList<List>();
    List<String> g1 = new ArrayList<String>();
    List<String> g2 = new ArrayList<String>();
    String compare = "./.";
    for ( int i = 0; i < step; i++ )
    {
      String allele = m[i][col];
      String pattern = allele + "/" + allele;
      g2.add(compare);
      g1.add(pattern);
    }
    col1.add(g1);
    col2.add(g2);
    // Key is weight:table column
    aDefs.put("1:0", col1);
    aDefs.put("0:1", col2);
    return aDefs;
  }

  //---------------------------------------------------------------------------
  private Map dHAdd( int col, String[][] m )
  {
    Map aDefs = new HashMap();
    List<List> col1 = new ArrayList<List>();
    List<List> col2 = new ArrayList<List>();
    List<List> col3 = new ArrayList<List>();
    List<String> g1 = new ArrayList<String>();
    List<String> g2_1 = new ArrayList<String>();
    List<String> g2_2 = new ArrayList<String>();
    List<String> g3 = new ArrayList<String>();
    String compare = "./.";
    
    for ( int i = 0; i < step; i++ )
    {
      String allele = m[i][col];
      String rec_pattern = allele + "/" + allele;
      String dom_pattern1 = allele + "/.";
      String dom_pattern2 = "./" + allele; 
      g3.add(compare);
      g2_1.add(dom_pattern1);
      g2_2.add(dom_pattern2);
      g1.add(rec_pattern);
    }
    
    col1.add(g1);
    col2.add(g2_1);
    col2.add(g2_2);
    col3.add(g3);
    // Key is weight:table column
    aDefs.put("2:0", col1);
    aDefs.put("1:1", col2);
    aDefs.put("0:2", col3);
    return aDefs;
  }

  //---------------------------------------------------------------------------
  private Map dCGModel()
  {
    Map aDefs = new HashMap();
    String[] result = imodel.split("/");
    List<List> col1 = new ArrayList<List>();
    List<List> col2 = new ArrayList<List>();
    List<List> addcol = new ArrayList<List>();
    List<String> g1 = new ArrayList<String>();
    List<String> g2 = new ArrayList<String>();
    List<String> add2 = new ArrayList<String>();
    boolean flag = false;
    int col_len = 2;
    // Parse the models and determine which model for each loci.
    for ( int i = 0; i < result.length; i++ )
    {
      if ( result[i].equals("CDom") || result[i].equals("CRec") )
      {
        g1.add("(2/.)|(./2)");
        g2.add("./.");
        add2.add("(2/.)|(./2)");
      }
      else
      {
        g1.add("(2/.)|(./2)");
        g2.add("./.");
        add2.add("2/2");
        col_len = col_len + 2;
      }
    }
    col1.add(g1);
//	col2.add(g2);
//	addcol.add(add2);
    ncols = col_len;
    for ( int j=0; j<col_len; j++ )
    {
      aDefs.put("" + j + ":" + j, col1);
    }
    return aDefs;
  }

  //---------------------------------------------------------------------------
//	private Map dCGModel() {
//		Map aDefs = new HashMap();
//		String[] result = imodel.split("/");
//		List<List> col1 = new ArrayList<List>();
//		List<List> col2 = new ArrayList<List>();
//		List<List> addcol = new ArrayList<List>();
//		List<String> g1 = new ArrayList<String>();
//		List<String> g2 = new ArrayList<String>();
//		List<String> add2 = new ArrayList<String>();
//		boolean flag = false;
//		// Parse the models and determine which model for each loci.
//		for (int i = 0; i < result.length; i++) {
//			// if the model is Dominant then set the three g tags for each col.
//			// g1 is the g tags for the risk col, g2 is the for the compare
//			// col.,
//			// and add2 is the g tag for the additive model.
//			if (result[i].equals("CDom")) {
//				g1.add("(2/.)|(./2)");
//				g2.add("./.");
//				add2.add("(2/.)|(./2)");
//			}
//			// If the model is recessive add the three g tags.
//			else if (result[i].equals("CRec")) {
//				g1.add("2/2");
//				g2.add("./.");
//				add2.add("2/2");
//			}
//			// If model is additive add the three g tags.
//			else {
//				// Set the flag to determine if the additive model was used.
//				flag = true;
//				g1.add("(2/.)|(./2)");
//				g2.add("./.");
//				add2.add("2/2");
//			}
//		}
//		col1.add(g1);
//		col2.add(g2);
//		addcol.add(add2);
//		int col = 3;
//		if (flag) {
//			col = 3;
//			ncols = 3;
//		} else {
//			col = 2;
//			ncols = 2;
//		}
//		// Add all columns to the aDefs map.
//		aDefs.put("0:" + (col - 1), col2);
//		aDefs.put("1:" + (col - 2), col1);
//		// Check if additive model is in the model.
//		if (flag)
//			aDefs.put("2:" + (col - 3), addcol);
//		return aDefs;
//	}

  //---------------------------------------------------------------------------
  private Map mSpecRed()
  {
    String[][] m = create_haps();
    String num = imodel.substring(8, imodel.length());
    int numb = Integer.parseInt(num);
    Map aDefs = new HashMap();
    numb--;
    List<List> col1 = new ArrayList<List>();
    List<List> col2 = new ArrayList<List>();
    double dstep = Double.valueOf(step).doubleValue();
    for ( int i = 0; i < Math.pow(2.0, dstep); i++ )
    {
      List<String> g = new ArrayList<String>();
      for ( int j = 0; j < step; j++ )
      {
        String allele = m[j][i];
        g.add(allele);
      }
      if ( i == numb )
      {
	    col1.add(g);
      }
      else
      {
        col2.add(g);
      }
    }
    aDefs.put("1:0", col1);
    aDefs.put("0:1", col2);
    return aDefs;
  }

  //---------------------------------------------------------------------------
  private List<List> create_Globals()
  {
    List<String> g1 = new ArrayList<String>();
    List<String> g2 = new ArrayList<String>();
    List<String> g3 = new ArrayList<String>();
    g1.add("1/1");
    g2.add("(1/2)|(2/1)");
    g3.add("2/2");
    List<List> master = new ArrayList<List>();
    master.add(g1);
    master.add(g2);
    master.add(g3);
    List<List> el = new ArrayList<List>();
    el.addAll(master);
    List<List> allM = new ArrayList<List>();
    allM.add(el);
    for ( int i = 0; i < (step - 1); i++ )
    {
      List<List> clonnie = new ArrayList<List>();
      clonnie.addAll(el);
      allM.add(clonnie);
    }
    return cursey(allM);
  }

  //---------------------------------------------------------------------------
  private List cursey( List lCursey )
  {
    if ( lCursey.size() == 1 )
    {
      return lCursey;
    } 
    else
    {
      return cursey(combineEs(lCursey));
    }
  }

  //---------------------------------------------------------------------------
  private List<List> combineEs( List<List> lCurse )
  {
    List<List> l1 = lCurse.get(0);
    List<List> l2 = lCurse.get(1);
    lCurse.remove(0);
    List combed = new ArrayList();
    for ( int i = 0; i < l1.size(); i++ )
    {
      List it = l1.get(i);
      for ( int j = 0; j < l2.size(); j++ )
      {
        List clonnie = new ArrayList();
        clonnie.addAll(it);
        clonnie.add(l2.get(j).get(0));
        combed.add(clonnie);
      }
    }
    lCurse.add(0, combed);
    lCurse.remove(1);
    return lCurse;
  }

  //---------------------------------------------------------------------------
  public CCAnalysis getAnalysis()
  {
    return analysis;
  }
	
  //---------------------------------------------------------------------------  
  private void setStats ( String fletter, String sletter, String model )
  {
    CCStat[] cstats = specie.getCCStats();
    if ( cstats.length > 0 )
    {
      stat_processor("cc",cstats, fletter, sletter, model);    	
    }
    CCStat[] mstats = specie.getMetaStats();
    if ( mstats.length > 0)
    {
      stat_processor("meta",mstats, fletter, sletter, model);    
    }
  }
  
  //---------------------------------------------------------------------------    
  private void stat_processor(String type, CCStat[] stats, String fletter, String sletter, String model )
  {
    List am = new ArrayList();
    
    for ( int i=0; i < stats.length; i++ )
    {
      String statname = stats[i].getName();
      int oddsratio_idx = statname.indexOf("Odds");
      int chisquare_idx = statname.indexOf("Chi");
      int trend_idx = statname.indexOf("Trend");
     
      if ( sletter.equals("G") && ((String) model.substring(7,8)).equals("O") && oddsratio_idx > -1 )
      {
    	am.add(i);
      }
      else if ( sletter.equals("G") && (((String) model.substring(7,8)).equals("C") || model.indexOf("CS") == 10) && chisquare_idx > -1 && trend_idx == -1 )
      {
    	am.add(i);
      }
      else if ( fletter.equals("H") && sletter.equals("A") && trend_idx > -1 && screentest < 1)
      {
        am.add(i);
      }
      else if ( fletter.equals("H") && sletter.equals("A") && oddsratio_idx > -1 && (screentest == -1 || screentest == 1))
      {
        am.add(i);
      }      
	  else if ( !sletter.equals("G") && !sletter.equals("A") )
	  {
        if( oddsratio_idx > -1 )
        {
          am.add(i);
		}
		else if ( chisquare_idx > -1 && trend_idx == -1 )
        {
          am.add(i);
        }
      }
    }
    int x = 0;
    int[] newstats = null;
    
    if( am.size() == 2 )
    {
      newstats = new int[] {(Integer) am.get(0), (Integer) am.get(1)};
    }
    else if(am.size() == 1)
    {
      newstats = new int[] {(Integer) am.get(0)};
    }
    if (type.equals("meta"))
    {
      imetas = new int[newstats.length];
      imetas = newstats;
    }
    else{
      istats = new int[newstats.length];
      istats = newstats;	
    }	  
  }
}

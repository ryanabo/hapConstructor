package edu.utah.med.genepi.hc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.util.Ut;

public class compressGtype 
{
  private int[][][]           case_sims;
  private int[][][]           control_sims;
  private int[][]             case_obs;
  private int[][]             control_obs;
  private int                 numloci;
  private int                 num_cycles;
  public int                  num_cases = 0;
  public int                  num_controls = 0;
  public int                  case_cells = 0;
  public int                  control_cells = 0;
  public int                  case_trailers = 0;
  public int                  control_trailers = 0;
  public Map<String,int[][]>  loci_monotype_prescreens = new HashMap<String,int[][]>();
  public Map<String,int[][]>  loci_diphet_prescreens = new HashMap<String,int[][]>();
  public Map<String,int[][]>  loci_diphom_prescreens = new HashMap<String,int[][]>();
  private List                bad_obs_analyses = new ArrayList();

  //---------------------------------------------------------------------------  
  public compressGtype ( int numGDef, int nCycles, int case_len, int control_len, int study ) 
  {
    numloci = numGDef;
    num_cycles = nCycles;
    num_cases = case_len;
    num_controls = control_len;
  }
	
  //---------------------------------------------------------------------------
  public void initiate_obsStorage ( int len, String whicharray, Indiv[] indiv )
  {
    if ( whicharray.equals("case") )
    {
      case_obs = new int[numloci][len];
      case_cells = len;
      case_trailers = 16 - ((case_cells * 16) - num_cases);
    }
    else
    {
      control_obs = new int[numloci][len];
      control_cells = len;
      control_trailers = 16 - ((control_cells * 16) - num_controls);
    }
    
    int storage_counter = 0;
    int cell = 0;
    for ( int i=0; i < indiv.length;i++ )
    {
      Gtype gt = indiv[i].getGtype(Indiv.GtSelector.OBS);
      store_obsgt(gt, storage_counter,whicharray,cell);
      if ( storage_counter == 30 ) 
      {
        storage_counter = 0;
        cell++;
      }
      else
      {
        storage_counter = storage_counter + 2;
      }
	}
  }
	
  //---------------------------------------------------------------------------    
  public void initiate_simStorage ( int len, String whicharray )
  {
    if ( whicharray.equals("case") )
    {
      case_sims = new int[num_cycles][numloci][len];
	}
	else
	{
	  control_sims = new int[num_cycles][numloci][len];
	}
  }

  //---------------------------------------------------------------------------     
  // This function will input a Gtype and store the genotype data as an
  // integer as well as the frequency into the matrices.
  public void store_simgt ( Gtype gt, int storage_counter, int index, String affstatus, int cell ) 
  {
    String gts = gt.toString();
    String[] pairs = gts.split(" ");
    int[][][] m;
    
    if ( affstatus.equalsIgnoreCase("case") )
    {
      m = case_sims;
    }
    else
    {
      m = control_sims;
    }

    for ( int i = 0; i < pairs.length; i++ )
    {
      int working_bits = 0;
      if ( storage_counter != 0 ) 
      {
        working_bits = m[index][i][cell];
      }
      int x = gen_code(pairs[i], working_bits);
      m[index][i][cell] = x;
    }
  }

  //---------------------------------------------------------------------------
  public void store_obsgt ( Gtype gt, int storage_counter, String affstatus, int cell )
  {
    int[][] m;
    
    if ( affstatus.equals("case") )
    {
      m = case_obs;
    }
    else
    {
      m = control_obs;
    }
    
    String gts = gt.toString();
    String[] pairs = gts.split(" ");
    
    for ( int i = 0; i < pairs.length; i++ )
    {
      int working_bits = 0;
      
      if ( storage_counter != 0 )
      {
		working_bits = m[i][cell];
      }
      int x = gen_code(pairs[i], working_bits);
      m[i][cell] = x;
    }
  }

  //---------------------------------------------------------------------------
  public int[][] get_caseobs()
  {
    return case_obs;
  }

  //---------------------------------------------------------------------------
  public int[][] get_controlobs()
  {
    return control_obs;
  }

  //---------------------------------------------------------------------------
  public int[][][] get_casesims()
  {
    return case_sims;
  }
	
  //---------------------------------------------------------------------------
  public int[][][] get_controlsims()
  {
    return control_sims;
  }

//	public void store_tgt(Gtype gt, int pid, int index) {
//		tFreqs[pid][index] = gt.getHaploFrequency();
//		String gts = gt.toString();
//		String[] pairs = gts.split(" ");
//		for (int i = 0; i < pairs.length; i++) {
//			tGtypes[i][pid][index] = gen_code(pairs[i]);
//		}
//	}

  //---------------------------------------------------------------------------
  private int gen_code(String pair, int working_bits) 
  {
    String[] ap = pair.split("/");
    // Store 0 for 1 and 1 for 2, turn into binary.
    int first_allele = (Integer.parseInt(ap[0]) - 1) << 1;
    int second_allele = Integer.parseInt(ap[1]) - 1;
    working_bits = (working_bits << 2) | (first_allele | second_allele);
    return working_bits;
  }

  //---------------------------------------------------------------------------
  public List prescreen_analyses ( List analyses, Set loci, int[] iloci, int sigrun )
  {
    //2-D array with first row containing the monotype counts, middle row containign the diplotype heter. counts,
    //the third row containing the diplotype homoz. counts.
    int[][] cases;
    int[][] controls;
    
    if ( sigrun == -1 )
    {
      cases = case_obs;
      controls = control_obs;
    }
    else
    {
      cases = case_sims[sigrun];
      controls = control_sims[sigrun];
    }
    
    //Count monotypes for cases
    int [] case_monotypes = process_monotypes(cases,case_trailers,case_cells,num_cases, iloci);
    //Count monotypes for controls
    int [] control_monotypes = process_monotypes(controls,control_trailers,control_cells,num_controls,iloci);
    //Count diplotypes for cases
    int nhaps = (int) Math.pow(2, iloci.length);
	  
    int [] case_diplotypes = new int[nhaps*(nhaps+1) / 2];
    int [] control_diplotypes = new int[nhaps*(nhaps+1) / 2];
    int[] temp1 = process_diplotypes(cases,case_trailers,case_cells,num_cases, iloci);
    int[] temp2 = process_diplotypes(controls,control_trailers,control_cells,num_controls,iloci);
	  
    for( int i=0; i < nhaps*(nhaps+1) / 2; i ++ )
    {
      case_diplotypes[i] = temp1[i];
      control_diplotypes[i] = temp2[i];
    }
    
    int [] case_composite = process_composite(cases,case_trailers,case_cells,num_cases, iloci);
    int [] control_composite = process_composite(controls,control_trailers,control_cells,num_controls,iloci);
	
    int[][] composites = {case_composite, control_composite};
    int[][] diplotypes = {case_diplotypes,control_diplotypes};
    int[][] monotypes = {case_monotypes,control_monotypes};
	  
    SortedSet store = (SortedSet) new TreeSet();
	  
    for( int i=0; i < iloci.length; i++ )
    {
      store.add(iloci[i] + 1);
    }
	  
    String key = store.toString();
    //System.out.println(store);
	  
    loci_monotype_prescreens.put(key,monotypes);

    int[][] diphets = extract_hets(diplotypes,iloci);
    int[][] diphoms = extract_homs(diplotypes,iloci);
      
    loci_diphet_prescreens.put(key,diphets);
    loci_diphom_prescreens.put(key,diphoms);
    List new_analyses = new ArrayList();
    new_analyses.addAll(analyses);
    
    if ( sigrun == -1 )
    {
      List bad_monotypes = check_array(monotypes);
      List bad_diplotypes = check_diplotypes(diphets, diphoms);
	  
      if ( check_composites(composites) && new_analyses.contains("CGlobalCS") )
	  {
        new_analyses.remove("CGlobalCS");
	  }
	  
      if ( bad_monotypes.size() != 0 )
      {
    	if(new_analyses.contains("MGlobalCS"))
    	{
    	  new_analyses.remove("MGlobalCS");
    	}
        for ( int j =0; j < bad_monotypes.size(); j++ )
        {
          new_analyses.remove(bad_monotypes.get(j));
        }
      }
      
      if ( bad_diplotypes.size() != 0 ) 
      {
    	if (new_analyses.contains("HGlobalCS"))
    	{
    	  new_analyses.remove("HGlobalCS");
    	}
        for ( int j =0; j < bad_diplotypes.size(); j++ )
        {
          new_analyses.remove(bad_diplotypes.get(j));
        }
      }

//  	    for(int i=0;i<analyses.size();i++){
//    	  String model = (String) analyses.get(i);
//  		  String sLetter = model.substring(1, 2);
//		  String fLetter = model.substring(0, 1);
//		  if(fLetter.equals("H")){
//  	        String num = model.substring(4, model.length());
//  		    int numb = Integer.parseInt(num);
//  		    numb--;
//  		    if(bad_diplotypes.contains(numb)){
//  		      analyses.remove(model);
//  		    }
//		  }
//		  else if(fLetter.equals("M")){
//			String num = model.substring(8, model.length());
//			int numb = Integer.parseInt(num);
//			numb--;
//  		    if(bad_monotypes.contains(numb)){
//    		  analyses.remove(model);
//    		}
//		  }
//  	    }
	    //}
    }
    return new_analyses;
  }

  //---------------------------------------------------------------------------
  private List check_diplotypes ( int[][] hets, int[][] homs )
  {
    List<String> badones = new ArrayList<String>();
    
    for ( int i = 0 ; i < hets[0].length; i++ )
    {
          int dom_sum = hets[0][i] + hets[1][i] + homs[0][i] + homs[1][i];
          int rec_sum = homs[0][i] + homs[1][i];
          boolean additive_flag = false;
          if(dom_sum < 5){
            badones.add("HDom" + (i+1));
            additive_flag = true;
          }
          if(rec_sum < 5){
            badones.add("HRec" + (i+1));
            additive_flag = true;
          }
          if(additive_flag){
        	badones.add("HAdd" + (i+1));
          }
        }
        return badones;	
      }
    
  //---------------------------------------------------------------------------  
  private int[][] extract_homs ( int[][] dips, int[] loci )
  {
      int nhaps = (int) Math.pow(2, loci.length);
      int[][] homcounts = new int[2][nhaps];
      int[] recinds = get_homindices(nhaps);
      for(int i=0; i < recinds.length; i++){
        homcounts[0][i] = dips[0][recinds[i]];
        homcounts[1][i] = dips[1][recinds[i]];
      }
      return homcounts;
    }

  //---------------------------------------------------------------------------  
  private int[][] extract_hets ( int[][] dips, int[] loci )
  {
      int nhaps = (int) Math.pow(2, loci.length);
      int[][] hetmap = new int[nhaps][nhaps-1];
      int[][] indmap = build_indmap(nhaps);
      for(int i = 0; i < nhaps ; i++){
        int iter = 0;
        for(int j = 0; j < indmap.length; j++){
          if(i == j){
            for(int p=0; p < nhaps-i-1; p++){
              hetmap[i][iter] = indmap[j][p];
              iter = iter + 1;
            }
          }
          else{
            int indices = i-j-1;
            hetmap[i][iter] = indmap[j][indices];
            iter = iter+1;
          }
          if(iter == nhaps -1){
            break;
          }
        }
      }
      int[][] casecontrol_hets = new int[2][nhaps];
      for(int i=0; i < hetmap.length ; i++){
        int[] indices = hetmap[i];
    	int casetotal = 0;
    	int controltotal = 0;
        for(int j =0; j< indices.length ;j++){
        	casetotal = casetotal + dips[0][indices[j]];
        	controltotal = controltotal + dips[1][indices[j]];
        }
        casecontrol_hets[0][i] = casetotal;
        casecontrol_hets[1][i] = controltotal;
      }
      return casecontrol_hets;
    }
    
  //---------------------------------------------------------------------------  
  private int[] get_homindices ( int nhaps )
  {
      int[] recind = new int[nhaps];
      for(int i =0; i< nhaps; i++){
        recind[i] = ((nhaps*(nhaps+1)) / 2) - (( (nhaps - i) * ((nhaps -i) + 1) ) / 2);
      }
      return recind;
    }
    
  //---------------------------------------------------------------------------  
  private int[][] build_indmap ( int nhaps )
  {
      int[][] imap = new int[nhaps-1][nhaps-1];
      int[] recind = get_homindices(nhaps);
      for(int i =0; i < nhaps-1; i++){
        int start = recind[i] + 1;
        int end = recind[i+1];
        int iter = 0;
        for(int j = start; j < recind[i+1] ; j++){
          imap[i][iter] = j;
          iter = iter + 1;
        }
      }
      return imap;
    }
    
  //---------------------------------------------------------------------------
  private List check_array ( int[][] array )
  {
    List badones = new ArrayList();
    
    for ( int i = 0 ; i < array[0].length; i++ )
    {
      int sum = array[0][i] + array[1][i];
        
      if ( sum < 5 )
      {
        badones.add("MSpecRed" + (i+1));  
      }
    }
    return badones;	
  }
  
  //---------------------------------------------------------------------------
  private boolean check_composites ( int[][] array )
  {   
    for ( int j = 0; j < 2; j++ )
    {
      for ( int i = 0 ; i < array[j].length; i++ )
      {      
        if ( array[j][i] < 3 )
        {
          return true;  
        }
      }
    }
    return false;
  }
    
  //---------------------------------------------------------------------------  
  private int[] process_monotypes ( int[][] array, int trailers, int cells, int num_indiv, int[] loci )
  {
    int[][] tbl_counter = new int[2][(int) Math.pow(2.0,loci.length)];
    
    for ( int i=0; i< Math.pow(2.0,loci.length); i++ )
    {
      tbl_counter[1][i] = i;
    }
    
    tbl_counter = process_mglobals(tbl_counter,array,trailers,cells,num_indiv, loci);
    return process_tblcounter(tbl_counter);
  }
    
  //---------------------------------------------------------------------------
  private int[] process_diplotypes ( int[][] array, int trailers, int cells, int num_indiv, int[] loci )
  {
      int[][] haps = create_haps(loci.length);
      int[][] haps2 = create_haps(loci.length);
      int num_haps = (int) Math.pow(2.0, loci.length);
      int num_cols = num_haps*num_haps;
	  int[][] tbl_counter = new int[2][num_cols];
	  int iter = 0;
	  
	  for(int i=0;i< (int) haps[0].length; i++){
		for(int j =0; j < (int) haps2[0].length - i; j++){
		  int genotype = 0;
		  int first = ((haps[0][i] << 1) | haps[0][j+i]);
		  int genotype_inverse = 0;
		  boolean inverse_flg = false;
		  for(int k=0;k<loci.length;k++){
		    genotype = genotype << 2 | ((haps[k][i] << 1) | haps[k][j+i]);
		    int allele1 = haps[k][i];
		    int allele2 = haps[k][j+i];
		    int exor = allele1^allele2;
		    if(exor == 1){
		      if(k == 0 ){
		    	 genotype_inverse = genotype_inverse << 2 | (((haps[k][i] ^ 1) << 1)  | (haps[k][j+i] ^ 1));
		      }
		      else{
		        genotype_inverse = genotype_inverse << 2 | (((haps[k][i] ^ 1) << 1)  | (haps[k][j+i] ^ 1));
		      }
		    }
		    else{
		      if(k == 0){
		        genotype_inverse = first;
		      }
		      else{
		        genotype_inverse = genotype_inverse << 2 | ((haps[k][i] << 1) | haps[k][j+i]);
		      }
		    }
		  }
		  tbl_counter[1][genotype] = iter;
		  if(genotype_inverse > 0){
	        tbl_counter[1][genotype_inverse] = iter;	
		  }
		  iter++;
		} 
	  }
	  
	  tbl_counter = process_hglobals(tbl_counter,array,trailers,cells,num_indiv, loci);
	  return process_tblcounter(tbl_counter);
  }
  
  //---------------------------------------------------------------------------  
  private int[] process_composite (int[][] array, int trailers, int cells, int num_indiv, int[] loci )
  {
    int[][] tbl_counter = create_Globals(loci);
    tbl_counter = process_cglobals(tbl_counter,array,trailers,cells,num_indiv, loci);
    return process_tblcounter(tbl_counter);
  }
    
  //---------------------------------------------------------------------------    
  private int[][] process_mglobals ( int[][] tbl_counters,int[][] array, int trailers, int cells, int num_indiv, int[] loci )
  {
	for ( int j = 0; j < cells; j++ )
	{
	  if ( trailers > 0 && j == 0 )
	  {
	    for ( int p = 0; p < (2*trailers); p++ )
	    {
	      int previous = 0;
	  	  for ( int i = 0; i < loci.length; i++ )
	  	  {
	  		  int n = array[loci[i]][(cells - 1)];
	  		  n = (n >> p) & 1;
	          previous = (previous << 1) | n;
	      }
	  	  tbl_counters[0][previous]++;
	    }
	  }
	  else
	  {
        for ( int k = 0; k < 32; k++ )
        {
          int previous = 0;
  	      for ( int i = 0; i < loci.length; i++ )
  	      {
  		    int n = array[loci[i]][(cells - 1) - j];
  		    n = (n >> k) & 1;
            previous = (previous << 1) | n;
          }
  	      tbl_counters[0][previous]++;
        }  	    
	  }
	}
    return tbl_counters;  
  }

  //---------------------------------------------------------------------------
  private int[][] process_hglobals ( int[][] tbl_counters, int[][] array, int trailers, int cells, int num_indiv, int[] loci )
  {
	for ( int j = 0; j < cells; j++ )
	{
	  if ( trailers > 0 && j == 0){
	    for(int p = 0; p < trailers; p++){
	      int previous = 0;
	  	  for(int i = 0; i < loci.length; i++){
	  		  int n = array[loci[i]][(cells - 1)];
	  		  n = (n >> (p*2)) & 3;
	          previous = (previous << 2) | n;
	        }
	  	  tbl_counters[0][previous]++;
	    }
	  }
	  else
	  {
        for(int k = 0; k < 16; k++){
          int previous = 0;
  	      for(int i = 0; i < loci.length; i++){
  		    int n = array[loci[i]][(cells - 1) - j];
  		    n = (n >> (k*2)) & 3;
            previous = (previous << 2) | n;
          }
  	      tbl_counters[0][previous]++;
        }
	  }
	}
    return tbl_counters;  
  }
  
  //---------------------------------------------------------------------------  
  private int[][] process_cglobals ( int[][] tbl_counters, int[][] array, int trailers, int cells, int num_indiv, int[] loci )
  {
    for ( int j = 0; j < cells; j ++ )
    {
      if ( trailers > 0 && j == 0 )
      {
	    for ( int p = 0; p < trailers; p++ )
	    {
          int previous = 0;
          for ( int i = 0; i < loci.length; i++ )
          {
            int n = array[loci[i]][(cells - 1)];
            n = (n >> (p*2)) & 3;
            //XOR two bits, shift left and append AND of two bits
            int exclusiveor = (n & 1) ^ ((n >> 1) & 1);
            int newbits = (exclusiveor << 1) | ((n & 1) & ((n >> 1) & 1));
            previous = (previous << 2) | newbits;
          }
          tbl_counters[0][previous]++;
        }
      }
      else
      {
        for ( int k = 0; k < 16; k++ )
        {
          int previous = 0;
          for ( int i = 0; i < loci.length; i++ )
          {
            int n = array[loci[i]][(cells - 1) - j];
            n = (n >> (k*2)) & 3;
            //XOR two bits, shift left and append AND of two bits
            int exclusiveor = (n & 1) ^ ((n >> 1) & 1);
            int newbits = (exclusiveor << 1) | ((n & 1) & ((n >> 1) & 1));
            previous = (previous << 2) | newbits;
          }
          tbl_counters[0][previous]++;
        } 
      }
    }
    return tbl_counters;
  }
  
  //-----------------------------------------------------------------------------------------   
  private int[][] create_Globals ( int[] loci )
  {
    int column = 0;
    
    for ( int pp = 0; pp < loci.length; pp++ )
    {
      column = (column << 2) | 2;
    }
    
    int[][] tbl_counter = new int[2][column + 1];
    List<Integer> orig = new ArrayList<Integer>();
    orig.add(0);
    orig.add(2);
    orig.add(1);
    int iter = 0;
    
    for ( int i =0; i < loci.length - 1; i++ )
    {
      List<Integer> copy = new ArrayList<Integer>();
      List<Integer> empty = new ArrayList<Integer>();
      copy.add(0);
      copy.add(2);
      copy.add(1);
      
      for ( int j = 0; j < orig.size(); j++ )
      {
        for ( int k = 0; k < copy.size(); k++ )
        {
          int first_num = (int) orig.get(j);
          int second_num = (int) copy.get(k);
          empty.add((first_num) << 2 | (second_num));
          tbl_counter[1][(first_num) << 2 | (second_num)] = iter;
          iter++;
        }
      }
      orig = empty;
      iter = 0;
    }
    return tbl_counter;
  }
  
  //---------------------------------------------------------------------------
  private int[] process_tblcounter(int[][] tbl_counter){
  	//System.out.println("End model "+ model);
	int[] s = tbl_counter[1];
	int [] counts = new int[tbl_counter[1].length];
    for(int j = 0; j < tbl_counter[1].length; j++){
      if(tbl_counter[0][j] > 0){
	    counts[tbl_counter[1][j]] = counts[tbl_counter[1][j]] + tbl_counter[0][j];
      }
    }
    return counts;
  }
  
  //---------------------------------------------------------------------------  
  private int[][] create_haps(int step) {
	double dStep = Double.valueOf(step).doubleValue();
	double dNumcols = Math.pow(2.0, dStep);
	int col_dim = (int) dNumcols;
	int row_dim = step;
	int[][] m = new int[row_dim][col_dim];
	// Fill the 2d array with the proper pattern of 1's and 2's.
	for (int i = 0; i < row_dim; i++) {
		int fill = 0;
		int current_fill = 0;
		int last_fill = 1;
		int switchCount = 0;
		double cc = i + 1.0;
		double denom = Math.pow(2.0, cc);
		int divid = (int) (Math.pow(2.0, dStep) / denom);
		for (int j = 0; j < col_dim; j++) {
			if (switchCount == divid) {
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
  public int[] get_hdom(int aff, int[] loci,int h_num){
    int row = 0;
    SortedSet s = new TreeSet();
    for(int i=0; i < loci.length; i++){
      s.add(loci[i] + 1);
    }
    
    String key = s.toString();
    
    int[][] hetcounts = loci_diphet_prescreens.get(key);
    int[][] homcounts = loci_diphom_prescreens.get(key);
    int[] domcounts = new int[2];
    
    int num_inds = num_cases;
    if(aff == 1){
      num_inds = num_controls;
      row = 1;
    }
    int domtotal = hetcounts[row][h_num-1] + homcounts[row][h_num-1];
    domcounts[1] = domtotal;
    domcounts[0] = num_inds - domtotal;
    return domcounts;
  }
  
  //---------------------------------------------------------------------------  
  public int[] get_hrec(int aff, int[] loci,int h_num){
    int row = 0;
    SortedSet s = new TreeSet();
    for(int i=0; i < loci.length; i++){
      s.add(loci[i] + 1);
    }
    
    String key = s.toString();
    
    int[][] homcounts = loci_diphom_prescreens.get(key);
    int[] reccounts = new int[2];
    
    int num_inds = num_cases;
    if(aff == 1){
      num_inds = num_controls;
      row = 1;
    }
    int rectotal = homcounts[row][h_num-1];
    reccounts[1] = rectotal;
    reccounts[0] = num_inds - rectotal;
    return reccounts;
  }

  //---------------------------------------------------------------------------
  public int[] get_hadd(int aff, int[] loci,int h_num){
    int row = 0;
    SortedSet s = new TreeSet();
    for(int i=0; i < loci.length; i++){
      s.add(loci[i] + 1);
    }
    
    String key = s.toString();
    
    int[][] hetcounts = loci_diphet_prescreens.get(key);
    int[][] homcounts = loci_diphom_prescreens.get(key);
    int[] addcounts = new int[3];
    
    int num_inds = num_cases;
    if(aff == 1){
      num_inds = num_controls;
      row = 1;
    }
    int domtotal = hetcounts[row][h_num-1];
    int rectotal = homcounts[row][h_num-1];
    addcounts[2] = rectotal;
    addcounts[1] = domtotal;
    addcounts[0] = num_inds - domtotal - rectotal;
    return addcounts;
  }
  
  //---------------------------------------------------------------------------
  public int[] get_mspec(int aff, int[] loci,int h_num){
    int row = 0;
    SortedSet s = new TreeSet();
    for(int i=0; i < loci.length; i++){
      s.add(loci[i] + 1);
    }
    
    String key = s.toString();
    
    int[][] monocounts = loci_monotype_prescreens.get(key);
    int[] mcounts = new int[2];
    
    int num_inds = num_cases;
    if(aff == 1){
      num_inds = num_controls;
      row = 1;
    }
    int mtotal = monocounts[row][h_num-1];
    mcounts[1] = mtotal;
    mcounts[0] = (num_inds*2) - mtotal;
    return mcounts;
  }
  
  //---------------------------------------------------------------------------
  //indx is a String analysis index : statrun index
  public void store_badanalyses_indices(String indx){
    bad_obs_analyses.add(indx);
  }
  
  //---------------------------------------------------------------------------
  public boolean check_bad_analyses(int[] loci, String model, 
                                    int statrun_indx)
  {
    String l = "";
    for(int k=0; k < loci.length; k++)
    {
      int snp = loci[k] + 1;
      l = l + "-" + snp;
    }
    String key = l + ":" + model + ":" + statrun_indx;
    if(bad_obs_analyses.contains(key)){
      return true;
    }
    else{
      return false;
    }
  }
  
  //--------------------------------------------------------------------------- 
  public void clear_bad_analyses(){
    bad_obs_analyses = new ArrayList();	  
  }
  
  //  Dump out the estimated haplotypes for the observed data
  //---------------------------------------------------------------------------
  public void dump_haps ( int studynum )
  {
    process_haps(case_obs, case_cells, case_trailers, "case_haps_" + studynum);
    //process_haps2(case_obs, case_cells, case_trailers, "case_haps_" + studynum);
    process_haps(control_obs, control_cells, control_trailers, "control_haps_" + studynum);
    //process_haps2(control_obs, control_cells, control_trailers, "control_haps_" + studynum); 
  }
  
  //---------------------------------------------------------------------------
  private void process_haps ( int[][] gMatrix, int cells, int trailers, String fname)
  {
    for ( int i =0; i < cells; i++ )
    {
      int ppl = 16;
      if ( i == 0 && trailers > 0 )
      {
        ppl = trailers;  
      }
      for ( int j =0; j < ppl; j ++ )
      {
    	String h1 = "";
    	String h2 = "";
        for ( int k = 0; k < numloci; k++ )
        {
          h1 = h1 + (((gMatrix[k][(cells-1) - i] >> (j*2)) & 1 ) + 1);
          h2 = h2 + (((gMatrix[k][(cells-1) - i] >> ((j*2) + 1)) & 1 ) + 1);
        }
        write_haps(h1,h2, fname);
      }
    }
  }
  
  //---------------------------------------------------------------------------
  private void process_haps2 ( int[][] gMatrix, int cells, int trailers, String fname)
  {
	File freport = new File(fname + ".out");
	System.out.println(numloci);
    String h1="";
    String total = "";
    for ( int i =0; i < numloci; i++ )
    {
      for ( int j =0; j < cells; j ++ )
      {
        int ppl = 16;
        if ( j == 0 && trailers > 0 )
        {
          ppl = trailers;  
        }    	  
        for ( int k = 0; k < ppl; k++ )
        {
          h1 = "|" + (((gMatrix[i][(cells-1) - j] >> ((k*2) + 1)) & 1 ) + 1) + (((gMatrix[i][(cells-1) - j] >> (k*2)) & 1 ) + 1) + h1;
        }
      }
      total = total + h1 + "\n";
      h1 = "";
      System.out.print(total);
    }
    String s = "cases";
	  freport  = Ut.fExtended(s,"out");
	  PrintWriter pwReport;
    try {
	    pwReport = new PrintWriter(new BufferedWriter(new FileWriter(freport)), true);
		pwReport.println(total);
		pwReport.close();		
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
  }
  
  //---------------------------------------------------------------------------  
  private void write_haps ( String h1, String h2, String fname )
  {
	File freport = new File(fname + ".out");	    
	if ( freport.exists() )
	{
	  String filename = fname + ".out";
	  boolean append = true;
	  FileWriter fw;
	  try {
		fw = new FileWriter(filename,append);
		fw.write(h1 + "\n" + h2 + "\n");
	    fw.close();
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }	 
	}
	else
	{
	  freport  = Ut.fExtended(fname, "out");
	  PrintWriter pwReport;
	  try {
		pwReport = new PrintWriter(new BufferedWriter(new FileWriter(freport)), true);
		pwReport.println(h1 + "\n" + h2);
		pwReport.close();
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	}
  }
  
  //---------------------------------------------------------------------------  
  private void write_sims ( String outstr, String fname )
  {
	File freport = new File(fname + ".out");	    
	if ( freport.exists() )
	{
	  String filename = fname + ".out";
	  boolean append = true;
	  FileWriter fw;
	  try {
		fw = new FileWriter(filename,append);
		fw.write(outstr);
	    fw.close();
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }	 
	}
	else
	{
	  freport  = Ut.fExtended(fname, "out");
	  PrintWriter pwReport;
	  try {
		pwReport = new PrintWriter(new BufferedWriter(new FileWriter(freport)), true);
		pwReport.print(outstr);
		pwReport.close();
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	}
  }  
  
  public void file_sims(int simindex, int studynum)
  {
    store_sims(case_sims[simindex], "case_study"+studynum, simindex);
	store_sims(control_sims[simindex], "control_study"+ studynum, simindex);
  }
  
  public void store_sims(int[][] gMatrix, String fname, int simindex)
  {
    for ( int i = 0; i < numloci; i++ )
    {
      int[] row = gMatrix[i];
      String outstr = "";
      if(i == 0)
      {
        outstr = "Simulation:" + simindex + "\n" + gMatrix[i][0];   
      }
      else
      {
        outstr = "" + gMatrix[i][0];
      }
      for ( int k = 1; k < row.length; k++ )
      {
        outstr = outstr + "," + gMatrix[i][k];
      }
      write_sims(outstr+"\n", fname);
    }	  
  }
  
  public void loadnulls( int studyindex )
  {
	String cases_filename = "case_study" + studyindex + ".out";
	String controls_filename = "control_study" + studyindex + ".out"; 
    StringBuffer contents = new StringBuffer();

	//declared here only to make visible to finally clause
	BufferedReader cases_input = null;
	BufferedReader controls_input = null;
	try
	{
	  //use buffering, reading one line at a time
	  //FileReader always assumes default encoding is OK!
	  cases_input = new BufferedReader( new FileReader(cases_filename) );
	  read_storage(cases_input, "case");
	  controls_input = new BufferedReader( new FileReader(controls_filename) );
	  read_storage(controls_input, "control");
	}
	catch (FileNotFoundException ex) {
	  ex.printStackTrace();
	}
	catch (IOException ex){
	  ex.printStackTrace();
	}	  
  }
  
  public void read_storage( BufferedReader input, String affstatus ) throws IOException
  {
    String line = null;
    int lines_persim = numloci + 1;
    int line_counter = 0;
    int simindex = 0;
    int [][] m = null;
    while (( line = input.readLine()) != null )
    {
      if( line_counter == 0)
      {
        simindex = (int) Integer.parseInt(line.split(":")[1]);
        System.out.println("Reading in " + affstatus + " sim: " + simindex);
      }
      if ( line_counter == 1 && simindex == 0)
      {
        int cels = line.split(",").length;
        this.initiate_simStorage(cels,affstatus);
      }
      if( line_counter == 1 )
      {
        if(affstatus == "case")
        {
          m = case_sims[simindex];
        }
        else
        {
          m = control_sims[simindex];
        }    	  
      }
      if( line_counter > 0)
      {
        String[] values = line.split(",");
        int[] gts = new int[values.length];
        for( int i = 0; i < values.length; i++ )
        {
          gts[i] = Integer.parseInt(values[i]);
        }
        m[line_counter - 1] = gts;
      }
    	  
      if( line_counter == (lines_persim - 1))
      {
        line_counter = 0;
      }
      else
      {
        line_counter++;
      }
    }
  }
  
}

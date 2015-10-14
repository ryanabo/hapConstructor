package edu.utah.med.genepi.hc;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.stat.CCAnalysis.Table.Column;
import edu.utah.med.genepi.stat.ContingencyTable;
import edu.utah.med.genepi.util.Counter;

public class binAnalysis
{
  private compressGtype cGtype;
  //sim or obs
  private String        eval_type;
  //simulation cycle
  private int           sigrun;
  private int           simcycle;
  private String        model;
  private int[]         loci;
  private Counter[][]   theCounters;
  private Ptype[]       thePtypes;
  private Column[]      col_defs;
  private int           numcols;
  private int           numrows;
  private int[]         pid2RowIx = {0,1,0};
  private int[][]       case_simdata;
  private int[][]       control_simdata;
  private int[][]       case_obsdata;
  private int[][]       control_obsdata;
  private int           case_len;
  private int           control_len;
  
  //---------------------------------------------------------------------------
  public binAnalysis ( int significancerun, int simulationcycle, String datatype, Column[] col_def, String mymodel, int[] myloci, compressGtype gtypes )
  {
    cGtype = gtypes;
    sigrun = significancerun;
    eval_type = datatype;
    simcycle = simulationcycle;
    col_defs = col_def;
    model = mymodel;
    loci = myloci;
    // If simcycle is -1 and sigrun != -1, then set obs data to be the simulated set at index
    // sigrun. A simcycle == -1, means that you are calculating observed data.
	
    // If simcycle != -1, and sigruns > -1, then the update function should never be called
    // with the same simcycle and sigrun values.
    if ( simcycle == -1 && sigrun != -1 )
    {
      case_obsdata = cGtype.get_casesims()[sigrun];
      control_obsdata = cGtype.get_controlsims()[sigrun];
      case_len = case_obsdata[loci[0]].length;
      control_len = control_obsdata[loci[0]].length;
    }
    else if ( simcycle != -1 )
    {
      case_simdata = cGtype.get_casesims()[simcycle];
      control_simdata = cGtype.get_controlsims()[simcycle];
      case_len = case_simdata[loci[0]].length;
      control_len = control_simdata[loci[0]].length;
    }
    else if ( sigrun == -1 && simcycle == -1 )
    {
      case_obsdata = cGtype.get_caseobs();
      control_obsdata = cGtype.get_controlobs();
      case_len = case_obsdata[loci[0]].length;
      control_len = control_obsdata[loci[0]].length;
    }
	
    thePtypes = new Ptype[] {Ptype.CASE, Ptype.CONTROL};
    numcols = col_def.length;
    numrows = thePtypes.length;
    theCounters = create_counters(numrows, numcols);
    //int wt = col_def[0].getWeight();
  }
  
  //---------------------------------------------------------------------------
  public CCAnalysis.Table create_table ()
  {
    if ( eval_type.equals("sim") )
    {
      theCounters = process_table(case_simdata,cGtype.case_cells,cGtype.num_cases,cGtype.case_trailers,2);
      theCounters = process_table(control_simdata,cGtype.control_cells,cGtype.num_controls,cGtype.control_trailers,1);
	}
	else
	{
      theCounters = process_table(case_obsdata,cGtype.case_cells, cGtype.num_cases,cGtype.case_trailers,2);
      theCounters = process_table(control_obsdata,cGtype.control_cells, cGtype.num_controls,cGtype.control_trailers,1);	
    }
    return new ContingencyTable(thePtypes, col_defs, theCounters, pid2RowIx);
  }

  //---------------------------------------------------------------------------
  public Counter[][] process_table ( int[][] array,int cells,int num_indiv, int trailers, int aff )
  {
    int ttl = 0;
    int[] tbl_counts = new int[theCounters[0].length];
    
    if ( loci.length == 1 )
	{
      if ( model.equals("Dom") )
      {
        for ( int i=0; i < cells; i++ )
        {
          int n = array[loci[0]][(cells-1) - i];
          ttl = ttl + process_domgtypes(n,i,trailers);
        }
        tbl_counts[0] = num_indiv - ttl;
        tbl_counts[1] = ttl;
      }
      else if( model.equals("Rec") )
	  {
        for ( int i=0; i < cells; i++ )
        {
          int n = array[loci[0]][(cells-1) - i];
          ttl = ttl + process_recgtypes(n,i,trailers);
        }
        tbl_counts[0] = num_indiv - ttl;
        tbl_counts[1] = ttl;
      }
      else if ( model.equals("Additive") || model.equals("Global") )
      {
        for ( int i=0; i < cells; i++ )
        {
          int n = array[loci[0]][(cells-1) - i];
          tbl_counts = process_addgtypes(n,i,trailers,tbl_counts);
        }    
      }
      else
      {
        for ( int i=0; i < cells; i++ )
        {
          int n = array[loci[0]][(cells-1) - i];
          tbl_counts = process_allelegtypes(n,i,trailers,tbl_counts);
        }   
      }
      
      for ( int j = 0; j < tbl_counts.length; j++ )
      {
        if ( tbl_counts[j] > 0 )
        {
          theCounters[pid2RowIx[aff]][j].add(tbl_counts[j]);
        }
      }
      
      return theCounters;
    }
    else
    {
      String sLetter = model.substring(1, 2);
      String fLetter = model.substring(0, 1);
      
      if ( sLetter.equals("G") )
      {
        // Call specific global functions
        if ( fLetter.equals("H") )
        {
          int[][] haps = create_haps(loci.length);
          int[][] haps2 = create_haps(loci.length);
          int num_cols = (int) Math.pow(2.0, (int) Math.pow(2.0,loci.length));
          int[][] tbl_counter = new int[2][num_cols];
          int iter = 0;
		  
          for ( int i=0; i < (int) haps[0].length; i++ )
          {
            for ( int j=0; j < (int) haps2[0].length - i; j++ )
            {
              int genotype = 0;
              int first = ((haps[0][i] << 1) | haps[0][j+i]);
              int genotype_inverse = 0;
              boolean inverse_flg = false;
			  
              for ( int k=0; k < loci.length; k++)
              {
                genotype = genotype << 2 | ((haps[k][i] << 1) | haps[k][j+i]);
                if ( (((haps[k][i]) ^ haps[k][j+i]) ) == 1)
                {
                  if ( k == 0 )
                  {
                    genotype_inverse = genotype_inverse << 2 | (((haps[k][i] ^ 1) << 1)  | (haps[k][j+i] ^ 1));
                  }
                  else
                  {
                    genotype_inverse = genotype_inverse << 2 | (((haps[k][i] ^ 1) << 1)  | (haps[k][j+i] ^ 1));
                  }
                }
                else
			    {
                  if ( k == 0 )
                  {
                    genotype_inverse = first;
                  }
                  else
                  {
                    genotype_inverse = genotype_inverse << 2 | ((haps[k][i] << 1) | haps[k][j+i]);
                  }
                }
              }
              tbl_counter[1][genotype] = iter;
              
              if ( genotype_inverse > 0 )
              {
                tbl_counter[1][genotype_inverse] = iter;	
              }
              iter++;
            } 
          }
          tbl_counter = process_hglobals(tbl_counter,array,trailers,cells,num_indiv);
          process_tblcounter(tbl_counter,aff);
        }
        else if ( fLetter.equals("C") )
        {
          int[][] tbl_counter = create_Globals();
          tbl_counter = process_cglobals(tbl_counter,array,aff,trailers,cells,num_indiv);
          process_tblcounter(tbl_counter,aff);
        } 
        else if ( fLetter.equals("M") )
        {
          int[][] tbl_counter = new int[2][(int) Math.pow(2.0,loci.length)];
          for ( int i=0; i < Math.pow(2.0,loci.length); i++ )
          {
            tbl_counter[1][i] = i;
          }
          tbl_counter = process_mglobals(tbl_counter,array,trailers,cells,num_indiv);
          process_tblcounter(tbl_counter,aff);
        }
      } 
      else
      {
        if ( fLetter.equals("H") )
        {
          int h_num = Integer.parseInt(model.substring(4, model.length()));
          
          if ( sLetter.equals("D") )
          {
            int[][] tbl_counter = new int[2][2];
            tbl_counter[1][0] = 1;
            tbl_counter[1][1] = 0;
            if ( eval_type.equals("sim") )
            {
              tbl_counter = process_hdom(tbl_counter,array,aff,trailers,cells,num_indiv);
            }
            else
            {
              tbl_counter[0] = cGtype.get_hdom(aff,loci,h_num);
            }
            process_tblcounter(tbl_counter,aff);
          }
          else if ( sLetter.equals("R") )
          {
            int[][] tbl_counter = new int[2][2];
            tbl_counter[1][0] = 1;
            tbl_counter[1][1] = 0;
        	
            if ( eval_type.equals("sim") )
            {
              tbl_counter = process_hrec(tbl_counter,array,aff,trailers,cells,num_indiv);
            }
            else
            {
              tbl_counter[0] = cGtype.get_hrec(aff,loci,h_num);
            }
            process_tblcounter(tbl_counter,aff);
          }
          else if ( sLetter.equals("A") )
          {
            int[][] tbl_counter = new int[2][3];
            tbl_counter[1][0] = 2;
            tbl_counter[1][1] = 1;
            tbl_counter[1][2] = 0;
        	
            if ( eval_type.equals("sim") )
            {
              tbl_counter = process_hadd(tbl_counter,array,aff,trailers,cells,num_indiv);
            }
            else
            {
              tbl_counter[0] = cGtype.get_hadd(aff,loci,h_num);
            }
            process_tblcounter(tbl_counter,aff);
          }
        }
        else if ( fLetter.equals("C") )
        {
          String[] models = model.split("/");
          int col_len = 0;
//		  for(int i=0; i< models.length;i++){
//		    if(models[i].substring(1,2).equals("D") || models[i].substring(1,2).equals("R")){
//		      col_len++;
//		    }
//		    else{
//		      col_len = col_len+2;
//		    }
//		  }
//          int[][] tbl_counter = new int[2][col_len];
//          for(int j = 0; j < col_len; j++){
//            tbl_counter[1][j] = j;
//          }
          int[][] tbl_counter = new int[2][2];
          tbl_counter[1][0] = 0;
          tbl_counter[1][1] = 1;
          tbl_counter = process_cspec(tbl_counter,array,aff,trailers,cells,num_indiv);
          process_tblcounter(tbl_counter,aff);
        }
        else if ( fLetter.equals("M") )
        {
          int h_num = Integer.parseInt(model.substring(8, model.length()));
          int[][] tbl_counter = new int[2][2];
          tbl_counter[1][0] = 1;
          tbl_counter[1][1] = 0;
          if ( eval_type.equals("sim") )
          {
            tbl_counter = process_mspec(tbl_counter,array,aff,trailers,cells,num_indiv);	    
          }
          else
          {
            tbl_counter[0] = cGtype.get_mspec(aff,loci,h_num);
          }
          process_tblcounter(tbl_counter,aff);
        }
      }
      return theCounters;	  
    }
  }

  //---------------------------------------------------------------------------
  private void process_tblcounter ( int[][] tbl_counter, int aff )
  {
    // System.out.println("End model "+ model);
    for ( int j = 0; j < tbl_counter[1].length; j++ )
    {
      if ( tbl_counter[0][j] > 0 )
      {
        theCounters[pid2RowIx[aff]][tbl_counter[1][j]].add(tbl_counter[0][j]);
      }
    }
  }
  
  //---------------------------------------------------------------------------
  private int[][] process_cspec ( int[][] tbl_counters,int[][] array, int aff, int trailers, int cells, int num_indiv )
  { 
    String[] models = model.split("/");
    
    for ( int j = 0; j < cells; j ++)
    {
      int[] loci_results = new int[loci.length];
      
      if ( trailers > 0 && j == 0 )
      {
        for ( int p = 0; p < trailers; p++ ) 
        {
          int col = 0;
          int add_count = 0;
          boolean flg = false;
          int check = 1;
          for ( int i = 0; i < loci.length; i++ )
          {
            int n = array[loci[i]][(cells - 1)];
            if ( models[i].substring(1,2).equals("D") )
            {
              int s = ((n >> (p*2)) & 1) | ((n >> (p*2+1)) & 1);
              check = check & s;
              add_count++;
            }
            else if ( models[i].substring(1,2).equals("R") )
            {
              int s = ((n >> (p*2)) & 1) & ((n >> (p*2+1)) & 1);
              check = check & s;
              add_count++;
            }
            else
            {
              flg = true;
              int s = (((n >> (p*2)) & 1) + ((n >> (p*2+1)) & 1));
              int truth = 0;
              if ( s > 0 )
              {
                truth = 1;
              }
              check =  check & truth;
              add_count = add_count + s;
            }
          }
          if ( flg && (check == 1) )
          {
            col = add_count;//additive count
          }
          else
          {
            col = check;
          }
          tbl_counters[0][col]++;
        }
      }
      else
      {
        for ( int k = 0; k < 16; k++ )
        {
          int col = 0;
          int add_count = 0;
          boolean flg = false;
          int check = 1;
          for ( int i = 0; i < loci.length; i++ )
          {
            int n = array[loci[i]][(cells - 1) - j];
            if ( models[i].substring(1,2).equals("D") )
            {
              int s = ((n >> (k*2) & 1) | ((n >> (k*2+1)) & 1));
              check = check & s;
              add_count++;
            }
            else if ( models[i].substring(1,2).equals("R") )
            {
              int s = ((n >> (k*2) & 1) & ((n >> (k*2 +1)) & 1));
              check = check & s;
              add_count++;
            }
            else
            {
              flg = true;
              int s = ((n >> (k*2) & 1) + ((n >> (k*2+1)) & 1));
              int truth = 0;
              if ( s > 0 )
              {
                truth = 1;
              }
              check =  check & truth;
              add_count = add_count + s;
            }
          }
          if ( flg && (check == 1) )
          {
            col = add_count;//additive count
          }
          else
          {
            col = check;
          }
          tbl_counters[0][col]++;
        }
      }
    }
    return tbl_counters;
  }
  
  //---------------------------------------------------------------------------
  private int[][] process_mspec ( int[][] tbl_counters, int[][] array, int aff, int trailers, int cells, int num_indiv )
  { 
    int h_num = Integer.parseInt(model.substring(8, model.length()));
    int[][] haps = create_haps(loci.length);
    for ( int j = 0; j < cells; j ++ )
    {
      int[] loci_results = new int[loci.length];
      if ( trailers > 0 && j == 0 )
      {
        for ( int i = 0; i < loci.length; i++ )
        {
          int n = array[loci[i]][(cells - 1)];
          if ( haps[i][h_num - 1] == 0 )
          {
            loci_results[i] = ~n;
          }
          else
          {
            loci_results[i] = n;
          }
        }
        int final_n = loci_results[0];
        
        for ( int lr = 1; lr < loci_results.length; lr++ )
        {
          final_n = final_n & loci_results[lr];
        }
        
        for ( int p = 0; p < 2*trailers; p++ )
        {
          tbl_counters[0][(final_n >> p) & 1]++;
        }
      }
      else
      {
        for ( int i = 0; i < loci.length; i++ ) 
        {
          int n = array[loci[i]][(cells - 1) - j];
          if ( haps[i][h_num - 1] == 0 )
          {
            loci_results[i] = ~n;
          }
          else
          {
            loci_results[i] = n;
          }
        }
        int final_n = loci_results[0];
        
        for ( int lr = 1; lr < loci_results.length; lr++ )
        {
          final_n = final_n & loci_results[lr];
        }
        for ( int k = 0; k < 32; k++ )
        {
          tbl_counters[0][(final_n >> k) & 1]++;
        }
	  }
	}
    return tbl_counters;
  }
  
  //---------------------------------------------------------------------------
  private int[][] process_hrec ( int[][] tbl_counters, int[][] array, int aff, int trailers, int cells, int num_indiv )
  {
	// To count the recessives the idea is to take the haplotype of interest and try to find the number of alleles
	// matching the haplotype of interest at each loci in the haplotype. So the genotypes for each locus in the haplotype
	// and the genotypes are made to have 1's to represent the allele of interest, then ANDed with the subsequent loci.
	// For example, if the haplotype of interest is 1-1 (0-0), at SNP 1, the genotypes would be inversed and ANDed with
	// the inverse genotypes at SNP 2. This would provide the number of people with the haplotype.
	// The final number is taken and read out two bits at a time (one person's genotype). For recessive, both bits have to
	// be one to count the person in the risk cell.
    int h_num = Integer.parseInt(model.substring(4, model.length()));
    int[][] haps = create_haps(loci.length);
    
    for ( int j = 0; j < cells; j++ )
    {
      int[] loci_results = new int[loci.length];
	    
      if ( trailers > 0 && j == 0 )
      {      
        for ( int i = 0; i < loci.length; i++ )
        {
          int n = array[loci[i]][(cells - 1)];
          if ( haps[i][h_num - 1] == 0 )
          {
            loci_results[i] = ~n;
          }
          else
          {
            loci_results[i] = n;
          }
        }
        int final_n = loci_results[0];
        for ( int lr = 1; lr < loci_results.length; lr++ )
        {
          final_n = final_n & loci_results[lr];
        }
        for ( int p = 0; p < trailers; p++ )
        {
          tbl_counters[0][((final_n >> (p*2)) & 1) & (((final_n >> (p*2)+1)) & 1)]++;
        }
      }
      else
      {
        for ( int i = 0; i < loci.length; i++ )
        {
          int n = array[loci[i]][(cells - 1) - j];
          if ( haps[i][h_num - 1] == 0 )
          {
            loci_results[i] = ~n;
          }
          else
          {
            loci_results[i] = n;
          }
        }
        int final_n = loci_results[0];
        for ( int lr = 1; lr < loci_results.length; lr++ )
        {
          final_n = final_n & loci_results[lr];
        }
        for ( int k = 0; k < 16; k++ )
        {
          tbl_counters[0][((final_n >> (k*2)) & 1) & ((final_n >> ((k*2)+1)) & 1)]++;
        }
	  }
  	}
  	return tbl_counters;  
  }
  
  //---------------------------------------------------------------------------
  private int[][] process_hadd ( int[][] tbl_counters,int[][] array, int aff, int trailers, int cells, int num_indiv )
  { 
    int h_num = Integer.parseInt(model.substring(4, model.length()));
    int[][] haps = create_haps(loci.length);
    
    for ( int j = 0; j < cells; j++ )
    {
      int[] loci_results = new int[loci.length];
      if ( trailers > 0 && j == 0 )
      {
        for ( int i = 0; i < loci.length; i++ )
        {
          int n = array[loci[i]][(cells - 1)];
          if ( haps[i][h_num - 1] == 0 )
          {
            loci_results[i] = ~n;
          }
          else
          {
            loci_results[i] = n;
          }
        }
        int final_n = loci_results[0];
        for ( int lr = 1; lr < loci_results.length; lr++ )
        {
          final_n = final_n & loci_results[lr];
        }
        for ( int p = 0; p < trailers; p++ )
        {
          tbl_counters[0][((final_n >> (p*2)) & 1) + ((final_n >> ((p*2)+1)) & 1)]++;
        }
      }
      else
      {
        for ( int i = 0; i < loci.length; i++ )
        {
          int n = array[loci[i]][(cells - 1) - j];
          if ( haps[i][h_num - 1] == 0 )
          {
            loci_results[i] = ~n;
          }
          else
          {
            loci_results[i] = n;
          }
        }
        int final_n = loci_results[0];
        for ( int lr = 1; lr < loci_results.length; lr++ )
        {
          final_n = final_n & loci_results[lr];
        }
        for( int k = 0; k < 16; k++ )
        {
          tbl_counters[0][((final_n >> (k*2)) & 1) + ((final_n >> ((k*2)+1)) & 1)]++;
        }
      }
    }
    return tbl_counters;  
  }    
  
  //---------------------------------------------------------------------------
  private int[][] process_hdom ( int[][] tbl_counters, int[][] array, int aff, int trailers, int cells, int num_indiv )
  { 
    int h_num = Integer.parseInt(model.substring(4, model.length()));
    int[][] haps = create_haps(loci.length);
    
    for ( int j = 0; j < cells; j++ )
    {
      int[] loci_results = new int[loci.length];
      
      if ( trailers > 0 && j == 0 )
      {
        for ( int i = 0; i < loci.length; i++ )
        {
          int n = array[loci[i]][(cells - 1)];
          if ( haps[i][h_num - 1] == 0 )
          {
            loci_results[i] = ~n;
          }
          else
          {
            loci_results[i] = n;
          }
        }
        int final_n = loci_results[0];
	  	    
        for ( int lr = 1; lr < loci_results.length; lr++ )
        {
          final_n = final_n & loci_results[lr];
        }
        
        for ( int p = 0; p < trailers; p++ )
        {
          tbl_counters[0][((final_n >> (p*2)) & 1) | ((final_n >> ((p*2)+1)) & 1)]++;
        }
      }
      else
      {
        for ( int i = 0; i < loci.length; i++ )
        {
          int n = array[loci[i]][(cells - 1) - j];
          if ( haps[i][h_num - 1] == 0 )
          {
            loci_results[i] = ~n;
          }
          else
          {
            loci_results[i] = n;
          }
        }
        int final_n = loci_results[0];
        for ( int lr = 1; lr < loci_results.length; lr++ )
        {
          final_n = final_n & loci_results[lr];
        }          
        for ( int k = 0; k < 16; k++ )
        {
          tbl_counters[0][((final_n >> (k*2)) & 1) | ((final_n >> ((k*2)+1)) & 1)]++;
        }
      }
    }
    return tbl_counters;  
  }  
  
  //---------------------------------------------------------------------------
  private int[][] process_hglobals ( int[][] tbl_counters,int[][] array, int trailers, int cells, int num_indiv )
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
            previous = (previous << 2) | n;
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
            previous = (previous << 2) | n;
          }
          tbl_counters[0][previous]++;
        } 
      }
    }
    return tbl_counters;  
  }
  
  //---------------------------------------------------------------------------
  private int[][] process_mglobals ( int[][] tbl_counters,int[][] array, int trailers, int cells, int num_indiv ) 
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
  private int[][] process_cglobals ( int[][] tbl_counters,int[][] array, int aff, int trailers, int cells, int num_indiv )
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
  
  //--------------------------------------------------------------------
  // Returns a 2d array with the possible haplotypes given the step. If step
  // is 2 then there are 4 possible haplotypes.
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
  
  //-----------------------------------------------------------------------------------------
  public int[] process_allelegtypes ( int gtypes, int iter, int trailer, int[] tbl_counter )
  {
    if ( iter == 0 )
    {
      for ( int j=0; j < trailer*2; j++ )
      {
        tbl_counter[(gtypes >> j) & 1]++;
      }
    }
    else
    {
      tbl_counter[(gtypes >> 0) & 1]++;
      tbl_counter[(gtypes >> 1) & 1]++;
      tbl_counter[(gtypes >> 2) & 1]++;
      tbl_counter[(gtypes >> 3) & 1]++;
      tbl_counter[(gtypes >> 4) & 1]++;
      tbl_counter[(gtypes >> 5) & 1]++;
      tbl_counter[(gtypes >> 6) & 1]++;
      tbl_counter[(gtypes >> 7) & 1]++;
      tbl_counter[(gtypes >> 8) & 1]++;
      tbl_counter[(gtypes >> 9) & 1]++;
      tbl_counter[(gtypes >> 10) & 1]++;
      tbl_counter[(gtypes >> 11) & 1]++;
      tbl_counter[(gtypes >> 12) & 1]++;
      tbl_counter[(gtypes >> 13) & 1]++;
      tbl_counter[(gtypes >> 14) & 1]++;
      tbl_counter[(gtypes >> 15) & 1]++;
      tbl_counter[(gtypes >> 16) & 1]++;
      tbl_counter[(gtypes >> 17) & 1]++;
      tbl_counter[(gtypes >> 18) & 1]++;
      tbl_counter[(gtypes >> 19) & 1]++;
      tbl_counter[(gtypes >> 20) & 1]++;
      tbl_counter[(gtypes >> 21) & 1]++;
      tbl_counter[(gtypes >> 22) & 1]++;
      tbl_counter[(gtypes >> 23) & 1]++;
      tbl_counter[(gtypes >> 24) & 1]++;
      tbl_counter[(gtypes >> 25) & 1]++;
      tbl_counter[(gtypes >> 26) & 1]++;
      tbl_counter[(gtypes >> 27) & 1]++;
      tbl_counter[(gtypes >> 28) & 1]++;
      tbl_counter[(gtypes >> 29) & 1]++;
      tbl_counter[(gtypes >> 30) & 1]++;
      tbl_counter[(gtypes >> 31) & 1]++;
    }
    return tbl_counter;
  }
  
  //-----------------------------------------------------------------------------------------  
  public int process_domgtypes ( int gtypes, int iter, int trailer )
  {
    int total = 0;
    if ( iter == 0 )
    {
      for ( int j=0; j < trailer; j++ )
      {
        total = total + get_trailers(j*2,gtypes);
      }
    }
    else
    {
      total = total + ((gtypes >> 0 & 1) | ((gtypes >> 1) & 1));
      total = total + (((gtypes >> 2) & 1) | ((gtypes >> 3) & 1));
      total = total + (((gtypes >> 4) & 1) | ((gtypes >> 5) & 1));
      total = total + (((gtypes >> 6) & 1) | ((gtypes >> 7) & 1));
      total = total + (((gtypes >> 8) & 1) | ((gtypes >> 9) & 1));
      total = total + (((gtypes >> 10) & 1) | ((gtypes >> 11) & 1));
      total = total + (((gtypes >> 12) & 1) | ((gtypes >> 13) & 1));
      total = total + (((gtypes >> 14) & 1) | ((gtypes >> 15) & 1));
      total = total + (((gtypes >> 16) & 1) | ((gtypes >> 17) & 1));
      total = total + (((gtypes >> 18) & 1) | ((gtypes >> 19) & 1));
      total = total + (((gtypes >> 20) & 1) | ((gtypes >> 21) & 1));
      total = total + (((gtypes >> 22) & 1) | ((gtypes >> 23) & 1));
      total = total + (((gtypes >> 24) & 1) | ((gtypes >> 25) & 1));
      total = total + (((gtypes >> 26) & 1) | ((gtypes >> 27) & 1));
      total = total + (((gtypes >> 28) & 1) | ((gtypes >> 29) & 1));
      total = total + (((gtypes >> 30) & 1) | ((gtypes >> 31) & 1));
    }
    return total;
  }

  //-----------------------------------------------------------------------------------------  
  public int process_recgtypes ( int gtypes, int iter, int trailer ) 
  {
    int total = 0;
    
    if ( iter == 0 )
    {
      for ( int j=0; j < trailer; j++ )
      {
        total = total + rec(bit(gtypes,j*2),bit(gtypes,(j*2) + 1));
      }
    }
    else
    {
      total = total + ((gtypes & 1) & ((gtypes >> 1) & 1));
      total = total + (((gtypes >> 2) & 1) & ((gtypes >> 3) & 1));
      total = total + (((gtypes >> 4) & 1) & ((gtypes >> 5) & 1));
      total = total + (((gtypes >> 6) & 1) & ((gtypes >> 7) & 1));
      total = total + (((gtypes >> 8) & 1) & ((gtypes >> 9) & 1));
      total = total + (((gtypes >> 10) & 1) & ((gtypes >> 11) & 1));
      total = total + (((gtypes >> 12) & 1) & ((gtypes >> 13) & 1));
      total = total + (((gtypes >> 14) & 1) & ((gtypes >> 15) & 1));
      total = total + (((gtypes >> 16) & 1) & ((gtypes >> 17) & 1));
      total = total + (((gtypes >> 18) & 1) & ((gtypes >> 19) & 1));
      total = total + (((gtypes >> 20) & 1) & ((gtypes >> 21) & 1));
      total = total + (((gtypes >> 22) & 1) & ((gtypes >> 23) & 1));
      total = total + (((gtypes >> 24) & 1) & ((gtypes >> 25) & 1));
      total = total + (((gtypes >> 26) & 1) & ((gtypes >> 27) & 1));
      total = total + (((gtypes >> 28) & 1) & ((gtypes >> 29) & 1));
      total = total + (((gtypes >> 30) & 1) & ((gtypes >> 31) & 1));
    }
    return total;
  }
  
  //-----------------------------------------------------------------------------------------  
  public int[] process_addgtypes ( int gtypes, int iter, int trailer, int[] tbl_counts )
  {
	if ( iter == 0 )
	{
      for ( int j=0; j < trailer; j++ )
      {
        tbl_counts[(((gtypes >> j*2) & 1) + ((gtypes >> (j*2)+1) & 1))]++;
      }
    }
    else
    {
      tbl_counts[((gtypes & 1) + ((gtypes >> 1) & 1))]++;
      tbl_counts[(((gtypes >> 2) & 1) + ((gtypes >> 3) & 1))]++;
      tbl_counts[(((gtypes >> 4) & 1) + ((gtypes >> 5) & 1))]++;
      tbl_counts[(((gtypes >> 6) & 1) + ((gtypes >> 7) & 1))]++;
      tbl_counts[(((gtypes >> 8) & 1) + ((gtypes >> 9) & 1))]++;
      tbl_counts[(((gtypes >> 10) & 1) + ((gtypes >> 11) & 1))]++;
      tbl_counts[(((gtypes >> 12) & 1) + ((gtypes >> 13) & 1))]++;
      tbl_counts[(((gtypes >> 14) & 1) + ((gtypes >> 15) & 1))]++;
      tbl_counts[(((gtypes >> 16) & 1) + ((gtypes >> 17) & 1))]++;
      tbl_counts[(((gtypes >> 18) & 1) + ((gtypes >> 19) & 1))]++;
      tbl_counts[(((gtypes >> 20) & 1) + ((gtypes >> 21) & 1))]++;
      tbl_counts[(((gtypes >> 22) & 1) + ((gtypes >> 23) & 1))]++;
      tbl_counts[(((gtypes >> 24) & 1) + ((gtypes >> 25) & 1))]++;
      tbl_counts[(((gtypes >> 26) & 1) + ((gtypes >> 27) & 1))]++;
      tbl_counts[(((gtypes >> 28) & 1) + ((gtypes >> 29) & 1))]++;
      tbl_counts[(((gtypes >> 30) & 1) + ((gtypes >> 31) & 1))]++;
    }
    return tbl_counts;
  }  
  
  //-----------------------------------------------------------------------------------------   
  public int bit ( int gtype, int pos )
  {
    return ((gtype >> pos) & 1);
  }
  
  //-----------------------------------------------------------------------------------------   
  public int rec ( int bit1, int bit2 )
  {
    int bit = bit1 & bit2;
    return bit;
  }
  
  //-----------------------------------------------------------------------------------------   
  public int get_trailers ( int j, int gtypes )
  {
    int v = (gtypes >> j);
    int t = (gtypes >> j+1);
    int value = ((v & 1) | (t & 1));
    return value;
  }
  
  //-----------------------------------------------------------------------------------------   
  public Counter[][] create_counters ( int nRows, int nCols )
  {
    Counter[][] counters = new Counter[nRows][nCols];
    for ( int irow = 0; irow < nRows; irow++ )
    {
      for ( int icol = 0; icol < nCols; icol++ )
      {
        counters[irow][icol] = new Counter();
        counters[irow][icol].set(0);
      }
    }
    return counters;
  }
   
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
	
  //-----------------------------------------------------------------------------------------   
  private int[][] create_Globals ()
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
  
//	private List<List> create_Globals() {
//		List<String> g1 = new ArrayList<String>();
//		List<String> g2 = new ArrayList<String>();
//		List<String> g3 = new ArrayList<String>();
//		g1.add("1/1");
//		g2.add("(1/2)|(2/1)");
//		g3.add("2/2");
//		List<List> master = new ArrayList<List>();
//		master.add(g1);
//		master.add(g2);
//		master.add(g3);
//		List<List> el = new ArrayList<List>();
//		el.addAll(master);
//		List<List> allM = new ArrayList<List>();
//		allM.add(el);
//		for (int i = 0; i < (step - 1); i++) {
//			List<List> clonnie = new ArrayList<List>();
//			clonnie.addAll(el);
//			allM.add(clonnie);
//		}
//		return cursey(allM);
//	}
//
//	private List cursey(List lCursey) {
//		if (lCursey.size() == 1) {
//			return lCursey;
//		} else {
//			return cursey(combineEs(lCursey));
//		}
//	}
//
//	private List<List> combineEs(List<List> lCurse) {
//		List<List> l1 = lCurse.get(0);
//		List<List> l2 = lCurse.get(1);
//		lCurse.remove(0);
//		List combed = new ArrayList();
//		for (int i = 0; i < l1.size(); i++) {
//			List it = l1.get(i);
//			for (int j = 0; j < l2.size(); j++) {
//				List clonnie = new ArrayList();
//				clonnie.addAll(it);
//				clonnie.add(l2.get(j).get(0));
//				combed.add(clonnie);
//			}
//		}
//		lCurse.add(0, combed);
//		lCurse.remove(1);
//		return lCurse;
//	}
  
}

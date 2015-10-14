//******************************************************************************
// StatUt.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.util.MatrixInverter;

//==============================================================================
class StatUt {

  private static final double  NORMAL95 = 1.96;
  private static final boolean DEBUG = false;

  //----------------------------------------------------------------------------
  //static double chiSquared(int[] r, int R, int[] n, int N)
  static double chiSquared( Number[] r,
                            Number rtotal, 
                            Number[] ctotal,
                            Number tabletotal)
  {
    double sum = 0.0;
    
    /*
    int R = rtotal.intValue();
    int N = tabletotal.intValue();
      
    for (int i = 0; i < r.length; ++i)
      sum += r[i].intValue() * r[i].intValue() / (double) ctotal[i].intValue();
    */
  
    double R = rtotal.doubleValue();
    double N = tabletotal.doubleValue();
    for ( int i = 0; i < r.length; i++ )
      sum += r[i].doubleValue() * r[i].doubleValue() / ctotal[i].doubleValue();
    
    return (N * N * (sum - R * R / (double) N)) / R / (N - R);
  }

  //----------------------------------------------------------------------------
  //static double chiSquaredTrend(int[] r, int R, int[] n, int N, int[] x)
  static double chiSquaredTrend( Number[] r, Number R, Number[] n,
                                 Number N, int[] x )
  {
    //int newR = R.intValue();
    //int newN = N.intValue();
    //int RX = 0, NX = 0, NX2 = 0;
    double newR = R.doubleValue();
    double newN = N.doubleValue();
    double RX = 0.0, NX = 0.0, NX2 = 0.0;

    for (int i = 0; i < r.length; ++i)
    {
      //int x_i = x[i];
      //int nx = n[i].intValue() * x_i;
      double nx = n[i].doubleValue() * x[i];

      //RX += r[i].intValue() * x_i;
      RX += r[i].doubleValue() * x[i];
      NX += nx;
      NX2 += nx * x[i];
    }

    //double A = RX - newR / (double) newN * NX;
    double A = newN * RX - newR * NX;

    if (DEBUG)
      A = Math.abs(A) - 0.5; // continuity correction

    //return A * A /
    //       (newR * (newN - newR) /
    //       (double) (newN * newN * (newN - 1)) * (newN * NX2 - NX * NX));
    double aa = newR * ( newN - newR ) ;
    double bb = newN * NX2 - NX * NX;

    return (double)( newN * ( A * A ) / (aa * bb) ); 
            
  }

  //----------------------------------------------------------------------------
  static double[] oddsRatios(Number[] row1, Number[] row2, int refcol_index)
  {
    final double a = row1[refcol_index].doubleValue();
    final double b = row2[refcol_index].doubleValue();

    double[] ratios = new double[row1.length - 1];
    for (int icol = 0, iratio = 0; icol < row1.length; icol++)
      if (icol != refcol_index)
      {
        //ratios[iratio] = a * row2[icol] / (double) (b * row1[icol]);
        ratios[iratio] =  b * row1[icol].doubleValue() /
                          (double) (a * row2[icol].doubleValue()) ;
        iratio++;
      }

    return ratios;
  }
  
  //----------------------------------------------------------------------------
  static double InteractionOddsRatios(Number[] row1, Number[] row2, int refcol_index)
  {
    final double a = row1[refcol_index].doubleValue();
    final double b = row2[refcol_index].doubleValue();

    double[] ratios = new double[row1.length - 1];
    for (int icol = 0, iratio = 0; icol < row1.length; icol++)
      if (icol != refcol_index)
      {
        //ratios[iratio] = a * row2[icol] / (double) (b * row1[icol]);
        ratios[iratio] =  b * row1[icol].doubleValue() /
                          (double) (a * row2[icol].doubleValue()) ;
        iratio++;
      }
    return ( ratios[2] / ( ratios[0] * ratios[1] ));
  }

  //----------------------------------------------------------------------------
  /* Nicola Camp instructions for new formula (replacing *K183 below):
     "Here are the correct equations:
      lowerCI= exp(ln(OR)-1.96*sd)    [NOTE: ln =natural logarithm]
      upperCI=exp(ln(OR)+1.96*sd)
      sd=sqrt((1/a)+(1/b)+1/c)+(1/d)), where a,b,c and d are the 4
         observed values in the table for which we are calculating the OR."
  */
  //static double[][] oddsRatiosWithConfidence(int[] row1, int[] row2,
  static double[][] oddsRatiosWithConfidence( Number[] row1, Number[] row2,
                                             int refcol_index)
  {
    final int a = row1[refcol_index].intValue();
    final int b = row2[refcol_index].intValue();

    double[][] triplets = new double[row1.length - 1][5];
    for (int icol = 0, iratio = 0; icol < row1.length; ++icol)
      if (icol != refcol_index)
      {
        int c = row1[icol].intValue();
        int d = row2[icol].intValue();

        //double ratio = a * d / (double) (b * c);
        double ratio = b * c / (double) (a * d);
        double lnor = Math.log(ratio);
        double delta = NORMAL95 * Math.sqrt(1.0/a + 1.0/b + 1.0/c + 1.0/d);

        // add icol number and reference column # to the result 
        triplets[iratio][0] = icol + 1;
        triplets[iratio][1] = refcol_index + 1;
        triplets[iratio][2] = ratio;
        triplets[iratio][3] = Math.exp(lnor - delta);
        triplets[iratio][4] = Math.exp(lnor + delta);
        ++iratio;
      }

    return triplets;
  }

  //----------------------------------------------------------------------------
  static double[][] oddsRatiosWithConfidenceK183(int[] row1, int[] row2,
                                                 int refcol_index,
                                                 double chi_sq)
  {
    final double   plusminus = NORMAL95 / Math.sqrt(chi_sq);
    final double   expon_lo = 1 - plusminus;
    final double   expon_hi = 1 + plusminus;
    Number[] newrow1 = new Number[row1.length];
    Number[] newrow2 = new Number[row2.length];

    for ( int i = 0; i < row1.length; i++ )
      newrow1[i] = new Integer(row1[i]);
      
    for ( int i = 0; i < row2.length; i++ )
      newrow1[i] = new Integer(row1[i]);

    final double[] or = oddsRatios(newrow1, newrow2, refcol_index);

    double[][] triplets = new double[or.length][3];
    for (int ior = 0; ior < or.length; ++ior)
    {
      double ratio = or[ior];
      triplets[ior][0] = ratio;
      triplets[ior][1] = Math.pow(ratio, ratio > 1.0 ? expon_lo : expon_hi);
      triplets[ior][2] = Math.pow(ratio, ratio > 1.0 ? expon_hi : expon_lo);
    }

    return triplets;
  }

  //----------------------------------------------------------------------------
  static double[] quantitative (  Number[] columnTotals,
				  Number[] cellsqcolTotals,
				  Number tableTotal,
				  Number cellsqtableTotal,
				  int[] coltotalCounts,
				  int refcol_index )
  {
    int nCounts = 0;
    int nStatistic;
    final int nColumns   = columnTotals.length;
    final double[] means = new double[nColumns];
    final double[] se    = new double[nColumns];
    double residualSS = 0.0;
  
    if ( nColumns > 2 )
      nStatistic = nColumns;
    else 
      nStatistic = nColumns - 1;

    final double[] statistic = new double[nStatistic];

    for ( int i = 0; i < nColumns; i++ )
    {
      nCounts += coltotalCounts[i];
      means[i] = columnTotals[i].doubleValue() / coltotalCounts[i] ;
      double colsumsq = columnTotals[i].doubleValue() * 
                        columnTotals[i].doubleValue() /
                        coltotalCounts[i];
      se[i]    = Math.sqrt ( (cellsqcolTotals[i].doubleValue() - colsumsq )
		  / (double) ( coltotalCounts[i] - 1 ) );
      residualSS += ( ( coltotalCounts[i] - 1 ) * se[i] * se[i] );
    }

    for ( int nCol = 0, i = 0; nCol < nColumns ; nCol++ )
    {
      if ( nCol != refcol_index ) 
      {
        statistic[i] = ( means[nCol] - means[refcol_index] ) /
  		         Math.sqrt( (se[nCol] * se[nCol]) / 
                         coltotalCounts[nCol] + 
		         (se[refcol_index] * se[refcol_index]) / 
			 coltotalCounts[refcol_index] );
        i++;
      }
    }

    if ( nColumns > 2 )
    {
      double totalSS = cellsqtableTotal.doubleValue() -
                         (tableTotal.doubleValue() * tableTotal.doubleValue())/
                         nCounts;
      double betweenSS = totalSS - residualSS;
      statistic[nColumns - 1] = ( betweenSS / (nColumns - 1) ) /
		  	      ( residualSS / (nCounts - nColumns)) ;
    }
    return statistic;
  }
  //----------------------------------------------------------------------------
  public static double trioTDT ( int cellb, int cellc )
  {
    final double statistic = ( cellb - cellc ) * ( cellb - cellc ) / 
                       ( double ) ( cellb + cellc );
    return statistic; 
  }

  //----------------------------------------------------------------------------
  public static double[] sibTDT ( Number[] columnTotals )
  {
    double mean = columnTotals[0].doubleValue();
    double var  = columnTotals[1].doubleValue();
    int y = columnTotals[2].intValue();
    double z = ( Math.abs(y - mean) - 0.5) / Math.sqrt(var);
    return new double[] {z, mean, var, y}; 
  }

  //----------------------------------------------------------------------------
  public static double combTDT ( Number[] columnTotals )
  {
    double mean = columnTotals[0].doubleValue();
    double var  = columnTotals[1].doubleValue();
    int w = columnTotals[2].intValue();
    double z = ( w - mean ) / Math.sqrt(var);
    return z;
  }

  //----------------------------------------------------------------------------
  public static double metaChiSquared ( Number[] aa,
                                        Number[] ee, 
                                        Number[] ff,
                                        Number[] gg,
                                        int[]    hh,
                                        Number[] nn )
  {
    double sumA = 0.0;
    double sumExpectedA = 0.0;
    double sumVarianceA = 0.0;
    double mantelHaenszel = 0.0;
    for ( int i = 0 ; i < aa.length; i++ )
    { 
      double a = aa[i].doubleValue();
      double e = ee[i].doubleValue();
      double f = ff[i].doubleValue();
      double g = gg[i].doubleValue();
      int    h = hh[i];
      double n = nn[i].doubleValue();
      if ( n != 0.0 )
      {
        sumA += a;
        sumExpectedA += e * g / n;
        sumVarianceA += (e * f * g * h) / 
                        ( n * n * (n - 1) );
      }
    }
    double diff = (double) Math.abs( sumA - sumExpectedA );
    mantelHaenszel = (diff * diff) / sumVarianceA;
    return mantelHaenszel;
  }

  //----------------------------------------------------------------------------
  public static double cmhChiSquared ( Number[][] casecells,
                                       Number[][] controlcells,
                                       Number[][] rTotals,
                                       Number[][] cTotals,
                                       Number[]   tTotal,
                                       int        constantJ )
  {
    int           nTable = tTotal.length;
    int             nRow = rTotals[0].length;
    int             nCol = cTotals[0].length;
    int              nRC = nRow * nCol;
    double[]           G = new double[(nRow-1)*(nCol-1)];
    double[][] constantB = new double[(nRow-1)*(nCol-1)][nRow*nCol]; 
    int[][]    rowScores = new int[nRow - 1][nRow];
    int[][]    colScores = new int[nCol - 1][nCol];
    double[][]      varG = new double[nCol-1][nCol-1];

    // row & column scores, (nRow-1 or nCol-1) identity matrix by a 1's vector 
    for ( int i = 0; i < nRow - 1; i++ )
    {
      for ( int j = 0 ; j < nRow; j++ )
      {
        rowScores[i][j] = 0;
        if ( i == j ) 
          rowScores[i][j] = 1;
        if ( j == nRow - 1 )
          rowScores[i][j] = constantJ;
      }
    }
    for ( int i = 0 ; i < nCol - 1; i++ )
    {
      for ( int j = 0; j < nCol; j++ )
      {
        colScores[i][j] = 0;
        if ( i == j )
          colScores[i][j] = 1;
        if ( j == nCol - 1 )
          colScores[i][j] = constantJ;
      }
    }

    // initialize G and varG
    for ( int i = 0; i < G.length; i++ )
      G[i] = 0.0;
    for ( int i = 0; i < nCol - 1; i++ )
    {
      for ( int j = 0; j < nCol - 1; j++ )
        varG[i][j] = 0.0;
    }

    for ( int t = 0; t < nTable; t++ )
    {
      double[]       m = new double[nRC];
      double[]       n = new double[nRC];
      double[][]  dRow = new double[nRow][nRow];
      double[][]  dCol = new double[nCol][nCol];
      double[][]   var = new double[nRC][nRC];
      double[][] varg1 = new double[constantB.length][nRC];
      double    ttotal = tTotal[t].doubleValue();
      double constantC = (ttotal * ttotal) / (ttotal - 1);

      // put all observed in order
      for ( int i = 0; i < nCol; i++ )
        n[i] = casecells[t][i].doubleValue();

      for ( int i = 0; i < nCol; i++ )
        n[i + nCol] = controlcells[t][i].doubleValue();

      for ( int r = 0; r < nRow; r++ )
      {
        double rtotal = rTotals[t][r].doubleValue() / ttotal;
        // calculate expected
        for ( int c = 0; c < nCol; c++ )
          m[r * nCol + c] = ttotal * ( rtotal * 
                            (cTotals[t][c].doubleValue() / ttotal));

        
        // DiagR 
        for ( int rr = 0; rr < nRow; rr++ )
        {         
          double diagR = 0.0;
          if ( r == rr )
            diagR = rtotal;
          dRow[r][rr] = diagR - ( rtotal * 
                        (rTotals[t][rr].doubleValue() / ttotal )) ;
        }
      }

      for ( int c = 0; c < nCol; c++ )
      {
        for ( int cc = 0; cc < nCol; cc++ )
        {
          double diagC = 0.0;
          double ctotal = cTotals[t][c].doubleValue() / ttotal;
          if ( c == cc )
            diagC = ctotal;
          // DiagC
          dCol[c][cc] = diagC - ( ctotal 
                        * (cTotals[t][cc].doubleValue() / ttotal) );
        }
      }

      // calculate var[n|Ho]
      for ( int r = 0; r < dRow.length; r++ )
      {
        for ( int rr = 0 ; rr < dRow[r].length; rr++ )
        {
          for ( int c = 0; c < dCol.length; c++ )
          {
            for ( int cc = 0; cc < dCol[c].length; cc++ )
            {
              var[r * dCol.length + c][rr * dCol[c].length + cc] =
                  constantC * dRow[r][rr] * dCol[c][cc];
              //int f = r*dCol.length+c;
              //int s = rr *dCol[c].length +cc;
            }
          }
        }
      }

      // calculate constantB based on row and column scores
      for ( int c1 = 0; c1 < nCol - 1; c1++ )
        for ( int c2 = 0 ; c2 < nCol; c2++ )
          for ( int r1 = 0; r1 < nRow - 1; r1++ )
            for ( int r2 = 0; r2 < nRow; r2++ )
              constantB[r1*(nCol-1)+c1][r2*nCol+c2] = 
               (new Integer(colScores[c1][c2]*rowScores[r1][r2])).doubleValue();
    
      // calculate G, constantB * ( observed - expected )
      for ( int i = 0; i < constantB.length; i++ )
      {
        for ( int j = 0; j < constantB[i].length; j++ )
          G[i] += constantB[i][j] * (n[j] - m[j]);
      }

      // calculate varG; 
      // First calculate constantB * var[n|Ho]
      for ( int i = 0; i < varg1.length; i++ )
        for ( int j = 0; j < nRC; j++ )
          for ( int k = 0; k < nRC; k++ ) 
            varg1[i][j] += constantB[i][k] * var[k][j];

      // Then calculate varg1 * constantB Transpose
      for ( int i = 0 ; i < varg1.length; i++ )
      {
        for ( int j = 0; j < constantB.length; j++ )
        {
          for ( int k = 0 ; k < nRC; k++ )
            varG[i][j] += varg1[i][k] * constantB[j][k];
        }
      }
    }  // end of table loop

    // G Transpose * inverse ( varG) * G
    try 
    {
      MatrixInverter matrixInverter = new MatrixInverter();
      double[][] transvarG = matrixInverter.inverse(varG);

      // varG should be a nRC x nRC matrix
      double[] first = new double[varG.length];
      for ( int i = 0; i < varG.length; i++ )
      {
        first[i] = 0.0;
        for ( int k = 0; k < G.length; k++ )
          first[i] += G[k] * transvarG[k][i];
      }

      double qcmh = 0.0;
      for ( int i = 0; i < first.length; i++ )
        qcmh += first[i] * G[i]; 

      return qcmh;
    }
    catch ( RuntimeException exception )
    { throw exception; }

  }

  //----------------------------------------------------------------------------
  public static double metaChiSqTrend ( Number[][] caseCells,
                                        Number[][] columnTotal,
                                        Number[]   caseTotal,
                                        Number[]   tableTotal,
                                        int[]      columnWeights )
  {
    double sumA         = 0.0;
    double sumB         = 0.0;
    
    for ( int i = 0 ; i < tableTotal.length; i++ )
    {
      double cellWt       = 0.0;
      double colTotalWt   = 0.0;
      double colTotalWtSq = 0.0;
      for ( int j = 0 ; j < caseCells[i].length; j++ )
      {
        cellWt       += caseCells[i][j].doubleValue() * columnWeights[j];
        colTotalWt   += columnTotal[i][j].doubleValue() * columnWeights[j];
        colTotalWtSq += columnTotal[i][j].doubleValue() * 
                        columnWeights[j] * columnWeights[j];
      }
      double ct = caseTotal[i].doubleValue();
      double tt = tableTotal[i].doubleValue();

      sumA += cellWt - (ct / tt) * colTotalWt; 
      sumB += ( (ct * (tt - ct) ) / (tt * tt * (tt - 1)) ) * 
              ( tt * colTotalWtSq - colTotalWt * colTotalWt );
    }
    return ( sumA * sumA ) / (double) sumB;
  }

  //----------------------------------------------------------------------------
  public static double cmhChiSqTrend  ( Number[][] caseCells,
                                          Number[][] controlCells,
                                          Number[][] rTotals,
                                          Number[][] cTotals,
                                          Number[]   tTotal,
                                          int        constantJ,
                                          int[]      weights        )
  {
    int 	nTable     = tTotal.length;
    int 	nRow       = rTotals[0].length;
    int 	nCol       = cTotals[0].length;
    int 	nRC        = nRow * nCol;
    double[][]  cells      = new double[nRow][nCol];
    double[]    cMarg      = new double[nTable]; // col marginal proportions
    double[]    sVar       = new double[nTable]; // total variance with respect
                                                 // to score (weights) 
    double[]    tableMS    = new double[nRow];
    double[][]  diag       = new double[nRow][nRow];
    double[]    meanscores = new double[nRow];
    double[]    expectedMS = new double[nRow];
    double[][]  varF       = new double[nRow][nRow];
    double[][]  constantC  = new double[nRow-1][nRow];
    double[]    d          = new double[nRow-1];
    
    // initialize meanscore, expectedMS
    for ( int i = 0; i < nRow; i++ )
    {
      meanscores[i] = 0.0;
      expectedMS[i] = 0.0;
      for ( int j = 0; j < nRow; j++ )
        varF[i][j] = 0.0;
    }
 
    // initialize constantC, re: Landis, Heyman and Koch 
    for ( int i = 0; i < nRow - 1; i++ )
    {
      for ( int j = 0; j < nRow; j++ )
      {
        constantC[i][j] = 0.0;
        if ( i == j )
          constantC[i][j] = 1.0;
        if ( j == nRow - 1 )
          constantC[i][j] = -1.0; 
      }
    }

    for ( int i = 0; i < nTable; i++ )
    {
      // convert tTotal to ttotal
      double ttotal = tTotal[i].doubleValue();
  
      // initialize Ph*.Ph*. Transpose for each table
      double[][] rowMargP = new double[nRow][nRow];

      // put all individuals in an array
      for ( int j = 0; j < nCol; j++ )
        cells[0][j] = caseCells[i][j].doubleValue();
      for ( int j = 0; j < nCol; j++ )
        cells[1][j] = controlCells[i][j].doubleValue();

      // calculate mean scores on column marginal proportions
      cMarg[i] = 0.0;
      for ( int j = 0; j < nCol; j++ )
        cMarg[i] += weights[j] * cTotals[i][j].doubleValue() / ttotal; 
    
      // calculate sVar 
      sVar[i] = 0.0;
      for ( int j = 0; j < nCol; j++ )
        sVar[i] += Math.pow((weights[j]-cMarg[i]), 2) * 
                   cTotals[i][j].doubleValue() / ttotal;


      // initialize diagonal matrix
      for ( int j = 0; j < nRow; j++ )
      { 
        for ( int k = 0; k < nRow; k++ )
        {
          diag[j][k] = 0.0;
          if ( j == k )
            diag[j][k] = rTotals[i][j].doubleValue() / ttotal; 
        }
      }
      

      for ( int j = 0; j < nRow; j++ )
      {
        tableMS[j] = 0.0;
        for ( int k = 0; k < nCol; k++ )
          tableMS[j] += weights[k] * cells[j][k];
        tableMS[j] = tableMS[j] / rTotals[i][j].doubleValue();
       
        for ( int k = 0; k < nRow; k++ )
          meanscores[j] += ttotal * diag[j][k] * tableMS[k];
        
        //meanscores[j] = meanscores[j] * ttotal;
    
        // calculate expected mean scores 
        expectedMS[j] += ttotal * cMarg[i] * rTotals[i][j].doubleValue() / ttotal;

        // calculate Ph*.Ph*.Transpose
        for ( int k = 0; k < nRow; k++)
          rowMargP[j][k] = rTotals[i][j].doubleValue() / ttotal * 
                           rTotals[i][k].doubleValue() / ttotal ;
        
        for ( int k = 0; k < nRow; k++ )
          varF[j][k] += (ttotal * ttotal) / (ttotal - 1) * 
                        sVar[i] * (diag[j][k] - rowMargP[j][k]);
      }
    }  // end nTable loop

    // differences between the mean scores and their expected values
    for ( int i = 0; i < nRow - 1; i++ )
    {
      d[i] = 0.0;
      for ( int j = 0; j < nRow; j++ )
        d[i] += constantC[i][j] * (meanscores[j] - expectedMS[j]);
    }
 
    // inverse [constantC * varF * constantC Transpose]
    double[][] first = new double[nRow-1][nRow];
    for ( int i = 0; i < nRow - 1; i++ )
      for ( int j = 0; j < nRow; j++ )
      {
        for ( int k = 0; k < nRow; k++ )
          first[i][j] += constantC[i][k] * varF[k][j];
      }

    double[][] second = new double[nRow-1][nRow-1];
    for ( int i = 0; i < nRow - 1; i++ )
      for ( int j = 0; j < nRow - 1; j++ )
      {
        for ( int k = 0; k < nRow; k++ )
           second[i][j] += first[i][k] * constantC[j][k];

      }
     try
     {
       MatrixInverter matrixInverter = new MatrixInverter();
       double[][] transSecond = matrixInverter.inverse(second);

       double[] part1 = new double[second.length];
       for ( int i = 0; i < transSecond.length; i++ )
       {
         part1[i] = 0.0;
         for ( int j = 0; j < d.length; j++ )
           part1[i] += d[j] * transSecond[j][i];
       }
       
       double qcmms = 0.0;
       for ( int i = 0; i < part1.length; i++ )
         qcmms += part1[i] * d[i];
       
 
       return qcmms;
     }
     catch ( RuntimeException exception )
     { throw exception; }

  }

  //----------------------------------------------------------------------------
  public static double metaInteractionOR( Number[][] caseCells,
                                          Number[][] controlCells,
                                          Number[][] ctotal,
                                          //Number[]   tableTotal,
                                          int        refcol_index )
  {
    double[] or = metaOddsRatios ( caseCells, 
                                   controlCells,
                                   ctotal,
                                   refcol_index );
    return ( or[2] / ( or[0] * or[1] ));
  }

  //----------------------------------------------------------------------------
  public static double[] metaOddsRatios ( Number[][] caseCells,
                                          Number[][] controlCells,
                                          Number[][] ctotal,
                                          //Number[]   tableTotal,
                                          int        refcol_index )
  {
    int nratio = caseCells[0].length - 1; 
    int nTable = ctotal.length;
    double[] sumAD  = new double[nratio];
    double[] sumBC  = new double[nratio];
    double[] ratios = new double[nratio];
    
    for ( int i = 0; i < nTable; i++ )
    {
      double caseRef    = caseCells[i][refcol_index].doubleValue();
      double controlRef = controlCells[i][refcol_index].doubleValue();
      for ( int icol = 0, iratio = 0; icol <= nratio; icol++ )
      {
        if ( icol != refcol_index )
        {
          double casecell    = caseCells[i][icol].doubleValue();
          double controlcell = controlCells[i][icol].doubleValue();
          double ttotal      = ctotal[i][icol].doubleValue()
                               + ctotal[i][refcol_index].doubleValue();
          
          //double ad = caseRef * controlcell / (double) ttotal;
          //double bc = controlRef * casecell / (double) ttotal;

          if ( ttotal != 0 )
          {
            sumAD[iratio]     += caseRef * controlcell / ttotal;
            sumBC[iratio]     += controlRef * casecell / ttotal;
          }
          iratio++;
        }
      }
    }
    for ( int iratio = 0; iratio < nratio; iratio++ )
      ratios[iratio] = sumBC[iratio] / sumAD[iratio];
    return ratios;
  }

  //----------------------------------------------------------------------------
  public static double[] metaQtestOR ( Number[][] caseCells,
                                       Number[][] controlCells,
                                       Number[][] ctotal,
                                       int        refcol_index )
  {
    double[] metaOR = metaOddsRatios( caseCells, controlCells,
                                      ctotal, refcol_index );
    //System.out.println("metaOR : " + metaOR[0]);
    //System.out.println("log metaOR : " + Math.log(metaOR[0]));
    double[] logMetaOR = new double[metaOR.length];
    for ( int i = 0; i < metaOR.length; i++ )
    {
      logMetaOR[i] = Math.log(metaOR[i]);
    //System.out.println("log metaOR : " + Math.log(metaOR[i]));
    }
      
    int nratio = caseCells[0].length - 1;
    int nTable = ctotal.length;
    double[] ratios = new double[nratio];

    for ( int i = 0; i < nTable; i++ )
    {
      //System.out.println("TAble : " + i);
      double caseRef    = caseCells[i][refcol_index].doubleValue();
      double controlRef = controlCells[i][refcol_index].doubleValue();
      for ( int icol = 0, iratio = 0; icol <= nratio; icol++ )
      {
        if ( icol != refcol_index )
        {
          double casecell    = caseCells[i][icol].doubleValue();
          double controlcell = controlCells[i][icol].doubleValue();
          double r = Math.log((controlRef * casecell)/(caseRef * controlcell));
          double v = 1/caseRef + 1/casecell + 1/controlRef + 1/controlcell;
          //System.out.println("r and metaOR " + r + " " + logMetaOR[iratio]);
          ratios[iratio] += (Math.pow((r - logMetaOR[iratio]),2)/ v);
          //System.out.println("r : " + r);
          //System.out.println("v : " + v );
          //System.out.println("ratio : " + ratios[0]);
          iratio++;
        }
      }
    }
    return ratios;
  }

  //----------------------------------------------------------------------------
  public static boolean checkRowColSum ( Number caseRowTotal, 
  	                                 Number[] columnTotals,
         	                         Number tableTotal )
  {
    //Number[] colTotals = columnTotals;
    //int controlRowTotal = tableTotal.intValue() - caseRowTotal.intValue();
    double controlRowTotal = tableTotal.doubleValue() - 
                             caseRowTotal.doubleValue();
    boolean returnValue = true;
    
    if ( controlRowTotal == 0.0 || caseRowTotal.doubleValue() == 0.0 )
      returnValue = false;

    for ( int i = 0; i < columnTotals.length; i++ )
    { 
      if ( columnTotals[i].doubleValue() == 0.0 )
        returnValue = false;
    } 
   
    return returnValue;

  }
  //----------------------------------------------------------------------------
  public static boolean checkRefCell ( Number[] casecells, 
                                       Number[] controlcells,
                                       int refcol_index )
  {
    boolean returnValue = true;
    final int a = casecells[refcol_index].intValue();
    final int b = controlcells[refcol_index].intValue();
    if ( a == 0 || b == 0 )
      returnValue = false;  
    return returnValue;
  }
  //----------------------------------------------------------------------------
  public static void main(String[] args)
  {
    Integer a = new Integer(22);
    Integer b = new Integer(21);
    Integer c = new Integer(17); 
    Integer d = new Integer(15); 
    Integer e = new Integer(75);
    Integer f = new Integer(35);
    Integer g = new Integer(43);
    Integer h = new Integer(58);
    Integer i = new Integer(29);
    Integer j = new Integer(165);
    Integer k = new Integer(36);
    Integer l = new Integer(80);
    Integer m = new Integer(171);
    Integer n = new Integer(226);
    Integer p = new Integer(186);
    Integer q = new Integer(583);
    Integer r = new Integer(10);
    Integer s = new Integer(20);
    Integer t = new Integer(583);

    System.out.println("---");

    System.out.println("Testing stat utility routines.  Pages references are");
    System.out.println("to formulae in Kirkwood (1988).  Data used are from");
    System.out.println("the associated examples in the same text.");
    System.out.println();

    System.out.print("Chi-squared (p.93): ");
    System.out.println(
      chiSquared(
        //new int[] {22, 21, 17, 15}, 75, new int[] {35, 43, 58, 29}, 165
        new Number[] {a, b, c, d}, e, new Number[] {f, g, h, i}, j
      )
    );

    String qualif = DEBUG ? "" : " w/o continuity correction";
    System.out.print("Chi-squared Trend (p.103" + qualif + "): ");
    System.out.println(
      chiSquaredTrend(
        //new int[] {15, 29, 36}, 80, new int[] {171, 226, 186}, 583,
        new Number[] {d, i, k}, l, new Number[] {m, n, p}, q,
        new int[] {1, 2, 3}
      )
    );

    double[] triplet = oddsRatiosWithConfidenceK183(
      new int[] {22, 10}, new int[] {10, 20}, 0, 26.4
    )[0];
    System.out.print("Odds Ratio 95% CI (p.183): ");
    System.out.println(
      triplet[0] + " (" + triplet[1] + ", " + triplet[2] + ")"
    );

    triplet = oddsRatiosWithConfidence(
      new Number[] {a, r}, new Number[] {r, s}, 0
    )[0];
    System.out.print("Odds Ratio 95% CI (new formula): ");
    System.out.println(
      triplet[0] + " (" + triplet[1] + ", " + triplet[2] + ")"
    );

    System.out.println("---");
  }

  //----------------------------------------------------------------------------
  static double[] hwe (Number[] row1, Number[] row2,
                       double[] totals )
  {
    Number[][] cell = new Number[2][3];
    cell[0] = row1;
    cell[1] = row2;
    double[] results = new double[2];
   
    for ( int r = 0; r < 2; r++ )
    {
      double rowTotal = 4 * totals[r];
      double a = cell[r][0].doubleValue();
      double b = cell[r][1].doubleValue();
      double c = cell[r][2].doubleValue();

      double Ea = Math.pow((2*a + b), 2) /rowTotal;
      double Eb = 2 * ( 2*a + b) * ( b + 2*c) /rowTotal;
      double Ec = Math.pow((2*c + b), 2) /rowTotal;
      results[r] = Math.pow((a-Ea), 2) / Ea   
                   + Math.pow((b-Eb), 2) / Eb 
                   + Math.pow((c-Ec), 2) / Ec ;
    }
    return results;
  } 
}

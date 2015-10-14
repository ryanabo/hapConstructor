package edu.utah.med.genepi.genie;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Calendar;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.analysis.AnalysisReport;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.stat.QuantitativeTable;
import edu.utah.med.genepi.stat.ResultImp;
import edu.utah.med.genepi.util.Ut;

public class GenieReporter {
	
	private static final int TABLE_WIDTH  = 75;
	private static final int LABEL_FWIDTH = 15;
	private static final int COL_FWIDTH   = 15;
	private static final int DIV_FWIDTH   = 3;
	
	private static int numSim;
	private static Calendar inDate;
	private PrintWriter pwReport, pwSummary, pwSimulated;
	private String[] studyname, peddatasrc;
	private static final NumberFormat numberformat = NumberFormat.getInstance();
	private String pathStem = null;
	private GenieDataSet gs = null;

	//---------------------------------------------------------------------------
	public GenieReporter( GenieDataSet genieDataSet, Analysis[] analyses ) throws IOException
	{
		gs = genieDataSet;
		String reporttype = genieDataSet.getReportType();
		numSim = genieDataSet.getNSims();
		numberformat.setMaximumFractionDigits(numSim + 1);
		pathStem = genieDataSet.getoutpathStem();

		int nStudy = genieDataSet.getNStudy();
		studyname = new String[nStudy];
		peddatasrc = new String[nStudy];
		for ( int i = 0; i < nStudy; i++ ) 
		{
			studyname[i] = gs.getStudyName(i);
			peddatasrc[i] = gs.getDataSourceName(i);
		}

		if ( "summary".equals(reporttype) ) summaryReport(analyses);
		//else if ( "simulated".equals(reporttype) ) simulatedReport(analyses);
		else if ( "both".equals(reporttype) ) {
			standardReport(analyses);
			summaryReport(analyses);
		} 
		else standardReport(analyses);
		
		System.out.println("Analyses Completed Successfully");
	}

	//---------------------------------------------------------------------------
	private void summaryReport( Analysis[] a ) throws IOException
	{
		File fsummary = Ut.fExtended(pathStem,"summary");
		pwSummary = new PrintWriter(new BufferedWriter(new FileWriter(fsummary)),true);
		for ( int i = 0; i < a.length; i++ ) summaryCCAnalysis(a[i]);
		closeReport(pwSummary,fsummary);
	}
	
	//---------------------------------------------------------------------------
//	private void simulatedReport( Analysis[] a ) throws IOException
//	{
//		File fsimulated = Ut.fExtended(pathStem,"simulated");
//		pwSimulated = new PrintWriter(new BufferedWriter(new FileWriter(fsimulated)), true);
//		for ( int i = 0; i < a.length; i++ ) simulatedCCAnalysis(a[i]);
//		closeReport(pwSimulated,fsimulated);
//	}
	
	//---------------------------------------------------------------------------
	private void standardReport( Analysis[] a ) throws IOException
	{
		File freport  = Ut.fExtended(pathStem, "report");
		pwReport  = new PrintWriter(new BufferedWriter(new FileWriter(freport)),true);
		reportHeaderInfo();
		for ( int i = 0; i < a.length; i++ ) reportCCAnalysis(a[i],i+1);
		putElapseTime();
		closeReport(pwReport,freport);
	}
	
	//----------------------------------------------------------------------------
	public void reportCCAnalysis(Analysis a, int index)
	{
		int[] loci = a.getLoci();
		String[] markerNames = new String[loci.length];
		for ( int i = 0; i < loci.length; i++ ) 
		{
			String markerName = gs.getLocus(-1,loci[i]).getName();
			if ( markerName.length() == 0 ) markerName = "Marker " + loci[i];
			markerNames[i] = markerName;
		}

		String tableName = "";
		if ( a.getTallierType().equals("psuedo") ) tableName = "   Table Type : Psuedo";
		pwReport.println();
		pwReport.println(Ut.fit(" ", TABLE_WIDTH + 6, '_', false));
		pwReport.print(
				Ut.fit(" ", DIV_FWIDTH, '*', false)
				+ "Analysis " + index
				+ Ut.fit(" ", DIV_FWIDTH, '*', true) 
				+ tableName
				+ "   Loci : " + Ut.array2str(markerNames," ")
				+ "   Model : " + a.getModel()
				+ "   Type : " + a.getTableType() );
		pwReport.println();

		AnalysisTable[] aTables = a.getTables();
		for ( int i=0; i < aTables.length; i++ )
		{
			pwReport.println();
			pwReport.println(
					Ut.fit(" ", DIV_FWIDTH, '*', false)
					+ "Study : " + studyname[i] );
			pwReport.println();

			int tablenamesize = aTables[i].getTableName().length();
			int tabpadsize = ( TABLE_WIDTH - tablenamesize + 2) / 2;
			pwReport.println(Ut.fit(" ", tabpadsize, '=', false) +  
					aTables[i].getTableName() +
					Ut.fit(" ", tabpadsize, '=', true ));
			putColumnDefinitions(aTables[i]);
			putTableContents(aTables[i]);
			pwReport.println();
			if ( aTables.length == 1 ) putFullStatReports(a.getReport());
		}
		if ( aTables.length > 1 )
		{
			pwReport.println();
			String header = "Meta / CMH Analysis";
			int headersize = header.length();
			int headerpadsize = ( TABLE_WIDTH - headersize + 2 ) / 2;
			pwReport.println(Ut.fit(" ", headerpadsize, '_', false) + 
					header + Ut.fit(" ", headerpadsize, '_', true));
			putFullStatReports(a.getReport());
		}
	}

	//----------------------------------------------------------------------------
	public void summaryCCAnalysis ( Analysis a )
	{
		int[] loci = a.getLoci();
		String[] markerNames = new String[loci.length];
		for ( int i = 0; i < loci.length; i++ ) 
		{
			String markerName = gs.getLocus(-1,loci[i]).getName();
			if ( markerName.length() == 0 ) markerName = "Marker " + loci[i];
			markerNames[i] = markerName;
		}
		pwSummary.print(Ut.array2str(markerNames," ") + "  " + a.getModel() + "  " + gs.getRSeed() + "  ");

		AnalysisTable[] aTables = a.getTables();
		for ( int i=0; i < aTables.length; i++ )
		{
			pwSummary.print(" " + studyname[i] + " ");
			if ( aTables.length > 1 ) putSummaryStatReports( a.getReport() );
		}
		if ( aTables.length > 1 )
		{
			putSummaryStatReports( a.getReport() );
		}
		pwSummary.println(" ");
	}

	//----------------------------------------------------------------------------
//	public void simulatedCCAnalysis ( Analysis a )
//	{
//		CCAnalysis.Table[][][][] tables = a.getObservedTable();
//		CCAnalysis.Table[][][] metaTDTtables = a.getObsMetaTDTTable();
//		int nMetaReports = a.getMetaReports(0,0).length;
//		int nMetaTDTReports = a.getMetaTDTReports(0,0).length;
//
//		int nrepeat = a.getRepeat();
//		String markers = new String();
//		for ( int r = 0; r < nrepeat; r++ ) {
//			int[] loci_id = a.getLoci(r);
//			for ( int i = 0; i < loci_id.length; i++ ) {
//				String markerName = gdef.getLocus(loci_id[i]).getMarker();
//				if ( markerName.length() == 0 )
//					markerName = "Marker " + loci_id[i];
//				markers += markerName + " ";
//			}
//			pwSimulated.print(markers + "  " + a.getModel() + "  " + seed + "  ");
//
//			for ( int t = 0; t < tables[r].length; t++ ) {
//				for ( int s = 0; s < tables[r][t].length; s++ )
//					putSimulatedStatReports( a.getStatReports(r, t, s) );
//
//				if ( nMetaReports > 0 )
//					putSummaryStatReports( a.getMetaReports(r, t) );
//				if ( nMetaTDTReports > 0 )
//					putSummaryStatReports( a.getMetaTDTReports(r, t) );
//				pwSummary.println(" ");
//			}
//		}
//	}
	
	
	//---------------------------------------------------------------------------
	void reportHeaderInfo()
	{
		System.out.println("\nWriting Report.....");
		pwReport.println("********** " + gs.getAppId() + gs.getAppVersion() + " Report **********");
		pwReport.println("Created: " + gs.getInCal().getTime());
		pwReport.println("Specification from: " + gs.getRgenFileName());
		pwReport.println("Data from: " + peddatasrc[0]);
		int nStudy = gs.getNStudy();
		for ( int i = 1; i < nStudy; i++ ) pwReport.println("           " + gs.getDataSourceName(i));
		pwReport.println();
	}
	
	//----------------------------------------------------------------------------
	private void putColumnDefinitions( AnalysisTable t )
	{
		int colheadsize = (t.getColumnHeading()).length();
		int colpadsize = ( TABLE_WIDTH - colheadsize - LABEL_FWIDTH + 2) / 2 ;
		pwReport.println();
		pwReport.println(Ut.ljust(t.getRowHeading(), LABEL_FWIDTH) + " " +
				Ut.fit(" ", colpadsize, '-', false) + t.getColumnHeading() + 
				Ut.fit(" ", colpadsize, '-', true));
		pwReport.print(Ut.ljust(" ", LABEL_FWIDTH) +  " ");

		for (int i = 0, n = t.getColumnCount(); i < n; ++i)
		{
			pwReport.print(Ut.rjust( t.getColumnLabelAt(i), COL_FWIDTH ));
		}
		String tempTotal = "TOTAL";
		if ( t instanceof QuantitativeTable )
			tempTotal = "Row Total";
		pwReport.println(Ut.rjust(tempTotal, COL_FWIDTH));
		pwReport.println();
	}

	//----------------------------------------------------------------------------
	private void putTableContents( AnalysisTable t )
	{
		for (int irow = 0; irow < t.getRowCount(); ++irow)
		{
			AnalysisTable.Row row = t.getRowAt(irow);
			Number rowtotal = new Double(0.0);
			if ( row != null )
			{
				Number[] rowCells = row.getCells();
				String[] extraInfo = new String[rowCells.length];
				if ( t instanceof QuantitativeTable )
				{
					Number[] tempCells  = row.getCells();
					int[]    tempCellsN = row.getCellsN();

					for ( int i = 0; i < rowCells.length; i++ )
					{
						rowCells[i] = new Double(tempCells[i].doubleValue() / (double) tempCellsN[i]);
						if ( Double.isNaN(tempCells[i].doubleValue()))
							rowCells[i] = new Double(0.0); 
						rowtotal = new Double(rowtotal.doubleValue() + rowCells[i].doubleValue());
						extraInfo[i] = "/" + tempCellsN[i];
					}
				}
				else
					rowtotal = t.getTotals().forRows()[irow];
				putTableContentLine(
						//row.getPtype().toString(), row.getCells(), t.getTotals().forRows()[irow]
						row.getLabel(), rowCells, extraInfo, rowtotal);
			}
		}

		if ( t.getRowAt(0) != null )
		{
			Number[] cTotals = t.getTotals().forColumns();
			String[] extraTotalInfo = new String[cTotals.length];
			Number gTotal = t.getTotals().forTable();
			String tempTotal = "TOTAL";
			// Quantitative Table special case!
			if ( t instanceof QuantitativeTable )
			{
				Number[] colTotals = cTotals;
				gTotal = new Double(0.0);
				tempTotal = "Col TOTAL";
				QuantitativeTable q = (QuantitativeTable) t;
				QuantitativeTable.TotalsExt qtotals = ( QuantitativeTable.TotalsExt ) q.getTotals();
				int[] colcount = qtotals.forCol_counts();
				for ( int i = 0; i < q.getColumnCount(); i++ )
				{
					colTotals[i] = new Double(cTotals[i].doubleValue() / (double) colcount[i]);  
					if ( Double.isNaN(cTotals[i].doubleValue()) )
						colTotals[i] = new Double(0.0);
					cTotals[i] = colTotals[i];
					gTotal = new Double(gTotal.doubleValue() + colTotals[i].doubleValue());
				}
			}
			pwReport.println("--");
			putTableContentLine(tempTotal, cTotals, extraTotalInfo, gTotal );
		}

		if ( t.getAttachMessage() != null ) pwReport.println(t.getAttachMessage());
		pwReport.println();
	}
	
	//----------------------------------------------------------------------------
	private void putTableContentLine(String label, Number[] vals, 
			String[] extraInfo, Number total)
	{
		pwReport.print(Ut.ljust(label, LABEL_FWIDTH) + ":");
		for (int i = 0; i < vals.length; ++i)
		{
			//  pwReport.print(Ut.rjust(vals[i], COL_FWIDTH));
			String printval;
			if ( vals[i] instanceof Double )
				printval = new ResultImp.Real(vals[i].doubleValue()).toString();
			else 
				printval = String.valueOf(vals[i]);

			if ( extraInfo[i] != null )
				printval = printval + extraInfo[i];

			pwReport.print(Ut.rjust(printval, COL_FWIDTH));
		}
		String printtotal = " ";
		if ( total != null )
		{
			if ( total instanceof Double )
				printtotal = new ResultImp.Real(total.doubleValue()).toString();
			else 
				printtotal = String.valueOf(total);
		}
		pwReport.println(Ut.rjust(printtotal, COL_FWIDTH));
	}

	//----------------------------------------------------------------------------
	private void putFullStatReports(AnalysisReport[] reports)
	{
		for (int i = 0; i < reports.length; ++i)
		{
			int[] validSim = reports[i].getNumSimulationReport(); 
			pwReport.print(reports[i].getTitle() + " : "); 
			if ( validSim.length > 0  && !gs.getDistribution().equals("weightedindex") )
			{
				for ( int j = 0; j < validSim.length; j++ ) 
				{
					pwReport.print( validSim[j] + " / " + numSim );
					if ( validSim.length > 1 && j != (validSim.length - 1) )
						pwReport.print(", ");
				}
				pwReport.println(" total statistics calculated");
			}
			else 
				pwReport.println();
			pwReport.println("   Observed statistic : " + 
					setMaxDigits(reports[i].getObservationalReport()));
			if ( reports[i].getObsExtraReport() != null )
				pwReport.println("   " + setMaxDigits(reports[i].getObsExtraReport()));
			pwReport.println("   " + reports[i].getCompStatTitle() + 
					setMaxDigits(reports[i].getInferentialReport()));
			if ( reports[i].getInfExtraReport() != null )
				pwReport.println("   " + reports[i].getInfExtraStatTitle() +
						" : " + setMaxDigits(reports[i].getInfExtraReport()));
			if ( reports[i].getWarning() != null )
				pwReport.println("   " + reports[i].getWarning());
		}
	}

	//----------------------------------------------------------------------------
	private void putSummaryStatReports(AnalysisReport[] reports)
	{
		for (int i = 0; i < reports.length; ++i)
		{
			pwSummary.print(" " + setMaxDigits(reports[i].getObservationalReport()));
			pwSummary.print(" " + setMaxDigits(reports[i].getInferentialReport()));
			if ( reports[i].getInfExtraReport() != null )
				pwSummary.print(" " + setMaxDigits(reports[i].getInfExtraReport()));
		}
	}

	//----------------------------------------------------------------------------
//	private void putSimulatedStatReports(CCStatRun.Report[] reports)
//	{
//		for (int i = 0; i < reports.length; ++i)
//		{
//			pwSimulated.print(" obs " +
//					setMaxDigits(reports[i].getObservationalReport()));
//			//pwSimulated.print(" inf " + reports[i].getInferentialReport());
//			//if ( reports[i].getInfExtraReport() != null )
//			//  pwSummary.print(" " + reports[i].getInfExtraReport());
//			pwSimulated.println();
//
//			if ( reports[i].getInfSimReport() != null )
//			{
//				Vector<Vector<Double>> v = reports[i].getInfSimReport();
//				int nv = v.size();
//				int ncv = v.get(0).size();
//				for (int k = 0; k < ncv; k++ )
//				{
//					pwSimulated.println();
//					//pwSimulated.print("sim : " + k + " " );
//					for ( int j = 0; j < nv; j++ )
//					{
//						double value = v.get(j).get(k);
//						pwSimulated.print(" " + value);
//					}
//				}
//			}
//			pwSimulated.println();
//		}
//	}
	
	//----------------------------------------------------------------------------
	void putElapseTime()
	{
		long[] times = getElapsedTime();
		pwReport.println();
		pwReport.println("Elapse Time : " + times[0] + " h : " + 
				times[1] + " m : " +
				times[2] + " s" );
	}
	//----------------------------------------------------------------------------
	void closeReport(PrintWriter pw, File file)
	{
		pw.close();
		System.out.println("Report written to '" + file + "'." + Ut.N);
	}
	
	//----------------------------------------------------------------------------
	public long[] getElapsedTime()
	{
		long outSecs  = Calendar.getInstance().getTimeInMillis();
		long inSecs   = gs.getInCal().getTimeInMillis();
		long diffSecs = outSecs - inSecs; 
		long hours    = diffSecs / ( 1000 * 60 * 60 );
		long mins     = diffSecs / ( 1000 * 60 ) - hours * 60;
		long secs     = diffSecs / 1000 - mins * 60; 

		return new long[] { hours, mins, secs };
	}
	
	//----------------------------------------------------------------------------
	public String setMaxDigits(String str)
	{
		return str;
	}

	//----------------------------------------------------------------------------
	public String setMaxDigits(double val)
	{  
		if ( Double.isNaN(val) || Double.isInfinite(val) )
			return "-";
		else
			return numberformat.format(val);
	}
}

//******************************************************************************
// Reporter.java
//******************************************************************************
package edu.utah.med.genepi.app.rgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.stat.CCAnalysis;
import edu.utah.med.genepi.stat.CCStatRun;
import edu.utah.med.genepi.stat.CombTDTTable;
import edu.utah.med.genepi.stat.QuantitativeTable;
import edu.utah.med.genepi.stat.ResultImp;
import edu.utah.med.genepi.stat.SibTDTTable;
import edu.utah.med.genepi.util.Ut;

//==============================================================================
public class Reporter {

	private static final int TABLE_WIDTH  = 75;
	private static final int LABEL_FWIDTH = 15;
	private static final int COL_FWIDTH   = 15;
	private static final int DIV_FWIDTH   = 3;
	private static int numSim;
	private static Calendar inDate;
	private PrintWriter pwReport, pwSummary, pwSimulated;
	private String[] studyname, peddatasrc;
	private GDef gdef;
	private Map mparams;
	private int seed;
	private static final NumberFormat numberformat = NumberFormat.getInstance();
	private String distribution;

	//----------------------------------------------------------------------------
	public Reporter ( String app_id, String app_ver, Specification spec,
			Study[] study, Calendar inCal,
			CCAnalysis[] a, String pathStem ) 
	throws IOException
	{
		mparams = spec.getAllGlobalParameters();
		gdef = spec.getGDef();
		String reporttype = ((String) mparams.get("report")).toLowerCase();
		seed = spec.getRseed();
		numSim = spec.getNumberOfSimulations();
		numberformat.setMaximumFractionDigits(numSim + 1);
		distribution = spec.getDistribution();
		studyname = new String[study.length];
		peddatasrc = new String[study.length];
		for ( int i = 0; i < study.length; i++ ) {
			studyname[i]  = study[i].getStudyName();
			peddatasrc[i] = study[i].getPedData().getID();
		}

		if ( "summary".equals(reporttype) ) {
			File fsummary = Ut.fExtended(pathStem, "summary");
			pwSummary = new PrintWriter(
					new BufferedWriter(new FileWriter(fsummary)), true);
			for ( int i = 0; i < a.length; i++ )
				summaryCCAnalysis(a[i]);
			closeReport(pwSummary, fsummary);
		} 
		else if ( "simulated".equals(reporttype) ) {
			File fsimulated = Ut.fExtended(pathStem, "simulated");
			pwSimulated = new PrintWriter(
					new BufferedWriter(new FileWriter(fsimulated)), true);
			for ( int i = 0; i < a.length; i++ )
				simulatedCCAnalysis(a[i]);
			closeReport(pwSimulated, fsimulated);
		} 
		else if ( "both".equals(reporttype) ) {
			File fsummary = Ut.fExtended(pathStem, "summary");
			File freport  = Ut.fExtended(pathStem, "report");
			pwSummary = new PrintWriter(
					new BufferedWriter(new FileWriter(fsummary)), true);
			pwReport  = new PrintWriter(
					new BufferedWriter(new FileWriter(freport)), true);
			reportHeaderInfo ( app_id, app_ver, spec, inCal );
			for ( int i = 0; i < a.length; i++ ) {
				summaryCCAnalysis(a[i]);
				reportCCAnalysis(a[i], i+1);
			}
			putElapseTime();
			closeReport(pwSummary, fsummary);
			closeReport(pwReport, freport);
		} 
		else 
		{
			File freport  = Ut.fExtended(pathStem, "report");
			pwReport  = new PrintWriter(
					new BufferedWriter(new FileWriter(freport)), true);
			reportHeaderInfo ( app_id, app_ver, spec, inCal );
			for ( int i = 0; i < a.length; i++ )
				reportCCAnalysis(a[i], i+1);
			putElapseTime();
			closeReport(pwReport, freport);
		}
		System.out.println("Analyses Completed Successfully");
	}

	//----------------------------------------------------------------------------
	void reportHeaderInfo( String app_id, String app_ver, Specification spec, 
			Calendar inCal )
	{
		System.out.println("Writing Report.....");
		pwReport.println("********** " + app_id + app_ver + " Report **********");
		pwReport.println("Created: " + inCal.getTime());
		pwReport.println("Specification from: " + spec.getName());
		pwReport.println("Data from: " + peddatasrc[0]);
		for ( int i = 1; i < peddatasrc.length; i++ )
			pwReport.println("           " + peddatasrc[i]);

		//numSim = spec.getNumberOfSimulations();
		inDate = inCal;
		String[] pairs = new String[mparams.size()];
		int      ipair = 0;

		for (Iterator i = mparams.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry e = (Map.Entry) i.next();
			pairs[ipair++] = e.getKey() + "=" + e.getValue();
		}
		pwReport.println("Runtime global parameters: ");
		pwReport.print("  " + Ut.join(pairs, Ut.N + "  "));
		pwReport.println();
	}

	//----------------------------------------------------------------------------
	public void reportCCAnalysis(CCAnalysis a, int index)
	{
		CCAnalysis.Table[][][][] tables = a.getObservedTable();
		CCAnalysis.Table[][][] metaTDTtables = a.getObsMetaTDTTable();
		int nMetaReports = a.getMetaReports(0, 0).length;
		int nMetaTDTReports = a.getMetaTDTReports(0, 0).length;
		int nrepeat = a.getRepeat();
		String[] tabletype = a.getTableType();
		int ntabletype = tabletype.length;
		for ( int r = 0; r < nrepeat; r++ ) {
			String markers = new String();
			int[] loci_id = a.getLoci(r);
			for ( int i = 0; i < loci_id.length; i++ ) {
				String markerName = gdef.getLocus(loci_id[i]).getMarker();
				if ( markerName.length() == 0 )
					markerName = "Marker " + loci_id[i];
				markers += markerName + " ";
			}

			for ( int t = 0; t < ntabletype; t++ ) {
				String tableName = "";
				if ( tabletype[t].equals("psuedo") )
					tableName = "   Table Type : Psuedo";
				pwReport.println();
				pwReport.println(Ut.fit(" ", TABLE_WIDTH + 6, '_', false));
				pwReport.print(
						Ut.fit(" ", DIV_FWIDTH, '*', false)
						+ "Analysis " + index
						+ Ut.fit(" ", DIV_FWIDTH, '*', true) 
						+ tableName
						+ "   Loci : " + markers
						+ "   Model : " + a.getModel()
						+ "   Type : " + a.getType() );
				pwReport.println();

				for ( int s = 0; s < tables[r][t].length; s++ ) {
					pwReport.println();
					pwReport.println(
							Ut.fit(" ", DIV_FWIDTH, '*', false)
							+ "Study : " + studyname[s] );
					pwReport.println();
					for ( int j = 0 ; j < tables[r][t][s].length; j++ ) {
						if ( tables[r][t][s][j] != null && 
								!(tables[r][t][s][j] instanceof SibTDTTable) )  {
							int tablenamesize = ( tables[r][t][s][j].getTableName()).length();
							int tabpadsize    = ( TABLE_WIDTH - tablenamesize + 2) / 2;
							pwReport.println(Ut.fit(" ", tabpadsize, '=', false) +  
									tables[r][t][s][j].getTableName() +
									Ut.fit(" ", tabpadsize, '=', true ));
							putColumnDefinitions(tables[r][t][s][j]);
							putTableContents(tables[r][t][s][j]);
							pwReport.println();
						}
					}
					putFullStatReports(a.getStatReports(r, t, s));
				}
				if ( nMetaReports > 0 ) {
					pwReport.println();
					String header = "Meta / CMH Analysis";
					int headersize = header.length();
					int headerpadsize = ( TABLE_WIDTH - headersize + 2 ) / 2;
					pwReport.println(Ut.fit(" ", headerpadsize, '_', false) + 
							header + Ut.fit(" ", headerpadsize, '_', true));
					putFullStatReports(a.getMetaReports(r, t));
				}
				if ( nMetaTDTReports > 0 ) {
					pwReport.println();
					String header = "Meta TDT Analysis";
					int headersize = header.length();
					int headerpadsize = ( TABLE_WIDTH - headersize + 2 ) / 2;
					pwReport.println(Ut.fit(" ", headerpadsize, '_', false) + 
							header + Ut.fit(" ", headerpadsize, '_', true));
					pwReport.println();
					for ( int m = 0; m < metaTDTtables[r][t].length; m++ ) {
						if ( metaTDTtables[r][t][m] != null &&
								!(metaTDTtables[r][t][m] instanceof SibTDTTable) ) {
							String tName = metaTDTtables[r][t][m].getTableName();
							int tablenamesize = tName.length();
							int tabpadsize    = ( TABLE_WIDTH - tablenamesize + 2) / 2;
							pwReport.println(Ut.fit(" ", tabpadsize, '=', false) +
									"Meta " + tName +
									Ut.fit(" ", tabpadsize, '=', true ));
							putColumnDefinitions(metaTDTtables[r][t][m]);
							putTableContents(metaTDTtables[r][t][m]);
							pwReport.println();
						}     
					}
					putFullStatReports(a.getMetaTDTReports(r,t));
				}
			}
		}
	}

	//----------------------------------------------------------------------------
	public void summaryCCAnalysis (CCAnalysis a)
	{
		CCAnalysis.Table[][][][] tables = a.getObservedTable();
		CCAnalysis.Table[][][] metaTDTtables = a.getObsMetaTDTTable();
		int nMetaReports = a.getMetaReports(0,0).length;
		int nMetaTDTReports = a.getMetaTDTReports(0,0).length;
		int nrepeat = a.getRepeat();
		for ( int r = 0; r < nrepeat; r++ ) {
			String markers = new String();
			int[] loci_id = a.getLoci(r);
			for ( int i = 0; i < loci_id.length; i++ ) {
				String markerName = gdef.getLocus(loci_id[i] ).getMarker();
				if ( markerName.length() == 0 )
					markerName = "Marker " + loci_id[i];
				markers += markerName + " ";
			}
			pwSummary.print(markers + "  " + a.getModel() + "  " + seed + "  ");

			for ( int t = 0; t < tables[r].length; t++ ) {
				for ( int s = 0; s < tables[r][t].length; s++ ) {
					pwSummary.print(" " + studyname[s] + " ");
					putSummaryStatReports( a.getStatReports(r, t, s) );
				}

				if ( nMetaReports > 0 )
					putSummaryStatReports( a.getMetaReports(r, t) );
				if ( nMetaTDTReports > 0 )
					putSummaryStatReports( a.getMetaTDTReports(r, t) );
				pwSummary.println(" ");
			}
		}
	}

	//----------------------------------------------------------------------------
	public void simulatedCCAnalysis (CCAnalysis a)
	{
		CCAnalysis.Table[][][][] tables = a.getObservedTable();
		CCAnalysis.Table[][][] metaTDTtables = a.getObsMetaTDTTable();
		int nMetaReports = a.getMetaReports(0,0).length;
		int nMetaTDTReports = a.getMetaTDTReports(0,0).length;

		int nrepeat = a.getRepeat();
		String markers = new String();
		for ( int r = 0; r < nrepeat; r++ ) {
			int[] loci_id = a.getLoci(r);
			for ( int i = 0; i < loci_id.length; i++ ) {
				String markerName = gdef.getLocus(loci_id[i]).getMarker();
				if ( markerName.length() == 0 )
					markerName = "Marker " + loci_id[i];
				markers += markerName + " ";
			}
			pwSimulated.print(markers + "  " + a.getModel() + "  " + seed + "  ");

			for ( int t = 0; t < tables[r].length; t++ ) {
				for ( int s = 0; s < tables[r][t].length; s++ )
					putSimulatedStatReports( a.getStatReports(r, t, s) );

				if ( nMetaReports > 0 )
					putSummaryStatReports( a.getMetaReports(r, t) );
				if ( nMetaTDTReports > 0 )
					putSummaryStatReports( a.getMetaTDTReports(r, t) );
				pwSummary.println(" ");
			}
		}
	}

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
	private void putColumnDefinitions(CCAnalysis.Table t)
	{
		int colheadsize = (t.getColumnHeading()).length();
		int colpadsize = ( TABLE_WIDTH - colheadsize - LABEL_FWIDTH + 2) / 2 ;
		//int colpadsize = ( TABLE_WIDTH - colheadsize - LABEL_FWIDTH - 2 ) / 2 ;

		pwReport.println();
		pwReport.println(Ut.ljust(t.getRowHeading(), LABEL_FWIDTH) + " " +
				Ut.fit(" ", colpadsize, '-', false) + t.getColumnHeading() + 
				Ut.fit(" ", colpadsize, '-', true));
		pwReport.print(Ut.ljust(" ", LABEL_FWIDTH) +  " ");

		for (int i = 0, n = t.getColumnCount(); i < n; ++i)
			pwReport.print(
					Ut.rjust( t.getColumnLabelAt(i), COL_FWIDTH )
			);
		String tempTotal = "TOTAL";
		if ( t instanceof QuantitativeTable )
			tempTotal = "Row Total";
		pwReport.println(Ut.rjust(tempTotal, COL_FWIDTH));
		pwReport.println();
	}

	//----------------------------------------------------------------------------
	private void putTableContents(CCAnalysis.Table t)
	{
		for (int irow = 0; irow < t.getRowCount(); ++irow)
		{
			CCAnalysis.Table.Row row = t.getRowAt(irow);
			Number rowtotal = new Double(0.0);
			if ( row != null )
			{
				Number[] rowCells = row.getCells();
				String[] extraInfo = new String[rowCells.length];
				if ( t instanceof CombTDTTable ) 
					rowtotal = null;
				else if ( t instanceof QuantitativeTable )
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
			if ( t instanceof SibTDTTable || t instanceof CombTDTTable )
				gTotal = null;
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

		if ( t.getAttachMessage() != null )
			pwReport.println(t.getAttachMessage());
		pwReport.println();
	}

	//----------------------------------------------------------------------------
	//private void putTableContentLine(String label, String[] vals, int total)
	// Change vals to String[] so we can pass cell value (quant) + count
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
	private void putFullStatReports(CCStatRun.Report[] reports)
	{
		for (int i = 0; i < reports.length; ++i)
		{
			int[] validSim = reports[i].getNumSimulationReport(); 
			pwReport.print(reports[i].getTitle() + " : "); 
			if ( validSim.length > 0  && !distribution.equals("weightedindex") )
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
	private void putSummaryStatReports(CCStatRun.Report[] reports)
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
	private void putSimulatedStatReports(CCStatRun.Report[] reports)
	{
		for (int i = 0; i < reports.length; ++i)
		{
			pwSimulated.print(" obs " +
					setMaxDigits(reports[i].getObservationalReport()));
			//pwSimulated.print(" inf " + reports[i].getInferentialReport());
			//if ( reports[i].getInfExtraReport() != null )
			//  pwSummary.print(" " + reports[i].getInfExtraReport());
			pwSimulated.println();

			if ( reports[i].getInfSimReport() != null )
			{
				Vector<Vector<Double>> v = reports[i].getInfSimReport();
				int nv = v.size();
				int ncv = v.get(0).size();
				for (int k = 0; k < ncv; k++ )
				{
					pwSimulated.println();
					//pwSimulated.print("sim : " + k + " " );
					for ( int j = 0; j < nv; j++ )
					{
						double value = v.get(j).get(k);
						pwSimulated.print(" " + value);
					}
				}
			}
			pwSimulated.println();
		}
	}

	//----------------------------------------------------------------------------
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
	public String setMaxDigits(String str)
	{
		return str;
	}

	public String setMaxDigits(double val)
	{  
		if ( Double.isNaN(val) || Double.isInfinite(val) )
			return "-";
		else
			return numberformat.format(val);
	}

}

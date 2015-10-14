//******************************************************************************
// MainManager.java
//******************************************************************************
package edu.utah.med.genepi.app.rgen;

import edu.utah.med.genepi.apps.HapMC;
import edu.utah.med.genepi.apps.PedGenie;
import edu.utah.med.genepi.apps.SimNullData;
import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.HapConstructor;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Ut;

/** Class controls the flow of the program from the start. 
 * 
 * @author Jathine Wong
 * @author Ryan Abo
 *
 */
public class MainManager {

	private static String appId = null; 
	//public static final String  appVersion = "3.0";
	public static final String  appVersion = "2.1";
	private static final boolean debug = true;

	/** Setup parameters and data into GenieDataSet object and run application.
	 * 
	 * @param args String array of user input. Argument 0 is the application id, 
	 * and argument 1 should be the rgen file path.
	 * 
	 */
	//----------------------------------------------------------------------------
	public void executeGenie(String[] args)
	{
		appId = args[0].toLowerCase();
		System.out.println(Ut.N + "***** " + appId + appVersion + " *****" + Ut.N);
		try
		{
			GenieDataSet genieDataSet = new GenieDataSet(args,appVersion);
			execute(genieDataSet);
		}
		catch (Exception e) {
			System.err.println(Ut.N + "<< " + appId + appVersion + " aborted. >>");
			if ( debug || !(e instanceof GEException))
				e.printStackTrace();
			else
				System.err.println("Error: " + e.getMessage());
			System.err.println();
		}
	}

	/** Runs the specified application. Currently available programs include: pedgenie (single
	 * marker analysis, hapmc (haplotype analysis), and hapconstructor (haplotype data mining).
	 * 
	 * @param genieDataSet GenieDataSet object with all the data and parameter information
	 * stored and accessible.
	 * 
	 */
	//----------------------------------------------------------------------------
	public void execute( GenieDataSet genieDataSet )
	{
		if ( appId.equals("pedgenie") )
		{
			PedGenie pg = new PedGenie(genieDataSet);
		}
		else if ( appId.equals("hapmc") )
		{
			HapMC hapmc = new HapMC(genieDataSet);
		}
		else if ( appId.equals("hapconstructor") )
		{
			HapConstructor hapconstructor = new HapConstructor(genieDataSet);
		}
		else if ( appId.equals("simnull") )
		{
			SimNullData simnulldata = new SimNullData(genieDataSet);
		}
	}
}


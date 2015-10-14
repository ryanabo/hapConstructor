//******************************************************************************
// Main.java
//******************************************************************************
package edu.utah.med.genepi.app.rgen;

import edu.utah.med.genepi.gui2.GenieGUI;

//==============================================================================
/** Contains the main() method that either runs the GUI console or the mainmanager
 *  controls the flow of the program.
 */
public class Main {

	public static void main(String[] args)
	{
		// allow GUI 
		if ( args.length == 0 )
		{
			GenieGUI.main(args);
		}
		else
		{
			MainManager mm = new MainManager();
			mm.executeGenie(args);
		}
	}

}


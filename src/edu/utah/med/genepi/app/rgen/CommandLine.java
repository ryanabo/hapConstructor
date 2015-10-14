//******************************************************************************
// CommandLine.java
//******************************************************************************
package edu.utah.med.genepi.app.rgen;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import edu.utah.med.genepi.util.Ut;

//==============================================================================
public class CommandLine {

  private static final String[] USAGE = {
    "USAGE: ",
//    "   java -jar PedGenie.jar rgenfile[.rgen] datafile [name=value [...]]",
    "   java -jar Genie.jar application_id rgenfile[.rgen] ",
    "",
//    " GCHapFreqTopSim requires a Linkage .par file.",
//    " 	This file format is describle at http://linkage.rockefeller.edu",
    " HapFreqTopSim requires a .hap file.",
    "	This file format is describle at edu/utah/med/genepi/doc/example.hap",
    "",
    " * Optional name/value pairs override rgenfile globals, i.e., <rgen>",
    "   element attributes and optional <param> element content.  To disable",
    "   rather than change <param> content, use a value of dot (.), since any",
    "   parameter whose value starts with dot is skipped.",
    " * Datafile format is a LINKAGE pre-makeped pedfile subset - see docs.",
    " * Report will be written to {datafile-prefix}.{rgenfile-prefix}.rgenr.",
    " * Per-iterate data will be output if the 'dumper' global is set:",
    "     LineRecsDumper writes per-individual lines to *_{i}.rgend",
    "     DescentDumper (testing) writes descent-organized data to *_{i}.html"
  };

//  public static void setInstance(CommandLine l)
//  {
//	defaultInstance = l;
//  }
//
//  public static CommandLine getInstance()
//  {
//	return defaultInstance;
//  }
//
//  private static CommandLine defaultInstance = null;


  private final Map<String, String> mProps = new HashMap<String, String>();
  //private File      fSpec, fPed;
  private File      fSpec;
  private boolean   helpRequested;

  //----------------------------------------------------------------------------
  CommandLine() {}

  //----------------------------------------------------------------------------
  public File getSpecFile() { return fSpec; }

  //----------------------------------------------------------------------------
  //public File getDataFile() { return fPed; }

  //----------------------------------------------------------------------------
  Map<String, String> getProperties() { return mProps; }

  //----------------------------------------------------------------------------
  boolean parse(String[] args)
  {
    resetValues();

    helpRequested = args.length == 0;

    if (args.length < 2)
    //if (args.length < 1)
      return false;

    File f = Ut.fExtended(args[1], "rgen");
    if (!f.exists())
      return false;
    fSpec = f;

    //f = new File(args[1]);
    //if (!f.exists())
    //  return false;
    //fPed = f;

    return parsePropertyArgs(args, 2, mProps);
    //return parsePropertyArgs(args, 1, mProps);
  }

  //----------------------------------------------------------------------------
  void resetValues()
  {
    //fSpec = fPed = null;
    fSpec = null;
    mProps.clear();
  }

  //----------------------------------------------------------------------------
  void exitWithUsage()
  {
    printUsage(System.out,!helpRequested);
    System.exit(helpRequested ? 0 : 1);
  }

  //----------------------------------------------------------------------------
  void printUsage(PrintStream out, boolean bad_args)
  {
   if (bad_args)
     out.println("Invalid command line!");
    for (int i = 0; i < USAGE.length; ++i)
      out.println(USAGE[i]);
    out.println();
  }

  //----------------------------------------------------------------------------
  private static boolean parsePropertyArgs( String[] args,
                                            int ifirst,
                                            Map<String, String> dest)
  {
    for (int i = ifirst; i < args.length; ++i)
    {
      String[] kvpair = args[i].split("=");
      if (kvpair.length != 2)
        return false;

      dest.put(kvpair[0], kvpair[1]);
    }
    return true;
  }
}


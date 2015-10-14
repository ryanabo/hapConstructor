package edu.utah.med.genepi.gui2;

import java.util.ArrayList;

public class Statistic 
{
  String programName;
  String displayName;
  String group;

  public Statistic ( String inProg,
                     String inDisplay, String inGroup )
  {
    programName = inProg;
    displayName = inDisplay;
    group = inGroup;
  }
  
  public static Statistic[] initializeStats()
  {
    ArrayList<Statistic> s = new ArrayList<Statistic>();
    s.add(new Statistic("ChiSquared", "Chi Squared", "s"));
    s.add(new Statistic("ChiSquaredTrend", "Chi Squared Trend", "s"));
    s.add(new Statistic("OddsRatios", "Odds Ratios", "s"));
    s.add(new Statistic("OddsRatiosWithCI",
                         "Odds Ratios With Confidence Intervals", "s"));
    s.add(new Statistic("TrioTDT", "Trio TDT", "s"));
    s.add(new Statistic("SibTDT", "Sib TDT", "s"));
    s.add(new Statistic("CombTDT", "Combine TDT", "s"));
    s.add(new Statistic("Quantitative", "Quantitative", "s"));
    s.add(new Statistic("CMHChiSquared", "CMH Chi Squared", "m"));
    s.add(new Statistic("CMHChiSqTrend", "CMH Chi Squared Trend", "m"));
    s.add(new Statistic("MetaOddsRatios", "Meta Odds Ratios", "m"));
    s.add(new Statistic("MetaOddsRatiosWithCI",
                         "Meta Odds Ratios With Confidence Intervals", "m"));
    s.add(new Statistic("MetaSibTDT", "Meta Sib TDT", "m"));
    s.add(new Statistic("MetaTrioTDT", "Meta Trio TDT", "m"));
    s.add(new Statistic("MetaCombTDT", "Meta Combine TDT", "m"));
    s.add(new Statistic("MetaInteractionOR",
                        "2-Loci Interaction statistic","m"));
    s.add(new Statistic("MetaInteractionORWithCI",
                        "2-Loci Interaction with Confidence Intervals","m"));
    s.add(new Statistic("HWE", "Hardy Weinberg Equilibrium","s"));
    s.add(new Statistic("MetaQtestOR", "Q test Odds Ratio","m"));
    
    return (Statistic[]) s.toArray(new Statistic[0]);
  }
}


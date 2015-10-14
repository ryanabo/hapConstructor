package edu.utah.med.genepi.gui2;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class UIConstants
{
  public static int SCREEN_WIDTH = 900;
  public static int SCREEN_HEIGHT = 700;
  public static int TEXT_WIDTH = 60;
  public static int TEXT_HEIGHT = 25;

  public static JButton BROWSE_button = new JButton("Browse");
  public static final JButton ADD_button = new JButton("Add");
  public static final JButton DELETE_button = new JButton("Delete");
  public static final JButton COMMIT_button = new JButton("Commit");
  public static final JButton BUILDNEXT_button = new JButton("Commit and Build Next Tab");
  public static final JLabel BLANK5_label = new JLabel("     ");
  public static final JLabel BLANK10_label = new JLabel("               ");
  public static final JLabel BLANK20_label = new JLabel("                    ");
  public static final JTextField NOFILESELECTED_text = new JTextField("No File Selected", 30);
  
  public static final String REPEAT_MESSAGE = "Divide all selected loci into groups. Group size is the value in the number of Locus field. Each group will be analyzed with the column group definition and the selected statistics.\nFor example, 5 loci selected and number of locus is set to 2.\nRepeatLoci : there will be 2 groups, locus 1 & 2, and locus 3 & 4, with the last locus not analysed.\nRepeatSlidingWindow : there will be 4 groups, locus 1 & 2, locus 2 & 3, locus 3 & 4, and locus 4 & 5.";
  public static final  String WEIGHT_MESSAGE = "Weight value for this column. Column with the lowest weight value is the reference column.";
  public static final String RGEN_FAILED_MESSAGE = "Failed to create parameter file. Please try again.";
  public static final String NO_FILE_SAVED = "No file saved";

  public UIConstants() 
  {
  }
}

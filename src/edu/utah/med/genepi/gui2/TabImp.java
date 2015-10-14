package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

public class TabImp extends JPanel
                    implements Tab
{
  GenieGUI gui;
  String title;

  public TabImp ()
  { super(); }

  public void build(GenieGUI inGUI)
  {
    gui = inGUI;
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(UIConstants.SCREEN_WIDTH, UIConstants.SCREEN_HEIGHT));
  }

}

package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ColumnDetail extends DetailPanelImp
                          implements ActionListener
{
  JButton weighthelpB, addGroupB;
  JTextField weightT;
  ColumnDetail thisPanel;
  AnalysisTab.AnalysisPanel topPanel;

  public ColumnDetail()
  { 
    super();
    childname = "ColumnGroup";
    allowsChildren = true;
  }

  public void buildDetail(int value)
  {
    setLayout(new BorderLayout());
    index = value;
    title = "Column";
    allowsChildren = true;
    setPreferredSize(new Dimension(600, 400));
    TitledEtched subtitle = new TitledEtched(title + " " + index);
    setBorder(subtitle.title);
    thisPanel = this;
    JPanel panel = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    panel.setLayout(gridbag);
    GridBagConstraints constraints = new GridBagConstraints();
    int childPaneCount = 0;
    JLabel weightL = new JLabel("Weight");
    weightT = new JTextField(20);
    weightL.setLabelFor(weightT);
    weighthelpB = new JButton(createImageIcon("images/Help24.gif",
                                          "column weight help button"));
    weighthelpB.setContentAreaFilled(false);
    weighthelpB.setBorderPainted(false);
    weighthelpB.setHorizontalAlignment(JLabel.LEFT);
    weighthelpB.addActionListener(this);
    weighthelpB.setActionCommand("weighthelp");
    addGroupB = new JButton("Add Pattern Group");
    addGroupB.addActionListener(this);
    constraints.gridx = 10;
    constraints.gridwidth = 3;
    constraints.gridheight = 2;
    constraints.ipadx = 5;
    constraints.weightx = 1.0;
    gridbag.setConstraints(weightL, constraints);
    constraints.gridx = 20;
    constraints.gridwidth = 40;
    gridbag.setConstraints(weightT, constraints);
    constraints.gridx = 80;
    gridbag.setConstraints(weighthelpB, constraints);
    //gridbag.setConstraints(addGroupB, constraints);
    panel.add(weightL);
    panel.add(weightT);
    panel.add(weighthelpB);
    add(panel, BorderLayout.CENTER);
    add(addGroupB, BorderLayout.SOUTH);
    //grouplayer.add(addGroupB);
    //add(wtPanel, BorderLayout.NORTH);
    //add(grouplayer, BorderLayout.CENTER);
  }

  public void actionPerformed ( ActionEvent ae )
  {
    Object source = ae.getSource();
    if ( source == weighthelpB )
    {
        JOptionPane.showMessageDialog(this, UIConstants.WEIGHT_MESSAGE);
    }
    else if ( source == addGroupB )
    {
      int wt = 0;
      ColumnGroupDetail screen = null;
      if ( weightT.getText().length() > 0 )
      {
        try 
        {
          wt = Integer.parseInt(weightT.getText()); 
          screen = (ColumnGroupDetail) createChildPanel();
          screen.setOpaque(true);
          addGroupB.setVisible(false);
          screen.setVisible(true);
          screen.setOpaque(true);
          treexdetailpanel.layeredPane.add(screen, screen.getIndex());
          treexdetailpanel.layeredPane.validate();
          treexdetailpanel.displaySelectedScreen(screen);
        }
        catch ( NumberFormatException nfe )
        { 
          JOptionPane.showMessageDialog(this, "Please enter an INTEGER weight value");
          weightT.setText("");
        }
        catch ( Exception ee )
        {
          System.out.println("Failed to create Column Group Detail");
          System.exit(0);
        }
      }
      else
      {
         JOptionPane.showMessageDialog (this,
          "Enter weight value for this column");
      }   
    }
  }

  public boolean getFlag()
  { return false; }

  public String getWeight()
  {
    return weightT.getText();
  }

  public void setDisplayOnly()
  {
    Util.disable(weightT);
  }
}

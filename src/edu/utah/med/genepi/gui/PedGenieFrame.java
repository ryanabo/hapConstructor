package edu.utah.med.genepi.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

public class PedGenieFrame extends JFrame
                               implements ActionListener, ItemListener
{
    JDesktopPane desktop;
    JInternalFrame cctFrame;
    SimulationPanel simulationPanel = new SimulationPanel();
    LocusPanel locusPanel;
    StatisticPanel statisticPanel;
    FileParser fileparser;
    JFileChooser filechooser;
    JButton cctableButton, saveButton, quitButton;
    JLabel displaymessage;
    String message = "no messages";
    int cctableCount = 0;
    private final List<CCTableFrame> lCCTableF = new ArrayList<CCTableFrame>();
    int numLocus;
    String[] locusName;
    
    public PedGenieFrame()
    {
      new PedGenieFrame(0, new String[] {"aa", "bb"});
    }

    public PedGenieFrame(int dumdum, String[] locusString) 
    {
        super("PedGenie .rgen parameter file setup");

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);

        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        setContentPane(desktop);

        //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
       
        //Add panels and buttons
        desktop.setLayout(new BorderLayout());
        JFileChooser fchooser = new JFileChooser();
        fchooser.setDialogTitle("Please select pedigree file");
        int returnVal = fchooser.showOpenDialog(desktop);
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
          File file = fchooser.getSelectedFile();
          try 
          { fileparser = new FileParser(file); }
          catch ( Exception e )
          {System.out.println("Failed to read file : " + file + ", " + e); } 
          //fchooser.setVisible(false);
        }
        JPanel northPanel      = north();
        JPanel savequitPanel   = savequitPanel();
        desktop.add(northPanel, BorderLayout.NORTH);
        desktop.add(savequitPanel, BorderLayout.SOUTH);
    }

    protected JPanel north()
    {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.add( simulationPanel );
      panel.add( locusAndstatistic() );
      return panel;
    }

    protected JPanel locusAndstatistic()
    {
      JPanel panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.X_AXIS));
      numLocus = fileparser.getNumLocus();
      locusName = fileparser.getLocusName();
      locusPanel = new LocusPanel(numLocus, locusName);
      //locusPanel = new LocusPanel();
      statisticPanel = new StatisticPanel();
      JScrollPane locusSPane = new JScrollPane(locusPanel);
      locusSPane.setPreferredSize(new Dimension(450, 110));
      panel.add( locusSPane );
      panel.add( statisticPanel );
      return panel;
    }

    protected JPanel savequitPanel()
    {
       JPanel panel = new JPanel();
       panel.setLayout( new GridLayout(1, 4));
      
       cctableButton = new JButton("Create A Subset Analysis");
       saveButton = new JButton("Save and Exit");
       quitButton = new JButton("Quit");
       //displaymessage = new JLabel(message);
       displaymessage = new JLabel(String.valueOf(locusName[0]));
       cctableButton.addActionListener(this);
       saveButton.addActionListener(this);
       quitButton.addActionListener(this);
       panel.add(cctableButton);
       panel.add(saveButton);
       panel.add(quitButton);
       panel.add(displaymessage);
       return panel;
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Set up the lone menu.
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        //Set up the second menu item.
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        return menuBar;
    }

    public void itemStateChanged(ItemEvent e) 
    {
      Object source = e.getItemSelectable();
      
      //if (source == chisquaredCB)
       // add this item to statisticsList
      //if ( e.getStateChange() == ItemEvent.DESELECTED )
        // remove this item from the statisticsList
    }

    public void actionPerformed(ActionEvent e) 
    {
        //if ("new".equals(e.getActionCommand())) { //new
        if ( e.getSource() == cctableButton )
        {
          if ( cctableCount < 6 )
          {
            CCTableFrame cctf = createCCTable();
            lCCTableF.add(cctf); 
            //displaymessage.setText(message);
            cctableCount++;
          }
          else
          {
            message = "max # of cctable reached";
            displaymessage.setText(message);
          }
        }
        else if ( e.getSource() == saveButton )
        {
          filechooser = new JFileChooser();
          int returnVal = filechooser.showSaveDialog(PedGenieFrame.this);
          if ( returnVal == JFileChooser.APPROVE_OPTION )
          {
            File file = filechooser.getSelectedFile();
            writeXML(file);
            quit();
          }
          else
          {
            displaymessage.setText("Save command cancelled by user");
          }
        }
        else if ( e.getSource() == quitButton )
            quit();
    }

    //Create a new internal frame.
    //protected void createCCTable()
    protected CCTableFrame createCCTable()
    {
      CCTableFrame cctFrame = new CCTableFrame();
      cctFrame.setVisible(true); //necessary as of 1.3
      cctFrame.setSize(100, 100);
      desktop.add(cctFrame, BorderLayout.CENTER);
      try 
      {    cctFrame.setSelected(true); }
      catch (java.beans.PropertyVetoException e) {}
      return cctFrame;
    }
   
    protected void writeXML(File file)
    {
      CCTableFrame[] cctf = (CCTableFrame[]) lCCTableF.toArray(
                                             new CCTableFrame[0]);
      Integer rseed = simulationPanel.getRseed();
      Integer nsims = simulationPanel.getNsim();
      String top    = simulationPanel.getTop();
      String drop   = simulationPanel.getDrop();
      String freq   = simulationPanel.getFreq();
      String dumper = simulationPanel.getDump();
      String xmlMessage = "null";

      xmlMessage = "inside";
      try 
      {
        XMLfile xml = new XMLfile( file,
                                   locusPanel.getLocusInfo(),
                                   statisticPanel.getStatistics() );
        xml.headerInfo();
        xml.simInfo(rseed, nsims, top, drop, freq, dumper);
        xml.locusInfo();
        xml.statisticInfo();
        if ( statisticPanel.getquantfile() != null )
          xml.quantfileInfo(statisticPanel.getquantfile().getName());
        for ( int i = 0; i < cctf.length; i++ )
        {
          if ( !cctf[i].getQuitIndicator() )
          { 
            if ( cctf[i].getcctable() != null )
            {
        xmlMessage = new String("MM");
              xml.writeCCTable(cctf[i].getcctable());
              xmlMessage = "cctable is not null" ;
            }
            else 
              xmlMessage = "cctable is null";
          }
        }
        xml.closefile();
      }
      catch ( Exception e )
      { 
        displaymessage.setText(xmlMessage + cctf.length);
      }
    }

    //Quit the application.
    protected void quit() {
        System.exit(0);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
  private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        PedGenieFrame pedgenieFrame = new PedGenieFrame();
        pedgenieFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        pedgenieFrame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
  
}

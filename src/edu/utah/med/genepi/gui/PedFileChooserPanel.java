package edu.utah.med.genepi.gui;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PedFileChooserPanel extends FileChooserPanel
{
  public PedFileChooserPanel() 
  {
    super();
    frameTitle = new String("Choose the Pedigree File");
    setLayout(new BorderLayout());

    //Create the log first, because the action listeners
    //need to refer to it.
    log = new JTextArea(5,20);
    log.setMargin(new Insets(5,5,5,5));
    log.setEditable(false);
    JScrollPane logScrollPane = new JScrollPane(log);

    //Create a file chooser
    fc = new JFileChooser();

    openButton = new JButton("Open a File...",
                             createImageIcon("images/Open16.gif"));
    openButton.addActionListener(this);

    //For layout purposes, put the buttons in a separate panel
    JPanel buttonPanel = new JPanel(); //use FlowLayout
    buttonPanel.add(openButton);

    //Add the buttons and the log to this panel.
    add(buttonPanel, BorderLayout.PAGE_START);
    add(logScrollPane, BorderLayout.CENTER);
  }
  
  public void hidePanel()
  {
    setVisible(false);
  }

  public File getFile()
  {
    return fc.getSelectedFile();
  }
}

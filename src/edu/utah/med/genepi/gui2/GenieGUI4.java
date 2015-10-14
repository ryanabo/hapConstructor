package edu.utah.med.genepi.gui2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class GenieGUI extends JPanel
                      implements ItemListener
{
    MainPanel mainPanel;

    public JMenuBar createMenuBar() 
    {
        JMenuBar menuBar;
        JMenu menu;
        menuBar = new JMenuBar();

        menu = new JMenu("Global");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        menu = new JMenu("Statistics");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);

        return menuBar;
    }
  
    public void itemStateChanged( ItemEvent e )
    {
      if (e.getStateChange() == ItemEvent.SELECTED )
      {
        String selected = e.paramString();
        // display selected item in MainPanel
        //mainPanel.displayPanel(selected);
      }
    }
        
    public Container createContentPane() {
        //Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);

        mainPanel = new MainPanel();
        contentPane.add(mainPanel, BorderLayout.CENTER);
        return contentPane;
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Genie GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        GenieGUI geniegui = new GenieGUI();
        frame.setJMenuBar(geniegui.createMenuBar());
        frame.setContentPane(geniegui.createContentPane());

        //Display the window.
        //frame.setSize(450, 260);
        frame.setVisible(true);
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

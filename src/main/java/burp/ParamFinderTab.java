package burp;

import java.io.PrintWriter;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.GridLayout;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.List;


public class ParamFinderTab implements ITab {

    private JPanel panel;
    private JLabel pathLabel;
    //private final PrintWriter stdout;
    private JButton createListsBtn;
    private JTextField pathTxtField;
    private JPanel firstSection;
    private List<JScrollPane> allScrollPanes = new ArrayList<JScrollPane>();

    public ParamFinderTab(final IBurpExtenderCallbacks callbacks, final PrintWriter stdout, final PrintWriter stderr, final IExtensionHelpers helpers){
    		
            SwingUtilities.invokeLater(new Runnable() 
            {
                @Override
                public void run()
                {
                    String currentDir = System.getProperty("user.dir");

                    panel = new JPanel();
                    panel.setLayout(new GridLayout(0, 2));
                    //panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
                    //panel.add(Box.createRigidArea(new Dimension(0,5)));

                    pathLabel = new JLabel("Download path:");


                    pathTxtField = new JTextField(currentDir);
                    pathTxtField.setPreferredSize(new Dimension(250, 20));
                    pathTxtField.setAlignmentX(JTextField.LEFT_ALIGNMENT);
                    
                    createListsBtn = new JButton("Create Parameter Wordlists");

                    firstSection = new JPanel();
                    firstSection.add(pathLabel);
                    firstSection.add(pathTxtField);
                    firstSection.add(createListsBtn);

                    createListsBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                           //System.out.println("Create list button pressed.");
                           
                           stdout.println("Removing all old JScrollPanes");
                           removeOldScrollPanes();
                           IHttpRequestResponse[] history = callbacks.getProxyHistory();
						Map<String, Set<String>> uniqueInScopeParams = ParameterFactory.getAllParameters(history, callbacks, stdout, stderr, helpers);
                           for (Map.Entry<String, Set<String>> set : uniqueInScopeParams.entrySet()) {
                                //stdout.println("Current scope is: " + set.getKey());
                                String params = ParameterFactory.printParams(set.getValue(), set.getKey(), stdout, pathTxtField);
                                
                                JScrollPane paramScrollPane = createScrollBoxes(params, set.getKey(), stdout);
                                allScrollPanes.add(paramScrollPane);
                                panel.add(paramScrollPane);
                            }
                           
                        }
                     });

                    panel.add(firstSection);
                    //panel.add(createListsBtn);
                    
                    // customize our UI components
                    callbacks.customizeUiComponent(panel);
                    // add the custom tab to Burp's UI
                    callbacks.addSuiteTab(ParamFinderTab.this);
                }
            });
    }
    public JScrollPane createScrollBoxes(String params, String scope, PrintWriter stdout){
            
            JTextArea paramTextArea = new JTextArea();
            //stdout.println("Params are: " + params);
            paramTextArea.setBorder(new TitledBorder(new EtchedBorder(), scope));
            paramTextArea.setText(params);
            JScrollPane newScrollPane = new JScrollPane(paramTextArea);
            //newScrollPane.add(scopeLabel);
            //newScrollPane.add();
            //newPanel.add(newScrollPane);
            
            
            return newScrollPane;
    }
    
    private void removeOldScrollPanes()
    {
    	for(JScrollPane scrollPane : allScrollPanes) {
    		panel.remove(scrollPane);
    	}
    }
    @Override
    public String getTabCaption()
    {
        return "Param Finder";
    }

    @Override
    public Component getUiComponent()
    {
        return panel;
    }
}


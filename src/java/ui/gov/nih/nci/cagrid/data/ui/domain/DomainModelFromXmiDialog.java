package gov.nih.nci.cagrid.data.ui.domain;


import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.xmi.FixXmiExecutor;
import gov.nih.nci.cagrid.metadata.xmi.XMIParser;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.model.Application;
import org.cagrid.grape.utils.CompositeErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/** 
 *  DomainModelFromXmiDialog
 *  Dialog for generating a Domain Model from an XMI file
 * 
 * @author David Ervin
 * 
 * @created Oct 23, 2007 11:05:04 AM
 * @version $Id: DomainModelFromXmiDialog.java,v 1.7 2007/11/07 21:47:43 dervin Exp $ 
 */
public class DomainModelFromXmiDialog extends JDialog {
    // keys for validation messages
    public static final String KEY_XMI_FILENAME = "XMI File Name";
    public static final String KEY_SDK_DIR = "caCORE SDK Directory";
    public static final String KEY_PROJECT_SHORT_NAME = "Project Short Name";
    public static final String KEY_PROJECT_VERSION = "Project Version";

    private JLabel xmiFileLabel = null;
    private JTextField xmiFileTextField = null;
    private JButton xmiBrowseButton = null;
    private JLabel shortNameLabel = null;
    private JTextField projectShortNameTextField = null;
    private JLabel projectVersionLabel = null;
    private JTextField projectVersionTextField = null;
    private JLabel projectLongNameLabel = null;
    private JTextField projectLongNameTextField = null;
    private JLabel projectDescriptionLabel = null;
    private JTextArea projectDescriptionTextArea = null;
    private JScrollPane projectDescriptionScrollPane = null;
    private JPanel mainPanel = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private JPanel buttonPanel = null;
    private JCheckBox fixEaModelCheckBox = null;
    private JTextField sdkDirTextField = null;
    private JButton sdkDirBrowseButton = null;
    
    private boolean canceled;
    private String suppliedXmiFilename = null;
    private ValidationResultModel validationModel = null;
    private DocumentChangeAdapter documentChangeListener = null;
    
    private DomainModelFromXmiDialog(JFrame parent, String xmiFilename) {
        super(parent, "Generate Domain Model", true);
        canceled = true;
        suppliedXmiFilename = xmiFilename;
        validationModel = new DefaultValidationResultModel();
        documentChangeListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        initialize();
    }
    
    
    private void initialize() {
        setContentPane(new IconFeedbackPanel(validationModel, getMainPanel()));
        configureValidation();
        pack();
        setSize(500, getPreferredSize().height);
        setVisible(true);
    }
    
    
    public static DomainModel createDomainModel(JFrame parent) {
        return createDomainModel(parent, null);
    }
    
    
    public static DomainModel createDomainModel(JFrame parent, String xmiFilename) {
        DomainModelFromXmiDialog dialog = new DomainModelFromXmiDialog(parent, xmiFilename);
        if (!dialog.canceled) {
            File xmiFile = null;
            if (dialog.getFixEaModelCheckBox().isSelected()) {
                File sdkDir = new File(dialog.getSdkDirTextField().getText());
                File rawXmiFile = new File(dialog.getXmiFileTextField().getText());
                try {
                    xmiFile = FixXmiExecutor.fixEaXmiModel(rawXmiFile, sdkDir);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error executing fix-xmi command", ex.getMessage(), ex);
                    return null;
                }
            } else {
                xmiFile = new File(dialog.getXmiFileTextField().getText()); 
            }

            String shortName = dialog.getProjectShortNameTextField().getText();
            String version = dialog.getProjectVersionTextField().getText();
            
            XMIParser parser = new XMIParser(shortName, version);
            
            String longName = dialog.getProjectLongNameTextField().getText();
            if (longName != null && longName.length() != 0) {
                parser.setProjectLongName(longName);
            }
            String description = dialog.getProjectDescriptionTextArea().getText();
            if (description != null && description.length() != 0) {
                parser.setProjectDescription(description);
            }
            
            DomainModel model = null;
            try {
                model = parser.parse(xmiFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog("Error parsing XMI to domain model", ex.getMessage(), ex);
            }
            
            return model;
        }
        return null;
    }
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getXmiFileTextField(), KEY_XMI_FILENAME);
        ValidationComponentUtils.setMessageKey(getSdkDirTextField(), KEY_SDK_DIR);
        ValidationComponentUtils.setMessageKey(getProjectShortNameTextField(), KEY_PROJECT_SHORT_NAME);
        ValidationComponentUtils.setMessageKey(getProjectVersionTextField(), KEY_PROJECT_VERSION);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (ValidationUtils.isBlank(getXmiFileTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_XMI_FILENAME + " must not be blank", Severity.ERROR, KEY_XMI_FILENAME));
        }
        
        if (ValidationUtils.isBlank(getProjectShortNameTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_PROJECT_SHORT_NAME + " must not be blank", Severity.ERROR, KEY_PROJECT_SHORT_NAME));
        }
        
        if (ValidationUtils.isBlank(getProjectVersionTextField().getText())) {
            result.add(new SimpleValidationMessage(
                KEY_PROJECT_VERSION + " must not be blank", Severity.ERROR, KEY_PROJECT_VERSION));
        }
        
        if (getFixEaModelCheckBox().isSelected()) {
            if (ValidationUtils.isBlank(getSdkDirTextField().getText())) {
                result.add(new SimpleValidationMessage(
                    KEY_SDK_DIR + " must not be blank.\nPlease select the caCORE SDK directory", 
                    Severity.ERROR, KEY_SDK_DIR));
            }
        }
        
        validationModel.setResult(result);
        
        updateComponentTreeSeverity();
        updateOkButton();
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
    
    
    private void updateOkButton() {
        getOkButton().setEnabled(!validationModel.hasErrors());
    }


    /**
     * This method initializes xmiFileLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getXmiFileLabel() {
        if (xmiFileLabel == null) {
            xmiFileLabel = new JLabel();
            xmiFileLabel.setText("XMI File:");
        }
        return xmiFileLabel;
    }


    /**
     * This method initializes xmiFileTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getXmiFileTextField() {
        if (xmiFileTextField == null) {
            xmiFileTextField = new JTextField();
            xmiFileTextField.setEditable(false);
            xmiFileTextField.addFocusListener(new FocusChangeHandler());
            if (suppliedXmiFilename != null) {
                xmiFileTextField.setText(suppliedXmiFilename);
                getXmiBrowseButton().setEnabled(false);
            }
        }
        return xmiFileTextField;
    }


    /**
     * This method initializes xmiBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getXmiBrowseButton() {
        if (xmiBrowseButton == null) {
            xmiBrowseButton = new JButton();
            xmiBrowseButton.setText("Browse");
            xmiBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(FileFilters.XMI_FILTER);
                    int choice = chooser.showOpenDialog(DomainModelFromXmiDialog.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        String selectedFile = chooser.getSelectedFile().getAbsolutePath();
                        getXmiFileTextField().setText(selectedFile);
                    } else {
                        getXmiFileTextField().setText(null);
                    }
                    
                    validateInput();
                }
            });
        }
        return xmiBrowseButton;
    }


    /**
     * This method initializes shortNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getShortNameLabel() {
        if (shortNameLabel == null) {
            shortNameLabel = new JLabel();
            shortNameLabel.setText("Project Short Name:");
        }
        return shortNameLabel;
    }


    /**
     * This method initializes shortNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectShortNameTextField() {
        if (projectShortNameTextField == null) {
            projectShortNameTextField = new JTextField();
            projectShortNameTextField.addFocusListener(new FocusChangeHandler());
            projectShortNameTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return projectShortNameTextField;
    }


    /**
     * This method initializes projectVersionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getProjectVersionLabel() {
        if (projectVersionLabel == null) {
            projectVersionLabel = new JLabel();
            projectVersionLabel.setText("Project Version:");
        }
        return projectVersionLabel;
    }


    /**
     * This method initializes projectVersionTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectVersionTextField() {
        if (projectVersionTextField == null) {
            projectVersionTextField = new JTextField();
            projectVersionTextField.addFocusListener(new FocusChangeHandler());
            projectVersionTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return projectVersionTextField;
    }


    /**
     * This method initializes projectLongNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getProjectLongNameLabel() {
        if (projectLongNameLabel == null) {
            projectLongNameLabel = new JLabel();
            projectLongNameLabel.setText("Project Long Name:");
        }
        return projectLongNameLabel;
    }


    /**
     * This method initializes projectLongNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectLongNameTextField() {
        if (projectLongNameTextField == null) {
            projectLongNameTextField = new JTextField();
        }
        return projectLongNameTextField;
    }


    /**
     * This method initializes projectDescriptionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getProjectDescriptionLabel() {
        if (projectDescriptionLabel == null) {
            projectDescriptionLabel = new JLabel();
            projectDescriptionLabel.setText("Project Description:");
        }
        return projectDescriptionLabel;
    }


    /**
     * This method initializes projectDescriptionTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getProjectDescriptionTextArea() {
        if (projectDescriptionTextArea == null) {
            projectDescriptionTextArea = new JTextArea();
            projectDescriptionTextArea.setLineWrap(true);
            projectDescriptionTextArea.setWrapStyleWord(true);
        }
        return projectDescriptionTextArea;
    }


    /**
     * This method initializes projectDescriptionScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getProjectDescriptionScrollPane() {
        if (projectDescriptionScrollPane == null) {
            projectDescriptionScrollPane = new JScrollPane();
            projectDescriptionScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            projectDescriptionScrollPane.setViewportView(getProjectDescriptionTextArea());
        }
        return projectDescriptionScrollPane;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 2;
            gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.gridy = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridy = 1;
            gridBagConstraints21.weightx = 1.0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.gridx = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 2;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 6;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.BOTH;
            gridBagConstraints10.gridy = 5;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.weighty = 1.0;
            gridBagConstraints10.gridwidth = 2;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 5;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 4;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.gridwidth = 2;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 4;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 3;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.gridwidth = 2;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 3;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setSize(new Dimension(384, 243));
            mainPanel.add(getXmiFileLabel(), gridBagConstraints);
            mainPanel.add(getXmiFileTextField(), gridBagConstraints1);
            mainPanel.add(getXmiBrowseButton(), gridBagConstraints2);
            mainPanel.add(getShortNameLabel(), gridBagConstraints3);
            mainPanel.add(getProjectShortNameTextField(), gridBagConstraints4);
            mainPanel.add(getProjectVersionLabel(), gridBagConstraints5);
            mainPanel.add(getProjectVersionTextField(), gridBagConstraints6);
            mainPanel.add(getProjectLongNameLabel(), gridBagConstraints7);
            mainPanel.add(getProjectLongNameTextField(), gridBagConstraints8);
            mainPanel.add(getProjectDescriptionLabel(), gridBagConstraints9);
            mainPanel.add(getProjectDescriptionScrollPane(), gridBagConstraints10);
            mainPanel.add(getButtonPanel(), gridBagConstraints11);
            mainPanel.add(getFixEaModelCheckBox(), gridBagConstraints12);
            mainPanel.add(getSdkDirTextField(), gridBagConstraints21);
            mainPanel.add(getSdkDirBrowseButton(), gridBagConstraints31);
        }
        return mainPanel;
    }


    /**
     * This method initializes okButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("OK");
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    canceled = false;
                    dispose();
                }
            });
        }
        return okButton;
    }


    /**
     * This method initializes cancelButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    canceled = true;
                    dispose();
                }
            });
        }
        return cancelButton;
    }


    /**
     * This method initializes buttonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(4);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(gridLayout);
            buttonPanel.add(getCancelButton(), null);
            buttonPanel.add(getOkButton(), null);
        }
        return buttonPanel;
    }
    
    
    /**
     * This method initializes fixEaModelCheckBox   
     *  
     * @return javax.swing.JCheckBox    
     */
    private JCheckBox getFixEaModelCheckBox() {
        if (fixEaModelCheckBox == null) {
            fixEaModelCheckBox = new JCheckBox();
            fixEaModelCheckBox.setText("Fix EA Model");
            fixEaModelCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    boolean enable = fixEaModelCheckBox.isSelected();
                    getSdkDirTextField().setEnabled(enable);
                    getSdkDirBrowseButton().setEnabled(enable);
                    validateInput();
                }
            });
        }
        return fixEaModelCheckBox;
    }


    /**
     * This method initializes sdkDirTextField  
     *  
     * @return javax.swing.JTextField   
     */
    private JTextField getSdkDirTextField() {
        if (sdkDirTextField == null) {
            sdkDirTextField = new JTextField();
            sdkDirTextField.setEditable(false);
            sdkDirTextField.setEnabled(getFixEaModelCheckBox().isSelected());
            sdkDirTextField.getDocument().addDocumentListener(documentChangeListener);
        }
        return sdkDirTextField;
    }


    /**
     * This method initializes sdkDirBrowseButton   
     *  
     * @return javax.swing.JButton  
     */
    private JButton getSdkDirBrowseButton() {
        if (sdkDirBrowseButton == null) {
            sdkDirBrowseButton = new JButton();
            sdkDirBrowseButton.setText("Browse");
            sdkDirBrowseButton.setEnabled(getFixEaModelCheckBox().isSelected());
            sdkDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setApproveButtonText("Select");
                    int choice = chooser.showOpenDialog(DomainModelFromXmiDialog.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        getSdkDirTextField().setText(chooser.getSelectedFile().getAbsolutePath());
                    } else {
                        getSdkDirTextField().setText("");
                    }
                    validateInput();
                }
            });
        }
        return sdkDirBrowseButton;
    }
    
    
    private final class FocusChangeHandler implements FocusListener {

        public void focusGained(FocusEvent e) {
            update();

        }


        public void focusLost(FocusEvent e) {
            update();
        }


        private void update() {
            validateInput();
        }
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Error selecting system look and feel");
        }
        // set up the Grid Portal application instance
        Application app = new Application();
        app.setName("Domain Model from XMI");
        try {
            GridApplication.getInstance(app);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error creating grid application instance");
            System.exit(1);
        }
        
        DomainModel model = createDomainModel(null);
        if (model != null) {
            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setFileFilter(FileFilters.XML_FILTER);
            int choice = saveChooser.showSaveDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                try {
                    FileWriter writer = new FileWriter(saveChooser.getSelectedFile());
                    MetadataUtils.serializeDomainModel(model, writer);
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}

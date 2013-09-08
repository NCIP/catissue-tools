package gov.nih.nci.cagrid.data.style.sdkstyle.wizard;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;
import gov.nih.nci.cadsr.umlproject.domain.UMLPackageMetadata;
import gov.nih.nci.cagrid.cadsr.client.CaDSRServiceClient;
import gov.nih.nci.cagrid.cadsr.portal.CaDSRBrowserPanel;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.portal.PortalUtils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.CadsrInformation;
import gov.nih.nci.cagrid.data.extension.CadsrPackage;
import gov.nih.nci.cagrid.data.extension.ClassMapping;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.ui.NamespaceUtils;
import gov.nih.nci.cagrid.data.ui.domain.DomainModelFromXmiDialog;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.BusyDialogRunnable;
import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  DomainModelPanel
 *  Panel to allow selection / generation of a domain model for the service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 25, 2006 
 * @version $Id: DomainModelPanel.java,v 1.6 2008/01/17 21:29:31 dervin Exp $ 
 */
public class DomainModelPanel extends AbstractWizardPanel {
	
	private static Project lastSelectedProject = null;

	private JRadioButton noDomainModelRadioButton = null;
	private JRadioButton fromFileRadioButton = null;
	private JRadioButton fromCaDsrRadioButton = null;
	private JPanel dmSourcePanel = null;
	private JLabel fileLabel = null;
	private JTextField fileTextField = null;
	private JButton browseButton = null;
	private JPanel dmFilePanel = null;
	private InternalCaDSRBrowserPanel caDsrBrowser = null;
	private JList selectedPackagesList = null;
	private JScrollPane selectedPackagesScrollPane = null;
	private JButton addPackageButton = null;
	private JButton removePackageButton = null;
	private JPanel packageButtonsPanel = null;
	private JPanel caDsrPanel = null;

    private JButton addProjectButton = null;

    public DomainModelPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
		super(extensionDescription, info);
		initialize();
	}


	public void update() {
		try {
			CadsrInformation info = getCadsrInformation();
			if (info.isNoDomainModel()) {
				getNoDomainModelRadioButton().setSelected(true);
			}
			if (info.getServiceUrl() != null) {
				getCaDsrBrowser().setDefaultCaDSRURL(info.getServiceUrl());
				getCaDsrBrowser().getCadsr().setText(info.getServiceUrl());
			}
			if (info.isUseSuppliedModel()) {
				ResourcePropertyType dmResourceProperty = CommonTools.getResourcePropertiesOfType(
					getServiceInformation().getServices().getService(0), 
					DataServiceConstants.DOMAIN_MODEL_QNAME)[0];
				String filename = getServiceInformation().getBaseDirectory().getAbsolutePath() 
				+ File.separator + "etc" + File.separator + dmResourceProperty.getFileLocation();
				getFileTextField().setText(filename);
				getFromFileRadioButton().setSelected(true);
			}
			if (info.getProjectLongName() != null && info.getProjectVersion() != null) {
				lastSelectedProject = new Project();
				lastSelectedProject.setLongName(info.getProjectLongName());
				lastSelectedProject.setVersion(info.getProjectVersion());
			}
			if (info.getPackages() != null) {
				String[] names = new String[info.getPackages().length];
				for (int i = 0; i < info.getPackages().length; i++) {
					names[i] = info.getPackages(i).getName();
				}
				getSelectedPackagesList().setListData(names);
			}
			storeCadsrInformation(info);
		} catch (Exception ex) {
			ex.printStackTrace();
			CompositeErrorDialog.showErrorDialog("Error loading extension data", ex);
		}
	}


	public String getPanelTitle() {
		return "Domain Model Selection";
	}
    

	public String getPanelShortName() {
		return "Model";
	}
	
	
	private void initialize() {
        GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
        gridBagConstraints13.gridx = 0;
        gridBagConstraints13.gridwidth = 2;
        gridBagConstraints13.weightx = 1.0D;
        gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints13.gridy = 1;
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.gridx = 1;
        gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints12.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints12.gridy = 0;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new java.awt.Dimension(623,298));
        this.add(getDmSourcePanel(), gridBagConstraints11);
        this.add(getDmFilePanel(), gridBagConstraints12);
        this.add(getCaDsrPanel(), gridBagConstraints13);		
	}


	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getNoDomainModelRadioButton() {
		if (noDomainModelRadioButton == null) {
			noDomainModelRadioButton = new JRadioButton();
			noDomainModelRadioButton.setText("Use No Domain Model");
			noDomainModelRadioButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (noDomainModelRadioButton.isSelected()) {
						// enable / disable panels
						PortalUtils.setContainerEnabled(getDmFilePanel(), false);
						PortalUtils.setContainerEnabled(getCaDsrPanel(), false);
						// clear out the supplied domain model info
						getFileTextField().setText("");
						setSelectedDomainModelFilename(null);
					}
					storeNoDomainModelState();
				}
			});
		}
		return noDomainModelRadioButton;
	}


	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getFromFileRadioButton() {
		if (fromFileRadioButton == null) {
			fromFileRadioButton = new JRadioButton();
			fromFileRadioButton.setText("Domain Model From File");
			fromFileRadioButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (fromFileRadioButton.isSelected()) {
						// enable / disable panels
						PortalUtils.setContainerEnabled(getDmFilePanel(), true);
						PortalUtils.setContainerEnabled(getCaDsrPanel(), false);
					}
				}
			});
		}
		return fromFileRadioButton;
	}


	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getFromCaDsrRadioButton() {
		if (fromCaDsrRadioButton == null) {
			fromCaDsrRadioButton = new JRadioButton();
			fromCaDsrRadioButton.setText("Generate From caDSR");
			fromCaDsrRadioButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (fromCaDsrRadioButton.isSelected()) {
						// enable / disable panels
						PortalUtils.setContainerEnabled(getDmFilePanel(), false);
						PortalUtils.setContainerEnabled(getCaDsrPanel(), true);
						// clear out supplied domain model info
						getFileTextField().setText("");
						ResourcePropertyType dmResourceProp = getDomainModelResourceProperty();
						dmResourceProp.setPopulateFromFile(false);
					}
				}
			});
		}
		return fromCaDsrRadioButton;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDmSourcePanel() {
		if (dmSourcePanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			dmSourcePanel = new JPanel();
			dmSourcePanel.setLayout(new GridBagLayout());
			dmSourcePanel.add(getNoDomainModelRadioButton(), gridBagConstraints);
			dmSourcePanel.add(getFromFileRadioButton(), gridBagConstraints1);
			dmSourcePanel.add(getFromCaDsrRadioButton(), gridBagConstraints2);
			ButtonGroup group = new ButtonGroup();
			group.add(getNoDomainModelRadioButton());
			group.add(getFromFileRadioButton());
			group.add(getFromCaDsrRadioButton());
			group.setSelected(getFromCaDsrRadioButton().getModel(), true);
		}
		return dmSourcePanel;
	}


	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getFileLabel() {
		if (fileLabel == null) {
			fileLabel = new JLabel();
			fileLabel.setText("Domain Model File:");
		}
		return fileLabel;
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFileTextField() {
		if (fileTextField == null) {
			fileTextField = new JTextField();
			fileTextField.setEditable(false);
		}
		return fileTextField;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBrowseButton() {
		if (browseButton == null) {
			browseButton = new JButton();
			browseButton.setText("Browse");
            browseButton.setToolTipText("Browse for Domain Model or an XMI to convert");
			browseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectDomainModelFile();
				}
			});
		}
		return browseButton;
	}
    
    
    private void selectDomainModelFile() {
        String selectedFilename = null;
        String mostRecentFilename = null;
        try {
            mostRecentFilename = ResourceManager.getStateProperty(ResourceManager.LAST_FILE);
        } catch (IOException ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error determining most recently selected file", ex);
        }
        JFileChooser chooser = new JFileChooser(mostRecentFilename);
        chooser.addChoosableFileFilter(FileFilters.XML_FILTER);
        chooser.addChoosableFileFilter(FileFilters.XMI_FILTER);
        chooser.setFileFilter(FileFilters.XML_FILTER);
        chooser.setAcceptAllFileFilterUsed(false);
        int choice = chooser.showOpenDialog(DomainModelPanel.this);
        if (choice == JFileChooser.APPROVE_OPTION) { // selection made
            selectedFilename = chooser.getSelectedFile().getAbsolutePath();
            try {
                ResourceManager.setStateProperty(ResourceManager.LAST_FILE, selectedFilename);
            } catch (IOException ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog("Error storing most recently selected file", ex);
            }

            // if user selected an XMI file, there is more processing to be done
            if (chooser.getFileFilter() == FileFilters.XMI_FILTER) {
                DomainModel model = DomainModelFromXmiDialog.createDomainModel(
                    GridApplication.getContext().getApplication(), selectedFilename);
                if (model != null) {
                    String trimmedFileName = new File(selectedFilename).getName();
                    trimmedFileName = trimmedFileName.substring(0, trimmedFileName.length() - 4);
                    File convertedModelFile = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() 
                        + File.separator + "etc" + File.separator + trimmedFileName + ".xml");
                    try {
                        FileWriter modelWriter = new FileWriter(convertedModelFile);
                        MetadataUtils.serializeDomainModel(model, modelWriter);
                        modelWriter.flush();
                        modelWriter.close();
                        String[] message = {
                            "The selected XMI file " + selectedFilename,
                            "has been converted to a caGrid Domain Model and stored",
                            "as " + convertedModelFile.getName()
                        };
                        GridApplication.getContext().showMessage(message);
                        getFileTextField().setText(convertedModelFile.getAbsolutePath());
                        setSelectedDomainModelFilename(convertedModelFile.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error storing converted domain model", ex.getMessage(), ex);
                    }
                } else {
                    CompositeErrorDialog.showErrorDialog("Domain model was not generated");
                }
            } else {
                // use the model as-is
                getFileTextField().setText(selectedFilename);
                setSelectedDomainModelFilename(selectedFilename);
            }
        } else {
            // no selection, clear out model file name
            getFileTextField().setText("");
            setSelectedDomainModelFilename(null); // TODO: is this right?
        }
    }
	
	
	private void setSelectedDomainModelFilename(String filename) {
		// set the selected file on the data extension's info
		try {
			CadsrInformation info = getCadsrInformation();
			
			ResourcePropertyType dmResourceProp = getDomainModelResourceProperty();
			
			if (filename != null) {
				File selectedFile = new File(filename);
				File localDomainFile = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() 
					+ File.separator + "etc" + File.separator + selectedFile.getName());
				Utils.copyFile(selectedFile, localDomainFile);
				
				dmResourceProp.setPopulateFromFile(true);
				dmResourceProp.setFileLocation(localDomainFile.getName());
				info.setUseSuppliedModel(true);

				storeCadsrInformation(info);

				loadDomainModelFile();
			} else {
				info.setUseSuppliedModel(false);
				dmResourceProp.setPopulateFromFile(false);
				dmResourceProp.setFileLocation("");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			CompositeErrorDialog.showErrorDialog("Error copying selected file to service", ex);
		}
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDmFilePanel() {
		if (dmFilePanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints5.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 0;
			dmFilePanel = new JPanel();
			dmFilePanel.setLayout(new GridBagLayout());
			dmFilePanel.add(getFileLabel(), gridBagConstraints3);
			dmFilePanel.add(getFileTextField(), gridBagConstraints4);
			dmFilePanel.add(getBrowseButton(), gridBagConstraints5);
		}
		return dmFilePanel;
	}
	
	
	private InternalCaDSRBrowserPanel getCaDsrBrowser() {
		if (caDsrBrowser == null) {
			caDsrBrowser = new InternalCaDSRBrowserPanel(true, false);
			String url = ResourceManager.getServiceURLProperty(DataServiceConstants.CADSR_SERVICE_URL);
			caDsrBrowser.setDefaultCaDSRURL(url);
			caDsrBrowser.getCadsr().setText(url);
		}
		return caDsrBrowser;
	}


	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getSelectedPackagesList() {
		if (selectedPackagesList == null) {
			selectedPackagesList = new JList();
			selectedPackagesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		return selectedPackagesList;
	}


	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getSelectedPackagesScrollPane() {
		if (selectedPackagesScrollPane == null) {
			selectedPackagesScrollPane = new JScrollPane();
			selectedPackagesScrollPane.setViewportView(getSelectedPackagesList());
			selectedPackagesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Selected Packages", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
		}
		return selectedPackagesScrollPane;
	}


    /**
     * This method initializes addProjectButton 
     *  
     * @return javax.swing.JButton  
     */
    private JButton getAddProjectButton() {
        if (addProjectButton == null) {
            addProjectButton = new JButton();
            addProjectButton.setText("Add Project");
            addProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (validateProjectSelection()) {
                        BusyDialogRunnable bdr = new BusyDialogRunnable(
                            GridApplication.getContext().getApplication(), "Loading All Packages from Project") {
                            public void process() {
                                getAddProjectButton().setEnabled(false);
                                setProgressText("Finding packages in selected project");
                                UMLPackageMetadata[] packages = getCaDsrBrowser().getAvailablePackages();
                                int current = 1;
                                setProgressText("Adding packages to model");
                                for (UMLPackageMetadata pack : packages) {
                                    setProgressText("Adding package " + current + 
                                        " of " + packages.length + " (" + pack.getName() + ")");
                                    // the add method already ignores existing packages
                                    addUmlPackage(pack);
                                    current++;
                                }
                                getAddProjectButton().setEnabled(true);
                            }
                        };
                        Thread runner = new Thread(bdr);
                        runner.start();
                    }
                }
            });
        }
        return addProjectButton;
    }


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddPackageButton() {
		if (addPackageButton == null) {
			addPackageButton = new JButton();
			addPackageButton.setText("Add Package");
			addPackageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    if (validateProjectSelection()) {
				        UMLPackageMetadata pack = getCaDsrBrowser().getSelectedPackage();
				        if (pack != null) {
				            addUmlPackage(pack);
				        }
                    }
				}
			});
		}
		return addPackageButton;
	}


	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemovePackageButton() {
		if (removePackageButton == null) {
			removePackageButton = new JButton();
			removePackageButton.setText("Remove");
			removePackageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Object[] selection = getSelectedPackagesList().getSelectedValues();
					if (selection != null && selection.length != 0) {
						String[] names = new String[selection.length];
						for (int i = 0; i < selection.length; i++) {
							names[i] = selection[i].toString();
						}
						removeUmlPackages(names);
					}
				}
			});
		}
		return removePackageButton;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPackageButtonsPanel() {
		if (packageButtonsPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setHgap(4);
			gridLayout.setColumns(3);
			packageButtonsPanel = new JPanel();
			packageButtonsPanel.setLayout(gridLayout);
			packageButtonsPanel.add(getAddProjectButton(), null);
			packageButtonsPanel.add(getAddPackageButton(), null);
			packageButtonsPanel.add(getRemovePackageButton(), null);
		}
		return packageButtonsPanel;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCaDsrPanel() {
		if (caDsrPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.weighty = 1.0;
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridy = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridheight = 2;
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 0;
			caDsrPanel = new JPanel();
			caDsrPanel.setLayout(new GridBagLayout());
			caDsrPanel.add(getCaDsrBrowser(), gridBagConstraints8);
			caDsrPanel.add(getPackageButtonsPanel(), gridBagConstraints9);
			caDsrPanel.add(getSelectedPackagesScrollPane(), gridBagConstraints10);
		}
		return caDsrPanel;
	}
    
    
    private boolean validateProjectSelection() {
        // get the selected project
        Project proj = getCaDsrBrowser().getSelectedProject();
        if (lastSelectedProject != null) {
            // verify the project is still the same
            if (!lastSelectedProject.getLongName().equals(proj.getLongName()) ||
                !lastSelectedProject.getVersion().equals(proj.getVersion())) {
                // they don't match...
                String[] message = {
                    "The selected project is not the same as the project",
                    "to which the previously selected packages belong.",
                    "To use this project, all currently selected packages",
                    "must be removed from the domain model.",
                    "Procede?"
                };
                int choice = JOptionPane.showConfirmDialog(
                    DomainModelPanel.this, message, "Project Conflict", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // remove all packages from list, set the last selected project
                    getSelectedPackagesList().setListData(new String[0]);
                } else {
                    // user selected no, so bail out
                    return false;
                }
            }
        }
        lastSelectedProject = proj;
        return true;
    }
	
	
	private void addUmlPackage(UMLPackageMetadata pack) {
		// see if the package name is new
		boolean newName = true;
		for (int i = 0; i < getSelectedPackagesList().getModel().getSize(); i++) {
			if (pack.getName().equals(getSelectedPackagesList().getModel().getElementAt(i))) {
				newName = false;
				break;
			}
		}
		
		if (newName) {
			// add the package name to the list
			String[] names = new String[getSelectedPackagesList().getModel().getSize() + 1];
			for (int i = 0; i < getSelectedPackagesList().getModel().getSize(); i++) {
				names[i] = (String) getSelectedPackagesList().getModel().getElementAt(i);
			}
			names[names.length - 1] = pack.getName();
			getSelectedPackagesList().setListData(names);
			// add the package to the cadsr information in extension data
			try {
				CadsrInformation info = getCadsrInformation();
				// create cadsr package for the new metadata package
				CadsrPackage newPackage = new CadsrPackage();
				newPackage.setName(pack.getName());
				newPackage.setCadsrClass(getClassMappings(lastSelectedProject, pack));
				newPackage.setMappedNamespace(NamespaceUtils.createNamespaceString(lastSelectedProject, pack));			
				CadsrPackage[] packages = info.getPackages();
				if (packages == null) {
					packages = new CadsrPackage[] {newPackage};
				} else {
					packages = (CadsrPackage[]) Utils.appendToArray(packages, newPackage);
				}
				info.setPackages(packages);
				info.setServiceUrl(getCaDsrBrowser().getCadsr().getText());
				info.setProjectLongName(lastSelectedProject.getLongName());
				info.setProjectVersion(lastSelectedProject.getVersion());
				storeCadsrInformation(info);
			} catch (Exception ex) {
				ex.printStackTrace();
				CompositeErrorDialog.showErrorDialog("Error storing the new package information", ex);
			}
		}
	}
	
	
	private ClassMapping[] getClassMappings(Project proj, UMLPackageMetadata pack) throws Exception {
		CaDSRServiceClient client = new CaDSRServiceClient(getCaDsrBrowser().getCadsr().getText());
		UMLClassMetadata[] classMdArray = client.findClassesInPackage(proj, pack.getName());
		ClassMapping[] mappings = new ClassMapping[] {};
		if (classMdArray != null) {
			mappings = new ClassMapping[classMdArray.length];
			for (int i = 0; i < classMdArray.length; i++) {
				ClassMapping map = new ClassMapping();
				map.setClassName(classMdArray[i].getName());
				map.setElementName(classMdArray[i].getName());
				map.setSelected(true);
				map.setTargetable(true);
				mappings[i] = map;
			}
		}
		return mappings;
	}
	
	
	private void removeUmlPackages(String[] packageNames) {
		// change the gui
		Set<String> selected = new HashSet<String>();
		Collections.addAll(selected, packageNames);
		Vector<String> remaining = new Vector<String>();
		for (int i = 0; i < getSelectedPackagesList().getModel().getSize(); i++) {
			String name = (String) getSelectedPackagesList().getModel().getElementAt(i);
			if (!selected.contains(name)) {
				remaining.add(name);
			}
		}
		getSelectedPackagesList().setListData(remaining);
		// if everything has been removed, also remove the last selected project
		if (remaining.size() == 0) {
			lastSelectedProject = null;
		}
		// change the data model
		try {
			CadsrInformation information = getCadsrInformation();
			CadsrPackage[] packages = information.getPackages();
			List<CadsrPackage> remainingPackages = new ArrayList<CadsrPackage>();
			for (int i = 0; i < packages.length; i++) {
				if (!selected.contains(packages[i].getName())) {
					remainingPackages.add(packages[i]);
				}
			}
			CadsrPackage[] remainingPackagesArray = new CadsrPackage[remainingPackages.size()];
			remainingPackages.toArray(remainingPackagesArray);
			information.setPackages(remainingPackagesArray);
			storeCadsrInformation(information);
		} catch (Exception ex) {
			ex.printStackTrace();
			CompositeErrorDialog.showErrorDialog("Error removing the selected packages from the model", ex);
		}
	}
	
	
	private void storeNoDomainModelState() {
		try {
			CadsrInformation info = getCadsrInformation();
			info.setNoDomainModel(getNoDomainModelRadioButton().isSelected());
			storeCadsrInformation(info);
		} catch (Exception ex) {
			ex.printStackTrace();
			CompositeErrorDialog.showErrorDialog("Error storing 'no domain model' flag state", ex);
		}		
	}
	
	
	private void loadDomainModelFile() {
		String domainModelFile = getFileTextField().getText();
		if (domainModelFile != null && domainModelFile.length() != 0) {
			try {
				// get the domain model
				DomainModel model = MetadataUtils.deserializeDomainModel(new FileReader(domainModelFile));
				// get extension data
				CadsrInformation info = getCadsrInformation();
				// set the most recent project information
				Project proj = new Project();
				proj.setDescription(model.getProjectDescription());
				proj.setLongName(model.getProjectLongName());
				proj.setShortName(model.getProjectShortName());
				proj.setVersion(model.getProjectVersion());
				lastSelectedProject = proj;
				// set cadsr project information
				info.setProjectLongName(model.getProjectLongName());
				info.setProjectVersion(model.getProjectVersion());
				// walk classes, creating package groupings as needed
				Map<String, List<String>> packageClasses = new HashMap<String, List<String>>();
				UMLClass[] modelClasses = model.getExposedUMLClassCollection().getUMLClass(); 
				for (int i = 0; i < modelClasses.length; i++) {
					String packageName = modelClasses[i].getPackageName();
                    List<String> classList = null;
					if (packageClasses.containsKey(packageName)) {
                        classList = packageClasses.get(packageName);
					} else {
						classList = new ArrayList<String>();
						packageClasses.put(packageName, classList);
					}
                    classList.add(modelClasses[i].getClassName());
				}
				// create cadsr packages
				CadsrPackage[] packages = new CadsrPackage[packageClasses.keySet().size()];
                String[] packageNames = new String[packages.length];
				int packIndex = 0;
				Iterator packageNameIter = packageClasses.keySet().iterator();
				while (packageNameIter.hasNext()) {
					String packName = (String) packageNameIter.next();
					String mappedNamespace = NamespaceUtils.createNamespaceString(
						model.getProjectShortName(), model.getProjectVersion(), packName);
					CadsrPackage pack = new CadsrPackage();
					pack.setName(packName);
					pack.setMappedNamespace(mappedNamespace);
					// create ClassMappings for the package's classes
					List<String> classNameList = packageClasses.get(packName);
					ClassMapping[] mappings = new ClassMapping[classNameList.size()];
					for (int i = 0; i < classNameList.size(); i++) {
						ClassMapping mapping = new ClassMapping();
						String className = classNameList.get(i);
						mapping.setClassName(className);
						mapping.setElementName(className);
						mapping.setSelected(true);
						mapping.setTargetable(true);
						mappings[i] = mapping;
					}
					pack.setCadsrClass(mappings);
					packages[packIndex] = pack;
                    packageNames[packIndex] = pack.getName();
					packIndex++;
				}
				info.setPackages(packages);
                getSelectedPackagesList().setListData(packageNames);
				storeCadsrInformation(info);
			} catch (Exception ex) {
				ex.printStackTrace();
				CompositeErrorDialog.showErrorDialog("Error loading domain model information", ex);
			}
		}
	}
	
	
	private ResourcePropertyType getDomainModelResourceProperty() {
		ServiceType baseService = getServiceInformation().getServices().getService(0);

		ResourcePropertyType[] typedProps = CommonTools.getResourcePropertiesOfType(
			getServiceInformation().getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
		if (typedProps == null || typedProps.length == 0) {
			ResourcePropertyType dmProp = new ResourcePropertyType();
			dmProp.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);
			dmProp.setRegister(true);
			CommonTools.addResourcePropety(baseService, dmProp);
			return dmProp;
		} else {
			return typedProps[0];
		}
	}
	
	
	private CadsrInformation getCadsrInformation() throws Exception {
		Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
		CadsrInformation info = data.getCadsrInformation();
		if (info == null) {
			info = new CadsrInformation();
			info.setNoDomainModel(getNoDomainModelRadioButton().isSelected());
			info.setUseSuppliedModel(getFromFileRadioButton().isSelected());
			data.setCadsrInformation(info);
			ExtensionDataUtils.storeExtensionData(getExtensionData(), data);
		}
		return info;
	}
	
	
	private void storeCadsrInformation(CadsrInformation info) throws Exception {
		Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
		data.setCadsrInformation(info);
		ExtensionDataUtils.storeExtensionData(getExtensionData(), data);
	}
    
    
    private static class InternalCaDSRBrowserPanel extends CaDSRBrowserPanel {
        
        public InternalCaDSRBrowserPanel() {
            super();
        }
        
        
        public InternalCaDSRBrowserPanel(boolean showQueryPanel, boolean showClassSelection) {
            super(showQueryPanel, showClassSelection);
        }
        
        
        public UMLPackageMetadata[] getAvailablePackages() {
            int count = getPackageComboBox().getItemCount();
            List<UMLPackageMetadata> packs = new ArrayList<UMLPackageMetadata>();
            for (int i = 0; i < count; i++) {
                Object o = getPackageComboBox().getItemAt(i);
                if (o instanceof PackageDisplay) {
                    packs.add(((PackageDisplay) o).getPackage());
                }
            }
            UMLPackageMetadata[] packArray = new UMLPackageMetadata[packs.size()];
            packs.toArray(packArray);
            return packArray;
        }
    }
}

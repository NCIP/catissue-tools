package gov.nih.nci.cagrid.data.ui.domain;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;
import gov.nih.nci.cadsr.umlproject.domain.UMLPackageMetadata;
import gov.nih.nci.cagrid.cadsr.client.CaDSRServiceClient;
import gov.nih.nci.cagrid.cadsr.portal.CaDSRBrowserPanel;
import gov.nih.nci.cagrid.cadsr.portal.discovery.CaDSRDiscoveryConstants;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.common.portal.PortalUtils;
import gov.nih.nci.cagrid.common.portal.PromptButtonDialog;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.codegen.DomainModelCreationUtil;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.extension.CadsrInformation;
import gov.nih.nci.cagrid.data.extension.CadsrPackage;
import gov.nih.nci.cagrid.data.extension.ClassMapping;
import gov.nih.nci.cagrid.data.ui.DataServiceModificationSubPanel;
import gov.nih.nci.cagrid.data.ui.NamespaceUtils;
import gov.nih.nci.cagrid.data.ui.SchemaResolutionDialog;
import gov.nih.nci.cagrid.data.ui.tree.CheckTreeSelectionEvent;
import gov.nih.nci.cagrid.data.ui.tree.CheckTreeSelectionListener;
import gov.nih.nci.cagrid.data.ui.tree.uml.UMLClassTreeNode;
import gov.nih.nci.cagrid.data.ui.tree.uml.UMLPackageTreeNode;
import gov.nih.nci.cagrid.data.ui.tree.uml.UMLProjectTree;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.BusyDialogRunnable;
import org.cagrid.grape.utils.CompositeErrorDialog;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * DomainModelConfigPanel 
 * Panel to contain all the configuration of the domain model
 * 
 * @author David Ervin
 * 
 * @created Apr 11, 2007 9:59:24 AM
 * @version $Id: DomainModelConfigPanel.java,v 1.10 2008/01/24 18:03:14 dervin Exp $
 */
public class DomainModelConfigPanel extends DataServiceModificationSubPanel {

    private transient List<DomainModelClassSelectionListener> classSelectionListeners = null;
    private transient Project mostRecentProject;
    private transient DomainModel installedDomainModel = null;
    private transient boolean currentlyFromCadsr;
    private transient Set<String> warnedMissingNamespaces = null;

    private JButton visualizeDomainModelButton = null;
    private JPanel cadsrDomainModelPanel = null;
    private CaDSRBrowserPanel cadsrBrowserPanel = null;
    private JPanel packageSelectionButtonPanel = null;
    private JButton addFullProjectButton = null;
    private JButton addPackageButton = null;
    private JButton removePackageButton = null;
    private JScrollPane umlClassScrollPane = null;
    private UMLProjectTree umlTree = null;
    private JButton advancedOptionsButton = null;
    private JPanel specialtyButtonsPanel = null;


    public DomainModelConfigPanel(ServiceInformation info, ExtensionDataManager dataManager) {
        super(info, dataManager);
        this.classSelectionListeners = new LinkedList<DomainModelClassSelectionListener>();
        this.warnedMissingNamespaces = new HashSet<String>();
        initialize();
    }


    public void updateDisplayedConfiguration() {
        boolean noDomainModel = false;
        boolean suppliedDomainModel = false;
        try {
            noDomainModel = getExtensionDataManager().isNoDomainModel();
            suppliedDomainModel = getExtensionDataManager().isSuppliedDomainModel();
            // handle possibility that the domain model source has just been set to 'none'
            CadsrInformation cadsrInfo = getExtensionDataManager().getCadsrInformation();
            if (noDomainModel && cadsrInfo.getPackages() != null && cadsrInfo.getPackages().length != 0) {
                Set<String> shownPackages = getUmlTree().getPackagesInTree();
                for (CadsrPackage pack : cadsrInfo.getPackages()) {
                    if (shownPackages.contains(pack.getName())) {
                        getUmlTree().removeUmlPackage(pack.getName());
                    }
                }
                getExtensionDataManager().storeCadsrPackages(null);
                fireClassesCleared();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error loading domain model source", 
                ex.getMessage(), ex);
        }
        // only enabled if domain model from caDSR
        PortalUtils.setContainerEnabled(getCadsrBrowserPanel(), 
            !noDomainModel && !suppliedDomainModel);
        getAddFullProjectButton().setEnabled(!noDomainModel && !suppliedDomainModel);
        getAddPackageButton().setEnabled(!noDomainModel && !suppliedDomainModel);
        getRemovePackageButton().setEnabled(!noDomainModel && !suppliedDomainModel);
        if (suppliedDomainModel) {
            currentlyFromCadsr = false;
            System.out.println("The domain model is supplied");
            try {
                File dmFile = getSuppliedDomainModelFile();
                FileReader reader = new FileReader(dmFile.getAbsolutePath());
                DomainModel model = MetadataUtils.deserializeDomainModel(reader);
                reader.close();
                if (!model.equals(installedDomainModel)) {
                    installDomainModel(model);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog("Error loading domain model",
                    ex.getMessage(), ex);
            }
        } else {
            installedDomainModel = null;
            if (!noDomainModel && !suppliedDomainModel && !currentlyFromCadsr) {
                currentlyFromCadsr = true;
                firstCadsrBrowserInit();
            }
        }
        getUmlTree().setEnabled(!(noDomainModel || suppliedDomainModel));
        if (!getUmlTree().isEnabled()) {
            getUmlTree().expandFullTree();
        }
    }
    
    
    private synchronized void firstCadsrBrowserInit() {
        // domain model must be from caDSR
        try {
            // get the project from cadsr that has been selected
            String projectLongName = getExtensionDataManager().getCadsrProjectLongName();
            String projectVersion = getExtensionDataManager().getCadsrProjectVersion();
            String caDsrUrl = getExtensionDataManager().getCadsrUrl();
            // set the cadsr URL to that used in the extension data
            if (caDsrUrl != null) {
                getCadsrBrowserPanel().getCadsr().setText(caDsrUrl);
            }
            // find the requested project in the caDSR
            CaDSRServiceClient cadsrClient = new CaDSRServiceClient(
                getCadsrBrowserPanel().getCadsr().getText());
            Project[] allProjects = cadsrClient.findAllProjects();
            for (Project proj : allProjects) {
                if (proj.getLongName().equals(projectLongName) 
                    && proj.getVersion().equals(projectVersion)) {
                    mostRecentProject = proj;
                    break;
                }
            }
            // populate the UI's packages and classes
            List<String> packageNames = getExtensionDataManager().getCadsrPackageNames();
            if (packageNames != null) {
                for (String packName : packageNames) {
                    if (getUmlTree().getUmlPackageNode(packName) == null) {
                        // package isn't in the tree yet
                        getUmlTree().addUmlPackage(packName);
                        List<ClassMapping> mappings = getExtensionDataManager()
                            .getClassMappingsInPackage(packName);
                        for (ClassMapping mapping : mappings) {
                            UMLClassTreeNode classNode = getUmlTree().addUmlClass(
                                packName, mapping.getClassName());
                            classNode.getCheckBox().setSelected(mapping.isSelected());
                        }
                    }
                }
            }
            currentlyFromCadsr = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error loading cadsr domain model information", 
                ex.getMessage(), ex);
        }
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0D;
        gridBagConstraints1.weighty = 1.0D;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(501, 570));
        this.add(getCadsrDomainModelPanel(), gridBagConstraints1);
        this.add(getSpecialtyButtonsPanel(), gridBagConstraints2);
    }


    private JPanel getCadsrDomainModelPanel() {
        if (cadsrDomainModelPanel == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = GridBagConstraints.BOTH;
            gridBagConstraints14.gridy = 2;
            gridBagConstraints14.weightx = 1.0;
            gridBagConstraints14.weighty = 1.0D;
            gridBagConstraints14.gridx = 0;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.gridy = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 0;
            cadsrDomainModelPanel = new JPanel();
            cadsrDomainModelPanel.setLayout(new GridBagLayout());
            cadsrDomainModelPanel.add(getCadsrBrowserPanel(), gridBagConstraints12);
            cadsrDomainModelPanel.add(getPackageSelectionButtonPanel(), gridBagConstraints13);
            cadsrDomainModelPanel.add(getUmlClassScrollPane(), gridBagConstraints14);
        }
        return cadsrDomainModelPanel;
    }


    private CaDSRBrowserPanel getCadsrBrowserPanel() {
        if (cadsrBrowserPanel == null) {
            cadsrBrowserPanel = new CaDSRBrowserPanel(true, false);
            // get the URL of the cadsr service
            String cadsrUrl = null;
            try {
                cadsrUrl = getExtensionDataManager().getCadsrUrl();
            } catch (Exception ex) {
                ex.printStackTrace();
                String[] message = new String[]{
                        "There was an error loading the caDSR service url from the extension data.",
                        "The URL specified in the Introduce configuration will be used."};
                CompositeErrorDialog.showErrorDialog("Error getting caDSR Service url", message, ex);
            }
            if (cadsrUrl == null || cadsrUrl.length() == 0) {
                // grab the default URL and store it in the extension data
                cadsrUrl = ResourceManager.getServiceURLProperty(CaDSRDiscoveryConstants.CADSR_URL_PROPERTY);
                try {
                    getExtensionDataManager().storeCadsrServiceUrl(cadsrUrl);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error storing caDSR url", ex.getMessage(), ex);
                }
            }
            // set the URL appropriatly in the GUI
            cadsrBrowserPanel.getCadsr().setText(cadsrUrl);
            // add listener to the cadsr URL text field
            cadsrBrowserPanel.getCadsr().getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    try {
                        getExtensionDataManager().storeCadsrServiceUrl(
                            getCadsrBrowserPanel().getCadsr().getText());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error storing domain model source", ex.getMessage(), ex);
                    }
                }
            });
        }
        return cadsrBrowserPanel;
    }


    private JPanel getPackageSelectionButtonPanel() {
        if (packageSelectionButtonPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 2;
            gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 0;
            packageSelectionButtonPanel = new JPanel();
            packageSelectionButtonPanel.setLayout(new GridBagLayout());
            packageSelectionButtonPanel.add(getAddFullProjectButton(), gridBagConstraints2);
            packageSelectionButtonPanel.add(getAddPackageButton(), gridBagConstraints3);
            packageSelectionButtonPanel.add(getRemovePackageButton(), gridBagConstraints4);
        }
        return packageSelectionButtonPanel;
    }


    private JButton getAddFullProjectButton() {
        if (addFullProjectButton == null) {
            addFullProjectButton = new JButton();
            addFullProjectButton.setText("Add Full Project");
            addFullProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Project selectedProject = getCadsrBrowserPanel().getSelectedProject();
                    if (selectedProject != null) {
                        // contact the caDSR for all packages in the project
                        try {
                            CaDSRServiceClient cadsrClient = new CaDSRServiceClient(
                                getCadsrBrowserPanel().getCadsr().getText());
                            UMLPackageMetadata[] umlPackages = 
                                cadsrClient.findPackagesInProject(selectedProject);
                            // only one project validation for all packages
                            if (verifyProjectSelection(selectedProject)) {
                                for (UMLPackageMetadata pack : umlPackages) {
                                    addUmlPackageToModel(selectedProject, pack);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            CompositeErrorDialog.showErrorDialog("Error loading packages from project", 
                                ex.getMessage(), ex);
                        }
                    } else {
                        GridApplication.getContext().showMessage("Please select a project");
                    }
                }
            });
        }
        return addFullProjectButton;
    }


    private JButton getAddPackageButton() {
        if (addPackageButton == null) {
            addPackageButton = new JButton();
            addPackageButton.setText("Add Package");
            addPackageButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Project selectedProject = getCadsrBrowserPanel().getSelectedProject();
                    UMLPackageMetadata selectedPackage = cadsrBrowserPanel.getSelectedPackage();
                    if (selectedProject != null && selectedPackage != null) {
                        // ensure the selected project is consistent with current domain model
                        if (verifyProjectSelection(selectedProject)) {
                            // ensure the package is not already present in the model
                            try {
                                if (!packageUsedInModel(selectedPackage)) {
                                    // add the package to the model
                                    addUmlPackageToModel(selectedProject, selectedPackage);
                                } else {
                                    GridApplication.getContext().showMessage(
                                    "The selected package is already part of the domain model");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                CompositeErrorDialog.showErrorDialog("Error determining package use", ex.getMessage(), ex);
                            }
                        }
                    } else {
                        GridApplication.getContext().showMessage("Please select both a project and package");
                    }
                }
            });
        }
        return addPackageButton;
    }


    private JButton getRemovePackageButton() {
        if (removePackageButton == null) {
            removePackageButton = new JButton();
            removePackageButton.setText("Remove Package");
            removePackageButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Project selectedProject = getCadsrBrowserPanel().getSelectedProject();
                    UMLPackageMetadata selectedPackage = getCadsrBrowserPanel().getSelectedPackage();
                    if (selectedProject != null && selectedPackage != null) {
                        boolean removed = removeUmlPackage(selectedProject, selectedPackage.getName());
                        if (!removed) {
                            ErrorDialog.showError("The selected package was not removed");
                        }
                    } else {
                        GridApplication.getContext().showMessage("Please select both a project and package");
                    }
                }
            });
        }
        return removePackageButton;
    }


    private JScrollPane getUmlClassScrollPane() {
        if (umlClassScrollPane == null) {
            umlClassScrollPane = new JScrollPane();
            umlClassScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null, "UML Class Selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, 
                PortalLookAndFeel.getPanelLabelColor()));
            umlClassScrollPane.setViewportView(getUmlTree());
        }
        return umlClassScrollPane;
    }


    private UMLProjectTree getUmlTree() {
        if (umlTree == null) {
            umlTree = new UMLProjectTree();
            umlTree.addCheckTreeSelectionListener(new CheckTreeSelectionListener() {
                public void nodeChecked(CheckTreeSelectionEvent e) {
                    if (e.getNode() instanceof UMLClassTreeNode) {
                        UMLClassTreeNode classNode = (UMLClassTreeNode) e.getNode();
                        // add the type to the configuration table
                        String packName = ((UMLPackageTreeNode) classNode.getParent()).getPackageName();
                        String className = classNode.getClassName();
                        try {
                            // need the class mapping
                            ClassMapping mapping = getExtensionDataManager()
                                .getClassMapping(packName, className);
                            // and the namespace type
                            String namespace = getExtensionDataManager()
                                .getMappedNamespaceForPackage(packName);
                            NamespaceType packageNamespace = CommonTools.getNamespaceType(
                                getServiceInfo().getNamespaces(), namespace);
                            // if the namespace type has been removed from the service,
                            // there's no way the domain type can be used in the data service
                            if (packageNamespace == null) {
                                if (!warnedMissingNamespaces.contains(namespace)) {
                                    String[] message = new String[] {
                                        "The selected class maps to the XML namespace",
                                        namespace,
                                        "which could not be found in the service."
                                    };
                                    JOptionPane.showMessageDialog(DomainModelConfigPanel.this, message,
                                        "Missing Namespace", JOptionPane.ERROR_MESSAGE);
                                }
                                // cannot check this node
                                classNode.getCheckBox().setSelected(false);
                                warnedMissingNamespaces.add(namespace);
                            } else {
                                // inform interested parties of the selection
                                fireClassSelected(packName, mapping, packageNamespace);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            CompositeErrorDialog.showErrorDialog("Error selecting class: " + ex.getMessage(), ex);
                        }
                    }
                }


                public void nodeUnchecked(CheckTreeSelectionEvent e) {
                    if (e.getNode() instanceof UMLClassTreeNode) {
                        UMLClassTreeNode classNode = (UMLClassTreeNode) e.getNode();
                        // add the type to the configuration table
                        String packName = ((UMLPackageTreeNode) classNode.getParent()).getPackageName();
                        String className = classNode.getClassName();
                        fireClassDeselected(packName, className);
                    }
                }
            });
        }
        return umlTree;
    }


    private JButton getVisualizeDomainModelButton() {
        if (visualizeDomainModelButton == null) {
            visualizeDomainModelButton = new JButton();
            visualizeDomainModelButton.setText("Visualize Domain Model");
            visualizeDomainModelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    visualizeDomainModel();
                }
            });
        }
        return visualizeDomainModelButton;
    }


    /**
     * This method initializes advancedOptionsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAdvancedOptionsButton() {
        if (advancedOptionsButton == null) {
            advancedOptionsButton = new JButton();
            advancedOptionsButton.setText("Advanced Options");
            advancedOptionsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    DomainModelAdvancedOptionsDialog dialog = new DomainModelAdvancedOptionsDialog(
                        getServiceInfo(), getExtensionDataManager());
                    dialog.setVisible(true);
                    updateDisplayedConfiguration();
                }
            });
        }
        return advancedOptionsButton;
    }


    /**
     * This method initializes specialtyButtonsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSpecialtyButtonsPanel() {
        if (specialtyButtonsPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            specialtyButtonsPanel = new JPanel();
            specialtyButtonsPanel.setLayout(new GridBagLayout());
            specialtyButtonsPanel.add(getVisualizeDomainModelButton(), gridBagConstraints);
            specialtyButtonsPanel.add(getAdvancedOptionsButton(), gridBagConstraints1);
        }
        return specialtyButtonsPanel;
    }


    //
    // ---
    // non UI related helpers
    // ---
    //

    private void visualizeDomainModel() {
        BusyDialogRunnable modelCreationRunnable = new BusyDialogRunnable(
            GridApplication.getContext().getApplication(), "Creating Domain Model") {
            public void process() {
                DomainModel model = null;
                boolean noModel = false;
                boolean isSupplied = false;
                setProgressText("Determining model's source");
                try {
                    noModel = getExtensionDataManager().isNoDomainModel();
                    isSupplied = getExtensionDataManager().isSuppliedDomainModel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CompositeErrorDialog.showErrorDialog("Error determining domain model source", 
                        ex.getMessage(), ex);
                }
                // get the domain model selected
                if (isSupplied) {
                    setProgressText("Loading from file system");
                    // domain model from file system
                    File suppliedFile = getSuppliedDomainModelFile();
                    if (suppliedFile != null) {
                        try {
                            model = MetadataUtils.deserializeDomainModel(new FileReader(suppliedFile));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            CompositeErrorDialog.showErrorDialog("Error loading domain model", ex.getMessage(), ex);
                        }
                    }
                } else if (!noModel) {
                    setProgressText("Creating Model From caDSR");
                    // build the domain model
                    try {
                        final CadsrInformation info = getExtensionDataManager().getCadsrInformation();
                        model = DomainModelCreationUtil.createDomainModel(info);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error getting caDSR information", ex.getMessage(), ex);
                    }
                }
                if (model != null) {
                    setProgressText("Visualizing domain model");
                    new DomainModelVisualizationDialog(
                        GridApplication.getContext().getApplication(), model);
                }
            }
        };
        Thread runner = new Thread(modelCreationRunnable);
        runner.start();
    }


    private boolean removeUmlPackage(Project proj, String packageName) {
        Set packageNames = getUmlTree().getPackagesInTree();
        if (!packageNames.contains(packageName)
            || (mostRecentProject != null && !projectEquals(proj, mostRecentProject))) {
            GridApplication.getContext().showMessage("The selected package is not involved in the model");
            return false;
        }
        // remove the package from the cadsr information
        try {
            getExtensionDataManager().removeCadsrPackage(packageName);
            String[] classNames = getUmlTree().getSelectedClassNames(packageName);
            for (String name : classNames) {
                fireClassDeselected(packageName, name);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error removing cadsr package: " + ex.getMessage(), ex);
            return false;
        }
        // clean out the UI
        getUmlTree().removeUmlPackage(packageName);
        return true;
    }


    private boolean installDomainModel(DomainModel model) {
        // clear the UML tree
        getUmlTree().clearTree();

        // clear out classes elsewhere
        fireClassesCleared();

        // set the most recent project information
        Project proj = new Project();
        proj.setDescription(model.getProjectDescription());
        proj.setLongName(model.getProjectLongName());
        proj.setShortName(model.getProjectShortName());
        proj.setVersion(model.getProjectVersion());

        // walk classes from the model, grouping classes by package
        Map<String, List<String>> packageToClass = new HashMap<String, List<String>>();
        if (model.getExposedUMLClassCollection() != null 
            && model.getExposedUMLClassCollection().getUMLClass() != null) {
            for (UMLClass umlClass : model.getExposedUMLClassCollection().getUMLClass()) {
                String packName = umlClass.getPackageName();
                List<String> classList = packageToClass.get(packName);
                if (classList == null) {
                    classList = new LinkedList<String>();
                    packageToClass.put(packName, classList);
                }
                classList.add(umlClass.getClassName());
            }
        }

        // walk packages names --> class lists, create CadsrPackages
        List<CadsrPackage> cadsrPackages = new ArrayList<CadsrPackage>(packageToClass.keySet().size());
        List<UMLClassTreeNode> addedClassNodes = new LinkedList<UMLClassTreeNode>();
        for (String packName : packageToClass.keySet()) {
            // get the namespace type for the package
            NamespaceType packageNamespace = getNamespaceForPackage(
                proj.getShortName(), proj.getVersion(), packName);
            // create the cadsr package
            CadsrPackage pack = new CadsrPackage();
            pack.setName(packName);
            pack.setMappedNamespace(packageNamespace.getNamespace());
            // create class mappings
            List<String> classNames = packageToClass.get(packName);
            ClassMapping[] mappings = createClassMappings(packName, packageNamespace, classNames);
            pack.setCadsrClass(mappings);
            // queue up the package for addition later
            cadsrPackages.add(pack);
            // add the package and classes to the UML tree
            getUmlTree().addUmlPackage(packName);
            for (ClassMapping mapping : mappings) {
                addedClassNodes.add(getUmlTree().addUmlClass(packName, mapping.getClassName()));
            }
        }
        // convert package list to an array
        CadsrPackage[] packageArray = new CadsrPackage[cadsrPackages.size()];
        cadsrPackages.toArray(packageArray);

        // keep this LAST in the order of operation, since errors can cause
        // the installation to fail part way through
        try {
            getExtensionDataManager().storeCadsrProjectInformation(proj);
            getExtensionDataManager().storeCadsrPackages(packageArray);
            mostRecentProject = proj;
            installedDomainModel = model;
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error storing domain model information", 
                ex.getMessage(), ex);
            return false;
        }
        
        // walk through the newly added class nodes, and check them off in the tree,
        // by definition, they are part of the domain model
        for (UMLClassTreeNode node : addedClassNodes) {
            node.getCheckBox().setSelected(true); // selecting the node causes the selection event to fire
        }
        return true;
    }


    private ClassMapping[] createClassMappings(String packageName, 
        NamespaceType packageNamespace, List<String> classNames) {
        CadsrPackage pack = new CadsrPackage();
        pack.setName(packageName);
        pack.setMappedNamespace(packageNamespace.getNamespace());
        Map<String, String> classToElement = NamespaceUtils.mapClassNamesToElementNames(
            classNames, packageNamespace);
        // create ClassMappings for the package's classes
        List<ClassMapping> mappingList = new ArrayList<ClassMapping>(classNames.size());
        for (String className : classNames) {
            ClassMapping mapping = new ClassMapping();
            mapping.setClassName(className);
            mapping.setElementName(classToElement.get(className));
            // TODO: determine if these should be true or false by default
            mapping.setSelected(true);
            mapping.setTargetable(true);
            mappingList.add(mapping);
        }
        ClassMapping[] mappings = new ClassMapping[classNames.size()];
        mappingList.toArray(mappings);
        return mappings;
    }


    private NamespaceType getNamespaceForPackage(String projectShortName, 
        String projectVersion, String packageName) {
        String mappedNamespace = null;
        try {
            mappedNamespace = getExtensionDataManager().getMappedNamespaceForPackage(packageName);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error loading mapped namespace", ex.getMessage(), ex);
        }
        if (mappedNamespace == null) {
            // fall back to the default mapping
            mappedNamespace = NamespaceUtils.createNamespaceString(
                projectShortName, projectVersion, packageName);
        }
        // try to get the namespace type from the service model
        NamespaceType packageNamespace = CommonTools.getNamespaceType(
            getServiceInfo().getNamespaces(), mappedNamespace);
        if (packageNamespace == null) {
            // namespace not found, get the user to import it
            String[] message = {"The imported domain model has a package which maps to the namespace",
                mappedNamespace + ".", 
                "This namespace is not loaded into the service.",
                "Please locate a suitable namespace."};
            JOptionPane.showMessageDialog(GridApplication.getContext().getApplication(), message);
            NamespaceType[] resolved = SchemaResolutionDialog.resolveSchemas(getServiceInfo());
            if (resolved == null || resolved.length == 0) {
                // user didn't map it... show a warning
                String[] error = {"The package " + packageName + " was not mapped to a namespace.",
                    "This can cause errors when the service builds."};
                CompositeErrorDialog.showErrorDialog("No namespace mapping provided", error);
                return null;
            } else {
                // add the resolved namespaces to the service
                for (NamespaceType ns : resolved) {
                    CommonTools.addNamespace(getServiceInfo().getServiceDescriptor(), ns);
                }
                packageNamespace = resolved[0];
            }
        }
        return packageNamespace;
    }


    private boolean addUmlPackageToModel(Project project, UMLPackageMetadata pack) {
        // store the project selection
        try {
            getExtensionDataManager().storeCadsrProjectInformation(project);
            mostRecentProject = project;
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error storing project selection", ex.getMessage(), ex);
            return false;
        }

        // can now add the package's contents to the UI
        // do we have a namespace for the package?
        NamespaceType packageNamespace = getNamespaceForPackage(
            project.getShortName(), project.getVersion(), pack.getName());
        if (packageNamespace == null) {
            return false;
        }
        // store the new package stub in the extension data
        CadsrPackage cadsrPack = new CadsrPackage();
        cadsrPack.setName(pack.getName());
        cadsrPack.setMappedNamespace(packageNamespace.getNamespace());
        try {
            getExtensionDataManager().storeCadsrPackage(cadsrPack);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error storing new cadsr package", ex.getMessage(), ex);
            return false;
        }

        // add the package to the UML model tree
        getUmlTree().addUmlPackage(pack.getName());

        // get classes out of the package using the cadsr
        try {
            CaDSRServiceClient cadsrClient = new CaDSRServiceClient(
                getCadsrBrowserPanel().getCadsr().getText());
            // TODO: potentially slow, use some kind of threading
            UMLClassMetadata[] classes = cadsrClient.findClassesInPackage(project, pack.getName());
            List<String> classNames = new ArrayList<String>(classes.length);
            for (UMLClassMetadata currentClass : classes) {
                classNames.add(currentClass.getName());
            }
            ClassMapping[] classMappings = createClassMappings(
                pack.getName(), packageNamespace, classNames);
            cadsrPack.setCadsrClass(classMappings);
            getExtensionDataManager().storeCadsrPackage(cadsrPack);
            // add the classes to the UML tree
            String packName = pack.getName();
            for (ClassMapping mapping : classMappings) {
                UMLClassTreeNode classNode = getUmlTree().addUmlClass(packName, mapping.getClassName());
                classNode.getCheckBox().setSelected(mapping.isSelected());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error getting classes for package", ex.getMessage(), ex);
        }

        return true;
    }


    private boolean verifyProjectSelection(Project project) {
        if (mostRecentProject != null && !projectEquals(mostRecentProject, project)) {
            // package comes from a different project, which is not allowed
            // prompt the developer about it
            String[] choices = {"Remove all other packages and insert", "Cancel"};
            String[] message = {"Domain models may only be derived from one project.",
                "To add the package you've selected, all other packages",
                "currently in the domain model will have to be removed.", 
                "Should this operation procede?"};
            String choice = PromptButtonDialog.prompt(GridApplication.getContext().getApplication(),
                "Package incompatability...", message, choices, choices[1]);
            if (choice == choices[0]) {
                // user has elected to go with this project / package
                // combination
                // there are likley namespaces added to the service from
                // previous
                // package selections. These should be removed.
                for (String packageName : getUmlTree().getPackagesInTree()) {
                    try {
                        String mappedNamespace = 
                            getExtensionDataManager().getMappedNamespaceForPackage(packageName);
                        NamespaceType nsType = CommonTools.getNamespaceType(getServiceInfo().getNamespaces(),
                            mappedNamespace);
                        if (!CommonTools.isNamespaceTypeInUse(nsType, getServiceInfo().getServiceDescriptor())) {
                            NamespaceType[] allNamespaces = getServiceInfo().getNamespaces().getNamespace();
                            NamespaceType[] cleanedNamespaces = (NamespaceType[]) Utils.removeFromArray(
                                allNamespaces, nsType);
                            getServiceInfo().getNamespaces().setNamespace(cleanedNamespaces);
                        }
                        // clear the package out of the UML tree
                        getUmlTree().removeUmlPackage(packageName);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error removing namespace for package", 
                            ex.getMessage(), ex);
                        return false;
                    }
                }
                // inform whom it may concern that classes were cleared
                fireClassesCleared();

                // change the most recent project
                mostRecentProject = project;
            } else {
                return false;
            }
        }
        return true;
    }


    private File getSuppliedDomainModelFile() {
        ResourcePropertyType[] domainModelProps = CommonTools.getResourcePropertiesOfType(
            getServiceInfo().getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
        if (domainModelProps != null && domainModelProps.length != 0) {
            return new File(getServiceInfo().getBaseDirectory().getAbsolutePath() + File.separator 
                + "etc" + File.separator + domainModelProps[0].getFileLocation());
        }
        return null;
    }
    
    
    private boolean packageUsedInModel(UMLPackageMetadata pack) throws Exception {
        List<String> names = getExtensionDataManager().getCadsrPackageNames();
        return (names != null && names.contains(pack.getName()));
    }


    /**
     * p1 must be non-null!!
     * 
     * @param p1
     * @param p2
     * @return True if the projects have the same values for long name and
     *         version
     */
    private boolean projectEquals(Project p1, Project p2) {
        if (p2 != null) {
            return p1.getLongName().equals(p2.getLongName()) 
                && p1.getVersion().equals(p2.getVersion());
        }
        return false;
    }


    //
    // ---
    // Event notification
    // ---
    //

    public void addClassSelectionListener(DomainModelClassSelectionListener listener) {
        classSelectionListeners.add(listener);
    }


    public boolean removeClassSelectionListener(DomainModelClassSelectionListener listener) {
        return classSelectionListeners.remove(listener);
    }


    /**
     * Informs listeners that a class has been selected to participate in the
     * DomainModel of the currently loaded data service
     * 
     * @param packageName
     * @param classMapping
     * @param packageNsType
     */
    protected void fireClassSelected(String packageName, ClassMapping classMapping, NamespaceType packageNsType) {
        for (DomainModelClassSelectionListener listener : classSelectionListeners) {
            listener.classSelected(packageName, classMapping, packageNsType);
        }
    }


    /**
     * Informs listeners that a class has been deselected from participation
     * in the service's DomainModel
     * 
     * @param packageName
     * @param className
     */
    protected void fireClassDeselected(String packageName, String className) {
        for (DomainModelClassSelectionListener listener : classSelectionListeners) {
            listener.classDeselected(packageName, className);
        }
    }


    /**
     * Informs listeners that all classes have been removed from the domain model
     */
    protected void fireClassesCleared() {
        for (DomainModelClassSelectionListener listener : classSelectionListeners) {
            listener.classesCleared();
        }
    }
}

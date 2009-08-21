/*
 * Ext JS Library 2.1
 * 
 */

Ext.onReady(function(){
    Ext.QuickTips.init();

	// for toolTip
	Ext.menu.BaseItem.prototype.onRender = function(container){
		this.el = Ext.get(this.el);
		container.dom.appendChild(this.el.dom);
		if (this.tooltip) {
		this.el.dom.qtip = this.tooltip;
		}
	};
	
	// end Here


    // Menus can be prebuilt and passed by reference
    var menuHome = new Ext.menu.Menu({
        id: 'menuHome',
         items: [
			{
                text: 'My Profile',
				tooltip:'My Profile',
				 handler: editUserProfile
			},
			{
                text: 'Change Password',
				tooltip:'Change Password',
			    href :'ChangePassword.do?operation=edit&pageOf=pageOfChangePassword'                      
            }
        ]
    });

    var menu = new Ext.menu.Menu({
        id: 'mainMenu',
        items: [
            {
                text: 'User',
				href:'User.do?operation=add&pageOf=pageOfUserAdmin&menuSelected=1',
				tooltip:'Add User',
                menu: {        // <-- submenu by nested config object
                    items: [
                        // stick any markup in a menu
                       {
                            text: 'Add',
							href:'User.do?operation=add&pageOf=pageOfUserAdmin&menuSelected=1'
                        }, {
                            text: 'Edit',
                            href:'SimpleQueryInterface.do?pageOf=pageOfUserAdmin&aliasName=User&menuSelected=1'
                        }, {
                            text: 'Approve New Users',
                            href:'ApproveUserShow.do?pageNum=1&menuSelected=1'
	                       }
                    ]
                }
            },
				{
                text: 'Institution',
				tooltip:'Add Institution',
				href:'Institution.do?operation=add&pageOf=pageOfInstitution&menuSelected=2',
                menu: {        // <-- submenu by nested config object
                    items: [
                        // stick any markup in a menu
                       {
                            text: 'Add',
							href:'Institution.do?operation=add&pageOf=pageOfInstitution&menuSelected=2'
                        }, {
                            text: 'Edit',
                            href:'SimpleQueryInterface.do?pageOf=pageOfInstitution&aliasName=Institution&menuSelected=2' 
                        } 
                    ]
                }
            },
				{
                text: 'Department',
				tooltip:'Add Department',
				href:'Department.do?operation=add&pageOf=pageOfDepartment&menuSelected=3',
                menu: {        // <-- submenu by nested config object
                    items: [
                        // stick any markup in a menu
                       {
                            text: 'Add',
							href:'Department.do?operation=add&pageOf=pageOfDepartment&menuSelected=3'
                        }, {
                            text: 'Edit',
                            href:'SimpleQueryInterface.do?pageOf=pageOfDepartment&aliasName=Department&menuSelected=3' 
                        } 
                    ]
                }
            },
				{
                text: 'Research Group',
				tooltip:'Add Research Group',
				href:'CancerResearchGroup.do?operation=add&pageOf=pageOfCancerResearchGroup&menuSelected=4',
                menu: {        // <-- submenu by nested config object
                    items: [
                        // stick any markup in a menu
                       {
                            text: 'Add',
							href:'CancerResearchGroup.do?operation=add&pageOf=pageOfCancerResearchGroup&menuSelected=4'
                        }, {
                            text: 'Edit',
                            href:'SimpleQueryInterface.do?pageOf=pageOfCancerResearchGroup&aliasName=CancerResearchGroup&menuSelected=4' 
                        }
                    ]
                }
            },
             
			  {
                text: 'Site',
				tooltip:'Add Site',
				href:'Site.do?operation=add&pageOf=pageOfSite&menuSelected=5',
                menu: {        // <-- submenu by nested config object
                    items: [
                        // stick any markup in a menu
                       {
                            text: 'Add',
                            href:'Site.do?operation=add&pageOf=pageOfSite&menuSelected=5'
                        }, {
                            text: 'Edit',
                            href:'SimpleQueryInterface.do?pageOf=pageOfSite&aliasName=Site&menuSelected=5' 
                        }
                    ]
                }
            },
			{
                text: 'Clinical Study',
				tooltip:'Add Clinical Study',
				href:'ClinicalStudy.do?operation=add&pageOf=pageOfClinicalStudy',
					              //  href:'OpenCollectionProtocol.do?pageOf=pageOfmainCP&operation=add',
                menu: {        // <-- submenu by nested config object
                    items: [
                        // stick any markup in a menu
                       {
                            text: 'Add',
                            href:'ClinicalStudy.do?operation=add&pageOf=pageOfClinicalStudy'
                        }, {
                            text: 'Edit',
                            href:'SimpleQueryInterface.do?pageOf=pageOfClinicalStudy&aliasName=ClinicalStudy' 
                        }
                    ]
                }
            },        	
		]
    });
            // For Data Entry

    var menu_bio = new Ext.menu.Menu({
        id: 'menu_bio',
        items: [
            {
                text: 'Clinical Study Based View',
				tooltip:'Clinical Study Based View',
                href:'CSBasedSearch.do?menuSelected=22'

            },
			{
                text: 'Patient',
				tooltip:'Add Patient',
				href:'Participant.do?operation=add&pageOf=pageOfParticipant&menuSelected=12',
                menu: {        // <-- submenu by nested config object
                    items: [
                        // stick any markup in a menu
                       {
                            text: 'Add',
                            href:'Participant.do?operation=add&pageOf=pageOfParticipant&menuSelected=12'
                        }, {
                            text: 'Edit',
                            href:'SimpleQueryInterface.do?pageOf=pageOfParticipant&aliasName=Participant&menuSelected=12'
                        }
                    ]
                }
            }

        ]
    });
            // For Search link

    var menu_search = new Ext.menu.Menu({
        id: 'menu_search',
        items: [
			{
                text: 'Saved Queries',
				tooltip:'Saved Queries',
			    href : 'RetrieveQueryAction.do'                       
            },
			{
                text: 'Simple',
				tooltip:'Simple Search',
			    href : 'SimpleQueryInterface.do?pageOf=pageOfSimpleQueryInterface'                       
            },
			{
                text: 'Advanced',
				tooltip:'Advanced Search',
			    href : 'QueryWizard.do?'                       
            },
        ]
    });

	
    var tb = new Ext.Toolbar();
    tb.render('toolbarLoggedIn');
        
	tb.add(new Ext.Toolbar.MenuButton({text: 'Home',link:'Home.do',handler: handleMenu,menu: menuHome}),
	        
		{
            text:'Administrative Data',	
            menu: menu  // assign menu by instance
        }, 
        {
            text:'Data Entry',
            menu: menu_bio  // assign menu by instance
        },
		{
       		text: 'Search',
            menu: menu_search  // assign menu by instance
        }
			
		);


    // functions to display feedback


    function onItemCheck(){
		alert("This Page is under construction");
      //  Ext.example.msg('Item Check', 'You {1} the "{0}" menu item.', item.text, checked ? 'checked' : 'unchecked');
    }
	function handleMenu(item)
	{
	document.location.href = item.link;
	}
	
    
});

(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    hobs.param("fycPageUrl", "/silversea-com/en/cruise.html"); 
    new hobs.TestSuite('FYC Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/FYCPageTestSuite.js', register: true})
        .addTestCase(new hobs.TestCase("Navigate to FYC Result Page")
          .navigateTo("%fycPageUrl%")
    	  .click(".c-search-hero__form button.btn",{expectNav: true})        
		  .asserts.exists(".findyourcruise .c-fyc-v2__result__link", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Navigate to Cruise Detail Page")
    	  .click(".c-fyc-v2__result a.c-fyc-v2__result__link",{expectNav: true})        
		  .asserts.exists(".cruise-2018-overview-description", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Set Filter")
            .navigateTo("%fycPageUrl%")         
            .click(".c-fyc-light__form .ships .chosen-container a:first")	
            .click(".c-search-hero__form button.btn",{expectNav: true})
            .wait(5000) 
			.click("#v2-filter-collapse .destination-tracking b")
            .execFct(function() {
                (hobs.context().document.getElementsByTagName('select')[0]).selectedIndex = 1; 
    		})   
        );
}(window, hobs));




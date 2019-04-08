(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    hobs.param("shipPageUrl", "/content/silversea-com/en/ships/silver-spirit.html");
    new hobs.TestSuite('Ship Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/ShipPageTestSuite.js', 
         register: true,
        })
        .addTestCase(new hobs.TestCase("Navigate to Ship Page")
          .navigateTo("%shipPageUrl%")
		  .asserts.exists(".c-tab-ship", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Check cruise result")        
          .click(".c-fyc-v2__result a.c-fyc-v2__result__link",{expectNav: true})        
		  .asserts.exists(".cruise-2018-overview-description", true, {timeout: 5000}) 
          .navigateTo("%shipPageUrl%")           
        )

    	.addTestCase(new hobs.TestCase("Check tab content")        
          .click(".c-tab-ship #suites") 
        )
}(window, hobs));




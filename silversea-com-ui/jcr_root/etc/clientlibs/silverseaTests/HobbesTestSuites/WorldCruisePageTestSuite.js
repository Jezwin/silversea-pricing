(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    new hobs.TestSuite('World Cruise Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/WorldCruisePageTestSuite.js', register: true})
        .addTestCase(new hobs.TestCase("Navigate to World Cruise Page")
          .navigateTo("/content/silversea-com/en/destinations/world-cruise.html")
		  .asserts.exists(".combocruiseheader", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Click on Teaser")
          .click(".verticalteasermanual .c-vertical-teaser a:first",{expectNav: true})        
		  .asserts.exists(".c-combo-cruise-header", true, {timeout: 5000})
        )
}(window, hobs));




(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    hobs.param("a-portSearchPageUrl", "/silversea-com/en/other-resources/find-port/a.html");
    hobs.param("portSearchPageUrl", "/silversea-com/en/other-resources/find-port.html");


    new hobs.TestSuite('Port Search Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/PortSearchTestSuite.js', register: true})

        .addTestCase(new hobs.TestCase("Navigate to Port Search Page")
          .navigateTo("%portSearchPageUrl%")
		  .asserts.exists(".finyourport .port-dest-search", true, {timeout: 5000})          
        )

    	.addTestCase(new hobs.TestCase("Navigate to Port Search Page with alphabet a")
          .click('a[href="/silversea-com/en/other-resources/find-port/a.html"]', {expectNav: true}) 
		  .asserts.exists(".finyourport .port-dest-search", true, {timeout: 5000})   
          .click('.finyourport .c-find-port a', {expectNav: true})           
          .asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/other-resources/find-port/a/") > -1;})                   
        )

    	.addTestCase(new hobs.TestCase("Navigate to Port Search Page with alphabet p")
          .navigateTo("%portSearchPageUrl%")           
          .click('a[href="/silversea-com/en/other-resources/find-port/p.html"]', {expectNav: true}) 
		  .asserts.exists(".finyourport .port-dest-search", true, {timeout: 5000})   
          .click('.finyourport .c-find-port a', {expectNav: true})           
          .asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/other-resources/find-port/p/") > -1;})                   
        )

		.addTestCase(new hobs.TestCase("Search Port by destination")
          .navigateTo("%portSearchPageUrl%")             
          .click(".port-dest-search a:first-child")
          .execFct(function() {
        	(hobs.context().document.getElementsByTagName('select')[0]).selectedIndex = 3; 
    		})  
          .click('.port-dest-search button#counter')           
        );
}(window, hobs));




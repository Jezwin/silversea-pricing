(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    new hobs.TestSuite('FYC Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/FYCPageTestSuite.js', register: true})
        .addTestCase(new hobs.TestCase("Navigate to FYC Page")
          .navigateTo("/content/silversea-com/en/cruise.html")
    	  .click(".c-search-hero__form button.btn",{expectNav: true})
          .click(".c-fyc-v2__result a.c-fyc-v2__result__link",{expectNav: true})           
		  .asserts.exists(".cruise .cruise-2018-overview-description", true, {timeout: 10000})	
        );

}(window, hobs));




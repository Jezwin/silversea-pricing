(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    hobs.param("fycPageUrl", "/silversea-com/en/cruise.html"); 
    new hobs.TestSuite('FYC Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/FYCPageTestSuite.js', register: true})
        .addTestCase(new hobs.TestCase("Navigate to FYC Result Page")
          .navigateTo("%fycPageUrl%")
    	  .click(".c-search-hero__form button.btn",{expectNav: true})        
		  .asserts.exists(".findyourcruise2018 .c-fyc-v2__result__link", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Navigate to Cruise Detail Page")
    	  .click(".c-fyc-v2__result a.c-fyc-v2__result__link",{expectNav: true})        
		  .asserts.exists(".cruise-2018-overview-description", true, {timeout: 5000})
        )

		// Cruise 2018 style
   		.addTestCase(new hobs.TestCase("Navigate to filter page")
    	  .navigateTo("%fycPageUrl%")         
          .click(".c-fyc-light__form .ships .chosen-container a:first")	
          .click(".c-search-hero__form button.btn",{expectNav: true})
          .wait(3000)  
        )

    	.addTestCase(new hobs.TestCase("Set Filter", { params: { initialResultValue:  function(){hobs.find(".findyourcruise2018.v2 .fyc2018-header-container div:nth-child(2)").text()} } })
            .click(".filter-destination .fyc2018-filter-value-container div.filter-no-selected:first")
            .click(".filter-destination .fyc2018-filter-clear")
            .click(".filter-departure .fyc2018-filter-value-container div.filter-no-selected:first")
            .click(".filter-departure .fyc2018-filter-clear") 
            .click(".filter-duration .fyc2018-filter-value-container div.filter-no-selected:first")
            .click(".filter-duration .fyc2018-filter-clear") 
            .click(".filter-ship .fyc2018-filter-value-container div.filter-no-selected:first")
            .click(".filter-ship .fyc2018-filter-clear") 
            .click(".filter-cruise-type .fyc2018-filter-value-container div.filter-no-selected:first")
            .click(".filter-cruise-type .fyc2018-filter-clear") 
            .click(".filter-port .fyc2018-filter-value-container div.filter-no-selected:first")
            .click(".filter-port .fyc2018-filter-clear")  
            .wait(3000)         
            .asserts.isTrue(function(){ return hobs.find(".findyourcruise2018.v2 .fyc2018-header-container div:nth-child(2)").text() != "%initialResultValue%";})      

			//Clear all filters
			.click(".findyourcruise2018 div.fyc2018-header-reset-all:first")   
            .wait(3000)         
            .asserts.isTrue(function(){ return hobs.find(".findyourcruise2018.v2 .fyc2018-header-container div:nth-child(2)").text() != "%initialResultValue%";})            
        );
}(window, hobs));




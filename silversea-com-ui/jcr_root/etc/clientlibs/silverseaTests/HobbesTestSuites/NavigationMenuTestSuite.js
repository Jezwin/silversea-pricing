(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    hobs.param("homePageUrl", "/silversea-com/en.html");
    hobs.param("requestQuotePageUrl", "/silversea-com/en/request-quote.html");
    hobs.param("brochurePageUrl", "/silversea-com/en/brochures.html");
    hobs.param("mySilverseaPageUrl", "/silversea-com/en/my-silversea.html");
    hobs.param("FYCPageUrl", "/silversea-com/en/cruise.html");
    hobs.param("whySilverseaPageUrl", "/silversea-com/en/why-silversea.html");
    hobs.param("destinationsPageUrl", "/silversea-com/en/destinations.html");
    hobs.param("shipsPageUrl", "/silversea-com/en/ships.html");
    hobs.param("onboardPageUrl", "/silversea-com/en/onboard.html");
    hobs.param("ashorePageUrl", "/silversea-com/en/going-ashore-flights.html");
    hobs.param("expeditionPageUrl", "/silversea-com/en/expeditions.html");

    new hobs.TestSuite('Navigation Menu',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/NavigationMenuTestSuite.js', register: true})

        .addTestCase(new hobs.TestCase("Navigate to Home Page")
          .navigateTo("%homePageUrl%")
		  .asserts.exists(".c-main-nav .c-main-nav__bottom", true, {timeout: 5000})
          .asserts.exists(".c-main-nav .c-main-nav__top", true, {timeout: 5000})           
        )

    	.addTestCase(new hobs.TestCase("Request a Quote")
          .navigateTo("%homePageUrl%")
          .click('a[href="%requestQuotePageUrl%"]', {expectNav: true}) 
          .click(".request-quote-form .input--title b")
		  .execFct(function() {
        	(hobs.context().document.getElementsByTagName('select')[0]).selectedIndex = 3; 
            (hobs.context().document.getElementsByTagName('select')[1]).selectedIndex = 228; 
    		})
          .fillInput('[name="firstname"]', 'xxx_FirstName')
          .fillInput('[name="lastname"]', 'xxx_LastName')
          .fillInput('[name="email"]', 's.shetty@77agency.com')
          .fillInput('[name="localphone"]', '201-555-0123')
          .click('.request-quote-form button.btn', {expectNav: true})
          .asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/request-quote/thank-you.html") > -1;})
        )

    	.addTestCase(new hobs.TestCase("Request a Brochure")
          .navigateTo("%homePageUrl%")   
          .click('a[href="%brochurePageUrl%"]', {expectNav: true})            
		  .asserts.exists(".brochureteaserlist", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("FYC Page")
          .navigateTo("%homePageUrl%")   
          .click('a[href="%FYCPageUrl%"]', {expectNav: true})               
		  .asserts.exists(".c-fyc-light__form", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Why Silversea Page")
           .navigateTo("%homePageUrl%")  
          .click('a[href="%whySilverseaPageUrl%"]', {expectNav: true})                     
		  .asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/why-silversea.html") > -1;})
        )

    	.addTestCase(new hobs.TestCase("Destinations Page")
          .navigateTo("%homePageUrl%")  
          .click('a[href="%destinationsPageUrl%"]', {expectNav: true})             
          .asserts.exists(".destinationsMap", true, {timeout: 5000})           
		  .asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/destinations.html") > -1;})
        )

    	.addTestCase(new hobs.TestCase("Ships Page")
          .navigateTo("%homePageUrl%")      
          .click('a[href="%shipsPageUrl%"]', {expectNav: true})               
		  .asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/ships.html") > -1;})
        )

    	.addTestCase(new hobs.TestCase("Onboard Page")
          .navigateTo("%homePageUrl%")  
          .click('a[href="%onboardPageUrl%"]', {expectNav: true})            
		  .asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/onboard.html") > -1;})
        )

    	.addTestCase(new hobs.TestCase("Expedition Page")
          .navigateTo("%homePageUrl%")    
          .click('a[href="%expeditionPageUrl%"]', {expectNav: true})            
		  .asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/expeditions.html") > -1;})
        );
}(window, hobs));




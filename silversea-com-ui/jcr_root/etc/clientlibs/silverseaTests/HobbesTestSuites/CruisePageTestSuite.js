(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    hobs.param("cruisePageUrl", "/silversea-com/en/destinations/africa-indian-ocean-cruise/singapore-to-mumbai-3908.html");

    new hobs.TestSuite('Cruise Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/CruisePageTestSuite.js', register: true})

        .addTestCase(new hobs.TestCase("Cruise Page")
          .navigateTo("%cruisePageUrl%")
		  .asserts.exists(".cruise-2018-overview-description .cruise-2018-overview-description-cruise", true, {timeout: 5000})       
        ) 

    	.addTestCase(new hobs.TestCase("RAQ Itinerary PDF")
          .navigateTo("%cruisePageUrl%")
		  .navigateTo("%cruisePageUrl%")
		  .click(".cruise-2018-itineraries-itinerary .cruise-2018-itineraries-itinerary-row .cruise-2018-itineraries-itinerary-row-text")
          .wait(5000)           
		  .click(".cruise-2018-itineraries-itinerary .cruise-2018-itineraries-itinerary-row .cruise-2018-itineraries-itinerary-row-text")
    	  .click(".cruise-2018-overview #cruise-2018-pdf-download")	
        ); 
}(window, hobs));




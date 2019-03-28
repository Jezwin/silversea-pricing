(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    hobs.param("destinationsPageUrl", "/silversea-com/en/destinations/africa-indian-ocean-cruise.html");


    new hobs.TestSuite('Destinations Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/DestinationsPageTestSuite.js', register: true})

        .addTestCase(new hobs.TestCase("Steve and silversea")
          .navigateTo("%destinationsPageUrl%")
		  .asserts.exists(".steveandsilversea .c-steveandsilversea", true, {timeout: 5000})
          .click(".steveandsilversea .c-steveandsilversea .image-div a") 
          .wait(5000)           
          .click(".modal .c-steveandsilversea__gallery button.close")           
        ) 

    	.addTestCase(new hobs.TestCase("Inline Gallery")
          .navigateTo("%destinationsPageUrl%")
		  .asserts.exists(".inlineGallery .c-inlinegallery", true, {timeout: 5000})
          .click(".inlineGallery button.slick-prev") 
          .wait(5000)           
          .click(".inlineGallery button.slick-next")
          .click(".inlineGallery .slick-track .slick-slide a")
          .wait(5000)       
          .click(".modal-content--gallery button.close")           
        ) 
}(window, hobs));




(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    hobs.param("homePageUrl", "/silversea-com/en.html");


    new hobs.TestSuite('Home Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/HomePageTestSuite.js', register: true})

        .addTestCase(new hobs.TestCase("Hero Carousel")
          .navigateTo("%homePageUrl%")
		  .asserts.exists(".slider .heroBannerDestinationND", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Quick Search")
		  .asserts.exists(".searchhero .c-fyc-light__form", true, {timeout: 5000})
          .click(".c-search-hero__form button.btn",{expectNav: true})        
		  .asserts.exists(".findyourcruise .c-fyc-v2__result__link", true, {timeout: 5000}) 
		  .click(".c-fyc-v2__result a.c-fyc-v2__result__link",{expectNav: true})        
		  .asserts.exists(".cruise-2018-overview-description", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Exclusive Offer")
          .navigateTo("%homePageUrl%")           
		  .asserts.exists(".c-exclusiveOfferListND .c-exclusiveOfferListND__slider", true, {timeout: 5000})
		  .click(".c-exclusiveOfferListND .c-exclusiveOfferListND__slider a.c-eolist__link",{expectNav: true})        
		  .asserts.exists(".exclusiveOfferBoxes-holder", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Destination Slider")
          .navigateTo("%homePageUrl%")           
		  .asserts.exists(".destinationSlider .c-destinationSlider", true, {timeout: 5000})
		  .click(".c-destinationSlider .c-destinationSlider__slider-parent button.slick-prev")
          .wait(5000) 
          .click(".c-destinationSlider .c-destinationSlider__slider-parent button.slick-next")
		  .wait(5000)  
		  .click(".destinationSlider .c-destinationSlider .c-destinationSlider__slider__slide a")
          .asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/destinations/") > -1;})           
        );

}(window, hobs));




(function(window, hobs) {
    'use strict';
    var DEBUG = false;
    hobs.param("portPageUrl", "/silversea-com/en/other-resources/find-port/a/aalborg.html");
    new hobs.TestSuite('Port Page',
        {path:'/etc/clientlibs/silverseaTests/FYCPageTest/PortPageTestSuite.js', register: true})
        .addTestCase(new hobs.TestCase("Navigate to Port Page")
          .navigateTo("%portPageUrl%")      
		  .asserts.exists(".find-your-cruise-component", true, {timeout: 5000})
        )

    	.addTestCase(new hobs.TestCase("Port Carousel")
          .asserts.exists(".port_v2__section .port_v2__postcarousel", true, {timeout: 5000})           
    	  .click(".port_v2__section .port_v2__postcarousel button.slick-prev")  
          .wait(5000)            
          .click(".port_v2__section .port_v2__postcarousel button.slick-next")           
        )

    	.addTestCase(new hobs.TestCase("Request a brochure")
            .navigateTo("%portPageUrl%")         
			.click(".port_v2__imagetext .port_v2__imagetext_text_cta a.btn", {expectNav: true})
			.asserts.isTrue(function(){ return hobs.window.location.href.indexOf("/silversea-com/en/brochures.html") > -1;})
        );
}(window, hobs));




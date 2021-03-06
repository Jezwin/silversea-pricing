if ($(".c-footer-2018 .c-footer-other-link").length > 0) {
    $(function () {
        var footerOtherLinkSection = $(".c-footer-2018 .c-footer-other-link");
        var accordionTitleItem = footerOtherLinkSection.find(".other-link-accordion-title");
        accordionTitleItem.on("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
            var rowLinks = footerOtherLinkSection.find(".other-link-row-links");
            var rowSearch = footerOtherLinkSection.find(".other-link-row-search");
            var accordionContent = footerOtherLinkSection.find(".container-accordion-content");
            if (rowLinks.is(":visible")) {
                accordionContent.slideUp();
                rowLinks.hide();
                accordionTitleItem.find("i").removeClass("fa-angle-up").addClass("fa-angle-down");
            } else {
                rowSearch.hide();
                rowLinks.show();
                accordionContent.slideDown();
                accordionTitleItem.find("i").removeClass("fa-angle-down").addClass("fa-angle-up");
                var timerInteval = setInterval(function () {
                    if (accordionContent.is(":visible")) {
                        clearInterval(timerInteval)
                        $('html, body').animate({
                            scrollTop: rowLinks.offset().top
                        }, 2000);
                    }
                }, 100);
            }
        });

        var accordionSearchItem = footerOtherLinkSection.find(".other-link-accordion-search");
        accordionSearchItem.on("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
            var rowSearch = footerOtherLinkSection.find(".other-link-row-search");
            var rowLinks = footerOtherLinkSection.find(".other-link-row-links");
            var accordionContent = footerOtherLinkSection.find(".container-accordion-content");
            if (rowSearch.is(":visible")) {
                accordionContent.slideUp();
                rowSearch.hide();
            } else {
                rowLinks.hide();
                rowSearch.show();
                accordionContent.slideDown();
                accordionTitleItem.find("i").removeClass("fa-angle-up").addClass("fa-angle-down");
                $("#other-link-input-search").focus();
                var timerInteval = setInterval(function () {
                    if (accordionContent.is(":visible")) {
                        clearInterval(timerInteval)
                        $('html, body').animate({
                            scrollTop: $("#other-link-input-search").offset().top
                        }, 2000);
                    }
                }, 100);
            }
        });

        var $inputSearchMobile = footerOtherLinkSection.find(".other-link-mobile-search-form-input");
        var $buttonSearchMobile = footerOtherLinkSection.find(".other-link-mobile-search-form button");
        $inputSearchMobile.on("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
            $buttonSearchMobile.show();
            /*
            Code to create transition effect to move the icon from left to right
            var intevarlBtn = setInterval(function() {
                if($buttonSearchMobile.is(":visible")) {
                    clearInterval(intevarlBtn);
                    $buttonSearchMobile.find("i.fa").css("color","white");
                    var translateX = $inputSearchMobile.width() - 41;
                    $buttonSearchMobile.css("transform","translateX("+translateX+"px)");
                }
            },50);
            $(window).resize(function() {
                if($buttonSearchMobile.is(":visible")) {
                    var translateX = $inputSearchMobile.width() - 41;
                    $buttonSearchMobile.css("transform","translateX("+translateX+"px)");
                }
            });
            */
        });
        $inputSearchMobile.focusout(function(){
           if($inputSearchMobile.val().length ==0) {
               $buttonSearchMobile.hide();
           }
            /*
                $buttonSearchMobile.css("transform","translateX(-5px)");
                setTimeout(function() {
                    $buttonSearchMobile.find("i.fa").removeAttr("style");
                },1900);
             */
            });
        });

}

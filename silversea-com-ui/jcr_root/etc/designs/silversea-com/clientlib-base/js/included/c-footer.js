if($(".c-footer-2018 .c-footer-other-link").length > 0 ){
    $(function() {
        var footerOtherLinkSection = $(".c-footer-2018 .c-footer-other-link");
        var accordionTitleItem = footerOtherLinkSection.find(".other-link-accordion-title");
        accordionTitleItem.on("click", function(e) {
            e.preventDefault();
            e.stopPropagation();
            var rowLinks = footerOtherLinkSection.find(".other-link-row-links");
            var rowSearch = footerOtherLinkSection.find(".other-link-row-search");
            var accordionContent = footerOtherLinkSection.find(".container-accordion-content");
            if(rowLinks.is(":visible")) {
                accordionContent.hide();
                rowLinks.hide();
                accordionTitleItem.find("i").removeClass("fa-angle-up").addClass("fa-angle-down");
            } else {
                accordionContent.show();
                rowLinks.show();
                rowSearch.hide();
                accordionTitleItem.find("i").removeClass("fa-angle-down").addClass("fa-angle-up");
            }
        });

        var accordionSearchItem = footerOtherLinkSection.find(".other-link-accordion-search");
        accordionSearchItem.on("click", function(e) {
            e.preventDefault();
            e.stopPropagation();
            var rowSearch = footerOtherLinkSection.find(".other-link-row-search");
            var rowLinks = footerOtherLinkSection.find(".other-link-row-links");
            var accordionContent = footerOtherLinkSection.find(".container-accordion-content");
            if(rowSearch.is(":visible")) {
                accordionContent.hide();
                rowSearch.hide();
            } else {
                accordionContent.show();
                rowSearch.show();
                rowLinks.hide();
                accordionTitleItem.find("i").removeClass("fa-angle-up").addClass("fa-angle-down");
            }
        });
    });

}

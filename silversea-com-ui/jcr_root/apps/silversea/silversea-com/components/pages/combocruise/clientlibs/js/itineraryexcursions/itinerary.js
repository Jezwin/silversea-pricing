$(function () {


    var truncateItineraryDescription = (function (element) {
        if (element != null) {
            var textTruncate = element.text().slice(0, 330);
            element.text(textTruncate + " ...");
            return textTruncate;
        }
        return null;
    });

    var createViewMoreLessDiv = (function (e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $viewMoreDiv = $(".cruise-2018-itineraries-description-view"),
            $itineraryDescription = $(".cruise-2018-itineraries-description"),
            isVisibleViewMoreLessDiv = $viewMoreDiv.length > 0;
        if (isVisibleViewMoreLessDiv) {
            var isViewMore = $viewMoreDiv.hasClass("view-more");
            if (isViewMore) {
                $itineraryDescription.css("height", "100%");
                var lessLabel = $viewMoreDiv.attr("data-viewless");
                $viewMoreDiv.text(lessLabel);
                $itineraryDescription.text(itineraryDescriptionText);
                $viewMoreDiv.addClass("view-less");
                $viewMoreDiv.removeClass("view-more");
            } else {
                $itineraryDescription.removeAttr("style");
                var moreLabel = $viewMoreDiv.attr("data-viewmore");
                $viewMoreDiv.text(moreLabel);
                $itineraryDescription.text(itineraryDescriptionTextTruncate);
                $viewMoreDiv.addClass("view-more");
                $viewMoreDiv.removeClass("view-less");
            }
        }
    });

    var $itineraryDescription = $(".cruise-2018-itineraries-description"),
        iAmInComboCruisePage = $("body").hasClass("combocruise"),
        iHaveItineraryDescription = $itineraryDescription.length > 0,
        iHaveToShowViewMoreLess = $(".cruise-2018-itineraries-description").text().length > 330;
        iAmMobile = $(window).width() < 768,
        itineraryDescriptionText = null,
        itineraryDescriptionTextTruncate = null;

    if (iAmInComboCruisePage && iHaveItineraryDescription && iAmMobile && iHaveToShowViewMoreLess) {
        itineraryDescriptionText = $(".cruise-2018-itineraries-description").text();
        itineraryDescriptionTextTruncate = truncateItineraryDescription($itineraryDescription);
        $(".cruise-2018-itineraries-description-view").show();
        $(".cruise-2018-itineraries-description-view").on("click",createViewMoreLessDiv);
    }
});
$(function () {
    "use strict";
    var isSplittedDynamicTeaseronTablet = $(".splitteddynamicteaser").length > 0 && $(window).width() >= 768 && $(window).width() < 992;

    if (isSplittedDynamicTeaseronTablet) {
        console.log("r");
        $(".splitteddynamicteaser").each(addEventClickBothSide);
    }

    function addEventClickBothSide() {
        var $splitteddynamicteaser = $(this);
        //left side - remove href and create data-href to disable desktop behaviour on tablet
        var $leftSide = $splitteddynamicteaser.find(".splitteddynamicteaser-left"),
            attrHrefLeft = $leftSide.attr("href");
        $leftSide.attr("data-href", attrHrefLeft);
        $leftSide.removeAttr("href");
        $leftSide.on("click", addEventClickLeftSide);

        //right side - remove href and create data-href to disable desktop behaviour on tablet
        var $rightSide = $splitteddynamicteaser.find(".splitteddynamicteaser-right"),
            attrHrefRight = $rightSide.attr("href");
        $rightSide.attr("data-href", attrHrefRight);
        $rightSide.removeAttr("href");
        $rightSide.on("click", addEventClickRightSide);
    };//addEventClickBothSide

    function addEventClickLeftSide(e) {
        var $leftSide = $(this),
            $fullImage = $leftSide.parent(),
            $rightSide = $fullImage.find(".splitteddynamicteaser-right"),
            attrHrefLeft = $leftSide.attr("data-href");
        var isSecondClick = $leftSide.attr("href") != null;
        if (!isSecondClick) {
            e.preventDefault();
            e.stopPropagation();
            $leftSide.attr("href", attrHrefLeft);
            //workaround for real ipad
            $leftSide.find(".splitteddynamicteaser-text-top, .splitteddynamicteaser-text-bottom").addClass("splitteddynamicteaser-color-white");
            $rightSide.removeAttr("href");
            $rightSide.find(".splitteddynamicteaser-text-top, .splitteddynamicteaser-text-bottom").removeClass("splitteddynamicteaser-color-white");
        }
    };//addEventClickLeftSide

    function addEventClickRightSide(e) {
        e.preventDefault();
        e.stopPropagation();
        var $rightSide = $(this),
            $fullImage = $rightSide.parent(),
            $leftSide = $fullImage.find(".splitteddynamicteaser-left"),
            attrHrefRight = $rightSide.attr("data-href");
        var isSecondClick = $rightSide.attr("href") != null;
        if (!isSecondClick) {
            e.preventDefault();
            e.stopPropagation();
            $rightSide.attr("href", attrHrefRight);
            //workaround for real ipad
            $rightSide.find(".splitteddynamicteaser-text-top, .splitteddynamicteaser-text-bottom").addClass("splitteddynamicteaser-color-white");
            $leftSide.removeAttr("href");
            $leftSide.find(".splitteddynamicteaser-text-top, .splitteddynamicteaser-text-bottom").removeClass("splitteddynamicteaser-color-white");
        }
    };//addEventClickRightSide

});
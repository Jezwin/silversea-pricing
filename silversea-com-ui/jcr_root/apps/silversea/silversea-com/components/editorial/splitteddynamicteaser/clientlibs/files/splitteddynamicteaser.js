
$(function () {
    "use strict";
    var isSplittedDynamicTeaseronTablet = $(".splitteddynamicteaser").length > 0 && isTablet;

    if (isSplittedDynamicTeaseronTablet) {
        //$(".splitteddynamicteaser").each(addEventClickBothSide);
    }

    function addEventClickBothSide() {
        var $splitteddynamicteaser = $(this);
        $splitteddynamicteaser.find(".splitteddynamicteaser-left").on("click", addEventClickLeftSide);
        $splitteddynamicteaser.find(".splitteddynamicteaser-left").addClass("splitteddynamicteaser-disable-click");
        $splitteddynamicteaser.find(".splitteddynamicteaser-right").on("click", addEventClickRightSide);
        $splitteddynamicteaser.find(".splitteddynamicteaser-right").addClass("splitteddynamicteaser-disable-click");
    };//addEventClickBothSide

    function addEventClickLeftSide() {
        var $splitteddynamicteaserLeft = $(this),
            $splitteddynamicteaserFullImage =  $splitteddynamicteaserLeft.parent();
        $splitteddynamicteaserLeft.removeClass("splitteddynamicteaser-disable-click");
        $splitteddynamicteaserFullImage.find(".splitteddynamicteaser-right").addClass("splitteddynamicteaser-disable-click");
    };//addEventClickLeftSide

    function addEventClickRightSide() {
        var $splitteddynamicteaserRight = $(this),
            $splitteddynamicteaserFullImage =  $splitteddynamicteaserRight.parent();
        $splitteddynamicteaserRight.removeClass("splitteddynamicteaser-disable-click");
        $splitteddynamicteaserFullImage.find(".splitteddynamicteaser-left").addClass("splitteddynamicteaser-disable-click");
    };//addEventClickRightSide

    function isTablet() {
        return $(window).width() >= 768 && $(window).width() < 992;
    };//isTablet
});
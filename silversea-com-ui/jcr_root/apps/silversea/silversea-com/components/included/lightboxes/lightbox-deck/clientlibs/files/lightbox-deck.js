$(function () {
    "use strict";

    function showImageDeckPlan(myThat, e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $element = $(myThat),
            deckNumber= $element.data("deck-number"),
            srcImageTop = $element.data("image-src-top"),
            srcImageSide = $element.data("image-src-side"),
            $imgTop = $("#image-deck-plan-top"),
            $imgSide = $("#image-deck-plan-side");

        $imgTop.attr("src", srcImageTop);
        $imgSide.attr("src", srcImageSide);

        $(".passenger-decks").html(deckNumber);

        $(".lightbox-deck .lg-deck-number").each(function () {
            $(this).removeClass("lg-active-deck");
        });

        $element.parent().addClass("lg-active-deck");
    };//showImageDeckPlan

    /***************************************************************************
     * Lightbox Deck
     **************************************************************************/
    $('.open-lightbox-deck').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-lightbox-deck]').data('lightbox-deck'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);
        $modalContent.find(".modal-content").addClass("lightbox-suite-content");
        $modalContent.addClass("lightbox-no-scroll");

        // Activate Modal
        $modalContent.modal('show');
        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            e.preventDefault();
            e.stopPropagation();
            var $modal = $(this);
            $modal.off('shown.bs.modal');
            // Append html response inside modal
            $modal.find('.modal-content').load(ajaxContentPath, function (e) {
                $(".lightbox-deck  .lg-deck-number span").on("click", function (e) {
                    showImageDeckPlan(this, e);
                });

                var $deckActive = $modal.find(".lightbox-deck .lg-deck-number.lg-active-deck span");
                if ($deckActive != null && $deckActive.length > 0) {
                    showImageDeckPlan($deckActive);
                }
            });
        });
    });
});
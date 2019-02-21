$(function () {

    var changeRotationIcon = (function (numIteration) {
        var isLightboxOpened = $(".modal.lightbox .lightbox-map").length > 0;
        if (isLightboxOpened && numIteration < 15) {
            var $iconRotate = $(".lightbox-map i"),
                isPortrait = $iconRotate.hasClass("fa-portrait");
            if (isPortrait) {
                $iconRotate.removeClass("fa-portrait");
            } else {
                $iconRotate.addClass("fa-portrait");
            }
            setTimeout(function (args) {
                changeRotationIcon(++numIteration);
            }, 800);
        }
    });//changeRotationIcon

    /***************************************************************************
     * Lightbox Map for "cruise page" 2018"
     **************************************************************************/

    var isContainerVisible = $(".cruise-2018-itineraries-container,#cruise2018overview").length > 0;

    if (isContainerVisible) {
        $(".cruise-2018-itineraries-container,#cruise2018overview").on("click", ".open-lightbox-map", function (e) {
            e.preventDefault();
            e.stopPropagation();
            var $link = $(this),
                ajaxContentPath = $link.closest('[data-lightbox-map-path]').data('lightbox-map-path'),
                modalTarget = $link.data('target'),
                $modalContent = $(modalTarget);
            $modalContent.find(".modal-content").addClass("modal-content--transparent");
            $modalContent.find(".modal-dialog").addClass("modal-larger");
            $modalContent.find(".modal-content").addClass("lightbox-gallery-assets-content");
            $modalContent.addClass("lightbox-no-scroll");
            $modalContent.modal('show');

            // Wait for modal opening
            $modalContent.on('shown.bs.modal', function (e) {
                e.preventDefault();
                e.stopPropagation();
                var $modal = $(this);
                $modal.off('shown.bs.modal');
                // Append html response inside modal
                $modal.find('.modal-content').load(ajaxContentPath, function (e) {
                    $(".lightbox-close").addClass("close-full-width");
                    setTopLightboxModal(0);
                    changeRotationIcon(0);
                });
            });
        });
    }

});

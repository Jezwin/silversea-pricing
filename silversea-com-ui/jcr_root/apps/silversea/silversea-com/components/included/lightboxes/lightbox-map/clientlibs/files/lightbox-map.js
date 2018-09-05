$(function () {
    /***************************************************************************
     * Lightbox Map for "cruise page" 2018"
     **************************************************************************/
    $(".open-lightbox-map").on("click", function (e) {
        e.preventDefault();
        e.stopPropagation();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-lightbox-map-path]').data('lightbox-map-path'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);
        $modalContent.find(".modal-content").addClass("modal-content--transparent");
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
            });
        });
    });
});
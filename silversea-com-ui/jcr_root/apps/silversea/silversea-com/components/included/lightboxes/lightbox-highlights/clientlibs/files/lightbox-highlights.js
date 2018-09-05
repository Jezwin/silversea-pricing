$(function () {
    "use strict";
    $('.open-lightbox-highlights ').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-lightbox-highlights]').data('lightbox-highlights'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);
        $modalContent.find(".modal-content").addClass("lightbox-no-top");
        $modalContent.find(".lightbox-close i").addClass("lightbox-close-dark");

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
            });
        });
    });
});
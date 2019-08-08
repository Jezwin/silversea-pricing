$(function () {
    "use strict";
    $('.open-lightbox-highlights ').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-lightbox-highlights]').data('lightbox-highlights'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');
        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            e.preventDefault();
            e.stopPropagation();
            history.pushState(null, null, "#lb-detatils"); // push state that hash into the url
            //avoid ios issue
            if (window.scrollSupport != null && window.scrollSupport) {
                window.iNoBounce.enable();
            }
            var $modal = $(this);
            $modal.off('shown.bs.modal');
            // Append html response inside modal
            $modal.find('.modal-content').load(ajaxContentPath, function (e) {
                setTopLightboxModal();
            });
        });
    });
});
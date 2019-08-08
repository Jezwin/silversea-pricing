$(function () {
    $('.c-smartbtn.video-link-dam').on('click', function (e) {
        e.preventDefault();
        var $link = $(this),
            ajaxContentPath = $link.data('video-href'),
            modalTarget = $link.data('target-video'),
            $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            var $modal = $(this);
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function () {
                // init Video after modal load
                $(this).find('.c-video').initVideo();
            });
        });
    });
});

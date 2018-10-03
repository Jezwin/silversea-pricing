$(document).on('shown.bs.modal', function (e) {
    var $modalLightbox = $(".modal.lightbox");
    var $modalBody = $(this).find(".modal-body");
    var $modalContent = $(this).find(".modal-content");
    var $lightboxClose = $(this).find(".lightbox-close");

    if ($modalBody.hasClass("cruise-2018-offers")) {
        setTopLightboxModal();
        $(".cruise-2018-modal-exclusive-offer-title .open, .cruise-2018-modal-exclusive-offer-title .times").each(function () {
            $(this).html($(this).html().toLowerCase());
        });
    }


});
$('.modal.lightbox').on('hidden.bs.modal', function (e) {
    var $modalLightbox = $(".modal.lightbox");
    var $lightboxClose = $(this).find(".lightbox-close");
    var $modalContent = $(this).find(".modal-content");
    $modalContent.removeAttr("style");
    $lightboxClose.removeAttr("style");
    $modalLightbox.css("visibility", "hidden");


});

function setTopLightboxModal() {
    var $modalLightbox = $(".modal.lightbox");
    var $lightboxClose = $modalLightbox.find(".lightbox-close");
    var $modalDialog = $modalLightbox.find(".modal-dialog");
    var $modalContent = $modalLightbox.find(".modal-content");
    var $modalContenHeight = $modalContent.height();
    var $windowHeight = $(window).height();
    if ($(window).width() > 768) {
        try {
            var topModalContent = ($modalContenHeight / $windowHeight);
            var topModalContent = topModalContent > 0.5 ? 1 - topModalContent : topModalContent;
            if (($modalContenHeight > $windowHeight) || (topModalContent < 0.16)) {
                $lightboxClose.css("top", "0px");
                $modalContent.css("top", "45px");
            } else {
                topModalContent = Math.round($modalContenHeight * topModalContent) - 45;
                $modalContent.css("top", topModalContent + "px");
                $lightboxClose.css("top", topModalContent - 45 + "px");
            }
        } catch (e) {
            console.log(e);
        }
    } else {
        $lightboxClose.css("top", "0px");
        $modalContent.css("top", "45px");
    }
    $modalLightbox.css("visibility", "visible");
};//setTopLightboxModal
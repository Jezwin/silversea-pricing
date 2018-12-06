$(document).on('shown.bs.modal', function (e) {
    var $modalLightbox = $(".modal.lightbox");
    var $modalBody = $(this).find(".modal-body");
    var $modalContent = $(this).find(".modal-content");
    var $lightboxClose = $(this).find(".lightbox-close");

    if ($modalBody.hasClass("cruise-2018-offers") || $modalBody.find(".cruise-2018").length > 0) {
        setTopLightboxModal(0);
        $(".cruise-2018-modal-exclusive-offer-title .open, .cruise-2018-modal-exclusive-offer-title .times, .cruise-2018-offers .title").each(function () {
            $(this).html($(this).html().toLowerCase());
        });
        history.pushState(null, null, "#" + $(e.relatedTarget).attr("id"));
    }

    if ($("body").hasClass("cruise") && $modalLightbox.find(".aem-Grid").length > 0) {
        setTopLightboxModal(0);
        $modalLightbox.find(".modal-header").hide();
    }
    if ($("body").hasClass("cruise") && $(".cruise-2018-overview").height() > 0) {
        setTimeout(function () {
            $(".modal.lightbox").css("visibility", "visible");
        }, 1500);
    }

});

$('.modal.lightbox').on('hidden.bs.modal', function (e) {
    var $modalLightbox = $(".modal.lightbox");
    var $lightboxClose = $(this).find(".lightbox-close");
    var $modalContent = $(this).find(".modal-content");
    $modalContent.removeAttr("style");
    $lightboxClose.removeAttr("style");
    $modalLightbox.css("visibility", "hidden");
    history.replaceState(null, null, location.href.replace(location.hash, ""));
});

function setTopLightboxModal(height, imgId) {
    var $modalLightbox = $(".modal.lightbox");
    var $lightboxClose = $modalLightbox.find(".lightbox-close");
    var $modalDialog = $modalLightbox.find(".modal-dialog");
    var $modalContent = $modalLightbox.find(".modal-content");
    var $modalContenHeight = $modalContent.height();
    var $windowHeight = $(window).height();
    var $picture = $modalLightbox.find(".modal-content").find("picture, " + imgId);

    if ($(window).width() > 768) {
        try {
            if ($picture.length > 0 && height > 0) {
                $modalContenHeight = $modalContenHeight + height;
            }
            var topModalContent = Math.round(($modalContenHeight / $windowHeight) * 100) / 100;
            if ($modalContenHeight > $windowHeight || topModalContent >= 0.62) {
                $lightboxClose.css("top", "0px");
                $modalContent.css("top", "45px");
                //console.log("0px", " 45px","because $modalContenHeight > $windowHeight");
            } else if (topModalContent < 0.62) {
                var top = Math.round($windowHeight * 0.3) - 45;
                $modalContent.css("top", top + "px");
                $lightboxClose.css("top", top - 45 + "px");
                //console.log(top, "px", (top - 45), " px", "topModalContent < 0.5");
            }
        } catch (e) {
            console.log(e);
        }
    } else {
        $lightboxClose.css("top", "0px");
        $modalContent.css("top", "45px");
    }
    $modalLightbox.css("visibility", "visible");

    //debug
    console.log("topModalContent", topModalContent);
    console.log("$modalContenHeight", $modalContenHeight, " $windowHeight", $windowHeight);
};//setTopLightboxModal
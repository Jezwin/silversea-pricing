$(function() {  
	$('.c-hero-banner-landing .video-link-dam').on('click', function(e) {
        e.preventDefault();
        var $link = $(this),
            ajaxContentPath = $link.attr('href'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function(e) {
            var $modal = $(this);
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function() {
                // init Video after modal load
                $(this).find('.c-video').initVideo();
            });
        });
    });
	$('.c-hero-banner-landing .c-hero-banner-landing-scrolldown').on('click', function(e) { 
		e.stopPropagation();
		var target = $(this).closest(".herobannerLanding").next().offset().top;
		var height =  $(this).closest(".c-hero-banner-landing").height() != null ? $(this).closest(".c-hero-banner-landing").height() : $(this).closest(".herobannerLanding").height();
		var speed = 750; 
		$('html, body').animate({scrollTop: target - 70}, speed ); 
	});
});
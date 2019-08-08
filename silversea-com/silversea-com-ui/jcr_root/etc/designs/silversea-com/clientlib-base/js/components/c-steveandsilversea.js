(function() {
	$(".steveandsilversea-video-link").click(function(e) {
		var t = setInterval(function(){
			if ($(".modal .c-steveandsilversea__gallery .modal-body").length > 0) {
				clearInterval(t);
				var counter = $(".modal .c-steveandsilversea__gallery .slide-item-total").html();
				if (parseInt(counter) == 1) {
					$(".modal .c-steveandsilversea__gallery  .c-gallery__counter").hide();
					$(".modal .c-steveandsilversea__gallery  .c-slider--nav").hide();
				}
			}
		},10);
	});
})();

document.addEventListener('webkitfullscreenchange', exitHandler);
document.addEventListener('mozfullscreenchange', exitHandler);
document.addEventListener('msfullscreenchange', exitHandler);
document.addEventListener('fullscreenchange', exitHandler);

function exitHandler() {
        if(document.webkitFullscreenEnabled || document.fullscreenEnabled || document.msFullscreenEnabled || document.mozFullScreenEnabled){ 
            $(document).on('webkitfullscreenchange mozfullscreenchange fullscreenchange msfullscreenchange', function(e) {
                $('#steveandsilversea-modal-dialog-close').trigger( "click" );
            });
        }
}
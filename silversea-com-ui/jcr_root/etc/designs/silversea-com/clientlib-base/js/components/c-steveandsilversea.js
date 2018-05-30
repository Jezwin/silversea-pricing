(function() {
	$(".steveandsilversea-video-link").click(function(e) {
		var t = setInterval(function(){
			if ($(".modal .c-steveandsilversea__gallery .modal-body").length > 0) {
				clearInterval(t);
				var counter = $(".modal .c-steveandsilversea__gallery .slide-item-total").html();
				if (parseInt(counter) > 1) {
					$(".modal .c-steveandsilversea__gallery  .c-gallery__counter").show();
					$(".modal .c-steveandsilversea__gallery  .c-slider--nav .slick-list").css("visibility","visible");
				}
			}
		},100);
	});
})();
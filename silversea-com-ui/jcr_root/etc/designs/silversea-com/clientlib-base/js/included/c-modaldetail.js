$(function() {
	$(window).on('resize', function() {
		var href = $(".c-modaldetail-link").data("href");
		var target = $(".c-modaldetail-link").data("target");
		$.get(href, function(data) {
			$(target + " .modal-content").html(data);
		});
	});

});


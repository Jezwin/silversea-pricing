$(document).ready(function() {
	portv2_ss_read_more();
	portV2_PageClass();
	scrollToAnchor();
});

function portv2_ss_read_more() {
	$('#ss_read_more').on('click', function() {
		if(!$('.port_v2__discover_text').hasClass('open')) {
			var text = $(this).attr('data-text-less');
			$(this).html(text);
			$('.port_v2__discover_text').addClass('open').stop().slideDown(600);
		} else {
			var text = $(this).attr('data-text-more');
			$(this).html(text);
			$('.port_v2__discover_text').removeClass('open').stop().slideUp(600);
		}
	});
}
function portV2_PageClass() {
	$('html').addClass('port_v2_page');
}
function portV2_Accordion(selector) {
	$(selector + ' .port_v2__expander_header').on('click', function () {
		var self = $(this);
		if (self.hasClass('no_description')){
			return;
		}
		// Stores height of the collapsed header of accordion item (substracting border-height)
		var collapsedHeaderHeight = $(this).parent().outerHeight(true) - 1;
		// If the clicked accordion is active it will close
		if ($(this).parent().hasClass('active')) {
			$(this).parent().find('.port_v2__expander_content', this).slideUp('300');
			$(this).parent().removeClass('active');
		}
		// Else it becomes active
		else {
			//Slides up currently active accordion item
			$('.port_v2__expander_item.active .port_v2__expander_content').slideUp('300');
			$('.port_v2__expander_item.active').removeClass('active');
			//Slides down the content of clicked accordion
			$(this).parent().addClass('active');
			$(this).parent().find('.port_v2__expander_content', this).slideDown('300');
			setTimeout(function () {
				$('html, body').animate({
					scrollTop: self.offset().top - 70
				}, 1000);
			}, 350);
		}
	});
}
function portV2_AccordionSlice(selector) {
	$(selector + ' .port_v2__expander').each(function () {
		// Init toggle button
		var itemsNum = $(this).find('.port_v2__expander_item:last-child').index();
		if (itemsNum > 4) {
			$(this).parent().find('.port_v2__expander_slice').show();
		} else {
			$(this).parent().find('.port_v2__expander_slice').hide();
		}
		// Slice items
		$(this).find('.port_v2__expander_item').slice(5).hide();
	});

	$(selector + ' .port_v2__expander_slice a').on('click', function () {
		if (!$(this).is('a.open')) {
			// Change button text and icon
			var self = $(this);
			var buttonText = self.attr('data-less');
			self.find('span').text(buttonText);
			self.find('.fa').removeClass('fa-angle-down').addClass('fa-angle-up');
			self.addClass('open');
			// Open all items
			self.parent().parent().find('.port_v2__expander .port_v2__expander_item:hidden').each(function () {
				$(this).slideDown('300');
			});
		} else {
			// Change button text and icon
			var self = $(this);
			var buttonText = self.attr('data-more');
			self.find('span').text(buttonText);
			self.find('.fa').removeClass('fa-angle-up').addClass('fa-angle-down');
			self.removeClass('open');
			// Slice items
			self.parent().parent().find('.port_v2__expander .port_v2__expander_item').slice(5).slideUp('300');
		}
	});
}
function scrollToAnchor() {
	$(".scrolltoanchor").click(function(e) {
		var target = $(this).attr("href");
		var targetOffset = $(target).offset().top - 90;
		//scroll to element
		jQuery("body,html").animate({
			scrollTop : targetOffset
		},1000)
		return false;
	});
}
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
function portV2_matchingHeights() {
	$('.port_v2__post_title').matchHeight();
	$('.port_v2__post_meta').matchHeight();
}
function portV2_CarouselRelated() {
	var relatedCarousel = $('#port_v2__relatedcarousel');
	relatedCarousel.on('init', function () {
		var self = $(this);
		slickLazyLoad('.slick-slide', '.port_v2__post_img', 0, self, true);
	});
	relatedCarousel.slick({
		dots: true,
		arrows: true,
		infinite: true,
		slidesToShow: 3,
		slidesToScroll: 3,
		speed: 700,
		prevArrow: '<button type="button" data-role="none" class="slick-prev slick-arrow c-slider-black-arrow" aria-label="Previous" role="button"><i class="fa fa-angle-left"></i></button>',
		nextArrow: '<button type="button" data-role="none" class="slick-next slick-arrow c-slider-black-arrow" aria-label="Next" role="button"><i class="fa fa-angle-right"></i></button>',
		responsive: [
			{
				breakpoint: 1030,
				settings: {
					arrows: false,
					slidesToShow: 3,
					slidesToScroll: 3
				}
			},
			{
				breakpoint: 991,
				settings: {
					arrows: false,
					slidesToShow: 3,
					slidesToScroll: 3
				}
			},
			{
				breakpoint: 767,
				settings: {
					arrows: false,
					slidesToShow: 2,
					slidesToScroll: 2
				}
			},
			{
				breakpoint: 480,
				settings: {
					arrows: false,
					slidesToShow: 1,
					slidesToScroll: 1
				}
			}
		]
	});
	relatedCarousel.on('afterChange', function (event, slick, currentSlide, nextSlide) {
		var self = $(this);
		slickLazyLoad('.slick-slide', '.port_v2__post_img', currentSlide, self, true);
	});
}
function slickLazyLoad(slideSelector, imageSelector, currentSlide, self, loadNextPrev) {
	// Boolean if load next/prev slides
	var loadNextPrev = loadNextPrev;
	// Get number of visible slides
	var itemsNum = self.find(slideSelector + ':not(".slick-cloned").slick-active').length;
	// Slides Index
	var slidesCurrent = currentSlide;
	var slidesNext = currentSlide + itemsNum;
	var slidesPrev = currentSlide - itemsNum;
	for (i = 0; i < itemsNum; i++) {
		// LazyLoad all visible slides
		var lazySlide = self.find(slideSelector + ':not(".slick-cloned"):eq(' + slidesCurrent + ')').find(imageSelector);
		var lazyImage = lazySlide.attr("data-bg");
		lazySlide.attr("style", lazyImage).removeAttr("data-bg");

		if (loadNextPrev) {
			// LazyLoad all next slides
			var lazySlide = self.find(slideSelector + ':not(".slick-cloned"):eq(' + slidesNext + ')').find(imageSelector);
			var lazyImage = lazySlide.attr("data-bg");
			lazySlide.attr("style", lazyImage).removeAttr("data-bg");
		}

		if (loadNextPrev) {
			// LazyLoad all next slides
			var lazySlide = self.find(slideSelector + ':not(".slick-cloned"):eq(' + slidesPrev + ')').find(imageSelector);
			var lazyImage = lazySlide.attr("data-bg");
			lazySlide.attr("style", lazyImage).removeAttr("data-bg");
		}

		// Move to next slide
		slidesCurrent++;
		slidesNext++;
		slidesPrev++;
	}
}
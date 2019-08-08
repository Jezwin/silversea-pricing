$(function() {
    $.fn.initGallery = function() {
        this.each(function() {
            var $component = $(this);

            $('.c-slider--for, .c-slider--nav').on('init', function(event, slick) {
                var $activeSlide = $(this).find('.slick-active');
                $activeSlide.find('.lazy').lazy();
                $activeSlide.prev().find('.lazy').lazy();
                $activeSlide.next().find('.lazy').lazy();
            });

            // Activate sliders in sync
            var $slideFor = $component.find('.c-slider--for').slick({
                prevArrow : prevArrowCustom,
                nextArrow : nextArrowCustom,
                slidesToShow : 1,
                slidesToScroll : 1,
                asNavFor : $component.find('.c-slider--nav')
            });

            var $slideNav = $component.find('.c-slider--nav').slick({
                prevArrow : prevArrowCustom,
                nextArrow : nextArrowCustom,
                slidesToShow : 6,
                slidesToScroll : 1,
                asNavFor : $component.find('.c-slider--for'),
                focusOnSelect : true
            });

            // Category tab : Slide to the first image of the current category
            $('.c-gallery--cc__tab__link').on('click', function(e) {
                e.preventDefault();
                var $link = $(this);

                if (!$link.closest('.c-gallery--cc__tab__item').hasClass('active')) {
                    var targetSlideIndex = $slideFor.find('.slick-slide:not(".slick-cloned")[data-category-target="' + $link.data('category') + '"]').index() - 1;

                    $link.closest($component).find($slideFor).slick('slickGoTo', targetSlideIndex);

                    // Set Active status on the current tab
                    $link.closest('.c-gallery--cc__tab__item').addClass('active').siblings().removeClass('active');
                }
            });

            // Init video on click
            $component.find('.video-link').on('click', function(e) {
                e.preventDefault();
                $(this).next('.c-video').initVideo();
            });

            // Update category tab according to the current slide
            $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                var currentCategory = $component.find('.slick-slide:not(".slick-cloned")').eq(nextSlide).data('category-target');

                $component.find('.c-gallery--cc__tab__link').closest('.c-gallery--cc__tab__item').removeClass('active');
                $component.find('.c-gallery--cc__tab__link[data-category="' + currentCategory + '"]').closest('.c-gallery--cc__tab__item').addClass('active');

                var $slide = $(this);
                // Set counter according to the current slide
                $slide.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-current').html(nextSlide + 1);

                // Kill video if current slide contains video
                var $video = $slide.find('.slick-current .c-video');
                $video.find('.s7playpausebutton[selected="false"]').trigger('click');
                $video.attr('class', 'c-video').empty();
            }).on('afterChange', function(event, slick, currentSlide) {
                var $slider = $(this);

                // call lazy loading
                var $activeSlide = $slider.closest('.c-gallery--cc').find('.slick-active');
                $activeSlide.find('.lazy').lazy();
                $activeSlide.next().find('.lazy').lazy();
                $activeSlide.prev().find('.lazy').lazy();
                setTimeout(function() {
                	$activeSlide.find('.lazy').lazy();
                }, 50);
                // Description : Show description on slide change
                var index = currentSlide,
                    $captionWrapper = $component.find('.c-gallery--cc__caption'),
                    $currentSlideElement = $(this).find('[data-slick-index = ' + index + ']');
                $captionWrapper.text($currentSlideElement.find('.c-gallery--cc__text').text());
            });
        });
    };

    $('.c-gallery--cc').initGallery();
});
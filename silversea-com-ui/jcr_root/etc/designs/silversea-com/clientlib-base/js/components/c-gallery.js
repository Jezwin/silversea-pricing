$(function() {
    $.fn.initGallery = function() {
        this.each(function() {
            var $component = $(this);

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
                var $link = $(this), targetSlideIndex = $slideFor.find('.slick-slide:not(".slick-cloned")[data-category-target="' + $link.data('category') + '"]').index() - 1;

                $link.closest($component).find($slideFor).slick('slickGoTo', targetSlideIndex);

                // Set Active status on the current tab
                $link.closest('.c-gallery--cc__tab__item').addClass('active').siblings().removeClass('active');
            });

            // The following click triggered on init will force the slider to trigger afterChange event
            $('.active .c-gallery--cc__tab__link').trigger('click');

            // Description : Show description on slide change
            $slideFor.on('afterChange', function(event, slick, currentSlide) {
                var index = currentSlide,
                    $captionWrapper = $component.find('.c-gallery--cc__caption'),
                    $currentSlideElement = $(this).find('[data-slick-index = ' + index + ']');
                $captionWrapper.text($currentSlideElement.find('.c-gallery--cc__text').text());
            });

            // Update category tab according to the current slide
            $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                var currentCategory = $component.find('.slick-slide:not(".slick-cloned")').eq(nextSlide).data('category-target');

                console.log(currentCategory);
                $component.find('.c-gallery--cc__tab__link').closest('.c-gallery--cc__tab__item').removeClass('active');
                $component.find('.c-gallery--cc__tab__link[data-category="' + currentCategory + '"]').closest('.c-gallery--cc__tab__item').addClass('active');
            });
        });
    };

    $('.c-gallery--cc').initGallery();
});
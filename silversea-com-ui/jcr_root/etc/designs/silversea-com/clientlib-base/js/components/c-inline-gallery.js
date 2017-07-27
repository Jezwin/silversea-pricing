$(function() {
    /***************************************************************************
     * Activate slider with responsive setting
     **************************************************************************/
    settingInlineGallery = {
        prevArrow : prevArrowCustom,
        nextArrow : nextArrowCustom,
        slidesToShow : 5,
        slidesToScroll : 5,
        responsive : [ {
            breakpoint : 991,
            settings : {
                slidesToShow : 3,
                slidesToScroll : 3
            }
        }, {
            breakpoint : 768,
            settings : {
                slidesToShow : 1,
                slidesToScroll : 1
            }
        } ]
    };

    // Kill slick with default settings and reinit with inline gallery settings
    $('.c-inline-gallery').slick('unslick').slick(settingInlineGallery);

    /***************************************************************************
     * Set Image in background (allow to use background-size : cover)
     **************************************************************************/
    var setBackgroundImage = (function setBackgroundImage() {
        $('.c-inline-gallery .o-img').each(function() {
            var $image = $(this);
            var src = $image.prop('currentSrc') || $image.prop('src');
            $image.closest('div').css('background-image', 'url(' + src + ')');
            $image.css('visibility', 'hidden');
        });

        return setBackgroundImage;
    })();

    $('body').on('trigger.viewport.changed', function() {
        setBackgroundImage();
    });
});
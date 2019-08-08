$(function() {  		
	 /***************************************************************************
     * Mozaic Slider
     **************************************************************************/
    
        $('.c-destinationSlider .c-destinationSlider__slider').each(function() {
        var $mozaicSlider = $(this);
        if($mozaicSlider.attr('data-ssc-row') == "1"){
	        settingDesktopDest= {
	        		arrows: true,
	            dots : true,
	            slidesToShow : parseInt($mozaicSlider.attr('data-ssc-element')),
	            slidesToScroll : parseInt($mozaicSlider.attr('data-ssc-element'))
	        };
        }else {
        	 settingDesktopDest= {
        			arrows: true,
     	            dots : true,
     	            rows : parseInt($mozaicSlider.attr('data-ssc-row')),
     	            slidesPerRow : parseInt($mozaicSlider.attr('data-ssc-element'))
     	        };
        }
        settingMobileDest= {
            dots : true
        };

        // Is Desktop viewport
        function isDesktop() {
            return $(window).width() > 767;
        }

        // Fill last slide with content from first slide
        var fillContent = (function fillContent() {
            if (!$mozaicSlider.hasClass('fillup') && isDesktop()) {
                // Append placeholder
               // $mozaicSlider.find('.c-mozaic__slider__slide:nth-child(5n + 1)').after('<div class="c-mozaic__slider__slide c-mozaic__slider__slide--cloned"></div>');

                // Fill up last slide
                var itemToFillUp = $mozaicSlider.find('.c-destinationSlider__slider__slide').length % parseInt($mozaicSlider.attr('data-ssc-total'));
                if(itemToFillUp != 0){
                	itemToFillUp =parseInt($mozaicSlider.attr('data-ssc-total')) - itemToFillUp;
                }
                
                if (itemToFillUp !== 0) {
                    for (var i = 0; i < itemToFillUp; i++) {
                        $mozaicSlider.find('.c-destinationSlider__slider__slide:not(.c-destinationSlider__slide--cloned--cloned)').eq(i).clone().addClass('c-destinationSlider__slide--cloned').appendTo($mozaicSlider);
                    }
                }

                $mozaicSlider.addClass('fillup');
            }
            return fillContent;
        }());


        // Run slick
        $mozaicSlider.on('init', function(event, slick) {
            $mozaicSlider.closest('.c-mozaic').css('visibility', 'visible');
        });

        if (isDesktop()) {
            $mozaicSlider.attr('isDesktop', 'true');
            $mozaicSlider.slick(settingDesktopDest);
        } else {
            $mozaicSlider.attr('isDesktop', 'false');
            $mozaicSlider.slick(settingMobileDest).slick('slickFilter',':not(.c-destinationSlider__slide--cloned)');
        }


        // Run slick again on viewport changed
        $('body').on('trigger.viewport.changed', function() {
            if (isDesktop()) {
                if( $mozaicSlider.attr('isDesktop') == 'false') {
                    if ($mozaicSlider.attr('data-ssc-row') == "1") {
                        settingDesktopDest = {
                            arrows: true,
                            dots: true,
                            slidesToShow: parseInt($mozaicSlider.attr('data-ssc-element')),
                            slidesToScroll: parseInt($mozaicSlider.attr('data-ssc-element'))
                        };
                    } else {
                        settingDesktopDest = {
                            arrows: true,
                            dots: true,
                            rows: parseInt($mozaicSlider.attr('data-ssc-row')),
                            slidesPerRow: parseInt($mozaicSlider.attr('data-ssc-element'))
                        };
                    }
                    $mozaicSlider.attr('isDesktop', 'true');
                    $mozaicSlider.slick('unslick');
                    fillContent();
                    $mozaicSlider.slick(settingDesktopDest);
                }
            } else {
                if($mozaicSlider.attr('isDesktop') == 'true')
                {
                    $mozaicSlider.attr('isDesktop', 'false');
                    $mozaicSlider.removeClass('fillup');
                    $mozaicSlider.slick('unslick').slick(settingMobileDest).slick('slickFilter', ':not(.c-destinationSlider__slide--cloned)');
                }
            }
        });

            $('.c-destinationSlider-slide-small-inner__description-title').each(function(){
                var currentPort = $(this).html();
                currentPort = currentPort.toLowerCase();
                $(this).html(currentPort);
            });
       
    });

    wdest = $(window).width();
    
        function createLineProgressBarDest() {
            $(".c-destinationSlider").each(function () {
                var widthSlider = $(this).find(".slick-list").width();
                if ($("body").hasClass("viewport-md") || $("body").hasClass("viewport-lg")) {
                    if(widthSlider > 930){
                        widthSlider = 930;
                    }
                } else if ($("body").hasClass("viewport-sm") ){
                    if(widthSlider > 768){
                        widthSlider = 630;
                    }
                }else if ($("body").hasClass("viewport-xs") ){
                    widthSlider = widthSlider;
                }

                var liItem = $(this).find("ul.slick-dots li").length;
                var liWidth = ((widthSlider /  liItem) - 1);

                if ($("body").hasClass("viewport-md") || $("body").hasClass("viewport-lg")) {
                    $(this).find("ul.slick-dots li").css("width", liWidth  + "px");
                } else if ($("body").hasClass("viewport-sm") ){
                    $(this).find("ul.slick-dots li").css("width", liWidth  + "px");
                }else if ($("body").hasClass("viewport-xs") ){
                    $(this).find("ul.slick-dots li").css("width", liWidth  + "px");
                }
            });

    };
    
    createLineProgressBarDest();

    $(window).resize(sscThrottled(createLineProgressBarDest));
});
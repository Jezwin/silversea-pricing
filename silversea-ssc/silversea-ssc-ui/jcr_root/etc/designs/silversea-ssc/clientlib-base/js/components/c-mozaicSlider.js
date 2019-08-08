$(function() {  
	window.sortMozaicDestSlider = function(target){
		try {
			if($('.c-mozaic').length > 0 && typeof window.destinationsOrder != 'undefined'){
				var destinationsOrderSplit = '';
				destinationsOrderSplit = window.destinationsOrder.split(';');

				if(destinationsOrderSplit.length > 0){
					if($('.c-mozaic:not(".c-mozaic-nine") .c-mozaic__slider').length > 0){ //Slider reorder
						$('.c-mozaic .c-mozaic__slider').slick('unslick');
						var $container = $('.c-mozaic:not(".c-mozaic-nine") .c-mozaic__slider');

						for (var i = 0; i < destinationsOrderSplit.length; i++) {
							var currentValue = destinationsOrderSplit[i];
							if(currentValue != ''){
								//reorder to the position int

                                $('.c-mozaic .c-mozaic__slider').find("[data-ssc-mozaic='"+currentValue+"']").each(function () {
                                    $(this).not('.c-mozaic__slider__slide--cloned').attr('data-ssc-mozaic-order', i+1);
                                });
							}
						}

						$tickets = $container.find('.c-mozaic__slider__slide.msname').detach();

						//Assign default value to not filled order ticket - starting from destinationsOrderSplit.length +1
						var intStart = destinationsOrderSplit.length + 1;
						$tickets.each(function( index ) {
							if($( this ).attr('data-ssc-mozaic-order') == undefined){
								$(this).attr('data-ssc-mozaic-order', intStart);
								intStart = intStart + 1;
							}
						});

						$tickets = $tickets.sort(function(a, b) {
							var aVal = 1 * $(a).attr('data-ssc-mozaic-order'),
							bVal = 1 * $(b).attr('data-ssc-mozaic-order');
							return aVal - bVal;
						});

						$container.html($tickets);
                        
                        //Adding empty slide div for mozaic-text    
                   	    $container.find('.c-mozaic__slider__slide:nth-child(5n + 1)').after('<div class="c-mozaic__slider__slide c-mozaic__slider__slide--cloned"></div>');

						 // Is Desktop viewport
                        function isDesktop() {
                            return ($.viewportDetect() === 'md' || $.viewportDetect() === 'lg');
                        }
                        var settingDesktop= {
                            dots : true,
                            fade : true,
                            rows : 2,
                            slidesPerRow : 3
                        },
                        settingMobile= {
                            dots : true
                        };
						 if (isDesktop()) {
                			$container.slick(settingDesktop);
                         } else {
                            $container.slick(settingMobile).slick('slickFilter',':not(.c-mozaic__slider__slide--cloned)');
                         }
					}

                    if($('.c-mozaic-nine .c-mozaic__slider').length > 0){

                        $('.c-mozaic-nine .c-mozaic__slider').slick('unslick');
						var $container = $('.c-mozaic-nine .c-mozaic__slider');

						for (var i = 0; i < destinationsOrderSplit.length; i++) {
							var currentValue = destinationsOrderSplit[i];
							if(currentValue != ''){
								//reorder to the position int

                                $('.c-mozaic-nine .c-mozaic__slider').find("[data-ssc-mozaic='"+currentValue+"']").each(function () {
                                    $(this).not('.c-mozaic__slider__slide--cloned').attr('data-ssc-mozaic-order', i+1);
                                });
							}
						}

						$tickets = $container.find('.c-mozaic__slider__slide.msname').detach();

						//Assign default value to not filled order ticket - starting from destinationsOrderSplit.length +1
						var intStart = destinationsOrderSplit.length + 1;
						$tickets.each(function( index ) {
							if($( this ).attr('data-ssc-mozaic-order') == undefined){
								$(this).attr('data-ssc-mozaic-order', intStart);
								intStart = intStart + 1;
							}
						});

						$tickets = $tickets.sort(function(a, b) {
							var aVal = 1 * $(a).attr('data-ssc-mozaic-order'),
							bVal = 1 * $(b).attr('data-ssc-mozaic-order');
							return aVal - bVal;
						});


						$container.html($tickets);

						 // Is Desktop viewport
                        function isDesktop() {
                            return ($.viewportDetect() === 'md' || $.viewportDetect() === 'lg');
                        }
                        var settingDesktop= {
                            prevArrow : prevArrowCustomBlack,
                            nextArrow : nextArrowCustomBlack,
                            dots : true,
                            fade : true,
                            rows : 3,
                            slidesPerRow : 3
                        },
                        settingMobile= {
                            dots : true
                        };
						 if (isDesktop()) {
                			$container.slick(settingDesktop);
                         } else {
                            $container.slick(settingMobile).slick('slickFilter',':not(.c-mozaic__slider__slide--cloned)');
                         }

                    }
				}    
			}
		}
		catch(error) {
			console.error(error);
		}
	};	
	window.sortMozaicDestSlider();
});
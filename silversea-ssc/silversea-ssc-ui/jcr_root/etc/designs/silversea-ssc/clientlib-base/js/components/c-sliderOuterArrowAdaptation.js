function adaptSliderNb(sliderNb){
	var sliderList = $('.c-slider--style1').not('.c-slider--full-width');
	if (sliderList[sliderNb] != undefined){
		$(sliderList[sliderNb]).on('init', function(event, slick){
			setTimeout(function(){
				$(sliderList[sliderNb]).addClass('grey-dots');
				$(sliderList[sliderNb]).find('.slick-next').addClass('c-slider-black-arrow');
				$(sliderList[sliderNb]).find('.slick-prev').addClass('c-slider-black-arrow');
			}, 2500);
		});		
		$(sliderList[sliderNb]).addClass('grey-dots');
		$(sliderList[sliderNb]).find('.slick-next').addClass('c-slider-black-arrow');
		$(sliderList[sliderNb]).find('.slick-prev').addClass('c-slider-black-arrow');
	}
}
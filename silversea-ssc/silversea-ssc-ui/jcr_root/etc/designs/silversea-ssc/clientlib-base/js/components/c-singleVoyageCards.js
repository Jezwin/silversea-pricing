$(function() {  
    /***************************************************************************
     * Activate slider with responsive setting
     **************************************************************************/
	
	var numCards = $('.c-singleVoyageCards-slider').data("num-cards") != null ? $('.c-singleVoyageCards-slider').data("num-cards") : '4';
	
    settingSingleVoyageCards = {
        slidesToShow : numCards,
        slidesToScroll : numCards,
        dots : true,
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

    // Kill slick with default settings and reinit with single voyage cards
    $('.c-singleVoyageCards-slider').slick('unslick').slick(settingSingleVoyageCards);

    createLineProgressBar();

    function createLineProgressBar() {
        if($(".singleVoyageCardsNewDesign2018.c-singleVoyageCards").lenght > 0) {
            var widthSlider = $(window).width();
            var $dots = $(".singleVoyageCardsNewDesign2018.c-singleVoyageCards ul.slick-dots li");
            var liItem = $dots.length;
            var liWidth = ((widthSlider / liItem) );
            $dots.css("width", liWidth + "px");
        }
    };//createLineProgressBar
});
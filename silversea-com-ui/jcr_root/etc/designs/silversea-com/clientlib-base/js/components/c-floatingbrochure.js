$(document).ready(function(){
    if($(window).width() > 768){

        $('.floating-opener').off('click').on('click', function(){
            $(this).parent().toggleClass('visible');
            if($(this).parent().hasClass("visible")){
            	$(this).parent().css("right", "0px");
            	$('.floating-opener .fa-close').show();
        	}else{
        		$(this).parent().css("right", "-220px");
        		$('.floating-opener .fa-close').hide();
        	}
            
        });

        var $floatingBrochure = $('.floating-form'),
            whitelisted_tags = $floatingBrochure.data('flyout-tag-whitelist'),
            blacklist_tags = $floatingBrochure.data('flyout-tag-blacklist'),
            minScroll = $floatingBrochure.data('scrolltop') || 0,
            whitelisted_elems,
            blacklist_elems,
            timer;
    
        if(!$floatingBrochure.length){return;}

        function isInView(element){
            var pageTop = $(window).scrollTop(),
                pageBottom = pageTop + $(window).height(),
                elementTop = $(element).offset().top,
                elementBottom = elementTop + $(element).height();

            return ((elementTop <= pageBottom) && (elementBottom >= pageTop));
        }

        function getJqElems(tags){
            return tags ? tags.split(',').map(function(item){
                return '.'+$.trim(item);
            }).join(',') : '';
        }

        function areElemsInView(elems){
            var isInViewport = false;
            elems.length && $.each(elems, function(){
                if(isInView(this)){
                    isInViewport = true;
                    return;
                }
            });
            return isInViewport;
        }

        whitelisted_elems = getJqElems(whitelisted_tags);
        blacklist_elems = getJqElems(blacklist_tags);

        $(window).scroll(function() {
            if(timer) {
                window.clearTimeout(timer);
            }
        
            timer = window.setTimeout(function() {
                isInViewport = false;

                if(blacklist_elems.length && areElemsInView($(blacklist_elems))){
                	$floatingBrochure.css("right", "-270px");
                   // $floatingBrochure.hide();
                } else { 
                    if(whitelisted_elems.length && areElemsInView($(whitelisted_elems))){
                        //$floatingBrochure.show();
                    	if($floatingBrochure.hasClass("visible")){
                    		$floatingBrochure.css("right", "0px");
                    	}else{
                    		$floatingBrochure.css("right", "-220px");	
                    	}
                    } else {
                        if(window.scrollY >= minScroll){
                           // $floatingBrochure.show();
                        	if($floatingBrochure.hasClass("visible")){
                        		$floatingBrochure.css("right", "0px");
                        	}else{
                        		$floatingBrochure.css("right", "-220px");	
                        	}
                        } else {
                        	$floatingBrochure.css("right", "-270px");
                           // $floatingBrochure.hide();
                        }
                    }
                }
            }, 100);
        });
        $(window).trigger('scroll');
 }
});
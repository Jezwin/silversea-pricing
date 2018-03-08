$(document).ready(function(){
    if($(window).width() > 767){
        $('.floating-opener').off('click').on('click', function(){
            $(this).parent().toggleClass('visible');
        });
    
        function isInView(element){
            var pageTop = $(window).scrollTop();
            var pageBottom = pageTop + $(window).height();
            var elementTop = $(element).offset().top;
            var elementBottom = elementTop + $(element).height();
    
            return ((elementTop <= pageBottom) && (elementBottom >= pageTop));
        }
        var whitelisted_tags = $('[data-flyout-tag-list]').data('flyout-tag-list'),
            whitelisted_elems,
            timer,
            isInViewport;
        if(whitelisted_tags || $.trim(whitelisted_tags) === ''){
            if($.trim(whitelisted_tags) === ''){
                $('.floating-form').show();
            } else {
                whitelisted_tags = whitelisted_tags.split(',').map(function(item){
                    return '.'+$.trim(item);
                });
                whitelisted_elems = $(whitelisted_tags.join(','));
                $(window).scroll(function() {
                    if(timer) {
                        window.clearTimeout(timer);
                    }
                
                    timer = window.setTimeout(function() {
                        isInViewport = false;
                        $.each(whitelisted_elems, function(){
                            if(isInView(this)){
                                isInViewport = true;
                                return;
                            }
                        });
                        if(isInViewport){
                            $('.floating-form').show();
                        } else {
                            $('.floating-form').hide();
                        }
                    }, 100);
                });
            }
        } else {
            $('.floating-form').remove();
        }
        $(window).trigger('scroll');
	}
});
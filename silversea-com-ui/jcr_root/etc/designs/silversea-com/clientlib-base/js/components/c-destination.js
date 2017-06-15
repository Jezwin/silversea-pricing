$(function(){
    var html_block_wrapper = jQuery('#destination-html-switchable-block');
    var html_block_trigger = jQuery('#destination-html-switcher');
    var fyc_block_wrapper = jQuery('#destination-fyc-switchable-block');
    var fyc_block_trigger = jQuery('#destination-fyc-switcher');
    
    var flag = false;
    setTimeout(function(){
        if($('body.is-mobile:not(.is-tablet)').length) {
            var sdn_desc = ($('#sdn_desc')).detach();
            $('#sdn_gallery').append(sdn_desc);
        }
    }, 300);
    
    jQuery("#destination-html-switcher").on('touchstart click', function(){
        if (!flag) {
            flag = true;
            html_block_wrapper.removeClass('hidden');
            html_block_trigger.addClass('hidden');
            fyc_block_wrapper.addClass('hidden');
            fyc_block_trigger.removeClass('hidden');
            /*jQuery(".lazyLoadImage").lazyload({
                effect : "fadeIn"
            }).removeClass('lazyLoadImage');*/
            setTimeout(function(){ flag = false; }, 100);
        }
        return false;
    });
    
    jQuery("#destination-fyc-switcher").on('touchstart click', function(){
        if (!flag) {
            flag = true;
            html_block_wrapper.addClass('hidden');
            html_block_trigger.removeClass('hidden');
            fyc_block_wrapper.removeClass('hidden');
            fyc_block_trigger.addClass('hidden');
            setTimeout(function(){ flag = false; }, 100);
        }
        return false;
    });

});
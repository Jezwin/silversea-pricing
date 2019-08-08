$(function() {
    // Route tab : toggle style for active/inactive tab
    $('.c-combo-cruise .select-segment dd a').on('click', function(e) {
        e.preventDefault();
        var $tab = $(this);
        var $item = $tab.closest('dd');
        $item.addClass('active');
        $item.siblings('dd.active').toggleClass('active');

        // init slider inside current tab content
        var $currentSlider;
        $tab.one('shown.bs.tab', function() {
            var $panel = $($(this).attr('href'));
            $currentSlider = $panel.find('.c-slider');
            $currentSlider.slick('unslick').slick(settingSlider);

            // load lazy image
            $panel.find('.lazy').lazy();

            // Scroll to panel
            $('html, body').animate({
                scrollTop : $panel.offset().top - $('.c-header').height() - 24 - $('.c-main-nav__bottom').height()
            }, 300);
        });
    });

    // Overview tab : open suite/route tab for the current item
    $('.c-combo-cruise #overview [data-tab-target]').on('click', function() {
        var $trigger = $(this);

        // scroll to "top" page
        $('html, body').stop().animate({
            scrollTop: $('.c-combo-cruise .c-tab__nav--fixed').offset().top - $('.c-header').height() - 24
        }, 0, function() {
            // Open tab
            var tabId = $trigger.data('tab-target');
            $('a[href="' + tabId + '"]').closest('li').trigger('click');

            if (tabId == '#suitenfare') {
                if($trigger.closest('.c-suitelist').length > 0) {
                    // Open current suite only (force close others suite)
                    $(tabId).find('.panel:eq(' + $trigger.index() + ')').find('[role="tab"]').trigger('click');
                }
            } else {
                // Open current segment
                $(tabId).find('.select-segment dd:eq(' + $trigger.closest('[data-slick-index]').data('slick-index') + ') a').trigger('click');
            }
        });
    });

    // Open benefit and event tab and scroll to target Accordion
    $('.c-combo-cruise .c-vertical-teaser').each(function() {
        var $teaser = $(this);
        $teaser.find('a').on('click', function(e) {
            e.preventDefault();

            // First open benefit and event tab
            $('a[href="#benefitsnevents"]').trigger('click');

            // Next find and scroll to accordion target
            var $target = $('#' + $(this).attr('href').split("#")[1]);

            $('html, body').animate({
                scrollTop : $target.offset().top - $('.c-header').height() - 24 - $('.c-main-nav__bottom').height()
            }, 300).promise().then(function() {
                // Open accordion target
                $target.trigger('click');
            });
        });
    });
    
    jQuery("a.coolanchorminusbig").click(function(){
		//check if it has a hash (i.e. if it's an anchor link)
		if(this.hash){
			var hash = this.hash.substr(1);
			var $toElement = jQuery("[id="+hash+"]");
			var toPosition = $toElement.offset().top - 310;
			//scroll to element
			jQuery("body,html").animate({
				scrollTop : toPosition
			},1000)
			return false;
		}
	});

});
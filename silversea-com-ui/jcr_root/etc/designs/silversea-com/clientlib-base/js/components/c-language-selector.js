$(function() {
    $.fn.initLanguageSelector = function() {
        var $trigger = $('.open-language-selector'),
        $languageSelector = $('#c-language-selector'),
        $triggerContainer = $trigger.closest('.c-main-nav__top');

        OpenLanguageSelector();

        $('body').on('trigger.viewport.changed', function() {
            OpenLanguageSelector();
        });

        // Open language selector according to the viewport
        function OpenLanguageSelector() {
            // "sm" <==> tablet viewport
            if ($.viewportDetect() == 'sm') {
                if (!$('.c-language-selector__list.cloned').length) {
                    $languageSelector.clone().addClass('cloned').attr('id', 'c-language-selector-cloned').appendTo('.c-main-nav__top');
                }
            }

            $trigger.on('click', function(e) {
                e.preventDefault();
                var $collapsible = $('#c-language-selector-cloned, #c-language-selector');
                $collapsible.collapse('toggle');
                $(this).attr('aria-expanded', $collapsible.attr('aria-expanded'));
            })
        }

        this.closeLanguageSelector = function() {
            $('#c-language-selector').collapse('hide');
        };

        return this
    };

    $.fn.initLanguageSelector();

    // force close language selector on click on the screen.
    $(document).on('click', function(e) {
        e.stopPropagation();
        if ($(e.target).parents('.c-language-selector__list').length === 0) {
            $.fn.initLanguageSelector().closeLanguageSelector();
        }
    });
});
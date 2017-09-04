$(function() {
    $.fn.initLanguageSelector = function() {
        var $trigger = $('.open-language-selector'),
        $languageSelector = $('#c-language-selector'),
        $triggerContainer = $trigger.closest('.c-main-nav__top');

        bindLanguageSelector();

        $('body').on('trigger.viewport.changed', function() {
            bindLanguageSelector();
        });

        // Open language selector according to the viewport
        function bindLanguageSelector() {
            // "sm" <==> tablet viewport
            if ($.viewportDetect() === 'sm') {
                // Create specific language selector for tablet viewport
                if (!$('.c-language-selector__list.cloned').length) {
                    $languageSelector.clone().addClass('cloned').attr('id', 'c-language-selector-cloned').appendTo('.c-main-nav__top');
                }
            }

            closeLanguageSelector();

            $trigger.on('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                var $collapsible = $('#c-language-selector-cloned, #c-language-selector');
                $collapsible.collapse('toggle');
                $trigger.attr('aria-expanded', $collapsible.attr('aria-expanded'));
            })
        }

        function closeLanguageSelector() {
            $('#c-language-selector, #c-language-selector-cloned').collapse('hide');
            $trigger.attr('aria-expanded', 'false');
        }

        this.closeLanguageSelector = function() {
            closeLanguageSelector();
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
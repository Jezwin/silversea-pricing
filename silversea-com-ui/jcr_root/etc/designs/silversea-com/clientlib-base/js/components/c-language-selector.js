$(function() {
    var $trigger = $('.open-language-selector');
    var $languageSelector = $('#c-language-selector');
    var $triggerContainer = $trigger.closest('.c-main-nav__top');

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
});
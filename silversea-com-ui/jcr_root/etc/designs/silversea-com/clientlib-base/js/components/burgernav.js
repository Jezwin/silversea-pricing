+function ($) {
    'use strict';

    $.fn.BurgerNav = function() {
        
        var _self = this;
        this.isActive = false;
        
        /*
        ** Toggled Classes to the Navigation
        */
        this.toggledClass = function () {
            _self.upActivity();
            
        
            _self.parent().toggleClass("is-active");
            $('.c-main-nav').toggleClass("c-main-nav--open");
            $('.c-main-nav__backdrop').toggleClass("c-main-nav__backdrop--is-active");
            $('body').toggleClass("c-main-nav--body-fixed");
            $('.c-header').toggleClass("c-main-nav--nav-expanded");
        }
        
        /*
        ** Update the status of navigation
        */
        this.upActivity = function () {
            _self.isActive = !_self.isActive;
            
            if (!_self.isActive)
                _self.cleanListener();
        }
        
        /*
        ** Clean listener for performance
        */
        this.cleanListener = function () {
            $(window).off("resize");
            $('.c-main-nav__backdrop').off("click");
        }

        /*
        ** Click Event to show / hide navigation
        */
        this.click(function () {
            _self.toggledClass();
            
            if (_self.isActive) {
                
                $('.c-main-nav__backdrop').on('click', function () {
                    _self.toggledClass();        
                });
                $(window).on("resize", function() {
                    if ($(this).width() >= 992 && _self.isActive) {
                        _self.toggledClass();
                    }
                });
            }
        });
    };
    
    $(function() {
        /*
        ** Set Plugin BurgerNav to the button
        */
        $(".c-nav-burger button").BurgerNav();
    });
}(jQuery);
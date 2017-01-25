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
            $('.c-main-navigation').toggleClass("c-main-navigation--open");
            $('.c-main-navigation__backdrop').toggleClass("c-main-navigation__backdrop--is-active");
        }
        
        /*
        ** Update the status of navigation
        */
        this.upActivity = function () {
            _self.isActive = !_self.isActive;
        }

        /*
        ** Click Event to show / hide navigation
        */
        this.click(function () {
            _self.toggledClass();
            
            if (_self.isActive) {
                $(window).on("resize", function() {
                    if ($(this).width() >= 992 && _self.isActive) {
                        _self.toggledClass();
                        $(this).off("resize");
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
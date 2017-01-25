+function ($) {
    'use strict';

    $(function() {

        var show = true;
        $(document).scroll(function () {
            console.log('$(document).scrollTop() : ', $(document).scrollTop());
            
            if ($(document).scrollTop() > 120 && show) {
                $('.c-main-navigation__bottom').toggleClass('c-main-navigation__bottom--hide');  
                show = false;
            }
            else if ($(document).scrollTop() < 120 && !show) {
                $('.c-main-navigation__bottom').toggleClass('c-main-navigation__bottom--hide');  
                show = true;
            }
            
        });
        /*
            <div class="new section aem-Grid-newComponent" style="
                display: table;
                height: 900px;
                width: 100%;
            ">
              <div style="
                display: table-cell;
                vertical-align: middle;
            ">
              	CONTENT
              </div>
            </div>
        */
    });
}(jQuery);
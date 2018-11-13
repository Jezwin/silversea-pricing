$(function () {
   "use strict";

   $(".findyourcruise2018 .fyc2018-header-mobile-filter").click(function () {
       var isOpen = $(this).hasClass("fyc2018-filter-mobile-open");
       if (isOpen){
           $(this).removeClass("fyc2018-filter-mobile-open");
           $(".findyourcruise2018 .fyc2018-filters-container").slideUp('slow');
       } else {
           $(this).addClass("fyc2018-filter-mobile-open");
           $(".findyourcruise2018 .fyc2018-filters-container").slideDown('slow');
       }
   });

});
$(function () {
    if($(".c-smarttext").length > 0 ){
        $(".readMoreDesktopTrigger").on("click", function (){
            $(this).fadeOut();
          $(this).closest(".c-smarttext").find(".readMoreDesktopContent").slideDown();
        }
        );

        $(".readMoreMobileTrigger").on("click", function (){
                $(this).fadeOut();
                $(this).closest(".c-smarttext").find(".readMoreMobileContent").slideDown();
            }
        );
    }
});

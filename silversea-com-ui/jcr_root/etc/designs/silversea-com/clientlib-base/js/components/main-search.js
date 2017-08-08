$(document).ready(function(){
    var submitIcon = $('.searchbox-icon');
    var inputBox = $('.searchbox-input');
    var searchBoxSubmit = $('.searchbox-submit');
    var searchBox = $('.searchbox');
    var isOpen = false;

    submitIcon.click(function(){
        if(isOpen == false){
            $(this).removeClass('searchbox-icon');
            $(this).addClass('glyphicon-search-left');
            searchBox.addClass('searchbox-open');
            searchBoxSubmit.css('display','block');
            searchBoxSubmit.css('background-color','#999');
            inputBox.focus();
            isOpen = true;
            inputBox.css('display','block');
            inputBox.css('margin-left','100%');
        } else {
            $(this).addClass('searchbox-icon');
            $(this).removeClass('glyphicon-search-left');
            searchBox.removeClass('searchbox-open');
            inputBox.focusout();
            searchBoxSubmit.css('display','none');
            isOpen = false;
            inputBox.animate({
                marginLeft: "0%",
            }, 1500 );
            inputBox.css('display','none');
        }
    });
    submitIcon.mouseup(function(){
        return false;
    });
    searchBox.mouseup(function(){
        return false;
    });

    $(document).mouseup(function(){
        if(isOpen == true){
            $('.searchbox-icon').css('display','block');
            submitIcon.click();
        }
    });


     $.fn.buttonUp = function() {
         var inputVal = $('.searchbox-input').val();
        inputVal = $.trim(inputVal).length;
        if( inputVal !== 0){
            $('.searchbox-icon').css('display','none');
        } else {
            $('.searchbox-input').val('');
            $('.searchbox-icon').css('display','block');
        }
     }


});
$(function() {

    var wrapperSearch = $('.c-main-nav__search');
    var submitIcon = wrapperSearch.find('.c-main-nav__search__searchbox-icon');
    var inputBox = wrapperSearch.find('.c-main-nav__search__searchbox-input');
    var searchBoxSubmit = wrapperSearch.find('.c-main-nav__search__searchbox-submit');
    var searchBox = wrapperSearch.find('.c-main-nav__search__searchbox');
    var isOpen = false;

    submitIcon.click(function(){
        if(isOpen == false){
            wrapperSearch.css({'z-index': '0'});
            searchBox.css({'witdh': '300'});
            $(this).removeClass('c-main-nav__search__searchbox-icon');
            $(this).addClass('glyphicon-search-left');
            searchBox.addClass('searchbox-open');
            searchBoxSubmit.css('display','block');
            searchBoxSubmit.css('background-color','#999');
            inputBox.focus();
            isOpen = true;
            inputBox.css('display','block');
            inputBox.css('margin-left','100%');
        } else {
            wrapperSearch.css({'z-index': '-1'});
            searchBox.animate({
                witdh: "50",
            }, 1500 );
            $(this).addClass('c-main-nav__search__searchbox-icon');
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
            submitIcon.css('display','block');
            submitIcon.click();
        }
    });


    $( ".c-search-result__search-trigger" ).click(function() {
        $( ".c-search-result__expand" ).toggle();
    });

});
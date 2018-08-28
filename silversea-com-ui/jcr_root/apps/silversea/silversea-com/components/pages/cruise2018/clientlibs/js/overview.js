function pdfInit(uri, countryCode, ccpt) {
    var printWin = {};
    $('#cruise-2018-pdf-print').on('click', function (e) {
        e.preventDefault();
        if (!window.voyagePdfPrintClicked) {
            window.voyagePdfPrintClicked = true;
            $.ajax({
                url: uri + '.rendition.print.' + countryCode + ccpt + '.pdf',
                beforeSend: function () {
                    $('.cruise-2018-pdf-print-link').toggle();
                    printWin = window.open('', '_blank');
                },
                success: function () {
                    $('.cruise-2018-pdf-print-link').toggle();
                    printWin.location = uri + '.rendition.print.' + countryCode + ccpt + '.pdf';
                    printWin.focus();
                    window.voyagePdfPrintClicked = false;
                }
            });
        }
    });
}

function socialInit(shareText) {
    $('#cruise-2018-facebook').jsSocials({
        showLabel: false,
        showCount: false,
        shareIn: "popup",
        text: shareText,
        shares: ["facebook"]
    });
}

jQuery("a.coolanchorminussmall").click(function(){
    //check if it has a hash (i.e. if it's an anchor link)
    if(this.hash){
        var hash = this.hash.substr(1);
        var $toElement = jQuery("[id="+hash+"]");
        var toPosition = $toElement.offset().top - 120;
        //scroll to element
        jQuery("body,html").animate({
            scrollTop : toPosition
        },1000)
        return false;
    }
});

window.voyagePdfPrintClicked = false;

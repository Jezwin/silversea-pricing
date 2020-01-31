$(document).ready(
    function(){
		$(function() {
            $('.lazy').lazy({ 
                picturetag: function(element) {
                    var source1=element.find(">:first-child");
                    var source2=element.find(">:nth-child(2)");
                    var img=element.find("img:nth-child(3)");
                    var src1=source1.data("srcset");
                    var src2=source2.data("srcset");
                    var src3=img.data("src");
                    source1.attr('srcset',src1);
                    source2.attr('srcset',src2);
                    img.attr('src',src3);
             }
           })
        });

    }
);
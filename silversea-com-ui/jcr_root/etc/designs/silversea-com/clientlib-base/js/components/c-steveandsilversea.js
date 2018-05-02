(function(){
    $('.blueimp-cont').on('click', function(e){
        e.preventDefault();
        var $this = $(this),
            dataUrl = $this.parents('.c-steveandsilversea').data('gallery-path');
        
        $.ajax({
            url: dataUrl,
            success: function(data){
                var elems = $(data).find("[data-image]"),
                    sources=[],
                    t = [];
                Array.prototype.forEach.call(elems, function(e) {

					var posterPath = $(e).find('.ratio').css('background-image').replace('url(','').replace(')','').replace(/\"/gi, "");
                    if(e.dataset.image){
                        if(e.dataset.image.indexOf('mp4') > 0){
                            var src =  {
                                        href: e.dataset.image,
                                        type: 'video/mp4'
                                    };
                            sources.push(src);
                            t.push({
                                poster:posterPath,
                                type: 'video/*',
                                sources:sources
                            });
                        } else {
                            t.push({
                                href: e.dataset.image
                        });
                        }
                    }
                });
                console.log(t);
                blueimp.Gallery(t, {
                    container: '#blueimp-gallery',
                    startSlideshow: false,
                    onclose: function(){
						if (document.exitFullscreen) {
                            document.exitFullscreen()
                          } else if (document.webkitCancelFullScreen) {
                            document.webkitCancelFullScreen()
                          } else if (document.mozCancelFullScreen) {
                            document.mozCancelFullScreen()
                          } else if (document.msExitFullscreen) {
                            document.msExitFullscreen()
                          }
                    },
                    carousel: true
                });

            },
            error: function(){

            }
        });

         if (document.documentElement.requestFullscreen) {
            document.documentElement.requestFullscreen();
            $(window).resize();
        } else if (document.documentElement.webkitRequestFullscreen) {
            document.documentElement.webkitRequestFullscreen();
            $(window).resize();
        } else if (document.documentElement.mozRequestFullScreen) {
            document.documentElement.mozRequestFullScreen();
            $(window).resize();
        } else if (document.documentElement.msRequestFullscreen) {
            document.documentElement.msRequestFullscreen();
            $(window).resize();
        }

    });
    $(window).resize(function() {
        if((window.fullScreen) || (window.innerWidth == screen.width && window.innerHeight == screen.height)) {
            $("html").css("overflow", "hidden");
        } else {
            $("html").css("overflow", "auto");
        }
    });
})();


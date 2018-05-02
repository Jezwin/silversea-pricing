(function(){
    $('.blueimp-cont').on('click', function(e){
        e.preventDefault();
        var $this = $(this),
            dataUrl = $this.parents('.c-steveandsilversea').data('gallery-path');
        
        $.ajax({
            url: dataUrl,
            success: function(data){
                var elems = $(data).find("[data-image]"),
                    t = [];
                Array.prototype.forEach.call(elems, function(e) {
                    t.push({
                        href: e.dataset.image
                    });
                });
                console.log(t);
                blueimp.Gallery(t, {
                    container: '#blueimp-gallery',
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
            document.documentElement.requestFullscreen()
        } else if (document.documentElement.webkitRequestFullscreen) {
            document.documentElement.webkitRequestFullscreen()
        } else if (document.documentElement.mozRequestFullScreen) {
            document.documentElement.mozRequestFullScreen()
        } else if (document.documentElement.msRequestFullscreen) {
            document.documentElement.msRequestFullscreen()
        }

    });
})();


$(function() {
    $.fn.initVideo = function() {
        this.each(function() {
            var $video = $(this);
            var asset = $video.data('video-asset');
            var autoplay = $video.data('video-autoplay') !== 'undefined' ? $video.data('video-autoplay') : '0'; // autoplay false by default

            var s7videoviewer = new s7viewers.VideoViewer({
                'containerId' : $video.attr('id'),
                'params' : {
                    'VideoPlayer.ssl' : 'on',
                    'serverurl' : 'https://silversea-h.assetsadobe2.com/is/image',
                    'contenturl' : 'https://www.silversea.com/',
                    'config' : 'etc/dam/presets/viewer/Video',
                    //'videoserverurl' : 'https://gateway-eu.assetsadobe.com/DMGateway/public/silversea',
                    'videoserverurl' : 'https://gateway-eu.assetsadobe.com/DMGateway/public-ssl/silversea',
                    'posterimage' : asset,
                    'asset' : asset,
                    'autoplay' : autoplay.toString(),
                    'progressivebitrate' : '3000',
                    'initialbitrate' : '3000'
                }

            });

            // Bind initComplete event before init()
            s7videoviewer.setHandlers({
                'initComplete' : function() {
                    var $videoElement = $video.find('video');

                    $videoElement.on('loadstart', function() {
                        // Add loader (svg)
                        $video.addClass('loading');
                    }).on('loadeddata', function() {
                        // remove loader
                        $video.removeClass('loading');
                    });
                }
            });

            s7videoviewer.init()
        });
    };

    // Init video on page load
    function initV(){
        
    	if(typeof s7viewers !== 'undefined'){
    		$('.c-video:not(.c-video--cc-gallery)').initVideo(); 
    	}else {
    		setTimeout(initV,700);
    	}
    }
    	 
   initV();
});
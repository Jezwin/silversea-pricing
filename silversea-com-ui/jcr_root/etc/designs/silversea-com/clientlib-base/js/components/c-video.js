$(function() {
    $.fn.initVideo = function() {
        this.each(function() {
            var $video = $(this);
            var asset = $video.data('video-asset');
            var autoplay = $video.data('video-autoplay') !== 'undefined' ? $video.data('video-autoplay') : '0'; // autoplay false by default
            var s7videoviewer = new s7viewers.VideoViewer({
                'containerId' : $video.attr('id'),
                'params' : {
                    'serverurl' : window.location.origin + '/is/image',
                    'contenturl' : 'https://silversea.assetsadobe.com/',
                    'config' : 'etc/dam/presets/viewer/Video',
                    'videoserverurl' : 'https://gateway-eu.assetsadobe.com/DMGateway/public/silversea',
                    'posterimage' : asset,
                    'asset' : asset,
                    'autoplay' : autoplay.toString(),
                    'initialbitrate' : '2600'
                }
            }).init();
        });
    };

    // Init video on page load
    var $video = $('.c-video');

    $video.each(function(currentVideo, i) {
        if ($(currentVideo).closest('.c-gallery--cc').length > 0) {
            $(currentVideo).initVideo();
        }
    });
});
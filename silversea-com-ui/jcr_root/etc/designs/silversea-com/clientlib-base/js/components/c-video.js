$(function() {
    $.fn.initVideo = function() {
        this.each(function() {
            var $video = $(this);
            var asset = $video.data('asset');
            var s7videoviewer = new s7viewers.VideoViewer({
                'containerId' : $video.attr('id'),
                'params' : {
                    'serverurl' : window.location.origin + '/is/image',
                    'contenturl' : 'https://silversea.assetsadobe.com/',
                    'config' : '/etc/dam/presets/viewer/Video',
                    'videoserverurl' : 'https://gateway-eu.assetsadobe.com/DMGateway/public/silversea',
                    'posterimage' : asset,
                    'asset' : asset
                }
            }).init();
        });
    };

    // Init video on page load
    $('.c-video').initVideo();
});
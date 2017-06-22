$(function(){
    $('.c-video').each(function() {
        var $video = $(this);
        var s7videoviewer = new s7viewers.VideoViewer({
            'containerId' : $video.attr('id'),
            'params' : { 
                'serverurl' : 'http://localhost:4502/is/image/',
                'contenturl' : 'http://localhost:4502/',
                'config' : '/etc/dam/presets/viewer/Video',
                'videoserverurl': 'https://gateway-eu.assetsadobe.com/DMGateway/public/silversea',
                //'autoplay': '0',
                'asset' : $video.data('asset')
            }
        }).init();
    });
});
(function(){

    var portsDest = [];
if($(".finyourport").length){
    $.ajax({
        url: $('.port-dest-search').data('path'),
        async: false,
        success: getPortsDestinationSuccess,
        error: getPortsDestinationError
    });

    function getPortsDestinationSuccess(data, response, xhr){
        portsDest = data.data;
    }

    function getPortsDestinationError(data, response, xhr){
        console.log(arguments);
    }

    function showResults(){
        var results = [],
            destinations = [],
            visiblePorts = [],
            // visiblePorts = $('.finyourport [name="itinerary-port"] option:gt(1):not(:disabled)').map(function(port){return this.value;}),
            filteredPortData = {};

        if($.trim($('.finyourport [name="itinerary-port"]').val()) === ''){
            visiblePorts = $('.finyourport [name="itinerary-port"] option:gt(1):not(:disabled)').map(function(port){return this.value;});
        } else {
            visiblePorts.push($.trim($('.finyourport [name="itinerary-port"]').val()));
        }

        visiblePorts = $.makeArray(visiblePorts);
        filteredPortData = portsDest.map(function(port){
            if(visiblePorts.indexOf(port.port_city_id) > -1){
                destinations = [];
                $.each(port.related_destinations, function(k, v){
                    destinations.push(v.dest_name);
                });
                results += "<a href='"+port.port_path+".html' class='clearfix c-find-port__result__link'><span class='col-sm-5 c-find-port__result__city'>"+port.port_name+"</span><span class='col-sm-7 c-find-port__result__destination'>"+destinations.join(',')+"</span></a>";
            }
        });

        $('.c-find-port__result-list').html(results);
    }

    function setCounter(){
        var selectedPort = $.trim($('.finyourport [name="itinerary-port"]').val()),
            selectedDest = $.trim($('.finyourport [name="destination"]').val()),
            counter = 0;

        if(selectedPort !== ''){
            // port selected
            if(selectedDest === ''){ // destination not selected
                //counter = $('.finyourport [name="destination"] option:not(:disabled)').length-2;
                counter = false;
            } else { // destination selected
                counter = 1;
            }
        } else {
            // port not selected
            if(selectedDest !== ''){ // destination selected
                counter = $('.finyourport [name="itinerary-port"] option:not(:disabled)').length-2;
            } else {
                counter = portsDest.length;
            }
            // for destination not selected -- nothing
        }

        if(counter){
            $('.port-count-container').show();
            $('.port-count').html(counter);
        } else {
            $('.port-count-container').hide();
        }
    }

    function selectDestinations(selectedDestination, selected){
        portsDest.map(function(port){
            if(port.port_city_id == selected){
                $.each(port.related_destinations, function(key, dest){
                    if(selectedDestination === ''){
                        $('.finyourport [name="destination"]').children('[value="'+dest.dest_id+'"]').removeAttr('disabled');
                    } else {
                        $('.finyourport [name="destination"]').children('[value="'+selectedDestination+'"]').removeAttr('disabled');
                    }
                });
            }
        });
        $('.finyourport [name="destination"] option:lt(2)').removeAttr('disabled');
        $('.finyourport [name="destination"]').trigger("chosen:updated");
    }

    function selectPorts(selectedPort, selected){
        portsDest.map(function(port){
            var selectedDest = port.related_destinations.map(function(dest){
                return dest.dest_id;
            });
            if(selectedDest.indexOf(selected) !== -1){
                if(selectedPort === ''){
                    $('.finyourport [name="itinerary-port"]').children('[value="'+port.port_city_id+'"]').removeAttr('disabled');
                } else {
                    $('.finyourport [name="itinerary-port"]').children('[value="'+selectedPort+'"]').removeAttr('disabled');
                }
            }
        });
        $('.finyourport [name="itinerary-port"] option:lt(2)').removeAttr('disabled');
        $('.finyourport [name="itinerary-port"]').trigger("chosen:updated");
    }

    function initChosen(){
        $('.chosen-select').chosen({display_disabled_options: false});
        //setCounter();

        $('[name="itinerary-port"]').off('change').on('change', function(){
            var selected = this.value,
                selectedDestination = $.trim($('[name="destination"]').val());
            if(selectedDestination === ''){
                $('.finyourport [name="destination"] option').removeAttr('disabled');
            }
            if(selected.trim() === ""){
                if(selectedDestination !== ''){
                    selectPorts(selected, selectedDestination);
                } else {
                    $('.finyourport [name="itinerary-port"] option').removeAttr('disabled');
                }
                $('.finyourport [name="itinerary-port"], .finyourport [name="destination"]').trigger("chosen:updated");
                setCounter();
                return;
            }
            $('.finyourport [name="destination"] option').attr('disabled', 'disabled');
            selectDestinations(selectedDestination, selected);
            setCounter();
        });

        $('.finyourport [name="destination"]').off('change').on('change', function(){
            var selected = this.value,
                selectedPort = $.trim($('[name="itinerary-port"]').val());
            if(selectedPort === ''){
                $('.finyourport [name="itinerary-port"] option').removeAttr('disabled');
            }
            if(selected.trim() === ""){
                if(selectedPort !== ''){
                    selectDestinations(selected, selectedPort);
                } else {
                    $('.finyourport [name="destination"] option').removeAttr('disabled');
                }
                $('.finyourport [name="itinerary-port"], .finyourport [name="destination"]').trigger("chosen:updated");
                setCounter();
                return;
            }
            $('.finyourport [name="itinerary-port"] option').attr('disabled', 'disabled');
            selectPorts(selectedPort, selected);
            setCounter();
        });

        $('#counter').off('click').on('click', showResults);
    }

    $(document).ready(initChosen);
}
})();
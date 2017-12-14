(function(){

    var portsDest = [];

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
            visiblePorts = null,
            // visiblePorts = $('[name="itinerary-port"] option:gt(1):not(:disabled)').map(function(port){return this.value;}),
            filteredPortData = {};

        if($.trim($('[name="itinerary-port"]').val()) === ''){
            visiblePorts = $('[name="itinerary-port"] option:gt(1):not(:disabled)').map(function(port){return this.value;});
        } else {
            visiblePorts.push($.trim($('[name="itinerary-port"]').val()));
        }

        visiblePorts = $.makeArray(visiblePorts);
        filteredPortData = portsDest.map(function(port){
            if(visiblePorts.indexOf(port.port_city_id) > -1){
                destinations = [];
                $.each(port.related_destinations, function(k, v){
                    destinations.push(v.dest_name);
                });
                results += "<div class='ports'><span class='port-name'>"+port.port_name+"</span><span class='port-destinations'>"+destinations.join(',')+"</span></div>";
            }
        });

        $('.result-set').html(results);
    }

    function setCounter(){
        var selectedPort = $.trim($('[name="itinerary-port"]').val()),
            selectedDest = $.trim($('[name="destination"]').val()),
            counter = 0;

        if(selectedPort !== ''){
            // port selected
            if(selectedDest === ''){ // destination not selected
                counter = $('[name="destination"] option:not(:disabled)').length-2;
            } else { // destination selected
                counter = 1;
            }
        } else {
            // port not selected
            if(selectedDest !== ''){ // destination selected
                counter = $('[name="itinerary-port"] option:not(:disabled)').length-2;
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
                        $('[name="destination"]').children('[value="'+dest.dest_id+'"]').removeAttr('disabled');
                    } else {
                        $('[name="destination"]').children('[value="'+selectedDestination+'"]').removeAttr('disabled');
                    }
                });
            }
        });
        $('[name="destination"] option:lt(2)').removeAttr('disabled');
        $('[name="destination"]').trigger("chosen:updated");
    }

    function selectPorts(selectedPort, selected){
        portsDest.map(function(port){
            var selectedDest = port.related_destinations.map(function(dest){
                return dest.dest_id;
            });
            if(selectedDest.indexOf(selected) !== -1){
                if(selectedPort === ''){
                    $('[name="itinerary-port"]').children('[value="'+port.port_city_id+'"]').removeAttr('disabled');
                } else {
                    $('[name="itinerary-port"]').children('[value="'+selectedPort+'"]').removeAttr('disabled');
                }
            }
        });
        $('[name="itinerary-port"] option:lt(2)').removeAttr('disabled');
        $('[name="itinerary-port"]').trigger("chosen:updated");
    }

    function initChosen(){
        $('.chosen-select').chosen({display_disabled_options: false});
        setCounter();

        $('[name="itinerary-port"]').off('change').on('change', function(){
            var selected = this.value,
                selectedDestination = $.trim($('[name="destination"]').val());
            if(selectedDestination === ''){
                $('[name="destination"] option').removeAttr('disabled');
            }
            if(selected.trim() === ""){
                if(selectedDestination !== ''){
                    selectPorts(selected, selectedDestination);
                } else {
                    $('[name="itinerary-port"] option').removeAttr('disabled');
                }
                $('[name="itinerary-port"], [name="destination"]').trigger("chosen:updated");
                setCounter();
                return;
            }
            $('[name="destination"] option').attr('disabled', 'disabled');
            selectDestinations(selectedDestination, selected);
            setCounter();
        });

        $('[name="destination"]').off('change').on('change', function(){
            var selected = this.value,
                selectedPort = $.trim($('[name="itinerary-port"]').val());
            if(selectedPort === ''){
                $('[name="itinerary-port"] option').removeAttr('disabled');
            }
            if(selected.trim() === ""){
                if(selectedPort !== ''){
                    selectDestinations(selected, selectedPort);
                } else {
                    $('[name="destination"] option').removeAttr('disabled');
                }
                $('[name="itinerary-port"], [name="destination"]').trigger("chosen:updated");
                setCounter();
                return;
            }
            $('[name="itinerary-port"] option').attr('disabled', 'disabled');
            selectPorts(selectedPort, selected);
            setCounter();
        });

        $('#counter').off('click').on('click', showResults);
    }

    $(document).ready(initChosen);
})();
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

    function initChosen(){
        $('.chosen-select').chosen({display_disabled_options: false});

        $('[name="itinerary-port"]').off('change').on('change', function(){
            var selected = this.value,
                selectedDestination = $.trim($('[name="destination"]').val());
            if(selectedDestination === ''){
                $('[name="destination"] option').removeAttr('disabled');
            }
            if(selected.trim() === ""){
                $('[name="itinerary-port"] option, [name="destination"] option').removeAttr('disabled');
                $('[name="itinerary-port"], [name="destination"]').trigger("chosen:updated");
                setCounter();
                return;
            }
            $('[name="destination"] option').attr('disabled', 'disabled');
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
            setCounter();
        });

        $('[name="destination"]').off('change').on('change', function(){
            var selected = this.value,
                selectedPort = $.trim($('[name="itinerary-port"]').val());
            if(selectedPort === ''){
                $('[name="itinerary-port"] option').removeAttr('disabled');
            }
            if(selected.trim() === ""){
                $('[name="itinerary-port"] option, [name="destination"] option').removeAttr('disabled');
                $('[name="itinerary-port"], [name="destination"]').trigger("chosen:updated");
                setCounter();
                return;
            }
            $('[name="itinerary-port"] option').attr('disabled', 'disabled');
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
            setCounter();
        });

        $('#counter').off('click').on('click', function(){

        });
    }

    $(document).ready(initChosen);
})();
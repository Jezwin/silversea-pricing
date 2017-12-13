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

    function initChosen(){
        debugger;
        $('.chosen-select').chosen({display_disabled_options: false});

        $('[name="itinerary-port"').off('change').on('change', function(){
            var selected = this.value;
            $('[name="destination"] option').removeAttr('disabled');
            if(selected.trim() === ''){
                $('[name="destination"').trigger("chosen:updated");
                return;
            }
            portsDest.map(function(port){
                if(port.port_city_id != selected){
                    $.each(port.related_destinations, function(key, dest){
                        $('[name="destination"]').children('[value="'+dest.dest_id+'"]').attr('disabled', 'disabled');
                    });
                }
            });
            $("#counter").html($('[name="destination"] option:not(:disabled)').length-2);
            $('[name="destination"').trigger("chosen:updated");
        });

        $('[name="destination"').off('change').on('change', function(){
            var selected = this.value;
            $('[name="itinerary-port"] option').removeAttr('disabled');
            if(selected.trim() === ""){
                $('[name="itinerary-port"').trigger("chosen:updated");
                return;
            }
            portsDest.map(function(port){
                var selectedDest = port.related_destinations.map(function(dest){
                    return dest.dest_id;
                });
                if(selectedDest.indexOf(selected) == -1){
                    $('[name="itinerary-port"').children('[value="'+port.port_city_id+'"]').attr('disabled', 'disabled');
                };
            });
            $("#counter").html($('[name="itinerary-port"] option:not(:disabled)').length-2);
            $('[name="itinerary-port"').trigger("chosen:updated");
        });
    }

    $(document).ready(initChosen);
})();
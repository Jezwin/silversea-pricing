$(function () {
    var isVisibleFindTravelAgentComponent = $("#findTravelAgent2019").length > 0,
        findTravelAgentsToShow = [],
        findTravelAgentsFilteredByLocation = [],
        numberSlot = 25,
        travelAgentMarker = [],
        travelAgentMap = null,
        infoWindowList = [],
        defaultAddress = null,
        defaultLocation = null,
        user_country = window.dataLayer[0].user_country,
        lastAddress = null,
        lastLocation = null,
        locationSearched = null,
        findTravelAgentsList = null;

    if (isVisibleFindTravelAgentComponent) {
        try {
            findTravelAgentsList = JSON.parse(window.travelAgents);
            lastLocation = user_country;
            findTravelAgentsToShow = initFindTravelAgentsToShow(findTravelAgentsList, user_country);
            initTravelAgentMap(user_country);
            initLocationFiter();
            initAgencyFilter(findTravelAgentsList);
            $("#findTravelAgent2019").on("click", "#findTravelAgents-more", onClickViewMoreTravelAgent);
            $("#findTravelAgent2019").on("keyup", "#findTravelAgent2019-agency", keyPressAgencyFilter);
            $("#findTravelAgent2019").on("keyup", "#findTravelAgent2019-location", keyPressLocationFilter);
            $("#findTravelAgent2019 #findTravelAgent2019-list").on("click", ".list-group-item", onClickTravelAgent);
            $("#findTravelAgent2019").on("click", ".findTravelAgent2019-zoomhere", zoomHere);
            $("#findTravelAgent2019").on("click", ".findTravelAgent2019-streetview", streetView);
        } catch (e) {
            console.log("error during initialitation findTravelAgent");
        }
    }

    /**
     * Create travel agent marker
     * @param item
     * @param map
     */
    function renderMarketInsideMap(item, map, openWindow) {
        var myLatLng = new google.maps.LatLng(item.latitude, item.longitude);
        var marker = new google.maps.Marker({
            position: myLatLng,
            map: map
        });
        var infoWindow = createMarkerInfoWindow(item);
        marker.addListener('click', function () {
            closeInfoWindows();
            infoWindow.open(map, marker);
        });
        if (openWindow) {
            infoWindow.open(map, marker);
        }
        travelAgentMarker.push(marker);
    };//renderMarketInsideMap

    /**
     * Delete all markers inside map
     * Use global variable travelAgentMarker
     */
    function removeMarkerFromTheMap() {
        for (var i = 0; i < travelAgentMarker.length; i++) {
            travelAgentMarker[i].setMap(null);
        }
        travelAgentMarker = [];
        infoWindowList = [];
    };//removeMarkerFromTheMap

    /**
     * Handler click on travel agent in table
     * Use global variable travelAgentMap
     * @param e
     */
    function onClickTravelAgent(e) {
        e.preventDefault();
        e.stopPropagation();
        var travelAgent = {
            id: $(this).data("id"),
            city: $(this).data("city"),
            title: $(this).data("title"),
            address: $(this).data("address"),
            phone: $(this).data("phone"),
            country: $(this).data("country"),
            latitude: $(this).data("latitude"),
            longitude: $(this).data("longitude"),
            stateCode: $(this).data("stateCode"),
            zip: $(this).data("zip")
        };
        $("#findTravelAgent2019 .travel-agent-classic").removeClass("travel-agent-classic");
        $(this).addClass("travel-agent-classic");
        removeMarkerFromTheMap();
        updateMapCenter(travelAgent, 8);
        renderMarketInsideMap(travelAgent, travelAgentMap, true);
    };//onClickTravelAgent

    /**
     * Initialized google maps based on dataLayer country
     */
    function initTravelAgentMap(country) {
        if (country != null) {
            var geocoder = new google.maps.Geocoder();
            geocoder.geocode({
                componentRestrictions: {
                    country: country
                }
            }, function (results, status) {
                $("#findTravelAgent2019-overlay").hide();
                if (status === 'OK' && results.length > 0) {
                    travelAgentMap = renderTraveAgentMap(results[0]);
                    defaultAddress = results[0].formatted_address;
                    defaultLocation = results[0];
                    setAddressInsideInput(defaultAddress);
                    createMarketInsideMap(travelAgentMap);
                }
            });
        }
    };//initTravelAgentMap

    /**
     * Update google maps based on dataLayer country
     */
    function updateTravelMap(country) {
        if (country != null) {
            var geocoder = new google.maps.Geocoder();
            geocoder.geocode({
                componentRestrictions: {
                    country: country
                }
            }, function (results, status) {
                if (status === 'OK' && results.length > 0) {
                    travelAgentMap.setOptions({
                        center: results[0].geometry.location,
                        zoom: 4
                    });
                    createMarketInsideMap(travelAgentMap);
                }
            });
        }
    };//initTravelAgentMap

    /**
     * Update map center
     * Use global variable travelAgentMap
     */
    function updateMapCenter(item, zoom) {
        if (item != null) {
            var myLatLng = new google.maps.LatLng(item.latitude, item.longitude);
            travelAgentMap.setOptions({
                center: myLatLng,
                zoom: zoom || 4
            });
        }
    };//updateMapCenter

    /**
     * Create all markers inside the map
     * @param map
     */
    function createMarketInsideMap(map) {
        for (var i = 0; i < findTravelAgentsFilteredByLocation.length; i++) {
            var item = findTravelAgentsFilteredByLocation[i];
            renderMarketInsideMap(item, map);
        }
    };//createMarketInsideMap

    /**
     * Create inside the DOM a google map
     * @param point
     * @returns google maps
     */
    function renderTraveAgentMap(point) {
        return new google.maps.Map(document.getElementById('map-findTravelAgent2019'), {
            center: point.geometry.location,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            gestureHandling: "greedy",//"cooperative",
            zoom: 4
        });
    };//renderTraveAgentMap

    /**
     * Handler key press on agency filter
     * Use global variables findTravelAgentsToShow and findTravelAgentsList
     */
    function keyPressAgencyFilter() {
        var filter = $("#findTravelAgent2019 #findTravelAgent2019-agency"),
            isFilterAgencyEmpty = filter.val() != null && filter.val().trim().length == 0;
        if (isFilterAgencyEmpty) {

            clearTravelAgentList();
            $("#findTravelAgent2019 #findTravelAgent2019-location").attr("disabled", null);
            findTravelAgentsToShow = initFindTravelAgentsToShow(findTravelAgentsList, lastLocation);
            for (var i = 0; i < findTravelAgentsToShow.length; i++) {
                createNewTravelAgent(findTravelAgentsToShow[i]);
            }
        }
    };//keyPressAgencyFilter

    /**
     * Handler key press on location filter
     * Use global variable findTravelAgentsList
     */
    function keyPressLocationFilter() {
        var filter = $("#findTravelAgent2019 #findTravelAgent2019-location"),
            isFilterLocationEmpty = filter.val() != null && filter.val().trim().length == 0;
        if (isFilterLocationEmpty) {
            var agencyFitler = $("#findTravelAgent2019-agency");
            var isAgencyFilterNotEmpty = agencyFitler.val() != null && agencyFitler.val().trim().length > 0;
            if (isAgencyFilterNotEmpty) return;
            clearTravelAgentList();
            findTravelAgentsToShow = initFindTravelAgentsToShow(findTravelAgentsList, user_country);
            for (var i = 0; i < findTravelAgentsToShow.length; i++) {
                createNewTravelAgent(findTravelAgentsToShow[i]);
            }
            removeMarkerFromTheMap();
            showTravelAgentList();
            travelAgentMap.setOptions({
                center: defaultLocation.geometry.location,
                zoom: 4
            });
            createMarketInsideMap(travelAgentMap);
            initAgencyFilter(findTravelAgentsToShow);
        }
    };//keyPressLocationFilter

    /**
     * Initialize agency filter with easy complete
     * @param all travel agents list
     */
    function initAgencyFilter(originalList) {
        var options = {};
        options.data = originalList;
        options.getValue = "title";
        options.list = {
            match: {
                enabled: true
            },
            onChooseEvent: function () {
                var travelAgent = $("#findTravelAgent2019-agency").getSelectedItemData();
                if (travelAgent != null) {
                    clearTravelAgentList();
                    hideViewMoreBtn();
                    showTravelAgentList();
                    removeMarkerFromTheMap();
                    updateResultNumbers(1);
                    updateMapCenter(travelAgent, 8);
                    createNewTravelAgent(travelAgent);
                    renderMarketInsideMap(travelAgent, travelAgentMap, true);
                    $("#findTravelAgent2019 #findTravelAgent2019-location").attr("disabled", "disabled");
                }
            }
        };
        $("#findTravelAgent2019-agency").easyAutocomplete(options);
    };//initAgencyFilter

    /**
     * Delete all travel agents inside the DOM
     */
    function clearTravelAgentList() {
        $("#findTravelAgent2019-list").empty();
    };//clearTravelAgentList

    /**
     * Search all find travel agent based on location and create div on the DOM
     * Use global variable findTravelAgentsToShow, numberSlot, findTravelAgentsList and findTravelAgentsFilteredByLocation
     * @param address
     */
    function searchTravelAgentByLocation(address) {
        findTravelAgentsFilteredByLocation = [];
        var zoom = 4;
        for (var i = 0; i < findTravelAgentsList.length; i++) {
            var isAgentToInsert = checkSpecificLocationWithAgent(address, findTravelAgentsList[i]);
            if (isAgentToInsert) {
                findTravelAgentsFilteredByLocation.push(findTravelAgentsList[i]);
            }
        }
        if (findTravelAgentsFilteredByLocation.length == 0 && address[0].types.includes("administrative_area_level_1")) {
            for (var i = 0; i < findTravelAgentsList.length; i++) {
                var isAgentToInsert = checkLocationWithAgent(address, findTravelAgentsList[i]);
                if (isAgentToInsert) {
                    findTravelAgentsFilteredByLocation.push(findTravelAgentsList[i]);
                }
            }
            findTravelAgentsFilteredByLocation = sortByDistance(locationSearched, findTravelAgentsFilteredByLocation);
            zoom = 8;
        }
        clearTravelAgentList();
        hideViewMoreBtn();
        removeMarkerFromTheMap();
        if (findTravelAgentsFilteredByLocation.length > 0) {
            showTravelAgentList();
            hideViewMoreBtn();
            findTravelAgentsToShow = findTravelAgentsFilteredByLocation.slice(0, numberSlot + 1);
            for (var i = 0; i < findTravelAgentsToShow.length; i++) {
                createNewTravelAgent(findTravelAgentsToShow[i]);
            }
            if (findTravelAgentsFilteredByLocation.length > numberSlot) {
                showViewMoreBtn();
            }
            var country = null;
            if (address.length > 0) {
                for (var i = 0; i < address.length; i++) {
                    if (address[i].types.includes("country")) {
                        country = address[i].short_name;
                    }
                }
            } else {
                country = address.short_name;
            }
            lastLocation = country;

            updateResultNumbers(findTravelAgentsFilteredByLocation.length);
            initAgencyFilter(findTravelAgentsFilteredByLocation);
            travelAgentMap.setOptions({
                center: locationSearched,
                zoom: zoom
            });
            createMarketInsideMap(travelAgentMap);
        } else {
            updateResultNumbers(0);
            hideTravelAgentList();
        }

    };//searchTravelAgentByLocation

    /**
     * Check specific location otherwise callback to generic one
     * @param address
     * @param agent
     * @returns {boolean}
     */
    function checkSpecificLocationWithAgent(address, agent) {
        var typesToCompare = address[0].types,
            itemToCompare = address[0].short_name;
        if (compareType(typesToCompare)) {
            return compareLocation(typesToCompare, itemToCompare, agent);
        }
        return checkLocationWithAgent(address, agent);
    };//checkAddressWithAgent

    /**
     * General compare if the user has selected not specific types
     * @param address
     * @param agent
     * @returns {boolean}
     */
    function checkLocationWithAgent(address, agent) {
        for (var i = 1; i < address.length; i++) {
            if (compareLocation(address[i].types, address[i].short_name, agent)) return true;
        }
        return false;
    };//checkAddressWithAgent

    /**
     * Check if the user has selected a specific type
     * @param typesToCompare
     * @returns {boolean}
     */
    function compareType(typesToCompare) {
        return typesToCompare.includes("route") || typesToCompare.includes("street_number") || typesToCompare.includes("locality") || typesToCompare.includes("country") || typesToCompare.includes("postal_code") || typesToCompare.includes("administrative_area_level_1");
    };//compareLocation

    /**
     * Based on type compare the location
     * Use window.isoCode3List
     * @param typesToCompare
     * @param itemToCompare
     * @param agent
     * @returns {boolean}
     */
    function compareLocation(typesToCompare, itemToCompare, agent) {
        if (typesToCompare.includes("route") || typesToCompare.includes("street_number")) {
            return distanceTo(locationSearched, agent) < 1;
        } else if (typesToCompare.includes("locality")) {
            return itemToCompare.toLowerCase() == agent.city.toLowerCase();
        } else if (typesToCompare.includes("country")) {
            return window.isoCode3List[itemToCompare] == agent.country;
        } else if (typesToCompare.includes("postal_code")) {
            return itemToCompare == agent.zip;
        } else if (typesToCompare.includes("administrative_area_level_1")) {
            return itemToCompare == agent.stateCode;
        }
        return false;
    };//compareLocation

    /**
     * Initialize location filter with google maps API
     */
    function initLocationFiter() {
        var $inputLocation = document.getElementById("findTravelAgent2019-location");
        var autocompleteLocation = new google.maps.places.Autocomplete($inputLocation);
        autocompleteLocation.addListener("place_changed", function () {
            lastAddress = autocompleteLocation.getPlace().address_components;
            locationSearched = new google.maps.LatLng(autocompleteLocation.getPlace().geometry.location.lat(), autocompleteLocation.getPlace().geometry.location.lng());
            if (lastAddress != null) {
                searchTravelAgentByLocation(lastAddress);
            }
        });
    };//initLocationFiter

    /**
     * Use global variable numberSlot
     * @param originalList = all travel agents
     * @returns travel agents showed on the DOM
     */
    function initFindTravelAgentsToShow(originalList, country) {
        findTravelAgentsFilteredByLocation = [];
        hideViewMoreBtn()
        var index = 0;
        for (var i = 0; i < originalList.length; i++) {
            if (window.isoCode3List[country] == originalList[i].country) {
                findTravelAgentsFilteredByLocation.push(originalList[i]);
                if (index++ < 25) {
                    createNewTravelAgent(originalList[i]);
                }
            }
        }
        if (findTravelAgentsFilteredByLocation.length > numberSlot) {
            showViewMoreBtn();
        }
        updateResultNumbers(findTravelAgentsFilteredByLocation.length);
        return findTravelAgentsFilteredByLocation.slice(0, numberSlot + 1);
    };//inittFindTravelAgentsToShow

    /**
     * Click on view more
     * Use global variable numberSlot
     * @param toShowList = element showed
     * @param originalList = all travel agent
     * @returns update list travel agents in the DOM
     */
    function showMoreTravelAgents(toShowList, originalList) {
        var numberShowed = toShowList.length,
            numberToShow = toShowList.length + numberSlot;
        toShowList = originalList.slice(0, numberToShow);
        if (toShowList.length >= originalList.length) {
            hideViewMoreBtn();
        }
        for (var i = numberShowed; i < numberToShow && toShowList[i] != null; i++) {
            createNewTravelAgent(toShowList[i]);
        }
        return toShowList;
    };//showMoreTravelAgents

    /**
     * Append new travel agent in the DOM
     * @param agent to create
     */
    function createNewTravelAgent(agent) {
        if (agent != null) {
            var phoneLabel = $("#findTravelAgent2019").data("phone-label");
            var $newAgent = "<a id='" + agent.agencyId + "'" + "class='list-group-item' data-zip='" + agent.zip + "'  data-stateCode='" + agent.stateCode + "' data-latitude='" + agent.latitude + "' data-longitude='" + agent.longitude + "' data-id='" + agent.agencyId + "' data-phone='" + agent.phone + "' data-city='" + agent.city + "' data-address='" + agent.address + "' data-title='" + agent.title + "'" + " >" +
                "<span class='list-group-item-heading'>" + agent.title + "</span>" +
                "<p class='list-group-item-text'>" + agent.address + "<br>" + agent.city + "<br>" +
                "<b>" + phoneLabel + ": </b>" + agent.phone + "</p> </a>";
            $("#findTravelAgent2019-list").append($newAgent);
        }
    };//createNewTravelAgent

    function hideViewMoreBtn() {
        $("#findTravelAgent2019 #findTravelAgents-more").hide();
    };//hideViewMoreBtn

    function showViewMoreBtn() {
        $("#findTravelAgent2019 #findTravelAgents-more").show();
    };//showViewMoreBtn

    /**
     * Hanlder click on view more
     * Use global variable findTravelAgentsToShow and findTravelAgentsList
     * @param event
     */
    function onClickViewMoreTravelAgent(e) {
        e.preventDefault();
        e.stopPropagation();
        findTravelAgentsToShow = showMoreTravelAgents(findTravelAgentsToShow, findTravelAgentsFilteredByLocation);
    };//onClickViewMoreTravelAgent

    /**
     * Create popup info window with a marker
     * @param item
     * @returns {google.maps.InfoWindow}
     */
    function createMarkerInfoWindow(item) {
        var infoWindow = new google.maps.InfoWindow();
        var phoneLabel = $("#findTravelAgent2019").data("phone-label");
        infoWindow.setContent('\
				<div class="agent-info">\
				<div class="agent-title">' + item.title + '</div>\
				<div>' + item.address + '</div>\
				<div>' + item.city + '</div>\
				<div><label>' + phoneLabel + '</label>: <a href="callto:"' + item.phone + '</a>' + item.phone + '</div></div>\
				<div><a href="#" class="action findTravelAgent2019-zoomhere zoomhere" data-lat="' + item.latitude + '" data-ln="' + item.longitude + '">Zoom here</a>\
				<a href="#" class="action findTravelAgent2019-streetview streetview" data-lat="' + item.latitude + '" data-ln="' + item.longitude + '">Street view</a>\
				</div>\
		');
        infoWindowList.push(infoWindow);
        return infoWindow;
    };//createMarkerInfoWindow

    /**
     * Remove all info window inside the map
     * Use global variable infoWindowList
     */
    function closeInfoWindows() {
        for (var i = 0; i < infoWindowList.length; i++) {
            infoWindowList[i].close();
        }
    }; //closeInfoWindows

    /**
     * Handle zoom click inside the info window
     * Use global variable travelAgentMap
     * @param lat
     * @param lng
     * @param e
     */
    function zoomHere(e) {
        e.preventDefault();
        e.stopPropagation();
        var lat = $(this).data("lat");
        var lng = $(this).data("ln");
        travelAgentMap.setOptions({
            center: {
                lat: lat,
                lng: lng
            },
            zoom: 16
        });
    }; //zoomHere

    /**
     * Handle street map click inside the info window
     * Use global variable travelAgentMap
     * @param lat
     * @param lng
     * @param e
     */
    function streetView(e) {
        e.preventDefault();
        e.stopPropagation();
        var lat = $(this).data("lat");
        var lng = $(this).data("ln");
        var streetView = travelAgentMap.getStreetView();
        streetView.setPosition({
            lat: lat,
            lng: lng
        });
        streetView.setVisible(true);
    }; //streetView

    /**
     * Update number results above the list
     * @param list
     */
    function updateResultNumbers(number) {
        var $spanResults = $("#findTravelAgent2019 #findTravelAgent2019-results");
        var label = number > 1 ? $spanResults.data("results") : $spanResults.data("result");
        $spanResults.html(number + " " + label);
    };//updateResultNumbers

    function hideTravelAgentList() {
        $("#findTravelAgent2019 .scroll-box").hide();
    };//hideTravelAgentList

    function showTravelAgentList() {
        $("#findTravelAgent2019 .scroll-box").show();
    };//showTravelAgentList

    function setAddressInsideInput(value) {
        $("#findTravelAgent2019 #findTravelAgent2019-location").val(value);
    };//setAddressInsideInput

    function distanceTo(point, location) {
        var R = 6371; // mean radius of earth
        var lat1 = toRad_(location.latitude);
        var lon1 = toRad_(location.longitude);
        var lat2 = toRad_(point.lat());
        var lon2 = toRad_(point.lng());
        var dLat = lat2 - lat1;
        var dLon = lon2 - lon1;

        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(lat1) * Math.cos(lat2) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }; //distanceTo

    function sortByDistance(latLng, data) {
        return data.sort(function (a, b) {
            return distanceTo(latLng, a) - distanceTo(latLng, b);
        });
    }; //sortByDistance

    function toRad_(degrees) {
        return degrees * Math.PI / 180;
    }; //toRad_
});
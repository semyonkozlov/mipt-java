package ru.mipt.diht.students.semyonkozloff.moduletests.library;

import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Query;

import static
        ru.mipt.diht.students.semyonkozloff.moduletests.library.LocationSearch.*;

public class QueryMaker {

    public static FilterQuery makeFilterQuery(Configuration configuration)
            throws Exception {
        FilterQuery filterQuery = new FilterQuery();

        filterQuery.track(configuration.getQuery());

        Geometry locationBox = null;
        try {
            locationBox = findLocation(configuration.getLocation());
        } catch (Exception e) {
            Exception exception = new Exception("Fail of finding location.");
            exception.initCause(e);
            throw exception;
        }

        if (locationBox != null) {
            double[] southwest = {
                    locationBox.bounds.southwest.lng,
                    locationBox.bounds.southwest.lat
            };
            double[] northeast = {
                    locationBox.bounds.northeast.lng,
                    locationBox.bounds.northeast.lat
            };

            filterQuery.locations(southwest, northeast);
        }

        return filterQuery;
    }

    public static Query makeQuery(Configuration configuration)
            throws Exception {
        Query query = new Query();

        query.setCount(configuration.getLimit());

        String queryString = configuration.getQuery();
        if (configuration.shouldHideRetweets()) {
            queryString += " -filter:retweets";
        }
        query.setQuery(queryString);

        Geometry locationBox = null;
        try {
            locationBox = findLocation(configuration.getLocation());
        } catch (Exception e) {
            Exception exception = new Exception("Fail of finding location.");
            exception.initCause(e);
            throw exception;
        }

        if (locationBox != null) {
            LatLng northeast = locationBox.bounds.northeast;
            LatLng southwest = locationBox.bounds.southwest;

            double latitude = (northeast.lat + southwest.lat) / 2;
            double longitude = (northeast.lng + southwest.lng) / 2;
            double radius =
                    computeCoordinatesDistance(northeast, southwest) / 2;
            GeoLocation geoLocation = new GeoLocation(latitude, longitude);

            query.setGeoCode(geoLocation, radius, Query.Unit.km);
        }

        return query;
    }
}
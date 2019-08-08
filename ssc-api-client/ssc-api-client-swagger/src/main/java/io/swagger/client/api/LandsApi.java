

package io.swagger.client.api;

import io.swagger.client.ApiCallback;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.Configuration;
import io.swagger.client.Pair;
import io.swagger.client.ProgressRequestBody;
import io.swagger.client.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import org.joda.time.DateTime;
import io.swagger.client.model.Land;
import io.swagger.client.model.Land77;
import io.swagger.client.model.LandItinerary;
import io.swagger.client.model.LandItinerary77;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandsApi {
    private ApiClient apiClient;

    public LandsApi() {
        this(Configuration.getDefaultApiClient());
    }

    public LandsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for landsGet */
    private com.squareup.okhttp.Call landsGetCall(Integer cityId, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/landAdventures".replaceAll("\\{format\\}","json");

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (cityId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "city_id", cityId));
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
        if (envelope != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "envelope", envelope));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json", "text/json", "application/xml", "text/xml"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    /**
     * 
     * 
     * @param cityId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return List&lt;Land&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Land> landsGet(Integer cityId, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<Land>> resp = landsGetWithHttpInfo(cityId, page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param cityId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Land&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Land>> landsGetWithHttpInfo(Integer cityId, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = landsGetCall(cityId, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Land>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param cityId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call landsGetAsync(Integer cityId, Integer page, Integer perPage, String envelope, final ApiCallback<List<Land>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = landsGetCall(cityId, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Land>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for landsGetChanges */
    private com.squareup.okhttp.Call landsGetChangesCall(String changesFrom, Integer cityId, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'changesFrom' is set
        if (changesFrom == null) {
            throw new ApiException("Missing the required parameter 'changesFrom' when calling landsGetChanges(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/landAdventures/changesFrom/{changes_from}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "changes_from" + "\\}", apiClient.escapeString(changesFrom.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (cityId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "city_id", cityId));
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
        if (envelope != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "envelope", envelope));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json", "text/json", "application/xml", "text/xml"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    /**
     * 
     * 
     * @param changesFrom  (required)
     * @param cityId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return List&lt;Land77&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Land77> landsGetChanges(String changesFrom, Integer cityId, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<Land77>> resp = landsGetChangesWithHttpInfo(changesFrom, cityId, page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param changesFrom  (required)
     * @param cityId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Land77&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Land77>> landsGetChangesWithHttpInfo(String changesFrom, Integer cityId, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = landsGetChangesCall(changesFrom, cityId, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Land77>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param changesFrom  (required)
     * @param cityId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call landsGetChangesAsync(String changesFrom, Integer cityId, Integer page, Integer perPage, String envelope, final ApiCallback<List<Land77>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = landsGetChangesCall(changesFrom, cityId, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Land77>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for landsGetItinerary */
    private com.squareup.okhttp.Call landsGetItineraryCall(Integer cityId, Integer voyageId, DateTime date, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/landAdventures/Itinerary".replaceAll("\\{format\\}","json");

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (cityId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "city_id", cityId));
        if (voyageId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "voyage_id", voyageId));
        if (date != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "date", date));
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
        if (envelope != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "envelope", envelope));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json", "text/json", "application/xml", "text/xml"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    /**
     * 
     * 
     * @param cityId  (optional)
     * @param voyageId  (optional)
     * @param date  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return List&lt;LandItinerary&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<LandItinerary> landsGetItinerary(Integer cityId, Integer voyageId, DateTime date, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<LandItinerary>> resp = landsGetItineraryWithHttpInfo(cityId, voyageId, date, page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param cityId  (optional)
     * @param voyageId  (optional)
     * @param date  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;LandItinerary&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<LandItinerary>> landsGetItineraryWithHttpInfo(Integer cityId, Integer voyageId, DateTime date, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = landsGetItineraryCall(cityId, voyageId, date, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<LandItinerary>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param cityId  (optional)
     * @param voyageId  (optional)
     * @param date  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call landsGetItineraryAsync(Integer cityId, Integer voyageId, DateTime date, Integer page, Integer perPage, String envelope, final ApiCallback<List<LandItinerary>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = landsGetItineraryCall(cityId, voyageId, date, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<LandItinerary>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for landsGetItineraryChanges */
    private com.squareup.okhttp.Call landsGetItineraryChangesCall(String changesFrom, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'changesFrom' is set
        if (changesFrom == null) {
            throw new ApiException("Missing the required parameter 'changesFrom' when calling landsGetItineraryChanges(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/landAdventures/Itinerary/changesFrom/{changes_from}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "changes_from" + "\\}", apiClient.escapeString(changesFrom.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
        if (envelope != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "envelope", envelope));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json", "text/json", "application/xml", "text/xml"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    /**
     * 
     * 
     * @param changesFrom  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return List&lt;LandItinerary&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<LandItinerary77> landsGetItineraryChanges(String changesFrom, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<LandItinerary77>> resp = landsGetItineraryChangesWithHttpInfo(changesFrom, page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param changesFrom  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;LandItinerary&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<LandItinerary77>> landsGetItineraryChangesWithHttpInfo(String changesFrom, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = landsGetItineraryChangesCall(changesFrom, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<LandItinerary77>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param changesFrom  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call landsGetItineraryChangesAsync(String changesFrom, Integer page, Integer perPage, String envelope, final ApiCallback<List<LandItinerary>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = landsGetItineraryChangesCall(changesFrom, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<LandItinerary>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for landsGetOne */
    private com.squareup.okhttp.Call landsGetOneCall(Integer id, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new ApiException("Missing the required parameter 'id' when calling landsGetOne(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/landAdventures/{id}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (envelope != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "envelope", envelope));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json", "text/json", "application/xml", "text/xml"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    /**
     * 
     * 
     * @param id  (required)
     * @param envelope  (optional)
     * @return List&lt;Land&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Land> landsGetOne(Integer id, String envelope) throws ApiException {
        ApiResponse<List<Land>> resp = landsGetOneWithHttpInfo(id, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param id  (required)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Land&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Land>> landsGetOneWithHttpInfo(Integer id, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = landsGetOneCall(id, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Land>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param id  (required)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call landsGetOneAsync(Integer id, String envelope, final ApiCallback<List<Land>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = landsGetOneCall(id, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Land>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}

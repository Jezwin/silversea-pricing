

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
import io.swagger.client.model.Hotel;
import io.swagger.client.model.Hotel77;
import io.swagger.client.model.HotelItinerary;
import io.swagger.client.model.HotelItinerary77;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotelsApi {
    private ApiClient apiClient;

    public HotelsApi() {
        this(Configuration.getDefaultApiClient());
    }

    public HotelsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for hotelsGet */
    private com.squareup.okhttp.Call hotelsGetCall(Integer cityId, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/hotels".replaceAll("\\{format\\}","json");

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
     * @return List&lt;Hotel&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Hotel> hotelsGet(Integer cityId, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<Hotel>> resp = hotelsGetWithHttpInfo(cityId, page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param cityId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Hotel&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Hotel>> hotelsGetWithHttpInfo(Integer cityId, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = hotelsGetCall(cityId, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Hotel>>(){}.getType();
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
    public com.squareup.okhttp.Call hotelsGetAsync(Integer cityId, Integer page, Integer perPage, String envelope, final ApiCallback<List<Hotel>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = hotelsGetCall(cityId, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Hotel>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for hotelsGetChanges */
    private com.squareup.okhttp.Call hotelsGetChangesCall(String changesFrom, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'changesFrom' is set
        if (changesFrom == null) {
            throw new ApiException("Missing the required parameter 'changesFrom' when calling hotelsGetChanges(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/hotels/changesFrom/{changes_from}".replaceAll("\\{format\\}","json")
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
     * @return List&lt;Hotel77&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Hotel77> hotelsGetChanges(String changesFrom, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<Hotel77>> resp = hotelsGetChangesWithHttpInfo(changesFrom, page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param changesFrom  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Hotel77&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Hotel77>> hotelsGetChangesWithHttpInfo(String changesFrom, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = hotelsGetChangesCall(changesFrom, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Hotel77>>(){}.getType();
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
    public com.squareup.okhttp.Call hotelsGetChangesAsync(String changesFrom, Integer page, Integer perPage, String envelope, final ApiCallback<List<Hotel77>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = hotelsGetChangesCall(changesFrom, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Hotel77>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for hotelsGetItinerary */
    private com.squareup.okhttp.Call hotelsGetItineraryCall(Integer cityId, Integer voyageId, DateTime date, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/hotels/Itinerary".replaceAll("\\{format\\}","json");

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
     * @return List&lt;HotelItinerary&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<HotelItinerary> hotelsGetItinerary(Integer cityId, Integer voyageId, DateTime date, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<HotelItinerary>> resp = hotelsGetItineraryWithHttpInfo(cityId, voyageId, date, page, perPage, envelope);
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
     * @return ApiResponse&lt;List&lt;HotelItinerary&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<HotelItinerary>> hotelsGetItineraryWithHttpInfo(Integer cityId, Integer voyageId, DateTime date, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = hotelsGetItineraryCall(cityId, voyageId, date, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<HotelItinerary>>(){}.getType();
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
    public com.squareup.okhttp.Call hotelsGetItineraryAsync(Integer cityId, Integer voyageId, DateTime date, Integer page, Integer perPage, String envelope, final ApiCallback<List<HotelItinerary>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = hotelsGetItineraryCall(cityId, voyageId, date, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<HotelItinerary>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for hotelsGetItinerary2 */
    private com.squareup.okhttp.Call hotelsGetItinerary2Call(String changesFrom, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'changesFrom' is set
        if (changesFrom == null) {
            throw new ApiException("Missing the required parameter 'changesFrom' when calling hotelsGetItinerary2(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/hotels/Itinerary/changesFrom/{changes_from}".replaceAll("\\{format\\}","json")
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
     * @return List&lt;HotelItinerary77&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<HotelItinerary77> hotelsGetItinerary2(String changesFrom, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<HotelItinerary77>> resp = hotelsGetItinerary2WithHttpInfo(changesFrom, page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param changesFrom  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;HotelItinerary77&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<HotelItinerary77>> hotelsGetItinerary2WithHttpInfo(String changesFrom, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = hotelsGetItinerary2Call(changesFrom, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<HotelItinerary77>>(){}.getType();
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
    public com.squareup.okhttp.Call hotelsGetItinerary2Async(String changesFrom, Integer page, Integer perPage, String envelope, final ApiCallback<List<HotelItinerary77>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = hotelsGetItinerary2Call(changesFrom, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<HotelItinerary77>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for hotelsGetOne */
    private com.squareup.okhttp.Call hotelsGetOneCall(Integer id, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new ApiException("Missing the required parameter 'id' when calling hotelsGetOne(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/hotels/{id}".replaceAll("\\{format\\}","json")
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
     * @return List&lt;Hotel&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Hotel> hotelsGetOne(Integer id, String envelope) throws ApiException {
        ApiResponse<List<Hotel>> resp = hotelsGetOneWithHttpInfo(id, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param id  (required)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Hotel&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Hotel>> hotelsGetOneWithHttpInfo(Integer id, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = hotelsGetOneCall(id, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Hotel>>(){}.getType();
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
    public com.squareup.okhttp.Call hotelsGetOneAsync(Integer id, String envelope, final ApiCallback<List<Hotel>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = hotelsGetOneCall(id, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Hotel>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}

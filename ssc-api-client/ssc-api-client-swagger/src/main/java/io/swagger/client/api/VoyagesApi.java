

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

import io.swagger.client.model.Itinerary;
import io.swagger.client.model.Voyage;
import io.swagger.client.model.Voyage77;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoyagesApi {
    private ApiClient apiClient;

    public VoyagesApi() {
        this(Configuration.getDefaultApiClient());
    }

    public VoyagesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for voyagesGet */
    private com.squareup.okhttp.Call voyagesGetCall(Integer destinationId, Integer shipId, Integer voyageId, String after, String before, Integer page, Integer perPage, String languageCod, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/voyages".replaceAll("\\{format\\}","json");

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (destinationId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "destination_id", destinationId));
        if (shipId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "ship_id", shipId));
        if (voyageId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "voyage_id", voyageId));
        if (after != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "after", after));
        if (before != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "before", before));
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
        if (languageCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "language_cod", languageCod));
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
     * @param destinationId  (optional)
     * @param shipId  (optional)
     * @param voyageId  (optional)
     * @param after  (optional)
     * @param before  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return List&lt;Voyage&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Voyage> voyagesGet(Integer destinationId, Integer shipId, Integer voyageId, String after, String before, Integer page, Integer perPage, String languageCod, String envelope) throws ApiException {
        ApiResponse<List<Voyage>> resp = voyagesGetWithHttpInfo(destinationId, shipId, voyageId, after, before, page, perPage, languageCod, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param destinationId  (optional)
     * @param shipId  (optional)
     * @param voyageId  (optional)
     * @param after  (optional)
     * @param before  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Voyage&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Voyage>> voyagesGetWithHttpInfo(Integer destinationId, Integer shipId, Integer voyageId, String after, String before, Integer page, Integer perPage, String languageCod, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = voyagesGetCall(destinationId, shipId, voyageId, after, before, page, perPage, languageCod, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Voyage>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param destinationId  (optional)
     * @param shipId  (optional)
     * @param voyageId  (optional)
     * @param after  (optional)
     * @param before  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call voyagesGetAsync(Integer destinationId, Integer shipId, Integer voyageId, String after, String before, Integer page, Integer perPage, String languageCod, String envelope, final ApiCallback<List<Voyage>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = voyagesGetCall(destinationId, shipId, voyageId, after, before, page, perPage, languageCod, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Voyage>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for voyagesGet2 */
    private com.squareup.okhttp.Call voyagesGet2Call(Integer year, Integer month, Integer destinationId, Integer shipId, Integer page, Integer perPage, String languageCod, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'year' is set
        if (year == null) {
            throw new ApiException("Missing the required parameter 'year' when calling voyagesGet2(Async)");
        }
        
        // verify the required parameter 'month' is set
        if (month == null) {
            throw new ApiException("Missing the required parameter 'month' when calling voyagesGet2(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/voyages/{year}/{month}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "year" + "\\}", apiClient.escapeString(year.toString()))
        .replaceAll("\\{" + "month" + "\\}", apiClient.escapeString(month.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (destinationId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "destination_id", destinationId));
        if (shipId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "ship_id", shipId));
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
        if (languageCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "language_cod", languageCod));
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
     * @param year  (required)
     * @param month  (required)
     * @param destinationId  (optional)
     * @param shipId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return List&lt;Voyage&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Voyage> voyagesGet2(Integer year, Integer month, Integer destinationId, Integer shipId, Integer page, Integer perPage, String languageCod, String envelope) throws ApiException {
        ApiResponse<List<Voyage>> resp = voyagesGet2WithHttpInfo(year, month, destinationId, shipId, page, perPage, languageCod, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param year  (required)
     * @param month  (required)
     * @param destinationId  (optional)
     * @param shipId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Voyage&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Voyage>> voyagesGet2WithHttpInfo(Integer year, Integer month, Integer destinationId, Integer shipId, Integer page, Integer perPage, String languageCod, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = voyagesGet2Call(year, month, destinationId, shipId, page, perPage, languageCod, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Voyage>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param year  (required)
     * @param month  (required)
     * @param destinationId  (optional)
     * @param shipId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call voyagesGet2Async(Integer year, Integer month, Integer destinationId, Integer shipId, Integer page, Integer perPage, String languageCod, String envelope, final ApiCallback<List<Voyage>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = voyagesGet2Call(year, month, destinationId, shipId, page, perPage, languageCod, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Voyage>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for voyagesGet3 */
    private com.squareup.okhttp.Call voyagesGet3Call(Integer id, String languageCod, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new ApiException("Missing the required parameter 'id' when calling voyagesGet3(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/voyages/{id}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (languageCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "language_cod", languageCod));
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
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return List&lt;Voyage&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Voyage> voyagesGet3(Integer id, String languageCod, String envelope) throws ApiException {
        ApiResponse<List<Voyage>> resp = voyagesGet3WithHttpInfo(id, languageCod, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param id  (required)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Voyage&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Voyage>> voyagesGet3WithHttpInfo(Integer id, String languageCod, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = voyagesGet3Call(id, languageCod, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Voyage>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param id  (required)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call voyagesGet3Async(Integer id, String languageCod, String envelope, final ApiCallback<List<Voyage>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = voyagesGet3Call(id, languageCod, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Voyage>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for voyagesGetChanges */
    private com.squareup.okhttp.Call voyagesGetChangesCall(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'changesFrom' is set
        if (changesFrom == null) {
            throw new ApiException("Missing the required parameter 'changesFrom' when calling voyagesGetChanges(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/voyages/changesFrom/{changes_from}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "changes_from" + "\\}", apiClient.escapeString(changesFrom.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
        if (languageCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "language_cod", languageCod));
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
    
    private com.squareup.okhttp.Call multiVoyagesGetChangesCall(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'changesFrom' is set
        if (changesFrom == null) {
            throw new ApiException("Missing the required parameter 'changesFrom' when calling voyagesGetChanges(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/voyages/changesFrom/{changes_from}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "changes_from" + "\\}", apiClient.escapeString(changesFrom.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
        if (languageCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "language_cod", languageCod));
        if (envelope != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "envelope", envelope));

        localVarQueryParams.addAll(apiClient.parameterToPairs("", "is_combo", "true"));
        
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
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return List&lt;Voyage77&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Voyage77> voyagesGetChanges(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope) throws ApiException {
        ApiResponse<List<Voyage77>> resp = voyagesGetChangesWithHttpInfo(changesFrom, page, perPage, languageCod, envelope);
        return resp.getData();
    }
    
    public List<Voyage77> multiVoyagesGetChanges(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope) throws ApiException {
        ApiResponse<List<Voyage77>> resp = multiVoyagesGetChangesWithHttpInfo(changesFrom, page, perPage, languageCod, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param changesFrom  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Voyage77&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Voyage77>> voyagesGetChangesWithHttpInfo(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = voyagesGetChangesCall(changesFrom, page, perPage, languageCod, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Voyage77>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }
    
    public ApiResponse<List<Voyage77>> multiVoyagesGetChangesWithHttpInfo(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = multiVoyagesGetChangesCall(changesFrom, page, perPage, languageCod, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Voyage77>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param changesFrom  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call voyagesGetChangesAsync(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope, final ApiCallback<List<Voyage77>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = voyagesGetChangesCall(changesFrom, page, perPage, languageCod, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Voyage77>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for voyagesGetItinerary */
    private com.squareup.okhttp.Call voyagesGetItineraryCall(Integer voyageId, String languageCod, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'voyageId' is set
        if (voyageId == null) {
            throw new ApiException("Missing the required parameter 'voyageId' when calling voyagesGetItinerary(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/voyages/{voyage_id}/itinerary".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "voyage_id" + "\\}", apiClient.escapeString(voyageId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (languageCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "language_cod", languageCod));
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
     * @param voyageId  (required)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return List&lt;Itinerary&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Itinerary> voyagesGetItinerary(Integer voyageId, String languageCod, String envelope) throws ApiException {
        ApiResponse<List<Itinerary>> resp = voyagesGetItineraryWithHttpInfo(voyageId, languageCod, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param voyageId  (required)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;Itinerary&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Itinerary>> voyagesGetItineraryWithHttpInfo(Integer voyageId, String languageCod, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = voyagesGetItineraryCall(voyageId, languageCod, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<Itinerary>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param voyageId  (required)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call voyagesGetItineraryAsync(Integer voyageId, String languageCod, String envelope, final ApiCallback<List<Itinerary>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = voyagesGetItineraryCall(voyageId, languageCod, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Itinerary>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}

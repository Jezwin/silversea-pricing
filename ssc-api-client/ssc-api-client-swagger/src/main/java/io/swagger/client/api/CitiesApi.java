

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

import io.swagger.client.model.City;
import io.swagger.client.model.City77;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitiesApi {
    private ApiClient apiClient;

    public CitiesApi() {
        this(Configuration.getDefaultApiClient());
    }

    public CitiesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for citiesGet */
    private com.squareup.okhttp.Call citiesGetCall(String city, String countryIso3, Integer page, Integer perPage, String languageCod, String envelope, String embed, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/cities".replaceAll("\\{format\\}","json");

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (city != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "city", city));
        if (countryIso3 != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "country_iso3", countryIso3));
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
        if (languageCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "language_cod", languageCod));
        if (envelope != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "envelope", envelope));
        if (embed != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "embed", embed));

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
     * @param city  (optional)
     * @param countryIso3  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param embed  (optional)
     * @return List&lt;City&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<City> citiesGet(String city, String countryIso3, Integer page, Integer perPage, String languageCod, String envelope, String embed) throws ApiException {
        ApiResponse<List<City>> resp = citiesGetWithHttpInfo(city, countryIso3, page, perPage, languageCod, envelope, embed);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param city  (optional)
     * @param countryIso3  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param embed  (optional)
     * @return ApiResponse&lt;List&lt;City&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<City>> citiesGetWithHttpInfo(String city, String countryIso3, Integer page, Integer perPage, String languageCod, String envelope, String embed) throws ApiException {
        com.squareup.okhttp.Call call = citiesGetCall(city, countryIso3, page, perPage, languageCod, envelope, embed, null, null);
        Type localVarReturnType = new TypeToken<List<City>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param city  (optional)
     * @param countryIso3  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param embed  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call citiesGetAsync(String city, String countryIso3, Integer page, Integer perPage, String languageCod, String envelope, String embed, final ApiCallback<List<City>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = citiesGetCall(city, countryIso3, page, perPage, languageCod, envelope, embed, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<City>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for citiesGetChanges */
    private com.squareup.okhttp.Call citiesGetChangesCall(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope, String embed, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'changesFrom' is set
        if (changesFrom == null) {
            throw new ApiException("Missing the required parameter 'changesFrom' when calling citiesGetChanges(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/cities/changesFrom/{changes_from}".replaceAll("\\{format\\}","json")
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
        if (embed != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "embed", embed));

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
     * @param embed  (optional)
     * @return List&lt;City77&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<City77> citiesGetChanges(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope, String embed) throws ApiException {
        ApiResponse<List<City77>> resp = citiesGetChangesWithHttpInfo(changesFrom, page, perPage, languageCod, envelope, embed);
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
     * @param embed  (optional)
     * @return ApiResponse&lt;List&lt;City77&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<City77>> citiesGetChangesWithHttpInfo(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope, String embed) throws ApiException {
        com.squareup.okhttp.Call call = citiesGetChangesCall(changesFrom, page, perPage, languageCod, envelope, embed, null, null);
        Type localVarReturnType = new TypeToken<List<City77>>(){}.getType();
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
     * @param embed  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call citiesGetChangesAsync(String changesFrom, Integer page, Integer perPage, String languageCod, String envelope, String embed, final ApiCallback<List<City77>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = citiesGetChangesCall(changesFrom, page, perPage, languageCod, envelope, embed, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<City77>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for citiesGetOne */
    private com.squareup.okhttp.Call citiesGetOneCall(Integer id, String languageCod, String envelope, String embed, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new ApiException("Missing the required parameter 'id' when calling citiesGetOne(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/cities/{id}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (languageCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "language_cod", languageCod));
        if (envelope != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "envelope", envelope));
        if (embed != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "embed", embed));

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
     * @param embed  (optional)
     * @return City
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public City citiesGetOne(Integer id, String languageCod, String envelope, String embed) throws ApiException {
        ApiResponse<City> resp = citiesGetOneWithHttpInfo(id, languageCod, envelope, embed);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param id  (required)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param embed  (optional)
     * @return ApiResponse&lt;City&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<City> citiesGetOneWithHttpInfo(Integer id, String languageCod, String envelope, String embed) throws ApiException {
        com.squareup.okhttp.Call call = citiesGetOneCall(id, languageCod, envelope, embed, null, null);
        Type localVarReturnType = new TypeToken<City>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param id  (required)
     * @param languageCod  (optional)
     * @param envelope  (optional)
     * @param embed  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call citiesGetOneAsync(Integer id, String languageCod, String envelope, String embed, final ApiCallback<City> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = citiesGetOneCall(id, languageCod, envelope, embed, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<City>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}

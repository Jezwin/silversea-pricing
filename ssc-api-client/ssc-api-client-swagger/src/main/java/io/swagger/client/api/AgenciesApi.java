

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

import io.swagger.client.model.Agency;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgenciesApi {
    private ApiClient apiClient;

    public AgenciesApi() {
        this(Configuration.getDefaultApiClient());
    }

    public AgenciesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for agenciesGet */
    private com.squareup.okhttp.Call agenciesGetCall(String agency, String countryIso3, String stateCod, String zip, String city, Integer page, Integer perPage, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/agencies".replaceAll("\\{format\\}","json");

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (agency != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "agency", agency));
        if (countryIso3 != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "country_iso3", countryIso3));
        if (stateCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "state_cod", stateCod));
        if (zip != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "zip", zip));
        if (city != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "city", city));
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));

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
     * @param agency  (optional)
     * @param countryIso3  (optional)
     * @param stateCod  (optional)
     * @param zip  (optional)
     * @param city  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @return List&lt;Agency&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Agency> agenciesGet(String agency, String countryIso3, String stateCod, String zip, String city, Integer page, Integer perPage) throws ApiException {
        ApiResponse<List<Agency>> resp = agenciesGetWithHttpInfo(agency, countryIso3, stateCod, zip, city, page, perPage);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param agency  (optional)
     * @param countryIso3  (optional)
     * @param stateCod  (optional)
     * @param zip  (optional)
     * @param city  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @return ApiResponse&lt;List&lt;Agency&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Agency>> agenciesGetWithHttpInfo(String agency, String countryIso3, String stateCod, String zip, String city, Integer page, Integer perPage) throws ApiException {
        com.squareup.okhttp.Call call = agenciesGetCall(agency, countryIso3, stateCod, zip, city, page, perPage, null, null);
        Type localVarReturnType = new TypeToken<List<Agency>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param agency  (optional)
     * @param countryIso3  (optional)
     * @param stateCod  (optional)
     * @param zip  (optional)
     * @param city  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call agenciesGetAsync(String agency, String countryIso3, String stateCod, String zip, String city, Integer page, Integer perPage, final ApiCallback<List<Agency>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = agenciesGetCall(agency, countryIso3, stateCod, zip, city, page, perPage, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Agency>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for agenciesGetNear */
    private com.squareup.okhttp.Call agenciesGetNearCall(Double lat, Double lon, Integer r, Integer page, Integer perPage, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'lat' is set
        if (lat == null) {
            throw new ApiException("Missing the required parameter 'lat' when calling agenciesGetNear(Async)");
        }
        
        // verify the required parameter 'lon' is set
        if (lon == null) {
            throw new ApiException("Missing the required parameter 'lon' when calling agenciesGetNear(Async)");
        }
        
        // verify the required parameter 'r' is set
        if (r == null) {
            throw new ApiException("Missing the required parameter 'r' when calling agenciesGetNear(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/agencies/{lat}/{lon}/{r}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "lat" + "\\}", apiClient.escapeString(lat.toString()))
        .replaceAll("\\{" + "lon" + "\\}", apiClient.escapeString(lon.toString()))
        .replaceAll("\\{" + "r" + "\\}", apiClient.escapeString(r.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));

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
     * @param lat  (required)
     * @param lon  (required)
     * @param r  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @return Agency
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Agency agenciesGetNear(Double lat, Double lon, Integer r, Integer page, Integer perPage) throws ApiException {
        ApiResponse<Agency> resp = agenciesGetNearWithHttpInfo(lat, lon, r, page, perPage);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param lat  (required)
     * @param lon  (required)
     * @param r  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @return ApiResponse&lt;Agency&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Agency> agenciesGetNearWithHttpInfo(Double lat, Double lon, Integer r, Integer page, Integer perPage) throws ApiException {
        com.squareup.okhttp.Call call = agenciesGetNearCall(lat, lon, r, page, perPage, null, null);
        Type localVarReturnType = new TypeToken<Agency>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param lat  (required)
     * @param lon  (required)
     * @param r  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call agenciesGetNearAsync(Double lat, Double lon, Integer r, Integer page, Integer perPage, final ApiCallback<Agency> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = agenciesGetNearCall(lat, lon, r, page, perPage, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Agency>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for agenciesGetOne */
    private com.squareup.okhttp.Call agenciesGetOneCall(Integer id, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new ApiException("Missing the required parameter 'id' when calling agenciesGetOne(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/agencies/{id}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "id" + "\\}", apiClient.escapeString(id.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();

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
     * @return Agency
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Agency agenciesGetOne(Integer id) throws ApiException {
        ApiResponse<Agency> resp = agenciesGetOneWithHttpInfo(id);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param id  (required)
     * @return ApiResponse&lt;Agency&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Agency> agenciesGetOneWithHttpInfo(Integer id) throws ApiException {
        com.squareup.okhttp.Call call = agenciesGetOneCall(id, null, null);
        Type localVarReturnType = new TypeToken<Agency>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param id  (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call agenciesGetOneAsync(Integer id, final ApiCallback<Agency> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = agenciesGetOneCall(id, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Agency>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}

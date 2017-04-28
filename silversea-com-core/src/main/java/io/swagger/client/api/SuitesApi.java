

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

import io.swagger.client.model.CategoryAvailability;
import io.swagger.client.model.SuiteAvailability;
import io.swagger.client.model.SuiteCategory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuitesApi {
    private ApiClient apiClient;

    public SuitesApi() {
        this(Configuration.getDefaultApiClient());
    }

    public SuitesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for suitesGet */
    private com.squareup.okhttp.Call suitesGetCall(String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/suites".replaceAll("\\{format\\}","json");

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
     * @param envelope  (optional)
     * @return List&lt;SuiteCategory&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<SuiteCategory> suitesGet(String envelope) throws ApiException {
        ApiResponse<List<SuiteCategory>> resp = suitesGetWithHttpInfo(envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;SuiteCategory&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<SuiteCategory>> suitesGetWithHttpInfo(String envelope) throws ApiException {
        com.squareup.okhttp.Call call = suitesGetCall(envelope, null, null);
        Type localVarReturnType = new TypeToken<List<SuiteCategory>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call suitesGetAsync(String envelope, final ApiCallback<List<SuiteCategory>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = suitesGetCall(envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<SuiteCategory>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for suitesGetCategories */
    private com.squareup.okhttp.Call suitesGetCategoriesCall(String voyageCod, String currencyCod, Integer numberGuests, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'voyageCod' is set
        if (voyageCod == null) {
            throw new ApiException("Missing the required parameter 'voyageCod' when calling suitesGetCategories(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/suites/categories_available/{voyage_cod}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "voyage_cod" + "\\}", apiClient.escapeString(voyageCod.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (currencyCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "currency_cod", currencyCod));
        if (numberGuests != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "number_guests", numberGuests));
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
     * @param voyageCod  (required)
     * @param currencyCod  (optional)
     * @param numberGuests  (optional)
     * @param envelope  (optional)
     * @return CategoryAvailability
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public CategoryAvailability suitesGetCategories(String voyageCod, String currencyCod, Integer numberGuests, String envelope) throws ApiException {
        ApiResponse<CategoryAvailability> resp = suitesGetCategoriesWithHttpInfo(voyageCod, currencyCod, numberGuests, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param voyageCod  (required)
     * @param currencyCod  (optional)
     * @param numberGuests  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;CategoryAvailability&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<CategoryAvailability> suitesGetCategoriesWithHttpInfo(String voyageCod, String currencyCod, Integer numberGuests, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = suitesGetCategoriesCall(voyageCod, currencyCod, numberGuests, envelope, null, null);
        Type localVarReturnType = new TypeToken<CategoryAvailability>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param voyageCod  (required)
     * @param currencyCod  (optional)
     * @param numberGuests  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call suitesGetCategoriesAsync(String voyageCod, String currencyCod, Integer numberGuests, String envelope, final ApiCallback<CategoryAvailability> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = suitesGetCategoriesCall(voyageCod, currencyCod, numberGuests, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<CategoryAvailability>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for suitesGetSuiteAvailable */
    private com.squareup.okhttp.Call suitesGetSuiteAvailableCall(String voyageCod, String suiteCategoryCod, String currencyCod, Integer numberGuests, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'voyageCod' is set
        if (voyageCod == null) {
            throw new ApiException("Missing the required parameter 'voyageCod' when calling suitesGetSuiteAvailable(Async)");
        }
        
        // verify the required parameter 'suiteCategoryCod' is set
        if (suiteCategoryCod == null) {
            throw new ApiException("Missing the required parameter 'suiteCategoryCod' when calling suitesGetSuiteAvailable(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/suites/suites_available/{voyage_cod}/{suite_category_cod}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "voyage_cod" + "\\}", apiClient.escapeString(voyageCod.toString()))
        .replaceAll("\\{" + "suite_category_cod" + "\\}", apiClient.escapeString(suiteCategoryCod.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (currencyCod != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "currency_cod", currencyCod));
        if (numberGuests != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "number_guests", numberGuests));
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
     * @param voyageCod  (required)
     * @param suiteCategoryCod  (required)
     * @param currencyCod  (optional)
     * @param numberGuests  (optional)
     * @param envelope  (optional)
     * @return SuiteAvailability
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public SuiteAvailability suitesGetSuiteAvailable(String voyageCod, String suiteCategoryCod, String currencyCod, Integer numberGuests, String envelope) throws ApiException {
        ApiResponse<SuiteAvailability> resp = suitesGetSuiteAvailableWithHttpInfo(voyageCod, suiteCategoryCod, currencyCod, numberGuests, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param voyageCod  (required)
     * @param suiteCategoryCod  (required)
     * @param currencyCod  (optional)
     * @param numberGuests  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;SuiteAvailability&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<SuiteAvailability> suitesGetSuiteAvailableWithHttpInfo(String voyageCod, String suiteCategoryCod, String currencyCod, Integer numberGuests, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = suitesGetSuiteAvailableCall(voyageCod, suiteCategoryCod, currencyCod, numberGuests, envelope, null, null);
        Type localVarReturnType = new TypeToken<SuiteAvailability>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param voyageCod  (required)
     * @param suiteCategoryCod  (required)
     * @param currencyCod  (optional)
     * @param numberGuests  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call suitesGetSuiteAvailableAsync(String voyageCod, String suiteCategoryCod, String currencyCod, Integer numberGuests, String envelope, final ApiCallback<SuiteAvailability> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = suitesGetSuiteAvailableCall(voyageCod, suiteCategoryCod, currencyCod, numberGuests, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<SuiteAvailability>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}

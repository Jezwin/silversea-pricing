

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

import io.swagger.client.model.VoyagePrice;
import io.swagger.client.model.VoyagePriceComplete;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PricesApi {
    private ApiClient apiClient;

    public PricesApi() {
        this(Configuration.getDefaultApiClient());
    }

    public PricesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for pricesGet */
    private com.squareup.okhttp.Call pricesGetCall(String currencyCod, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'currencyCod' is set
        if (currencyCod == null) {
            throw new ApiException("Missing the required parameter 'currencyCod' when calling pricesGet(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/prices/{currency_cod}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "currency_cod" + "\\}", apiClient.escapeString(currencyCod.toString()));

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
     * @param currencyCod  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return List&lt;VoyagePrice&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<VoyagePrice> pricesGet(String currencyCod, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<VoyagePrice>> resp = pricesGetWithHttpInfo(currencyCod, page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param currencyCod  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;VoyagePrice&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<VoyagePrice>> pricesGetWithHttpInfo(String currencyCod, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = pricesGetCall(currencyCod, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<VoyagePrice>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param currencyCod  (required)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call pricesGetAsync(String currencyCod, Integer page, Integer perPage, String envelope, final ApiCallback<List<VoyagePrice>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = pricesGetCall(currencyCod, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<VoyagePrice>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for pricesGet2 */
    private com.squareup.okhttp.Call pricesGet2Call(Integer voyageId, String currencyCod, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'voyageId' is set
        if (voyageId == null) {
            throw new ApiException("Missing the required parameter 'voyageId' when calling pricesGet2(Async)");
        }
        
        // verify the required parameter 'currencyCod' is set
        if (currencyCod == null) {
            throw new ApiException("Missing the required parameter 'currencyCod' when calling pricesGet2(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v1/prices/{voyage_id}/{currency_cod}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "voyage_id" + "\\}", apiClient.escapeString(voyageId.toString()))
        .replaceAll("\\{" + "currency_cod" + "\\}", apiClient.escapeString(currencyCod.toString()));

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
     * @param voyageId  (required)
     * @param currencyCod  (required)
     * @param envelope  (optional)
     * @return VoyagePrice
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public VoyagePrice pricesGet2(Integer voyageId, String currencyCod, String envelope) throws ApiException {
        ApiResponse<VoyagePrice> resp = pricesGet2WithHttpInfo(voyageId, currencyCod, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param voyageId  (required)
     * @param currencyCod  (required)
     * @param envelope  (optional)
     * @return ApiResponse&lt;VoyagePrice&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<VoyagePrice> pricesGet2WithHttpInfo(Integer voyageId, String currencyCod, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = pricesGet2Call(voyageId, currencyCod, envelope, null, null);
        Type localVarReturnType = new TypeToken<VoyagePrice>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param voyageId  (required)
     * @param currencyCod  (required)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call pricesGet2Async(Integer voyageId, String currencyCod, String envelope, final ApiCallback<VoyagePrice> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = pricesGet2Call(voyageId, currencyCod, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<VoyagePrice>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for pricesGet3 */
    private com.squareup.okhttp.Call pricesGet3Call(Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/prices".replaceAll("\\{format\\}","json");

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
    
    private com.squareup.okhttp.Call pricesMultiGet3Call(Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/prices".replaceAll("\\{format\\}","json");

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (page != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "page", page));
        if (perPage != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "per_page", perPage));
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
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return List&lt;VoyagePriceComplete&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<VoyagePriceComplete> pricesGet3(Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<VoyagePriceComplete>> resp = pricesGet3WithHttpInfo(page, perPage, envelope);
        return resp.getData();
    }
    
    public List<VoyagePriceComplete> pricesMultiGet3(Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<VoyagePriceComplete>> resp = pricesMultiGet3WithHttpInfo(page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;VoyagePriceComplete&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<VoyagePriceComplete>> pricesGet3WithHttpInfo(Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = pricesGet3Call(page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<VoyagePriceComplete>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }
    
    public ApiResponse<List<VoyagePriceComplete>> pricesMultiGet3WithHttpInfo(Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = pricesMultiGet3Call(page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<VoyagePriceComplete>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call pricesGet3Async(Integer page, Integer perPage, String envelope, final ApiCallback<List<VoyagePriceComplete>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = pricesGet3Call(page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<VoyagePriceComplete>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}

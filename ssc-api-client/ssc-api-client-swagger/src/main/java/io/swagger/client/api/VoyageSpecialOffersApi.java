

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

import io.swagger.client.model.VoyageSpecialOffer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoyageSpecialOffersApi {
    private ApiClient apiClient;

    public VoyageSpecialOffersApi() {
        this(Configuration.getDefaultApiClient());
    }

    public VoyageSpecialOffersApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for voyageSpecialOffersGet */
    private com.squareup.okhttp.Call voyageSpecialOffersGetCall(String countryIso3, Integer voyageId, Integer page, Integer perPage, String envelope, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v1/voyageSpecialOffers".replaceAll("\\{format\\}","json");

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (countryIso3 != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "country_iso3", countryIso3));
        if (voyageId != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "voyage_id", voyageId));
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
     * @param countryIso3  (optional)
     * @param voyageId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return List&lt;VoyageSpecialOffer&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<VoyageSpecialOffer> voyageSpecialOffersGet(String countryIso3, Integer voyageId, Integer page, Integer perPage, String envelope) throws ApiException {
        ApiResponse<List<VoyageSpecialOffer>> resp = voyageSpecialOffersGetWithHttpInfo(countryIso3, voyageId, page, perPage, envelope);
        return resp.getData();
    }

    /**
     * 
     * 
     * @param countryIso3  (optional)
     * @param voyageId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @return ApiResponse&lt;List&lt;VoyageSpecialOffer&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<VoyageSpecialOffer>> voyageSpecialOffersGetWithHttpInfo(String countryIso3, Integer voyageId, Integer page, Integer perPage, String envelope) throws ApiException {
        com.squareup.okhttp.Call call = voyageSpecialOffersGetCall(countryIso3, voyageId, page, perPage, envelope, null, null);
        Type localVarReturnType = new TypeToken<List<VoyageSpecialOffer>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * 
     * @param countryIso3  (optional)
     * @param voyageId  (optional)
     * @param page  (optional)
     * @param perPage  (optional)
     * @param envelope  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call voyageSpecialOffersGetAsync(String countryIso3, Integer voyageId, Integer page, Integer perPage, String envelope, final ApiCallback<List<VoyageSpecialOffer>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = voyageSpecialOffersGetCall(countryIso3, voyageId, page, perPage, envelope, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<VoyageSpecialOffer>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}

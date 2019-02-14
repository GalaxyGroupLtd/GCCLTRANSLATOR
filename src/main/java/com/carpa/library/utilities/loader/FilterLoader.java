package com.carpa.library.utilities.loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.carpa.library.client.ServiceGen;
import com.carpa.library.client.Services;
import com.carpa.library.config.ErrorCodeConfig;
import com.carpa.library.models.ApiResponse;
import com.carpa.library.models.ErrorModel;
import com.carpa.library.models.ErrorsListModel;
import com.carpa.library.utilities.DataFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterLoader {
    private OnFilterLoader mListener;
    private ApiResponse responseBody = null;
    private String cmd;
    private String countryCode;
    private String language;
    private Context context;

    public FilterLoader(Context context, OnFilterLoader mListener, String cmd, String countryCode, String language) {
        this.context = context;
        this.mListener = mListener;
        this.cmd = cmd;
        this.countryCode = countryCode;
        this.language = language;
    }

    public void start() {
        try {
            //Log.d(cmd + "_REQUEST", DataFactory.objectToString(request));
            if(!haveNetworkConnection()){
                List<ErrorModel> error = Arrays.asList(new ErrorModel(ErrorCodeConfig.EXTERNAL_API_ERROR[0], ErrorCodeConfig.EXTERNAL_API_ERROR[1], "Not connected.", "Not connected."));
                responseBody = new ApiResponse(0, DataFactory.errorObject(new ErrorsListModel(error)));
                throw new Exception("Not connected");
            }
            Services services = ServiceGen.createService(Services.class, Services.BASE_URL);
            Call<ResponseBody> callService = services.queryService(countryCode, language, cmd);
            callService.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    int statusCode = response.code();
                    String received = null;
                    try {
                        received = response.body().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                        List<ErrorModel> error = Arrays.asList(new ErrorModel(ErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], ErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], e.getMessage(), statusCode + " failed to decode remote data."));
                        onPostExecute(new ApiResponse(statusCode, DataFactory.errorObject(new ErrorsListModel(error))));
                    }
                    Log.d(cmd + "_RESP", "(" + statusCode + ")" + received);
                    if (statusCode == 200) {
                        onPostExecute(new ApiResponse(statusCode, received));
                    } else {
                        try {
                            onPostExecute(new ApiResponse(statusCode, response.errorBody().string()));
                        } catch (IOException e) {
                            List<ErrorModel> error = Arrays.asList(new ErrorModel(ErrorCodeConfig.EXTERNAL_API_ERROR[0], ErrorCodeConfig.EXTERNAL_API_ERROR[1], "We couldn't process the data for the moment.", statusCode + " remote service failed."));
                            onPostExecute(new ApiResponse(statusCode, DataFactory.errorObject(new ErrorsListModel(error))));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable e) {
                    e.printStackTrace();
                    List<ErrorModel> error = Arrays.asList(new ErrorModel(ErrorCodeConfig.EXTERNAL_API_ERROR[0], ErrorCodeConfig.EXTERNAL_API_ERROR[1], "There is a problem with network.", "Network failure"));
                    onPostExecute(new ApiResponse(0, DataFactory.errorObject(new ErrorsListModel(error))));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            onPostExecute(responseBody);
        }
    }

    private void onPostExecute(ApiResponse response) {
        if (mListener != null)
            if (response == null) {
                mListener.onFilterLoader(false, "Internal processing error.");
            } else {
                if (response.getStatus() != 200) {
                    try {
                        ErrorsListModel errorsListModel = new ErrorsListModel().serialize(response.getReceived().toString());
                        StringBuilder message = new StringBuilder();
                        for (ErrorModel errorModel : errorsListModel.getErrors()) {
                            message.append(errorModel.getTitle()).append(": ").append(errorModel.getDescription()).append("\n");
                        }
                        mListener.onFilterLoader(false, message.toString());
                    } catch (Exception e) {
                        mListener.onFilterLoader(false, "There was an error from external resources.");
                    }
                }else if(response.getReceived() == null){
                    mListener.onFilterLoader(false, "No content found.");
                } else {
                    mListener.onFilterLoader(true, response.getReceived());

                }
            }
    }

    private boolean haveNetworkConnection() throws Exception{
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public interface OnFilterLoader {
        void onFilterLoader(boolean isLoaded, Object response);
    }
}
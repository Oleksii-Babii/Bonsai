package org.tudublin.bonsaiapp.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://bonsaiapi-ewe7gdd0hfd8a8dv.westeurope-01.azurewebsites.net";

    private static Retrofit retrofit;
    private static BonsaiApiService service;

    private static Retrofit getInstance() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static BonsaiApiService getService() {
        if (service == null) {
            service = getInstance().create(BonsaiApiService.class);
        }
        return service;
    }
}

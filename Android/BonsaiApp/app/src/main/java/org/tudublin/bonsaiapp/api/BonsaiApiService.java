package org.tudublin.bonsaiapp.api;

import org.tudublin.bonsaiapp.model.Species;
import org.tudublin.bonsaiapp.model.Tree;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BonsaiApiService {

    @GET("/api/species")
    Call<List<Species>> getAllSpecies();

    @GET("/api/species/{id}")
    Call<Species> getSpecies(@Path("id") int id);

    @GET("/api/trees")
    Call<List<Tree>> getAllTrees();

    @GET("/api/trees/{id}")
    Call<Tree> getTree(@Path("id") int id);

    @GET("/api/trees/search")
    Call<List<Tree>> searchTrees(@Query("name") String name);

    @POST("/api/trees")
    Call<Tree> createTree(@Body Tree tree);

    @PUT("/api/trees/{id}")
    Call<Void> updateTree(@Path("id") int id, @Body Tree tree);

    @DELETE("/api/trees/{id}")
    Call<Void> deleteTree(@Path("id") int id);
}

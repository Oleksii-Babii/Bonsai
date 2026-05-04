package org.tudublin.bonsaiapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.tudublin.bonsaiapp.api.BonsaiApiService;
import org.tudublin.bonsaiapp.api.RetrofitClient;
import org.tudublin.bonsaiapp.databinding.ActivityMainBinding;
import org.tudublin.bonsaiapp.model.Species;
import org.tudublin.bonsaiapp.util.DifficultyUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BonsaiApp";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.btnViewSpecies.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SpeciesListActivity.class);
            startActivity(intent);
        });

        binding.btnViewTrees.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TreeListActivity.class);
            startActivity(intent);
        });

        loadFeaturedSpecies();
    }

    private void loadFeaturedSpecies() {
        BonsaiApiService service = RetrofitClient.getService();
        service.getAllSpecies().enqueue(new Callback<List<Species>>() {
            @Override
            public void onResponse(Call<List<Species>> call, Response<List<Species>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Species featured = response.body().get(0);
                    binding.textFeaturedName.setText(featured.getName());
                    binding.textFeaturedOrigin.setText(featured.getOriginCountry());
                    binding.textFeaturedDifficulty.setText(featured.getDifficultyLevel());
                    DifficultyUtils.applyTo(binding.textFeaturedDifficulty, featured.getDifficultyLevel());
                    if (featured.getImageUrl() != null && !featured.getImageUrl().isEmpty()) {
                        Glide.with(MainActivity.this)
                                .load(featured.getImageUrl())
                                .placeholder(R.drawable.ic_tree_placeholder)
                                .centerCrop()
                                .into(binding.imageHome);
                    }
                    Log.d(TAG, "Loaded featured species: " + featured.getName());
                }
            }

            @Override
            public void onFailure(Call<List<Species>> call, Throwable t) {
                binding.textFeaturedName.setText(R.string.error_loading);
                Log.e(TAG, "Failed to load featured species: " + t.getMessage());
            }
        });
    }

}

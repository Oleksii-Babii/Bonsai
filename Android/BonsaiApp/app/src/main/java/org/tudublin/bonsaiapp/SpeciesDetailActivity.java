package org.tudublin.bonsaiapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.tudublin.bonsaiapp.api.BonsaiApiService;
import org.tudublin.bonsaiapp.api.RetrofitClient;
import org.tudublin.bonsaiapp.databinding.ActivitySpeciesDetailBinding;
import org.tudublin.bonsaiapp.model.Species;
import org.tudublin.bonsaiapp.util.DifficultyUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeciesDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SPECIES_ID = "org.tudublin.bonsaiapp.SPECIES_ID";
    private static final String TAG = "BonsaiApp";
    private ActivitySpeciesDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpeciesDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        int speciesId = getIntent().getIntExtra(EXTRA_SPECIES_ID, -1);
        if (speciesId == -1) {
            finish();
            return;
        }

        loadSpeciesDetail(speciesId);
    }

    private void loadSpeciesDetail(int id) {
        binding.progressBar.setVisibility(View.VISIBLE);

        BonsaiApiService service = RetrofitClient.getService();
        service.getSpecies(id).enqueue(new Callback<Species>() {
            @Override
            public void onResponse(Call<Species> call, Response<Species> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Species species = response.body();
                    binding.textName.setText(species.getName());
                    binding.textOrigin.setText(getString(R.string.label_origin) + " " + species.getOriginCountry());
                    binding.textDifficulty.setText(species.getDifficultyLevel());
                    DifficultyUtils.applyTo(binding.textDifficulty, species.getDifficultyLevel());
                    binding.textDescription.setText(species.getDescription());

                    if (species.getImageUrl() != null && !species.getImageUrl().isEmpty()) {
                        Glide.with(SpeciesDetailActivity.this)
                                .load(species.getImageUrl())
                                .placeholder(R.drawable.ic_tree_placeholder)
                                .into(binding.imageSpecies);
                    }

                    Log.d(TAG, "Loaded species detail: " + species.getName());
                }
            }

            @Override
            public void onFailure(Call<Species> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(SpeciesDetailActivity.this, getString(R.string.error_loading), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading species detail: " + t.getMessage());
            }
        });
    }

}

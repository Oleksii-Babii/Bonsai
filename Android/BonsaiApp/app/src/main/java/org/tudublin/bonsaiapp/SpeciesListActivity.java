package org.tudublin.bonsaiapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.tudublin.bonsaiapp.adapter.SpeciesAdapter;
import org.tudublin.bonsaiapp.api.BonsaiApiService;
import org.tudublin.bonsaiapp.api.RetrofitClient;
import org.tudublin.bonsaiapp.databinding.ActivitySpeciesListBinding;
import org.tudublin.bonsaiapp.model.Species;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeciesListActivity extends AppCompatActivity {

    private static final String TAG = "BonsaiApp";
    private ActivitySpeciesListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpeciesListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.recyclerSpecies.setLayoutManager(new LinearLayoutManager(this));

        loadSpecies();
    }

    private void loadSpecies() {
        binding.progressBar.setVisibility(View.VISIBLE);

        BonsaiApiService service = RetrofitClient.getService();
        service.getAllSpecies().enqueue(new Callback<List<Species>>() {
            @Override
            public void onResponse(Call<List<Species>> call, Response<List<Species>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Species> list = response.body();
                    binding.emptyView.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                    SpeciesAdapter adapter = new SpeciesAdapter(list, species -> {
                        Intent intent = new Intent(SpeciesListActivity.this, SpeciesDetailActivity.class);
                        intent.putExtra(SpeciesDetailActivity.EXTRA_SPECIES_ID, species.getId());
                        startActivity(intent);
                    });
                    binding.recyclerSpecies.setAdapter(adapter);
                    Log.d(TAG, "Loaded " + response.body().size() + " species");
                }
            }

            @Override
            public void onFailure(Call<List<Species>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(SpeciesListActivity.this, getString(R.string.error_loading), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading species: " + t.getMessage());
            }
        });
    }
}

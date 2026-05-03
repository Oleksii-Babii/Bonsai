package org.tudublin.bonsaiapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.tudublin.bonsaiapp.adapter.TreeAdapter;
import org.tudublin.bonsaiapp.api.BonsaiApiService;
import org.tudublin.bonsaiapp.api.RetrofitClient;
import org.tudublin.bonsaiapp.databinding.ActivityTreeListBinding;
import org.tudublin.bonsaiapp.model.Tree;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TreeListActivity extends AppCompatActivity {

    private static final String TAG = "BonsaiApp";
    private ActivityTreeListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTreeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.recyclerTrees.setLayoutManager(new LinearLayoutManager(this));

        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadAllTrees();
                } else {
                    searchTrees(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        FloatingActionButton fab = binding.fabAddTree;
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(TreeListActivity.this, AddEditTreeActivity.class);
            startActivity(intent);
        });

        loadAllTrees();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllTrees();
    }

    private void loadAllTrees() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.GONE);

        BonsaiApiService service = RetrofitClient.getService();
        service.getAllTrees().enqueue(new Callback<List<Tree>>() {
            @Override
            public void onResponse(Call<List<Tree>> call, Response<List<Tree>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    displayTrees(response.body());
                    Log.d(TAG, "Loaded " + response.body().size() + " trees");
                } else {
                    String err = "HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) err += ": " + response.errorBody().string();
                    } catch (Exception ignored) {}
                    binding.emptyView.setText(err);
                    binding.emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(TreeListActivity.this, err, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Load trees failed: " + err);
                }
            }

            @Override
            public void onFailure(Call<List<Tree>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = "Network error: " + t.getMessage();
                binding.emptyView.setText(msg);
                binding.emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(TreeListActivity.this, msg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading trees: " + t.getMessage(), t);
            }
        });
    }

    private void searchTrees(String query) {
        BonsaiApiService service = RetrofitClient.getService();
        service.searchTrees(query).enqueue(new Callback<List<Tree>>() {
            @Override
            public void onResponse(Call<List<Tree>> call, Response<List<Tree>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayTrees(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Tree>> call, Throwable t) {
                Log.e(TAG, "Search error: " + t.getMessage());
            }
        });
    }

    private void displayTrees(List<Tree> trees) {
        binding.emptyView.setVisibility(trees.isEmpty() ? View.VISIBLE : View.GONE);
        TreeAdapter adapter = new TreeAdapter(trees, tree -> {
            Intent intent = new Intent(TreeListActivity.this, TreeDetailActivity.class);
            intent.putExtra(TreeDetailActivity.EXTRA_TREE_ID, tree.getId());
            startActivity(intent);
        });
        binding.recyclerTrees.setAdapter(adapter);
    }
}

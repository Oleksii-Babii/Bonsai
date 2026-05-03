package org.tudublin.bonsaiapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tudublin.bonsaiapp.api.BonsaiApiService;
import org.tudublin.bonsaiapp.api.RetrofitClient;
import org.tudublin.bonsaiapp.databinding.ActivityAddEditTreeBinding;
import org.tudublin.bonsaiapp.model.Species;
import org.tudublin.bonsaiapp.model.Tree;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditTreeActivity extends AppCompatActivity {

    public static final String EXTRA_TREE_ID = "org.tudublin.bonsaiapp.EDIT_TREE_ID";
    private static final String TAG = "BonsaiApp";
    private ActivityAddEditTreeBinding binding;
    private List<Species> speciesList = new ArrayList<>();
    private int editTreeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditTreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        editTreeId = getIntent().getIntExtra(EXTRA_TREE_ID, -1);
        if (editTreeId != -1) {
            binding.toolbar.setTitle(R.string.label_edit_tree);
            loadExistingTree(editTreeId);
        } else {
            binding.toolbar.setTitle(R.string.label_add_tree);
        }

        loadSpeciesForSpinner();

        binding.btnSave.setOnClickListener(v -> saveTree());
    }

    private void loadSpeciesForSpinner() {
        BonsaiApiService service = RetrofitClient.getService();
        service.getAllSpecies().enqueue(new Callback<List<Species>>() {
            @Override
            public void onResponse(Call<List<Species>> call, Response<List<Species>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    speciesList = response.body();
                    List<String> names = new ArrayList<>();
                    for (Species s : speciesList) {
                        names.add(s.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddEditTreeActivity.this,
                            android.R.layout.simple_spinner_item,
                            names
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSpecies.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Species>> call, Throwable t) {
                Log.e(TAG, "Failed to load species: " + t.getMessage());
            }
        });
    }

    private void loadExistingTree(int id) {
        BonsaiApiService service = RetrofitClient.getService();
        service.getTree(id).enqueue(new Callback<Tree>() {
            @Override
            public void onResponse(Call<Tree> call, Response<Tree> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Tree tree = response.body();
                    binding.editNickname.setText(tree.getNickname());
                    binding.editAge.setText(String.valueOf(tree.getAge()));
                    binding.editHeight.setText(String.valueOf(tree.getHeight()));
                    if (tree.getNotes() != null) {
                        binding.editNotes.setText(tree.getNotes());
                    }
                    for (int i = 0; i < speciesList.size(); i++) {
                        if (speciesList.get(i).getId() == tree.getSpeciesId()) {
                            binding.spinnerSpecies.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Tree> call, Throwable t) {
                Log.e(TAG, "Failed to load tree for editing: " + t.getMessage());
            }
        });
    }

    private void saveTree() {
        String nickname = binding.editNickname.getText().toString().trim();
        String ageText = binding.editAge.getText().toString().trim();
        String heightText = binding.editHeight.getText().toString().trim();
        String notes = binding.editNotes.getText().toString().trim();

        if (nickname.isEmpty() || ageText.isEmpty() || heightText.isEmpty()) {
            Toast.makeText(this, R.string.error_fill_required, Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedSpeciesIndex = binding.spinnerSpecies.getSelectedItemPosition();
        if (speciesList.isEmpty() || selectedSpeciesIndex < 0) {
            Toast.makeText(this, R.string.error_select_species, Toast.LENGTH_SHORT).show();
            return;
        }

        Tree tree = new Tree();
        tree.setNickname(nickname);
        tree.setAge(Integer.parseInt(ageText));
        tree.setHeight(Double.parseDouble(heightText));
        tree.setNotes(notes.isEmpty() ? null : notes);
        tree.setLastWateredDate(LocalDate.now().toString() + "T00:00:00");
        tree.setSpeciesId(speciesList.get(selectedSpeciesIndex).getId());

        BonsaiApiService service = RetrofitClient.getService();

        if (editTreeId == -1) {
            service.createTree(tree).enqueue(new Callback<Tree>() {
                @Override
                public void onResponse(Call<Tree> call, Response<Tree> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEditTreeActivity.this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String err = "HTTP " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                err += ": " + response.errorBody().string();
                            }
                        } catch (Exception ignored) {}
                        Toast.makeText(AddEditTreeActivity.this, err, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Create failed: " + err);
                    }
                }

                @Override
                public void onFailure(Call<Tree> call, Throwable t) {
                    Toast.makeText(AddEditTreeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to create tree: " + t.getMessage());
                }
            });
        } else {
            tree.setId(editTreeId);
            service.updateTree(editTreeId, tree).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEditTreeActivity.this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String err = "HTTP " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                err += ": " + response.errorBody().string();
                            }
                        } catch (Exception ignored) {}
                        Toast.makeText(AddEditTreeActivity.this, err, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Update failed: " + err);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddEditTreeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to update tree: " + t.getMessage());
                }
            });
        }
    }
}

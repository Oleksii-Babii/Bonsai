package org.tudublin.bonsaiapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.tudublin.bonsaiapp.api.BonsaiApiService;
import org.tudublin.bonsaiapp.api.RetrofitClient;
import org.tudublin.bonsaiapp.databinding.ActivityTreeDetailBinding;
import org.tudublin.bonsaiapp.model.Tree;
import org.tudublin.bonsaiapp.util.ImageUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TreeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TREE_ID = "org.tudublin.bonsaiapp.TREE_ID";
    private static final String TAG = "BonsaiApp";
    private ActivityTreeDetailBinding binding;
    private int treeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTreeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        treeId = getIntent().getIntExtra(EXTRA_TREE_ID, -1);
        if (treeId == -1) {
            finish();
            return;
        }

        loadTreeDetail(treeId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (treeId != -1) {
            loadTreeDetail(treeId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tree_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, AddEditTreeActivity.class);
            intent.putExtra(AddEditTreeActivity.EXTRA_TREE_ID, treeId);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            confirmDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTreeDetail(int id) {
        binding.progressBar.setVisibility(View.VISIBLE);

        BonsaiApiService service = RetrofitClient.getService();
        service.getTree(id).enqueue(new Callback<Tree>() {
            @Override
            public void onResponse(Call<Tree> call, Response<Tree> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Tree tree = response.body();
                    binding.textNickname.setText(tree.getNickname());
                    binding.textAge.setText(getString(R.string.label_age) + " " + tree.getAge());
                    binding.textHeight.setText(getString(R.string.label_height) + " " + tree.getHeight() + " cm");
                    binding.textLastWatered.setText(getString(R.string.label_last_watered) + " " + formatDate(tree.getLastWateredDate()));
                    binding.textNotes.setText(tree.getNotes() != null ? tree.getNotes() : "");
                    if (tree.getSpecies() != null) {
                        binding.textSpecies.setText(getString(R.string.label_species) + " " + tree.getSpecies().getName());
                    }

                    ImageUtils.loadTreeImage(binding.imageTree, tree);

                    Log.d(TAG, "Loaded tree: " + tree.getNickname());
                }
            }

            @Override
            public void onFailure(Call<Tree> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(TreeDetailActivity.this, getString(R.string.error_loading), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading tree: " + t.getMessage());
            }
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> deleteTree())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void deleteTree() {
        BonsaiApiService service = RetrofitClient.getService();
        service.deleteTree(treeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TreeDetailActivity.this, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TreeDetailActivity.this, getString(R.string.error_loading), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error deleting tree: " + t.getMessage());
            }
        });
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        return isoDate.length() >= 10 ? isoDate.substring(0, 10) : isoDate;
    }
}

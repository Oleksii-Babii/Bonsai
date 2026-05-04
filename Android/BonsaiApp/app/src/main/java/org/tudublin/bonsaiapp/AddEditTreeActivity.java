package org.tudublin.bonsaiapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.tudublin.bonsaiapp.api.BonsaiApiService;
import org.tudublin.bonsaiapp.api.RetrofitClient;
import org.tudublin.bonsaiapp.databinding.ActivityAddEditTreeBinding;
import org.tudublin.bonsaiapp.model.Species;
import org.tudublin.bonsaiapp.model.Tree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditTreeActivity extends AppCompatActivity {

    public static final String EXTRA_TREE_ID = "org.tudublin.bonsaiapp.EDIT_TREE_ID";
    private static final String TAG = "BonsaiApp";
    private static final int MAX_IMAGE_PX = 800;
    private static final int JPEG_QUALITY = 80;

    private ActivityAddEditTreeBinding binding;
    private List<Species> speciesList = new ArrayList<>();
    private int editTreeId = -1;
    private String pendingImageData = null;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<String> pickPhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;

                // Disable button immediately so the user knows work is happening
                binding.btnPickPhoto.setEnabled(false);

                // compressPhoto() does disk I/O + bitmap decode/encode — move off main thread
                executor.execute(() -> {
                    try {
                        byte[] bytes = compressPhoto(uri);
                        String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);

                        // Post UI updates back to the main thread
                        mainHandler.post(() -> {
                            if (isDestroyed()) return;
                            pendingImageData = encoded;
                            Glide.with(AddEditTreeActivity.this)
                                    .load(bytes)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .centerCrop()
                                    .into(binding.imagePreview);
                            binding.btnPickPhoto.setText(R.string.btn_change_photo);
                            binding.btnPickPhoto.setEnabled(true);
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Photo processing error", e);
                        mainHandler.post(() -> {
                            if (isDestroyed()) return;
                            binding.btnPickPhoto.setEnabled(true);
                            Toast.makeText(AddEditTreeActivity.this,
                                    R.string.error_photo_processing, Toast.LENGTH_LONG).show();
                        });
                    }
                });
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditTreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        editTreeId = getIntent().getIntExtra(EXTRA_TREE_ID, -1);
        if (editTreeId != -1) {
            binding.toolbar.setTitle(R.string.label_edit_tree);
            loadExistingTree(editTreeId);
        } else {
            binding.toolbar.setTitle(R.string.label_add_tree);
        }

        loadSpeciesForSpinner();
        binding.btnPickPhoto.setOnClickListener(v -> pickPhotoLauncher.launch("image/*"));
        binding.btnSave.setOnClickListener(v -> {
            binding.btnSave.setEnabled(false);
            binding.btnSave.setText(R.string.btn_saving);
            saveTree();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

    private void loadSpeciesForSpinner() {
        BonsaiApiService service = RetrofitClient.getService();
        service.getAllSpecies().enqueue(new Callback<List<Species>>() {
            @Override
            public void onResponse(Call<List<Species>> call, Response<List<Species>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    speciesList = response.body();
                    List<String> names = new ArrayList<>();
                    for (Species s : speciesList) names.add(s.getName());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddEditTreeActivity.this,
                            android.R.layout.simple_spinner_item, names);
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
        RetrofitClient.getService().getTree(id).enqueue(new Callback<Tree>() {
            @Override
            public void onResponse(Call<Tree> call, Response<Tree> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Tree tree = response.body();
                    binding.editNickname.setText(tree.getNickname());
                    binding.editAge.setText(String.valueOf(tree.getAge()));
                    binding.editHeight.setText(String.valueOf(tree.getHeight()));
                    if (tree.getNotes() != null) binding.editNotes.setText(tree.getNotes());

                    if (tree.getImageData() != null && !tree.getImageData().isEmpty()) {
                        pendingImageData = tree.getImageData();
                        try {
                            byte[] bytes = Base64.decode(tree.getImageData(), Base64.DEFAULT);
                            Glide.with(AddEditTreeActivity.this).load(bytes)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .centerCrop().into(binding.imagePreview);
                            binding.btnPickPhoto.setText(R.string.btn_change_photo);
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Invalid base64 image data for tree " + id, e);
                            pendingImageData = null;
                        }
                    }

                    int selectedIndex = -1;
                    for (int i = 0; i < speciesList.size(); i++) {
                        if (speciesList.get(i).getId() == tree.getSpeciesId()) {
                            selectedIndex = i;
                            break;
                        }
                    }
                    if (selectedIndex >= 0) {
                        binding.spinnerSpecies.setSelection(selectedIndex);
                    } else {
                        Log.w(TAG, "Species id " + tree.getSpeciesId() + " not found in list — may not have loaded yet");
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
            binding.btnSave.setEnabled(true);
            binding.btnSave.setText(R.string.btn_save);
            Toast.makeText(this, R.string.error_fill_required, Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedSpeciesIndex = binding.spinnerSpecies.getSelectedItemPosition();
        if (speciesList.isEmpty() || selectedSpeciesIndex < 0) {
            binding.btnSave.setEnabled(true);
            binding.btnSave.setText(R.string.btn_save);
            Toast.makeText(this, R.string.error_select_species, Toast.LENGTH_SHORT).show();
            return;
        }

        Tree tree = new Tree();
        tree.setNickname(nickname);
        int age;
        double height;
        try {
            age = Integer.parseInt(ageText);
            height = Double.parseDouble(heightText);
        } catch (NumberFormatException e) {
            binding.btnSave.setEnabled(true);
            binding.btnSave.setText(R.string.btn_save);
            Toast.makeText(this, R.string.error_invalid_numbers, Toast.LENGTH_SHORT).show();
            return;
        }
        tree.setAge(age);
        tree.setHeight(height);
        tree.setNotes(notes.isEmpty() ? null : notes);
        tree.setLastWateredDate(LocalDate.now().toString() + "T00:00:00");
        tree.setSpeciesId(speciesList.get(selectedSpeciesIndex).getId());
        if (pendingImageData != null && !pendingImageData.isEmpty()) tree.setImageData(pendingImageData);

        BonsaiApiService service = RetrofitClient.getService();

        if (editTreeId == -1) {
            service.createTree(tree).enqueue(new Callback<Tree>() {
                @Override
                public void onResponse(Call<Tree> call, Response<Tree> response) {
                    if (response.isSuccessful()) {
                        completeSaveSuccess();
                    } else {
                        binding.btnSave.setEnabled(true);
                        binding.btnSave.setText(R.string.btn_save);
                        showError("Create failed", response);
                    }
                }

                @Override
                public void onFailure(Call<Tree> call, Throwable t) {
                    binding.btnSave.setEnabled(true);
                    binding.btnSave.setText(R.string.btn_save);
                    Toast.makeText(AddEditTreeActivity.this, getString(R.string.error_network, t.getMessage()), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            tree.setId(editTreeId);
            service.updateTree(editTreeId, tree).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        completeSaveSuccess();
                    } else {
                        binding.btnSave.setEnabled(true);
                        binding.btnSave.setText(R.string.btn_save);
                        showError("Update failed", response);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    binding.btnSave.setEnabled(true);
                    binding.btnSave.setText(R.string.btn_save);
                    Toast.makeText(AddEditTreeActivity.this, getString(R.string.error_network, t.getMessage()), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private byte[] compressPhoto(Uri uri) throws Exception {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(is, null, opts);
        }

        int scale = 1;
        while (opts.outWidth / scale > MAX_IMAGE_PX || opts.outHeight / scale > MAX_IMAGE_PX) {
            scale *= 2;
        }

        BitmapFactory.Options decodeOpts = new BitmapFactory.Options();
        decodeOpts.inSampleSize = scale;
        Bitmap bmp;
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            bmp = BitmapFactory.decodeStream(is, null, decodeOpts);
        }

        if (bmp == null) {
            throw new IllegalStateException("Unable to decode selected image");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out);
        bmp.recycle();
        return out.toByteArray();
    }

    private void completeSaveSuccess() {
        runOnUiThread(() -> {
            binding.btnSave.setEnabled(true);
            binding.btnSave.setText(R.string.btn_save);
            Toast.makeText(AddEditTreeActivity.this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showError(String prefix, Response<?> response) {
        String err = prefix + ": HTTP " + response.code();
        try { if (response.errorBody() != null) err += " " + response.errorBody().string(); }
        catch (IOException e) { Log.w(TAG, "Could not read error body", e); }
        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        Log.e(TAG, err);
    }
}

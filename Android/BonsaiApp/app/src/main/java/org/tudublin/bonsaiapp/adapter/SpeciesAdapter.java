package org.tudublin.bonsaiapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.tudublin.bonsaiapp.R;
import org.tudublin.bonsaiapp.model.Species;
import org.tudublin.bonsaiapp.util.DifficultyUtils;

import java.util.ArrayList;
import java.util.List;

public class SpeciesAdapter extends RecyclerView.Adapter<SpeciesAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Species species);
    }

    private List<Species> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public SpeciesAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Species> newItems) {
        final List<Species> oldItems = items;
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return oldItems.size(); }
            @Override public int getNewListSize() { return newItems.size(); }
            @Override public boolean areItemsTheSame(int oldPos, int newPos) {
                return oldItems.get(oldPos).getId() == newItems.get(newPos).getId();
            }
            @Override public boolean areContentsTheSame(int oldPos, int newPos) {
                return oldItems.get(oldPos).equals(newItems.get(newPos));
            }
        });
        items = new ArrayList<>(newItems);
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_species, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Species species = items.get(position);
        holder.textName.setText(species.getName());
        holder.textOrigin.setText(species.getOriginCountry());
        holder.textDifficulty.setText(species.getDifficultyLevel());
        DifficultyUtils.applyTo(holder.textDifficulty, species.getDifficultyLevel());

        if (species.getImageUrl() != null && !species.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(species.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_tree_placeholder)
                    .centerCrop()
                    .into(holder.imageSpecies);
        } else {
            holder.imageSpecies.setImageResource(R.drawable.ic_tree_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(species));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textOrigin;
        TextView textDifficulty;
        ImageView imageSpecies;

        ViewHolder(View view) {
            super(view);
            textName = view.findViewById(R.id.textSpeciesName);
            textOrigin = view.findViewById(R.id.textSpeciesOrigin);
            textDifficulty = view.findViewById(R.id.textSpeciesDifficulty);
            imageSpecies = view.findViewById(R.id.imageSpecies);
        }
    }
}

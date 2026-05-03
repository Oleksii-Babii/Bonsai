package org.tudublin.bonsaiapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.tudublin.bonsaiapp.R;
import org.tudublin.bonsaiapp.model.Tree;

import java.util.ArrayList;
import java.util.List;

public class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Tree tree);
    }

    private List<Tree> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public TreeAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Tree> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tree, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tree tree = items.get(position);
        holder.textNickname.setText(tree.getNickname());
        holder.textAge.setText(tree.getAge() + " yrs");
        holder.textSpecies.setText(tree.getSpecies() != null ? tree.getSpecies().getName() : "");

        if (tree.getImageData() != null && !tree.getImageData().isEmpty()) {
            byte[] bytes = Base64.decode(tree.getImageData(), Base64.DEFAULT);
            Glide.with(holder.itemView.getContext())
                    .load(bytes)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_tree_placeholder)
                    .centerCrop()
                    .into(holder.imageTree);
        } else {
            String imageUrl = tree.getImageUrl();
            if ((imageUrl == null || imageUrl.isEmpty()) && tree.getSpecies() != null) {
                imageUrl = tree.getSpecies().getImageUrl();
            }
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_tree_placeholder)
                        .centerCrop()
                        .into(holder.imageTree);
            } else {
                holder.imageTree.setImageResource(R.drawable.ic_tree_placeholder);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(tree));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNickname;
        TextView textAge;
        TextView textSpecies;
        ImageView imageTree;

        ViewHolder(View view) {
            super(view);
            textNickname = view.findViewById(R.id.textTreeNickname);
            textAge = view.findViewById(R.id.textTreeAge);
            textSpecies = view.findViewById(R.id.textTreeSpecies);
            imageTree = view.findViewById(R.id.imageTree);
        }
    }
}

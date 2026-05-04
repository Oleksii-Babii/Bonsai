package org.tudublin.bonsaiapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.tudublin.bonsaiapp.R;
import org.tudublin.bonsaiapp.model.Tree;
import org.tudublin.bonsaiapp.util.ImageUtils;

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
        final List<Tree> oldItems = items;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tree, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tree tree = items.get(position);
        holder.textNickname.setText(tree.getNickname() != null ? tree.getNickname() : "");
        holder.textAge.setText(tree.getAge() + " yrs");
        holder.textSpecies.setText(tree.getSpecies() != null ? tree.getSpecies().getName() : "");

        ImageUtils.loadTreeImage(holder.imageTree, tree);

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

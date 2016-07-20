package com.jueggs.podcaster.ui.playlists.manage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ManagePlaylistsAdapter extends RecyclerView.Adapter<ManagePlaylistsAdapter.ViewHolder>
{
    @Getter
    private List<String> playlists = new ArrayList<>();
    private Context context;
    private Callback callback;

    public ManagePlaylistsAdapter(Context context, Callback callback)
    {
        this.context = context;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_manage_playlists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.name.setText(playlists.get(position));
        holder.edit.setOnClickListener(callback::onEditPlaylist);
        holder.delete.setOnClickListener(callback::onDeletePlaylist);
    }

    public void setPlaylists(List<String> playlists)
    {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        int count = playlists.size();
        callback.showEmptyView(count == 0);
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.name) TextView name;
        @Bind(R.id.edit) ImageButton edit;
        @Bind(R.id.delete) ImageButton delete;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

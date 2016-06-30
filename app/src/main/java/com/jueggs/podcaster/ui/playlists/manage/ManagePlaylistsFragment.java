package com.jueggs.podcaster.ui.playlists.manage;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;

import java.util.List;

import static android.text.TextUtils.*;
import static com.jueggs.podcaster.data.db.PlaylistsProvider.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.UIUtils.*;
import static com.jueggs.utils.Utils.*;

public class ManagePlaylistsFragment extends Fragment implements Callback
{
    @Bind(R.id.root) FrameLayout root;
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.fab) FloatingActionButton fab;

    private ManagePlaylistsAdapter adapter;
    private String toUpdateDelete;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_manage_playlists, container, false);
        ButterKnife.bind(this, view);

        equipeRecycler(getContext(), recycler, adapter = new ManagePlaylistsAdapter(getContext(), this));
        fab.setOnClickListener(this::onAddPlaylist);

        List<String> playlists = queryPlaylists();
        if (playlists != null)
            adapter.setPlaylists(playlists);

        return view;
    }

    private List<String> queryPlaylists()
    {
        Cursor cursor = getContext().getContentResolver().query(Playlist.BASE_URI, null, null, null, null);
        return transformCursorToPlaylists(cursor);
    }

    private void onAddPlaylist(View view)
    {
        showSimpleOneFieldInputDialog(getContext(), root, null, R.string.playlist_input_title_add, R.string.playlist_input_hint,
                R.string.playlist_input_ok, R.string.playlist_input_cancel, this::processAddInput);
    }

    private void processAddInput(String input)
    {
        if (isInputValid(input))
            insertPlaylist(input);
    }

    private boolean isInputValid(String input)
    {
        boolean valid = true;
        List<String> playlists = queryPlaylists();
        if (hasElements(playlists) && playlists.contains(input))
        {
            Toast.makeText(getContext(), String.format(getString(R.string.playlist_input_exists_format), input), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if (isEmpty(input))
        {
            Toast.makeText(getContext(), R.string.playlist_input_empty, Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private void insertPlaylist(String input)
    {
        Uri result = getContext().getContentResolver().insert(Playlist.BASE_URI, createPlaylistValues(input));
        if (result != null)
        {
            adapter.getPlaylists().add(0, input);
            adapter.notifyItemInserted(0);
        }
    }

    @Override
    public void onEditPlaylist(View view)
    {
        position = recycler.getChildAdapterPosition((View) view.getParent().getParent());
        toUpdateDelete = adapter.getPlaylists().get(position);
        showSimpleOneFieldInputDialog(getContext(), root, toUpdateDelete, R.string.playlist_input_title_edit,
                R.string.playlist_input_hint, R.string.playlist_input_ok, R.string.playlist_input_cancel, this::processEditInput);
    }

    private void processEditInput(String input)
    {
        if (isInputValid(input))
            updatePlaylist(input);
    }

    private void updatePlaylist(String input)
    {
        int updated = getContext().getContentResolver().update(Playlist.withName(toUpdateDelete), createPlaylistValues(input), null, null);
        if (updated > 0)
        {
            adapter.getPlaylists().set(position, input);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onDeletePlaylist(View view)
    {
        position = recycler.getChildAdapterPosition((View) view.getParent().getParent());
        toUpdateDelete = adapter.getPlaylists().get(position);
        String message = String.format(getString(R.string.playlist_delete_message_format), toUpdateDelete);
        showConfirmationDialog(getContext(), R.string.playlist_delete_title, message, R.string.playlist_delete_yes,
                R.string.playlist_delete_no, this::doDelete);
    }

    private void doDelete()
    {
        int deleted=getContext().getContentResolver().delete(Playlist.withName(toUpdateDelete), null, null);
        if (deleted > 0)
        {
            adapter.getPlaylists().remove(position);
            adapter.notifyItemRemoved(position);
        }
    }
}

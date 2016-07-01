package com.jueggs.podcaster.ui.playlists.manage;

import android.content.Context;
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
import com.jueggs.podcaster.utils.DaUtils;

import java.util.List;

import static android.text.TextUtils.*;
import static com.jueggs.podcaster.utils.DaUtils.*;
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

        List<String> playlists = queryPlaylists(getContext());
        if (playlists != null)
            adapter.setPlaylists(playlists);

        return view;
    }

    private void onAddPlaylist(View view)
    {
        showSimpleOneFieldInputDialog(getContext(), root, null, R.string.playlist_input_title_add, R.string.playlist_input_hint,
                R.string.playlist_input_ok, R.string.playlist_input_cancel, this::processAddInput);
    }

    private void processAddInput(String input)
    {
        if (isInputValid(input))
            doInsert(input);
    }

    private boolean isInputValid(String input)
    {
        boolean valid = true;
        List<String> playlists = queryPlaylists(getContext());
        if (hasElements(playlists) && playlists.contains(input))
        {
            shortToast(getContext(), String.format(getString(R.string.playlist_input_exists_format), input));
            valid = false;
        }
        else if (isEmpty(input))
        {
            shortToast(getContext(), R.string.playlist_input_empty);
            valid = false;
        }
        return valid;
    }

    private void doInsert(String input)
    {
        Uri result = insertPlaylist(getContext(), input);
        if (result != null)
        {
            adapter.getPlaylists().add(0, input);
            adapter.notifyItemInserted(0);
            shortToast(getContext(), R.string.msg_playlist_added);
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
            doUpdate(input);
    }

    private void doUpdate(String input)
    {
        int updated = updatePlaylist(getContext(), toUpdateDelete, input);
        if (updated > 0)
        {
            adapter.getPlaylists().set(position, input);
            adapter.notifyItemChanged(position);
            shortToast(getContext(), R.string.msg_playlist_updated);
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
        int deleted = deletePlaylist(getContext(), toUpdateDelete);
        if (deleted > 0)
        {
            adapter.getPlaylists().remove(position);
            adapter.notifyItemRemoved(position);
            shortToast(getContext(), R.string.msg_playlist_deleted);
        }
    }
}

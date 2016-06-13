package com.piatt.udacity.popularmovies.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.model.MessageType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageFragment extends DialogFragment {
    @BindView(R.id.icon_view) TextView iconView;
    @BindView(R.id.message_view) TextView messageView;
    private MessageType messageType;

    public static MessageFragment newInstance(MessageType messageType) {
        MessageFragment fragment = new MessageFragment();
        fragment.messageType = messageType;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (messageType) {
            case API: iconView.setText(R.string.error_icon);
                messageView.setText(R.string.api_message);
                break;
            case CONNECTION: iconView.setText(R.string.connection_icon);
                messageView.setText(R.string.connection_message);
                break;
            case FAVORITES: iconView.setText(R.string.favorite_off_icon);
                messageView.setText(R.string.favorites_message);
                break;
        }
    }

    @OnClick(R.id.ok_button)
    public void onOkButtonClick() {
        dismiss();
    }
}
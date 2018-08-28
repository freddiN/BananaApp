package de.freddi.bananaapp.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.freddi.bananaapp.R;
import de.freddi.bananaapp.settings.Preferences;
import de.freddi.bananaapp.settings.Preferences.PREF;

public class AccountFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentAccountView = inflater.inflate(R.layout.fragment_account, container, false);

        GuiHelper.configurePullToRefresh(fragmentAccountView, R.id.text_account_swipe, getActivity());

        final Preferences prefs = new Preferences();

        final int nEmojiBanana = 0x1F34C;

        StringBuilder buff = new StringBuilder();
        buff.append(String.format(" User: %s\n", prefs.getAsStringEmptyIfNull(PREF.ACCOUNT_DISPLAYNAME)));
        buff.append(String.format(" Bananas to spend: %s %s\n", prefs.getAsStringEmptyIfNull(PREF.ACCOUNT_BANANAS_TO_SPEND), GuiHelper.getEmojiByUnicode(nEmojiBanana)));
        buff.append(String.format(" Bananas received: %s %s\n", prefs.getAsStringEmptyIfNull(PREF.ACCOUNT_BANANAS_RECEIVED), GuiHelper.getEmojiByUnicode(nEmojiBanana)));
        buff.append("\n");

        buff.append(String.format(" AD user: %s\n", prefs.getAsStringEmptyIfNull(PREF.ACCOUNT_AD_USER)));
        buff.append(String.format(" Is admin: %s\n", "1".equalsIgnoreCase(prefs.getAsString(PREF.ACCOUNT_IS_ADMIN)) ? "yes":"no"));
        buff.append(String.format(" Userid: %s\n", prefs.getAsStringEmptyIfNull(PREF.ACCOUNT_ID)));
        buff.append("\n");

        buff.append(String.format(" Token: %s\n", prefs.getAsStringEmptyIfNull(PREF.ACCOUNT_TOKEN)));
        buff.append(String.format(" Token expiration: %s\n", prefs.getAsStringEmptyIfNull(PREF.ACCOUNT_TOKEN_EXPIRATION)));
        buff.append(String.format(" Token duration: %s\n", prefs.getAsStringEmptyIfNull(PREF.ACCOUNT_TOKEN_DURATION)));

        final TextView textAccountsView = fragmentAccountView.findViewById(R.id.id_account_text);
        textAccountsView.setText(buff.toString());

        return fragmentAccountView;
    }
}
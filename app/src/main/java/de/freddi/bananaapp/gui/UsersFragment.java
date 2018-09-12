package de.freddi.bananaapp.gui;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.freddi.bananaapp.App;
import de.freddi.bananaapp.MainActivity;
import de.freddi.bananaapp.R;
import de.freddi.bananaapp.async.http.SendBanana;
import de.freddi.bananaapp.database.DBUser;
import de.freddi.bananaapp.logging.L;
import de.freddi.bananaapp.settings.Preferences;
import de.freddi.bananaapp.settings.Preferences.PREF;

public class UsersFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String LOGGING_TAG = "UsersFragment";

    private ArrayAdapter m_listAdapter;

    private final List<DBUser> m_listUsers = new ArrayList<>();

    public void doUpdate(final String strSrc) {
        final LiveData<List<DBUser>> listUsers = App.get().getDB().databaseInterface().getAllUsers();

        listUsers.observe(this, dbUsers -> {
            final boolean iDebug = new Preferences().isDebugLogging();
            if (iDebug) {
                int nSizeNew = -1;
                if (listUsers.getValue() != null) {
                    nSizeNew = listUsers.getValue().size();
                }
                L.log(LOGGING_TAG, "observe listUsers size=" + nSizeNew + " current=" + m_listUsers.size() + " Src=" + strSrc, new Preferences());
            }

            if (dbUsers != null && m_listAdapter != null) {
                m_listUsers.clear();
                m_listUsers.addAll(dbUsers);
                m_listAdapter.notifyDataSetChanged();
                L.log(LOGGING_TAG, "notifyDataSetChanged", iDebug);
            }
        });
     }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        L.log(LOGGING_TAG,"onCreateView" , new Preferences());

        configureAdapter();

        View fragmentUsersView = inflater.inflate(R.layout.fragment_users, container, false);

        ListView listUsersView = fragmentUsersView.findViewById(R.id.list_users);
        listUsersView.setAdapter(m_listAdapter);
        listUsersView.setOnItemClickListener(this);

        GuiHelper.configurePullToRefresh(fragmentUsersView, R.id.list_users_swipe, getActivity());

        doUpdate("onCreateView");

        return fragmentUsersView;
    }

    private void configureAdapter() {
        L.log(LOGGING_TAG,"configureAdapter" , new Preferences());
        if (getActivity() == null) {
            return;
        }

        try {
            m_listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, m_listUsers) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    final View renderer = super.getView(position, convertView, parent);

                    final String strUser = m_listUsers.get(position).display_name;
                    if (strUser.equalsIgnoreCase(new Preferences().getAsString(PREF.ACCOUNT_DISPLAYNAME))) {
                        renderer.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.selection));
                    } else {
                        renderer.setBackgroundColor(Color.WHITE);
                    }

                    TextView text1 = renderer.findViewById(android.R.id.text1);
                    text1.setText(String.format(Locale.GERMANY, "%s (%d / %d)", strUser, m_listUsers.get(position).bananas_to_spend, m_listUsers.get(position).bananas_received));
                    return renderer;
                }
            };

        } catch(final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity() == null) {
            return;
        }

        final DBUser selectedUser = (DBUser) parent.getItemAtPosition(position);
        final String strCurrentUser = new Preferences().getAsString(PREF.ACCOUNT_DISPLAYNAME);
        if (selectedUser.display_name.equalsIgnoreCase(strCurrentUser)) {
            GuiHelper.doSnack(getActivity(), "Sorry, self-bananaing is not allowed");
            return;
        }

        // send banana dialog
        AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.send_banana_dialog, null);
        d.setMessage("Send Banana to \"" + selectedUser.display_name + "\"");
        d.setView(dialogView);

        final TextInputEditText txtComment = dialogView.findViewById(R.id.comment);

        final AlertDialog sendDialog = d.create();
        sendDialog.setOnShowListener(dialog -> {
            // workaround: stellt die virtuelle Tastatur dar wenn der Dialog aufgeht
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null) {
                imm.showSoftInput(txtComment, InputMethodManager.SHOW_IMPLICIT);
                imm.showSoftInput(txtComment, InputMethodManager.SHOW_IMPLICIT);    //manchmal gehts beim ersten mal nicht auf, sieht nach Timingproblem aus
            }
        });

        final ImageButton buttonBanana = dialogView.findViewById(R.id.bananaSendButton);
        buttonBanana.setOnClickListener(v -> {
            final String strComment = txtComment.getText().toString().trim();

            if (strComment.length() < 5) {
                GuiHelper.doToast(getActivity(), "Please provide at least a short comment (min. 5 chars)");
                return;
            }

            new SendBanana() {
                @Override
                protected void onPostExecute(Boolean isSuccess) {
                    super.onPostExecute(isSuccess);

                    if (isSuccess) {
                        GuiHelper.doSnack(getActivity(), "Successful");

                        ((MainActivity) getActivity()).performRefresh("send banana");
                    } else {
                        GuiHelper.doSnack(getActivity(), "Error");
                    }
                }
            }.execute(selectedUser.display_name, strComment);

            sendDialog.dismiss();
        });

        sendDialog.show();
    }
}
package de.freddi.bananaapp.gui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freddi.bananaapp.App;
import de.freddi.bananaapp.R;
import de.freddi.bananaapp.database.DBTransaction;
import de.freddi.bananaapp.logging.L;
import de.freddi.bananaapp.settings.Preferences;
import de.freddi.bananaapp.settings.Preferences.PREF;

public class TransactionsFragment extends Fragment {
    private static final String LOGGING_TAG = "TransactionsFragment";

    private ArrayAdapter m_listAdapter;

    private final List<DBTransaction> m_listTransactions = new ArrayList<>();

    public void doUpdate(final String strSrc) {
        final LiveData<List<DBTransaction>> listTransactions = App.get().getDB().databaseInterface().getAllTransactions();
        listTransactions.observe(this, dbTransactions -> {
            final boolean isDebug = new Preferences().isDebugLogging();
            L.log(LOGGING_TAG, "doUpdate src=" + strSrc, isDebug);

            if (dbTransactions != null && m_listAdapter != null) {
                m_listTransactions.clear();
                m_listTransactions.addAll(dbTransactions);
                m_listAdapter.notifyDataSetChanged();
                L.log(LOGGING_TAG, "doUpdate done", isDebug);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        L.log(LOGGING_TAG, "onCreateView", new Preferences());

        configureAdapter();

        View fragmentTransactionsView = inflater.inflate(R.layout.fragment_transactions, container, false);

        ListView listTransactionsView = fragmentTransactionsView.findViewById(R.id.list_transactions);
        listTransactionsView.setAdapter(m_listAdapter);

        GuiHelper.configurePullToRefresh(fragmentTransactionsView, R.id.list_transactions_swipe, getActivity());

        doUpdate("onCreateView");

        return fragmentTransactionsView;
    }

    private void configureAdapter() {
        if (getActivity() == null) {
            return;
        }

        try {
            m_listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, m_listTransactions) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = view.findViewById(android.R.id.text1);
                    TextView text2 = view.findViewById(android.R.id.text2);

                    final DBTransaction t = m_listTransactions.get(position);

                    //text1.setText(String.format("%s %s -> %s", t.timestamp, t.from_user, t.to_user));
                    text1.setText(String.format("%s %s %s %s", t.timestamp, t.from_user, GuiHelper.getEmojiByUnicode(0x27A1), t.to_user));
                    setTextTextColor(text1, t.source);

                    String strCategory = "";
                    if (StringUtils.isNotBlank(t.category)) {
                        strCategory = "\n(" + t.category + ")";
                    }

                    text2.setText(String.format("%s%s", Html.fromHtml(t.comment), strCategory));
                    text2.setTextColor(Color.parseColor("#000000"));

                    final String strCurrentUser = new Preferences().getAsString(PREF.ACCOUNT_DISPLAYNAME);
                    if (StringUtils.equalsIgnoreCase(t.from_user, strCurrentUser) || StringUtils.equalsIgnoreCase(t.to_user, strCurrentUser)) {
                        view.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.selection));
                    } else if (StringUtils.equalsIgnoreCase(t.source, "rain")) {
                        view.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.selection_bananarain));
                    } else {
                        view.setBackgroundColor(Color.WHITE);
                    }

                    return view;
                }
            };

        } catch(final Exception e) {
          e.printStackTrace();
        }
    }

    private void setTextTextColor(final TextView text, final String strSource) {
        text.setTextColor(Color.BLACK);

        if (getActivity() == null) {
            return;
        }
        if (StringUtils.equalsIgnoreCase("webpage", strSource)) {
            text.setTextColor(ContextCompat.getColor(getActivity(), R.color.webpage));
        } else if (StringUtils.equalsIgnoreCase("app", strSource)) {
            text.setTextColor(ContextCompat.getColor(getActivity(), R.color.app));
        }
    }
}
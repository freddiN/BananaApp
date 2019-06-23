package de.freddi.bananaapp.gui;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import de.freddi.bananaapp.MainActivity;
import de.freddi.bananaapp.R;
import de.freddi.bananaapp.async.http.Refresh;

public class GuiHelper {

    public enum Navigation {
        USERS,
        TRANSACTIONS,
        ACCOUNT
    }

    public static void doSnack(final Activity activity, final String strMessage){
        if (activity != null) {
            final View v = activity.findViewById(R.id.container);
            Snackbar snackbar = Snackbar.make(v, strMessage, 3000);

            View snackbarView = snackbar.getView();
            TextView snackTextView = snackbarView.findViewById(R.id.snackbar_text);
            snackTextView.setMaxLines(4);

            snackbar.show();
        }
    }

    public static void doToast(final Activity activity, final String strMessage){
        activity.runOnUiThread(() -> Toast.makeText(activity, strMessage, Toast.LENGTH_LONG).show());
    }

    /**
     * used in the three fragments
     */
    public static void configurePullToRefresh(final View viewFragment, final int nSwipeViewId, final Activity activity) {
        final SwipeRefreshLayout swipeContainerUsers = viewFragment.findViewById(nSwipeViewId);
        swipeContainerUsers.setOnRefreshListener(() -> {
            new Refresh() {
                @Override
                protected void onPostExecute(Boolean isSuccess) {
                    super.onPostExecute(isSuccess);

                    swipeContainerUsers.setRefreshing(false);

                    ((MainActivity)activity).refreshViews("configurePullToRefresh");
                }
            }.execute();
        });

        swipeContainerUsers.setColorSchemeColors(
                Color.BLUE,
                Color.RED,
                Color.GREEN,
                Color.DKGRAY);

        /*swipeContainerUsers.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary);*/
    }

    /**
     * for the emoji display
     *
     * @param nUnicode e.g. 0x1F34C
     * @return Emoji
     */
    public static String getEmojiByUnicode(final int nUnicode){
        return new String(Character.toChars(nUnicode));
    }
}

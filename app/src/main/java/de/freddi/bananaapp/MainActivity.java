package de.freddi.bananaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.commons.lang3.StringUtils;

import de.freddi.bananaapp.async.database.Wipe;
import de.freddi.bananaapp.async.http.Logout;
import de.freddi.bananaapp.async.http.Refresh;
import de.freddi.bananaapp.gui.AccountFragment;
import de.freddi.bananaapp.gui.GuiHelper;
import de.freddi.bananaapp.gui.GuiHelper.Navigation;
import de.freddi.bananaapp.gui.TransactionsFragment;
import de.freddi.bananaapp.gui.UsersFragment;
import de.freddi.bananaapp.gui.ViewPagerAdapter;
import de.freddi.bananaapp.logging.L;
import de.freddi.bananaapp.notification.NotificationHelper;
import de.freddi.bananaapp.notification.firebase.AsyncFirebaseRegister;
import de.freddi.bananaapp.settings.Preferences;
import de.freddi.bananaapp.settings.Preferences.PREF;

public class MainActivity extends AppCompatActivity {

    private static final String LOGGING_TAG = "MainActivity";

    private ViewPager m_viewPager;

    private BottomNavigationView m_navigation;
    private MenuItem prevMenuItem;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.log(LOGGING_TAG, "onReceive", new Preferences());
            performRefresh("firebase");
        }
    };

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_users:
                        m_viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_transactions:
                        m_viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_account:
                        m_viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            };

    private void showNavigation(final Navigation nav) {
        final ViewPager v = findViewById(R.id.fragments);

        switch (nav) {
            case USERS:
                v.setCurrentItem(0, true); break;
            case TRANSACTIONS:
                v.setCurrentItem(1, true); break;
            case ACCOUNT:
                v.setCurrentItem(2, true); break;
        }
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addUsersFragment(new UsersFragment());
        adapter.addTransactionsFragment(new TransactionsFragment());
        adapter.addAccountFragment(new AccountFragment());

        m_viewPager = findViewById(R.id.fragments);
        m_viewPager.setAdapter(adapter);
        m_viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);    //Workaround wegen fehlerhafter overscroll animation an den Seiten
        m_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    m_navigation.getMenu().getItem(0).setChecked(false);
                }

                m_navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = m_navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationHelper.createNotificationChannel(this);

        setContentView(R.layout.activity_main);

        setupViewPager();

        setSupportActionBar(findViewById(R.id.banana_toolbar));

        m_navigation = findViewById(R.id.navigation);
        m_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        updateToolbar();

        //Start via Notification oder shortcut
        if (getIntent().getExtras() != null &&
            getIntent().getExtras().containsKey("goto") &&
            StringUtils.isNotBlank(new Preferences().getAsString(PREF.ACCOUNT_DISPLAYNAME))) {

            final String strGoto = (String)getIntent().getExtras().get("goto");
            if ("transactions".equalsIgnoreCase(strGoto)) {
                showNavigation(Navigation.TRANSACTIONS);
            } else if ("users".equalsIgnoreCase(strGoto)) {
                showNavigation(Navigation.USERS);
            }
        } else {
            showNavigation(Navigation.USERS);
        }

        new AsyncFirebaseRegister().execute();

        registerReceiver(mMessageReceiver, new IntentFilter("FIREBASE_NOTIFICATION"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                performRefresh("refresh menu");
                return true;
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 1337);
                return true;
            case R.id.action_scan_qr:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan the Token QR Code");
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
                return true;
            case R.id.action_logout:
                new Logout().execute();
                new Wipe() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        refreshViews("logout menu");
                    }
                }.execute();

                GuiHelper.doSnack(this, "Logout performed");

                return true;
            case R.id.action_close:
                System.exit(0);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateToolbar() {
        final Preferences prefs = new Preferences();

        final String strUsername = prefs.getAsString(PREF.ACCOUNT_DISPLAYNAME);

        final Toolbar t = findViewById(R.id.banana_toolbar);

        if (StringUtils.isNotBlank(strUsername)) {
            t.setTitle("Logged in as \"" + strUsername + "\"");
            t.setSubtitle("Last sync: " + prefs.getAsString(Preferences.PREF.STATE_LAST_SYNC));
        } else {
            t.setTitle("Not logged in");
            t.setSubtitle("Please provide a Token");
        }

        setSupportActionBar(t);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        //1337 = kommt aus den Settings zurück (siehe onOptionsItemSelected())
        final Preferences prefs = new Preferences();
        if (requestCode == 1337 && StringUtils.isNotBlank(prefs.getAsString(PREF.STATE_SETTINGS_CHANGED))) {
            prefs.set(PREF.STATE_SETTINGS_CHANGED, "");
            performRefresh("settings changed");
        } else if (IntentIntegrator.parseActivityResult(requestCode, resultCode, data) != null) {
            /* QR Code Scan Ende*/
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result.getContents() == null) {
                GuiHelper.doSnack(this, "QRScan cancelled");
            } else {
                final String strResult = result.getContents();
                if (isValidSetup(strResult)) {
                    setupFromQR(strResult);
                    performRefresh("qrcode ok");
                } else {
                    GuiHelper.doSnack(this, "QRScan cancelled (invalid data in qr code)");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean isValidSetup(final String strContent) {
        return strContent.length() > 20 && strContent.startsWith("bsetup:");
    }

    /*
     * @param strResult bsetup:server=http://192.168.178.24/BananaServer/service.php|user=uasad|pass=asdasd|topic=banana_new_banana_staging
     */
    private void setupFromQR(final String strResult) {
        final String strKeyValueParts[] = strResult.substring(7).split("\\|");  //entfernt das "bsetup:"

        final Preferences prefs = new Preferences();

        String[] strParts;
        for (String strPart: strKeyValueParts) {
            strParts = strPart.split("=");
            if (strParts.length != 2 || StringUtils.isBlank(strParts[0])|| StringUtils.isBlank(strParts[1]) ) {
                continue;
            }

            if ("server".equalsIgnoreCase(strParts[0])) {
                prefs.set(PREF.CONNECTION_SERVER, strParts[1]);
            } else if ("user".equalsIgnoreCase(strParts[0])) {
                prefs.set(PREF.CONNECTION_HTTP_USER, strParts[1]);
            } else if ("pass".equalsIgnoreCase(strParts[0])) {
                prefs.set(PREF.CONNECTION_HTTP_PASS, strParts[1]);
            } else if ("topic".equalsIgnoreCase(strParts[0])) {
                prefs.set(PREF.NOTIFICATIONS_TOPIC, strParts[1]);
            } else if ("token".equalsIgnoreCase(strParts[0])) {
                prefs.set(PREF.ACCOUNT_TOKEN, strParts[1]);
            }
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        /* aus der Notification */
        if(intent.getExtras() != null && intent.getExtras().get("goto") != null){
            final String strGoto = (String)intent.getExtras().get("goto");
            if ("transactions".equalsIgnoreCase(strGoto)) {
                showNavigation(Navigation.TRANSACTIONS);
            } else if ("users".equalsIgnoreCase(strGoto)) {
                showNavigation(Navigation.USERS);
            }
        }
    }

    public void performRefresh(final String strSrc) {
        new Refresh() {
            @Override
            protected void onPostExecute(final Boolean isSuccess) {
                super.onPostExecute(isSuccess);
                refreshViews(strSrc);
                new Preferences().set(PREF.STATE_FIREBASE_RECEIVED, "");
            }
        }.execute();
    }

    public void refreshViews(final String strSrc) {
        updateToolbar();

        for (Fragment f: getSupportFragmentManager().getFragments()) {
            if (f instanceof UsersFragment) {
                ((UsersFragment)f).doUpdate(strSrc);
            } else if (f instanceof TransactionsFragment) {
                ((TransactionsFragment)f).doUpdate(strSrc);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkForFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkForFirebase();
    }

    private void checkForFirebase() {
        final Preferences pref = new Preferences();
        L.log(LOGGING_TAG, "checkForFirebase", pref);

        if (StringUtils.equalsIgnoreCase(pref.getAsString(PREF.STATE_FIREBASE_RECEIVED), "true")) {
            L.log(LOGGING_TAG, "checkForFirebase refresh triggered", pref);
            performRefresh("checkForFirebase");
        }
    }

    @Override
    protected void onDestroy () {
        unregisterReceiver(mMessageReceiver);

        super.onDestroy ();
    }
}
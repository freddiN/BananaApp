package de.freddi.bananaapp.gui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Quelle: https://github.com/jaisonfdo/BottomNavigation
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Fragment m_fragUsers = null;
    private Fragment m_fragTransactions = null;
    private Fragment m_fragAccount = null;

    public ViewPagerAdapter(final FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: return m_fragUsers;
            case 1: return m_fragTransactions;
            case 2: return m_fragAccount;
         }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void addUsersFragment(UsersFragment fragUsers) {
        m_fragUsers = fragUsers;
    }

    public void addTransactionsFragment(TransactionsFragment freadTransactions) {
        m_fragTransactions = freadTransactions;
    }

    public void addAccountFragment(AccountFragment fragAccount) {
        m_fragAccount = fragAccount;
    }
}

package wada.programmics.thewada.AdapterClass;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import wada.programmics.thewada.FragmentClass.AboutFragment;
import wada.programmics.thewada.FragmentClass.NewsFragment;

public class Pager extends FragmentStatePagerAdapter {

    int tabCount;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AboutFragment aboutFragment = new AboutFragment();
                return aboutFragment;
            case 1:
                NewsFragment newsFragment = new NewsFragment();
                return newsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
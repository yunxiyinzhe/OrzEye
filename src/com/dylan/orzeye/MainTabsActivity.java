package com.dylan.orzeye;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class MainTabsActivity extends FragmentActivity {
    private static final String[] CONTENT = new String[] { "Notes", "Dictionary"};
    private static final int[] ICONS = new int[] {
            R.drawable.dict_result_notes_add_up_icon,
            R.drawable.dict_web_search_up_icon,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintabs);

        FragmentPagerAdapter adapter = new  TabPageIndicatorAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        pager.setCurrentItem(getIntent().getIntExtra("position", 0));
    }

    class  TabPageIndicatorAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public  TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	Fragment fragment = null; 
        	switch (position) {
			case 0:
				fragment = new DictionaryFragment();
				break;
			case 1:
				fragment = new NotesFragment();
				break;
			default:
				break;
			}
            return fragment ;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override public int getIconResId(int index) {
          return ICONS[index];
        }

      @Override
        public int getCount() {
          return CONTENT.length;
        }
    }
}

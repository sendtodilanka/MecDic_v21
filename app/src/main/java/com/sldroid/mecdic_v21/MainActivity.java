package com.sldroid.mecdic_v21;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.sldroid.mecdic_v21.fragment.EnFragment;
import com.sldroid.mecdic_v21.fragment.SiFragment;
import com.sldroid.mecdic_v21.pageAdapter.DicPagerAdapter;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private DicPagerAdapter dicPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Drawer result;
    private static SharedPreferences sPrefer;
    private static SharedPreferences.Editor editor;
    private MenuItem searchItem;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Dictionary");

        sPrefer = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sPrefer.edit();

        editor.putString("siSearch", "").apply();
        editor.putString("enSearch", "").apply();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        dicPagerAdapter = new DicPagerAdapter(getSupportFragmentManager());
        dicPagerAdapter.addFragment(new EnFragment(), "English");
        dicPagerAdapter.addFragment(new SiFragment(), "සිංහල");

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(dicPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position  == 0){
                    String query = sPrefer.getString("enSearch", "");
                    searchView.setQuery(query, false);
                }
                else{
                    String query = sPrefer.getString("siSearch", "");
                    searchView.setQuery(query, false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withInnerShadow(true)
                .withHeader(R.layout.nav_header_main)
                .withDrawerWidthDp(265)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName("Dictionary")
                                .withIconColor(Color.parseColor("#F06292"))
                                .withIcon(FontAwesome.Icon.faw_book)
                                .withIdentifier(1),
                        new PrimaryDrawerItem()
                                .withName("History")
                                .withIconColor(Color.parseColor("#BA68C8"))
                                .withIcon(FontAwesome.Icon.faw_history)
                                .withIdentifier(2),
                        new PrimaryDrawerItem()
                                .withName("Favourite")
                                .withIconColor(Color.parseColor("#7986CB"))
                                .withIcon(FontAwesome.Icon.faw_star_o)
                                .withIdentifier(3),
                        new PrimaryDrawerItem()
                                .withName("Translate")
                                .withIconColor(Color.parseColor("#4FC3F7"))
                                .withIcon(FontAwesome.Icon.faw_users)
                                .withIdentifier(4),
                        new PrimaryDrawerItem()
                                .withName("Text-To-Speach")
                                .withIconColor(Color.parseColor("#4DB6AC"))
                                .withIcon(FontAwesome.Icon.faw_volume_up)
                                .withIdentifier(5),
                        new PrimaryDrawerItem()
                                .withName("Submit Words")
                                .withIconColor(Color.parseColor("#81C784"))
                                .withIcon(FontAwesome.Icon.faw_envelope_o)
                                .withIdentifier(6),
                        new SectionDrawerItem()
                                .withName("App's Setting"),
                        new PrimaryDrawerItem()
                                .withName("About")
                                .withIconColor(Color.parseColor("#F06292"))
                                .withIcon(FontAwesome.Icon.faw_info).withIdentifier(7),
                        new PrimaryDrawerItem()
                                .withName("Setting")
                                .withIconColor(Color.parseColor("#4DB6AC"))
                                .withIcon(FontAwesome.Icon.faw_cogs).withIdentifier(8),
                        new PrimaryDrawerItem()
                                .withName("Rate this app")
                                .withIconColor(Color.parseColor("#DCE775"))
                                .withIcon(FontAwesome.Icon.faw_thumbs_o_up).withIdentifier(9)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position){
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 5:
                                break;
                            case 6:
                                break;
                            case 7:
                                break;
                            case 8:
                                break;
                            case 10:
                                break;
                            case 11:
                                break;
                            case 13:
                                break;
                        }

                        if (drawerItem instanceof Nameable) {
                            Toast.makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                        }
                        //we do not consume the event and want the Drawer to continue with the event chain
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_search, menu);

        menu.findItem(R.id.act_fb)
                .setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_facebook)
                        .color(Color.WHITE).actionBar().paddingDp(4));
        menu.findItem(R.id.act_gp)
                .setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_google_plus)
                        .color(Color.WHITE).actionBar().paddingDp(2));
        menu.findItem(R.id.search)
                .setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_search)
                        .color(Color.WHITE).actionBar().paddingDp(3));

        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(mViewPager.getCurrentItem() == 0) //First fragment
        {
            EnFragment engFragment = (EnFragment)mViewPager.getAdapter()
                    .instantiateItem(mViewPager, mViewPager.getCurrentItem());
            engFragment.textSearch(newText.replace(" ",""));
            editor.putString("enSearch", newText.replace(" ","")).apply();
        }
        else if(mViewPager.getCurrentItem() == 1) //First fragment
        {
            SiFragment sinFragment = (SiFragment) mViewPager.getAdapter()
                    .instantiateItem(mViewPager, mViewPager.getCurrentItem());
            sinFragment.textSearch(newText.replace(" ",""));
            editor.putString("siSearch", newText.replace(" ","")).apply();
        }
        return false;
    }

    public void callMainActivity(Boolean val){
        if (val){
            SiFragment sinFragment = (SiFragment) mViewPager.getAdapter()
                    .instantiateItem(mViewPager, mViewPager.getCurrentItem());
            sinFragment.update();
        }
        else {
            EnFragment engFragment = (EnFragment)mViewPager.getAdapter()
                    .instantiateItem(mViewPager, mViewPager.getCurrentItem());
            engFragment.update();
        }
    }
}

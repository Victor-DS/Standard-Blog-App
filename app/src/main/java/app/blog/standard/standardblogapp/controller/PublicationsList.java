package app.blog.standard.standardblogapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.controller.Fragments.DefaultWebviewFragment;
import app.blog.standard.standardblogapp.controller.Fragments.PublicationListFragment;
import app.blog.standard.standardblogapp.controller.Fragments.ViewImageFragment;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.util.PublicationHelper;

public class PublicationsList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PublicationListFragment.OnListFragmentInteractionListener,
        DefaultWebviewFragment.OnWebViewClickListener {

    private PublicationHelper publicationHelper;
    private PublicationListFragment publicationListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //region view
        setContentView(R.layout.activity_publications_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //endregion

        publicationHelper = PublicationHelper.getInstance(this);

        //region drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();

        String[] categories = publicationHelper.getAllCategories();

        for(int index = 0; index < categories.length; index++)
            menu.add(categories[index]);
        //endregion

        publicationListFragment = PublicationListFragment.newInstance();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, publicationListFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //TODO Add double press to exit
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.nav_home:
//                publicationListFragment.switchCategory(null);
                break;

            case R.id.nav_about:

                break;

            case R.id.nav_share:

                break;

            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            default: //Categories
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container,
                                PublicationListFragment.newInstance(item.getTitle().toString()))
                        .addToBackStack(null)
                        .commit();
//                publicationListFragment.switchCategory(item.getTitle().toString());
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(Publication item) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, DefaultWebviewFragment.newInstance(item))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onImageClicked(String url) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, ViewImageFragment.newInstance(url))
                .addToBackStack(null)
                .commit();
    }
}

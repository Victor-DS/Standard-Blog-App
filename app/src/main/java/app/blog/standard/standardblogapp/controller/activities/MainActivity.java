package app.blog.standard.standardblogapp.controller.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.controller.Fragments.DefaultWebviewFragment;
import app.blog.standard.standardblogapp.controller.Fragments.PublicationListFragment;
import app.blog.standard.standardblogapp.controller.Fragments.ViewImageFragment;
import app.blog.standard.standardblogapp.controller.service.AlarmSync;
import app.blog.standard.standardblogapp.controller.service.Syncronizer;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.util.AlarmHelper;
import app.blog.standard.standardblogapp.model.util.GoogleAnalyticsHelper;
import app.blog.standard.standardblogapp.model.util.PreferenceHelper;
import app.blog.standard.standardblogapp.model.util.PublicationHelper;
import app.blog.standard.standardblogapp.model.util.Util;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PublicationListFragment.OnListFragmentInteractionListener,
        DefaultWebviewFragment.OnWebViewClickListener {

    //region variables
    private PublicationHelper publicationHelper;
    private PublicationListFragment publicationListFragment;

    private boolean doubleBackToExitPressedOnce;
    //endregion

    //region Lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //region view
        setContentView(R.layout.activity_publications_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //endregion

        Util.dismissNotification();

        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));
        publicationHelper = PublicationHelper.getInstance(this);

        setUpAlarm();

        //region drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        SubMenu subMenu = menu.getItem(7).getSubMenu();

        for(String category : publicationHelper.getAllCategories())
            subMenu.add(category);
        //endregion

        publicationListFragment = PublicationListFragment.newInstance();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, publicationListFragment)
                    .commit();
        }
    }

    private void setUpAlarm() {
        if(AlarmHelper.getSyncInterval() == AlarmHelper.NEVER)
            return;

        Intent intent = new Intent(this, Syncronizer.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();

        final long firstTime = AlarmHelper.getSyncInterval() + cal.getTimeInMillis();

        alarm.setInexactRepeating(AlarmManager.RTC,
                firstTime, AlarmHelper.getSyncInterval(), pendingIntent);
    }
    //endregion

    //region Interaction
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (doubleBackToExitPressedOnce ||
                getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.double_press_to_exit, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.nav_home:
                GoogleAnalyticsHelper.sendEvent("Drawer Item Click", "Home Button");

                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;

            case R.id.nav_about:
                GoogleAnalyticsHelper.sendEvent("Drawer Item Click", "About Button");

                Util.openGenericDialog(this, R.string.action_about, R.string.about_message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                break;

            case R.id.nav_share:
                GoogleAnalyticsHelper.sendEvent("Drawer Item Click", "Share App Button");

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=" + this.getPackageName());
                shareIntent.setType("text/plain");
                startActivity(shareIntent);

                break;

            case R.id.nav_more_apps:
                GoogleAnalyticsHelper.sendEvent("Drawer Item Click", "More Apps Button");

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://search?q=pub:Willy+Wonka")));
                break;

            case R.id.nav_rate:
                GoogleAnalyticsHelper.sendEvent("Drawer Item Click", "Rate App Button");

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + this.getPackageName())));
                break;

            case R.id.nav_fb_link:
                GoogleAnalyticsHelper.sendEvent("Drawer Item Click", "Facebook Link Button");

                startActivity(Util.getFBPageIntent());
                break;

            case R.id.nav_settings:
                GoogleAnalyticsHelper.sendEvent("Drawer Item Click", "Settings Button");

                startActivity(new Intent(this, SettingsActivity.class));
                break;

            default: //Categories
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container,
                                PublicationListFragment.newInstance(item.getTitle().toString()))
                        .addToBackStack(null)
                        .commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    //region Implemented Methods
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
    //endregion
}

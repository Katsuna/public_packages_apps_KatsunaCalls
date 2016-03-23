package gr.crystalogic.calls.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import gr.crystalogic.calls.R;
import gr.crystalogic.calls.domain.Call;
import gr.crystalogic.calls.providers.CallsProvider;
import gr.crystalogic.calls.ui.adapters.CallsAdapter;
import gr.crystalogic.calls.utils.Device;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private CallsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Device.openApp(MainActivity.this, "gr.crystalogic.oldmen")) {
                    Toast.makeText(MainActivity.this, R.string.contacts_app_missing, Toast.LENGTH_SHORT).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initControls();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
/*        int id = item.getItemId();

        if (id == R.id.drawer_settings) {
            // Handle the camera action
        } else if (id == R.id.drawer_help) {

        } else if (id == R.id.drawer_info) {

        } */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCalls();
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.calls_list);
    }

    private void loadCalls() {
/*
        if (!Device.hasAllPermissions(this, permissions)) {
            Device.requestPermissions(this, permissions, Constants.REQUEST_CODE_READ_SMS_AND_CONTACTS);
            return;
        }*/

        CallsProvider callsProvider = new CallsProvider(this);
        List<Call> calls = callsProvider.getCalls();
        mAdapter = new CallsAdapter(calls,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
/*                        int position = mRecyclerView.getChildAdapterPosition(v);
                        Conversation conversation = mAdapter.getItemAtPosition(position);
                        showConversation(conversation.getId());*/
                    }
                },
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                });
        mRecyclerView.setAdapter(mAdapter);
    }

}

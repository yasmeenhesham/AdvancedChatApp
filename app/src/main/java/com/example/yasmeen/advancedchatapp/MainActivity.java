package com.example.yasmeen.advancedchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mytoolbar;
    private ViewPager mViewPager;
    private SelectPagerAdapter mSelectPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mytoolbar=findViewById(R.id.toolbar_main);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        FirebaseUser userr = mAuth.getCurrentUser();
        if(userr != null) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        mViewPager= findViewById(R.id.tabPager);
        mSelectPagerAdapter =new SelectPagerAdapter(getSupportFragmentManager(),getApplication().getApplicationContext());
        mViewPager.setAdapter(mSelectPagerAdapter);

        mTabLayout=findViewById(R.id.main_tab);
        mTabLayout.setupWithViewPager(mViewPager);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
            uppdateUI();

        }
        else
        {
            mDatabaseReference.child("online").setValue("true");
            mDatabaseReference.child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth.getCurrentUser() != null) {
            mDatabaseReference.child("online").setValue("false");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id= item.getItemId();
        if(id== R.id.logout_menu)
        {
            FirebaseAuth.getInstance().signOut();
            uppdateUI();
        }
        else if(id== R.id.account_setting_menu)
        {
            Intent settingIntnt= new Intent(MainActivity.this,SettingActivity.class);
            startActivity(settingIntnt);
        }
        else if(id== R.id.alluser_menu)
        {
            Intent allUsersIntent= new Intent(MainActivity.this,AllUsersActivity.class);
            startActivity(allUsersIntent);
        }
        return true;
    }

    private void uppdateUI() {
        Intent intent= new Intent(this,StartActivity.class);
        startActivity(intent);
        finish();
    }
}

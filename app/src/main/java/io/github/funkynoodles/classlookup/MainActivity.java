package io.github.funkynoodles.classlookup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<Fragment> activeFragments = new ArrayList<>();

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.selectTabAtPosition(1);
        bottomBar.setOnTabSelectListener((new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment;
                switch (tabId){
                    case R.id.tabHome:
                        getSupportActionBar().setTitle(getString(R.string.textHome));
                        fragment = fragmentManager.findFragmentByTag("Home Fragment");
                        hideAllOtherFragments("Home Fragment");
                        if(fragment != null){
                            fragmentManager.beginTransaction().show(fragment).commit();
                        }else{
                            HomeFragment homeFragment = HomeFragment.newInstance();
                            fragmentManager.beginTransaction().add(R.id.contentContainer, homeFragment, "Home Fragment").commit();
                            activeFragments.add(homeFragment);
                        }
                        break;

                    case R.id.tabSettings:
                        getSupportActionBar().setTitle(getString(R.string.textSettings));
                        fragment = fragmentManager.findFragmentByTag("Settings Fragment");
                        hideAllOtherFragments("Settings Fragment");
                        if(fragment != null){
                            fragmentManager.beginTransaction().show(fragment).commit();
                        }else{
                            SettingsFragment settingsFragment = SettingsFragment.newInstance();
                            fragmentManager.beginTransaction().add(R.id.contentContainer, settingsFragment, "Settings Fragment").commit();
                            activeFragments.add(settingsFragment);
                        }
                        break;

                    case R.id.tabSchedules:
                        getSupportActionBar().setTitle(getString(R.string.textSchedules));
                        fragment = fragmentManager.findFragmentByTag("Schedules Fragment");
                        hideAllOtherFragments("Schedules Fragment");
                        if(fragment != null){
                            fragmentManager.beginTransaction().show(fragment).commit();
                        }else{
                            SchedulesFragment schedulesFragment = SchedulesFragment.newInstance();
                            fragmentManager.beginTransaction().add(R.id.contentContainer, schedulesFragment, "Schedules Fragment").commit();
                            activeFragments.add(schedulesFragment);
                        }
                        break;
                }
            }
        }));
    }

    /**
     * Hide all other fragment except for the one with the given tag
     * @param fragmentTag tag of the fragment want to show
     */
    public void hideAllOtherFragments(String fragmentTag){
        for(Fragment fragment : activeFragments){
            if(!fragment.getTag().equals(fragmentTag)){
                getFragmentManager().beginTransaction().hide(fragment).commit();
            }
        }
    }
}

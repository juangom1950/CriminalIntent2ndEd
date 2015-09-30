package bignerdranch2nded.com.criminalintent2nded;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Juan on 9/1/2015.
 * AppCompatActivity is a sub class of FragmentActivity. This mean that you can still use support fragments in AppCompatActivity,
 * which makes this a simple change in CriminalIntent.
 * the toolbar has been back-ported to the AppCompat library. The AppCompat library allows you to provide a Lollipop’d toolbar on any version of Android back to API 7 (Android 2.1).
 * current version at the moment is API 22 (Android 5.1.1). You extends AppcompatActivity to be able to use toolbar and support it for lower versions
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    //SingleFragmentActivity will work the same as before, but now its subclasses can choose to override getLayoutResId()
    //to return a layout other than activity_fragment.xml.
    //@LayoutRes tells Android Studio that any implementation of this method should return a valid layout resource ID.
    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_fragment);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        //If fragment doesn't exist add new fragment to the Fragment_container
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}

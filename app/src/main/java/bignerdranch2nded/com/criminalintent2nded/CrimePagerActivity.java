package bignerdranch2nded.com.criminalintent2nded;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Juan on 9/2/2015.
 *
 There are two key classes that we will use from the support library: the Fragment class (android.support.v4. app.Fragment)
 and the FragmentActivity class (android.support.v4. app.FragmentActivity). Using fragments requires activities that know how
 to manage fragments. The FragmentActivity class knows how to manage the support version of fragments.

 AppCompatActivity is a sub class of FragmentActivity. This mean that you can still use support fragments in AppCompatActivity,
 * which makes this a simple change in CriminalIntent.
 * The toolbar has been back-ported to the AppCompat library. The AppCompat library allows you to provide a Lollipop’d toolbar on any version of Android back to API 7 (Android 2.1).
 * current version at the moment is API 22 (Android 5.1.1). You extends AppcompatActivity to be able to use toolbar and support it for lower versions
 */

//CrimeFragment.Callbacks must be implemented in all activities that host CrimeFragment.
public class CrimePagerActivity  extends AppCompatActivity implements CrimeFragment.Callbacks {

    private static final String EXTRA_CRIME_ID = "bignerdranch2nded.com.criminalintent2nded.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    /*public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        return intent;
    }*/

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        //Get value from intent
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

         mViewPager =  (ViewPager)findViewById(R.id.activity_crime_pager_view_pager);

        //Get the list of crimes
        mCrimes = CrimeLab.get(this).getCrimes();
        //Get the activity instance of FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            //It fetches the Crime instance for the given position in the dataset.
            //It then uses that Crime's ID to create and return a properly configured CrimeFragment
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            //Returns the number of items in the array list.
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        /*
        By default, the ViewPager shows the first item in its PagerAdapter. You can have it show the crime
        that was selected by setting the ViewPager’s current item to the index of the selected crime.
         */
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    //Implement method from interface
    @Override
    public void onCrimeUpdated(Crime crime) {

    }


}

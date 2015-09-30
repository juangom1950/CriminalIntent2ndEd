package bignerdranch2nded.com.criminalintent2nded;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.List;
import java.util.UUID;

/**
 * Created by Juan on 9/1/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity
    implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks, CrimeFragment.CallbacksUpdateDetails {


    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        //return R.layout.activity_twopane;
        return R.layout.activity_masterdetail;
    }

    //Implement "CrimeListFragment.Callbacks" dependency method
    @Override
    public void onCrimeSelected(Crime crime) {

        //If the layout does have a detail_fragment_container, then you are going to create a fragment transaction that removes the existing CrimeFragment
        // from detail_fragment_container (if there is one in there) and adds the CrimeFragment that you want to see.
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    //Implement method from CrimeFragment.Callbacks interface
    @Override
    public void onCrimeUpdated(Crime crime) {

        CrimeListFragment listFragment = (CrimeListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        listFragment.updateUI();

    }

    //Implement method from CrimeFragment.CallbacksUpdateDetails interface
    @Override
    public void onUpdatedDetails(Crime crime) {

        //Get crimes list
        CrimeLab crimeLab = CrimeLab.get(this);
        List<Crime> crimes = crimeLab.getCrimes();

        Crime existedCrime = crimes.get(0);
        Fragment newDetail = CrimeFragment.newInstance(existedCrime.getId());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container, newDetail)
                .commit();

        /*Intent intent = CrimePagerActivity.newIntent(this);
        startActivity(intent);*/;

        /*CrimeFragment detailFragment = (CrimeFragment)getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment_container);

        if (findViewById(R.id.detail_fragment_container) != null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, detailFragment)
                    .commit();

        }*/


    }
}

package bignerdranch2nded.com.criminalintent2nded;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Juan on 9/1/2015.
 */
public class CrimeListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    //Define a member variable that holds an object that implements Callbacks
    private Callbacks mCallbacks;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    /*
    When the activity receives its onCreateOptionsMenu(…) callback from the OS.
    You must explicitly tell the FragmentManager that your fragment should receive a call to onCreateOptionsMenu(…).
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //*** Let the FragmentManager know that CrimeListFragment needs to receive menu callbacks ***
        setHasOptionsMenu(true);

    }

    //This method is called when a fragment is attached to an activity, whether is was retained or not
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //This means that the hosting activity must implement CrimeListFragment.Callback
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*
          There are a few built-in LayoutManagers to choose from, and you can find more as third-party libraries. You will use the LinearLayoutManager,
          which will position the items in the list vertically. Later on in this book, you will use GridLayoutManager to arrange items in a grid instead.
         */
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Get the saved instanceState
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    /*
    If the other activity is transparent, your activity may just be paused.
    If your activity is paused and your update code is in onStart(), then the list will not be reloaded.
    In general, onResume() is the safest place to take action to update a fragment’s view.
     */
    @Override
    public void onResume() {
        super.onResume();

        //Do this to be able to see your changes when you hit the goBack button
        updateUI();
    }

    //You set the variable to null here because afterward you cannot access the activity or count on the activity continuing to exist.
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //sve in outState subtitle visibility
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    /*
        * The RecyclerView’s only responsibilities are recycling TextViews and positioning them on the screen.
        * To get the TextViews in the first place, it works with two classes that you will build in a moment: an Adapter subclass and a ViewHolder subclass.
        * The class Adapter extends ViewHolder class
        * Note 1 *1
        */
    public void updateUI() {

        //Get crimes list
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            //Refresh its view of crimeLab
            mAdapter.setCrimes(crimes);
            //call notifyDataSetChanged() if the CrimeAdapter is already set up.
            mAdapter.notifyDataSetChanged();
        }

        //The number of crimes in the subtitle will be updated to reflect the new number if crimes.
        updateSubtitle();

    }

    private class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        private Crime mCrime;

        public CrimeHolder(View itemView) {
            super(itemView);

            //Register a callback to be invoked when this view is clicked. If this view is not clickable, it becomes clickable.
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }

        //Binding viewholders to data from the model layer
        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View v) {

            //Toast.makeText(getActivity(), mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();

            //Intent intent = new Intent(getActivity(), CrimeActivity.class);

            //Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            //startActivity(intent);
            mCallbacks.onCrimeSelected(mCrime);

            //Use this when you are expecting results back from an activity
            //startActivityForResult( intent, REQUEST_CRIME);

        }
    }

    /*
    Returning results from a fragment is a bit different. A fragment can receive a result from an activity, but it cannot have its own result.
    Only activities have results. So while Fragment has its own startActivityForResult(…) and onActivityResult(…) methods,
    it does not have any setResult(…) methods. Instead, you tell the host activity to return a value. Like this:

        public class CrimeFragment extends Fragment {
        ...
            public void returnResult() {
                getActivity(). setResult( Activity.RESULT_OK, null);
            }
        }
     */


    //Use this to get your results back from and activity
   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME) {

        }
    }*/

    /*
        * First, the RecyclerView asks how many objects are in the list by calling the adapter’s getItemCount() method.
        * Then the RecyclerView calls the adapter’s createViewHolder( ViewGroup, int) method to create a new ViewHolder,
        * along with its juicy payload: a View to display.
        * Finally, the RecyclerView calls onBindViewHolder( ViewHolder, int). The RecyclerView will pass a ViewHolder into this method along with the position.
        * The adapter will look up the model data for that position and bind it to the ViewHolder’s View.
        * To bind it, the adapter fills in the View to reflect the data in the model object.
        * Here the class Adapter extends ViewHolder class
        * */
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        /*
        onCreateViewHolder is called by the RecyclerView when it needs a new View to display an item.
        In this method, you create the View and wrap it in a ViewHolder.
        You need to Override these 3 methods
         */
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, viewGroup, false);

            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder crimeHolder, int i) {
            Crime crime = mCrimes.get(i);
            crimeHolder.bindCrime(crime);
        }

        //This method swap out the crimes it displays. Page 271
        //It allows the information to be refresh when clicking the back button
        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }

    /**** Use these two methods for the menu ****/
    //Too make toolbar compatible with older versions you need to extends CrimePagerActivity and SingleFragmentActivity
    // to this class AppCompatActivity
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Inflate the menu
        inflater.inflate(R.menu.fragment_crirme_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                //Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                //startActivity(intent);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
    /**************** End ****************************/

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        //String subtitle = getString(R.string.subtitle_format, crimeCount);

        //It shows if the quantity is singular or plural
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        //respect the mSubtitleVisible member variable when showing or hiding the subtitle in the toolbar.
        //This will hide the subtitle that is at the left side
        if (!mSubtitleVisible) {
            subtitle = null;
        }

        /* Next, the activity that is hosting the CrimeListFragment is cast to an AppCompatActivity. CriminalIntent uses the AppCompat library,
        * so all activities will be a subclass of AppCompatActivity, which allows you to access the toolbar. */
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
}

/*
*1 Adapters
    RecyclerView does not create ViewHolders itself. Instead, it asks an adapter.
    An adapter is a controller object that sits between the RecyclerView and the data set that the RecyclerView should display.
    The adapter is responsible for:
    - creating the necessary ViewHolders
    - binding ViewHolders to data from the model layer
 */

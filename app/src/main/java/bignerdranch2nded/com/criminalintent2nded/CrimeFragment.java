package bignerdranch2nded.com.criminalintent2nded;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private String mPhoneNumber;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Callbacks mCallbacks;
    private CallbacksUpdateDetails mCallbacksUpdateDetails;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {

        void onCrimeUpdated(Crime crime);
        //void onCrimeSelected(Crime crime);
    }

    public interface CallbacksUpdateDetails {

        void onUpdatedDetails(Crime crime);
    }

        /*
    * When the hosting activity needs an instance of that fragment, you have it call the newInstance() method rather than calling the constructor directly.
    * The activity can pass in any required parameters to newInstance(�) that the fragment needs to create its arguments.
    */
    public static CrimeFragment newInstance(UUID crimeId) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //This means that the hosting activity must implement CrimeListFragment.Callback
        mCallbacks = (Callbacks)activity;

        /*try {
            mCallbacksUpdateDetails = (CallbacksUpdateDetails) activity;
        }catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + e.getMessage());
        }*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // *** Let the FragmentManager know that CrimeFragment needs to receive menu callbacks ***
        //Without this the menu doesn't shows up
        setHasOptionsMenu(true);

        //mCrime = new Crime();

        //Retrieving the data from the intent. Not recommended
        /***************************************/
        //UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);

        //Retrieving the data from the arguments. It is a better way
        /***************************************/
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        //We get the photo file and stored it in this field
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (getActivity() == null) {
                    return;
                }

                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Like all fragments, instances of DialogFragment are managed by the FragmentManager of the hosting activity.
                FragmentManager manager = getFragmentManager();
                //DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                //***Set the target fragment to pass the date from the DatePickerFragment to CrimeFragment ***
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                //It creates a chooser title
                i = Intent.createChooser(i, getString(R.string.send_report));

                startActivity(i);
            }
        });

        //This implicit intent will have an action and a location where the relevant data can be found. Page 283
        //final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        final Intent pickContact = new Intent(Intent.ACTION_PICK, PhoneCONTENT_URI);

        //Test purposes. In case user doesn't have contact application.
        //pickContact.addCategory(Intent.CATEGORY_HOME);

        mSuspectButton = (Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //You use this method because you expect a result back from the started activity
                //You will see the result of this in onActivityResult() below
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallSuspectButton = (Button)v.findViewById(R.id.crime_call_suspect);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "tel:" + mPhoneNumber;

                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                startActivity(i);
            }
        });


        //This is used to avoid the app crash if it doesn't have contact application
        //PackageManager knows about all the components installed on your Android device, including all of its activities.
        // (You will run into the other components later on in this book.) By calling resolveActivity( Intent, int),
        // you ask it to find an activity that matches the Intent you gave it. The MATCH_DEFAULT_ONLY flag restricts
        // this search to activities with the CATEGORY_DEFAULT flag. Page 287
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);

        //This intent lunches the camera
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Check if the file and the camera exists
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;

        //Disable if the file and the camera doesn't exist
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            //uri is the path of the file. It creates a string that let the app know where the camera save this photo
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        //Your image will be saved to a file on the filesystem for you to use
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        updatePhotoView();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);

            updateCrime();
            updateDate();

        } else if (requestCode == REQUEST_CONTACT && data != null) {

            Uri contactUri = data.getData();

            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            // Perform your query - the contactUri is like a "where"
            // clause here
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor c = resolver
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name.
                c.moveToFirst();

                String suspect = c.getString(0);
                mPhoneNumber = c.getString(1);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);

                updateCrime();

            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {

            //if (resultCode == getActivity().RESULT_OK)

            updateCrime();
            updatePhotoView();

            /*Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(mPhotoFile), "image/jpeg");

            startActivity(i);*/
        }
    }

    private void updateCrime() {

        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {

        String solvedString = null;

        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();

        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }


    //*** Create menu ***
    //Don't forget to add in onCreate method this setHasOptionsMenu(true); to make the menu works
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;*/
            case R.id.menu_item_delete_crime:
                CrimeLab crimeLab = CrimeLab.get(getActivity());
                crimeLab.deleteCrime(mCrime);
                //crimeLab.saveCrimes();

                getActivity().finish();
                //updateCrime();
                //mCallbacksUpdateDetails.onUpdatedDetails(mCrime);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    ////*** End Create menu ***

    private void updatePhotoView() {

        /*if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Picasso.with(getActivity()).load(mPhotoFile).into(mPhotoView);
        }*/

        String filePath = mPhotoFile.getPath();
        Bitmap bitmap = null;

        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {

            bitmap = PictureUtils.scaleDownAndRotatePic(filePath);
            //bitmap = PictureUtils.getScaledBitmap(filePath, getActivity());

            //mPhotoView.setImageBitmap(bitmap);
            Picasso.with(getActivity()).load(mPhotoFile).into(mPhotoView);
        }
    }
}

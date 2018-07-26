package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static android.view.View.OnClickListener;
import static com.bignerdranch.android.criminalintent.CrimeLab.*;

public class CrimeListFragment extends android.support.v4.app.Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private ConstraintLayout mEmptyView;
    private Button mNewCrimeButton;
    private static final String SAVED_SUBTITLE_VISIBLE ="subtitle";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view
                .findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        mEmptyView = (ConstraintLayout) view.findViewById(R.id.empty_layout);

        mNewCrimeButton = (Button) view.findViewById(R.id.crime_button);
        mNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCrime();
            }
        });


        updateUI();

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
            }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }
    private void updateUI(){
        CrimeLab crimeLab = get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();


        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        mEmptyView.setVisibility(View.VISIBLE);
        if (crimes.size() > 0 ) {
            mEmptyView.setVisibility(View.INVISIBLE);
        }
        updateSubtitle();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity
                        .newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle(){
        CrimeLab crimeLab = get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural,crimeCount, crimeCount);

        if (!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
    private class CrimeHolder extends RecyclerView.ViewHolder
             implements OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;



        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solveImage);


        }

        private Crime mCrime;

        public void bind(Crime crime){
            mCrime = crime;
            DateFormat mDateDateFormat = android.text.format.DateFormat.getMediumDateFormat(getActivity());
            DateFormat mTimeDateFormat = android.text.format.DateFormat.getTimeFormat(getActivity());
            Date date = crime.getDate();
            String dateString = String.format("%s, %s %s",
                    android.text.format.DateFormat.format("EEE", date),
                    mDateDateFormat.format(date),
                    mTimeDateFormat.format(date));
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(dateString);
            mSolvedImageView.setVisibility(crime.isSolved()? View.VISIBLE : View.GONE);






        }


        @Override
        public void onClick(View view) {
           Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
           startActivity(intent);
        }
    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new CrimeHolder(layoutInflater, parent);
        }
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position){
            Crime crime = mCrimes.get(position);
            holder.bind(crime);

        }
        @Override
        public int getItemCount(){
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }


    }
    private void addCrime() {
        Crime crime = new Crime();
        get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
        startActivity(intent);
    }


}

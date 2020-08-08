package com.example.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

//Fragment which shows the search history list
public class HistoryFragment extends Fragment {

    //sharedpreferences
    private SharedPreferences sharedPreferences;

    //history adapter object
    private HistoryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the default shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());

        //gets the current search history
        //list of the search history
        ArrayList<String> searchHistory = getSearchHistory();

        //initialize the adapter with the search history
        adapter = new HistoryAdapter(getActivity(), searchHistory);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the history fragment
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //gets the list view of the search history
        ListView listView = view.findViewById(R.id.search_history_list);

        //set the listview adapter
        listView.setAdapter(adapter);

        //gets the delete button
        Button deleteButton = getActivity().findViewById(R.id.delete_fragment);

        //remove this fragment when delete button is pressed
        deleteButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit());
    }


    //this method gets the current search history from shared preferences
    private ArrayList<String> getSearchHistory () {

        //gets the hashset of search history from shared preferences
        //string used to get the search history from shared preferences
        String SEARCH = "SEARCH";
        HashSet<String> searchHistorySet = (HashSet<String>) sharedPreferences.getStringSet(SEARCH, new HashSet<>());

        //converts hashset to arraylist and returns arraylist
        return new ArrayList<>(searchHistorySet);
    }
}

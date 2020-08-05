package com.example.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class HistoryFragment extends Fragment {

    ArrayList<String> searchHistory;

    SharedPreferences sharedPreferences;

    HistoryAdapter adapter;

    private final String SEARCH = "SEARCH";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        searchHistory = getSearchHistory();
        adapter = new HistoryAdapter(getActivity(), searchHistory);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.search_history_list);
        listView.setAdapter(adapter);
    }

    private ArrayList<String> getSearchHistory () {

        HashSet<String> searchHistorySet = (HashSet<String>) sharedPreferences.getStringSet(SEARCH, new HashSet<>());

        return new ArrayList<>(searchHistorySet);
    }
}

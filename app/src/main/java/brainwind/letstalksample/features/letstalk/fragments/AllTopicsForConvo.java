package brainwind.letstalksample.features.letstalk.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import brainwind.letstalksample.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllTopicsForConvo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllTopicsForConvo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONVO_ID = "param1";
    private static final String IS_STANDALONE = "param3";
    // TODO: Rename and change types of parameters
    private String conversation_id;
    private boolean is_standalone;

    public AllTopicsForConvo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param conversation_id Parameter 1.
     * @param is_standalone Parameter 2.
     * @return A new instance of fragment AllTopicsForConvo.
     */
    // TODO: Rename and change types and number of parameters
    public static AllTopicsForConvo newInstance(String conversation_id, boolean is_standalone) {
        AllTopicsForConvo fragment = new AllTopicsForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putBoolean(IS_STANDALONE, is_standalone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            conversation_id = getArguments().getString(CONVO_ID);
            is_standalone = getArguments().getBoolean(IS_STANDALONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_all_topics_for_convo,
                container,
                false);
        return view;
    }
}
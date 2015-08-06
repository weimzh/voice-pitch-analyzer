package de.lilithwittmann.voicepitchanalyzer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lilithwittmann.de.voicepitchanalyzer.R;
import de.lilithwittmann.voicepitchanalyzer.models.PitchRange;
import de.lilithwittmann.voicepitchanalyzer.models.Recording;
import de.lilithwittmann.voicepitchanalyzer.models.database.RecordingDB;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class RecordingListFragment extends Fragment implements AbsListView.OnItemClickListener {
    private List<Recording> recordings = new ArrayList<Recording>();
    private OnFragmentInteractionListener listener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView listView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordingListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecordingDB recordingDB = new RecordingDB(getActivity());
        this.recordings = recordingDB.getRecordings();

        this.adapter = new ArrayAdapter<Recording>(getActivity(),
                android.R.layout.simple_list_item_activated_2, android.R.id.text2, this.recordings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                super.getView(position, convertView, parent);

                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(
                            android.R.layout.simple_list_item_activated_2, parent, false);
                }

                TextView largeText = (TextView) convertView.findViewById(android.R.id.text1);
                TextView smallText = (TextView) convertView.findViewById(android.R.id.text2);

                Recording record = this.getItem(position);
                PitchRange range = record.getRange();

                largeText.setText(record.getDisplayDate(getContext()));
                smallText.setText(String.format(getResources().getString(R.string.min_max_avg),
                        Math.round(range.getMin()), Math.round(range.getMax()),
                        Math.round(range.getAvg())));

                return convertView;
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording_list, container, false);

        // Set the adapter
        listView = (AbsListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        // Set OnItemClickListener so we can be notified on item clicks
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RecordingDB recordingDB = new RecordingDB(getActivity());
        this.recordings.clear();
        this.recordings.addAll(recordingDB.getRecordings());
        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != listener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            listener.onFragmentInteraction(this.recordings.get(position).getId());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = listView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(long recordID);
    }
}
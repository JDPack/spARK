package edu.uark.spARK;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.graphics.Color;
import android.view.View.OnClickListener;


public class NewsFeed_Fragment extends Fragment {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";

	public static NewsFeed_Fragment newInstance(String param1, String param2) {
		NewsFeed_Fragment fragment = new NewsFeed_Fragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public NewsFeed_Fragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	// TODO: Rename method, update argument and hook method into UI event

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);

	    View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
	    ListView listView = (ListView) view.findViewById(R.id.ListView1);
	    final String[] mListTitles = getResources().getStringArray(R.array.nav_drawer_title_array);
	    
	    // An invisible view added as a header to the list and clicking it leads to the mapfragment
	    TextView invisibleView = new TextView(inflater.getContext());
	    invisibleView.setBackgroundColor(Color.TRANSPARENT);
	    invisibleView.setHeight(300);
	    invisibleView.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	//set focus to the map fragment
	                                                                        }
	    });
	    listView.addHeaderView(invisibleView);
	    listView.setAdapter(new NavListArrayAdapter(getActivity().getApplicationContext(), R.layout.drawer_list_item, mListTitles));

		return view;
	}
	
	    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

}

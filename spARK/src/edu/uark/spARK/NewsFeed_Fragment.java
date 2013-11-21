package edu.uark.spARK;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import edu.uark.spARK.JSONQuery.AsyncResponse;
import edu.uark.spARK.PullToRefreshListView.OnRefreshListener;
import edu.uark.spARK.entities.Comment;
import edu.uark.spARK.entities.Content;
import edu.uark.spARK.entities.Discussion;
import edu.uark.spARK.entities.User;


public class NewsFeed_Fragment extends Fragment implements AsyncResponse {
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";
    private static final String TAG_SUCCESS = "success";
	private static final String TAG_DISCUSSIONS = "discussions";
	private static final String TAG_COMMENTS = "comments";
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_BODY = "body";
	private static final String TAG_TIMESTAMP = "timestamp";
	private static final String TAG_USER_ID = "userid";
	private static final String TAG_USER_NAME = "username";
	
	private NewsFeedArrayAdapter mAdapter; 
    private static PullToRefreshListView mListView;
    private static Bundle args;
    
    public static ArrayList<Content> arrayListContent = new ArrayList<Content>();
    private JSONArray discussions = null;
    private JSONArray comments = null;
    
	public static NewsFeed_Fragment newInstance(String param1, String param2) {
		NewsFeed_Fragment fragment = new NewsFeed_Fragment();
		args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	
	public NewsFeed_Fragment() {
		
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    mAdapter = new NewsFeedArrayAdapter(getActivity(), R.layout.discussion_list_item, arrayListContent, this);		
		mListView.setAdapter(mAdapter);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		if (getArrayListContent().size() == 0)
			loadContent();
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		//View v = super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.list_feed, container, false);
//		mListView = new PullToRefreshListView(container.getContext());
		mListView = (PullToRefreshListView) v.findViewById(R.id.pullToRefreshListView);
		
//		mListView = new PullToRefreshListView(inflater.getContext());
//		mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    //set mapHeader clicklistener so the listview can be hidden
	    mListView.mapHeader.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
        	//set focus to the map fragment
        	MapView_Fragment f = (MapView_Fragment) getFragmentManager().findFragmentById(R.id.map);
        	FragmentManager fm = getFragmentManager();
        	FragmentTransaction ft = fm.beginTransaction();
        	
        	ft.addToBackStack(null);
        	//animations are ordered (enter, exit, popEnter, popExit)
        	ft.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, 
        			R.animator.slide_up, R.animator.slide_down)
        	.hide(getFragmentManager().findFragmentById(R.id.fragment_frame)).commit();                                                                      
        }
    });
        mListView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                    // Your code to refresh the list contents goes here

                    // for example:
                    // If this is a webservice call, it might be asynchronous so
                    // you would have to call listView.onRefreshComplete(); when
                    // the webservice returns the data
                    loadContent();
                    
                    // Make sure you call listView.onRefreshComplete()
                    // when the loading is done. This can be done from here or any
                    // other place, like on a broadcast receive from your loading
                    // service or the onPostExecute of your AsyncTask.

                    // For the sake of this sample, the code will pause here to
                    // force a delay when invoking the refresh
                    mListView.postDelayed(new Runnable() {

                            
                            @Override
                            public void run() {
                                    mListView.onRefreshComplete();
                            }
                    }, 2000);
            }
    });
        
		return v;

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
	
	public void loadContent() {
		
		SharedPreferences preferences = this.getActivity().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE);
		String currentUser = preferences.getString("currentUser", "");
		
		JSONQuery jquery = new JSONQuery(this);
		jquery.execute(ServerUtil.URL_LOAD_ALL_POSTS, currentUser);
	}
	
	@Override
	public void processFinish(JSONObject result) {
		arrayListContent.clear();
		try {
			int success = result.getInt(TAG_SUCCESS);

			if (success == 1) {
				// Get Array of discussions
				discussions = result.getJSONArray(TAG_DISCUSSIONS);

				for (int i = 0; i < discussions.length(); i++) {
					JSONObject discussion = discussions.getJSONObject(i);

					int discussion_id = Integer.parseInt(discussion.getString(TAG_ID));
					String user_id = discussion.getString(TAG_USER_ID).trim();
					String username = discussion.getString(TAG_USER_NAME).trim();
					String title = discussion.getString(TAG_TITLE).trim();
					String body = discussion.getString(TAG_BODY).trim();
					Date d_date = Timestamp.valueOf(discussion.getString(TAG_TIMESTAMP).trim());
					    		
					comments = discussion.getJSONArray(TAG_COMMENTS);
					List<Comment> commentsList = new ArrayList<Comment>();
					for (int j = 0; j < comments.length(); j++) {
						List<Comment> list = new ArrayList<Comment>();
						JSONObject comment = comments.getJSONObject(j);

						String comment_id = comment.getString(TAG_ID).trim();
						String userid = comment.getString(TAG_USER_ID).trim();
						String user = comment.getString(TAG_USER_NAME).trim();
						String comment_body = comment.getString(TAG_BODY).trim();
						Date c_date = Timestamp.valueOf(discussion.getString(TAG_TIMESTAMP).trim());
						
						//String comment_timestamp = comment.getString(TAG_TIMESTAMP).trim();
						User u = new User(Integer.parseInt(userid), user, null);
						Comment c = new Comment(Integer.parseInt(comment_id), comment_body, u, c_date);
						
						commentsList.add(c);
					}
					@SuppressWarnings("deprecation")
					Discussion d = new Discussion(discussion_id, title, body, new User(Integer.parseInt(user_id), username, null), d_date, commentsList);
					arrayListContent.add(d);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}	
	}
	
	public PullToRefreshListView getListView() {
		return mListView;
	}
	
	public NewsFeedArrayAdapter getListAdapter() {
		return mAdapter;
	}

	public ArrayList<Content> getArrayListContent() {
		return arrayListContent;
	}

	public void setArrayListContent(ArrayList<Content> arrayListContent) {
		this.arrayListContent = arrayListContent;
	}
	
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        Log.i("FragmentList", "Item clicked: " + id);
//    }

	//onActivityResult which is received by the fragment
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Result not being pass to either activity or fragment for some reason after finishing commentActivity
		if (requestCode == 1) {
			 if(resultCode == Activity.RESULT_OK){
		         //notify array adapter of new value
				 Discussion d = (Discussion) intent.getSerializableExtra("Object");
				 int p = intent.getIntExtra("position", -1);
				 	if (p > -1) {
				 		getArrayListContent().set(p, d);
				 		getListAdapter().notifyDataSetChanged();
				 	}
		         
		         //load from server here like in hw???
//		         ContentResolver cr = getContentResolver();
//		         ContentValues values = new ContentValues();
//		         
//		         values.put(RazorSquareContentProvider.KEY_USER, username);
//		         values.put(RazorSquareContentProvider.KEY_DESC, e.getDescription());
//		         values.put(RazorSquareContentProvider.KEY_TIMESTAMP, e.getCreatedDateAsInt());
//		         cr.insert(RazorSquareContentProvider.CONTENT_URI, values);
//		         getLoaderManager().restartLoader(0, null, this);
		         
			 }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Handle cancel
        }
	}
}

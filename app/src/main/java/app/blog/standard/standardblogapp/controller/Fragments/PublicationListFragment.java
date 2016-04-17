package app.blog.standard.standardblogapp.controller.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.controller.adapter.MyPublicationRecyclerViewAdapter;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.util.NetworkHelper;
import app.blog.standard.standardblogapp.model.util.PublicationHelper;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PublicationListFragment extends Fragment {

    //region variables
    private final String TAG = this.getClass().getSimpleName();
    private int CURRENT_SKIP = 0;

    private OnListFragmentInteractionListener mListener;
    private PublicationHelper publicationHelper;
    private NetworkHelper networkHelper;

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView mRecyclerView;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private ArrayList<Publication> aPublications;
    private String category;
    //endregion

    //region Constructor
    public PublicationListFragment() {}

    public static PublicationListFragment newInstance() {
        PublicationListFragment fragment = new PublicationListFragment();
        Bundle args = new Bundle();
        args.putString("category", null);
        fragment.setArguments(args);
        return fragment;
    }

    public static PublicationListFragment newInstance(String category) {
        PublicationListFragment fragment = new PublicationListFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Lifecycle methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        category = getArguments().getString("category");
        publicationHelper = PublicationHelper.getInstance(getActivity());
        networkHelper = new NetworkHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publication_list, container, false);

        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        final LinearLayoutManager mLayoutManager;

        aPublications = getNextPublications();

        TypedValue typed_value = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize,
                typed_value, true);
        swipeContainer.setProgressViewOffset(false, 0, getResources()
                .getDimensionPixelSize(typed_value.resourceId));

        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setAdapter(new MyPublicationRecyclerViewAdapter(context,
                aPublications, mListener));
        mRecyclerView.invalidate();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //FIXME This shit is bugged AF
//                if(dy > 0) {
//                    visibleItemCount = mRecyclerView.getChildCount();
//                    totalItemCount = mLayoutManager.getItemCount();
//                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
//
//                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                        Log.i(TAG, "Reached end of the list");
//                        aPublications.addAll(getNextPublications());
//
//                        mRecyclerView.getAdapter().notifyDataSetChanged();
//                    }
//                }
//                if(!recyclerView.canScrollVertically(1)) {
//                    aPublications.addAll(getNextPublications());
//                    mRecyclerView.getAdapter().notifyDataSetChanged();
//                }
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    CURRENT_SKIP = 0;
                    new OnlineSync(getActivity()).execute();
                }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!publicationHelper.hasPublications() && networkHelper.hasPreferedConnection()) {
            CURRENT_SKIP = 0;
            new OnlineSync(getActivity()).execute();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    //endregion

    //region Publication list
    private ArrayList<Publication> getNextPublications() {
        final int tempSkip = CURRENT_SKIP;
        CURRENT_SKIP += PublicationHelper.DEFAULT_PAGE_SIZE;

        //FIXME Uncomment this and implement endless scroll!

        if(category == null) {
//            return publicationHelper.getAllPublicationsFromDatabase(tempSkip,
//                    PublicationHelper.DEFAULT_PAGE_SIZE);
            return publicationHelper.getAllPublicationsFromDatabase();
        } else {
//            return publicationHelper.getAllByCategory(category, tempSkip,
//                    PublicationHelper.DEFAULT_PAGE_SIZE);
            return publicationHelper.getAllByCategory(category);
        }
    }
    //endregion

    //region interaction
    /**
     *
     * @param category Category name, or null for ALL.
     */
    public void switchCategory(String category) {
        this.category = category;
        CURRENT_SKIP = 0;

        aPublications.clear();
        aPublications.addAll(getNextPublications());

        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.invalidate();
    }

    public void sync() {
        if(!networkHelper.hasPreferedConnection()) return;

        CURRENT_SKIP = 0;
        new OnlineSync(getActivity()).execute();
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Publication item);
    }
    //endregion

    //region OnlineSync
    private class OnlineSync extends AsyncTask<Void, Void, Boolean> {

        private Context mContext;

        public OnlineSync(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeContainer.setRefreshing(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            swipeContainer.setRefreshing(false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return publicationHelper.sync();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            swipeContainer.setRefreshing(false);

            if(CURRENT_SKIP == 0) aPublications.clear();

            aPublications.addAll(getNextPublications());
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mRecyclerView.invalidate();
        }

    }
    //endregion
}

package com.sldroid.mecdic_v21.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.sldroid.mecdic_v21.R;
import com.sldroid.mecdic_v21.ResultActivity;
import com.sldroid.mecdic_v21.dbms.TestAdapter;
import com.sldroid.mecdic_v21.extra.Word;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EngFragment extends Fragment {

    private FastScrollRecyclerView recyclerView;
    private TestAdapter dbHelper;
    private RecyclerViewAdapter recyclerAdapter;
    private ArrayList<Word> words, wordsCopy;
    private String dbTable = "enDic";

    public EngFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new TestAdapter(getContext());
        dbHelper.createDatabase();
        dbHelper.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eng, container, false);

        recyclerView = (FastScrollRecyclerView)view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        words = dbHelper.getAllWordToArray(dbTable);
        recyclerAdapter = new RecyclerViewAdapter(words);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

        return view;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>
            implements FastScrollRecyclerView.SectionedAdapter{

        private final ArrayList<Word> mDataSet;

        public RecyclerViewAdapter(ArrayList<Word> wordSet){
            this.mDataSet = wordSet;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.en_card_row, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
            holder.txtId.setText(String.valueOf(mDataSet.get(position).get_id()));
            holder.txtWord.setText(mDataSet.get(position).getWord());
            holder.txtDef.setText(mDataSet.get(position).getDefinition());

            if (dbHelper.isFavourite(dbTable, String.valueOf(mDataSet.get(position).get_id())))
                holder.imgFav.setImageResource(R.drawable.fav_on);
            else
                holder.imgFav.setImageResource(R.drawable.fav_off);

            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SetFavourite(holder).execute(position);
                }
            });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ResultActivity.class);
                    intent.putExtra("fName", "en");
                    intent.putExtra("id", mDataSet.get(position).get_id());
                    intent.putExtra("word", mDataSet.get(position).getWord());
                    intent.putExtra("definition", mDataSet.get(position).getDefinition());
                    intent.putExtra("favourite", mDataSet.get(position).getFavourite());
                    startActivity(intent);
                    /*
                    * used android:launchMode="singleTask"  in AndroidManifest
                    * to achieve fast BackNavigation between two activity
                    */
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        @NonNull
        @Override
        public String getSectionName(int position) {
            return mDataSet.get(position).getWord().substring(0,1).toUpperCase();
        }

        public class SetFavourite extends AsyncTask<Integer, Void, Boolean>{

            RecyclerViewHolder holder;

            public SetFavourite(RecyclerViewHolder mHolder){
                this.holder = mHolder;
            }
            @Override
            protected Boolean doInBackground(Integer... params) {
                Boolean state;

                if (dbHelper.isFavourite(dbTable, String.valueOf(mDataSet.get(params[0]).get_id()))){
                    state = false;
                    dbHelper.favUpdate(dbTable, String.valueOf(mDataSet.get(params[0]).get_id()),0);
                }
                else{
                    state = true;
                    dbHelper.favUpdate(dbTable, String.valueOf(mDataSet.get(params[0]).get_id()),1);
                }
                return state;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result)
                    holder.imgFav.setImageResource(R.drawable.fav_on);
                else
                    holder.imgFav.setImageResource(R.drawable.fav_off);
            }
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView txtId, txtWord, txtDef;
        ImageView imgFav;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            txtId = (TextView) itemView.findViewById(R.id.txt_id);
            txtWord = (TextView) itemView.findViewById(R.id.txt_word);
            txtDef = (TextView) itemView.findViewById(R.id.txt_def);
            imgFav = (ImageView) itemView.findViewById(R.id.img_fav);
        }
    }

    public void textSearch(String inputTxt){
        new SearchTask().execute(inputTxt);
    }

    public class SearchTask extends AsyncTask<String, Void, ArrayList<Word>> {

        @Override
        protected ArrayList<Word> doInBackground(String... params) {
            String query = params[0].toLowerCase();

            final ArrayList<Word> filteredWordList = new ArrayList<>();
            for (Word word : words) {
                final String text = word.getWord().toLowerCase();
                if (text.contains(query)) {
                    filteredWordList.add(word);
                }
            }
            wordsCopy = filteredWordList;
            return wordsCopy;
        }

        @Override
        protected void onPostExecute(ArrayList<Word> result) {
            super.onPostExecute(result);
            recyclerAdapter = new RecyclerViewAdapter(result);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private class GetAllWords extends AsyncTask<Cursor, Void, ArrayList<Word>> {

        @Override
        protected ArrayList<Word> doInBackground(Cursor... params) {
            words = dbHelper.getAllWordToArray(dbTable);
            wordsCopy = words;
            return words;
        }

        @Override
        protected void onPostExecute(ArrayList<Word> result) {
            super.onPostExecute(result);
            recyclerAdapter =  new RecyclerViewAdapter(result);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}

package com.sldroid.mecdic_v21.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.sldroid.mecdic_v21.R;
import com.sldroid.mecdic_v21.dbms.TestAdapter;
import com.sldroid.mecdic_v21.extra.Word;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class SiFragment extends Fragment {

    private TestAdapter dbHelper;
    private ListView listView;
    private ArrayList<Word> words;
    private WordAdapter wordAdapter;

    public SiFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_si, container, false);
        setListView(view);
        return view;
    }

    private void setListView(View view) {
        Button btnSubmitEng = (Button) view.findViewById(R.id.btnSubmitEng);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setEmptyView(btnSubmitEng);

        /*words = dbHelper.getAllWordToArray("siDic");
        wordAdapter = new WordAdapter(getContext(),words);
        listView.setAdapter(wordAdapter);*/

        new GetAllWords().execute();
    }

    public void textSearch(String inputTxt){
        new SearchTask().execute(inputTxt);
    }

    public void resetSearch(){
        wordAdapter = new WordAdapter(getContext(), words);
        listView.setAdapter(wordAdapter);
        wordAdapter.notifyDataSetChanged();
    }

    public class SearchTask extends AsyncTask<String, Void, ArrayList<Word>>{

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
            return filteredWordList;
        }

        @Override
        protected void onPostExecute(ArrayList<Word> word_List) {
            super.onPostExecute(words);
            wordAdapter = new WordAdapter(getContext(), word_List);
            listView.setAdapter(wordAdapter);
            wordAdapter.notifyDataSetChanged();
        }
    }

    public class WordAdapter extends ArrayAdapter<Word> implements SectionIndexer {

        private final HashMap<String, Integer> alphaIndexer;
        private final String[] sections;

        // View lookup cache
        private class ViewHolder {
            TextView txtID, txtWord, txtDef;
            ImageButton imgFav;
        }

        public WordAdapter(Context context, ArrayList<Word> words) {
            super(context, R.layout.si_word_row, words);

            alphaIndexer = new HashMap<>();
            for (int i = 0; i < words.size(); i++)
            {
                String s = words.get(i).getWord().substring(0, 1).toUpperCase();
                if (!alphaIndexer.containsKey(s))
                    alphaIndexer.put(s, i);
            }

            Set<String> sectionLetters = alphaIndexer.keySet();
            ArrayList<String> sectionList = new ArrayList<>(sectionLetters);
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            for (int i = 0; i < sectionList.size(); i++)
                sections[i] = sectionList.get(i);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final Word user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            final ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.si_word_row, parent, false);
                viewHolder.txtID = (TextView) convertView.findViewById(R.id.txt_id);
                viewHolder.txtWord = (TextView) convertView.findViewById(R.id.txt_word);
                viewHolder.txtDef = (TextView) convertView.findViewById(R.id.txt_def);
                viewHolder.imgFav = (ImageButton) convertView.findViewById(R.id.img_fav);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.txtID.setText(user.get_id());
            viewHolder.txtWord.setText(user.getWord());
            viewHolder.txtDef.setText(user.getDefinition());

            if (user.getFavourite().equalsIgnoreCase("1"))
                viewHolder.imgFav.setImageResource(R.drawable.fav_on);
            else
                viewHolder.imgFav.setImageResource(R.drawable.fav_off);

            viewHolder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dbHelper.isFavourite("siDic", user.get_id())){
                        viewHolder.imgFav.setImageResource(R.drawable.fav_off);
                        dbHelper.favUpdate("siDic", user.get_id(),0);
                    }
                    else{
                        viewHolder.imgFav.setImageResource(R.drawable.fav_on);
                        dbHelper.favUpdate("siDic", user.get_id(),1);
                    }
                    new GetAllWords().execute();
                }
            });

            // Return the completed view to render on screen
            return convertView;
        }

        public int getPositionForSection(int section)
        {
            try {
                return alphaIndexer.get(sections[section]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return section;
        }

        public int getSectionForPosition(int position)
        {
            for ( int i = sections.length - 1; i >= 0; i-- ) {
                if ( position >= alphaIndexer.get( sections[ i ] ) ) {
                    return i;
                }
            }
            return 0;
        }

        public Object[] getSections()
        {
            return sections;
        }
    }

    private class GetAllWords extends AsyncTask<Cursor, Void, ArrayList<Word>> {

        @Override
        protected ArrayList<Word> doInBackground(Cursor... params) {
            return dbHelper.getAllWordToArray("siDic");
        }

        @Override
        protected void onPostExecute(ArrayList<Word> result) {
            super.onPostExecute(result);
            words = result;
            wordAdapter =  new WordAdapter(getContext(), result);
            listView.setAdapter(wordAdapter);
        }
    }

}

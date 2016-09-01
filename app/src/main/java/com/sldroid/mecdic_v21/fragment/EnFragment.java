package com.sldroid.mecdic_v21.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.sldroid.mecdic_v21.R;
import com.sldroid.mecdic_v21.dbms.TestAdapter;

import static com.sldroid.mecdic_v21.R.layout.en_word_row;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnFragment extends Fragment {

    private TestAdapter dbHelper;
    private myCursorAdapter dataAdapter;

    public EnFragment() {
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
        View view = inflater.inflate(R.layout.fragment_en, container, false);
        setListView(view);
        return view;
    }

    private void setListView(View view) {
        Cursor cursor = dbHelper.getAllWord("enDic");

        String[] columns = new String[]{
                "word",
                "definition",
                "_id",
                "favourite"
        };

        int[] to = new int[]{
                R.id.txt_word,
                R.id.txt_def,
                R.id.txt_id
        };
        
        dataAdapter = new myCursorAdapter(
                getContext(), en_word_row,
                cursor,
                columns,
                to,
                0);

        Button btnSubmitEng = (Button) view.findViewById(R.id.btnSubmitEng);
        
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(dataAdapter);
        listView.setEmptyView(btnSubmitEng);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                final String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
                Toast.makeText(getContext(), word, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void textSearch(String searchTxt){
        String newStr = searchTxt.replace(" ","");
        dataAdapter.getFilter().filter(newStr);

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.getWordByName("enDic",constraint.toString());
            }
        });
    }
    
    private class myCursorAdapter extends SimpleCursorAdapter {
        
        public myCursorAdapter(Context context, int layout, 
                               Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }
        
        public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(en_word_row, parent, false);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            super.bindView(view, context, cursor);
            
            final ImageButton imgFav = (ImageButton) view.findViewById(R.id.img_fav);
            final String _id = cursor.getString(cursor.getColumnIndex("_id"));
            final Integer fav = Integer
                    .valueOf(cursor.getString(cursor.getColumnIndex("favourite")));

            if (fav == 1)
                imgFav.setImageResource(R.drawable.fav_on);
            else
                imgFav.setImageResource(R.drawable.fav_off);
                
            imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dbHelper.isFavourite("enDic", _id)){
                        imgFav.setImageResource(R.drawable.fav_off);
                        dbHelper.favUpdate("enDic", _id,0);
                    }
                    else{
                        imgFav.setImageResource(R.drawable.fav_on);
                        dbHelper.favUpdate("enDic", _id,1);
                    }
                }
            });
        }
    }
}

package com.sldroid.mecdic_v21;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sldroid.mecdic_v21.dbms.TestAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener{

    private String fName;
    private TextToSpeech tts;
    private String word;
    private TestAdapter dbHelper;
    private String id;
    private ImageButton imgFavResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new TestAdapter(this);
        dbHelper.createDatabase();
        dbHelper.open();

        Intent intent = getIntent();
        fName = intent.getStringExtra("fName");
        id = intent.getStringExtra("id");
        word = intent.getStringExtra("word");
        String definition = intent.getStringExtra("definition");

        TextView resultWord = (TextView)findViewById(R.id.resultWord);
        Button btnCopy = (Button)findViewById(R.id.btnCopy);
        Button btnPlay = (Button)findViewById(R.id.btnPlay);
        Button btnShare = (Button)findViewById(R.id.btnShare);
        imgFavResult = (ImageButton)findViewById(R.id.imgfavResult);
        LinearLayout layout = (LinearLayout)findViewById(R.id.btnPlayLyt);

        btnCopy.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        imgFavResult.setOnClickListener(this);

        new SetFavourite().execute();

        resultWord.setTextSize(20);

        if (word.length() > 1)
            resultWord.setText(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
        else
            resultWord.setText(word.toUpperCase());

        if (fName.equalsIgnoreCase("si")){
            btnPlay.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(new RecyclerViewAdapter(definition));
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public class SetFavourite extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            String table;
            if (fName.equalsIgnoreCase("si"))
                table = "siDic";
            else
                table = "enDic";
            return dbHelper.isFavourite(table, id);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean)
                imgFavResult.setImageResource(R.drawable.fav_on);
            else
                imgFavResult.setImageResource(R.drawable.fav_off);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCopy:
                setClipboard(getApplicationContext(),word);
                Toast.makeText(getApplicationContext(), "Copy to clipboard",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.btnPlay:
                textToSpeech(word);
                break;
            case  R.id.btnShare:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, word);
                startActivity(Intent.createChooser(share, "Share Word"));
                break;
            case  R.id.imgfavResult:
                if (dbHelper.isFavourite("enDic", id)){
                    imgFavResult.setImageResource(R.drawable.fav_off);
                    dbHelper.favUpdate("enDic", id,0);
                }
                else{
                    imgFavResult.setImageResource(R.drawable.fav_on);
                    dbHelper.favUpdate("enDic", id,1);
                }
                break;
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewViewHolder>{

        private final ArrayList<String> items;

        public RecyclerViewAdapter(String def){
            items = new  ArrayList<>(Arrays.asList(def.split("\\s*,\\s*")));
        }

        @Override
        public RecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.en_result_row, parent, false);
            RecyclerViewViewHolder holder = new RecyclerViewViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewViewHolder holder, final int position) {
            holder.resultDef.setText(items.get(position));
            holder.cardBtnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, items.get(position));
                    startActivity(Intent.createChooser(share, "Share Text"));
                }
            });
            holder.cardBtnCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClipboard(getApplicationContext(), items.get(position));
                    Toast.makeText(getApplicationContext(), "Copy to clipboard",
                            Toast.LENGTH_LONG).show();
                }
            });
            holder.cardBtnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech(items.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private class RecyclerViewViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout layout;
        public TextView resultDef;
        public Button cardBtnCopy, cardBtnPlay, cardBtnShare;

        public RecyclerViewViewHolder(View v) {
            super(v);

            resultDef = (TextView)v.findViewById(R.id.resultDef);
            cardBtnCopy = (Button)v.findViewById(R.id.cardBtnCopy);
            cardBtnPlay = (Button)v.findViewById(R.id.cardBtnPlay);
            cardBtnShare = (Button)v.findViewById(R.id.cardBtnShare);
            layout = (LinearLayout)v.findViewById(R.id.btnPlay_lyt);

            if (fName.equalsIgnoreCase("en")){
                cardBtnPlay.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
            }
        }
    }

    // copy text to clipboard
    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)getSystemService(context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)getSystemService(context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public void textToSpeech(final String s){

        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);

            tts=new TextToSpeech(ResultActivity.this, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS){
                        int result=tts.setLanguage(Locale.US);
                        if(result==TextToSpeech.LANG_MISSING_DATA ||
                                result==TextToSpeech.LANG_NOT_SUPPORTED){
                            Log.e("error", "This Language is not supported");
                        }
                        else{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                String utteranceId=this.hashCode() + "";
                                tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                            } else {
                                @SuppressWarnings("deprecation")
                                HashMap<String, String> map = new HashMap<>();
                                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
                                tts.speak(s, TextToSpeech.QUEUE_FLUSH, map);
                            }
                        }
                    }
                    else
                        Log.e("error", "Initialization Failed!");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(
                    getApplicationContext(),
                    "Can't start TTS at this moment",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onBackPressed();
    }
}

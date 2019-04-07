package com.austin.stockquotes;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private TextView symbolRes;
    private TextView nameRes;
    private TextView priceRes;
    private TextView timeRes;
    private TextView changeRes;
    private TextView rangeRes;

    static String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        symbolRes = (TextView) findViewById(R.id.symbolResult);
        nameRes = (TextView) findViewById(R.id.nameResult);
        priceRes = (TextView) findViewById(R.id.priceResult);
        timeRes = (TextView) findViewById(R.id.timeResult);
        changeRes = (TextView) findViewById(R.id.changeResult);
        rangeRes = (TextView) findViewById(R.id.rangeResult);
        SearchView searchView = (SearchView) findViewById(R.id.search2);

        searchView.setImeOptions(searchView.getImeOptions()| EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        if(savedInstanceState != null){
            SearchAsync async = new SearchAsync(savedInstanceState.getString("query"), (View)findViewById(R.id.activitymain), MainActivity.this);
            async.execute();
            searchView.setQuery(savedInstanceState.getString("query"), true);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                SearchAsync async = new SearchAsync(s, (View)findViewById(R.id.activitymain), MainActivity.this);
                async.execute();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Log.i("Query Text", s);
                MainActivity.query = s;
                return false;
            }
        });
        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));*/

        // old search
       /* Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("Search result",query);
        }*/

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("query", query);
    }

    static class SearchAsync extends AsyncTask<Void, Void, Stock>{

        private String query;
        private View view;
        private Context context;

        SearchAsync(String s, View v, Context c){
            this.query = s;
            this.view = v;
            this.context = c;
        }

        @Override
        protected Stock doInBackground(Void... voids) {

            Stock stock = new Stock(this.query);
            try {
                stock.load();
            }
            catch (FileNotFoundException f){
                Log.i("Failure", f.toString());
                return null;
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return stock;
        }

        @Override
        protected void onPostExecute(Stock stock) {
            super.onPostExecute(stock);

            if(stock == null){
                Toast toast = Toast.makeText(context, "This company could not be found.", Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                TextView symbolRes = (TextView) view.findViewById(R.id.symbolResult);
                TextView nameRes = (TextView) view.findViewById(R.id.nameResult);
                TextView priceRes = (TextView) view.findViewById(R.id.priceResult);
                TextView timeRes = (TextView) view.findViewById(R.id.timeResult);
                TextView changeRes = (TextView) view.findViewById(R.id.changeResult);
                TextView rangeRes = (TextView) view.findViewById(R.id.rangeResult);

                symbolRes.setText(stock.getSymbol());
                nameRes.setText(stock.getName());
                priceRes.setText(stock.getLastTradePrice());
                timeRes.setText(stock.getLastTradeTime());
                changeRes.setText(stock.getChange());
                rangeRes.setText(stock.getRange());


                // I was trying to make it so that I could pull an image of the company and display it but I then realized that
                // it wasn't going to be reasonable or even remotely do-able.
                /*ImgAsync async = new ImgAsync("https://logo.clearbit.com/google.com", (View)view.findViewById(R.id.activitymain), context);
                async.execute();*/


            }

        }

        // Just leaving this here for future reference
        /*static class ImgAsync extends AsyncTask<Void, Void, Drawable>{

            private String url;
            View view;
            private Context context;

            ImgAsync(String s, View v, Context c){
                this.url = s;
                this.view = v;
                this.context = c;
            }

            @Override
            protected Drawable doInBackground(Void... voids) {

                return LoadImageFromWebOperations(this.url);
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);

                ImageView img = (ImageView) view.findViewById(R.id.logo);

                if(drawable != null){
                    img.setImageDrawable(drawable);
                }

            }

            static Drawable LoadImageFromWebOperations(String url) {

                try {
                    InputStream is = (InputStream) new URL(url).getContent();
                    Drawable d = Drawable.createFromStream(is, "src name");
                    return d;
                } catch (Exception e) {
                    Log.i("fail",e.toString());
                    return null;
                }
            }


        }*/
    }
}

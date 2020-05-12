package com.example.projectapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private listAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final String BASE_URL = "https://api.jikan.moe/";
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private TextView titre;
    private TextView typ;
    private TextView ep;
    private TextView sco;
    private ImageView img;
    private EditText input;
    private int idAnime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gson = new GsonBuilder()
                .setLenient()
                .create();

        titre = (TextView) findViewById(R.id.title);
        typ = (TextView) findViewById(R.id.type);
        ep = (TextView) findViewById(R.id.ep);
        sco = (TextView) findViewById(R.id.score);
        img = (ImageView) findViewById(R.id.icon);

        //Pour la mise en cache
        sharedPreferences = getSharedPreferences("app_esiea", Context.MODE_PRIVATE);

        //Variables pour la recherche d'un animé
        Button button = (Button)findViewById(R.id.search);
        input = (EditText)findViewById(R.id.input);
        button.setOnClickListener(this);

        //recupere les data misent en cache
        List<Genres> genresList = getDataFromCache();

        if(genresList != null){
            printData();
            showList(genresList);
        }else {
            makeApiCall(32);
        }

        Intent intent = getIntent();
         idAnime = intent.getIntExtra("ID",0);
        if(idAnime != 0) {
            makeApiCall(idAnime);
        }
    }

    private void printData(){
        String s = sharedPreferences.getString("title",null);
        if(s != null) titre.setText(s);
        String url = sharedPreferences.getString("url",null);
        if(url != null) Picasso.get().load(url).into(img);
        String t = sharedPreferences.getString("type",null);
        if(t != null) typ.setText("Type : "+t);;
        String e = sharedPreferences.getString("episodes",null);
        if(e != null) ep.setText("Episodes : "+e);
        String sc = sharedPreferences.getString("score",null);
        if(sc != null) sco.setText("Score : "+sc);
    }

    private List<Genres> getDataFromCache() {
        String jsonGenres = sharedPreferences.getString("cle_string", null);
        if(jsonGenres == null){
            return null;
        }else {
            Type listType = new TypeToken<List<Genres>>() {}.getType();
            return gson.fromJson(jsonGenres, listType);
        }
    }

    private void showList(final List<Genres> genresList){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // define an adapter
        mAdapter = new listAdapter(genresList,null);
        recyclerView.setAdapter(mAdapter);

        /*ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        genresList.remove(viewHolder.getAdapterPosition());
                        mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/
    }

    private void makeApiCall(int id){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AnimeAPI animeAPI = retrofit.create(AnimeAPI.class);

        Call<Restanimereponse> call = animeAPI.getanimeresponse(id);
        call.enqueue(new Callback<Restanimereponse>() {
            @Override
            public void onResponse(Call<Restanimereponse> call, Response<Restanimereponse> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    String image_url = response.body().getImage_url();
                    String title = response.body().getTitle();
                    String type = response.body().getType();
                    Integer episodes = response.body().getEpisodes();
                    String score = response.body().getScore();
                    List<Genres> genres = response.body().getGenres();
                    Toast.makeText(getApplicationContext(),"API Succes",Toast.LENGTH_SHORT).show();

                    titre.setText(title);
                    typ.setText("Type : "+type);
                    ep.setText("Episodes : "+episodes.toString());
                    sco.setText("Score : "+score);
                    Picasso.get().load(image_url).into(img);

                    saveList(genres,title,image_url,type,episodes.toString(),score);
                    showList(genres);
                }else{
                    showError();
                }
            }

            @Override
            public void onFailure(Call<Restanimereponse> call, Throwable t) {
                showError();
            }
        });

    }

    private void callAPI(String name){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AnimeAPI animeAPI = retrofit.create(AnimeAPI.class);

        Call<AnimeList> call = animeAPI.getanimelist(name);
        call.enqueue(new Callback<AnimeList>() {
            @Override
            public void onResponse(Call<AnimeList> call, Response<AnimeList> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    List<Anime> anime = response.body().getResults();


                    Toast.makeText(getApplicationContext(),"API Succes",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
                    intent.putExtra("anime", (Serializable) anime);
                    startActivity(intent);

                }else{
                    showError();
                }
            }
            @Override
            public void onFailure(Call<AnimeList> call, Throwable t) {
                showError();
            }
        });
    }

    private void saveList(List<Genres> genresList,String title,String image_url,String type,String episodes,String score){
        String jsonString = gson.toJson(genresList);
        sharedPreferences
                .edit()
                .putString("type", type)
                .putString("episodes", episodes)
                .putString("score", score)
                .putString("url", image_url)
                .putString("title", title)
                .putString("cle_string", jsonString)
                .apply();
        Toast.makeText(getApplicationContext(),"List saved",Toast.LENGTH_SHORT).show();
    }

    private void showError(){
        Toast.makeText(this,"API Error",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }
        if (id == R.id.action_fav) {
            Intent intent = new Intent(getApplicationContext(),ThirdActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_addfav) {

            int i = 0;
            Map<String, ?> map = sharedPreferences.getAll();
            for(Map.Entry mEntry : map.entrySet()){
                if(Pattern.matches("fav "+titre.getText(), mEntry.getKey()+"")){
                    Toast.makeText(this,titre.getText()+" removed from favorite",Toast.LENGTH_SHORT).show();
                    sharedPreferences
                            .edit()
                            .remove(mEntry.getKey()+"")
                            .remove(titre.getText()+" id")
                            .apply();
                    i=1;
                }
            }

            if(i==0) {
                Toast.makeText(this, titre.getText() + " added to favorite", Toast.LENGTH_SHORT).show();
                sharedPreferences
                        .edit()
                        .putString("fav " + titre.getText(), titre.getText() + "")
                        .putInt(titre.getText() + " id", idAnime)
                        .apply();
            }
            i=0;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String s = input.getText().toString();
        Toast.makeText(this,"Searching",Toast.LENGTH_SHORT).show();
        /*int id = Integer.valueOf(s);
        makeApiCall(id);*/
        callAPI(s);
    }
}

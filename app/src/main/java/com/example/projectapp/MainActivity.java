package com.example.projectapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private listAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final String BASE_URL = "https://api.jikan.moe/";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new GsonBuilder()
                .setLenient()
                .create();

        sharedPreferences = getSharedPreferences("app_esiea", Context.MODE_PRIVATE);
        List<Genres> genresList = getDataFromCache();
        if(genresList != null){
            showList(genresList);
        }else {
            makeApiCall();
        }

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
        mAdapter = new listAdapter(genresList);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        genresList.remove(viewHolder.getAdapterPosition());
                        mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void makeApiCall(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AnimeAPI animeAPI = retrofit.create(AnimeAPI.class);

        Call<Restanimereponse> call = animeAPI.getanimeresponse();
        call.enqueue(new Callback<Restanimereponse>() {
            @Override
            public void onResponse(Call<Restanimereponse> call, Response<Restanimereponse> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    String url = response.body().getUrl();
                    String image_url = response.body().getImage_url();
                    String title = response.body().getTitle();
                    String type = response.body().getType();
                    String source = response.body().getSource();
                    Integer episodes = response.body().getEpisodes();
                    String score = response.body().getScore();
                    List<Genres> genres = response.body().getGenres();
                    Toast.makeText(getApplicationContext(),"API Succes",Toast.LENGTH_SHORT).show();
                    saveList(genres);
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

    private void saveList(List<Genres> genresList){
        String jsonString = gson.toJson(genresList);
        sharedPreferences
                .edit()
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

        return super.onOptionsItemSelected(item);
    }
}

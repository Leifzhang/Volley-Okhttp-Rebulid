package com.leif.example;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kronos.download.DownloadManager;
import com.leif.api.ReposApi;
import com.leif.baseapi.ResponseListener;
import com.leif.moudle.ReposEntity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {

        }, Throwable::printStackTrace);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ReposApi api = new ReposApi(new ResponseListener<List<ReposEntity>>() {
            private int i;

            @Override
            public void onSuccess(List<ReposEntity> model, boolean isCache) {
                Log.i("ReposApi", String.valueOf(isCache));
                for (ReposEntity entity : model) {
                    Toast.makeText(MainActivity.this, entity.getName() + "    " + i++, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(int code, String error) {

            }
        });
        api.start();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemAdapter());

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.getInstance().saveAll();
    }


}

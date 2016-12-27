package jp.techacademy.takafumi.matsushima.taskapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_TASK = "jp.techacademy.taro.kirameki.taskapp.TASK";
    private Realm mRealm,mmRealm;
    private RealmResults<Task> mTaskRealmResults,mmTaskRealmResults;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            reloadListView();
        }
    };

    private ListView mlistView;
    private TaskAdapter mtaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
     //           Snackbar.make(view, "Replace with あなたの　own action", Snackbar.LENGTH_LONG)
     //                   .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this,InputActivity.class);
                startActivity(intent);

            }
        });
        // Realmの設定
        mRealm = Realm.getDefaultInstance();
        mTaskRealmResults = mRealm.where(Task.class).findAll();
        mTaskRealmResults.sort("date", Sort.DESCENDING);
        mRealm.addChangeListener(mRealmListener);


        // ListViewの設定
        mtaskAdapter = new TaskAdapter(MainActivity.this);
        mlistView = (ListView) findViewById(R.id.listView1);

        //listview タップ処理
        mlistView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 入力・編集する画面に遷移させる
                Task task = (Task) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_TASK, task);

                startActivity(intent);

            }
        });

        //ListView 長押し
        mlistView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                // タスクを削除する

                final Task task = (Task) parent.getAdapter().getItem(position);

                // ダイアログを表示する
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();

                        mRealm.beginTransaction();
                        results.deleteAllFromRealm();
                        mRealm.commitTransaction();

                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlermReciever.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);

                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

//        if (mTaskRealmResults.size() == 0) {
 //           // アプリ起動時にタスクの数が0であった場合は表示テスト用のタスクを作成する
  //          addTaskForTest();
   //     }

        reloadListView();
    }

    private void reloadListView() {

        // 後でTaskクラスに変更する
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (int i = 0;i < mTaskRealmResults.size(); i++){
            if (!mTaskRealmResults.get(i).isValid() ) continue;
            Task task = new Task();
            task.setId(mTaskRealmResults.get(i).getId());
            task.setTitle(mTaskRealmResults.get(i).getTitle());
            task.setContents(mTaskRealmResults.get(i).getContents());
            task.setDate(mTaskRealmResults.get(i).getDate());
            task.setCategory(mTaskRealmResults.get(i).getCategory());
            taskArrayList.add(task);
        }


        mtaskAdapter.setTaskArrayList(taskArrayList);
        mlistView.setAdapter(mtaskAdapter);
        mtaskAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    private void addTaskForTest() {
        Task task = new Task();
        task.setTitle("作業");
        task.setContents("プログラムを書いてPUSHする");
        task.setDate(new Date());
        task.setId(0);
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(task);
        mRealm.commitTransaction();
    }

    public   void BTsel01(View v){
        Button button =(Button)findViewById(R.id.select_button);
        EditText editTextSel = (EditText)findViewById(R.id.categorysel_edit_text);
        String  selectWord = (String)editTextSel.getText().toString();
        Log.d("JAVATEST",selectWord);
        // Realmの設定
        mmRealm = Realm.getDefaultInstance();
        mmTaskRealmResults = mmRealm.where(Task.class).equalTo("category",selectWord).findAll();
        mmTaskRealmResults.sort("date", Sort.DESCENDING);
        mmRealm.addChangeListener(mRealmListener);

        ArrayList<Task> taskArrayList2 = new ArrayList<>();
        for (int i = 0;i < mmTaskRealmResults.size(); i++){
            if (!mmTaskRealmResults.get(i).isValid() ) continue;
            Task task = new Task();
            task.setId(mmTaskRealmResults.get(i).getId());
            task.setTitle(mmTaskRealmResults.get(i).getTitle());
            task.setContents(mmTaskRealmResults.get(i).getContents());
            task.setDate(mmTaskRealmResults.get(i).getDate());
            task.setCategory(mmTaskRealmResults.get(i).getCategory());
            taskArrayList2.add(task);
        }


        mtaskAdapter.setTaskArrayList(taskArrayList2);
        mlistView.setAdapter(mtaskAdapter);
        mtaskAdapter.notifyDataSetChanged();

    }
    public   void BTcle01(View v){
        Button button =(Button)findViewById(R.id.clear_button);
        Log.d("JAVATEST","BTcle01 PASS");

    }
}
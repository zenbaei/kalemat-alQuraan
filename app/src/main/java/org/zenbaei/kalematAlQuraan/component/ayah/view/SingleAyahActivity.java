package org.zenbaei.kalematAlQuraan.component.ayah.view;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.zenbaei.kalematAlQuraan.common.activity.BaseActivity;
import org.zenbaei.kalematAlQuraan.common.db.AppSqliteOpenHelper;
import org.zenbaei.kalematAlQuraan.component.R;

import org.zenbaei.kalematAlQuraan.component.setting.dao.SettingDAO;
import org.zenbaei.kalematAlQuraan.component.setting.entity.Setting;

/**
 * Created by Islam on 12/24/2015.
 */
public class SingleAyahActivity extends BaseActivity {

    private SearchView searchView;
    //private GestureDetectorCompat mDetector;
    private ClipboardManager clipboard;
    private SettingDAO settingDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_ayah);
        process();
        this.clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        this.settingDAO = new SettingDAO(this);
        //mDetector = new GestureDetectorCompat(this, new IntroGestureImpl(this));
        //addGestureListner();
    }
    /*
    private void addGestureListner() {
        RelativeLayout myView = (RelativeLayout) findViewById(R.id.singleAyahRoot);

        myView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetector.onTouchEvent(motionEvent);
                return super.onTouch(view, motionEvent);
            }
        });
    }
    */

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        searchView.setQuery("", false);
        searchView.setIconified(true);
        process();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        return true;
    }

    private void process() {
        Uri uri = getIntent().getData();
        Cursor cursor = managedQuery(uri, null, null, null, null);

        if (cursor == null) {
            finish();
        } else {
            cursor.moveToFirst();

            TextView surah = (TextView) findViewById(R.id.singleAyahSurah);
            TextView number = (TextView) findViewById(R.id.singleAyahNumber);
            TextView kalemah = (TextView) findViewById(R.id.singleAyahKalemah);
            TextView tafsir = (TextView) findViewById(R.id.singleAyahTafsir);

            int sIndex = cursor.getColumnIndexOrThrow(AppSqliteOpenHelper.SURAH);
            int nIndex = cursor.getColumnIndexOrThrow(AppSqliteOpenHelper.AYAH_NUMBER);
            int kIndex = cursor.getColumnIndexOrThrow(AppSqliteOpenHelper.KALEMAH);
            int tIndex = cursor.getColumnIndexOrThrow(AppSqliteOpenHelper.TAFSIR);

            surah.setText(getString(R.string.surah, new Object[]{cursor.getString(sIndex)}));
            number.setText(cursor.getString(nIndex));
            kalemah.setText(cursor.getString(kIndex));
            tafsir.setText(cursor.getString(tIndex));
        }
    }


    public void onCopy(View view) {
        String output = getString(R.string.copyDone);
        TextView kalemah = (TextView) findViewById(R.id.singleAyahKalemah);
        TextView tafsir = (TextView) findViewById(R.id.singleAyahTafsir);
        copyToClipboard("\"" + kalemah.getText() + "\": " + tafsir.getText());
        Toast.makeText(SingleAyahActivity.this, output , Toast.LENGTH_SHORT).show();
    }

    public void onFav(View view) {
        String output = getString(R.string.favouriteDone);
        TextView kalemah = (TextView) findViewById(R.id.singleAyahKalemah);
        TextView tafsir = (TextView) findViewById(R.id.singleAyahTafsir);
        settingDAO.insertIfNotExists(Setting.KEY_NAME.FAVOURITE, kalemah.getText() + "#" + tafsir.getText());
        Toast.makeText(SingleAyahActivity.this, output , Toast.LENGTH_SHORT).show();
    }

    private void copyToClipboard(String text) {
        ClipData clip = ClipData.newPlainText("tafsir", text);
        clipboard.setPrimaryClip(clip);
    }

}
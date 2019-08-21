package io.github.thang86.contentprovidersexample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.github.thang86.contentprovidersexample.data.NationContract.NationEntry;
import io.github.thang86.contentprovidersexample.data.NationDbHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCountry, etContinent, etWhereToUpdate, etNewContinent, etWhereToDelete, etQueryRowById;
    private Button btnInsert, btnUpdate, btnDelete, btnQueryRowById, btnDisplayAll;

    private static final String TAG = MainActivity.class.getSimpleName();

    private SQLiteDatabase database;
    private NationDbHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCountry = (EditText) findViewById(R.id.etCountry);
        etContinent = (EditText) findViewById(R.id.etContinent);
        etWhereToUpdate = (EditText) findViewById(R.id.etWhereToUpdate);
        etNewContinent = (EditText) findViewById(R.id.etUpdateContinent);
        etQueryRowById = (EditText) findViewById(R.id.etQueryByRowId);
        etWhereToDelete = (EditText) findViewById(R.id.etWhereToDelete);

        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnQueryRowById = (Button) findViewById(R.id.btnQueryByID);
        btnDisplayAll = (Button) findViewById(R.id.btnDisplayAll);

        btnInsert.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnQueryRowById.setOnClickListener(this);
        btnDisplayAll.setOnClickListener(this);

        databaseHelper = new NationDbHelper(this);
        database = databaseHelper.getWritableDatabase();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnInsert:
                insert();
                break;

            case R.id.btnUpdate:
                update();
                break;

            case R.id.btnDelete:
                delete();
                break;

            case R.id.btnQueryByID:
                queryRowById();
                break;

            case R.id.btnDisplayAll:
                queryAndDisplayAll();
                break;
        }
    }

    public void queryAndDisplayAll() {
        String[] projection = {
                NationEntry._ID,
                NationEntry.COLUMN_COUNTRY,
                NationEntry.COLUMN_CONTINENT
        };
        String selection = null;
        String[] selectionArg = null;
        String sortOrder = null;
        Cursor cursor = database.query(NationEntry.TABLE_NAME,//table name
                projection,                   //colurm return
                selection,                    // where clause or condition
                selectionArg,                   //select argument for where clause
                null,                          //don't group the row
                null,           // don't filter by row groups
                sortOrder
        );

        if (cursor != null) {
            String str = "";
            while (cursor.moveToNext()) {
                String[] colums = cursor.getColumnNames();
                for (String column : colums) {
                    str += "\t" + cursor.getString(cursor.getColumnIndex(column));
                }
                str += "\n";
            }

            cursor.close();
            Log.d(TAG, str);
        }

    }

    private void queryRowById() {
        String rowId = etQueryRowById.getText().toString();
        String[] projection = {
                NationEntry._ID,
                NationEntry.COLUMN_COUNTRY,
                NationEntry.COLUMN_CONTINENT
        };
        String selection = NationEntry._ID + "=? ";
        String[] selectionArg = {rowId};
        String sortOrder = null;
        Cursor cursor = database.query(NationEntry.TABLE_NAME,//table name
                projection,                   //colurm return
                selection,                    // where clause or condition
                selectionArg,                   //select argument for where clause
                null,                          //don't group the row
                null,           // don't filter by row groups
                sortOrder
        );

        if (cursor != null && cursor.moveToNext()) {
            String str = "";
            String[] colums = cursor.getColumnNames();
            for (String column : colums) {
                str += "\t" + cursor.getString(cursor.getColumnIndex(column));
            }
            str += "\n";

        cursor.close();
        Log.d(TAG, str);
    }

    }

    private void delete() {
        String countryName = etWhereToDelete.getText().toString();
        String selection = NationEntry.COLUMN_COUNTRY+" =?";
        String []selectionArg = {countryName};

        int rowsDeleted=database.delete(NationEntry.TABLE_NAME,selection,selectionArg);

        Log.i(TAG, "Number of rows deleted: " + rowsDeleted);
    }

    private void update() {
        String contryName = etWhereToUpdate.getText().toString();
        String newContinent = etNewContinent.getText().toString();

        String selection = NationEntry.COLUMN_COUNTRY+" =?";
        String []selectionArg= {contryName};
        ContentValues contentValues = new ContentValues();
        contentValues.put(NationEntry.COLUMN_CONTINENT, newContinent);
        int rowId = database.update(NationEntry.TABLE_NAME,contentValues,selection,selectionArg);
        Log.i(TAG, "Number of rows updated: " + rowId);
    }

    private void insert() {
        String countryName = etCountry.getText().toString();
        String continentName = etContinent.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NationEntry.COLUMN_COUNTRY, countryName);
        contentValues.put(NationEntry.COLUMN_CONTINENT, continentName);

        long rowId = database.insert(NationEntry.TABLE_NAME, null, contentValues);
        Log.d(TAG, "insert success " + rowId + " row ");

    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}

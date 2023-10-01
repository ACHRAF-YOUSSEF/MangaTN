package com.example.mangatn.db;

import static com.example.mangatn.Utils.userIsAuthenticated;
import static com.example.mangatn.db.Enum.DataBaseActionType.CREATE;
import static com.example.mangatn.db.Enum.DataBaseActionType.DELETE;
import static com.example.mangatn.db.Enum.DataBaseActionType.UPDATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.example.mangatn.interfaces.bookmark.OnBookmarkListener;
import com.example.mangatn.interfaces.update.OnFetchUpdateListener;
import com.example.mangatn.interfaces.update.OnMarkAsViewedOrNotListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.bookmark.BookmarkModel;
import com.example.mangatn.models.chapter.ChapterModel;
import com.example.mangatn.models.chapter.ReadChapterModel;

import java.util.ArrayList;
import java.util.List;

public class MangaDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "manga_app.db";
    private static final String CHAPTER_REFERENCE_COLUMN_NAME = "chapterReference";
    private static final String PROGRESS_COLUMN_NAME = "progress";
    private static final String COMPLETED_COLUMN_NAME = "completed";
    private static final String IN_PROGRESS_COLUMN_NAME = "inProgress";
    private static final String MANGA_ID_COLUMN_NAME = "mangaId";
    private static final String READ_CHAPTER_TABLE_NAME = "read_chapter";
    private static final String BOOKMARKED_MANGAS_TABLE_NAME = "bookmarked_mangas";
    private static final String READ_CHAPTER_JOURNAL_TABLE_NAME = "read_chapter_journal";
    private static final String BOOKMARKS_JOURNAL_TABLE_NAME = "bookmarks_journal";
    private static final String JOURNAL_ID_COLUMN_NAME = "id";
    private static final String JOURNAL_ACTION_COLUMN_NAME = "journal_action";
    private static final String JOURNAL_TIMESTAMP_COLUMN_NAME = "timestamp";
    private static final String CREATE_READ_CHAPTER_JOURNAL_TABLE_QUERY = "CREATE TABLE " + READ_CHAPTER_JOURNAL_TABLE_NAME + " (" + JOURNAL_ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " + JOURNAL_ACTION_COLUMN_NAME + " TEXT, " + MANGA_ID_COLUMN_NAME + " TEXT, " + CHAPTER_REFERENCE_COLUMN_NAME + " INTEGER, " + PROGRESS_COLUMN_NAME + " INTEGER, " + COMPLETED_COLUMN_NAME + " INTEGER, " + IN_PROGRESS_COLUMN_NAME + " INTEGER, " + JOURNAL_TIMESTAMP_COLUMN_NAME + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
    private static final String CREATE_BOOKMARKS_JOURNAL_TABLE_QUERY = "CREATE TABLE " + READ_CHAPTER_JOURNAL_TABLE_NAME + " (" + JOURNAL_ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " + JOURNAL_ACTION_COLUMN_NAME + " TEXT, " + MANGA_ID_COLUMN_NAME + " TEXT, " + JOURNAL_TIMESTAMP_COLUMN_NAME + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
    private static final String CREATE_READ_CHAPTER_TABLE =
            "CREATE TABLE " + READ_CHAPTER_TABLE_NAME +
                    " (" + MANGA_ID_COLUMN_NAME + " TEXT, "
                    + CHAPTER_REFERENCE_COLUMN_NAME +
                    " INTEGER, " + PROGRESS_COLUMN_NAME +
                    " INTEGER, " + COMPLETED_COLUMN_NAME +
                    " INTEGER, " + IN_PROGRESS_COLUMN_NAME +
                    " INTEGER)";
    private static final String CREATE_BOOKMARKS_TABLE_QUERY =
            "CREATE TABLE " + BOOKMARKED_MANGAS_TABLE_NAME + " (" + MANGA_ID_COLUMN_NAME + " TEXT)";
    private static final int DATABASE_VERSION = 1;
    private static MangaDatabaseHelper instance;
    private RequestManager requestManager;

    private MangaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized MangaDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MangaDatabaseHelper(context.getApplicationContext());
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_READ_CHAPTER_TABLE);
        db.execSQL(CREATE_BOOKMARKS_TABLE_QUERY);
        db.execSQL(CREATE_BOOKMARKS_JOURNAL_TABLE_QUERY);
        db.execSQL(CREATE_READ_CHAPTER_JOURNAL_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + READ_CHAPTER_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BOOKMARKED_MANGAS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BOOKMARKS_JOURNAL_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + READ_CHAPTER_JOURNAL_TABLE_NAME);

            onCreate(db);
        }
    }

    private void recordChange(String action, String mangaId, int chapterReference, int progress, boolean completed, boolean inProgress) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(JOURNAL_ACTION_COLUMN_NAME, action);
        values.put(MANGA_ID_COLUMN_NAME, mangaId);
        values.put(CHAPTER_REFERENCE_COLUMN_NAME, chapterReference);
        values.put(PROGRESS_COLUMN_NAME, progress);
        values.put(COMPLETED_COLUMN_NAME, completed ? 1 : 0);
        values.put(IN_PROGRESS_COLUMN_NAME, inProgress ? 1 : 0);

        db.insert(READ_CHAPTER_JOURNAL_TABLE_NAME, null, values);

        db.close();
    }

    private void recordChange(String action, String mangaId, int chapterReference) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(JOURNAL_ACTION_COLUMN_NAME, action);
        values.put(MANGA_ID_COLUMN_NAME, mangaId);
        values.put(CHAPTER_REFERENCE_COLUMN_NAME, chapterReference);
        values.put(PROGRESS_COLUMN_NAME, 0);
        values.put(COMPLETED_COLUMN_NAME, 0);
        values.put(IN_PROGRESS_COLUMN_NAME, 0);

        db.insert(READ_CHAPTER_JOURNAL_TABLE_NAME, null, values);

        db.close();
    }

    private void recordChange(String action, String mangaId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(JOURNAL_ACTION_COLUMN_NAME, action);
        values.put(MANGA_ID_COLUMN_NAME, mangaId);

        db.insert(BOOKMARKS_JOURNAL_TABLE_NAME, null, values);

        db.close();
    }

    public void storeReadChapter(String mangaId, int chapterReference) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MANGA_ID_COLUMN_NAME, mangaId);
        values.put(CHAPTER_REFERENCE_COLUMN_NAME, chapterReference);
        values.put(PROGRESS_COLUMN_NAME, 0);
        values.put(COMPLETED_COLUMN_NAME, 0);
        values.put(IN_PROGRESS_COLUMN_NAME, 0);

        if (!userIsAuthenticated()) {
            recordChange(CREATE.getName(), mangaId, chapterReference);
        }

        db.insert(
                READ_CHAPTER_TABLE_NAME,
                null,
                values
        );

        db.close();
    }

    public void updateReadChapter(String mangaId, int chapterReference, int progress, boolean completed, boolean inProgress) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PROGRESS_COLUMN_NAME, progress);
        values.put(COMPLETED_COLUMN_NAME, completed ? 1 : 0);
        values.put(IN_PROGRESS_COLUMN_NAME, inProgress ? 1 : 0);

        String whereClause = MANGA_ID_COLUMN_NAME + " = ? AND " + CHAPTER_REFERENCE_COLUMN_NAME + " = ?";
        String[] whereArgs = {mangaId, String.valueOf(chapterReference)};

        if (!userIsAuthenticated()) {
            recordChange(UPDATE.getName(), mangaId, chapterReference, progress, completed, inProgress);
        }

        db.update(
                READ_CHAPTER_TABLE_NAME,
                values,
                whereClause,
                whereArgs
        );

        db.close();
    }

    public boolean checkIfHasReadChapters(String mangaId) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = MANGA_ID_COLUMN_NAME + " = ?";
        String[] columns = {MANGA_ID_COLUMN_NAME};
        String[] selectionArgs = {mangaId};

        Cursor cursor = db.query(
                READ_CHAPTER_TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean chapterExists = cursor.moveToFirst();

        cursor.close();
        db.close();

        return chapterExists;
    }

    public void storeBookmark(String mangaId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MANGA_ID_COLUMN_NAME, mangaId);

        if (!userIsAuthenticated()) {
            recordChange(CREATE.getName(), mangaId);
        }

        db.insert(
                BOOKMARKED_MANGAS_TABLE_NAME,
                null,
                values
        );

        db.close();
    }

    public boolean checkIfBookmarkExists(String mangaId) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = MANGA_ID_COLUMN_NAME + " = ?";
        String[] columns = {MANGA_ID_COLUMN_NAME};
        String[] selectionArgs = {mangaId};

        Cursor cursor = db.query(
                BOOKMARKED_MANGAS_TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean bookmarkExists = cursor.moveToFirst();

        cursor.close();
        db.close();

        return bookmarkExists;
    }

    public void removeBookmark(String mangaId) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = MANGA_ID_COLUMN_NAME + " = ?";
        String[] whereArgs = {mangaId};

        if (!userIsAuthenticated()) {
            recordChange(DELETE.getName(), mangaId);
        }

        db.delete(
                BOOKMARKED_MANGAS_TABLE_NAME,
                whereClause,
                whereArgs
        );

        db.close();
    }

    public ReadChapterModel getLastReadAndInProgressChapter(String mangaId) {
        ReadChapterModel lastReadChapter;
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                CHAPTER_REFERENCE_COLUMN_NAME,
                PROGRESS_COLUMN_NAME,
                COMPLETED_COLUMN_NAME,
                IN_PROGRESS_COLUMN_NAME
        };

        String selection = MANGA_ID_COLUMN_NAME + " = ? AND " + IN_PROGRESS_COLUMN_NAME + " = ?";
        String[] selectionArgs = {mangaId, "1"};

        String orderBy = CHAPTER_REFERENCE_COLUMN_NAME + " DESC";
        String limit = "1";

        Cursor cursor = db.query(
                READ_CHAPTER_TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                orderBy,
                limit
        );

        if (cursor.moveToFirst()) {
            int chapterReferenceIndex = cursor.getColumnIndex(CHAPTER_REFERENCE_COLUMN_NAME);
            int progressIndex = cursor.getColumnIndex(PROGRESS_COLUMN_NAME);
            int completedIndex = cursor.getColumnIndex(COMPLETED_COLUMN_NAME);
            int inProgressIndex = cursor.getColumnIndex(IN_PROGRESS_COLUMN_NAME);

            int chapterReference = cursor.getInt(chapterReferenceIndex);
            int progress = cursor.getInt(progressIndex);
            boolean completed = cursor.getInt(completedIndex) == 1;
            boolean inProgress = cursor.getInt(inProgressIndex) == 1;

            ChapterModel chapterModel = new ChapterModel();
            chapterModel.setReference(chapterReference);
            chapterModel.setCompleted(completed);
            chapterModel.setInProgress(inProgress);

            lastReadChapter = new ReadChapterModel(completed, inProgress, progress, chapterModel, mangaId);
        } else {
            ChapterModel chapterModel = new ChapterModel();

            chapterModel.setReference(1);
            chapterModel.setCompleted(false);
            chapterModel.setInProgress(false);

            lastReadChapter = new ReadChapterModel(false, false, 0, chapterModel, mangaId);
        }

        cursor.close();
        db.close();

        return lastReadChapter;
    }
    
    public List<String> getBookmarkedChapters() {
        List<String> bookmarkedChapters = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {MANGA_ID_COLUMN_NAME};

        Cursor cursor = db.query(
                BOOKMARKED_MANGAS_TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            int mangaId = cursor.getColumnIndex(MANGA_ID_COLUMN_NAME);

            bookmarkedChapters.add(cursor.getString(mangaId));
        }

        cursor.close();
        db.close();

        return bookmarkedChapters;
    }

    public void processJournalEntries(Context context) {
        requestManager = new RequestManager(context);

        try (SQLiteDatabase db = getWritableDatabase()) {
            Cursor readChapterCursor = db.query(
                    READ_CHAPTER_JOURNAL_TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while (readChapterCursor.moveToNext()) {
                int idIndex = readChapterCursor.getColumnIndex(JOURNAL_ID_COLUMN_NAME);
                int actionIndex = readChapterCursor.getColumnIndex(JOURNAL_ACTION_COLUMN_NAME);
                int mangaIdIndex = readChapterCursor.getColumnIndex(MANGA_ID_COLUMN_NAME);
                int chapterReferenceIndex = readChapterCursor.getColumnIndex(CHAPTER_REFERENCE_COLUMN_NAME);
                int progressIndex = readChapterCursor.getColumnIndex(PROGRESS_COLUMN_NAME);
                int completedIndex = readChapterCursor.getColumnIndex(COMPLETED_COLUMN_NAME);
                int inProgressIndex = readChapterCursor.getColumnIndex(IN_PROGRESS_COLUMN_NAME);

                int id = readChapterCursor.getInt(idIndex);
                String action = readChapterCursor.getString(actionIndex);
                String mangaId = readChapterCursor.getString(mangaIdIndex);
                int chapterReference = readChapterCursor.getInt(chapterReferenceIndex);
                int progress = readChapterCursor.getInt(progressIndex);
                boolean completed = readChapterCursor.getInt(completedIndex) == 1;
                boolean inProgress = readChapterCursor.getInt(inProgressIndex) == 1;

                if (action.equals(CREATE.getName())) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            requestManager.createOrDeleteReadChapter(new OnMarkAsViewedOrNotListener() {
                                @Override
                                public void onFetchData(ApiResponse response, String message, Context context) {

                                }

                                @Override
                                public void onError(String message, Context context) {

                                }
                            }, chapterReference, mangaId, true);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            removeJournalEntry(READ_CHAPTER_JOURNAL_TABLE_NAME, id);
                        }
                    }.execute();
                } else if (action.equals(UPDATE.getName())) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            ReadChapterModel readChapterModel = new ReadChapterModel();

                            readChapterModel.setMangaId(mangaId);
                            readChapterModel.setProgress(progress);
                            readChapterModel.setCompleted(completed);
                            readChapterModel.setInProgress(inProgress);
                            requestManager.updateReadChapter(new OnFetchUpdateListener<ApiResponse>() {
                                @Override
                                public void onFetchData(ApiResponse apiResponse, String message, Context context) {

                                }

                                @Override
                                public void onError(String message, Context context) {

                                }
                            }, chapterReference, mangaId, readChapterModel);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            removeJournalEntry(READ_CHAPTER_JOURNAL_TABLE_NAME, id);
                        }
                    }.execute();
                } else if (action.equals(DELETE.getName())) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            requestManager.createOrDeleteReadChapter(new OnMarkAsViewedOrNotListener() {
                                @Override
                                public void onFetchData(ApiResponse response, String message, Context context) {

                                }

                                @Override
                                public void onError(String message, Context context) {

                                }
                            }, chapterReference, mangaId, false);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            removeJournalEntry(READ_CHAPTER_JOURNAL_TABLE_NAME, id);
                        }
                    }.execute();
                }
            }

            Cursor bookmarksCursor = db.query(
                    BOOKMARKS_JOURNAL_TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            while (bookmarksCursor.moveToNext()) {
                int idIndex = bookmarksCursor.getColumnIndex(JOURNAL_ID_COLUMN_NAME);
                int actionIndex = bookmarksCursor.getColumnIndex(JOURNAL_ACTION_COLUMN_NAME);
                int mangaIdIndex = bookmarksCursor.getColumnIndex(MANGA_ID_COLUMN_NAME);

                int id = bookmarksCursor.getInt(idIndex);
                String action = bookmarksCursor.getString(actionIndex);
                String mangaId = bookmarksCursor.getString(mangaIdIndex);

                if (action.equals(CREATE.getName())) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            requestManager.bookmark(new OnBookmarkListener() {
                                @Override
                                public void onFetchData(ApiResponse response, String message, Context context) {

                                }

                                @Override
                                public void onError(String message, Context context) {

                                }
                            }, new BookmarkModel(mangaId, true));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            removeJournalEntry(BOOKMARKS_JOURNAL_TABLE_NAME, id);
                        }
                    }.execute();
                } else if (action.equals(DELETE.getName())) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            requestManager.bookmark(new OnBookmarkListener() {
                                @Override
                                public void onFetchData(ApiResponse response, String message, Context context) {

                                }

                                @Override
                                public void onError(String message, Context context) {

                                }
                            }, new BookmarkModel(mangaId, false));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            removeJournalEntry(BOOKMARKS_JOURNAL_TABLE_NAME, id);
                        }
                    }.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeJournalEntry(String journalTableName, int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = JOURNAL_ID_COLUMN_NAME + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.delete(journalTableName, whereClause, whereArgs);
    }

    public ReadChapterModel getReadChapter(Integer reference, String mangaId) {
        ReadChapterModel readChapter = null;
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                CHAPTER_REFERENCE_COLUMN_NAME,
                PROGRESS_COLUMN_NAME,
                COMPLETED_COLUMN_NAME,
                IN_PROGRESS_COLUMN_NAME
        };

        String selection = MANGA_ID_COLUMN_NAME + " = ? AND " + CHAPTER_REFERENCE_COLUMN_NAME + " = ?";
        String[] selectionArgs = {mangaId, String.valueOf(reference)};

        Cursor cursor = db.query(
                READ_CHAPTER_TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int chapterReferenceIndex = cursor.getColumnIndex(CHAPTER_REFERENCE_COLUMN_NAME);
            int progressIndex = cursor.getColumnIndex(PROGRESS_COLUMN_NAME);
            int completedIndex = cursor.getColumnIndex(COMPLETED_COLUMN_NAME);
            int inProgressIndex = cursor.getColumnIndex(IN_PROGRESS_COLUMN_NAME);

            int chapterReference = cursor.getInt(chapterReferenceIndex);
            int progress = cursor.getInt(progressIndex);
            boolean completed = cursor.getInt(completedIndex) == 1;
            boolean inProgress = cursor.getInt(inProgressIndex) == 1;

            ChapterModel chapterModel = new ChapterModel();

            chapterModel.setReference(chapterReference);
            chapterModel.setCompleted(completed);
            chapterModel.setInProgress(inProgress);

            readChapter = new ReadChapterModel(completed, inProgress, progress, chapterModel, mangaId);
        }

        cursor.close();
        db.close();

        return readChapter;
    }
}

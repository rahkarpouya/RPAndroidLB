package ir.rahkarpouya.rpandroidlib.crashlytics

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class CrashInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        CustomActivityOnCrash.install(context)
        return false
    }

    @Nullable
    override fun query(
        @NonNull uri: Uri,
        @Nullable projection: Array<String>?,
        @Nullable selection: String?,
        @Nullable selectionArgs: Array<String>?,
        @Nullable sortOrder: String?
    ): Cursor? = null

    @Nullable
    override fun getType(@NonNull uri: Uri): String? = null

    @Nullable
    override fun insert(@NonNull uri: Uri, @Nullable values: ContentValues?): Uri? = null

    override fun delete(
        @NonNull uri: Uri,
        @Nullable selection: String?,
        @Nullable selectionArgs: Array<String>?
    ): Int = 0

    override fun update(
        @NonNull uri: Uri,
        @Nullable values: ContentValues?,
        @Nullable selection: String?,
        @Nullable selectionArgs: Array<String>?
    ): Int = 0
}

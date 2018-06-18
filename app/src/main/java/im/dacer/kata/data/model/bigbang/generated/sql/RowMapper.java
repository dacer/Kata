package im.dacer.kata.data.model.bigbang.generated.sql;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

/** Creates instances of {@code T} from rows in a {@link Cursor}. */
public interface RowMapper<T> {
    /**
     * Return an instance of {@code T} corresponding to the values of the current positioned row of
     * {@code cursor}.
     */
    @CheckResult @NonNull T map(@NonNull Cursor cursor);
}

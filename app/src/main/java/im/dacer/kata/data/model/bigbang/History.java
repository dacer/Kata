package im.dacer.kata.data.model.bigbang;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

import im.dacer.kata.core.model.HistoryModel;

/**
 * Created by Dacer on 13/02/2018.
 */

@AutoValue
public abstract class History implements Parcelable, HistoryModel {

    public static History newInstance(long id, @Nullable String text, @Nullable String alias, @Nullable Boolean star, @Nullable Long createdAt) {
        return new AutoValue_History(id, text, alias, star, createdAt);
    }

    public static final Factory<History> FACTORY =
            new Factory<>(AutoValue_History::new);

    public static final RowMapper<History> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
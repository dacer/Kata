package im.dacer.kata.core.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

/**
 * Created by Dacer on 11/01/2018.
 */

@AutoValue
public abstract class DictEntry implements Parcelable, DictEntryModel {

    public static final DictEntryModel.Factory<DictEntry> FACTORY =
            new DictEntryModel.Factory<>(new DictEntryModel.Creator<DictEntry>() {
                @Override
                public DictEntry create(long id, @Nullable String gloss, @Nullable String position, @Nullable String gloss_cn) {
                    return new AutoValue_DictEntry(id, gloss, position, gloss_cn);
                }
            });

    public static final RowMapper<DictEntry> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
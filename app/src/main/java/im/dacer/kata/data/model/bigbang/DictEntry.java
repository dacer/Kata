package im.dacer.kata.data.model.bigbang;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

import im.dacer.kata.core.model.DictEntryModel;

/**
 * Created by Dacer on 11/01/2018.
 */

@AutoValue
public abstract class DictEntry implements Parcelable, DictEntryModel {

    public static final DictEntryModel.Factory<DictEntry> FACTORY =
            new DictEntryModel.Factory<>(AutoValue_DictEntry::new);

    public static final RowMapper<DictEntry> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
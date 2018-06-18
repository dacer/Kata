package im.dacer.kata.data.model.bigbang.generated.autovalue;

import android.os.Parcelable;

import im.dacer.kata.data.model.bigbang.generated.sql.DictEntryModel;
import im.dacer.kata.data.model.bigbang.generated.sql.RowMapper;

/**
 * Created by Dacer on 11/01/2018.
 */

public abstract class DictEntry implements Parcelable, DictEntryModel {

    public static final DictEntryModel.Factory<DictEntry> FACTORY =
            new DictEntryModel.Factory<>(AutoValue_DictEntry::new);

    public static final RowMapper<DictEntry> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
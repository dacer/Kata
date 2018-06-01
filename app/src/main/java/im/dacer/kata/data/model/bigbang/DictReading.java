package im.dacer.kata.data.model.bigbang;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

import im.dacer.kata.core.model.DictReadingModel;

/**
 * Created by Dacer on 14/01/2018.
 */

@AutoValue
public abstract class DictReading implements Parcelable, DictReadingModel {

    public static final Factory<DictReading> FACTORY =
            new Factory<>(AutoValue_DictReading::new);

    public static final RowMapper<DictReading> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
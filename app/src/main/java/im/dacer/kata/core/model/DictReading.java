package im.dacer.kata.core.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

/**
 * Created by Dacer on 14/01/2018.
 */

@AutoValue
public abstract class DictReading implements Parcelable, DictReadingModel {

    public static final Factory<DictReading> FACTORY =
            new Factory<>(new DictReadingModel.Creator<DictReading>() {
                @Override
                public DictReading create(long id, @Nullable String reading, @Nullable Long id_in_entry) {
                    return new AutoValue_DictReading(id, reading, id_in_entry);
                }
            });

    public static final RowMapper<DictReading> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
package im.dacer.kata.data.model.bigbang.generated.autovalue;

import android.os.Parcelable;

import im.dacer.kata.data.model.bigbang.generated.sql.DictReadingModel;
import im.dacer.kata.data.model.bigbang.generated.sql.RowMapper;


/**
 * Created by Dacer on 14/01/2018.
 */

public abstract class DictReading implements Parcelable, DictReadingModel {

    public static final Factory<DictReading> FACTORY =
            new Factory<>(AutoValue_DictReading::new);

    public static final RowMapper<DictReading> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
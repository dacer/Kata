package im.dacer.kata.data.model.bigbang;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

import im.dacer.kata.core.model.DictKanjiModel;

/**
 * Created by Dacer on 14/01/2018.
 */

@AutoValue
public abstract class DictKanji implements Parcelable, DictKanjiModel {

    public static final Factory<DictKanji> FACTORY =
            new Factory<>(AutoValue_DictKanji::new);

    public static final RowMapper<DictKanji> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
package im.dacer.kata.core.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

/**
 * Created by Dacer on 14/01/2018.
 */

@AutoValue
public abstract class DictKanji implements Parcelable, DictKanjiModel {

    public static final Factory<DictKanji> FACTORY =
            new Factory<>(new DictKanjiModel.Creator<DictKanji>() {
                @Override
                public DictKanji create(long id, @Nullable String kanji, @Nullable Long id_in_entry) {
                    return new AutoValue_DictKanji(id, kanji, id_in_entry);
                }
            });

    public static final RowMapper<DictKanji> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
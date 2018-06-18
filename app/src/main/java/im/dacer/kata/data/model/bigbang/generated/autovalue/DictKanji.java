package im.dacer.kata.data.model.bigbang.generated.autovalue;

import android.os.Parcelable;

import im.dacer.kata.data.model.bigbang.generated.sql.DictKanjiModel;
import im.dacer.kata.data.model.bigbang.generated.sql.RowMapper;

/**
 * Created by Dacer on 14/01/2018.
 */

public abstract class DictKanji implements Parcelable, DictKanjiModel {

    public static final Factory<DictKanji> FACTORY =
            new Factory<>(AutoValue_DictKanji::new);

    public static final RowMapper<DictKanji> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

}
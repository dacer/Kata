package im.dacer.kata.data.model.bigbang.generated.sql;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.Deprecated;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface DictKanjiModel {
    String TABLE_NAME = "kanji";

    String ID = "id";

    String KANJI = "kanji";

    String ID_IN_ENTRY = "id_in_entry";

    String CREATE_TABLE = ""
            + "CREATE TABLE kanji (\n"
            + "    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
            + "    kanji TEXT,\n"
            + "    id_in_entry INTEGER\n"
            + ")";

    long id();

    @Nullable
    String kanji();

    @Nullable
    Long id_in_entry();

    interface Creator<T extends DictKanjiModel> {
        T create(long id, @Nullable String kanji, @Nullable Long id_in_entry);
    }

    final class Mapper<T extends DictKanjiModel> implements RowMapper<T> {
        private final Factory<T> dictKanjiModelFactory;

        public Mapper(Factory<T> dictKanjiModelFactory) {
            this.dictKanjiModelFactory = dictKanjiModelFactory;
        }

        @Override
        public T map(@NonNull Cursor cursor) {
            return dictKanjiModelFactory.creator.create(
                    cursor.getLong(0),
                    cursor.isNull(1) ? null : cursor.getString(1),
                    cursor.isNull(2) ? null : cursor.getLong(2)
            );
        }
    }

    final class Marshal {
        protected final ContentValues contentValues = new ContentValues();

        Marshal(@Nullable DictKanjiModel copy) {
            if (copy != null) {
                this.id(copy.id());
                this.kanji(copy.kanji());
                this.id_in_entry(copy.id_in_entry());
            }
        }

        public ContentValues asContentValues() {
            return contentValues;
        }

        public Marshal id(long id) {
            contentValues.put("id", id);
            return this;
        }

        public Marshal kanji(String kanji) {
            contentValues.put("kanji", kanji);
            return this;
        }

        public Marshal id_in_entry(Long id_in_entry) {
            contentValues.put("id_in_entry", id_in_entry);
            return this;
        }
    }

    final class Factory<T extends DictKanjiModel> {
        public final DictKanjiModel.Creator<T> creator;

        public Factory(DictKanjiModel.Creator<T> creator) {
            this.creator = creator;
        }

        /**
         * @deprecated Use compiled statements (https://github.com/square/sqldelight#compiled-statements)
         */
        @Deprecated
        public Marshal marshal() {
            return new Marshal(null);
        }

        /**
         * @deprecated Use compiled statements (https://github.com/square/sqldelight#compiled-statements)
         */
        @Deprecated
        public DictKanjiModel.Marshal marshal(DictKanjiModel copy) {
            return new Marshal(copy);
        }

        public SqlDelightStatement select_all() {
            return new SqlDelightStatement(""
                    + "SELECT *\n"
                    + "FROM kanji",
                    new String[0], Collections.<String>singleton("kanji"));
        }

        public SqlDelightStatement search(@Nullable String kanji) {
            List<String> args = new ArrayList<String>();
            int currentIndex = 1;
            StringBuilder query = new StringBuilder();
            query.append("SELECT *\n"
                    + "FROM kanji\n"
                    + "WHERE kanji IS ");
            if (kanji == null) {
                query.append("null");
            } else {
                query.append('?').append(currentIndex++);
                args.add(kanji);
            }
            return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("kanji"));
        }

        public DictKanjiModel.Mapper<T> select_allMapper() {
            return new DictKanjiModel.Mapper<T>(this);
        }

        public DictKanjiModel.Mapper<T> searchMapper() {
            return new DictKanjiModel.Mapper<T>(this);
        }
    }
}
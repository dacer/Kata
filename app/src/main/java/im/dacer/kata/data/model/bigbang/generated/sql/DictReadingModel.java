package im.dacer.kata.data.model.bigbang.generated.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface DictReadingModel {
  String TABLE_NAME = "reading";

  String ID = "id";

  String READING = "reading";

  String ID_IN_ENTRY = "id_in_entry";

  String CREATE_TABLE = ""
      + "CREATE TABLE reading (\n"
      + "    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
      + "    reading TEXT,\n"
      + "    id_in_entry INTEGER\n"
      + ")";

  long id();

  @Nullable
  String reading();

  @Nullable
  Long id_in_entry();

  interface Creator<T extends DictReadingModel> {
    T create(long id, @Nullable String reading, @Nullable Long id_in_entry);
  }

  final class Mapper<T extends DictReadingModel> implements RowMapper<T> {
    private final Factory<T> dictReadingModelFactory;

    public Mapper(Factory<T> dictReadingModelFactory) {
      this.dictReadingModelFactory = dictReadingModelFactory;
    }

    @Override
    public T map(@NonNull Cursor cursor) {
      return dictReadingModelFactory.creator.create(
          cursor.getLong(0),
          cursor.isNull(1) ? null : cursor.getString(1),
          cursor.isNull(2) ? null : cursor.getLong(2)
      );
    }
  }

  final class Marshal {
    protected final ContentValues contentValues = new ContentValues();

    Marshal(@Nullable DictReadingModel copy) {
      if (copy != null) {
        this.id(copy.id());
        this.reading(copy.reading());
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

    public Marshal reading(String reading) {
      contentValues.put("reading", reading);
      return this;
    }

    public Marshal id_in_entry(Long id_in_entry) {
      contentValues.put("id_in_entry", id_in_entry);
      return this;
    }
  }

  final class Factory<T extends DictReadingModel> {
    public final Creator<T> creator;

    public Factory(Creator<T> creator) {
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
    public Marshal marshal(DictReadingModel copy) {
      return new Marshal(copy);
    }

    public SqlDelightStatement select_all() {
      return new SqlDelightStatement(""
          + "SELECT *\n"
          + "FROM reading",
          new String[0], Collections.<String>singleton("reading"));
    }

    public SqlDelightStatement search(@Nullable String reading) {
      List<String> args = new ArrayList<String>();
      int currentIndex = 1;
      StringBuilder query = new StringBuilder();
      query.append("SELECT *\n"
              + "FROM reading\n"
              + "WHERE reading IS ");
      if (reading == null) {
        query.append("null");
      } else {
        query.append('?').append(currentIndex++);
        args.add(reading);
      }
      return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("reading"));
    }

    public SqlDelightStatement search_by_entry_id(@Nullable Long id_in_entry) {
      List<String> args = new ArrayList<String>();
      int currentIndex = 1;
      StringBuilder query = new StringBuilder();
      query.append("SELECT *\n"
              + "FROM reading\n"
              + "WHERE id_in_entry IS ");
      if (id_in_entry == null) {
        query.append("null");
      } else {
        query.append(id_in_entry);
      }
      return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("reading"));
    }

    public Mapper<T> select_allMapper() {
      return new Mapper<T>(this);
    }

    public Mapper<T> searchMapper() {
      return new Mapper<T>(this);
    }

    public Mapper<T> search_by_entry_idMapper() {
      return new Mapper<T>(this);
    }
  }
}

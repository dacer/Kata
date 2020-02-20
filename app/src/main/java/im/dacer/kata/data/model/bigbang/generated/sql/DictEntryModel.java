package im.dacer.kata.data.model.bigbang.generated.sql;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface DictEntryModel {
  String TABLE_NAME = "entry";

  String ID = "id";

  String GLOSS = "gloss";

  String POSITION = "position";

  String GLOSS_CN = "gloss_cn";

  String CREATE_TABLE = ""
      + "CREATE TABLE entry (\n"
      + "    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
      + "    gloss TEXT,\n"
      + "    position TEXT,\n"
      + "    gloss_cn TEXT\n"
      + ")";

  long id();

  @Nullable
  String gloss();

  @Nullable
  String position();

  @Nullable
  String gloss_cn();

  interface Creator<T extends DictEntryModel> {
    T create(long id, @Nullable String gloss, @Nullable String position, @Nullable String gloss_cn);
  }

  final class Mapper<T extends DictEntryModel> implements RowMapper<T> {
    private final Factory<T> dictEntryModelFactory;

    public Mapper(Factory<T> dictEntryModelFactory) {
      this.dictEntryModelFactory = dictEntryModelFactory;
    }

    @Override
    public T map(@NonNull Cursor cursor) {
      return dictEntryModelFactory.creator.create(
          cursor.getLong(0),
          cursor.isNull(1) ? null : cursor.getString(1),
          cursor.isNull(2) ? null : cursor.getString(2),
          cursor.isNull(3) ? null : cursor.getString(3)
      );
    }
  }

  final class Marshal {
    protected final ContentValues contentValues = new ContentValues();

    Marshal(@Nullable DictEntryModel copy) {
      if (copy != null) {
        this.id(copy.id());
        this.gloss(copy.gloss());
        this.position(copy.position());
        this.gloss_cn(copy.gloss_cn());
      }
    }

    public ContentValues asContentValues() {
      return contentValues;
    }

    public Marshal id(long id) {
      contentValues.put("id", id);
      return this;
    }

    public Marshal gloss(String gloss) {
      contentValues.put("gloss", gloss);
      return this;
    }

    public Marshal position(String position) {
      contentValues.put("position", position);
      return this;
    }

    public Marshal gloss_cn(String gloss_cn) {
      contentValues.put("gloss_cn", gloss_cn);
      return this;
    }
  }

  final class Factory<T extends DictEntryModel> {
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
    public Marshal marshal(DictEntryModel copy) {
      return new Marshal(copy);
    }

    public SqlDelightStatement select_all() {
      return new SqlDelightStatement(""
          + "SELECT *\n"
          + "FROM entry",
          new String[0], Collections.<String>singleton("entry"));
    }

    public SqlDelightStatement search(long id) {
      List<String> args = new ArrayList<String>();
      int currentIndex = 1;
      StringBuilder query = new StringBuilder();
      query.append("SELECT *\n"
              + "FROM entry\n"
              + "WHERE id IS ");
      query.append(id);
      return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("entry"));
    }

    public Mapper<T> select_allMapper() {
      return new Mapper<T>(this);
    }

    public Mapper<T> searchMapper() {
      return new Mapper<T>(this);
    }
  }
}

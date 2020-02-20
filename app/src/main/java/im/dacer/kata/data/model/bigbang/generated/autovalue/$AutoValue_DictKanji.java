
package im.dacer.kata.data.model.bigbang.generated.autovalue;

import androidx.annotation.Nullable;

abstract class $AutoValue_DictKanji extends DictKanji {

  private final long id;
  private final String kanji;
  private final Long id_in_entry;

  $AutoValue_DictKanji(
      long id,
      @Nullable String kanji,
      @Nullable Long id_in_entry) {
    this.id = id;
    this.kanji = kanji;
    this.id_in_entry = id_in_entry;
  }

  @Override
  public long id() {
    return id;
  }

  @Nullable
  @Override
  public String kanji() {
    return kanji;
  }

  @Nullable
  @Override
  public Long id_in_entry() {
    return id_in_entry;
  }

  @Override
  public String toString() {
    return "DictKanji{"
        + "id=" + id + ", "
        + "kanji=" + kanji + ", "
        + "id_in_entry=" + id_in_entry
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof DictKanji) {
      DictKanji that = (DictKanji) o;
      return (this.id == that.id())
           && ((this.kanji == null) ? (that.kanji() == null) : this.kanji.equals(that.kanji()))
           && ((this.id_in_entry == null) ? (that.id_in_entry() == null) : this.id_in_entry.equals(that.id_in_entry()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (this.id >>> 32) ^ this.id;
    h *= 1000003;
    h ^= (kanji == null) ? 0 : this.kanji.hashCode();
    h *= 1000003;
    h ^= (id_in_entry == null) ? 0 : this.id_in_entry.hashCode();
    return h;
  }

}

package im.dacer.kata.data.model.bigbang.generated.autovalue;

import androidx.annotation.Nullable;

abstract class $AutoValue_DictReading extends DictReading {

  private final long id;
  private final String reading;
  private final Long id_in_entry;

  $AutoValue_DictReading(
      long id,
      @Nullable String reading,
      @Nullable Long id_in_entry) {
    this.id = id;
    this.reading = reading;
    this.id_in_entry = id_in_entry;
  }

  @Override
  public long id() {
    return id;
  }

  @Nullable
  @Override
  public String reading() {
    return reading;
  }

  @Nullable
  @Override
  public Long id_in_entry() {
    return id_in_entry;
  }

  @Override
  public String toString() {
    return "DictReading{"
        + "id=" + id + ", "
        + "reading=" + reading + ", "
        + "id_in_entry=" + id_in_entry
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof DictReading) {
      DictReading that = (DictReading) o;
      return (this.id == that.id())
           && ((this.reading == null) ? (that.reading() == null) : this.reading.equals(that.reading()))
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
    h ^= (reading == null) ? 0 : this.reading.hashCode();
    h *= 1000003;
    h ^= (id_in_entry == null) ? 0 : this.id_in_entry.hashCode();
    return h;
  }

}

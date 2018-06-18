
package im.dacer.kata.data.model.bigbang.generated.autovalue;

import android.support.annotation.Nullable;

abstract class $AutoValue_DictEntry extends DictEntry {

  private final long id;
  private final String gloss;
  private final String position;
  private final String gloss_cn;

  $AutoValue_DictEntry(
      long id,
      @Nullable String gloss,
      @Nullable String position,
      @Nullable String gloss_cn) {
    this.id = id;
    this.gloss = gloss;
    this.position = position;
    this.gloss_cn = gloss_cn;
  }

  @Override
  public long id() {
    return id;
  }

  @Nullable
  @Override
  public String gloss() {
    return gloss;
  }

  @Nullable
  @Override
  public String position() {
    return position;
  }

  @Nullable
  @Override
  public String gloss_cn() {
    return gloss_cn;
  }

  @Override
  public String toString() {
    return "DictEntry{"
        + "id=" + id + ", "
        + "gloss=" + gloss + ", "
        + "position=" + position + ", "
        + "gloss_cn=" + gloss_cn
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof DictEntry) {
      DictEntry that = (DictEntry) o;
      return (this.id == that.id())
           && ((this.gloss == null) ? (that.gloss() == null) : this.gloss.equals(that.gloss()))
           && ((this.position == null) ? (that.position() == null) : this.position.equals(that.position()))
           && ((this.gloss_cn == null) ? (that.gloss_cn() == null) : this.gloss_cn.equals(that.gloss_cn()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (this.id >>> 32) ^ this.id;
    h *= 1000003;
    h ^= (gloss == null) ? 0 : this.gloss.hashCode();
    h *= 1000003;
    h ^= (position == null) ? 0 : this.position.hashCode();
    h *= 1000003;
    h ^= (gloss_cn == null) ? 0 : this.gloss_cn.hashCode();
    return h;
  }

}

package im.dacer.kata.data.model.bigbang.generated.autovalue;

import android.os.Parcel;
import android.os.Parcelable;

final class AutoValue_DictEntry extends $AutoValue_DictEntry {
  public static final Parcelable.Creator<AutoValue_DictEntry> CREATOR = new Parcelable.Creator<AutoValue_DictEntry>() {
    @Override
    public AutoValue_DictEntry createFromParcel(Parcel in) {
      return new AutoValue_DictEntry(
          in.readLong(),
          in.readInt() == 0 ? in.readString() : null,
          in.readInt() == 0 ? in.readString() : null,
          in.readInt() == 0 ? in.readString() : null
      );
    }
    @Override
    public AutoValue_DictEntry[] newArray(int size) {
      return new AutoValue_DictEntry[size];
    }
  };

  AutoValue_DictEntry(long id, String gloss, String position, String gloss_cn) {
    super(id, gloss, position, gloss_cn);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(id());
    if (gloss() == null) {
      dest.writeInt(1);
    } else {
      dest.writeInt(0);
      dest.writeString(gloss());
    }
    if (position() == null) {
      dest.writeInt(1);
    } else {
      dest.writeInt(0);
      dest.writeString(position());
    }
    if (gloss_cn() == null) {
      dest.writeInt(1);
    } else {
      dest.writeInt(0);
      dest.writeString(gloss_cn());
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }
}

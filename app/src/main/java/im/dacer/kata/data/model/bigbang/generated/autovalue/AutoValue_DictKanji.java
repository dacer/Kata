package im.dacer.kata.data.model.bigbang.generated.autovalue;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

final class AutoValue_DictKanji extends $AutoValue_DictKanji {
  public static final Parcelable.Creator<AutoValue_DictKanji> CREATOR = new Parcelable.Creator<AutoValue_DictKanji>() {
    @Override
    public AutoValue_DictKanji createFromParcel(Parcel in) {
      return new AutoValue_DictKanji(
          in.readLong(),
          in.readInt() == 0 ? in.readString() : null,
          in.readInt() == 0 ? in.readLong() : null
      );
    }
    @Override
    public AutoValue_DictKanji[] newArray(int size) {
      return new AutoValue_DictKanji[size];
    }
  };

  AutoValue_DictKanji(long id, String kanji, Long id_in_entry) {
    super(id, kanji, id_in_entry);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(id());
    if (kanji() == null) {
      dest.writeInt(1);
    } else {
      dest.writeInt(0);
      dest.writeString(kanji());
    }
    if (id_in_entry() == null) {
      dest.writeInt(1);
    } else {
      dest.writeInt(0);
      dest.writeLong(id_in_entry());
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }
}

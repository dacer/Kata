package im.dacer.kata.data.model.bigbang.generated.autovalue;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

final class AutoValue_DictReading extends $AutoValue_DictReading {
  public static final Parcelable.Creator<AutoValue_DictReading> CREATOR = new Parcelable.Creator<AutoValue_DictReading>() {
    @Override
    public AutoValue_DictReading createFromParcel(Parcel in) {
      return new AutoValue_DictReading(
          in.readLong(),
          in.readInt() == 0 ? in.readString() : null,
          in.readInt() == 0 ? in.readLong() : null
      );
    }
    @Override
    public AutoValue_DictReading[] newArray(int size) {
      return new AutoValue_DictReading[size];
    }
  };

  AutoValue_DictReading(long id, String reading, Long id_in_entry) {
    super(id, reading, id_in_entry);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(id());
    if (reading() == null) {
      dest.writeInt(1);
    } else {
      dest.writeInt(0);
      dest.writeString(reading());
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

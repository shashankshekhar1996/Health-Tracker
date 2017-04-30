package health.vit.com.healthtracker.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by akshaymahajan on 22/01/17.
 */

public class Doctors implements Parcelable {
    public static final Parcelable.Creator<Doctors> CREATOR = new Parcelable.Creator<Doctors>() {
        @Override
        public Doctors createFromParcel(Parcel source) {
            return new Doctors(source);
        }

        @Override
        public Doctors[] newArray(int size) {
            return new Doctors[size];
        }
    };
    private Integer id;
    private String desc;
    private String name;
    private String phone;
    private String city;
    private String address;
    private String timings;

    public Doctors() {
    }

    public Doctors(Integer id, String name, String desc, String phone, String city, String address, String timings) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.phone = phone;
        this.city = city;
        this.address = address;
        this.timings = timings;
    }

    protected Doctors(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.desc = in.readString();
        this.phone = in.readString();
        this.city = in.readString();
        this.address = in.readString();
        this.timings = in.readString();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.city);
        dest.writeString(this.address);
        dest.writeString(this.timings);
    }
}

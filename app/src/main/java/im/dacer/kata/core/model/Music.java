package im.dacer.kata.core.model;

/**
 * Created by Dacer on 26/02/2018.
 */

public class Music {
    public long id;
    public String name;
    public Artist[] artists;
    public Album album;




    public class Artist {
        public long id;
        public String name;
    }
    public class Album {
        public long id;
        public String name;
    }
}

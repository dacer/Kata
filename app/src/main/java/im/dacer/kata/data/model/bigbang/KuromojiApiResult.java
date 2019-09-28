package im.dacer.kata.data.model.bigbang;

/**
 * Created by Dacer on 21/09/2019.
 */

public class KuromojiApiResult {
    public Token[] tokens;

    public class Token {
        public String surface;
        public int position;
        public boolean isKnown;
        public String[] features;

        public String getBaseForm() {
            return features[7];
        }
        public String getReading() {
            return features[6];
        }
        public String getPartOfSpeechLevel1() {
            return features[0];
        }
        public String getPartOfSpeechLevel2() {
            return features[2];
        }
        public String getPartOfSpeechLevel3() {
            return features[3];
        }
        public String getPartOfSpeechLevel4() {
            return features[4];
        }
        public String getConjugationType() {
            return features[5];
        }
    }
}

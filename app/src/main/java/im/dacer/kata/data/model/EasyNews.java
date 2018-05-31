package im.dacer.kata.data.model;

import org.immutables.value.Value;

@Value.Immutable
public abstract class EasyNews {
    public abstract String news_priority_number();
    public abstract String title();
    public abstract String news_publication_time();
    public abstract String news_web_url();
    public abstract String news_web_image_uri();
    public abstract String news_web_movie_uri();
    public abstract String news_easy_voice_uri();

}

package Q2;

import java.util.Objects;

/**
 * Song
 * Immutable model representing a track in the playlist.
 */
public class Song {
    private final String id;
    private final String title;
    private final String artist;
    private final int durationSeconds;

    public Song(String id, String title, String artist, int durationSeconds) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.durationSeconds = durationSeconds;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getDurationSeconds() { return durationSeconds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return durationSeconds == song.durationSeconds &&
                Objects.equals(id, song.id) &&
                Objects.equals(title, song.title) &&
                Objects.equals(artist, song.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, artist, durationSeconds);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d:%02d)", artist, title, durationSeconds / 60, durationSeconds % 60);
    }
}


package Q2;

import java.util.*;

/**
 * MusicPlaylistEditor
 * Encapsulates playlist editing operations across different List and Deque structures.
 */
public class MusicPlaylistEditor {

    private final List<Song> playlist;

    public MusicPlaylistEditor(List<Song> backingList) {
        this.playlist = backingList;
    }

    public void addSongAtEnd(Song song) {
        playlist.add(song);
    }

    public void insertSongAtFront(Song song) {
        playlist.add(0, song);
    }

    public void insertSongAtMiddle(Song song) {
        playlist.add(playlist.size() / 2, song);
    }

    public Song removeSongAtFront() {
        if (playlist.isEmpty()) return null;
        return playlist.remove(0);
    }

    public Song removeSongAtMiddle() {
        if (playlist.isEmpty()) return null;
        return playlist.remove(playlist.size() / 2);
    }

    public Song getKthSong(int k) {
        if (k < 0 || k >= playlist.size()) return null;
        return playlist.get(k);
    }

    public long iterateAllSongs() {
        long totalDuration = 0;
        for (Song song : playlist) {
            totalDuration += song.getDurationSeconds();
        }
        return totalDuration;
    }

    public int size() {
        return playlist.size();
    }

    public void clear() {
        playlist.clear();
    }
}


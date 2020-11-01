package org.hujohner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hujohner.model.LeaderboardEntry;
import org.hujohner.model.LocalLeaderboards;
import org.hujohner.model.SongHashData;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static final String BEAT_SABER_APPLICATION_DATA = System.getProperty("user.home") + "\\AppData\\LocalLow\\Hyperbolic Magnetism\\Beat Saber";
    public static final String BEAT_SABER_LOCAL_LEADERBOARDS = BEAT_SABER_APPLICATION_DATA + "\\LocalLeaderboards.dat";
    public static final String BEAT_SABER_SONG_HASH_DATA = BEAT_SABER_APPLICATION_DATA + "\\SongHashData.dat";

    private static Gson gson;
    private static LocalLeaderboards localLeaderboards;
    private static Map<String, SongHashData> songHashData;

    public static void main(String[] args) {
        System.out.println("Starting program...");
        Main.gson = new Gson();
        initialiseData();
        if (Main.localLeaderboards == null || Main.songHashData == null) {
            throw new RuntimeException("An error occurred.");
        }

        System.out.println("Checking for old song scores...");
        List<LeaderboardEntry> existingEntries = new ArrayList<>();
        Map<String, LeaderboardEntry> fixedEntries = new HashMap<>();
        Map<String, String> hashSongNameMap = getHashSongNameMap();
        for (LeaderboardEntry entry : Main.localLeaderboards.data) {
            if (entry.id.startsWith("custom_level_")) {
                String songId = removeDifficulty(entry.id.replaceFirst("custom_level_", ""));
                if (hashSongNameMap.containsValue(songId)) { // song name
                    existingEntries.add(entry);
                }

                if (hashSongNameMap.containsKey(songId)) { // song hash
                    String songName = hashSongNameMap.get(songId);
                    System.out.println("Found old scores for " + songName);
                    entry.id = entry.id.replaceFirst(songId, songName);
                    fixedEntries.put(entry.id, entry);
                }
            }
        }

        System.out.println("Merging existing scores...");
        for (LeaderboardEntry entry : existingEntries) {
            if (fixedEntries.containsKey(entry.id)) {
                LeaderboardEntry fixedEntry = fixedEntries.get(entry.id);
                entry.scores.addAll(fixedEntry.scores);
                Main.localLeaderboards.data.remove(fixedEntry);
            }
        }

        saveData();

        System.out.println("Done.");
        waitForEnter();
    }

    private static void initialiseData() {
        System.out.println("Reading files...");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(BEAT_SABER_LOCAL_LEADERBOARDS));
            Main.localLeaderboards = gson.fromJson(br, LocalLeaderboards.class);
            br = new BufferedReader(new FileReader(BEAT_SABER_SONG_HASH_DATA));
            Type mapType = new TypeToken<Map<String, SongHashData>>(){}.getType();
            Main.songHashData = gson.fromJson(br, mapType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void saveData() {
        System.out.println("Saving leaderboard...");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(BEAT_SABER_LOCAL_LEADERBOARDS));
            bw.write(gson.toJson(Main.localLeaderboards));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Map<String, String> getHashSongNameMap() {
        Map<String, String> map = new HashMap<>();
        for (String songPath : Main.songHashData.keySet()) {
            map.put(Main.songHashData.get(songPath).songHash, Paths.get(songPath).getFileName().toString());
        }
        return map;
    }

    private static String removeDifficulty(String s) {
        String result = s;
        // Easy, Normal, Hard, Expert, ExpertPlus
        if (s.endsWith("Easy")) {
            result = s.substring(0, s.lastIndexOf("Easy"));
        } else if (s.endsWith("Normal")) {
            result = s.substring(0, s.lastIndexOf("Normal"));
        } else if (s.endsWith("Hard")) {
            result = s.substring(0, s.lastIndexOf("Hard"));
        } else if (s.endsWith("Expert")) {
            result = s.substring(0, s.lastIndexOf("Expert"));
        } else if (s.endsWith("ExpertPlus")) {
            result = s.substring(0, s.lastIndexOf("ExpertPlus"));
        }
        // _, OneSaber, NoArrows, 360Degree, 90Degree
        if (s.endsWith("OneSaber")) {
            result = s.substring(0, s.lastIndexOf("OneSaber"));
        } else if (s.endsWith("NoArrows")) {
            result = s.substring(0, s.lastIndexOf("NoArrows"));
        } else if (s.endsWith("360Degree")) {
            result = s.substring(0, s.lastIndexOf("360Degree"));
        } else if (s.endsWith("90Degree")) {
            result = s.substring(0, s.lastIndexOf("90Degree"));
        }
        return result;
    }

    public static void waitForEnter() {
        Console c = System.console();
        if (c != null) {
            c.format("\nPress ENTER to proceed.\n");
            c.readLine();
        }
    }
}

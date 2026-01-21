import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Class representing the JukeBox.
 * A single simulation "plays" songs from the jukebox and ends once a repeat is
 *  encountered.
 * The main function runs fifty thousand simulations and tracks statistics
 *     such as: most played song, average number of songs played, run time...
 *
 * @author Liam Cui
 * @author Tyler Ronnenberg
 */
public class JukeBox{

    /** random number generator to be seeded */
    Random rand;
    /** tally of how many times the simulation has been run */
    int simCount;
    /** HashMap of songs mapped to a boolean value */
    HashMap<Song, Boolean> songPlayed;
    /** List of songs in the jukebox */
    List<Song> songs;
    /** List of the first five songs played in the entire set of simulations */
    ArrayList<Song> firstFiveSongs;
    /** Number of unique songs in the jukebox */
    int size;

    /**
     * Constructor initializes fields and builds a hashmap from a file
     *  containing a list of possible songs to be played.
     *
     * @param filename Filepath
     * @param seed Integer value for the Seed
     * @param simulations Number of simulations
     */
    public JukeBox(String filename, int seed, int simulations) {
        this.rand = new Random(seed);
        this.songPlayed = new HashMap<>();
        this.firstFiveSongs = new ArrayList<>();
        this.simCount = simulations;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = br.readLine()) != null) {
                // split each line into list no more than 4 long
                String[] temp = line.split("<SEP>", 4);
                // creates a new song using the 3rd and 4th element of temp
                Song songToAdd = new Song(temp[2], temp[3]);
                // add new song as a key with false value into hashmap
                songPlayed.put(songToAdd, false);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        //Generate list containing keys of the hashmap
        this.songs = new ArrayList<>(songPlayed.keySet());
        this.size = this.songs.size();
    }

    /**
     * Takes a random song from the by choosing a random index from songs List.
     * Checks the hashmap songPlayed to see if the song has been played. If
     * not, increments the song object's timesPlayed field. Then changes the
     * value of the songPlayed from false to true and returns the song. If the
     * song has been played then the method returns a null instead. The first 5
     * songs ever returned by this function are stored in the firstFiveSongs
     *  list.
     *
     * @return the song if it hasn't been played this simulation, else null
     */
    public Song playSong() {
        Song song = this.songs.get(rand.nextInt(this.size));
        // if not false
        if (!this.songPlayed.get(song)) {
            this.songPlayed.put(song, true);
            song.incrementTimesPlayed();
            //Populate firstFiveSongs list
            if (this.firstFiveSongs.size() < 5) {
                this.firstFiveSongs.add(song);
            }
            return song;
        } else {
            return null;
        }
    }

    /**
     * Plays songs from song list while incrementing a counter per song play
     * until a repeated song appears by calling playSong() until it returns a
     * null. Stores the returns of playSong() in a list of songs to reset the
     * Hashmap songPlayed values from true to false for the next simulation
     *  run.
     *
     * @return Number of songs played this simulation
     */
    public int runSimulation() {
        Song song = new Song("Trash", "Can");
        int counter = 0;
        ArrayList<Song> JukeList = new ArrayList<>();
        while (song != null) {
            song = this.playSong();
            JukeList.add(song);
            ++counter;
        }
        for (Song j : JukeList) {
            this.songPlayed.put(j, false);
        }
        return counter;
    }

    /**
     * Calls runSimulation() a number of times specified by the simCount field.
     * Tracks the total number of songs played for all simulations
     *
     * @return the total number of played songs
     */
    public int run50kSimulations() {
        int simCounter = 0;
        int songCounter = 0;
        while (simCounter < this.simCount) {
            songCounter += runSimulation();
            simCounter++;
        }
        // total played songs
        return songCounter;
    }

    /** getter for size */
    public int getSize() {
        return this.size;
    }

    /** getter for simCount */
    public int getSimCount() {
        return this.simCount;
    }

    /**
     * Method to find the most played song across all simulations
     *
     * @return The most played song
     */
    public Song getMostPlayedSong() {
        //faster than initializing as null and checking null each time
        Song MostPlayed = new Song("Trash", "Can");
        for (Song other: this.songs) {
            if (other.getTimesPlayed() > MostPlayed.getTimesPlayed()) {
                MostPlayed = other;
            }
        }
        return MostPlayed;
    }

    /**
     * Method for printing all songs in the jukebox by the artist with the most
     *  played song in alphabetical order.
     *
     * @param artist Artist of the most played song
     */
    public void printAuthorsSongs(String artist) {
        System.out.println("All songs alphabetically by \"" + artist  + "\":");
        ArrayList<Song> alphabet = new ArrayList<>();
        for (Song song : this.songs) {
            if (song.getArtist().equals(artist)) {
                alphabet.add(song);
            }
        }
        alphabet.sort(Comparator.comparing(Song::getSongName,
                String.CASE_INSENSITIVE_ORDER));

        for (Song song : alphabet) {
            System.out.println("        \"" + song.getSongName() + "\" with " +
                    song.getTimesPlayed() + " plays");
        }
    }

    /**
     * Method for printing the first five songs played in a set of simulations.
     */
    public void printFirstFiveSongs() {
        System.out.println("Printing first 5 songs played...");
        for (Song song: this.firstFiveSongs) {
            System.out.println("        " + song.toString());
        }
    }

    /**
     * Main method computes command line arguments and runs the simulations.
     * This method prints all desired statistics and importantly
     *      calculates the run time of the entire simulation.
     *
     * @param args filename followed by a seed
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java JukeBox filename seed");
            System.exit(1);
        }
        long starting, ending;
        starting = System.currentTimeMillis();
        JukeBox jukebox = new JukeBox(args[0], Integer.parseInt(args[1]),
                50000);
        System.out.println("JukeBox of " + jukebox.getSize() +
                " songs starts rockin...");
        int totalPlayed = jukebox.run50kSimulations();
        ending = System.currentTimeMillis();
        jukebox.printFirstFiveSongs();
        System.out.println("Simulation took " + ((ending - starting) / 1000) +
                " second/s");
        System.out.println("Number of simulations run: " +
                jukebox.getSimCount());
        System.out.println("Total number of songs played: " + totalPlayed);
        System.out.println("Average number of songs played per simulation to " +
                "get duplicate: " + totalPlayed/50000);
        Song MP = jukebox.getMostPlayedSong();
        System.out.println("Most played song: \"" + MP.getSongName() +
                "\" by \"" + MP.getArtist() + "\"");
        jukebox.printAuthorsSongs(MP.getArtist());
    }
}
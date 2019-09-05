package sample.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class was taken from a previous UI so you will find som methods that are not being used for this specific project
 * however, new features can be implemented from them.
 */
public class DataSource {

    public static final String DB_NAME = "music.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\Robert\\Desktop\\all java projects\\UIwithSQL\\" + DB_NAME;

    //we use indexes, because accessing data in database is faster if using index.
    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;

    public static final String TABLE_ARTIST = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    //SQL commands are better saved as constant strings fields instead of implemented a bunch of sb.append commands
    //(those in queryArtist() and queryArtistBySong() would need to be done this way as well) only for study purposes I left them both ways
    public static final String QUERY_ARTIST_FOR_SONG =
            "SELECT " + TABLE_ARTIST + "." + COLUMN_ARTIST_NAME + ", " +
                    TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + ", " +
                    TABLE_SONGS + "." + COLUMN_SONG_TRACK + " FROM " + TABLE_SONGS +
                    " INNER JOIN " + TABLE_ALBUMS + " ON " +
                    TABLE_SONGS + "." + COLUMN_SONG_ALBUM + " = " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ID +
                    " INNER JOIN " + TABLE_ARTIST + " ON " +
                    TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST + " = " + TABLE_ARTIST + "." + COLUMN_ARTIST_ID +
                    " WHERE " + TABLE_SONGS + "." + COLUMN_SONG_TITLE + " = \"";

    //for song sort command in SQL
    public static final String QUERY_ARTIST_FOR_SONG_SORT =
            " ORDER BY " + TABLE_ARTIST + "." + COLUMN_ARTIST_NAME + ", " +
                    TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " COLLATE NOCASE ";

    public static final String TABLE_ARTIST_SONG_VIEW = "artist_list";

    public static final String CREATE_ARTIST_FOR_SONG_VIEW = "CREATE VIEW IF NOT EXISTS " +
            TABLE_ARTIST_SONG_VIEW + " AS SELECT " + TABLE_ARTIST + "." + COLUMN_ARTIST_NAME + ", " +
            TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " AS " + COLUMN_SONG_ALBUM + ", " +
            TABLE_SONGS + "." + COLUMN_SONG_TRACK + ", " + TABLE_SONGS + "." + COLUMN_SONG_TITLE +
            " FROM " + TABLE_SONGS +
            " INNER JOIN " + TABLE_ALBUMS + " ON " + TABLE_SONGS +
            "." + COLUMN_SONG_ALBUM + " = " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ID +
            " INNER JOIN " + TABLE_ARTIST + " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST +
            " = " + TABLE_ARTIST + "." + COLUMN_ARTIST_ID +
            " ORDER BY " +
            TABLE_ARTIST + "." + COLUMN_ARTIST_NAME + ", " +
            TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + ", " +
            TABLE_SONGS + "." + COLUMN_SONG_TRACK;

    public static final String QUERY_VIEW_SONG_INFO = "SELECT " + COLUMN_ARTIST_NAME + ", " +
            COLUMN_SONG_ALBUM + ", " + COLUMN_SONG_TRACK + " FROM " + TABLE_ARTIST_SONG_VIEW +
            " WHERE " + COLUMN_SONG_TITLE + " = \"";

    public static final String QUERY_VIEW_SONG_INFO_PREP = "SELECT " + COLUMN_ARTIST_NAME + ", " +
            COLUMN_SONG_ALBUM + ", " + COLUMN_SONG_TRACK + " FROM " + TABLE_ARTIST_SONG_VIEW +
            " WHERE " + COLUMN_SONG_TITLE + " = ?"; // the char '?' is the only difference btw a prepared statement and a statement weak against injection attacks
    //you need now to create a prepared statement
    private PreparedStatement querySongFindArtistPrep;

    //constant fields to add a record into all three tables
    public static final String INSERT_ARTIST = "INSERT INTO " + TABLE_ARTIST +
            "(" + COLUMN_ARTIST_NAME + ") VALUES(?)";

    public static final String INSERT_ALBUMS = "INSERT INTO " + TABLE_ALBUMS +
            "(" + COLUMN_ALBUM_NAME + ", " + COLUMN_ALBUM_ARTIST + ") VALUES(?,?)";

    public static final String INSERT_SONG = "INSERT INTO " + TABLE_SONGS +
            "(" + COLUMN_SONG_TRACK + ", " + COLUMN_SONG_TITLE + ", " + COLUMN_SONG_ALBUM +
            ") VALUES(?,?,?)";

    public static final String QUERY_ARTIST = "SELECT " + COLUMN_ARTIST_ID + " FROM " +
            TABLE_ARTIST + " WHERE " + COLUMN_ARTIST_NAME + " = ?";

    public static final String QUERY_ALBUM = "SELECT " + COLUMN_ALBUM_ID + " FROM " +
            TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_NAME + " = ?";

    public static final String QUERY_SONG = "SELECT " + COLUMN_SONG_ID + " FROM " +
            TABLE_SONGS + " WHERE " + COLUMN_SONG_TITLE + " = ?";

    //SELECT * FROM albums WHERE artist = ? ORDER BY name COLLATE NOCASE
    public static final String QUERY_ALBUMS_BY_ARTIST_ID = "SELECT * FROM " + TABLE_ALBUMS +
            " WHERE " + COLUMN_ALBUM_ARTIST + " = ? ORDER BY " + COLUMN_ALBUM_NAME + " COLLATE NOCASE";

    public static final String UPDATE_ARTIST_NAME = "UPDATE " + TABLE_ARTIST + " SET " +
            COLUMN_ARTIST_NAME + " = ? WHERE " + COLUMN_ARTIST_ID + " = ?";

    private PreparedStatement insertIntoArtists;
    private PreparedStatement insertINtoAlbums;
    private PreparedStatement insertINtoSongs;
    private PreparedStatement queryArtist;
    private PreparedStatement queryAlbum;
    private PreparedStatement querySong;
    private PreparedStatement queryAlbumsByAritstId;
    private PreparedStatement updateArtistName;

    private Connection conn;

    private static DataSource instance = new DataSource(); //this is "thread safe", always create singletons like this

    private DataSource() {} //singleton

    public static DataSource getInstance() {
        return instance;
    }

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            querySongFindArtistPrep = conn.prepareStatement(QUERY_VIEW_SONG_INFO_PREP);
            insertIntoArtists = conn.prepareStatement(INSERT_ARTIST, Statement.RETURN_GENERATED_KEYS); //return id (link btw tables)
            insertINtoAlbums = conn.prepareStatement(INSERT_ALBUMS, Statement.RETURN_GENERATED_KEYS);
            insertINtoSongs = conn.prepareStatement(INSERT_SONG);
            queryArtist = conn.prepareStatement(QUERY_ARTIST);
            queryAlbum = conn.prepareStatement(QUERY_ALBUM);
            querySong = conn.prepareStatement(QUERY_SONG);
            queryAlbumsByAritstId = conn.prepareStatement(QUERY_ALBUMS_BY_ARTIST_ID);
            updateArtistName = conn.prepareStatement(UPDATE_ARTIST_NAME);

            return true;
        } catch (SQLException e) {
            System.out.println("Error connecting to database " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {

            if (querySongFindArtistPrep != null)
                querySongFindArtistPrep.close();

            if (insertIntoArtists != null)
                insertIntoArtists.close();

            if (insertINtoAlbums != null)
                insertINtoAlbums.close();

            if (insertINtoSongs != null)
                insertINtoSongs.close();

            if (queryArtist != null)
                queryArtist.close();

            if (queryAlbum != null)
                queryAlbum.close();

            if (querySong != null)
                querySong.close();

            if (queryAlbumsByAritstId != null)
                queryAlbumsByAritstId.close();

            if (updateArtistName != null)
                updateArtistName.close();

            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing file " + e.getMessage());
        }
    }

    /**
     * This method "extracts" the data from db to the java compiler.
     *
     * @return Data from database as a List.
     */
    public List<Artist> queryArtist(int sortOrder) {

        // this is building a command...
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_ARTIST);

        if (sortOrder != ORDER_BY_NONE) {
            sb.append(" ORDER BY ");
            sb.append(COLUMN_ARTIST_NAME);
            sb.append(" COLLATE NOCASE ");

            if (sortOrder == ORDER_BY_DESC)
                sb.append("DESC");
            else
                sb.append("ASC");
        }
        //...at the end it would look like "SELECT * FROM artists ORDER BY COLLATE NOCASE ASC"


        //try with resources
        //this closes both connections, independently of what happens in the try block, no need to do a finally block
        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sb.toString())) //sb contains the whole command
        {
            //prepare list or collection
            List<Artist> artists = new ArrayList<>();

            while (results.next()) {

                //Simulate delay so that we can see the progress bar (only purpose of the sleep method)
                try {
                    Thread.sleep(20);
                }catch (InterruptedException e) {
                    System.out.println("Loading artist list was interrupted. " + e.getMessage());
                }

                //creating artist obj from database and...
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID)); //faster (goes straight to the column index #, instead of looping thru every column finding the match for the string)
                artist.setName(results.getString(INDEX_ARTIST_NAME)); //faster

                //...adding it to java
                artists.add(artist);
            }

            return artists;

        } catch (SQLException e) {
            System.out.println("Query failed " + e.getMessage());
            return null;
        }
    }//end of method


    public List<Album> queryAlbumsForArtistId(int id) {

        try {
            queryAlbumsByAritstId.setInt(1, id);
            ResultSet resultSet = queryAlbumsByAritstId.executeQuery();

            List<Album> albums = new ArrayList<>();

            while (resultSet.next()) {
                Album album = new Album();
                album.setId(resultSet.getInt(1));
                album.setName(resultSet.getString(2));
                album.setArtistId(id);
                albums.add(album);
            }

            return albums;

        } catch(SQLException e) {
            System.out.println("Error querying album for artist. " + e.getMessage());
            return null;
        }
    }

    /**
     * This method will retrieve all albums from a given artist as a list
     *
     * @param artistName Artist to retrieve its albums.
     * @param sortOrder  Either ascending, descending or no order at all.
     * @return List containing all albums from @artistName
     */
    public List<String> queryAlbumsArtist(String artistName, int sortOrder) {
        //This is what we will be doing next
        //SELECT albums.name FROM albums INNER JOIN artists ON albums.artist = artists._id WHERE artists.name = "Aerosmith"
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(TABLE_ALBUMS + '.');
        sb.append(COLUMN_ALBUM_NAME);
        sb.append(" FROM ");
        sb.append(TABLE_ALBUMS);
        sb.append(" INNER JOIN ");
        sb.append(TABLE_ARTIST);
        sb.append(" ON ");
        sb.append(TABLE_ALBUMS);
        sb.append('.');
        sb.append(COLUMN_ALBUM_ARTIST);
        sb.append(" = ");
        sb.append(TABLE_ARTIST);
        sb.append('.');
        sb.append(COLUMN_ARTIST_ID);
        sb.append(" WHERE ");
        sb.append(TABLE_ARTIST);
        sb.append('.');
        sb.append(COLUMN_ARTIST_NAME);
        sb.append(" = \"");
        sb.append(artistName);
        sb.append("\"");

        //sort order
        if (sortOrder != ORDER_BY_NONE) {
            sb.append(" ORDER BY ");
            sb.append(TABLE_ALBUMS);
            sb.append('.');
            sb.append(COLUMN_ALBUM_NAME);
            sb.append(" COLLATE NOCASE ");

            if (sortOrder == ORDER_BY_DESC)
                sb.append("DESC");
            else
                sb.append("ASC");
        }

        //Now that command is built, lets extract the data and save it to a List
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sb.toString())) {

            List<String> albums = new ArrayList<>();

            while (resultSet.next()) {
                albums.add(resultSet.getString(1)); //only 1 column returned from query
            }

            return albums;

        } catch (SQLException e) {
            System.out.println("Error querying albums by artist " + e.getMessage());
            return null;
        }
    }

    //returns amount of records in a table
    public int getCount(String table) {

        String sql = "SELECT COUNT(*) AS count FROM " + table;

        try(Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

            return resultSet.getInt("count");
        } catch(SQLException e) {
            System.out.println("Error counting records " + e.getMessage());
            return -1; //invalid number of records
        }
    }

    /**
     * This method will create a new table view in SQL
     * @return Confirmation of creation of table
     */
    public boolean createViewForSongArtist() {

        try(Statement statement = conn.createStatement()){

            statement.execute(CREATE_ARTIST_FOR_SONG_VIEW);

            return true;
        } catch(SQLException e) {
            System.out.println("Error printing view " + e.getMessage());
            return false; //invalid number of records
        }
    }

    private int insertArtist(String name) throws SQLException {

        queryArtist.setString(1, name); //append name into the SELECT artist FROM...
        ResultSet resultSet = queryArtist.executeQuery(); //SQL query executed

        if (resultSet.next()) { //if SQL returns a record, is bc the artist already exists
            return resultSet.getInt(1);
        } else { //create new record

            insertIntoArtists.setString(1,name);
            int affectedRows = insertIntoArtists.executeUpdate();

            if (affectedRows != 1) //inserting only 1 record
                throw new SQLException("Couldn't insert artist.");
        }

        ResultSet generatedId = insertIntoArtists.getGeneratedKeys();
        if (generatedId.next())
            return generatedId.getInt(1);
        else
            throw new SQLException("Couldn't get _id for artist");
    }

    private int insertAlbum(String name, int artist_id) throws SQLException {

        queryAlbum.setString(1, name); //append name into the SELECT album FROM...
        ResultSet resultSet = queryAlbum.executeQuery(); //SQL query executed

        if (resultSet.next()) { //if SQL returns a record, is bc the artist already exists
            return resultSet.getInt(1);
        } else { //create new record

            insertINtoAlbums.setString(1,name);
            insertINtoAlbums.setInt(2,artist_id);
            int affectedRows = insertINtoAlbums.executeUpdate();

            if (affectedRows != 1) //inserting only 1 record
                throw new SQLException("Couldn't insert album."); //throws error only, it is handled in insertSong()
        }

        ResultSet generatedId = insertINtoAlbums.getGeneratedKeys();
        if (generatedId.next())
            return generatedId.getInt(1);
        else
            throw new SQLException("Couldn't get _id for album"); //same
    }

    public boolean updateArtistName(int id, String newName) {

        try{
            updateArtistName.setString(1, newName);
            updateArtistName.setInt(2, id);
            int affectedRows = updateArtistName.executeUpdate();

            return affectedRows == 1;

        }catch(SQLException e) {
            System.out.println("Update artist name failed. " + e.getMessage());
            return false;
        }
    }
}//end of class
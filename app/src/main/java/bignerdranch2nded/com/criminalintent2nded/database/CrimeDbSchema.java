package bignerdranch2nded.com.criminalintent2nded.database;

/**
 * Created by Juan on 9/14/2015.
 */
public class CrimeDbSchema {

    public static final class CrimeTable {

        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }

}

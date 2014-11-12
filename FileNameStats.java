
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package name.wolneykien.filenamestats;

import java.lang.String;
import java.lang.Exception;
import java.lang.RuntimeException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.System;

public class FileNameStats {

    public static String PROG_NAME =
        System.getProperty( "program.name", "FileNameStats" );
    public static String PROG_VERSION = "0.1.0";

    public static String DEF_PATTERN = "-([0-9]+)$";
    public static String DEF_STATNAME = "stats.txt";
    
    private Pattern pattern = null;
    private long summary = 0;
    private String statName = null;


    public FileNameStats() {
        this.summary = 0;
    }

    public FileNameStats( String pattern ) {
        this();
        this.pattern = Pattern.compile( pattern );
    }

    public FileNameStats( String pattern, String statName ) {
        this();
        this.pattern = Pattern.compile( pattern );
        this.statName = statName;
    }

    public FileNameStats( FileNameStats stats ) {
        this();
        this.pattern = stats.pattern;
        this.statName = stats.statName;
    }


    public void calculatePath( String path )
    throws java.io.IOException
    {
        this.calculatePath( new File( path ) );
    }

    public void calculatePath( File path )
    throws java.io.IOException
    {
        if ( path.isDirectory() ) {
            File statfile = new File ( path, this.statName );
            statfile.delete();
            File[] list = path.listFiles();
            if ( list != null ) {
                for ( File f : list ) {
                    if ( f.isDirectory() ) {
                        FileNameStats stats = new FileNameStats( this );
                        stats.calculatePath( f.getAbsolutePath() );
                        long weight = stats.getSummary();
                        incSummary( weight );
                        appendStatfile( statfile, f.getName() + "/", weight );
                    } else {
                        long weight = getNameWeight( f.getName() );
                        incSummary( weight );
                        if ( weight != 0 ) {
                            appendStatfile( statfile, f.getName(), weight );
                        }
                    }
                }
            }
        } else {
            long weight = getNameWeight( path.getName() );
            incSummary( weight );
        }
    }

    public long getNameWeight( String name ) {
        Matcher m = this.pattern.matcher( name );
        if ( m.find() ) {
            if ( m.groupCount() > 0 ) {
                long weight = Long.parseLong( m.group( 1 ) );
                return 1 * weight;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    public long getSummary () {
        return this.summary;
    }

    public void incSummary( long weight ) {
        this.summary = this.summary + weight;
    }

    protected void appendStatfile( File statfile, String name, long weight )
    throws java.io.IOException
    {
        PrintWriter statWriter =
            new PrintWriter ( new FileWriter( statfile, true ),
                              true );

        statWriter.format( "%s\t%d\n", name, weight );
        statWriter.close();
    }


    public static void main ( String[] args ) {
        FileNameStats stats;

        if ( args.length > 0 ) {
            if ( args[0].equals( "-h" ) ||
                 args[0].equals( "--help" ) ||
                 args[0].equals( "--usage" ) )
            {
                printUsage();
                System.exit( 0 );
            }
                                 
            if ( args.length > 1 ) {
                stats = new FileNameStats( args[0], args[1] );
            } else {
                stats = new FileNameStats( args[0], DEF_STATNAME );
            }
        } else {
            stats = new FileNameStats( DEF_PATTERN, DEF_STATNAME );
        }

        try {
            if ( args.length > 2 ) {
                stats.calculatePath( new File( args[2] ) );
            } else {
                stats.calculatePath( System.getProperty( "user.dir" ) );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public static void printUsage() {
        System.out.print(
 PROG_NAME + " writes per-directory reports in the given filesystem tree\n" +
"making arithmetic calculations on file name parts.\n" );

        System.out.println();
        System.out.print(
"Usage: " + PROG_NAME + " [ PATTERN ] [ STATSNAME ] [ DIR ]\n"
);
        
        System.out.println();
        System.out.print(
"Arguments:\n"
);

        System.out.println();
        System.out.print(
"    PATTERN  the regular expression used to select the files; may contain\n" +
"             ONE capture group that matches an integer number; use '.*' to\n" +
"             count all files; *default* value is '" + DEF_PATTERN + "'" +
                                                           " that\n" +
"             extracts a number from the end of each filename and results\n" +
"             in calculation of the sum of these numbers;\n" );

        System.out.println();
        System.out.println(
"    STATNAME  the name of the stats-file report written in each" +
                                                         " recursively\n" +
"              processed directory; *default* value is '" + DEF_STATNAME +
                                                                "';\n" );

        System.out.println();
        System.out.println(
"    DIR       the directory to traverse into; *default* is the current\n" +
"              working directory." );
    }

}

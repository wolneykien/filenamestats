
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

package name.wolneykien.FileNameStats;

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

    public static String DEF_PATTERN = "-([0-9]+)$";
    public static String DEF_STATFILE = "stats.txt";
    
    private Pattern pattern = null;
    private double summary = 0;
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
            File[] list = path.listFiles();
            if ( list != null ) {
                for ( File f : list ) {
                    if ( f.isDirectory() ) {
                        FileNameStats stats = new FileNameStats( this );
                        stats.calculatePath( f.getAbsolutePath() );
                        double weight = stats.getSummary();
                        incSummary( weight );
                        appendStatfile( path, f.getName(), weight );
                    } else {
                        double weight = getNameWeight( f.getName() );
                        incSummary( weight );
                        appendStatfile( path, f.getName(), weight );
                    }
                }
            }
        } else {
            double weight = getNameWeight( path.getName() );
            incSummary( weight );
        }
    }

    public double getNameWeight( String name ) {
        Matcher m = this.pattern.matcher( name );
        if ( m.find() ) {
            if ( m.groupCount() > 0 ) {
                double weight = Double.parseDouble( m.group( 1 ) );
                return 1.0 * weight;
            } else {
                return 1.0;
            }
        } else {
            return 0.0;
        }
    }

    public double getSummary () {
        return this.summary;
    }

    public void incSummary( double weight ) {
        this.summary = this.summary + weight;
    }

    protected void appendStatfile( File dir, String name, double weight )
    throws java.io.IOException
    {
        PrintWriter statWriter =
            new PrintWriter (
                  new FileWriter( new File ( dir, this.statName ),
                                  true ),
                  true );

        statWriter.format( "%s\\t%.2f\\n", name, weight );
        statWriter.close();
    }


    public static void main ( String[] args ) {
        FileNameStats stats;

        if ( args.length > 0 ) {
            if ( args.length > 1 ) {
                stats = new FileNameStats( args[0], args[1] );
            } else {
                stats = new FileNameStats( args[0],
                                           FileNameStats.DEF_STATFILE );
            }
        } else {
            stats = new FileNameStats( FileNameStats.DEF_PATTERN,
                                       FileNameStats.DEF_STATFILE );
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

}

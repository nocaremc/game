package nocare.util.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import nocare.geometry.Skelaton;

public class SkelatonLoader {
	public static Skelaton loadSkelaton( String filePath ) {
		Gson gson = new Gson();
		BufferedReader buffer;
		FileReader reader;
		File file = new File( filePath );

		if ( !file.exists() ) {
			try {
				throw new FileNotFoundException( "Could not locate Skelaton file: " + filePath );
			}
			catch ( FileNotFoundException e ) {
				e.printStackTrace();
			}
		}

		try {
			reader = new FileReader( file );
			buffer = new BufferedReader( reader );
			Skelaton skelaton = gson.fromJson( buffer, Skelaton.class );
			//obj.toString();
			buffer.close();
			return skelaton;
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}

		return null; // TODO fix
	}
}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is my helper for consolidating all photos into annual directories.
 * The month and days are being flattened.
 *  
 * @author kompseye
 */
public class Helper 
{
	
	// class member variable used during size computation. base 1024
	private static final String[] IEC_BINARY = new String[]{"", "K", "M", "G", "T", "P", "E"};
	
	// class member variable used during size computation. base 10
	private static final String[] SI_DECIMAL = new String[]{"", "K", "M", "G", "T", "P", "E"};
	
	// class member variable for tracking the number of files processed
	private int numberFiles = 0;
	
	// class member variable for writing to a file to flatten the photo directories
	private BufferedWriter outputStream;
	
	// class member variable for writing to a file to restore the flattened directories
	private BufferedWriter restorePictures;
	
	// class member variable for the name of the root folder, i.e. source directory
	private String folderName;
	
	// class member variable for the name of the 
	private String burnFolderName;
	
	/**
	 * 
	 * @param copyFromRootFolder
	 * @param copyToRootFolder
	 * @throws IOException
	 */
	public Helper(String copyFromRootFolder, String copyToRootFolder) throws IOException 
	{
		this.folderName = copyFromRootFolder;
		this.burnFolderName = copyToRootFolder + "/" + folderName;
		File burnFolder = new File(this.burnFolderName);
		boolean val = burnFolder.mkdir();
		
		// create an output file as a complete bash script which when executed
		// will perform the copying from the copyFrom folder to copyTo folder.
		outputStream = new BufferedWriter(new FileWriter(new File(this.burnFolderName + "/output.sh")));
		outputStream.write("#!/bin/bash");
		outputStream.newLine();
		outputStream.newLine();
		
		// create an output file as a complete bash script which when executed
		// will perform a restoration of copied files back to original folder.
		// it is important to create this file now since after the copy the
		// folder hierarchy of YYYY/MM/DD will be flattened and therefore lost.
		restorePictures = new BufferedWriter(new FileWriter(new File(this.burnFolderName + "/restore.sh")));
		restorePictures.write("#!/bin/bash");
		restorePictures.newLine();
		restorePictures.newLine();
	}
	
	/**
	 * This method calculate the size of the folder starting at the parameter
	 * directory. Recursion is used to traverse the folder hierarchy. Since
	 * the context of this helper class is for moving photographs and movies, 
	 * only those file types are examined for the copy and restore output files.
	 * 
	 * @param directory folder to start determining size
	 * @return folder size in bytes
	 * @throws IOException
	 */
	public long determineFolderSizeRecusively(File directory) throws IOException
	{
		//System.out.println("directory is:" + directory.getAbsolutePath());
		// length returns the number of bytes for a file, i.e. the size
		long length = 0;
		
		// if the parameter directory is in fact a directory, the
		// listFiles method will return a list of files, else null
		// for more information see java.io.File#listFiles()
		for (File file : directory.listFiles())
		{
			if (file.isFile())
			{
				if (file.getName().endsWith(".jpg") ||
				    file.getName().endsWith(".JPG") ||
				    file.getName().endsWith(".jpeg") ||
				    file.getName().endsWith(".AVI") ||
				    file.getName().endsWith(".MOV") ||
				    file.getName().endsWith(".MPG") ||
				    file.getName().endsWith(".mov") ||
				    file.getName().endsWith(".png") ||
				    file.getName().endsWith(".psd") ||
				    file.getName().endsWith(".tiff") ||
				    file.getName().endsWith(".JPEG"))
				{					
					numberFiles++;
					length += file.length();
					outputStream.write("cp -pn \"" + file.getAbsolutePath() + "\" \"" + burnFolderName + "\"");
					outputStream.newLine();
					restorePictures.write("cp -pn \"" + file.getName() + "\" \"" + file.getParent() + "\"");
					restorePictures.newLine();
					
				}
			}
			else // this is a directory, enter recursive call
			{
				//System.out.println("[" + converter(length) + "]"); 
				length += determineFolderSizeRecusively(file);
			}
		}
		//System.out.println("Length: " + length);
		//System.out.println("Compare size: " + MAXDVDSIZE.compareTo(length));
		return length;
	}
	
	/**
	 * This method coverts the parameter to the largest possible size. Credit 
	 * for the algorithm goes to cited URL below. 
	 * 
	 * @param bytes to convert
	 * @return conversion as String
	 * @see http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	 * @see http://en.wikipedia.org/wiki/Gigabyte
	 */
	public static String convertSizeUsingBinary(long bytes)
	{
		String conversion = Long.toString(bytes);
		
		for (int exponent = 6; exponent > 0; exponent--)
		{
			// determine the largest divisor for the bytes
			double largestDivisor = Math.pow(1024, exponent);
			if (bytes > largestDivisor)
			{
				conversion = String.format("%3.1f %s", bytes / largestDivisor, IEC_BINARY[exponent]);
				break;
			}
		}
		
		return conversion;
	}
	
	/**
	 * This method coverts the parameter to the largest possible size. Credit 
	 * for the algorithm goes to cited URL below. 
	 * 
	 * @param bytes to convert
	 * @return conversion as String
	 * @see http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	 * @see http://en.wikipedia.org/wiki/Gigabyte
	 */
	public static String convertSizeUsingDecimal(long bytes)
	{
		String conversion = Long.toString(bytes);
		
		for (int exponent = 6; exponent > 0; exponent--)
		{
			// determine the largest divisor for the bytes			
			double largestDivisor = Math.pow(10, (exponent * 3) );
			if (bytes > largestDivisor)
			{
				conversion = String.format("%3.1f %s", bytes / largestDivisor, SI_DECIMAL[exponent]);
				break;
			}
		}
		
		return conversion;
	}	
	
	/**
	 * Accessor method for the number of files.
	 * 
	 * @return the number of file
	 */
	public int getNumberFiles() 
	{ 
		return numberFiles; 
	}
	
	/**
	 * Close the file.
	 * 
	 * @throws IOException if there is a problem closing the file
	 */
	public void closeFile() throws IOException 
	{ 
		outputStream.close(); 
		restorePictures.close();
	}
	

	/**
	 * The driver of this program
	 * 
	 * @param args
	 * @throws IOException if there is a problem running this program 
	 */
	public static void main(String[] args) throws IOException 
	{
		//for (int i = 2002; i <= 2010; ++i)
		for (int i = 2011; i <= 2011; ++i)
		{
			System.out.println("###YEAR###" + i);
			String iphotoLibrary = "/Volumes/MyBook/Pictures/iPhoto Library/" + String.valueOf(i);
			File inputFile = new File(iphotoLibrary);
			Helper myHelper = new Helper(inputFile.getName(), "/Users/nerditup/Desktop/BURN/DISC");
			long size = myHelper.determineFolderSizeRecusively(new File(iphotoLibrary));
			System.out.println("Size is: " + size);
			System.out.println("Number of files: " + myHelper.getNumberFiles());
			System.out.println("Converted Size (binary) is: " + Helper.convertSizeUsingBinary(size));
			System.out.println("Converted Size (decimal) is: " + Helper.convertSizeUsingDecimal(size));
			myHelper.closeFile();
			System.out.println();
		}

		
	}

}
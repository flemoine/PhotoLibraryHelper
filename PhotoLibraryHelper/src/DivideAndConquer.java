import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class is my helper for consolidating all photos into annual directories.
 * The month and days are being flattened.
 *  
 * @author nerditup
 *
 */
public class DivideAndConquer 
{
	
	// http://en.wikipedia.org/wiki/Gigabyte
	private static final String[] IEC_BINARY = new String[]{"", "K", "M", "G", "T", "P", "E"};
	private static final String[] SI_DECIMAL = new String[]{"", "K", "M", "G", "T", "P", "E"};
	
	private int dvdNumber = 1;
	private int numberFiles = 0;
	
	private static final Long MAXDVDSIZE = 5046586570L;
	
	//private BufferedWriter outputStream;
	//private BufferedWriter restorePictures;
	
	private String folderName;
	private String burnFolderName;
	
	/**
	 * This class only has static methods and is not to be instantiated.
	 * @throws IOException 
	 */
	public DivideAndConquer(String folderName, String burnFolderName) throws IOException 
	{
		this.folderName = folderName;
		this.burnFolderName = burnFolderName + "/" + folderName;
		File burnFolder = new File(this.burnFolderName);		
		
		//outputStream = new BufferedWriter(new FileWriter(new File(this.burnFolderName + "/output.sh")));
		//outputStream.write("#!/bin/bash");
		//outputStream.newLine();
		//outputStream.newLine();
		//restorePictures = new BufferedWriter(new FileWriter(new File(this.burnFolderName + "/restore.sh")));
		//restorePictures.write("#!/bin/bash");
		//restorePictures.newLine();
		//restorePictures.newLine();
	}
	
	public long determineFolderSize(File directory) throws IOException
	{
		System.out.println("directory is:" + directory.getAbsolutePath());
		// length returns the number of bytes for a file, i.e. the size
		long length = 0;
		
		// if the parameter directory is in fact a directory, the
		// listFiles method will return a list of files, else null
		// for more information see java.io.File#listFiles()
		for (File file : directory.listFiles())
		{
			if (file.isFile())
			{					
				if (MAXDVDSIZE.compareTo(length+file.length()) < 0 ) // if MAXDVDSIZE is less than length
				{
					System.out.println("MAX SIZE FOR DVD WILL BE REACHED WITH NEXT FILE. CURRENT SIZE: " + length);
					dvdNumber++;
					System.out.println("DVD Number: " + dvdNumber);
				}
				numberFiles++;
				length += file.length();
				
				//outputStream.write("cp -p '" + file.getAbsolutePath() + "' '" + burnFolderName + "'");
				//outputStream.newLine();
				
				//restorePictures.write("cp -p '" + file.getName() + "' '" + file.getParent() + "'");
				//restorePictures.newLine();
 			}		
		}
		//System.out.println("Length: " + length);
		//System.out.println("Compare size: " + MAXDVDSIZE.compareTo(length));
		return length;
	}
	
	/**
	 * Credit to cited URL 
	 * 
	 * @param bytes to convert
	 * @return conversion as String
	 * @see http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	 */
	public static String converter(long bytes)
	{
		String conversion = Long.toString(bytes);
		
		for (int exponent = 6; exponent > 0; exponent--)
		{
			// determine the largest divisor for the bytes
			double largestDivisor = Math.pow(1024, exponent);
			//double largestDivisor = Math.pow(10, 3);
			if (bytes > largestDivisor)
			{
				conversion = String.format("%3.1f %s", bytes / largestDivisor, IEC_BINARY[exponent]);
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
		//outputStream.write("end of file"); 
		//outputStream.close(); 
		//restorePictures.close();
	}
	

	/**
	 * The driver of this program
	 * 
	 * @param args
	 * @throws IOException if there is a problem running this program 
	 */
	public static void main(String[] args) throws IOException 
	{
		for (int i = 2008; i <= 2008; ++i)
		{
			System.out.println("###YEAR###" + i);
			String divide = "/Users/nerditup/Desktop/BURN/DISC/" + String.valueOf(i);
			File inputFile = new File(divide);
			DivideAndConquer myHelper = new DivideAndConquer(inputFile.getName(), "/Users/nerditup/Desktop/BURN/DISC");
			long size = myHelper.determineFolderSize(new File(divide));
			System.out.println("Size is: " + size);
			System.out.println("Number of files: " + myHelper.getNumberFiles());
			// convert to kilobytes
			System.out.println("Converted Size is: " + DivideAndConquer.converter(size));
			myHelper.closeFile();
			System.out.println();
		}

		
	}

}
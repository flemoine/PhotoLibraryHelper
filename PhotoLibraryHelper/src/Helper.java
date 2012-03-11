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
	
	// http://en.wikipedia.org/wiki/Gigabyte
	private static final String[] IEC_BINARY = new String[]{"", "K", "M", "G", "T", "P", "E"};
	private static final String[] SI_DECIMAL = new String[]{"", "K", "M", "G", "T", "P", "E"};
	
	private int numberFiles = 0;
	
	private BufferedWriter outputStream;
	private BufferedWriter restorePictures;
	
	private String folderName;
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
		this.burnFolderName = burnFolderName + "/" + folderName;
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
	 * 
	 * @param directory
	 * @return
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
		for (int i = 2009; i <= 2009; ++i)
		{
			System.out.println("###YEAR###" + i);
			String iphotoLibrary = "/Volumes/MyBook/Pictures/iPhoto Library/" + String.valueOf(i);
			File inputFile = new File(iphotoLibrary);
			Helper myHelper = new Helper(inputFile.getName(), "/Users/nerditup/Desktop/BURN/DISC");
			long size = myHelper.determineFolderSizeRecusively(new File(iphotoLibrary));
			System.out.println("Size is: " + size);
			System.out.println("Number of files: " + myHelper.getNumberFiles());
			System.out.println("Converted Size is: " + Helper.converter(size));
			myHelper.closeFile();
			System.out.println();
		}

		
	}

}
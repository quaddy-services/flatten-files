package de.quaddyservices.files.flatten;

import java.io.File;

/**
 *
 */
public class FlattenFiles {
	public static void main(String[] args) {
		FlattenFiles tempFlattenFiles = new FlattenFiles();
		tempFlattenFiles.setFromDir(new File(args[0]));
		tempFlattenFiles.setToDir(new File(args[1]));
		tempFlattenFiles.flatten();
	}

	private File fromDir;
	private File toDir;

	/**
	 *
	 */
	private void flatten() {
		System.out.println("Flatten all files and subfolders from " + fromDir.getAbsolutePath() + " to " + toDir.getAbsolutePath());

		System.out.println("Finished.");
	}

	/**
	 * @see #fromDir
	 */
	public File getFromDir() {
		return fromDir;
	}

	/**
	 * @see #fromDir
	 */
	public void setFromDir(File aFromDir) {
		fromDir = aFromDir;
	}

	/**
	 * @see #toDir
	 */
	public File getToDir() {
		return toDir;
	}

	/**
	 * @see #toDir
	 */
	public void setToDir(File aToDir) {
		toDir = aToDir;
	}
}

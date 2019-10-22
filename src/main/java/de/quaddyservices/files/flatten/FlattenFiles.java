package de.quaddyservices.files.flatten;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 */
public class FlattenFiles {
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Usage: <fromDir> <toDir>");
			System.exit(1);
		}
		FlattenFiles tempFlattenFiles = new FlattenFiles();
		tempFlattenFiles.setFromDir(new File(args[0]));
		tempFlattenFiles.setToDir(new File(args[1]));
		if (args.length >= 3) {
			tempFlattenFiles.setFileWildcard(args[2]);
		}
		tempFlattenFiles.flatten();
	}

	private Pattern fileRegExPattern;

	/**
	 *
	 */
	private void setFileWildcard(String aString) {
		String fileRegEx = aString.replaceAll("\\.", "\\\\.").replaceAll("\\?", ".").replaceAll("\\*", ".*");
		fileRegExPattern = Pattern.compile(fileRegEx, Pattern.CASE_INSENSITIVE);
		System.out.println("Use FileWildcard " + aString + " == File RegEx: " + fileRegExPattern + " (CASE_INSENSITIVE)");
	}

	private File fromDir;
	private File toDir;
	private int alreadyCopiedFiles = 0;

	/**
	 * @throws IOException
	 *
	 */
	private void flatten() throws IOException {
		System.out.println("Flatten all files and subfolders from " + fromDir.getAbsolutePath() + " to " + toDir.getAbsolutePath());

		File tempFromDir = getFromDir();
		if (!tempFromDir.exists()) {
			throw new IllegalArgumentException("Soure dir does not exist: " + tempFromDir.getAbsolutePath());
		}
		List<FileToProcess> tempFilesToProcess = collectFilesToProcess(tempFromDir);
		System.out.println("Found " + tempFilesToProcess.size() + " files.");
		prefixDuplicatFileNames(tempFilesToProcess);

		copyFiles(tempFilesToProcess);

		System.out.println("Finished.");
	}

	/**
	 * @throws IOException
	 *
	 */
	private void copyFiles(List<FileToProcess> aFilesToProcess) throws IOException {
		sort(aFilesToProcess);
		File tempTargetDir = getToDir();
		if (tempTargetDir.mkdirs()) {
			System.out.println("Created target directory " + tempTargetDir.getAbsolutePath());
		}
		long tempNextInfo = System.currentTimeMillis() + 5000;
		System.out.println("Copy " + aFilesToProcess.size() + " files to " + getToDir().getAbsolutePath() + " ...");
		for (int i = 0; i < aFilesToProcess.size(); i++) {
			FileToProcess tempFileToProcess = aFilesToProcess.get(i);
			if (tempNextInfo < System.currentTimeMillis()) {
				tempNextInfo = System.currentTimeMillis() + 5000;
				double tempPercent = (i * 1.0) / (aFilesToProcess.size() * 1.0) * 100.0;
				File tempFile = tempFileToProcess.getFile();
				System.out.println("Copy " + Math.round(tempPercent) + "% (current " + tempFile.getPath() + ")");
			}
			copyFile(tempFileToProcess);
		}
		if (alreadyCopiedFiles > 0) {
			System.out.println(alreadyCopiedFiles + " were skipped as size and date matched already in " + tempTargetDir.getAbsolutePath());
		}
	}

	/**
	 * @throws IOException
	 *
	 */
	private void copyFile(FileToProcess aFileToProcess) throws IOException {
		File tempSource = aFileToProcess.getFile();
		File tempTarget = new File(getToDir().getAbsolutePath() + "/" + aFileToProcess.getTargetName());
		if (tempTarget.exists()) {
			if (tempTarget.lastModified() == tempSource.lastModified() && tempTarget.length() == tempSource.length()) {
				// Assume file already same.
				alreadyCopiedFiles++;
				return;
			}
			throw new IllegalArgumentException("Cannot copy. Target file already exists: " + tempTarget.getAbsolutePath() + " and is different:" //
					+ "\nSource: " + new Date(tempSource.lastModified()) + " " + tempSource.length() + " bytes,"//
					+ "\nTarget: " + new Date(tempTarget.lastModified()) + " " + tempTarget.length() + " bytes,"//
			);
		}
		byte[] tempBuff = new byte[1024 * 1024];
		try (InputStream tempIn = new FileInputStream(tempSource)) {
			try (OutputStream tempOut = new FileOutputStream(tempTarget)) {
				int tempRead = tempIn.read(tempBuff);
				while (tempRead > 0) {
					tempOut.write(tempBuff, 0, tempRead);
					tempRead = tempIn.read(tempBuff);
				}
			}
		} catch (IOException e) {
			throw new IOException("Error copying " + tempSource + " to " + tempTarget, e);
		}
		if (!tempTarget.setLastModified(tempSource.lastModified())) {
			System.out.println("Could not set file date for target " + tempTarget.getAbsolutePath());
		}
	}

	/**
	 *
	 */
	private void sort(List<FileToProcess> aFilesToProcess) {
		Collections.sort(aFilesToProcess, new Comparator<FileToProcess>() {

			@Override
			public int compare(FileToProcess aO1, FileToProcess aO2) {
				File f1 = aO1.getFile();
				File f2 = aO2.getFile();
				return f1.getPath().compareTo(f2.getPath());
			}
		});
	}

	/**
	 * Duplicate files are prefixed with
	 */
	private void prefixDuplicatFileNames(List<FileToProcess> aFilesToProcess) {
		while (true) {
			Map<FileToProcess, FileToProcess> tempUniqueFiles = new HashMap<>();
			List<FileToProcess> tempDuplicates = new ArrayList<>();
			for (FileToProcess tempFileToProcess : aFilesToProcess) {
				FileToProcess tempDuplicate = tempUniqueFiles.put(tempFileToProcess, tempFileToProcess);
				if (tempDuplicate != null) {
					tempDuplicates.add(tempDuplicate);
					tempDuplicates.add(tempFileToProcess);
				}
			}
			if (tempDuplicates.size() == 0) {
				break;
			}
			System.out.println("Prefix " + tempDuplicates.size() + " duplicate files with directory names.");
			for (FileToProcess tempFileToProcess : tempDuplicates) {
				tempFileToProcess.prefix();
			}
		}
	}

	/**
	 *
	 */
	private List<FileToProcess> collectFilesToProcess(File aFromDir) {
		List<FileToProcess> tempFilesToProcess = new ArrayList<>();
		File[] tempFiles = aFromDir.listFiles();
		if (tempFiles != null) {
			for (File tempFile : tempFiles) {
				if (tempFile.isDirectory()) {
					tempFilesToProcess.addAll(collectFilesToProcess(tempFile));
				} else {
					String tempName = tempFile.getName();
					if (fileRegExPattern == null || fileRegExPattern.matcher(tempName).matches()) {
						tempFilesToProcess.add(new FileToProcess(tempFile));
					}
				}
			}
		}
		return tempFilesToProcess;
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

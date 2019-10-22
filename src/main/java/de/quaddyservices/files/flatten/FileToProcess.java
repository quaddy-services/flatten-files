package de.quaddyservices.files.flatten;

import java.io.File;

/**
 *
 */
public class FileToProcess {

	private File baseDir;
	private String targetName;
	private File file;
	private String compareName;

	/**
	 *
	 */
	public FileToProcess(File aFile) {
		file = aFile;
		baseDir = aFile.getParentFile();
		targetName = aFile.getName();
		compareName = targetName.toLowerCase();
	}

	/**
	 *
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compareName == null) ? 0 : compareName.hashCode());
		return result;
	}

	/**
	 *
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FileToProcess other = (FileToProcess) obj;
		if (compareName == null) {
			if (other.compareName != null) {
				return false;
			}
		} else if (!compareName.equals(other.compareName)) {
			return false;
		}
		return true;
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return "FileToProcess [baseDir=" + baseDir + ", targetName=" + targetName + ", file=" + file + ", compareName=" + compareName + "]";
	}

	/**
	 *
	 */
	public void prefix() {
		if (baseDir == null) {
			throw new IllegalArgumentException("No more prefix left for " + file.getAbsolutePath() + " in " + this);
		}
		String tempPrefix = baseDir.getName();
		baseDir = baseDir.getParentFile();
		targetName = tempPrefix + "_" + targetName;
		compareName = targetName.toLowerCase();
	}

	/**
	 * @see #file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @see #targetName
	 */
	public String getTargetName() {
		return targetName;
	}

}

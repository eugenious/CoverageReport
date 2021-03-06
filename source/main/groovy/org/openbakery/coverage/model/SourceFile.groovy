package org.openbakery.coverage.model

import org.apache.commons.io.FilenameUtils

import java.text.SimpleDateFormat

/**
 * Created by René Pirringer
 */
class SourceFile {

	String filename
	List<SourceLine> sourceLines
	List<Function> methods = []

	SourceFile(List<String> lines, String baseDirectory) {
		def currentMethod = null
		sourceLines = []

		lines.eachWithIndex { value, index ->

			if (value.startsWith("  ------------------") || value.startsWith("  |")) {
				return;
			}


			if (filename == null) {
				File file = new File(parseFilename(value))
				// make sure that the given path is a file path, because the value is a absolute path test if it is really, otherwise the string is not a file
				if (file.isAbsolute()) {
					filename = file.absolutePath - baseDirectory
				}
			} else {
				SourceLine sourceLine = new SourceLine(value)
				if (currentMethod == null && sourceLine.hits != SourceLine.NOT_A_NUMBER) {
					currentMethod = new Function()
					methods << currentMethod
				} else if (currentMethod != null && sourceLine.hits == SourceLine.NOT_A_NUMBER) {
					currentMethod = null;
				}
				if (currentMethod) {
					currentMethod.sourceLines << sourceLine
				}
				sourceLines << sourceLine
			}
		}

	}

	String parseFilename(String line) {
		if (line.endsWith(":")) {
			return line[0..-2]
		}
		return line
	}

	String getFileBasename() {
		String filenameEscaped = filename.replace("/", "_")
		return FilenameUtils.getBaseName(filenameEscaped)
	}

	List<SourceLine>getSourceLinesCovered() {
		return sourceLines.findAll { it.hits != SourceLine.NOT_A_NUMBER }
	}

	long getLinesNumber() {
		return sourceLines.size()
	}

	long getLinesCovered() {
		return sourceLines.count { it.hits > 0 }
	}

	long getLinesNotCovered() {
		return sourceLines.count { it.hits == 0 }
	}

	long getLinesExecuted() {
		return getLinesCovered() + getLinesNotCovered()
	}

	double getCoverage() {
		return SourceLine.getCoverage(sourceLines)
	}

	String getCoverageInPercent() {
		return ((int)(getCoverage()*1000))/10
	}


	String getName() {
		return FilenameUtils.getName(filename)
	}

	Coverage getCoverageRate() {
		return getCoverageRate(getCoverage())
	}

	String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat()
		return dateFormat.format(new Date())
	}

	static long getLinesCovered(List<SourceFile> sourceFiles) {
		if (sourceFiles == null || sourceFiles.size() == 0) {
			return 0
		}
		return sourceFiles.sum { it.getLinesCovered()}
	}

	static long getLinesNotCovered(List<SourceFile> sourceFiles) {
		if (sourceFiles == null || sourceFiles.size() == 0) {
			return 0
		}
		return sourceFiles.sum { it.getLinesNotCovered()}
	}

	static long getLinesExecuted(List<SourceFile> sourceFiles) {
		if (sourceFiles == null || sourceFiles.size() == 0) {
			return 0
		}
		return sourceFiles.sum { it.getLinesExecuted()}
	}

	static long getTotalLines(List<SourceFile> sourceFiles) {
		if (sourceFiles == null || sourceFiles.size() == 0) {
			return 0
		}
		return sourceFiles.sum {
			it.sourceLines.size()
		}
	}

	static double getCoverage(List<SourceFile> sourceFiles) {
		if (sourceFiles == null || sourceFiles.size() == 0) {
			return 0
		}
		return getLinesCovered(sourceFiles) / getLinesExecuted(sourceFiles)
	}


	static Coverage getCoverageRate(double coverage) {
		if (coverage < 0.5) {
			return Coverage.Poor
		}
		if (coverage > 0.75) {
			return Coverage.Good
		}
		return Coverage.Ok
	}

	@Override
	public String toString() {
		return "SourceFile{" +
						"filename='" + filename + '\'' +
						'}';
	}


}

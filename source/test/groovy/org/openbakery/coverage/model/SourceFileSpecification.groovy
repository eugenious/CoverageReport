package org.openbakery.coverage.model

import org.apache.commons.io.FileUtils
import spock.lang.Specification

/**
 * Created by René Pirringer
 */
class SourceFileSpecification extends Specification {


	List<String> getReportLines(String name) {
		File dataFile = new File("source/test/resource/", name)
		return FileUtils.readLines(dataFile)
	}


	def "load report data"() {
		when:
		List<String> lines = getReportLines("OBTableViewSection.txt")

		then:
		lines.size() == 92
	}


	def "parse data"() {
		when:
		SourceFile data = new SourceFile(getReportLines("OBTableViewSection.txt"), null);

		then:
		data.filename == "/Users/rene/workspace/openbakery/OBTableViewController/Core/Source/OBTableViewSection.m"
	}

	def "parse lines"() {
		when:
		SourceFile data = new SourceFile(getReportLines("OBTableViewSection.txt"), null);

		then:
		data.sourceLines.size == 91
	}


	def "parse line 1"() {
		when:
		SourceFile data = new SourceFile( getReportLines("OBTableViewSection.txt"), null);
		SourceLine line = data.sourceLines.get(0)

		then:
		line.number == 1
		line.hits == SourceLine.NOT_A_NUMBER
	}

	def "parse line 21"() {
		when:
		SourceFile data = new SourceFile( getReportLines("OBTableViewSection.txt"), null);
		SourceLine line = data.sourceLines.get(20)

		then:
		line.number == 21
		line.hits == 32
		line.code == "- (id)init {"
	}


	def "parse methods"() {
		when:
		SourceFile data = new SourceFile( getReportLines("OBTableViewSection.txt"), null);

		then:
		data.filename.endsWith("OBTableViewSection.m")
		data.sourceLines.size() == 91
		data.methods.size() == 7
		data.methods.first().name == "- (id)init"
		data.methods.first().coverage == 1.0
	}

	def "parse methods - isEqualToSection"() {
		when:
		SourceFile data = new SourceFile( getReportLines("OBTableViewSection.txt"), null);

		then:
		data.filename.endsWith("OBTableViewSection.m")
		data.sourceLines.size() == 91
		data.methods.size() == 7
		data.methods[4].name == "- (BOOL)isEqualToSection:(OBTableViewSection *)section"
		(data.methods[4].coverage*100).intValue() == 66
	}


	def "data all"() {
		when:
		SourceFile data = new SourceFile( getReportLines("OBTableViewSection.txt"), null);

		then:
		data.filename.endsWith("OBTableViewSection.m")
		data.sourceLines.size() == 91
		data.linesCovered == 39
		data.linesNotCovered == 20
		(data.coverage*1000).intValue() == 661
		data.coverageInPercent == "66.1"
		data.coverageRate == Coverage.Ok
	}

	def "coverate rate poor"() {
		when:
		SourceFile data = new SourceFile( getReportLines("OBTableViewSection.txt"), null);
		data.sourceLines.each { it.hits = 0 }

		then:
		data.coverageRate == Coverage.Poor
	}

	def "coverate rate good"() {
		when:
		SourceFile data = new SourceFile( getReportLines("OBTableViewSection.txt"), null);
		data.sourceLines.each { it.hits = 1 }

		then:
		data.coverageRate == Coverage.Good
	}


	def "parse data inline"() {
		when:
		SourceFile data = new SourceFile(getReportLines("CGAffineTransform.txt"), null);

		then:
		data.filename.endsWith("CGAffineTransform.h")
		data.sourceLines.size() == 147
	}

}

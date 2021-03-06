package org.openbakery.coverage

import org.apache.commons.io.FileUtils
import spock.lang.Specification

/**
 * Created by René Pirringer
 */
class CoverageReportSpecification extends Specification {

	File tmp

	def setup() {
		tmp = new File(System.getProperty("java.io.tmpdir"), "coverage")
	}

	def tearDown() {
		FileUtils.deleteDirectory(tmp)
	}

	def "main help"() {
		when:
		def args = ["--help"] as String[]
		CoverageReport coverageReport = new CoverageReport(args)

		then:
		coverageReport.options.h
	}

	def "main help short"() {
		when:
		def args = ["-h"] as String[]
		CoverageReport coverageReport = new CoverageReport(args)

		then:
		coverageReport.options.h
	}

	def "params"() {
		when:
		def args = []
		args << "--profdata"
		args << "profData"
		args << "--binary"
		args << "binary"
		args << "--include"
		args << "*"

		CoverageReport coverageReport = new CoverageReport(args as String[])
		coverageReport.processOptions()

		then:
		coverageReport.report.profileData.toString() == "profData"
		coverageReport.report.binary.toString() == "binary"
		coverageReport.report.include == "*"
	}


	def "params 2"() {
		when:
		def args = []
		args << "--profdata"
		args << "1"
		args << "--binary"
		args << "2"
		args << "--include"
		args << "3"

		CoverageReport coverageReport = new CoverageReport(args as String[])
		coverageReport.processOptions()

		then:
		coverageReport.report.profileData.toString() == "1"
		coverageReport.report.binary.toString() == "2"
		coverageReport.report.include == "3"
	}


	def "params type"() {
		when:
		def args = []
		args << "--profdata"
		args << "1"
		args << "--binary"
		args << "2"
		args << "--include"
		args << "3"
		args << "--type"
		args << "html"

		CoverageReport coverageReport = new CoverageReport(args as String[])
		coverageReport.processOptions()

		then:
		coverageReport.report.profileData.toString() == "1"
		coverageReport.report.binary.toString() == "2"
		coverageReport.report.include.toString() == "3"
		coverageReport.report.type == Report.Type.HTML
	}

	def "params output directory"() {
		when:
		def args = []
		args << "--profdata"
		args << '1'
		args << "--binary"
		args << '2'
		args << "--output"
		args << "cov"

		CoverageReport coverageReport = new CoverageReport(args as String[])
		coverageReport.processOptions()

		then:
		coverageReport.report.profileData.toString() == "1"
		coverageReport.report.binary.toString() == "2"
		coverageReport.report.destinationPath.toString() == "cov"
	}


	def "create text report"() {
		Report report = Mock(Report)
		when:
		def args = []

		args << "--profdata"
		args << "Coverage.profdata"
		args << "--binary"
		args << "Demo"

		CoverageReport coverageReport = new CoverageReport(args as String[])
		coverageReport.report = report
		coverageReport.run()

		then:
		1 * report.create()
	}

	def "report base directory"() {
		when:
		CoverageReport coverageReport = new CoverageReport()
		coverageReport.processOptions()

		then:
		coverageReport.report.baseDirectory == new File("").absolutePath + "/"

	}

	def "test exclude"() {
		when:
		def args = []
		args << "--exclude"
		args << "*"

		CoverageReport coverageReport = new CoverageReport(args as String[])
		coverageReport.processOptions()

		then:
		coverageReport.report.exclude == "*"
	}

	def "test title"() {
		when:
		def args = []
		args << "--title"
		args << "MyProject"

		CoverageReport coverageReport = new CoverageReport(args as String[])
		coverageReport.processOptions()

		then:
		coverageReport.report.title == "MyProject"
	}
}

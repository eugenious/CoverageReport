package org.openbakery.coverage.report

import org.apache.commons.io.FileUtils
import org.openbakery.coverage.model.SourceFile
import spock.lang.Specification

/**
 * Created by René Pirringer
 */
class HTMLReportSpecification extends Specification {

	HTMLReport htmlReport
	File tmp

	def setup() {
		htmlReport = new HTMLReport()
		tmp = new File(System.getProperty("java.io.tmpdir"), "coverage")
	}

	def tearDown() {
		FileUtils.deleteDirectory(tmp)
	}

	SourceFile getSourceFile() {
		File dataFile = new File("source/test/resource/", "OBTableViewSection.txt")
		return new SourceFile(FileUtils.readLines(dataFile), "/Users/rene/workspace/openbakery/OBTableViewController/")
	}

	ReportData getReportData() {
		List<SourceFile> sourceFiles = []
		sourceFiles << getSourceFile();
		return new ReportData(sourceFiles)
	}

	def "generated report exists"() {
		given:
		htmlReport.bootstrap = null
		ReportData data = getReportData()

		when:
		htmlReport.generate(data, tmp)

		then:
		new File(tmp, "index.html").exists()
	}


	def "bootstrap download"() {
		given:
		ReportData data = getReportData()

		when:
		htmlReport.generate(data, tmp)

		then:
		new File(tmp, "bootstrap/css/bootstrap.css").exists()
	}


	XmlParser getXmlParser() {
		def factory = javax.xml.parsers.SAXParserFactory.newInstance()
		factory.validating = false
		return new XmlParser(factory.newSAXParser().getXMLReader());
	}

	def "test html report"() {
		given:
		htmlReport.bootstrap = null
		ReportData data = getReportData()

		when:
		htmlReport.generate(data, tmp)
		File xmlFile = new File(tmp, "index.html")
		def parser= getXmlParser()
		def html =  parser.parse(xmlFile)

		then:
		html.body.div.table.tbody.tr.size() == 1
		html.body.div.table.tbody.tr[0].td[0].value()[0] == "Core/Source/OBTableViewSection.m"
		html.body.div.table.tbody.tr[0].td[2].value()[0] == "91"
		html.body.div.table.tbody.tr[0].td[3].value()[0] == "59"
		html.body.div.table.tbody.tr[0].td[4].value()[0] == "20"
	}
}
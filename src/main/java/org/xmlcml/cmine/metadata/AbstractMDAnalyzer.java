package org.xmlcml.cmine.metadata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.metadata.AbstractMetadata.Type;
import org.xmlcml.cmine.metadata.crossref.CrossrefMD;
import org.xmlcml.cmine.util.RectangularTable;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class AbstractMDAnalyzer {

	private static final Logger LOG = Logger.getLogger(AbstractMDAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	private CProject cProject;
	private Multimap<CTree, File> reservedFileByCTree;
	private HashMap<Type, Map<CTree, AbstractMetadata>> metadataMapByTypeMap;
	protected Map<CTree, AbstractMetadata> metadataByCTreeMap;
	protected MetadataObjects metadataObjects;
	private RectangularTable csvTable;
	private AbstractMetadata currentMetadata;
	private CTree currentCTree;

	public void setCProject(File directory) {
		this.cProject = directory == null ? null : new CProject(directory);
	}

	public CTreeList getCTreeList() {
		return cProject == null ? null : this.cProject.getCTreeList();
	}

	public Multimap<CTree, File> getOrCreateCTreeFileMap(String reservedName) {
		if (reservedFileByCTree == null) {
			reservedFileByCTree = cProject.extractCTreeFileMapContaining(reservedName);
		}
		return reservedFileByCTree;
	}

	/** creates a map of metadata (of a given type) by CTree 
	 * 
	 * @param type
	 * @return
	 */
	public Map<CTree, AbstractMetadata> getOrCreateMetadataMapByCTreeMap(AbstractMetadata.Type type) {
		getOrCreateCTreeFileMap(type.getJsonFilename());
		metadataByCTreeMap = new HashMap<CTree, AbstractMetadata>();
		for (CTree cTree : reservedFileByCTree.keySet()) {
			AbstractMetadata metadata = cTree.getOrCreateMetadata(type);
			metadata.setCTree(cTree);
			metadataByCTreeMap.put(cTree, metadata);
		}

		return metadataByCTreeMap;
	}

	protected void setCProject(CProject cProject) {
		this.cProject = cProject;
	}

	public void setMetadataObjects(MetadataObjects metadataObjects) {
		this.metadataObjects = metadataObjects;
	}

//	public void addMetadataRowToCSVTable(CTree cTree) {
//		this.currentCTree = cTree;
//		
//			CSVTable csvTable = metadataObjects.getOrCreateCSVTable();
//			
//			currentMetadata = metadataByCTreeMap.get(currentCTree);
//			if (currentMetadata != null) {
//				metadataObjects.getStringKeys().addAll(currentMetadata.getStringKeySet());
//				metadataObjects.getStringListKeys().addAll(currentMetadata.getStringListKeySet());
//				csvTable.clearRow();
//			    csvTable.addCell(currentMetadata.getURL());
//			    csvTable.addCell(currentMetadata.getTitle());
//			    csvTable.addCell(currentMetadata.getDate());
//			    csvTable.addCell(currentMetadata.getFulltextPDFURL());
//			    csvTable.addCell(currentMetadata.hasDownloadedFulltextPDF());
//			    csvTable.addCell(currentMetadata.getFulltextHTMLURL());
//			    csvTable.addCell(currentMetadata.hasDownloadedFulltextHTML());
//			    csvTable.addCell(currentMetadata.getFulltextXMLURL());
//			    csvTable.addCell(currentMetadata.hasDownloadedFulltextXML());
//			    csvTable.addCell(currentMetadata.getDOI());
//			    addString(currentMetadata.getPublisher(), metadataObjects.getPublisherMultiset(), csvTable);
//			    csvTable.addCell(currentMetadata.getVolume());
//			    csvTable.addCell(currentMetadata.getAuthorListAsStrings());
//			    csvTable.addCell(currentMetadata.getType());
//			    csvTable.addCell(currentMetadata.getIssue());
//			    csvTable.addCell(currentMetadata.getFirstPage());
//			    csvTable.addCell(currentMetadata.getDescription());
//			    csvTable.addCell(currentMetadata.getAbstract());
//			    csvTable.addCell(currentMetadata.getJournal());
//			    addString(currentMetadata.getLicense(), metadataObjects.getLicenseSet(), csvTable);
//			    csvTable.addCell(currentMetadata.getLinks());
//			    csvTable.addCell(currentMetadata.getCopyright());
//			    csvTable.addCell(currentMetadata.getISSN());
//			    addString(currentMetadata.getKeywords(), metadataObjects.getKeywordSet(), csvTable);
//			    addString(currentMetadata.getPrefix(), metadataObjects.getPrefixSet(), csvTable);
//				csvTable.addCell(currentMetadata.hasQuickscrapeMetadata());
//			    csvTable.addCell(currentMetadata.hasCrossrefMetadata());
//			    csvTable.addCell(currentMetadata.hasPublisherMetadata());
//				csvTable.addCurrentRow();
//	//				LOG.debug("M "+metadata.stringListValueMap.keys());
//			}
//		}

	public void addMetadataRowToCSVTable1(CTree cTree, List<String> columnHeaders) {
		currentCTree = cTree;
		RectangularTable csvTable = metadataObjects.getOrCreateCSVTable();
		currentMetadata = metadataByCTreeMap.get(currentCTree);
		if (currentMetadata != null) {
			metadataObjects.getStringKeys().addAll(currentMetadata.getStringKeySet());
			metadataObjects.getStringListKeys().addAll(currentMetadata.getStringListKeySet());
			csvTable.clearRow();
			for (String columnHeader : columnHeaders) {
				addCellForColumn(csvTable, columnHeader);
			}
			csvTable.addCurrentRow();
		}
	}
	
	private void addCellForColumn(RectangularTable csvTable, String header) {
		String value = "";
		if (header == null) {
		} else {
			value = getStringValue(header);
		}
		csvTable.addCell(value);
	}
	
	private String getStringValue(String header) {
		currentMetadata = metadataByCTreeMap.get(currentCTree);
		String value = "";
		if (header == AbstractMetadata.URL) {
		    value = currentMetadata.getURL();
		} else if (header == AbstractMetadata.TITLE) {
		    value = currentMetadata.getTitle();
		} else if (header == AbstractMetadata.DATE) {
			value = currentMetadata.getDate();
		} else if (header == AbstractMetadata.PDFURL) {
		    value = currentMetadata.getFulltextPDFURL();
		} else if (header == AbstractMetadata.DOWNLOADED_PDF) {
		    value = currentMetadata.hasDownloadedFulltextPDF();
		} else if (header == AbstractMetadata.HTMLURL) {
		    value = currentMetadata.getFulltextHTMLURL();
		} else if (header == AbstractMetadata.DOWNLOADED_HTML) {
		    value = currentMetadata.hasDownloadedFulltextHTML();
		} else if (header == AbstractMetadata.XMLURL) {
		    value = currentMetadata.getFulltextXMLURL();
		} else if (header == AbstractMetadata.DOWNLOADED_XML) {
		    value = currentMetadata.hasDownloadedFulltextXML();
		} else if (header == AbstractMetadata.DOI) {
		    value = currentMetadata.getDOI();
		} else if (header == AbstractMetadata.VOLUME) {
		    value = currentMetadata.getVolume();
		} else if (header == AbstractMetadata.AUTHOR_LIST) {
		    value = currentMetadata.getAuthorListAsStrings().toString();
		} else if (header == AbstractMetadata.TYPE) {
		    value = currentMetadata.getType();
		} else if (header == AbstractMetadata.ISSUE) {
		    value = currentMetadata.getIssue();
		} else if (header == AbstractMetadata.FIRST_PAGE) {
		    value = currentMetadata.getFirstPage();
		} else if (header == AbstractMetadata.DESCRIPTION) {
		    value = currentMetadata.getDescription();
		} else if (header == AbstractMetadata.ABSTRACT) {
		    value = currentMetadata.getAbstract();
		} else if (header == AbstractMetadata.JOURNAL) {
		    value = currentMetadata.getJournal();
		} else if (header == AbstractMetadata.LINKS) {
		    value = currentMetadata.getLinks();
		} else if (header == AbstractMetadata.COPYRIGHT) {
		    value = currentMetadata.getCopyright();
		} else if (header == AbstractMetadata.ISSN) {
		    value = currentMetadata.getISSN();
		} else if (header == AbstractMetadata.QUICKSCRAPE_MD) {
			value = currentMetadata.hasQuickscrapeMetadata();
		} else if (header == AbstractMetadata.CROSSREF_MD) {
		    value = currentMetadata.hasCrossrefMetadata();
		} else if (header == AbstractMetadata.PUBLISHER_MD) {
		    value = currentMetadata.hasPublisherMetadata();
		} else if (header == AbstractMetadata.LICENSE) {
		    value = currentMetadata.getLicense();
		} else if (header == AbstractMetadata.PUBLISHER) {
			value = currentMetadata.getPublisher();
		} else if (header == AbstractMetadata.KEYWORDS) {
			value = currentMetadata.getKeywords();
		} else if (header == AbstractMetadata.PREFIX) {
			value = currentMetadata.getPrefix();
		}
		return value;
	}

	private void addToMultiset(String header) {
		currentMetadata = metadataByCTreeMap.get(currentCTree);
		if (currentMetadata == null) {
			LOG.debug("null metadata");
			return;
		}
		String value = "";
		if (header == null) {
		} else if (header == AbstractMetadata.LICENSE) {
		    value = addString(currentMetadata.getLicense(), metadataObjects.getLicenseSet());
		} else if (header == AbstractMetadata.PUBLISHER) {
			value = addString(currentMetadata.getPublisher(), metadataObjects.getPublisherMultiset());
		} else if (header == AbstractMetadata.KEYWORDS) {
			value = addString(currentMetadata.getKeywords(), metadataObjects.getKeywordSet());
		} else if (header == AbstractMetadata.PREFIX) {
			value = addString(currentMetadata.getPrefix(), metadataObjects.getPrefixSet());
		}
	}

	private void addString(String value, Multiset<String> valueSet, RectangularTable csvTable) {
		if (value == null) {
			value = "";
		} else if (value.equals("[]")) {
			value = "";
		}
		csvTable.addCell(value);
		valueSet.add(value);
	}

	private String addString(String value, Multiset<String> valueSet) {
		if (value == null) {
			value = "";
		} else if (value.equals("[]")) {
			value = "";
		}
		valueSet.add(value);
		return value;
	}

	private void addStringArray(String value, Multiset<String> valueSet, RectangularTable csvTable) {
		csvTable.addCell(value);
		valueSet.add(value);
	}

	public void writeDOIs() {
		LOG.warn("DOIS NYI");
	}

	public void writeDOIs(File file) {
		LOG.warn("DOIS NYI");
	}

	public CProject getCProject() {
		return cProject;
	}

	public void addRowsToTable(List<String> headers, Type type) {
		MetadataObjects metadataObjects = new MetadataObjects();
		metadataObjects.setMetadataAnalyzer(this);
		csvTable = metadataObjects.getOrCreateCSVTable(headers);
		getOrCreateMetadataMapByCTreeMap(type);
		CTreeList cTreeList = getCProject().getCTreeList();
		for (CTree cTree : cTreeList) {
			addMetadataRowToCSVTable1(cTree, headers);
		}
	}
	
	public RectangularTable getCSVTable() {
		return csvTable;
	}

	public void writeCsvFile(File file) throws IOException {
		writeCsvFile(file.getAbsolutePath());
	}

	public void writeCsvFile(String filename) throws IOException {
		csvTable.writeCsvFile(filename);
	}

	public MetadataObjects getMetadataObjects() {
		return metadataObjects;
	}

	public void createMultisets() {
		CTreeList cTreeList = getCProject().getCTreeList();
		for (CTree cTree : cTreeList) {
			this.currentCTree = cTree;
			currentMetadata = metadataByCTreeMap.get(currentCTree);
		    addToMultiset(AbstractMetadata.PUBLISHER);
		    addToMultiset(AbstractMetadata.LICENSE);
		    addToMultiset(AbstractMetadata.KEYWORDS);
		    addToMultiset(AbstractMetadata.PREFIX);
		}
	}

}

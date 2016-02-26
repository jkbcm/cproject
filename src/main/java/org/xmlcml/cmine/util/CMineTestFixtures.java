package org.xmlcml.cmine.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.ProjectSnippetsTree;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

public class CMineTestFixtures {

	private static final Logger LOG = Logger.getLogger(CMineTestFixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static void cleanAndCopyDir(File sourceDir, File targetDir) throws IOException {
		if (targetDir.exists()) FileUtils.forceDelete(targetDir);
		FileUtils.copyDirectory(sourceDir, targetDir);
	}


	public static ProjectSnippetsTree createProjectSnippetsTree(File testZikaFile, String snippetsName) throws IOException {
		File targetDir = new File("target/relevance/zika");
		CMineTestFixtures.cleanAndCopyDir(testZikaFile, targetDir);
		Element snippetsTreeXML = XMLUtil.parseQuietlyToDocument(new File(targetDir, snippetsName)).getRootElement();;
		ProjectSnippetsTree projectsSnippetsTree = ProjectSnippetsTree.createProjectSnippetsTree(snippetsTreeXML);
		return projectsSnippetsTree;
	}



}

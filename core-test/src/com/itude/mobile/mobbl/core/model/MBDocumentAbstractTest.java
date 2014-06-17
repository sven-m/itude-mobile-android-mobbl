package com.itude.mobile.mobbl.core.model;

import com.itude.mobile.android.util.AssetUtil;
import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl.core.MBApplicationCore;
import com.itude.mobile.mobbl.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBMvcConfigurationParser;

import android.test.ApplicationTestCase;

public abstract class MBDocumentAbstractTest extends
		ApplicationTestCase<MBApplicationCore> {

	private byte[] configData;
	private byte[] jsonDocumentData;
	private byte[] xmlDocumentData;
	private MBConfigurationDefinition config;

	protected byte[] getConfigData() {
		return configData;
	}

	private void setConfigData(byte[] configData) {
		this.configData = configData;
	}

	protected byte[] getJsonDocumentData() {
		return jsonDocumentData;
	}

	private void setJsonDocumentData(byte[] jsonDocumentData) {
		this.jsonDocumentData = jsonDocumentData;
	}

	protected byte[] getXmlDocumentData() {
		return xmlDocumentData;
	}

	private void setXmlDocumentData(byte[] xmlDocumentData) {
		this.xmlDocumentData = xmlDocumentData;
	}

	protected MBConfigurationDefinition getConfig() {
		return config;
	}

	private void setConfig(MBConfigurationDefinition config) {
		this.config = config;
	}

	public MBDocumentAbstractTest() {
		super(MBApplicationCore.class);
	}

	@Override
	protected void setUp() throws Exception {
		DataUtil.getInstance().setContext(getContext());
		setJsonDocumentData(AssetUtil.getInstance().getByteArray(
				"unittests/testdocument.txt"));
		setXmlDocumentData(AssetUtil.getInstance().getByteArray(
				"unittests/testdocument.xml"));
		setConfigData(AssetUtil.getInstance().getByteArray(
				"unittests/config_unittests.xml"));

		MBMvcConfigurationParser configParser = new MBMvcConfigurationParser();
		setConfig((MBConfigurationDefinition) configParser.parseData(
				configData, "config"));
	}

}

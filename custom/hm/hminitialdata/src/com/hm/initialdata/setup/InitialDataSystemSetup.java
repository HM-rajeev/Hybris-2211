/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.initialdata.setup;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import com.hm.initialdata.constants.HmInitialDataConstants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class provides hooks into the system's initialization and update processes.
 */
@SystemSetup(extension = HmInitialDataConstants.EXTENSIONNAME)
public class InitialDataSystemSetup extends AbstractSystemSetup
{
	private static final String IMPORT_CORE_DATA = "importCoreData";
	private static final String IMPORT_SAMPLE_DATA = "importSampleData";
	private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";
	private static final String IMPORT_HM_ELECTRONICS = "importhmElectronics";
	private static final String IMPORT_HM_ELECTRONICS_SAMPLEDATA = "importHmElectronicsSampledata";
	private static final String IMPORT_HM_CONTENT_CATALOG = "importhmContentCatalog";
	private static final String IMPORT_HM_PRODUCT_CATALOG = "importhmProductCatalog";
	private static final String IMPORT_HM_PRODUCT_CATALOG_MULTI_D = "importhmProductCatalogmultid";
	private static final String IMPORT_HM_PRODUCT_CATALOG_SAMPLE_DATA = "importhmProductCatalogsampledata";
	private static final String IMPORT_HM_CONTENT_CATALOG_SAMPLE_DATA = "importhmContentCatalogSampledata";



	private CoreDataImportService coreDataImportService;
	private SampleDataImportService sampleDataImportService;

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_CORE_DATA, "Import Core Data", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA, "Import Sample Data", true));
		params.add(createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_HM_ELECTRONICS, "import hm Electronics", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_HM_ELECTRONICS_SAMPLEDATA, "import Hm Electronics Sample data", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_HM_CONTENT_CATALOG, "import hm Content Catalog", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_HM_PRODUCT_CATALOG, "import hm Product Catalog", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_HM_PRODUCT_CATALOG_MULTI_D, "import hm Product Catalog multi d", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_HM_PRODUCT_CATALOG_SAMPLE_DATA, "import hm Product Catalog Sample Data",true));
		params.add(createBooleanSystemSetupParameter(IMPORT_HM_CONTENT_CATALOG_SAMPLE_DATA, "import hm Content Catalog Sample Data", true));

		// Add more Parameters here as you require

		return params;
	}

	/**
	 * Implement this method to create initial objects. This method will be called by system creator during
	 * initialization and system update. Be sure that this method can be called repeatedly.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		// Add Essential Data here as you require
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization. <br>
	 * Add import data for each site you have configured
	 *
	 * <pre>
	 * final List<ImportData> importData = new ArrayList<ImportData>();
	 *
	 * final ImportData sampleImportData = new ImportData();
	 * sampleImportData.setProductCatalogName(SAMPLE_PRODUCT_CATALOG_NAME);
	 * sampleImportData.setContentCatalogNames(Arrays.asList(SAMPLE_CONTENT_CATALOG_NAME));
	 * sampleImportData.setStoreNames(Arrays.asList(SAMPLE_STORE_NAME));
	 * importData.add(sampleImportData);
	 *
	 * getCoreDataImportService().execute(this, context, importData);
	 * getEventService().publishEvent(new CoreDataImportedEvent(context, importData));
	 *
	 * getSampleDataImportService().execute(this, context, importData);
	 * getEventService().publishEvent(new SampleDataImportedEvent(context, importData));
	 * </pre>
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		if (this.getBooleanSystemSetupParameter(context, IMPORT_HM_ELECTRONICS))
		{
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/searchservices.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/searchservicestrigger.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/site.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/site_en.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/site_de.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/site-responsive.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/site-responsive_en.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/site-responsive_de.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/solr.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/solr_en.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/solr_de.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/solrtrigger.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/store.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/store_en.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/store_de.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/store-responsive.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/store-responsive_en.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/stores/hmElectronics/store-responsive_de.impex");

		}
		/*
		 * Add import data for each site you have configured
		 */
		if (this.getBooleanSystemSetupParameter(context, IMPORT_HM_ELECTRONICS_SAMPLEDATA))
		{
			importImpexFile(context, "/hmstorefront/import/sampledata/store/hmelectronics/consents.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/store/hmelectronics/consents_en.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/store/hmelectronics/jobs.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/store/hmelectronics/points-of-service.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/store/hmelectronics/points-of-service-media.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/store/hmelectronics/points-of-service_en.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/store/hmelectronics/solr.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/store/hmelectronics/solr_en.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/store/hmelectronics/warehouses.impex");

		}

		if (this.getBooleanSystemSetupParameter(context, IMPORT_HM_CONTENT_CATALOG))
		{
			importImpexFile(context, "/hmstorefront/import/coredata/contentCatalogs/hmContentCatalog/catalog.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/contentCatalogs/hmContentCatalog/catalog_en.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/contentCatalogs/hmContentCatalog/cms-content.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/contentCatalogs/hmContentCatalog/cms-content_en.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/contentCatalogs/hmContentCatalog/cms-responsive-content.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/contentCatalogs/hmContentCatalog/cms-responsive-content_en.impex");


		}

		if (this.getBooleanSystemSetupParameter(context, IMPORT_HM_CONTENT_CATALOG_SAMPLE_DATA))
		{
			importImpexFile(context, "/hmstorefront/import/sampledata/contentCatalogs/hmContentCatalog/cms-content.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/contentCatalogs/hmContentCatalog/cms-content_de.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/contentCatalogs/hmContentCatalog/cms-content_en.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/contentCatalogs/hmContentCatalog/cms-responsive-content.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/contentCatalogs/hmContentCatalog/cms-responsive-content_de.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/contentCatalogs/hmContentCatalog/cms-responsive-content_en.impex");

		}

		
		if (this.getBooleanSystemSetupParameter(context, IMPORT_HM_PRODUCT_CATALOG))
		{
			importImpexFile(context, "/hmstorefront/import/coredata/productCatalogs/hmProductCatalog/catalog.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/productCatalogs/hmProductCatalog/catalog_en.impex");
			importImpexFile(context, "/hmstorefront/import/coredata/productCatalogs/hmProductCatalog/catalog_de.impex");
		}



		if (this.getBooleanSystemSetupParameter(context, IMPORT_HM_PRODUCT_CATALOG_MULTI_D))
		{
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products_en.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products_de.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products-classifications.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products-classifications_en.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products-media.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products-pos-stocklevels.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products-prices.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products-stock-levels.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/multi-d/dimension-products-tax.impex");

		}

		if (this.getBooleanSystemSetupParameter(context, IMPORT_HM_PRODUCT_CATALOG_SAMPLE_DATA))
		{
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/categories.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/categories_en.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/categories_de.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/categories-classifications.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/categories-classifications_en.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/categories-classifications_de.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/categories-media.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/classifications-units.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products_en.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products_de.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-classifications.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-classifications_en.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-futurestock.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-media.impex");
			importImpexFile(context,
					"/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-pos-stocklevels.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-prices.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-relations.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-stocklevels.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-tax.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/suppliers.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/suppliers_en.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/suppliers_de.impex");
			importImpexFile(context, "/hmstorefront/import/sampledata/productCatalogs/hmProductCatalog/products-media.impex");
		}

	}

	public CoreDataImportService getCoreDataImportService()
	{
		return coreDataImportService;
	}

	@Required
	public void setCoreDataImportService(final CoreDataImportService coreDataImportService)
	{
		this.coreDataImportService = coreDataImportService;
	}

	public SampleDataImportService getSampleDataImportService()
	{
		return sampleDataImportService;
	}

	@Required
	public void setSampleDataImportService(final SampleDataImportService sampleDataImportService)
	{
		this.sampleDataImportService = sampleDataImportService;
	}
}

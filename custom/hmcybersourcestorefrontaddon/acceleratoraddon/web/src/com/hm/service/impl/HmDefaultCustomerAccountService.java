/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.service.impl;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hm.service.HmCustomerAccountService;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * @author Sagar.patil
 *
 */

public class HmDefaultCustomerAccountService implements HmCustomerAccountService
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(HmDefaultCustomerAccountService.class);

	private ModelService modelService;

	@Override
	public void setDefaultPaymentInfo(final CustomerModel customerModel, final PaymentInfoModel paymentInfoModel)
	{
		validateParameterNotNull(customerModel, "Customer model cannot be null");
		validateParameterNotNull(paymentInfoModel, "Payment info model cannot be null");
		if (customerModel.getPaymentInfos().contains(paymentInfoModel))
		{
			customerModel.setDefaultPaymentInfo(paymentInfoModel);
			getModelService().save(customerModel);
			getModelService().refresh(customerModel);
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
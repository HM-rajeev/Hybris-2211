/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.strategies.impl;

import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.spockframework.util.Assert;
import org.springframework.beans.factory.annotation.Required;

import com.hm.strategies.HmCommercePaymentProviderStrategy;

/**
 * @author Sagar.patil
 *
 */

public class HmDefaultCommercePaymentProviderStrategy implements HmCommercePaymentProviderStrategy
{
	private BaseStoreService baseStoreService;

	public String getPaymentProvider()
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		Assert.notNull(currentBaseStore, "Current BaseStore should exist in the session.");
		final String paymentProvider = currentBaseStore.getPaymentProvider();
		Assert.notNull(paymentProvider, "Payment provider should be set on the BaseStore");
		return paymentProvider;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}
}
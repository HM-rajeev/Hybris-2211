/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.strategies;

/**
 * @author Sagar.patil
 *
 */

public interface HmCommercePaymentProviderStrategy
{
	/**
	 * Get payment provider assigned to the {@link de.hybris.platform.store.BaseStoreModel}
	 * @return A payment provider name.
	 */
	
	String getPaymentProvider();
}
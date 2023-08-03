/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.service;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

/**
 * @author Sagar.patil
 *
 */

public interface HmCommerceCheckoutService
{
	/**
	 * Authorizes the total amount of the cart if
	 * {@link de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter#authorizationAmount} is null
	 * otherwise the passed in amout is authorized
	 *
	 * @param parameter
	 *           A parameter object holding the cart, security code, payment provider and optionaly authorization amount.
	 * @return A payment transaction entry.
	 */
	PaymentTransactionEntryModel authorizePayment(CommerceCheckoutParameter parameter);

	/**
	 * Get the payment provider name
	 *
	 * @return the payment provider
	 */
	String getPaymentProvider();
}
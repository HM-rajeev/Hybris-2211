/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.strategies;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

/**
 * @author Sagar.patil
 *
 */

public interface HmCommercePaymentAuthorizationStrategy
{
	PaymentTransactionEntryModel authorizePaymentAmount(CommerceCheckoutParameter parameter);
}
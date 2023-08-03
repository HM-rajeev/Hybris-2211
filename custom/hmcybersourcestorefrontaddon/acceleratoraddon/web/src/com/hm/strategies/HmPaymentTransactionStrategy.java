/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.strategies;

import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

/**
 * @author Sagar.patil
 *
 */

public interface HmPaymentTransactionStrategy
{
	PaymentTransactionEntryModel savePaymentTransactionEntry(CustomerModel customerModel, String requestId, OrderInfoData orderInfoData);
}
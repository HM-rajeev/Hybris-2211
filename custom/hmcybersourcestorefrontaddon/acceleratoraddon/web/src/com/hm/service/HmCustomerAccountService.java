/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.service;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 * @author Sagar.patil
 *
 */

public interface HmCustomerAccountService
{
	/**
	 * Sets the default <code>paymentInfoModel</code> for the specified <code>customerModel</code>. Both arguments must
	 * be set and <code>paymentInfoModel</code> must belong to the specified <code>customerModel</code>.
	 *
	 * @param customerModel
	 *           the customer model to set default <code>paymentInfoModel</code>
	 * @param paymentInfoModel
	 *           the payment info model to be set as default
	 * @throws IllegalArgumentException
	 *            the illegal argument exception if any argument is null
	 */
	void setDefaultPaymentInfo(CustomerModel customerModel, PaymentInfoModel paymentInfoModel);
}
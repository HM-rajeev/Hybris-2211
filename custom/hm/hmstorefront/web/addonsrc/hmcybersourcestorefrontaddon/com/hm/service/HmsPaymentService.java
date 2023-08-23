/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.service;

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.Currency;

import Model.PtsV2PaymentsCapturesPost201Response;

/**
 * @author Sagar.patil
 *
 */

public interface HmsPaymentService
{
	PaymentTransactionEntryModel authorize(String var1, BigDecimal var2, Currency var3, AddressModel var4, CreditCardPaymentInfoModel creditCardPaymentInfoModel, String var5,
			String var6, String var7);
	
	String getNewPaymentTransactionEntryCode(PaymentTransactionModel var1, PaymentTransactionType var2);
	
	PaymentTransactionEntryModel capture(PaymentTransactionModel var1);
	
	PtsV2PaymentsCapturesPost201Response capture(String code, String totalAmount, String currency, String id);	
}
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.UUID;

import com.hm.constants.HmcybersourcestorefrontaddonWebConstants;
import com.hm.service.HmCommerceCheckoutService;
import com.hm.service.HmsPaymentService;
import com.hm.strategies.HmPaymentTransactionStrategy;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * @author Sagar.patil
 *
 */

public class HmDefaultPaymentTransactionStrategy implements HmPaymentTransactionStrategy
{
	private ModelService modelService;
	private CommerceCheckoutService commerceCheckoutService;
	private HmsPaymentService hmsPaymentService;
	private HmCommerceCheckoutService hmCommerceCheckoutService;
	
	@Override
	public PaymentTransactionEntryModel savePaymentTransactionEntry(final CustomerModel customerModel, final String requestId, final OrderInfoData orderInfoData)
	{
		validateParameterNotNull(orderInfoData, "orderInfoData cannot be null");

		final PaymentTransactionModel transaction = getModelService().create(PaymentTransactionModel.class);
		final PaymentTransactionType paymentTransactionType = PaymentTransactionType.CREATE_SUBSCRIPTION;
		transaction.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
		transaction.setRequestId(requestId);
		transaction.setRequestToken(orderInfoData.getOrderPageRequestToken());
		//transaction.setPaymentProvider(getCommerceCheckoutService().getPaymentProvider());
		transaction.setPaymentProvider(HmcybersourcestorefrontaddonWebConstants.PAYMENT_PROVIDER_CYBERSOURCE);
		getModelService().save(transaction);

		final PaymentTransactionEntryModel entry = getModelService().create(PaymentTransactionEntryModel.class);
		entry.setType(paymentTransactionType);
		entry.setRequestId(requestId);
		entry.setRequestToken(orderInfoData.getOrderPageRequestToken());
		entry.setTime(new Date());
		entry.setPaymentTransaction(transaction);
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		//entry.setCode(getPaymentService().getNewPaymentTransactionEntryCode(transaction, paymentTransactionType));
		entry.setCode(getHmsPaymentService().getNewPaymentTransactionEntryCode(transaction, paymentTransactionType));
		getModelService().save(entry);
		return entry;
	}

	public ModelService getModelService()
	{
		return modelService;
	}
	
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
	
	public CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}
	
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}
	
	public HmsPaymentService getHmsPaymentService()
	{
		return hmsPaymentService;
	}
	
	public void setHmsPaymentService(final HmsPaymentService hmsPaymentService)
	{
		this.hmsPaymentService = hmsPaymentService;
	}
	
	public HmCommerceCheckoutService getHmCommerceCheckoutService()
	{
		return hmCommerceCheckoutService;
	}

	public void setHmCommerceCheckoutService(HmCommerceCheckoutService hmCommerceCheckoutService)
	{
		this.hmCommerceCheckoutService = hmCommerceCheckoutService;
	}	
}
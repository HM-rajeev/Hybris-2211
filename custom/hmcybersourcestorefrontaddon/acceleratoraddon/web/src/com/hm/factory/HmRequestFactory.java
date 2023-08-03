/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.factory;

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.dto.BillingInfo;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Properties;

import org.apache.log4j.Logger;

import Model.CapturePaymentRequest;
import Model.CreatePaymentRequest;
import Model.Ptsv2paymentsClientReferenceInformation;
import Model.Ptsv2paymentsOrderInformation;
import Model.Ptsv2paymentsOrderInformationAmountDetails;
import Model.Ptsv2paymentsOrderInformationBillTo;
import Model.Ptsv2paymentsPaymentInformation;
import Model.Ptsv2paymentsPaymentInformationCard;
import Model.Ptsv2paymentsProcessingInformation;
import Model.Ptsv2paymentsidcapturesOrderInformation;
import Model.Ptsv2paymentsidcapturesOrderInformationAmountDetails;

/**
 * @author Sagar.patil
 *
 */

public class HmRequestFactory
{
	private static final Logger LOG = Logger.getLogger(HmRequestFactory.class);
	
	public CreatePaymentRequest simpleAuthorizationInternet(final String merchantTransactionCode,
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final Currency currency, final BigDecimal totalAmount, final BillingInfo shippingInfo)
	{
		String responseCode = null;
		String status = null;
		Properties merchantProp;
		final boolean userCapture = false;

		final CreatePaymentRequest requestObj = new CreatePaymentRequest();

		final Ptsv2paymentsClientReferenceInformation clientReferenceInformation = new Ptsv2paymentsClientReferenceInformation();
		clientReferenceInformation.code(merchantTransactionCode);
		requestObj.clientReferenceInformation(clientReferenceInformation);

		final Ptsv2paymentsProcessingInformation processingInformation = new Ptsv2paymentsProcessingInformation();
		processingInformation.capture(false);
		if (userCapture)
		{
			processingInformation.capture(true);
		}

		requestObj.processingInformation(processingInformation);

		final Ptsv2paymentsPaymentInformation paymentInformation = new Ptsv2paymentsPaymentInformation();
		final Ptsv2paymentsPaymentInformationCard paymentInformationCard = new Ptsv2paymentsPaymentInformationCard();
	
		paymentInformationCard.setNumber(creditCardPaymentInfoModel.getNumber());
		paymentInformationCard.setExpirationMonth(creditCardPaymentInfoModel.getValidToMonth());	
		paymentInformationCard.setExpirationYear(creditCardPaymentInfoModel.getValidToYear());
		paymentInformation.card(paymentInformationCard);

		requestObj.paymentInformation(paymentInformation);

		final Ptsv2paymentsOrderInformation orderInformation = new Ptsv2paymentsOrderInformation();
		final Ptsv2paymentsOrderInformationAmountDetails orderInformationAmountDetails = new Ptsv2paymentsOrderInformationAmountDetails();
		orderInformationAmountDetails.setTotalAmount(totalAmount.toString());

		//orderInformationAmountDetails.currency("USD");
		orderInformationAmountDetails.setCurrency(currency.getCurrencyCode());
		orderInformation.amountDetails(orderInformationAmountDetails);

		final Ptsv2paymentsOrderInformationBillTo orderInformationBillTo = new Ptsv2paymentsOrderInformationBillTo();
	
		orderInformationBillTo.setFirstName(shippingInfo.getFirstName());	
		orderInformationBillTo.setLastName(shippingInfo.getLastName());	
		orderInformationBillTo.setAddress1(shippingInfo.getStreet1());	
		orderInformationBillTo.setLocality(shippingInfo.getCity());	
		orderInformationBillTo.setAdministrativeArea(shippingInfo.getState());		
		orderInformationBillTo.setPostalCode(shippingInfo.getPostalCode());
		
		//orderInformationBillTo.country("US");
		orderInformationBillTo.setCountry(shippingInfo.getCountry());
		orderInformationBillTo.setEmail(shippingInfo.getEmail());	
		orderInformationBillTo.setPhoneNumber(shippingInfo.getPhoneNumber());
		orderInformation.billTo(orderInformationBillTo);

		requestObj.orderInformation(orderInformation);

		return requestObj;
	}

	public CapturePaymentRequest capturePayment(String merchantTransactionCode, String totalAmount, String currency)
	{
		final CapturePaymentRequest requestObj = new CapturePaymentRequest();
		Ptsv2paymentsClientReferenceInformation clientReferenceInformation = new Ptsv2paymentsClientReferenceInformation();
		
		clientReferenceInformation.code(merchantTransactionCode);
		requestObj.clientReferenceInformation(clientReferenceInformation);

		Ptsv2paymentsidcapturesOrderInformation orderInformation = new Ptsv2paymentsidcapturesOrderInformation();
		Ptsv2paymentsidcapturesOrderInformationAmountDetails orderInformationAmountDetails = new Ptsv2paymentsidcapturesOrderInformationAmountDetails();
	
		orderInformationAmountDetails.totalAmount(totalAmount.toString());
		//orderInformationAmountDetails.currency("USD");
		orderInformationAmountDetails.setCurrency(currency);	
		orderInformation.amountDetails(orderInformationAmountDetails);

		requestObj.orderInformation(orderInformation);

		return requestObj;
	}

	public CapturePaymentRequest capturePayments(final String merchantTransactionCode, final String requestId, final Currency currency, final BigDecimal totalAmount)
	{
		final CapturePaymentRequest requestObj = new CapturePaymentRequest();
		Ptsv2paymentsClientReferenceInformation clientReferenceInformation = new Ptsv2paymentsClientReferenceInformation();
	
		clientReferenceInformation.code(merchantTransactionCode);

		requestObj.clientReferenceInformation(clientReferenceInformation);

		Ptsv2paymentsidcapturesOrderInformation orderInformation = new Ptsv2paymentsidcapturesOrderInformation();
		Ptsv2paymentsidcapturesOrderInformationAmountDetails orderInformationAmountDetails = new Ptsv2paymentsidcapturesOrderInformationAmountDetails();
		
		orderInformationAmountDetails.totalAmount(totalAmount.toString());
		//orderInformationAmountDetails.currency("USD");
		orderInformationAmountDetails.setCurrency(currency.getCurrencyCode());
		orderInformation.amountDetails(orderInformationAmountDetails);

		requestObj.orderInformation(orderInformation);

		return requestObj;
	}
}
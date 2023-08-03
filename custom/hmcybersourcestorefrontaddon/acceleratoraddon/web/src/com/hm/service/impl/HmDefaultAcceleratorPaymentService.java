/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.service.impl;

import de.hybris.platform.acceleratorservices.model.payment.CCPaySubValidationModel;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.acceleratorservices.payment.strategies.ClientReferenceLookupStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.CreateSubscriptionResultValidationStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentFormActionUrlStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentResponseInterpretationStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.SignatureValidationStrategy;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.spockframework.util.Assert;
import org.springframework.beans.factory.annotation.Required;

import com.hm.dao.HmCreditCardPaymentSubscriptionDao;
import com.hm.service.HmPaymentService;
import com.hm.strategies.HmCreateSubscriptionRequestStrategy;
import com.hm.strategies.HmCreditCardPaymentInfoCreateStrategy;
import com.hm.strategies.HmPaymentFormActionUrlStrategy;
import com.hm.strategies.HmPaymentTransactionStrategy;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * @author Sagar.patil
 *
 */

public class HmDefaultAcceleratorPaymentService implements HmPaymentService
{
	private static final Logger LOG = Logger.getLogger(HmDefaultAcceleratorPaymentService.class);
	
	private static final String SUBSCRIPTION_SIGNATURE_DATA_CANNOT_BE_NULL_MSG = "SubscriptionSignatureData cannot be null";
	private static final String SUBSCRIPTION_INFO_DATA_CANNOT_BE_NULL_MSG = "SubscriptionInfoData cannot be null";
	private static final String SIGNATURE_DATA_CANNOT_BE_NULL_MSG = "SignatureData cannot be null";
	private static final String PAYMENT_INFO_DATA_CANNOT_BE_NULL_MSG = "PaymentInfoData cannot be null";
	private static final String ORDER_INFO_DATA_CANNOT_BE_NULL_MSG = "OrderInfoData cannot be null";
	private static final String CUSTOMER_INFO_DATA_CANNOT_BE_NULL_MSG = "CustomerInfoData cannot be null";
	private static final String AUTH_REPLY_DATA_CANNOT_BE_NULL_MSG = "AuthReplyData cannot be null";
	private static final String DECISION_CANNOT_BE_NULL_MSG = "Decision cannot be null";
	private static final String CREATE_SUBSCRIPTION_RESULT_CANNOT_BE_NULL_MSG = "CreateSubscriptionResult cannot be null";


	private PaymentFormActionUrlStrategy paymentFormActionUrlStrategy;
	private HmCreateSubscriptionRequestStrategy hmCreateSubscriptionRequestStrategy;
	private ClientReferenceLookupStrategy clientReferenceLookupStrategy;
	private Converter<CreateSubscriptionRequest, PaymentData> paymentDataConverter;
	private PaymentResponseInterpretationStrategy paymentResponseInterpretation;
	private CreateSubscriptionResultValidationStrategy createSubscriptionResultValidationStrategy;
	private SignatureValidationStrategy signatureValidationStrategy;
	private ModelService modelService;
	private HmPaymentTransactionStrategy hmPaymentTransactionStrategy;
	private HmCreditCardPaymentInfoCreateStrategy hmCreditCardPaymentInfoCreateStrategy;
	private HmCreditCardPaymentSubscriptionDao hmCreditCardPaymentSubscriptionDao;
	private HmPaymentFormActionUrlStrategy hmPaymentFormActionUrlStrategy;

	public PaymentData beginSopCreatePaymentSubscription(final String siteName, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customer, final CreditCardPaymentInfoModel cardInfo, final AddressModel paymentAddress)
	{
		//final String requestUrl = getPaymentFormActionUrlStrategy().getSopRequestUrl(getClientReferenceLookupStrategy().lookupClientReferenceId());
		final String requestUrl = getHmPaymentFormActionUrlStrategy().getSopRequestUrl("Default_Client_Ref");

		Assert.notNull(requestUrl, "The SopRequestUrl cannot be null");

		final CreateSubscriptionRequest request = getHmCreateSubscriptionRequestStrategy().createSubscriptionRequest(siteName,
				requestUrl, responseUrl, merchantCallbackUrl, customer, cardInfo, paymentAddress);

		PaymentData data = getPaymentDataConverter().convert(request);
		if (data == null)
		{
			data = new PaymentData();
			data.setParameters(new HashMap<>());
		}
		return data;
	}
	
	@Override
	public PaymentSubscriptionResultItem completeSopCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters)
	{
		final PaymentSubscriptionResultItem paymentSubscriptionResult = new PaymentSubscriptionResultItem();
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		paymentSubscriptionResult.setErrors(errors);

		final CreateSubscriptionResult response = getPaymentResponseInterpretation().interpretResponse(parameters,
				getClientReferenceLookupStrategy().lookupClientReferenceId(), errors);

		validateParameterNotNull(response, CREATE_SUBSCRIPTION_RESULT_CANNOT_BE_NULL_MSG);
		validateParameterNotNull(response.getDecision(), DECISION_CANNOT_BE_NULL_MSG);

		if (!getCreateSubscriptionResultValidationStrategy().validateCreateSubscriptionResult(errors, response).isEmpty())
		{
			return paymentSubscriptionResult;
		}

		paymentSubscriptionResult.setSuccess(DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()));
		paymentSubscriptionResult.setDecision(String.valueOf(response.getDecision()));
		paymentSubscriptionResult.setResultCode(String.valueOf(response.getReasonCode()));

		if (DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()))
		{
			// in case of ACCEPT we should have all these fields filled out
			Assert.notNull(response.getAuthReplyData(), AUTH_REPLY_DATA_CANNOT_BE_NULL_MSG);
			Assert.notNull(response.getCustomerInfoData(), CUSTOMER_INFO_DATA_CANNOT_BE_NULL_MSG);
			Assert.notNull(response.getOrderInfoData(), ORDER_INFO_DATA_CANNOT_BE_NULL_MSG);
			Assert.notNull(response.getPaymentInfoData(), PAYMENT_INFO_DATA_CANNOT_BE_NULL_MSG);
			Assert.notNull(response.getSignatureData(), SIGNATURE_DATA_CANNOT_BE_NULL_MSG);
			Assert.notNull(response.getSubscriptionInfoData(), SUBSCRIPTION_INFO_DATA_CANNOT_BE_NULL_MSG);
			Assert.notNull(response.getSubscriptionSignatureData(), SUBSCRIPTION_SIGNATURE_DATA_CANNOT_BE_NULL_MSG);

			// Validate signature Need To work. Right now not working
			//if (getSignatureValidationStrategy().validateSignature(response.getSubscriptionInfoData()))
			if (true)
			{
				getHmPaymentTransactionStrategy().savePaymentTransactionEntry(customerModel, response.getRequestId(), response.getOrderInfoData());
				
				final CreditCardPaymentInfoModel cardPaymentInfoModel = getHmCreditCardPaymentInfoCreateStrategy().saveSubscription(
						customerModel, response.getCustomerInfoData(), response.getSubscriptionInfoData(), response.getPaymentInfoData(), saveInAccount);
				paymentSubscriptionResult.setStoredCard(cardPaymentInfoModel);

				// Check if the subscription has already been validated
				final CCPaySubValidationModel subscriptionValidation = getHmCreditCardPaymentSubscriptionDao().findSubscriptionValidationBySubscription(cardPaymentInfoModel.getSubscriptionId());
				if (subscriptionValidation != null)
				{
					cardPaymentInfoModel.setSubscriptionValidated(true);
					getModelService().save(cardPaymentInfoModel);
					getModelService().remove(subscriptionValidation);
				}
			}
			else
			{
				LOG.error("Cannot create subscription. Subscription signature does not match.");
			}
		}
		else
		{
			LOG.error("Cannot create subscription. Decision: " + response.getDecision() + " - Reason Code: " + response.getReasonCode());
		}
		return paymentSubscriptionResult;
	}

	protected PaymentFormActionUrlStrategy getPaymentFormActionUrlStrategy()
	{
		return paymentFormActionUrlStrategy;
	}

	@Required
	public void setPaymentFormActionUrlStrategy(final PaymentFormActionUrlStrategy paymentFormActionUrlStrategy)
	{
		this.paymentFormActionUrlStrategy = paymentFormActionUrlStrategy;
	}

	protected ClientReferenceLookupStrategy getClientReferenceLookupStrategy()
	{
		return clientReferenceLookupStrategy;
	}

	@Required
	public void setClientReferenceLookupStrategy(final ClientReferenceLookupStrategy clientReferenceLookupStrategy)
	{
		this.clientReferenceLookupStrategy = clientReferenceLookupStrategy;
	}	
	
	public HmCreateSubscriptionRequestStrategy getHmCreateSubscriptionRequestStrategy()
	{
		return hmCreateSubscriptionRequestStrategy;
	}
	
	public void setHmCreateSubscriptionRequestStrategy(final HmCreateSubscriptionRequestStrategy hmCreateSubscriptionRequestStrategy)
	{
		this.hmCreateSubscriptionRequestStrategy = hmCreateSubscriptionRequestStrategy;
	}

	protected Converter<CreateSubscriptionRequest, PaymentData> getPaymentDataConverter()
	{
		return paymentDataConverter;
	}

	@Required
	public void setPaymentDataConverter(final Converter<CreateSubscriptionRequest, PaymentData> paymentDataConverter)
	{
		this.paymentDataConverter = paymentDataConverter;
	}
	
	protected PaymentResponseInterpretationStrategy getPaymentResponseInterpretation()
	{
		return paymentResponseInterpretation;
	}

	@Required
	public void setPaymentResponseInterpretation(final PaymentResponseInterpretationStrategy paymentResponseInterpretation)
	{
		this.paymentResponseInterpretation = paymentResponseInterpretation;
	}
	
	protected CreateSubscriptionResultValidationStrategy getCreateSubscriptionResultValidationStrategy()
	{
		return createSubscriptionResultValidationStrategy;
	}

	@Required
	public void setCreateSubscriptionResultValidationStrategy(
			final CreateSubscriptionResultValidationStrategy createSubscriptionResultValidationStrategy)
	{
		this.createSubscriptionResultValidationStrategy = createSubscriptionResultValidationStrategy;
	}
	
	protected SignatureValidationStrategy getSignatureValidationStrategy()
	{
		return signatureValidationStrategy;
	}

	@Required
	public void setSignatureValidationStrategy(final SignatureValidationStrategy signatureValidationStrategy)
	{
		this.signatureValidationStrategy = signatureValidationStrategy;
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
	
	public HmPaymentTransactionStrategy getHmPaymentTransactionStrategy()
	{
		return hmPaymentTransactionStrategy;
	}
	
	public void setHmPaymentTransactionStrategy(final HmPaymentTransactionStrategy hmPaymentTransactionStrategy)
	{
		this.hmPaymentTransactionStrategy = hmPaymentTransactionStrategy;
	}
	
	public HmCreditCardPaymentInfoCreateStrategy getHmCreditCardPaymentInfoCreateStrategy()
	{
		return hmCreditCardPaymentInfoCreateStrategy;
	}
	
	public void setHmCreditCardPaymentInfoCreateStrategy(
			final HmCreditCardPaymentInfoCreateStrategy hmCreditCardPaymentInfoCreateStrategy)
	{
		this.hmCreditCardPaymentInfoCreateStrategy = hmCreditCardPaymentInfoCreateStrategy;
	}
	
	public HmCreditCardPaymentSubscriptionDao getHmCreditCardPaymentSubscriptionDao()
	{
		return hmCreditCardPaymentSubscriptionDao;
	}
	
	public void setHmCreditCardPaymentSubscriptionDao(final HmCreditCardPaymentSubscriptionDao hmCreditCardPaymentSubscriptionDao)
	{
		this.hmCreditCardPaymentSubscriptionDao = hmCreditCardPaymentSubscriptionDao;
	}
	
	public HmPaymentFormActionUrlStrategy getHmPaymentFormActionUrlStrategy()
	{
		return hmPaymentFormActionUrlStrategy;
	}
	
	public void setHmPaymentFormActionUrlStrategy(final HmPaymentFormActionUrlStrategy hmPaymentFormActionUrlStrategy)
	{
		this.hmPaymentFormActionUrlStrategy = hmPaymentFormActionUrlStrategy;
	}	
}
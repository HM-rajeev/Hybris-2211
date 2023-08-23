/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.service.impl;

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.commands.AuthorizationCommand;
import de.hybris.platform.payment.commands.CaptureCommand;
import de.hybris.platform.payment.commands.CreateSubscriptionCommand;
import de.hybris.platform.payment.commands.DeleteSubscriptionCommand;
import de.hybris.platform.payment.commands.EnrollmentCheckCommand;
import de.hybris.platform.payment.commands.FollowOnRefundCommand;
import de.hybris.platform.payment.commands.GetSubscriptionDataCommand;
import de.hybris.platform.payment.commands.PartialCaptureCommand;
import de.hybris.platform.payment.commands.StandaloneRefundCommand;
import de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand;
import de.hybris.platform.payment.commands.UpdateSubscriptionCommand;
import de.hybris.platform.payment.commands.VoidCommand;
import de.hybris.platform.payment.commands.factory.CommandFactory;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.commands.factory.CommandNotSupportedException;
import de.hybris.platform.payment.commands.request.AuthorizationRequest;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.request.CreateSubscriptionRequest;
import de.hybris.platform.payment.commands.request.DeleteSubscriptionRequest;
import de.hybris.platform.payment.commands.request.EnrollmentCheckRequest;
import de.hybris.platform.payment.commands.request.FollowOnRefundRequest;
import de.hybris.platform.payment.commands.request.PartialCaptureRequest;
import de.hybris.platform.payment.commands.request.StandaloneRefundRequest;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.request.SubscriptionDataRequest;
import de.hybris.platform.payment.commands.request.UpdateSubscriptionRequest;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.commands.result.EnrollmentCheckResult;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.commands.result.SubscriptionDataResult;
import de.hybris.platform.payment.commands.result.SubscriptionResult;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.dto.BasicCardInfo;
import de.hybris.platform.payment.dto.BillingInfo;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Properties;

import com.cybersource.authsdk.core.MerchantConfig;
import com.hm.configuration.HmConfiguration;
import com.hm.factory.HmRequestFactory;
import com.hm.service.HmCardPaymentService;

import Api.CaptureApi;
import Api.PaymentsApi;
import Invokers.ApiClient;
import Model.CapturePaymentRequest;
import Model.CreatePaymentRequest;
import Model.PtsV2PaymentsCapturesPost201Response;
import Model.PtsV2PaymentsPost201Response;

/**
 * @author Sagar.patil
 *
 */

public class HmDefaultCardPaymentServiceImpl implements HmCardPaymentService
{
	private CommandFactoryRegistry commandFactoryRegistry;
	private HmRequestFactory hmRequestFactory;
	private HmConfiguration hmConfiguration;

	public PtsV2PaymentsPost201Response authorize(final String merchantTransactionCode,
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final Currency currency, final BigDecimal totalAmount, final BillingInfo shippingInfo)
	{
		String responseCode = null;
		String status = null;
		Properties merchantProp;
		final boolean userCapture = false;
		
		CreatePaymentRequest paymentRequest = getHmRequestFactory().simpleAuthorizationInternet(merchantTransactionCode, creditCardPaymentInfoModel, currency, totalAmount, shippingInfo);

		PtsV2PaymentsPost201Response result = new PtsV2PaymentsPost201Response();
		try
		{
			merchantProp = hmConfiguration.getMerchantDetails();
			final ApiClient apiClient = new ApiClient();
			final MerchantConfig merchantConfig = new MerchantConfig(merchantProp);
			apiClient.merchantConfig = merchantConfig;

			final PaymentsApi apiInstance = new PaymentsApi(apiClient);
			result = apiInstance.createPayment(paymentRequest);

			responseCode = apiClient.responseCode;
			status = apiClient.status;
			System.out.println("ResponseCode :" + responseCode);
			System.out.println("ResponseMessage :" + status);
			System.out.println("Result : " + result);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public PtsV2PaymentsCapturesPost201Response capture(final String merchantTransactionCode, final String requestId, final Currency currency, final BigDecimal totalAmount)
	{
		String responseCode = null;
		String status = null;
		Properties merchantProp;
		final boolean userCapture = false;

		final CapturePaymentRequest capturePaymentRequest = getHmRequestFactory().capturePayments(merchantTransactionCode, requestId, currency, totalAmount);

		PtsV2PaymentsCapturesPost201Response result = new PtsV2PaymentsCapturesPost201Response();
		try
		{
			merchantProp = hmConfiguration.getMerchantDetails();
			final ApiClient apiClient = new ApiClient();
			final MerchantConfig merchantConfig = new MerchantConfig(merchantProp);
			apiClient.merchantConfig = merchantConfig;

			CaptureApi apiInstance = new CaptureApi(apiClient);
			result = apiInstance.capturePayment(capturePaymentRequest, requestId);

			responseCode = apiClient.responseCode;
			status = apiClient.status;
			System.out.println("ResponseCode :" + responseCode);
			System.out.println("ResponseMessage :" + status);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public AuthorizationResult authorize(AuthorizationRequest request)
	{
		try
		{
			CommandFactory commandFactory = this.commandFactoryRegistry.getFactory(request.getCard(), false);
			AuthorizationCommand command = (AuthorizationCommand) commandFactory.createCommand(AuthorizationCommand.class);
			AuthorizationResult result = (AuthorizationResult) command.perform(request);
			result.setPaymentProvider(commandFactory.getPaymentProvider());
			return result;
		}
		catch (CommandNotSupportedException var5)
		{
			throw new AdapterException(var5.getMessage(), var5);
		}
	}

	public AuthorizationResult authorize(SubscriptionAuthorizationRequest request)
	{
		try
		{
			CommandFactory commandFactory;
			if (request.getPaymentProvider() == null)
			{
				commandFactory = this.commandFactoryRegistry.getFactory((BasicCardInfo) null, false);
			}
			else
			{
				commandFactory = this.commandFactoryRegistry.getFactory(request.getPaymentProvider());
			}

			SubscriptionAuthorizationCommand command = (SubscriptionAuthorizationCommand) commandFactory
					.createCommand(SubscriptionAuthorizationCommand.class);
			AuthorizationResult result = (AuthorizationResult) command.perform(request);
			result.setPaymentProvider(commandFactory.getPaymentProvider());
			return result;
		}
		catch (CommandNotSupportedException var5)
		{
			throw new AdapterException(var5.getMessage(), var5);
		}
	}

	public CaptureResult capture(CaptureRequest request)
	{
		try
		{
			CaptureCommand command = (CaptureCommand) this.commandFactoryRegistry.getFactory(request.getPaymentProvider())
					.createCommand(CaptureCommand.class);
			return (CaptureResult) command.perform(request);
		}
		catch (CommandNotSupportedException var3)
		{
			throw new AdapterException(var3.getMessage(), var3);
		}
	}

	public CaptureResult partialCapture(PartialCaptureRequest request)
	{
		try
		{
			PartialCaptureCommand command = (PartialCaptureCommand) this.commandFactoryRegistry
					.getFactory(request.getPaymentProvider()).createCommand(PartialCaptureCommand.class);
			return (CaptureResult) command.perform(request);
		}
		catch (CommandNotSupportedException var3)
		{
			throw new AdapterException(var3.getMessage(), var3);
		}
	}

	public EnrollmentCheckResult enrollmentCheck(EnrollmentCheckRequest request)
	{
		CommandFactory commandFactory = this.commandFactoryRegistry.getFactory(request.getCard(), true);

		try
		{
			EnrollmentCheckCommand command = (EnrollmentCheckCommand) commandFactory.createCommand(EnrollmentCheckCommand.class);
			EnrollmentCheckResult result = (EnrollmentCheckResult) command.perform(request);
			result.setPaymentProvider(commandFactory.getPaymentProvider());
			return result;
		}
		catch (CommandNotSupportedException var5)
		{
			throw new AdapterException(var5.getMessage(), var5);
		}
	}

	public RefundResult refundFollowOn(FollowOnRefundRequest request)
	{
		try
		{
			FollowOnRefundCommand command = (FollowOnRefundCommand) this.commandFactoryRegistry
					.getFactory(request.getPaymentProvider()).createCommand(FollowOnRefundCommand.class);
			return command.perform(request);
		}
		catch (CommandNotSupportedException var3)
		{
			throw new AdapterException(var3.getMessage(), var3);
		}
	}

	public RefundResult refundStandalone(StandaloneRefundRequest request)
	{
		try
		{
			CommandFactory commandFactory;
			if (request.getPaymentProvider() == null)
			{
				commandFactory = this.commandFactoryRegistry.getFactory(request.getCard(), false);
			}
			else
			{
				commandFactory = this.commandFactoryRegistry.getFactory(request.getPaymentProvider());
			}

			StandaloneRefundCommand command = (StandaloneRefundCommand) commandFactory.createCommand(StandaloneRefundCommand.class);
			RefundResult result = command.perform(request);
			result.setPaymentProvider(commandFactory.getPaymentProvider());
			return result;
		}
		catch (CommandNotSupportedException var5)
		{
			throw new AdapterException(var5.getMessage(), var5);
		}
	}

	public VoidResult voidCreditOrCapture(VoidRequest request)
	{
		try
		{
			VoidCommand command = (VoidCommand) this.commandFactoryRegistry.getFactory(request.getPaymentProvider())
					.createCommand(VoidCommand.class);
			return (VoidResult) command.perform(request);
		}
		catch (CommandNotSupportedException var3)
		{
			throw new AdapterException(var3.getMessage(), var3);
		}
	}

	public SubscriptionResult createSubscription(CreateSubscriptionRequest request)
	{
		try
		{
			CreateSubscriptionCommand command = (CreateSubscriptionCommand) this.commandFactoryRegistry
					.getFactory(request.getPaymentProvider()).createCommand(CreateSubscriptionCommand.class);
			return (SubscriptionResult) command.perform(request);
		}
		catch (CommandNotSupportedException var3)
		{
			throw new AdapterException(var3.getMessage(), var3);
		}
	}

	public SubscriptionResult updateSubscription(UpdateSubscriptionRequest request)
	{
		try
		{
			UpdateSubscriptionCommand command = (UpdateSubscriptionCommand) this.commandFactoryRegistry
					.getFactory(request.getPaymentProvider()).createCommand(UpdateSubscriptionCommand.class);
			return (SubscriptionResult) command.perform(request);
		}
		catch (CommandNotSupportedException var3)
		{
			throw new AdapterException(var3.getMessage(), var3);
		}
	}

	public SubscriptionDataResult getSubscriptionData(SubscriptionDataRequest request)
	{
		try
		{
			GetSubscriptionDataCommand command = (GetSubscriptionDataCommand) this.commandFactoryRegistry
					.getFactory(request.getPaymentProvider()).createCommand(GetSubscriptionDataCommand.class);
			return (SubscriptionDataResult) command.perform(request);
		}
		catch (CommandNotSupportedException var3)
		{
			throw new AdapterException(var3.getMessage(), var3);
		}
	}

	public SubscriptionResult deleteSubscription(DeleteSubscriptionRequest request)
	{
		try
		{
			DeleteSubscriptionCommand command = (DeleteSubscriptionCommand) this.commandFactoryRegistry
					.getFactory(request.getPaymentProvider()).createCommand(DeleteSubscriptionCommand.class);
			return (SubscriptionResult) command.perform(request);
		}
		catch (CommandNotSupportedException var3)
		{
			throw new AdapterException(var3.getMessage(), var3);
		}
	}

	public CommandFactoryRegistry getCommandFactoryRegistry()
	{
		return this.commandFactoryRegistry;
	}

	public void setCommandFactoryRegistry(CommandFactoryRegistry commandFactoryRegistry)
	{
		this.commandFactoryRegistry = commandFactoryRegistry;
	}

	public HmRequestFactory getHmRequestFactory()
	{
		return hmRequestFactory;
	}

	public void setHmRequestFactory(HmRequestFactory hmRequestFactory)
	{
		this.hmRequestFactory = hmRequestFactory;
	}

	public HmConfiguration getHmConfiguration()
	{
		return hmConfiguration;
	}

	public void setHmConfiguration(HmConfiguration hmConfiguration)
	{
		this.hmConfiguration = hmConfiguration;
	}
}
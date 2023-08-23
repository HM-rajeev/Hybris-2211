/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hm.service.impl;

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.cybersource.authsdk.core.MerchantConfig;
import com.hm.configuration.HmConfiguration;
import com.hm.constants.HmcybersourcestorefrontaddonWebConstants;
import com.hm.exceptions.HmNonAuthorizedPaymentException;
import com.hm.factory.HmRequestFactory;
import com.hm.service.HmCardPaymentService;
import com.hm.service.HmsPaymentService;

import Api.CaptureApi;
import Invokers.ApiClient;
import Model.CapturePaymentRequest;
import Model.PtsV2PaymentsCapturesPost201Response;
import Model.PtsV2PaymentsPost201Response;


/**
 * @author Sagar.patil
 *
 */

public class HmsDefaultPaymentServiceImpl implements HmsPaymentService
{
	private static final Logger LOG = Logger.getLogger(HmsDefaultPaymentServiceImpl.class);

	private CommonI18NService commonI18NService;
	private ModelService modelService;
	private HmCardPaymentService hmCardPaymentService;
	private HmConfiguration hmConfiguration;
	private HmRequestFactory hmRequestFactory;
	private UserService userService;

	//public PaymentTransactionEntryModel authorize(String merchantTransactionCode, BigDecimal amount, Currency currency,
	//		AddressModel deliveryAddress, String subscriptionID, String cv2, String paymentProvider)
	public PaymentTransactionEntryModel authorize(final String merchantTransactionCode, final BigDecimal amount,
			final Currency currency, final AddressModel deliveryAddress, final CreditCardPaymentInfoModel creditCardPaymentInfoModel,
			final String subscriptionID, final String cv2, final String paymentProvider)
	{
		final BillingInfo shippingInfo = this.createBillingInfo(deliveryAddress);
		final PaymentTransactionModel transaction = (PaymentTransactionModel) this.getModelService()
				.create(PaymentTransactionModel.class);
		transaction.setCode(merchantTransactionCode);
		transaction.setPlannedAmount(amount);

		try
		{
			return this.authorizeInternal(transaction, amount, currency, shippingInfo, (creditCardPaymentInfoModel), subscriptionID,
					cv2, paymentProvider);
		}
		catch (final Exception e)
		{
			// XXX Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getNewPaymentTransactionEntryCode(final PaymentTransactionModel transaction,
			final PaymentTransactionType paymentTransactionType)
	{
		return transaction.getEntries() == null ? transaction.getCode() + "-" + paymentTransactionType.getCode() + "-1"
				: transaction.getCode() + "-" + paymentTransactionType.getCode() + "-" + (transaction.getEntries().size() + 1);
	}

	//protected PaymentTransactionEntryModel authorizeInternal(PaymentTransactionModel transaction, BigDecimal amount,
	//		Currency currency, BillingInfo shippingInfo, CardInfo card, String subscriptionID, String cv2, String paymentProvider)
	public PaymentTransactionEntryModel authorizeInternal(final PaymentTransactionModel transaction, final BigDecimal amount,
			final Currency currency, final BillingInfo shippingInfo, final CreditCardPaymentInfoModel creditCardPaymentInfoModel,
			final String subscriptionID, final String cv2, final String paymentProvider) throws Exception
	{
		final PaymentTransactionType paymentTransactionType = PaymentTransactionType.AUTHORIZATION;
		final String newEntryCode = this.getNewPaymentTransactionEntryCode(transaction, paymentTransactionType);
		final AuthorizationResult result1;
		PtsV2PaymentsPost201Response result = new PtsV2PaymentsPost201Response();

		if (subscriptionID == null)
		{
			//result = this.getCardPaymentService().authorize(new AuthorizationRequest(newEntryCode, card, currency, amount, shippingInfo));
			result = getHmCardPaymentService().authorize(newEntryCode, creditCardPaymentInfoModel, currency, amount, shippingInfo);
		}
		else
		{
			//result = this.getCardPaymentService().authorize(new SubscriptionAuthorizationRequest(newEntryCode, subscriptionID, currency, amount, shippingInfo, cv2, paymentProvider));
			result = getHmCardPaymentService().authorize(newEntryCode, creditCardPaymentInfoModel, currency, amount, shippingInfo);
		}

		if (result.getStatus() != null && result.getStatus() != "")
		{
			if (result.getStatus().equalsIgnoreCase(HmcybersourcestorefrontaddonWebConstants.CYBERSOURCE_PAYMENT_STATUS_AUTHORIZED))
			{
				transaction.setRequestId(result.getId());

				//transaction.setRequestToken(result.getRequestToken());

				//transaction.setPaymentProvider(result.getPaymentProvider());
				transaction.setPaymentProvider(HmcybersourcestorefrontaddonWebConstants.PAYMENT_PROVIDER_CYBERSOURCE);
				this.getModelService().save(transaction);

				final PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) this.getModelService()
						.create(PaymentTransactionEntryModel.class);

				entry.setAmount(new BigDecimal(result.getOrderInformation().getAmountDetails().getAuthorizedAmount()));

				if (result.getOrderInformation().getAmountDetails().getCurrency() != null)
				{
					entry.setCurrency(
							this.getCommonI18NService().getCurrency(result.getOrderInformation().getAmountDetails().getCurrency()));
				}

				entry.setType(paymentTransactionType);

				if (result.getSubmitTimeUtc() == null)
				{
					entry.setTime(new Date());
				}
				else
				{
					final DateFormat df = new SimpleDateFormat(HmcybersourcestorefrontaddonWebConstants.PAYMENT_AUTH_DATE_FORMAT);
					Date de = new Date();
					try
					{
						de = df.parse(result.getSubmitTimeUtc());
						entry.setTime(de);
					}
					catch (final ParseException e)
					{
						// XXX Auto-generated catch block
						e.printStackTrace();
					}
					finally
					{
					}
				}

				entry.setPaymentTransaction(transaction);

				entry.setRequestId(result.getId());

				//entry.setRequestToken(result.getRequestToken());

				//entry.setTransactionStatus(result.getStatus()); //e.g ACCEPTED, //Cyber AUTHORIZED
				entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());// Create New method for status

				//entry.setTransactionStatusDetails(result.getTransactionStatusDetails().toString());
				//entry.setTransactionStatusDetails(result.getProcessorInformation().getTransactionId());//e.g SUCCESFULL
				entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());//e.g SUCCESFULL // Create New method for status

				entry.setCode(newEntryCode);

				if (subscriptionID != null)
				{
					entry.setSubscriptionID(subscriptionID);
				}

				this.getModelService().save(entry);
				this.getModelService().refresh(transaction);
				return entry;
			}
		}
		throw new HmNonAuthorizedPaymentException(result);
	}

	public PaymentTransactionEntryModel capture(final PaymentTransactionModel transaction)
	{
		PaymentTransactionEntryModel auth = null;
		final Iterator var4 = transaction.getEntries().iterator();

		while (var4.hasNext())
		{
			final PaymentTransactionEntryModel pte = (PaymentTransactionEntryModel) var4.next();
			if (pte.getType().equals(PaymentTransactionType.AUTHORIZATION))
			//if (pte.getType().equals("AUTHORIZED"))
			{
				auth = pte;
				break;
			}
		}

		if (auth == null)
		{
			throw new AdapterException("Could not capture without authorization");
		}
		else
		{
			final PaymentTransactionType transactionType = PaymentTransactionType.CAPTURE;
			final String newEntryCode = this.getNewPaymentTransactionEntryCode(transaction, transactionType);

			PtsV2PaymentsCapturesPost201Response result = new PtsV2PaymentsCapturesPost201Response();

			//CaptureResult result = this.getCardPaymentService().capture(new CaptureRequest(
			//		              newEntryCode, transaction.getRequestId(), transaction.getRequestToken(),
			//			           Currency.getInstance(auth.getCurrency().getIsocode()), auth.getAmount(),
			//				        transaction.getPaymentProvider(), auth.getSubscriptionID()));

			result = getHmCardPaymentService().capture(newEntryCode, transaction.getRequestId(),
					Currency.getInstance(auth.getCurrency().getIsocode()), auth.getAmount());

			final PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) this.getModelService()
					.create(PaymentTransactionEntryModel.class);
			entry.setAmount(new BigDecimal(result.getOrderInformation().getAmountDetails().getTotalAmount()));

			if (result.getOrderInformation().getAmountDetails().getCurrency() != null)
			{
				entry.setCurrency(
						this.getCommonI18NService().getCurrency(result.getOrderInformation().getAmountDetails().getCurrency()));
			}

			entry.setType(transactionType);

			entry.setRequestId(result.getId());

			//entry.setRequestToken(result.getRequestToken());			

			if (result.getSubmitTimeUtc() == null)
			{
				entry.setTime(new Date());
			}
			else
			{
				final DateFormat df = new SimpleDateFormat(HmcybersourcestorefrontaddonWebConstants.PAYMENT_AUTH_DATE_FORMAT);
				Date de = new Date();
				try
				{
					de = df.parse(result.getSubmitTimeUtc());
					entry.setTime(de);
				}
				catch (final ParseException e)
				{
					// XXX Auto-generated catch block
					e.printStackTrace();
				}
				finally
				{
				}
			}

			entry.setPaymentTransaction(transaction);

			//entry.setTransactionStatus(result.getStatus());//e.g ACCEPTED CyberSource PENDING
			entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());//e.g ACCEPTED CyberSource PENDING

			//entry.setTransactionStatusDetails(result.getProcessorInformation().getTransactionId());// e.g SUCCESFULL
			entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());

			entry.setCode(newEntryCode);
			this.getModelService().save(entry);
			return entry;
		}
	}

	protected BillingInfo createBillingInfo(final AddressModel address)
	{
		if (address == null)
		{
			return null;
		}
		else
		{
			final BillingInfo billingInfo = new BillingInfo();
			billingInfo.setCity(address.getTown());
			if (address.getCountry() != null)
			{
				billingInfo.setCountry(address.getCountry().getIsocode());
			}

			if (address.getEmail() != null && address.getEmail() != "")
			{
				billingInfo.setEmail(address.getEmail());
			}
			else
			{
				billingInfo.setEmail(getUserService().getCurrentUser().getUid());
			}
			billingInfo.setFirstName(address.getFirstname());
			billingInfo.setLastName(address.getLastname());
			billingInfo.setPhoneNumber(address.getPhone1());
			billingInfo.setPostalCode(address.getPostalcode());
			if (address.getRegion() != null)
			{
				billingInfo.setState(address.getRegion().getName());
			}

			billingInfo.setStreet1(address.getStreetname());
			billingInfo.setStreet2(address.getStreetnumber());
			return billingInfo;
		}
	}

	public PtsV2PaymentsCapturesPost201Response capture(final String code, final String totalAmount, final String currency,
			final String id)
	{
		String responseCode = null;
		String status = null;
		Properties merchantProp;
		final boolean userCapture = false;

		final CapturePaymentRequest capturePaymentRequest = getHmRequestFactory().capturePayment(code, totalAmount, currency);

		PtsV2PaymentsCapturesPost201Response result = new PtsV2PaymentsCapturesPost201Response();
		try
		{
			merchantProp = hmConfiguration.getMerchantDetails();
			final ApiClient apiClient = new ApiClient();
			final MerchantConfig merchantConfig = new MerchantConfig(merchantProp);
			apiClient.merchantConfig = merchantConfig;

			final CaptureApi apiInstance = new CaptureApi(apiClient);
			result = apiInstance.capturePayment(capturePaymentRequest, id);

			responseCode = apiClient.responseCode;
			status = apiClient.status;
			System.out.println("ResponseCode :" + responseCode);
			System.out.println("ResponseMessage :" + status);
			System.out.println(result);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return this.commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected ModelService getModelService()
	{
		return this.modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public HmCardPaymentService getHmCardPaymentService()
	{
		return hmCardPaymentService;
	}

	public void setHmCardPaymentService(final HmCardPaymentService hmCardPaymentService)
	{
		this.hmCardPaymentService = hmCardPaymentService;
	}

	public HmRequestFactory getHmRequestFactory()
	{
		return hmRequestFactory;
	}

	public void setHmRequestFactory(final HmRequestFactory hmRequestFactory)
	{
		this.hmRequestFactory = hmRequestFactory;
	}

	public HmConfiguration getHmConfiguration()
	{
		return hmConfiguration;
	}

	public void setHmConfiguration(final HmConfiguration hmConfiguration)
	{
		this.hmConfiguration = hmConfiguration;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}

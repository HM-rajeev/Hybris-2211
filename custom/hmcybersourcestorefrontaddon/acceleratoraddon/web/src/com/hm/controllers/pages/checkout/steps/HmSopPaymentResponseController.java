/**
 * 
 */
package com.hm.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.SopPaymentDetailsForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.enums.CountryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hm.controllers.HmcybersourcestorefrontaddonControllerConstants;
import com.hm.facades.HmPaymentFacade;

/**
 * @author Sagar.patil
 *
 */

@Controller
@RequestMapping(value = "/checkout/multi/hmsop")
public class HmSopPaymentResponseController extends AbstractCheckoutStepController
{
	private static final Logger LOGGER = Logger.getLogger(HmSopPaymentResponseController.class);

	private static final String PAYMENT_METHOD = "payment-method";
	private static final String CART_DATA_ATTR = "cartData";
	protected static final Map<String, String> CYBERSOURCE_SOP_CARD_TYPES = new HashMap<>();
			
	@Resource(name = "hmPaymentFacade")
	private HmPaymentFacade hmPaymentFacade;
	
	@RequestMapping(value = "/hmresponse", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doHandleSopResponse(final HttpServletRequest request, @Valid final SopPaymentDetailsForm sopPaymentDetailsForm, final BindingResult bindingResult, final Model model,
					final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		final Map<String, String> resultMap = getRequestParameterMap(request);
		
		final boolean savePaymentInfo = sopPaymentDetailsForm.isSavePaymentInfo() || getCheckoutCustomerStrategy().isAnonymousCheckout();	
				
		final PaymentSubscriptionResultData paymentSubscriptionResultData = hmPaymentFacade.completeSopCreateSubscription(resultMap, savePaymentInfo, false);

		if (paymentSubscriptionResultData.isSuccess())
		{
			createNewPaymentSubscription(paymentSubscriptionResultData);
		}
		else if (paymentSubscriptionResultData.getDecision() != null && "error".equalsIgnoreCase(paymentSubscriptionResultData.getDecision())
				|| paymentSubscriptionResultData.getErrors() != null && !paymentSubscriptionResultData.getErrors().isEmpty())
		{
			return processErrors(sopPaymentDetailsForm, bindingResult, model, redirectAttributes, paymentSubscriptionResultData);
		}
		else
		{
			// SOP ERROR!
			LOGGER.error("Failed to create subscription.  Please check the log files for more information");
			return REDIRECT_URL_ERROR + "/?decision=" + paymentSubscriptionResultData.getDecision() + "&reasonCode="
					+ paymentSubscriptionResultData.getResultCode();
		}
		return getCheckoutStep().nextStep();
	}

	protected String processErrors(@Valid final SopPaymentDetailsForm sopPaymentDetailsForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes, final PaymentSubscriptionResultData paymentSubscriptionResultData)
			throws CMSItemNotFoundException
	{
		// Have SOP errors that we can display

		setupAddPaymentPage(model);

		// Build up the SOP form data and render page containing form
		try
		{
			setupSilentOrderPostPage(sopPaymentDetailsForm, model);
		}
		catch (final Exception e)
		{
			LOGGER.error("Failed to build beginCreateSubscription request", e);
			GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
			return enterStep(model, redirectAttributes);
		}

		processPaymentSubscriptionErrors(bindingResult, model, paymentSubscriptionResultData);
	
		return HmcybersourcestorefrontaddonControllerConstants.Views.Pages.MultiStepCheckout.HmSilentOrderPostPage;
	}

	protected void createNewPaymentSubscription(final PaymentSubscriptionResultData paymentSubscriptionResultData)
	{
		if (paymentSubscriptionResultData.getStoredCard() != null
				&& StringUtils.isNotBlank(paymentSubscriptionResultData.getStoredCard().getSubscriptionId()))
		{
			final CCPaymentInfoData newPaymentSubscription = paymentSubscriptionResultData.getStoredCard();

			if (getUserFacade().getCCPaymentInfos(true).size() <= 1)
			{
				getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			}
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
	}

	protected void processPaymentSubscriptionErrors(final BindingResult bindingResult, final Model model,
			final PaymentSubscriptionResultData paymentSubscriptionResultData)
	{
		if (paymentSubscriptionResultData.getErrors() != null && !paymentSubscriptionResultData.getErrors().isEmpty())
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.paymentethod.formentry.invalid");
			// Add in specific errors for invalid fields
			for (final PaymentErrorField paymentErrorField : paymentSubscriptionResultData.getErrors().values())
			{
				if (paymentErrorField.isMissing())
				{
					bindingResult.rejectValue(paymentErrorField.getName(),
							"checkout.error.paymentethod.formentry.sop.missing." + paymentErrorField.getName(),
							"Please enter a value for this field");
				}
				if (paymentErrorField.isInvalid())
				{
					bindingResult.rejectValue(paymentErrorField.getName(),
							"checkout.error.paymentethod.formentry.sop.invalid." + paymentErrorField.getName(),
							"This value is invalid for this field");
				}
			}
		}
		else if (paymentSubscriptionResultData.getDecision() != null
				&& "error".equalsIgnoreCase(paymentSubscriptionResultData.getDecision()))
		{
			LOGGER.error("Failed to create subscription. Error occurred while contacting external payment services.");
			GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
		}
	}

	@RequestMapping(value = "/billingaddressform", method = RequestMethod.GET)
	public String getCountryAddressForm(@RequestParam("countryIsoCode")
	final String countryIsoCode, @RequestParam("useDeliveryAddress")
	final boolean useDeliveryAddress, final Model model)
	{
		model.addAttribute("supportedCountries", getCheckoutFacade().getCountries(CountryType.BILLING));
		model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(countryIsoCode));
		model.addAttribute("country", countryIsoCode);

		final SopPaymentDetailsForm sopPaymentDetailsForm = new SopPaymentDetailsForm();
		model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
		if (useDeliveryAddress)
		{
			final AddressData deliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();

			if (deliveryAddress.getRegion() != null && !StringUtils.isEmpty(deliveryAddress.getRegion().getIsocode()))
			{
				sopPaymentDetailsForm.setBillTo_state(deliveryAddress.getRegion().getIsocodeShort());
			}

			sopPaymentDetailsForm.setBillTo_titleCode(deliveryAddress.getTitleCode());
			sopPaymentDetailsForm.setBillTo_firstName(deliveryAddress.getFirstName());
			sopPaymentDetailsForm.setBillTo_lastName(deliveryAddress.getLastName());
			sopPaymentDetailsForm.setBillTo_street1(deliveryAddress.getLine1());
			sopPaymentDetailsForm.setBillTo_street2(deliveryAddress.getLine2());
			sopPaymentDetailsForm.setBillTo_city(deliveryAddress.getTown());
			sopPaymentDetailsForm.setBillTo_postalCode(deliveryAddress.getPostalCode());
			sopPaymentDetailsForm.setBillTo_country(deliveryAddress.getCountry().getIsocode());
			sopPaymentDetailsForm.setBillTo_phoneNumber(deliveryAddress.getPhone());
		}		
		  return HmcybersourcestorefrontaddonControllerConstants.Views.Pages.MultiStepCheckout.HmBillingAddressForm;
	}
	
	protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
		prepareDataForPage(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}

	protected void setupSilentOrderPostPage(final SopPaymentDetailsForm sopPaymentDetailsForm, final Model model)
	{
		try
		{
			//final PaymentData silentOrderPageData = getPaymentFacade().beginSopCreateSubscription("/checkout/multi/sop/response",
			//		"/integration/merchant_callback");
			
			final PaymentData silentOrderPageData = hmPaymentFacade.beginSopCreateSubscription("/checkout/multi/hmsop/hmresponse",
					"/integration/merchant_callback");
			
			model.addAttribute("silentOrderPageData", silentOrderPageData);
			sopPaymentDetailsForm.setParameters(silentOrderPageData.getParameters());
			model.addAttribute("paymentFormUrl", silentOrderPageData.getPostUrl());		
		}
		catch (final IllegalArgumentException e)
		{
			model.addAttribute("paymentFormUrl", "");
			model.addAttribute("silentOrderPageData", null);
			LOGGER.warn("Failed to set up silent order post page", e);
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("silentOrderPostForm", new PaymentDetailsForm());
		model.addAttribute(CART_DATA_ATTR, cartData);
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
		model.addAttribute("paymentInfos", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute("sopCardTypes", getSopCardTypes());
		if (StringUtils.isNotBlank(sopPaymentDetailsForm.getBillTo_country()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(sopPaymentDetailsForm.getBillTo_country()));
			model.addAttribute("country", sopPaymentDetailsForm.getBillTo_country());
		}
	}
	
	protected Collection<CardTypeData> getSopCardTypes()
	{
		final Collection<CardTypeData> sopCardTypes = new ArrayList<CardTypeData>();

		final List<CardTypeData> supportedCardTypes = getCheckoutFacade().getSupportedCardTypes();
		for (final CardTypeData supportedCardType : supportedCardTypes)
		{
			// Add credit cards for all supported cards that have mappings for cybersource SOP
			if (CYBERSOURCE_SOP_CARD_TYPES.containsKey(supportedCardType.getCode()))
			{
				sopCardTypes.add(
						createCardTypeData(CYBERSOURCE_SOP_CARD_TYPES.get(supportedCardType.getCode()), supportedCardType.getName()));
			}
		}
		return sopCardTypes;
	}
	
	protected CardTypeData createCardTypeData(final String code, final String name)
	{
		final CardTypeData cardTypeData = new CardTypeData();
		cardTypeData.setCode(code);
		cardTypeData.setName(name);
		return cardTypeData;
	}
	
	@RequestMapping(value = "/back", method = RequestMethod.GET)
   @RequireHardLogIn
   @Override
   public String back(final RedirectAttributes redirectAttributes)
	{
       return getCheckoutStep().previousStep();
   }

   @RequestMapping(value = "/next", method = RequestMethod.GET)
   @RequireHardLogIn
   @Override
   public String next(final RedirectAttributes redirectAttributes) 
   {
       return getCheckoutStep().nextStep();
   }

   protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(PAYMENT_METHOD);
	}
		
	@Override
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateQuoteCheckoutStep
	@PreValidateCheckoutStep(checkoutStep = PAYMENT_METHOD)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getCheckoutFacade().setDeliveryModeIfAvailable();
		setupAddPaymentPage(model);

		// Use the checkout PCI strategy for getting the URL for creating new subscriptions.
		final CheckoutPciOptionEnum subscriptionPciOption = getCheckoutFlowFacade().getSubscriptionPciOption();
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		if (CheckoutPciOptionEnum.HOP.equals(subscriptionPciOption))
		{
			// Redirect the customer to the HOP page or show error message if it fails (e.g. no HOP configurations).
			try
			{
				final PaymentData hostedOrderPageData = getPaymentFacade().beginHopCreateSubscription("/checkout/multi/hop/response",
						"/integration/merchant_callback");
				model.addAttribute("hostedOrderPageData", hostedOrderPageData);

				final boolean hopDebugMode = getSiteConfigService().getBoolean(PaymentConstants.PaymentProperties.HOP_DEBUG_MODE,
						false);
				model.addAttribute("hopDebugMode", Boolean.valueOf(hopDebugMode));

				//return ControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
				return "";
			}
			catch (final Exception e)
			{
				LOGGER.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
			}
		}
		else if (CheckoutPciOptionEnum.SOP.equals(subscriptionPciOption))
		{
			// Build up the SOP form data and render page containing form
			final SopPaymentDetailsForm sopPaymentDetailsForm = new SopPaymentDetailsForm();
			try
			{
				setupSilentOrderPostPage(sopPaymentDetailsForm, model);				
				return HmcybersourcestorefrontaddonControllerConstants.Views.Pages.MultiStepCheckout.HmSilentOrderPostPage;
			}
			catch (final Exception e)
			{
				LOGGER.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
				model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
			}
		}

		// If not using HOP or SOP we need to build up the payment details form
		final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm();
		final AddressForm addressForm = new AddressForm();
		paymentDetailsForm.setBillingAddress(addressForm);
		model.addAttribute(paymentDetailsForm);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute(CART_DATA_ATTR, cartData);
	
		return HmcybersourcestorefrontaddonControllerConstants.Views.Pages.MultiStepCheckout.HmAddPaymentMethodPage;
	}   
}
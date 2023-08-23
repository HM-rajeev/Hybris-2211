package com.hm.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hm.service.HmsPaymentService;

/**
 * @author Sagar.patil
 *
 */

public class HmTakePaymentAction extends TakePaymentAction
{
	private static final Logger LOG = Logger.getLogger(HmTakePaymentAction.class);	
	
	private HmsPaymentService hmsPaymentService;

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();

		for (final PaymentTransactionModel txn : order.getPaymentTransactions())
		{
			if (txn.getInfo() instanceof CreditCardPaymentInfoModel)
			{				
				final PaymentTransactionEntryModel txnEntry = getHmsPaymentService().capture(txn);

				if (TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()))
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("The payment transaction has been captured. Order: " + order.getCode() + ". Txn: " + txn.getCode());
					}
					setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
				}
				else
				{
					LOG.error("The payment transaction capture has failed. Order: " + order.getCode() + ". Txn: " + txn.getCode());
					setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
					return Transition.NOK;
				}
			}
		}
		return Transition.OK;
	}	
	
	public HmsPaymentService getHmsPaymentService()
	{
		return hmsPaymentService;
	}
	
	public void setHmsPaymentService(final HmsPaymentService hmsPaymentService)
	{
		this.hmsPaymentService = hmsPaymentService;
	}	
}
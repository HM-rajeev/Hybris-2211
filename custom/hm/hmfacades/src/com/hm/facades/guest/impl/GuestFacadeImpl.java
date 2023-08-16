/**
 *
 */
package com.hm.facades.guest.impl;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import com.hm.core.services.GuestService;
import com.hm.facades.guest.GuestFacade;
import com.hm.facades.guest.data.GuestData;
import com.hm.model.GuestDetailsModel;


/**
 * @author Sathya.m
 *
 */
public class GuestFacadeImpl implements GuestFacade
{
	private GuestService guestService;
	private Converter<GuestDetailsModel, GuestData> guestConverter;

	/**
	 * @return the guestService
	 */
	public GuestService getGuestService()
	{
		return guestService;
	}

	/**
	 * @param guestService
	 *                        the guestService to set
	 */
	public void setGuestService(final GuestService guestService)
	{
		this.guestService = guestService;
	}

	/**
	 * @return the guestConverter
	 */
	public Converter<GuestDetailsModel, GuestData> getGuestConverter()
	{
		return guestConverter;
	}

	/**
	 * @param guestConverter
	 *                          the guestConverter to set
	 */
	public void setGuestConverter(final Converter<GuestDetailsModel, GuestData> guestConverter)
	{
		this.guestConverter = guestConverter;
	}

	@Override
	public List<GuestData> getGuestDetails(final String guestId)
	{
		final List<GuestDetailsModel> guestDetailsModels = guestService.getGuestIdDetails(guestId);
		return Converters.convertAll(guestDetailsModels, getGuestConverter());
	}



}

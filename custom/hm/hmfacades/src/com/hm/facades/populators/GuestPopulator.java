/**
 *
 */
package com.hm.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.hm.facades.guest.data.GuestData;
import com.hm.model.GuestDetailsModel;


/**
 * @author Sathya.m
 *
 */
public class GuestPopulator implements Populator<GuestDetailsModel, GuestData>
{

	@Override
	public void populate(final GuestDetailsModel source, final GuestData target) throws ConversionException
	{
		target.setGuestId(source.getGuestId());
		target.setGuestPlace(source.getGuestPlace());
		target.setGuestName(source.getGuestName());
		target.setRemarks(source.getRemarks());
	}
}

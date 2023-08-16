/**
 *
 */
package com.hm.core.services;

import java.util.List;

import com.hm.model.GuestDetailsModel;


/**
 * @author Sathya.m
 *
 */
public interface GuestService
{
	public List<GuestDetailsModel> getGuestIdDetails(final String guestId);
}

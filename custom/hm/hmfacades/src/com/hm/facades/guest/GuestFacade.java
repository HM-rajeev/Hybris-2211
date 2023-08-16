/**
 *
 */
package com.hm.facades.guest;


import java.util.List;

import com.hm.facades.guest.data.GuestData;


/**
 * @author Sathya.m
 *
 */
public interface GuestFacade
{
	public List<GuestData> getGuestDetails(String guestId);
}

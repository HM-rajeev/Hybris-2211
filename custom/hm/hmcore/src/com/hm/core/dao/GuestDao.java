/**
 *
 */
package com.hm.core.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;

import com.hm.model.GuestDetailsModel;


/**
 * @author Sathya.m
 *
 */
public interface GuestDao extends Dao
{
	List<GuestDetailsModel> getGuestDetailsByCode(String guestId);
}

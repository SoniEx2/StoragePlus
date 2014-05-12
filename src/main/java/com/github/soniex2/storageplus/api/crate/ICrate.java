package com.github.soniex2.storageplus.api.crate;

public interface ICrate {

	/**
	 * Returns the internal {@link CratePile}.
	 * 
	 * @return the internal CratePile
	 */
	CratePile getCratePile();
	
	/**
	 * Sets the internal CratePile.
	 * 
	 * @param pile The new crate pile.
	 * @return The old crate pile.
	 */
	CratePile setCratePile(CratePile pile);
}

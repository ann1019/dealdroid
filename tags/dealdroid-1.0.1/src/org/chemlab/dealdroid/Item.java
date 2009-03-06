package org.chemlab.dealdroid;

import android.net.Uri;

/**
 * Holds information pulled from a source about the current deal.
 * 
 * @author shade
 * @version $Id$
 */
public class Item implements Cloneable {

	private String title;

	private String salePrice;
	
	private String retailPrice;
	
	private String savings;

	private String description;

	private Uri link;

	private Uri imageLink;
	
	/**
	 * 
	 */
	public Item() {
		super();
	}

	/**
	 * @param title
	 * @param price
	 * @param description
	 * @param link
	 * @param imageLink
	 */
	public Item(String title, String salePrice, String retailPrice, String savings, String description, Uri link, Uri imageLink) {
		super();
		this.title = title;
		this.salePrice = salePrice;
		this.retailPrice = retailPrice;
		this.savings = savings;
		this.description = description;
		this.link = link;
		this.imageLink = imageLink;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the salePrice
	 */
	public String getSalePrice() {
		return salePrice;
	}

	/**
	 * @param salePrice the salePrice to set
	 */
	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	/**
	 * @return the retailPrice
	 */
	public String getRetailPrice() {
		return retailPrice;
	}

	/**
	 * @param retailPrice the retailPrice to set
	 */
	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}

	/**
	 * @return the savings
	 */
	public String getSavings() {
		return savings;
	}

	/**
	 * @param savings the savings to set
	 */
	public void setSavings(String savings) {
		this.savings = savings;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the link
	 */
	public Uri getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(Uri link) {
		this.link = link;
	}

	/**
	 * @return the imageLink
	 */
	public Uri getImageLink() {
		return imageLink;
	}

	/**
	 * @param imageLink the imageLink to set
	 */
	public void setImageLink(Uri imageLink) {
		this.imageLink = imageLink;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((imageLink == null) ? 0 : imageLink.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((retailPrice == null) ? 0 : retailPrice.hashCode());
		result = prime * result + ((salePrice == null) ? 0 : salePrice.hashCode());
		result = prime * result + ((savings == null) ? 0 : savings.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Item other = (Item) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (imageLink == null) {
			if (other.imageLink != null) {
				return false;
			}
		} else if (!imageLink.equals(other.imageLink)) {
			return false;
		}
		if (link == null) {
			if (other.link != null) {
				return false;
			}
		} else if (!link.equals(other.link)) {
			return false;
		}
		if (retailPrice == null) {
			if (other.retailPrice != null) {
				return false;
			}
		} else if (!retailPrice.equals(other.retailPrice)) {
			return false;
		}
		if (salePrice == null) {
			if (other.salePrice != null) {
				return false;
			}
		} else if (!salePrice.equals(other.salePrice)) {
			return false;
		}
		if (savings == null) {
			if (other.savings != null) {
				return false;
			}
		} else if (!savings.equals(other.savings)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {

		final Item clone;

		try {
			clone = (Item) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
		
		return clone;
	}
}

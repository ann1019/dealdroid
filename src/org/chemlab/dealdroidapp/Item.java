package org.chemlab.dealdroidapp;

import java.util.Date;

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

	private String shortDescription;

	private Uri link;

	private Uri imageLink;

	private Date expiration;

	private Date timestamp = new Date();

	/**
	 * 
	 */
	public Item() {
		super();
	}


	/**
	 * @param title
	 * @param salePrice
	 * @param retailPrice
	 * @param savings
	 * @param description
	 * @param shortDescription
	 * @param link
	 * @param imageLink
	 * @param expiration
	 */
	public Item(String title, String salePrice, String retailPrice, String savings, String description,
			String shortDescription, Uri link, Uri imageLink, Date expiration) {
		super();
		this.title = title;
		this.salePrice = salePrice;
		this.retailPrice = retailPrice;
		this.savings = savings;
		this.description = description;
		this.shortDescription = shortDescription;
		this.link = link;
		this.imageLink = imageLink;
		if (expiration != null) {
			this.expiration = (Date)expiration.clone();
		}
	}


	/**
	 * @param title
	 * @param salePrice
	 * @param retailPrice
	 * @param savings
	 * @param description
	 * @param shortDescription
	 * @param link
	 * @param imageLink
	 * @param expiration
	 * @param timestamp
	 */
	public Item(String title, String salePrice, String retailPrice, String savings, String description,
			String shortDescription, Uri link, Uri imageLink, Date expiration, Date timestamp) {
		this(title, salePrice, retailPrice, savings, description, shortDescription, link, imageLink, expiration);
		if (this.timestamp != null) {
			this.timestamp = (Date)timestamp.clone();
		}
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
	 * @param salePrice
	 *            the salePrice to set
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
	 * @param retailPrice
	 *            the retailPrice to set
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
	 * @param savings
	 *            the savings to set
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
	 * @param imageLink
	 *            the imageLink to set
	 */
	public void setImageLink(Uri imageLink) {
		this.imageLink = imageLink;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription
	 *            the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the expiration
	 */
	public Date getExpiration() {
		return expiration == null ? null : (Date)expiration.clone();
	}

	/**
	 * @param expiration
	 *            the expiration to set
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration == null ? null : (Date)expiration.clone();
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp == null ? null : (Date)timestamp.clone();
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp == null ? null : (Date)timestamp.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
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
		result = prime * result + ((shortDescription == null) ? 0 : shortDescription.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		if (shortDescription == null) {
			if (other.shortDescription != null) {
				return false;
			}
		} else if (!shortDescription.equals(other.shortDescription)) {
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

	/*
	 * (non-Javadoc)
	 * 
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

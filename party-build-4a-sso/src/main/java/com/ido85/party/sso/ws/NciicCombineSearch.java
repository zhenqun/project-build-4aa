//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.15 at 09:50:58 PM CST 
//


package com.ido85.party.sso.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inLicense" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="inConditions" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "inLicense",
    "inConditions"
})
@XmlRootElement(name = "nciicCombineSearch")
public class NciicCombineSearch {

    @XmlElement(required = true, nillable = true)
    protected String inLicense;
    @XmlElement(required = true, nillable = true)
    protected String inConditions;

    /**
     * Gets the value of the inLicense property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInLicense() {
        return inLicense;
    }

    /**
     * Sets the value of the inLicense property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInLicense(String value) {
        this.inLicense = value;
    }

    /**
     * Gets the value of the inConditions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInConditions() {
        return inConditions;
    }

    /**
     * Sets the value of the inConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInConditions(String value) {
        this.inConditions = value;
    }

}

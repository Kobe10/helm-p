//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.02.04 um 12:22:03 PM CET 
//

package com.bjshfb.vf.client.ipc.org.onvif.ver10.device.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.CertificateInformation;

/**
 * <p>
 * Java-Klasse f�r anonymous complex type.
 * 
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="CertificateInformation" type="{http://www.onvif.org/ver10/schema}CertificateInformation"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "certificateInformation" })
@XmlRootElement(name = "GetCertificateInformationResponse")
public class GetCertificateInformationResponse {

	@XmlElement(name = "CertificateInformation", required = true)
	protected CertificateInformation certificateInformation;

	/**
	 * Ruft den Wert der certificateInformation-Eigenschaft ab.
	 * 
	 * @return possible object is {@link CertificateInformation }
	 * 
	 */
	public CertificateInformation getCertificateInformation() {
		return certificateInformation;
	}

	/**
	 * Legt den Wert der certificateInformation-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link CertificateInformation }
	 * 
	 */
	public void setCertificateInformation(CertificateInformation value) {
		this.certificateInformation = value;
	}

}

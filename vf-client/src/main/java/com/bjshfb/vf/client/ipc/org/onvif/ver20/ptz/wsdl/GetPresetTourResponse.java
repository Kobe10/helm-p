//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.02.17 um 11:33:29 AM CET 
//

package com.bjshfb.vf.client.ipc.org.onvif.ver20.ptz.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.bjshfb.vf.client.ipc.org.onvif.ver10.schema.PresetTour;

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
 *         <element name="PresetTour" type="{http://www.onvif.org/ver10/schema}PresetTour"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "presetTour" })
@XmlRootElement(name = "GetPresetTourResponse")
public class GetPresetTourResponse {

	@XmlElement(name = "PresetTour", required = true)
	protected PresetTour presetTour;

	/**
	 * Ruft den Wert der presetTour-Eigenschaft ab.
	 * 
	 * @return possible object is {@link PresetTour }
	 * 
	 */
	public PresetTour getPresetTour() {
		return presetTour;
	}

	/**
	 * Legt den Wert der presetTour-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link PresetTour }
	 * 
	 */
	public void setPresetTour(PresetTour value) {
		this.presetTour = value;
	}

}

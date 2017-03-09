
package com.silversea.aem.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="New_x0020_Method_x0020_with_x0020_52_x0020_ArgumentsResult" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "newX0020MethodX0020WithX002052X0020ArgumentsResult"
})
@XmlRootElement(name = "New_x0020_Method_x0020_with_x0020_52_x0020_ArgumentsResponse")
public class NewX0020MethodX0020WithX002052X0020ArgumentsResponse {

    @XmlElement(name = "New_x0020_Method_x0020_with_x0020_52_x0020_ArgumentsResult")
    protected int newX0020MethodX0020WithX002052X0020ArgumentsResult;

    /**
     * Obtient la valeur de la propriété newX0020MethodX0020WithX002052X0020ArgumentsResult.
     * 
     */
    public int getNewX0020MethodX0020WithX002052X0020ArgumentsResult() {
        return newX0020MethodX0020WithX002052X0020ArgumentsResult;
    }

    /**
     * Définit la valeur de la propriété newX0020MethodX0020WithX002052X0020ArgumentsResult.
     * 
     */
    public void setNewX0020MethodX0020WithX002052X0020ArgumentsResult(int value) {
        this.newX0020MethodX0020WithX002052X0020ArgumentsResult = value;
    }

}

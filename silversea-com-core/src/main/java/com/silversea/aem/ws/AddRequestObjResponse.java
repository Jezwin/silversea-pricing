
package com.silversea.aem.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="addRequestObjResult" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
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
    "addRequestObjResult"
})
@XmlRootElement(name = "addRequestObjResponse")
public class AddRequestObjResponse {

    protected int addRequestObjResult;

    /**
     * Obtient la valeur de la propriété addRequestObjResult.
     * 
     */
    public int getAddRequestObjResult() {
        return addRequestObjResult;
    }

    /**
     * Définit la valeur de la propriété addRequestObjResult.
     * 
     */
    public void setAddRequestObjResult(int value) {
        this.addRequestObjResult = value;
    }

}

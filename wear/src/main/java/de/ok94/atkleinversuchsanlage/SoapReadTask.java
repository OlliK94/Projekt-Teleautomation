package de.ok94.atkleinversuchsanlage;

import android.util.Log;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class SoapReadTask extends SoapTask {

    private static final String SOAP_REQUEST = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<SOAP-ENV:Envelope\n" +
            "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "    xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "    <SOAP-ENV:Body>\n" +
            "        <m:Read xmlns:m=\"http://opcfoundation.org/webservices/XMLDA/1.0/\">\n" +
            "            <m:Options ReturnErrorText=\"false\" ReturnDiagnosticInfo=\"false\" ReturnItemTime=\"false\" ReturnItemPath=\"false\" ReturnItemName=\"true\"/>\n" +
            "            <m:ItemList>\n" +
            "                <m:Items ItemName=\"Schneider/Fuellstand1_Ist\"/>\n" +
            "                <m:Items ItemName=\"Schneider/Fuellstand2_Ist\"/>\n" +
            "                <m:Items ItemName=\"Schneider/Fuellstand3_Ist\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LH1\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LH2\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LH3\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LL1\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LL2\"/>\n" +
            "                <m:Items ItemName=\"Schneider/LL3\"/>\n" +
            "            </m:ItemList>\n" +
            "        </m:Read>\n" +
            "    </SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>";
    private static final String XPATH_LEVEL1 = "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/Fuellstand1_Ist']/Value";
    private static final String XPATH_LEVEL2 = "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/Fuellstand2_Ist']/Value";
    private static final String XPATH_LEVEL3 = "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/Fuellstand3_Ist']/Value";
    private static final String XPATH_LL1 = "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LL1']/Value";
    private static final String XPATH_LL2 = "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LL2']/Value";
    private static final String XPATH_LL3 = "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LL3']/Value";
    private static final String XPATH_LH1 = "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LH1']/Value";
    private static final String XPATH_LH2 = "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LH2']/Value";
    private static final String XPATH_LH3 = "/Envelope/Body/ReadResponse/RItemList/Items[@ItemName='Schneider/LH3']/Value";

    private ValuesAvailable listener;

    private float level1, level2, level3;
    private boolean ll1, ll2, ll3, lh1, lh2, lh3;

    SoapReadTask(ValuesAvailable listener) {
        super(SOAP_REQUEST);
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.updateTankLevels(level1, level2, level3);
        listener.updateCapacitiveSensorStates(ll1, ll2, ll3, lh1, lh2, lh3);
    }

    @Override
    protected void readSoapResponse(String soapResponse) {
        Log.d("SOAP_READ_RESPONSE", soapResponse);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(soapResponse.getBytes(StandardCharsets.UTF_8.name())));
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            XPathExpression expr = xpath.compile(XPATH_LEVEL1);
            level1 = (float) ((double) expr.evaluate(doc, XPathConstants.NUMBER));
            expr = xpath.compile(XPATH_LEVEL2);
            level2 = (float) ((double) expr.evaluate(doc, XPathConstants.NUMBER));
            expr = xpath.compile(XPATH_LEVEL3);
            level3 = (float) ((double) expr.evaluate(doc, XPathConstants.NUMBER));

            expr = xpath.compile(XPATH_LL1);
            ll1 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LL2);
            ll2 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LL3);
            ll3 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LH1);
            lh1 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LH2);
            lh2 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
            expr = xpath.compile(XPATH_LH3);
            lh3 = Boolean.parseBoolean((String) expr.evaluate(doc, XPathConstants.STRING));
        }
        catch (Exception e) {
            Log.e("XML_PARSE", e.toString());
        }
    }

    public interface ValuesAvailable {
        void updateTankLevels(float level1, float level2, float level3);

        void updateCapacitiveSensorStates(boolean ll1, boolean ll2, boolean ll3, boolean lh1, boolean lh2, boolean lh3);
    }
}
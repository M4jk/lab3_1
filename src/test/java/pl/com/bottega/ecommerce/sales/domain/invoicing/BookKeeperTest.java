package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;
import java.util.Currency;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class BookKeeperTest {

    private InvoiceRequest invoiceRequest;
    private InvoiceFactory invoiceFactory;
    private BookKeeper bookKeeper;
    private RequestItem requestItem;
    private ClientData clientData;
    private ProductData productData;
    private TaxPolicy taxPolicy;

    @Before
    public void initialize() {
        clientData = new ClientData(Id.generate(), "client");
        invoiceRequest = new InvoiceRequest(clientData);
        invoiceFactory = new InvoiceFactory();
        bookKeeper = new BookKeeper(invoiceFactory);
    }

    @Test
    public void stateTestRequestInvoiceWithOnePosition() {
        taxPolicy = mock(TaxPolicy.class);
        productData = mock(ProductData.class);
        requestItem = new RequestItem(productData, 1, new Money(100));

        invoiceRequest.add(requestItem);

        when(productData.getType()).thenReturn(ProductType.STANDARD);
        when(taxPolicy.calculateTax(ProductType.STANDARD, new Money(100))).thenReturn(new Tax(new Money(100), "tax"));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        int result = invoice.getItems().size();

        Assert.assertThat(result, is(1));
    }

    @Test
    public void behaviourTestRequestInvoiceCallingCalculateTaxTwoTimes() {
        taxPolicy = mock(TaxPolicy.class);
        productData = mock(ProductData.class);
        requestItem = new RequestItem(productData, 1, new Money(100));

        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        when(productData.getType()).thenReturn(ProductType.STANDARD);
        when(taxPolicy.calculateTax(ProductType.STANDARD, new Money(100))).thenReturn(new Tax(new Money(100), "tax"));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(ProductType.STANDARD, new Money(100));
    }

}

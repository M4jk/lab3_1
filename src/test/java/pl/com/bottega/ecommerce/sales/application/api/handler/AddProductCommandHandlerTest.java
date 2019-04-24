package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {

    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext;
    private AddProductCommandHandler addProductCommandHandler;
    private Reservation reservation;
    private AddProductCommand addProductCommand;
    private Product product;

    @Before
    public void initialize() {
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
        suggestionService = mock(SuggestionService.class);
        clientRepository = mock(ClientRepository.class);
        systemContext = new SystemContext();

        AddProductCommandHandlerBuilder addProductCommandHandlerBuilder = new AddProductCommandHandlerBuilder();
        addProductCommandHandlerBuilder.withReservationRepository(reservationRepository)
                                       .withProductRepository(productRepository)
                                       .withSuggestionService(suggestionService)
                                       .withClientRepository(clientRepository)
                                       .withSystemContext(systemContext);
        addProductCommandHandler = addProductCommandHandlerBuilder.build();

        reservation = mock(Reservation.class);
        addProductCommand = new AddProductCommand(Id.generate(), Id.generate(), 1);

        ProductBuilder productBuilder = new ProductBuilder();
        productBuilder.withProductId(Id.generate())
                      .withPrice(new Money(100))
                      .withName("product")
                      .withProductType(ProductType.STANDARD);
        product = productBuilder.build();
    }

    @Test
    public void testHandleShouldCallProductRepositoryLoadOneTime() {
        when(productRepository.load(addProductCommand.getProductId())).thenReturn(product);
        when(reservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);

        addProductCommandHandler.handle(addProductCommand);

        verify(productRepository, times(1)).load(addProductCommand.getProductId());
    }

    @Test
    public void testHandleShouldCallReservationRepositoryLoadThreeTimes() {
        when(productRepository.load(addProductCommand.getProductId())).thenReturn(product);
        when(reservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository, times(3)).load(addProductCommand.getOrderId());
    }

    @Test
    public void testProductIsAvailable() {
        Assert.assertThat(product.isAvailable(), is(true));
    }

    @Test
    public void testReservationStatus() {
        when(productRepository.load(addProductCommand.getProductId())).thenReturn(product);
        when(reservation.getStatus()).thenReturn(Reservation.ReservationStatus.OPENED);
        when(reservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);

        addProductCommandHandler.handle(addProductCommand);

        Assert.assertThat(reservation.getStatus(), is(Reservation.ReservationStatus.OPENED));
    }

}

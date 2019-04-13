package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import java.util.Date;

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
        addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository, suggestionService, clientRepository, systemContext);
        reservation = mock(Reservation.class);
        addProductCommand = new AddProductCommand(Id.generate(), Id.generate(), 1);
        product = new Product(Id.generate(), new Money(100), "product", ProductType.STANDARD);
    }

    @Test
    public void testHandleShouldCallProductRepositoryLoadOneTime() {
        when(productRepository.load(addProductCommand.getProductId())).thenReturn(product);
        when(reservationRepository.load(addProductCommand.getOrderId())).thenReturn(reservation);

        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository, times(1)).load(addProductCommand.getOrderId());
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

}

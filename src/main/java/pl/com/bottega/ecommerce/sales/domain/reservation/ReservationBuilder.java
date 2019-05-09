package pl.com.bottega.ecommerce.sales.domain.reservation;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import java.util.Date;

public class ReservationBuilder {

    private Id aggregateId = Id.generate();
    private Reservation.ReservationStatus status = Reservation.ReservationStatus.OPENED;
    private ClientData clientData = new ClientData(Id.generate(), "id");
    private Date createDate = new Date();

    public ReservationBuilder() {
    }

    public ReservationBuilder withAggregateId(Id id) {
        this.aggregateId = id;
        return this;
    }

    public ReservationBuilder withReservationStatus(Reservation.ReservationStatus status) {
        this.status = status;
        return this;
    }

    public ReservationBuilder withClientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }

    public ReservationBuilder withDate(Date date) {
        this.createDate = date;
        return this;
    }

    public Reservation build() {
        return new Reservation(aggregateId, status, clientData, createDate);
    }

}

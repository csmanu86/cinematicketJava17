package uk.gov.dwp.uc.pairtest;


import org.junit.Before;
import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TicketServiceImplTest {
    private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;

    private TicketService ticketService;

    @Before
    public void setup() {
        ticketPaymentService = mock(TicketPaymentServiceImpl.class);
        seatReservationService = mock(SeatReservationServiceImpl.class);
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
        TicketServiceImpl.amount = 0;
        TicketServiceImpl.noOfSeats = 0;
    }

    @Test
    public void testPurchaseTickets() {
        final TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        final TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);
        final TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        ticketService.purchaseTickets(1L, ticketTypeRequest1, ticketTypeRequest2, ticketTypeRequest3);

        verify(ticketPaymentService).makePayment(1L, 50);
        verify(seatReservationService).reserveSeat(1L, 3);
    }
    /**
     * Negative test for ticket purchased without Adult
     */
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTicketsThrowsInvalidPurchaseExceptionWhenOnlyChildAndInfantTicket() {
        final TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);
        final TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        ticketService.purchaseTickets(1L, ticketTypeRequest1, ticketTypeRequest2);
    }
    /**
     * Negative test for maximum number of ticket >20
     */
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTicketsThrowsInvalidPurchaseExceptionWhenMaxTicketsExceeded() {
        final TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        final TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);
        final TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        final TicketTypeRequest ticketTypeRequest4 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 15);
        final TicketTypeRequest ticketTypeRequest5 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);

        ticketService.purchaseTickets(1L, ticketTypeRequest1, ticketTypeRequest2, ticketTypeRequest3,
                ticketTypeRequest4, ticketTypeRequest5);
    }
    /**
     * Negative test for Invalid AccountID
     */
    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTicketsThrowsInvalidPurchaseExceptionWhenInvalidAccountId() {
        final TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        final TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);
        final TicketTypeRequest ticketTypeRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        ticketService.purchaseTickets(0L, ticketTypeRequest1, ticketTypeRequest2, ticketTypeRequest3);
    }
}